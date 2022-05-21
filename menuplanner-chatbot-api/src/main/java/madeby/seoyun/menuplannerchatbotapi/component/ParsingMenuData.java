package madeby.seoyun.menuplannerchatbotapi.component;

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

    public static boolean isGetWeekMenuServiceWorking = false;

    @Autowired
    public ParsingMenuData(TipMenuRepository tipMenuRepository, EblockMenuRepository eblockMenuRepository,
                           FileNameRepository fileNameRepository, RestTemplateBuilder restTemplateBuilder) {
        this.tipMenuRepository = tipMenuRepository;
        this.eblockMenuRepository = eblockMenuRepository;
        this.fileNameRepository = fileNameRepository;
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * 매주 월요일 0시 0분마다 올려놓은 aws lambda api를 이용해서
     * 메뉴 파일 이름, 메뉴 파싱을 해서 새로운 데이터를 DB에 저장한다.
     * 파싱중일 때는 isGetWeekMenuServiceWorking 시그널을 이용해 클라이언트에게 정보 수집중이라 알린다.
     * 서버를 실행할 때도 조건에 맞으면 실행된다. -> CheckDatabaseApplicationRunner.java 참조
     * api에 관한 자세한 내용은 aws-rambda-python 폴더에 존재하는 소스 코드 참조
     *
     * @ param : 없음
     * @ return : 없음
     */
    @Scheduled(cron = "0 0 0 * * 1")
    public void getDataAndSaveToDatabase() {
        LogData.printLog("파싱 작업 시작...", "getDataAndSaveToDatabase");
        isGetWeekMenuServiceWorking = true;

        String eBlockFileName = getFileName("0").get("fileName");
        String tipBlockFileName = getFileName("1").get("fileName");

        Map<String, Map<String, String>> eBlockMenu =
                getEblockMenu(eBlockFileName);

        Map<String, Map<String, String>> tipMenu =
                getTipMenu(tipBlockFileName);

        Object[] eBlockMenuKeys = eBlockMenu.keySet().toArray();
        Object[] tipMenuKeys = tipMenu.keySet().toArray();

        saveToEblockDatabase(eBlockMenu, eBlockMenuKeys);
        saveToTipDatabase(tipMenu, tipMenuKeys);
        saveToFileNameDatabase(eBlockFileName, tipBlockFileName);

        isGetWeekMenuServiceWorking = false;
        LogData.printLog("파싱 작업 완료", "getDataAndSaveToDatabase");
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
    public boolean isRecentData() throws RuntimeException {
        LogData.printLog("최신 데이터인지 확인...", "isRecentData");

        String eBlockFileName = getFileName("0").get("fileName");
        String tipBlockFileName = getFileName("1").get("fileName");

        if (eBlockFileName.equals(fileNameRepository.findByName("0").getFileName())
                && tipBlockFileName.equals(fileNameRepository.findByName("1").getFileName())) {
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
     * @ return Map<String, String> : json 형식의 데이터를 map으로 받아서 반환
     */
    private Map<String, String> getFileName(String num) throws RuntimeException{
        if(num.equals("0"))
            LogData.printLog("E동 메뉴 파일 이름 파싱중...", "getFileName");
        else if(num.equals("1"))
            LogData.printLog("TIP 메뉴 파일 이름 파싱중...", "getFileName");

        String url = ""
                + num;

        LogData.printLog("파싱 완료", "getFileName");
        return this.restTemplate.getForObject(url, HashMap.class);
    }

    /**
     * aws lambda api를 이용하여 E동 식당 메뉴를 파싱한다.
     *
     * @ param String fileName : E동 식당 메뉴의 엑셀 파일 이름
     * @ return Map<String, Map<String, String>> : json 형식의 데이터를 map으로 받아서 반환
     */
    private Map<String, Map<String, String>> getEblockMenu(String fileName) throws RuntimeException{
        LogData.printLog("E동 메뉴 파싱중...", "getEblockMenu");
        String url = ""
                + fileName;
        LogData.printLog("파싱 완료", "getEblockMenu");
        return this.restTemplate.getForObject(url, HashMap.class);
    }

    /**
     * aws lambda api를 이용하여 TIP 식당 메뉴를 파싱한다.
     *
     * @ param String fileName : TIP 지하 식당 메뉴의 엑셀 파일 이름
     * @ return Map<String, Map<String, String>> : json 형식의 데이터를 map으로 받아서 반환
     */
    private Map<String, Map<String, String>> getTipMenu(String fileName) throws RuntimeException{
        LogData.printLog("TIP 메뉴 파싱중...", "getTipMenu");
        String url = ""
                + fileName;
        LogData.printLog("파싱 완료", "getTipMenu");
        return this.restTemplate.getForObject(url, HashMap.class);
    }

    /**
     * 파싱한 E동 식당 메뉴를 DB에 저장한다.
     *
     * @ param Map<String, Map<String, String>> eBlockMenu : 파싱한 메뉴 json 데이터
     * @ param Object[] eBlockMenuKeys : json map 데이터의 key가 담긴 데이터
     * 메뉴의 날짜(5월 15일...5월 16일..)들이 담겨있음.
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
     * 메뉴의 날짜(5월 15일...5월 16일..)들이 담겨있음.
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
     * 파싱한 파일 이름들을 DB에 저장한다.
     *
     * @ param String eBlockFileName : 파싱한 E동 식당 메뉴 엑셀 파일 이름
     * @ param String tipBlockFileName : 파싱한 TIP 식당 메뉴 엑셀 파일 이름
     * @ return : 없음
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveToFileNameDatabase(String eBlockFileName, String tipBlockFileName) {
        LogData.printLog("메뉴 이름 DB에 저장중...", "saveToFileNameDatabase");
        fileNameRepository.save(new FileName("0", eBlockFileName));
        fileNameRepository.save(new FileName("1", tipBlockFileName));
        LogData.printLog("저장 완료", "saveToFileNameDatabase");
    }

}
