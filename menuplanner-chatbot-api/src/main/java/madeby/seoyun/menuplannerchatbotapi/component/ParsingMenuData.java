package madeby.seoyun.menuplannerchatbotapi.component;

import madeby.seoyun.menuplannerchatbotapi.exceptions.ParsingDataFailedException;
import madeby.seoyun.menuplannerchatbotapi.model.EblockMenu;
import madeby.seoyun.menuplannerchatbotapi.model.FileName;
import madeby.seoyun.menuplannerchatbotapi.model.TipMenu;
import madeby.seoyun.menuplannerchatbotapi.repository.EblockMenuRepository;
import madeby.seoyun.menuplannerchatbotapi.repository.FileNameRepository;
import madeby.seoyun.menuplannerchatbotapi.repository.TipMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 파일 이름 파싱, 메뉴 파싱을 요청하여 DB에 저장하기 위한 컴포넌트
 * DB에 데이터가 존재하거나 최신 데이터인지 비교하는 역할도 포함
 *
 * @filename : ParsingMenuData.java
 * @Author : lsy
 */
@Component
public class ParsingMenuData {
    private TipMenuRepository tipMenuRepository;
    private EblockMenuRepository eblockMenuRepository;
    private FileNameRepository fileNameRepository;
    private final RestTemplate restTemplate;

    public static boolean isParsingMenuDataWorking = false;
    public static boolean isMondayAndBeforeParsingEblock = false;
    public static boolean isMondayAndBeforeParsingTIP = false;

    @Autowired
    public ParsingMenuData(TipMenuRepository tipMenuRepository, EblockMenuRepository eblockMenuRepository,
                           FileNameRepository fileNameRepository, RestTemplateBuilder restTemplateBuilder) {
        this.tipMenuRepository = tipMenuRepository;
        this.eblockMenuRepository = eblockMenuRepository;
        this.fileNameRepository = fileNameRepository;
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * 월요일 0시 0분 0초가 되면, isMondayAndBeforeParsing 시그널을 true로 바꾼다.
     * 이 시그널은 컨트롤러가 식당으로부터 메뉴가 올라올 때까지 기다려달라는 json을 보내게 한다.
     *
     * @ param : 없음
     * @ return : 없음
     */
    @Scheduled(cron = "0 0 0 * * 1")
    public void setMondaySignalTrue() {
        LogData.printLog("monday = true", "setMondaySignalTrue");
        isMondayAndBeforeParsingEblock = true;
        isMondayAndBeforeParsingTIP = true;
    }

    /**
     * 월요일 7시 30분 0초가 되면 메뉴가 올라왔는지 체크하기 시작한다.
     * 체크는 DB의 파일 이름과 파싱해서 얻은 파일을 10분마다 비교한다.
     * 만약 파일 이름이 같지 않다면 새 메뉴가 올라온 것으로
     * isMondayAndBeforeParsing 시그널을 false로 바꾸고,
     * getDataAndSaveToDatabase() 메서드를 실행한다.
     *
     * @ param : 없음
     * @ return : 없음
     * @ exception : 쓰레드가 오작동하면 exception 발생
     */
    @Scheduled(cron = "0 30 7 * * 1")
    @Transactional(rollbackFor = Exception.class)
    public void checkUploadMenu() throws Exception{
        LogData.printLog("메뉴 파일 업로드 체크를 시작합니다...", "checkUploadMenu");
        String eBlockFileName = fileNameRepository.findByName("0").getFileName();
        String tipFileName = fileNameRepository.findByName("1").getFileName();

        boolean isEblockUploaded = false;
        boolean isTipUploaded = false;

        String newEblockFileName;
        String newTipFileName;

        while(true) {
            if (!isEblockUploaded) {
                newEblockFileName = getFileInfo("0").get("fileName");

                if (!newEblockFileName.equals(eBlockFileName)) {
                    LogData.printLog("메뉴 업로드가 감지되었습니다...", "checkUploadMenu");
                    getDataAndSaveToDatabaseEblock();
                    isEblockUploaded = true;
                }
            }

            if (!isTipUploaded) {
                newTipFileName = getFileInfo("1").get("fileName");

                if (!newTipFileName.equals(tipFileName)) {
                    LogData.printLog("메뉴 업로드가 감지되었습니다...", "checkUploadMenu");
                    getDataAndSaveToDatabaseTip();
                    isTipUploaded = true;
                }
            }

            if (isEblockUploaded && isTipUploaded) break;
            else Thread.sleep(600000);
        }
        LogData.printLog("메뉴 파일 업로드 체크 완료", "checkUploadMenu");
    }

    /**
     * E동, TIP 식당 메뉴 파싱을 둘 다 시도한다.
     * 서버를 실행할 때 조건에 맞으면 실행된다. -> CheckDatabaseApplicationRunner.java 참조
     * api에 관한 자세한 내용은 aws-rambda-python 폴더에 존재하는 소스 코드 참조
     *
     * @ param : 없음
     * @ return : 없음
     */
    public void getDataAndSaveToDatabase() {
        getDataAndSaveToDatabaseEblock();
        getDataAndSaveToDatabaseTip();
    }

    /**
     * E동 식당의 메뉴 파일 이름, 메뉴 파싱을 해서 새로운 데이터를 DB에 저장한다.
     * 파싱중일 때는 isParsingMenuDataWorking 시그널을 이용해 정보 수집중 상태로 바꾼다.
     * 파싱이 끝나면 isParsingMenuDataWorking, isMondayAndBeforeParsingEblock 시그널을
     * false로 바꾼다.
     * api에 관한 자세한 내용은 aws-rambda-python 폴더에 존재하는 소스 코드 참조
     *
     * @ param : 없음
     * @ return : 없음
     */
    public void getDataAndSaveToDatabaseEblock() {
        LogData.printLog("E동 파싱 작업 시작...", "getDataAndSaveToDatabaseEblock");
        isParsingMenuDataWorking = true;

        Map<String, String> eBlockFileInfo = getFileInfo("0");

        String eBlockFileName = eBlockFileInfo.get("fileName");
        String eBlockBookCode = eBlockFileInfo.get("bookCode");

        Map<String, Map<String, String>> eBlockMenu =
                getEblockMenu(eBlockFileName, eBlockBookCode);

        Object[] eBlockMenuKeys = eBlockMenu.keySet().toArray();
        saveToEblockDatabase(eBlockMenu, eBlockMenuKeys);
        saveToEblcokFileNameDatabase(eBlockFileName);

        isParsingMenuDataWorking = false;
        isMondayAndBeforeParsingEblock = false;
        LogData.printLog("파싱 작업 완료", "getDataAndSaveToDatabaseEblock");
    }

    /**
     * TIP 식당의 메뉴 파일 이름, 메뉴 파싱을 해서 새로운 데이터를 DB에 저장한다.
     * 파싱중일 때는 isParsingMenuDataWorking 시그널을 이용해 정보 수집중 상태로 바꾼다.
     * 파싱이 끝나면 isParsingMenuDataWorking, isMondayAndBeforeParsingTIP 시그널을
     * false로 바꾼다.
     * api에 관한 자세한 내용은 aws-rambda-python 폴더에 존재하는 소스 코드 참조
     *
     * @ param : 없음
     * @ return : 없음
     */
    public void getDataAndSaveToDatabaseTip() {
        LogData.printLog("TIP 파싱 작업 시작...", "getDataAndSaveToDatabaseTip");
        isParsingMenuDataWorking = true;

        Map<String, String> tipFileInfo = getFileInfo("1");

        String tipFileName = tipFileInfo.get("fileName");
        String tipBookCode = tipFileInfo.get("bookCode");

        Map<String, Map<String, String>> tipMenu =
                getTipMenu(tipFileName, tipBookCode);

        Object[] tipMenuKeys = tipMenu.keySet().toArray();
        saveToTipDatabase(tipMenu, tipMenuKeys);
        saveToTipFileNameDatabase(tipFileName);

        isParsingMenuDataWorking = false;
        isMondayAndBeforeParsingTIP = false;
        LogData.printLog("파싱 작업 완료", "getDataAndSaveToDatabaseTip");
    }

    /**
     * E동 메뉴 파일 이름과 tip 지하 식당 메뉴 파일 이름이 모두 DB에 존재하는지 판단한다.
     *
     * @ param : 없음
     * @ return boolean : 두 개의 파일 이름이 DB에 모두 존재하면 true, 아니라면 false
     */
    @Transactional(readOnly = true)
    public boolean isDatabaseDataExist() {
        LogData.printLog("DB에 파일 이름 존재하는지 확인...", "isDatabaseDataExist");

        if (fileNameRepository.findByName("0") != null
                && fileNameRepository.findByName("1") != null) {
            LogData.printLog("파일 이름이 존재합니다", "isDatabaseDataExist");
            return true;
        } else {
            LogData.printLog("파일 이름이 존재하지 않습니다", "isDatabaseDataExist");
            return false;
        }
    }

    /**
     * E동 메뉴 파일 이름과 tip 지하 식당 메뉴 파일 이름을 파싱하여
     * 두 개의 데이터가 파일 이름 DB에 저장되어있는 데이터와 다른지 판단한다.
     * 같다면 최신 데이터이고 아니라면 최신 데이터가 아니다.
     *
     * @ param : 없음
     * @ return boolean : 두 개의 파일 이름이 DB에 있는 것과 모두 같으면 true, 아니라면 false
     */
    @Transactional(readOnly = true)
    public boolean isRecentData() {
        LogData.printLog("최신 데이터인지 확인...", "isRecentData");

        String eBlockFileName = getFileInfo("0").get("fileName");
        String tipFileName = getFileInfo("1").get("fileName");

        if (eBlockFileName.equals(fileNameRepository.findByName("0").getFileName())
                && tipFileName.equals(fileNameRepository.findByName("1").getFileName())) {
            LogData.printLog("최신 데이터입니다", "isRecentData");
            return true;
        } else {
            LogData.printLog("최신 데이터가 아닙니다", "isRecentData");
            return false;
        }
    }

    /**
     * aws lambda api를 이용하여 파일 이름을 파싱한다.
     *
     * @ param String num : "0" -> E동 메뉴 파일 이름 파싱 | "1" -> TIP 메뉴 파일 이름 파싱
     * @ return Map<String, String> temp : json 형식의 데이터를 map으로 받아서 반환
     * @ exception ParsingDataFailedException : 파싱에 실패할 경우 발생
     */
    private Map<String, String> getFileInfo(String num) {
        if(num.equals("0"))
            LogData.printLog("E동 메뉴 파일 이름 파싱중...", "getFileName");
        else if(num.equals("1"))
            LogData.printLog("TIP 메뉴 파일 이름 파싱중...", "getFileName");

        String url = ""
                + num;

        Map<String, String> temp;

        try {
            temp = this.restTemplate.getForObject(url, HashMap.class);
        } catch(Exception e) {
            throw new ParsingDataFailedException(e.getMessage() + " : " + "getFileName");
        }

        LogData.printLog("파싱 완료", "getFileName");
        return temp;
    }

    /**
     * aws lambda api를 이용하여 E동 식당 메뉴를 파싱한다.
     *
     * @ param String fileName : E동 식당 메뉴의 엑셀 파일 이름
     * @ param String eBlockBookCode : E동 식당 메뉴 사이트에서 파일 다운받기 위한 bookcode
     * @ return Map<String, Map<String, String>> temp : json 형식의 데이터를 map으로 받아서 반환
     * @ exception ParsingDataFailedException : 파싱에 실패할 경우 발생
     */
    private Map<String, Map<String, String>> getEblockMenu(String fileName, String eBlockBookCode) {
        LogData.printLog("E동 메뉴 파싱중...", "getEblockMenu");

        String url = ""
                + fileName + "&bookCode=" + eBlockBookCode;
        Map<String, Map<String, String>> temp;

        try {
            temp = this.restTemplate.getForObject(url, HashMap.class);
        } catch(Exception e) {
            throw new ParsingDataFailedException(e.getMessage() + " : " + "getEblockMenu");
        }

        LogData.printLog("파싱 완료", "getEblockMenu");
        return temp;
    }

    /**
     * aws lambda api를 이용하여 TIP 식당 메뉴를 파싱한다.
     *
     * @ param String fileName : TIP 지하 식당 메뉴의 엑셀 파일 이름
     * @ param String tipBookCode : TIP 지하 식당 메뉴 사이트에서 파일 다운받기 위한 bookcode
     * @ return Map<String, Map<String, String>> temp : json 형식의 데이터를 map으로 받아서 반환
     * @ exception ParsingDataFailedException : 파싱에 실패할 경우 발생
     */
    private Map<String, Map<String, String>> getTipMenu(String fileName, String tipBookCode) {
        LogData.printLog("TIP 메뉴 파싱중...", "getTipMenu");

        String url = ""
                + fileName + "&bookCode=" + tipBookCode;;
        Map<String, Map<String, String>> temp;

        try {
            temp = this.restTemplate.getForObject(url, HashMap.class);
        } catch(Exception e) {
            throw new ParsingDataFailedException(e.getMessage() + " : " + "getTipMenu");
        }

        LogData.printLog("파싱 완료", "getTipMenu");
        return temp;
    }

    /**
     * 파싱한 E동 식당 메뉴를 DB에 저장한다.
     *
     * @ param Map<String, Map<String, String>> eBlockMenu : 파싱한 메뉴 json 데이터
     * @ param Object[] eBlockMenuKeys : json map 데이터의 key가 담긴 데이터
     *                                   메뉴의 날짜(5월 15일...5월 16일..)들이 담겨있음.
     * @ return : 없음
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveToEblockDatabase(
            Map<String, Map<String, String>> eBlockMenu, Object[] eBlockMenuKeys) {
        LogData.printLog("E동 메뉴 DB에 저장중...", "saveToEblockDatabase");

        eblockMenuRepository.deleteAll();

        for(int i = 0; i < eBlockMenuKeys.length; i++) {
            String tempLunchList = eBlockMenu.get(eBlockMenuKeys[i]).get("lunch");
            String tempDinnerList = eBlockMenu.get(eBlockMenuKeys[i]).get("dinner");

            eblockMenuRepository.save(
                    new EblockMenu(eBlockMenuKeys[i].toString(), tempLunchList, tempDinnerList)
            );
        }

        LogData.printLog("저장 완료", "saveToEblockDatabase");
    }

    /**
     * 파싱한 TIP 식당 메뉴를 DB에 저장한다.
     *
     * @ param Map<String, Map<String, String>> eBlockMenu : 파싱한 메뉴 json 데이터
     * @ param Object[] eBlockMenuKeys : json map 데이터의 key가 담긴 데이터
     *                                   메뉴의 날짜(5월 15일...5월 16일..)들이 담겨있음.
     * @ return : 없음
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveToTipDatabase(
            Map<String, Map<String, String>> tipMenu, Object[] tipMenuKeys) {
        LogData.printLog("TIP 메뉴 DB에 저장중...", "saveToTipDatabase");

        tipMenuRepository.deleteAll();

        for(int i = 0; i < tipMenuKeys.length; i++) {
            String tempbreakFastList = tipMenu.get(tipMenuKeys[i]).get("breakFast");
            String tempLunchList = tipMenu.get(tipMenuKeys[i]).get("lunch");
            String tempDinnerList = tipMenu.get(tipMenuKeys[i]).get("dinner");

            tipMenuRepository.save(
                    new TipMenu(tipMenuKeys[i].toString(), tempbreakFastList, tempLunchList, tempDinnerList)
            );
        }

        LogData.printLog("저장 완료", "saveToTipDatabase");
    }

    /**
     * 파싱한 E동 식당 파일 이름을 DB에 저장한다.
     *
     * @ param String eBlockFileName : 파싱한 E동 식당 메뉴 엑셀 파일 이름
     * @ return : 없음
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveToEblcokFileNameDatabase(String eBlockFileName) {
        LogData.printLog("E동 파일 이름 DB에 저장중...", "saveToEblcokFileNameDatabase");

        fileNameRepository.save(new FileName("0", eBlockFileName));

        LogData.printLog("저장 완료", "saveToFileNameDatabase");
    }

    /**
     * 파싱한 TIP 식당 파일 이름을 DB에 저장한다.
     *
     * @ param String tipBlockFileName : 파싱한 TIP 식당 메뉴 엑셀 파일 이름
     * @ return : 없음
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveToTipFileNameDatabase(String tipBlockFileName) {
        LogData.printLog("TIP 파일 이름 DB에 저장중...", "saveToTipFileNameDatabase");

        fileNameRepository.save(new FileName("1", tipBlockFileName));

        LogData.printLog("저장 완료", "saveToFileNameDatabase");
    }

}
