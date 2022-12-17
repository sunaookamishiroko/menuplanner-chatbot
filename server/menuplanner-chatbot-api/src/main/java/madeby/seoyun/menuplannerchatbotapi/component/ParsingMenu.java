package madeby.seoyun.menuplannerchatbotapi.component;

import madeby.seoyun.menuplannerchatbotapi.exceptions.ParsingDataFailedException;
import madeby.seoyun.menuplannerchatbotapi.model.*;
import madeby.seoyun.menuplannerchatbotapi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 파일 이름 파싱, 메뉴 파싱을 요청하여 DB에 저장하기 위한 컴포넌트
 * DB에 데이터가 존재하거나 최신 데이터인지 비교하는 역할도 포함
 *
 * @filename : ParsingMenu.java
 * @Author : lsy
 */
@Component
public class ParsingMenu {
    private final RestaurantMenuRepository restaurantMenuRepository;
    private final RestaurantPropertyRepository restaurantPropertyRepository;
    private final RestaurantFileNameRepository restaurantFileNameRepository;

    private final WebClient webClient;

    private final SettingProperty settingProperty;

    private boolean isParsingNowEblock = false;
    private boolean isParsingNowTIP = false;
    private boolean isBeforeParsingEblock = false;
    private boolean isBeforeParsingTIP = false;

    @Autowired
    public ParsingMenu(RestaurantFileNameRepository restaurantFileNameRepository,
                       RestaurantMenuRepository restaurantMenuRepository,
                       RestaurantPropertyRepository restaurantPropertyRepository,
                       SettingProperty settingProperty,
                       WebClient webClient) {
        this.restaurantMenuRepository = restaurantMenuRepository;
        this.restaurantPropertyRepository = restaurantPropertyRepository;
        this.restaurantFileNameRepository = restaurantFileNameRepository;
        this.settingProperty = settingProperty;
        this.webClient = webClient;
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

        if (restaurantFileNameRepository.findById(0).isPresent()
                && restaurantFileNameRepository.findById(1).isPresent()) {
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

        String eBlockFileName = getFileInfo(0).get("fileName");
        String tipFileName = getFileInfo(1).get("fileName");


        if (eBlockFileName.equals(restaurantFileNameRepository.getById(0).getFileName())
                && tipFileName.equals(restaurantFileNameRepository.getById(1).getFileName())) {
            LogData.printLog("최신 데이터입니다", "isRecentData");
            return true;
        } else {
            LogData.printLog("최신 데이터가 아닙니다", "isRecentData");
            return false;
        }
    }

    /**
     * 월요일 0시 0분 0초가 되면, isBeforeParsingEblock, isBeforeParsingTIP 시그널을 true로 바꾼다.
     * 이 시그널은 컨트롤러가 식당으로부터 메뉴가 올라올 때까지 기다려달라는 json을 보내게 한다.
     *
     * @ param : 없음
     * @ return : 없음
     */
    @Scheduled(cron = "0 0 0 * * 1")
    public void setMondaySignalTrue() {
        LogData.printLog("monday = true", "setMondaySignalTrue");
        isBeforeParsingEblock = true;
        isBeforeParsingTIP = true;
    }

    /**
     * 월요일 7시 0분 0초가 되면 메뉴가 올라왔는지 체크하기 시작한다.
     * 체크는 DB의 파일 이름과 파싱해서 얻은 파일을 10분마다 비교한다.
     * 만약 파일 이름이 같지 않다면 새 메뉴가 올라온 것으로
     * getDataAndSaveToDatabase() 메서드를 실행한다.
     *
     * @ param : 없음
     * @ return : 없음
     * @ exception : 쓰레드가 오작동하면 exception 발생
     */
    @Scheduled(cron = "0 0 7 * * 1")
    @Transactional(rollbackFor = Exception.class)
    public void checkUploadMenu() throws Exception{
        LogData.printLog("메뉴 파일 업로드 체크를 시작합니다...", "checkUploadMenu");

        // null이어도 진행. 상관 없음
        String eBlockFileName = restaurantFileNameRepository.findById(0).get().getFileName();
        String tipFileName = restaurantFileNameRepository.findById(1).get().getFileName();

        boolean isEblockUploaded = false;
        boolean isTipUploaded = false;

        String newEblockFileName;
        String newTipFileName;

        isParsingNowEblock = true;
        isParsingNowTIP = true;

        isBeforeParsingEblock = false;
        isBeforeParsingTIP = false;

        while(true) {
            if (!isEblockUploaded) {
                newEblockFileName = getFileInfo(0).get("fileName");

                if (!newEblockFileName.equals(eBlockFileName)) {
                    LogData.printLog("메뉴 업로드가 감지되었습니다...", "checkUploadMenu");
                    getDataAndSaveToDatabaseEblock();
                    isEblockUploaded = true;
                }
            }

            if (!isTipUploaded) {
                newTipFileName = getFileInfo(1).get("fileName");

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
    @Transactional(rollbackFor = Exception.class)
    public void getDataAndSaveToDatabase() {
        isParsingNowEblock = true;
        isParsingNowTIP = true;
        getDataAndSaveToDatabaseEblock();
        getDataAndSaveToDatabaseTip();
    }

    /**
     * E동 식당의 메뉴 파일 이름, 메뉴 파싱을 해서 새로운 데이터를 DB에 저장한다.
     * 파싱이 끝나면 isParsingNowEblock 시그널을 false로 바꾼다.
     * api에 관한 자세한 내용은 aws-rambda-python 폴더에 존재하는 소스 코드 참조
     *
     * @ param : 없음
     * @ return : 없음
     */
    public void getDataAndSaveToDatabaseEblock() {
        LogData.printLog("E동 파싱 작업 시작...", "getDataAndSaveToDatabaseEblock");

        Map<String, String> eBlockFileInfo = getFileInfo(0);

        String eBlockFileName = eBlockFileInfo.get("fileName");
        String eBlockBookCode = eBlockFileInfo.get("bookCode");

        saveEblockFileName(eBlockFileName);

        Map<String, HashMap<String, String>> eBlockMenu =
                getEblockMenu(eBlockFileName, eBlockBookCode);
        String[] eBlockMenuKeys = new String[eBlockMenu.keySet().size()];
        eBlockMenuKeys = eBlockMenu.keySet().toArray(eBlockMenuKeys);

        saveEblockMenu(eBlockMenu, eBlockMenuKeys);

        isParsingNowEblock = false;
        LogData.printLog("파싱 작업 완료", "getDataAndSaveToDatabaseEblock");
    }

    /**
     * TIP 식당의 메뉴 파일 이름, 메뉴 파싱을 해서 새로운 데이터를 DB에 저장한다.
     * 파싱이 끝나면 isParsingNowTIP 시그널을 false로 바꾼다.
     * api에 관한 자세한 내용은 aws-rambda-python 폴더에 존재하는 소스 코드 참조
     *
     * @ param : 없음
     * @ return : 없음
     */
    public void getDataAndSaveToDatabaseTip() {
        LogData.printLog("TIP 파싱 작업 시작...", "getDataAndSaveToDatabaseTip");

        Map<String, String> tipFileInfo = getFileInfo(1);

        String tipFileName = tipFileInfo.get("fileName");
        String tipBookCode = tipFileInfo.get("bookCode");

        saveTipFileName(tipFileName);

        Map<String, HashMap<String, String>> tipMenu =
                getTipMenu(tipFileName, tipBookCode);
        String[] tipMenuKeys = new String[tipMenu.keySet().size()];
        tipMenuKeys = tipMenu.keySet().toArray(tipMenuKeys);

        saveTipMenu(tipMenu, tipMenuKeys);

        isParsingNowTIP = false;
        LogData.printLog("파싱 작업 완료", "getDataAndSaveToDatabaseTip");
    }

    /**
     * aws lambda api를 이용하여 파일 이름을 파싱한다.
     *
     * @ param String num : "0" -> E동 메뉴 파일 이름 파싱 | "1" -> TIP 메뉴 파일 이름 파싱
     * @ return Map<String, String> temp : json 형식의 데이터를 map으로 받아서 반환
     * @ exception ParsingDataFailedException : 파싱에 실패할 경우 발생
     */
    private Map<String, String> getFileInfo(int num) {
        if(num == 0)
            LogData.printLog("E동 메뉴 파일 이름 파싱중...", "getFileName");
        else if(num == 1)
            LogData.printLog("TIP 메뉴 파일 이름 파싱중...", "getFileName");

        Map<String, String> res;
        try {
            res = webClient.get()
                    .uri(uriBuilder ->
                            uriBuilder.path("/fileinfo")
                                    .queryParam("classify", num)
                                    .build()
                    ).retrieve()
                    .bodyToMono(new ParameterizedTypeReference<HashMap<String, String>>() {
                    })
                    .block();
        } catch(Exception e) {
            throw new ParsingDataFailedException(e.getMessage() + " : " + "getFileInfo");
        }

        LogData.printLog("파싱 완료", "getFileInfo");
        return res;
    }

    /**
     * aws lambda api를 이용하여 E동 식당 메뉴를 파싱한다.
     *
     * @ param String fileName : E동 식당 메뉴의 엑셀 파일 이름
     * @ param String eBlockBookCode : E동 식당 메뉴 사이트에서 파일 다운받기 위한 bookcode
     * @ return Map<String, Map<String, String>> temp : json 형식의 데이터를 map으로 받아서 반환
     * @ exception ParsingDataFailedException : 파싱에 실패할 경우 발생
     */
    private Map<String, HashMap<String, String>> getEblockMenu(String fileName, String eBlockBookCode) {
        LogData.printLog("E동 메뉴 파싱중...", "getEblockMenu");

        String url;
        if (settingProperty.checkIsVacation(0))
            url = "/veblock";
        else
            url = "/eblock";

        Map<String, HashMap<String, String>> res;

        try {
            res = webClient.get()
                    .uri(uriBuilder ->
                            uriBuilder.path(url)
                                    .queryParam("filename", fileName)
                                    .queryParam("bookcode", eBlockBookCode)
                                    .build()
                    ).retrieve()
                    .bodyToMono(new ParameterizedTypeReference<HashMap<String, HashMap<String, String>>>() {
                    })
                    .block();
        } catch(Exception e) {
            throw new ParsingDataFailedException(e.getMessage() + " : " + "getEblockMenu");
        }

        LogData.printLog("파싱 완료", "getEblockMenu");
        return res;
    }

    /**
     * aws lambda api를 이용하여 TIP 식당 메뉴를 파싱한다.
     *
     * @ param String fileName : TIP 지하 식당 메뉴의 엑셀 파일 이름
     * @ param String tipBookCode : TIP 지하 식당 메뉴 사이트에서 파일 다운받기 위한 bookcode
     * @ return Map<String, Map<String, String>> temp : json 형식의 데이터를 map으로 받아서 반환
     * @ exception ParsingDataFailedException : 파싱에 실패할 경우 발생
     */
    private Map<String, HashMap<String, String>> getTipMenu(String fileName, String tipBookCode) {
        LogData.printLog("TIP 메뉴 파싱중...", "getTipMenu");

        HashMap<String, HashMap<String, String>> res;
        try {
            res = webClient.get()
                    .uri(uriBuilder ->
                            uriBuilder.path("/tip")
                                    .queryParam("filename", fileName)
                                    .queryParam("bookcode", tipBookCode)
                                    .build()
                    ).retrieve()
                    .bodyToMono(new ParameterizedTypeReference<HashMap<String, HashMap<String, String>>>() {
                    })
                    .block();
        } catch(Exception e) {
            throw new ParsingDataFailedException(e.getMessage() + " : " + "getEblockMenu");
        }

        LogData.printLog("파싱 완료", "getTipMenu");
        return res;
    }

    /**
     * 파싱한 E동 식당 메뉴를 DB에 저장한다.
     *
     * @ param Map<String, Map<String, String>> eBlockMenu : 파싱한 메뉴 json 데이터
     * @ param String[] eBlockMenuKeys : json map 데이터의 key가 담긴 데이터
     *                                   메뉴의 날짜(5월 15일...5월 16일..)들이 담겨있음.
     * @ return : 없음
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveEblockMenu(
            Map<String, HashMap<String, String>> eBlockMenu, String[] eBlockMenuKeys) {
        LogData.printLog("E동 메뉴 DB에 저장중...", "saveEblockMenu");

        restaurantMenuRepository.deleteAllByRestaurantPropertyId(0);
        RestaurantProperty property = restaurantPropertyRepository.getById(0);

        for (String eBlockMenuKey : eBlockMenuKeys) {
            String tempLunchList = eBlockMenu.get(eBlockMenuKey).get("lunch");
            String tempDinnerList = eBlockMenu.get(eBlockMenuKey).get("dinner");

            restaurantMenuRepository.save(
                    new RestaurantMenu(
                            eBlockMenuKey, null, tempLunchList, tempDinnerList, property)
            );
        }

        LogData.printLog("저장 완료", "saveEblockMenu");
    }

    /**
     * 파싱한 TIP 식당 메뉴를 DB에 저장한다.
     *
     * @ param Map<String, Map<String, String>> eBlockMenu : 파싱한 메뉴 json 데이터
     * @ param String[] tipMenuKeys : json map 데이터의 key가 담긴 데이터
     *                                   메뉴의 날짜(5월 15일...5월 16일..)들이 담겨있음.
     * @ return : 없음
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveTipMenu(
            Map<String, HashMap<String, String>> tipMenu, String[] tipMenuKeys) {
        LogData.printLog("TIP 메뉴 DB에 저장중...", "saveTipMenu");

        restaurantMenuRepository.deleteAllByRestaurantPropertyId(1);
        RestaurantProperty property = restaurantPropertyRepository.getById(1);

        for (String tipMenuKey : tipMenuKeys) {
            String tempBreakFastList = tipMenu.get(tipMenuKey).get("breakFast");
            String tempLunchList = tipMenu.get(tipMenuKey).get("lunch");
            String tempDinnerList = tipMenu.get(tipMenuKey).get("dinner");

            restaurantMenuRepository.save(
                    new RestaurantMenu(tipMenuKey, tempBreakFastList, tempLunchList, tempDinnerList, property)
            );
        }

        LogData.printLog("저장 완료", "saveTipMenu");
    }

    /**
     * 파싱한 E동 식당 파일 이름을 DB에 저장한다.
     *
     * @ param String eBlockFileName : 파싱한 E동 식당 메뉴 엑셀 파일 이름
     * @ return : 없음
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveEblockFileName(String eBlockFileName) {
        LogData.printLog("E동 파일 이름 DB에 저장중...", "saveEblockFileName");

        Optional<RestaurantFileName> optional = restaurantFileNameRepository.findById(0);

        if(optional.isPresent()) {
            optional.get().setFileName(eBlockFileName);
        } else {
            restaurantFileNameRepository.save(new RestaurantFileName(0, eBlockFileName));
        }

        LogData.printLog("저장 완료", "saveEblockFileName");
    }

    /**
     * 파싱한 TIP 식당 파일 이름을 DB에 저장한다.
     *
     * @ param String tipFileName : 파싱한 TIP 식당 메뉴 엑셀 파일 이름
     * @ return : 없음
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveTipFileName(String tipFileName) {
        LogData.printLog("TIP 파일 이름 DB에 저장중...", "saveTipFileName");

        Optional<RestaurantFileName> optional = restaurantFileNameRepository.findById(1);

        if(optional.isPresent()) {
            optional.get().setFileName(tipFileName);
        } else {
            restaurantFileNameRepository.save(new RestaurantFileName(1, tipFileName));
        }

        LogData.printLog("저장 완료", "saveTipFileName");
    }

    /**
     * E동 메뉴가 파싱 전인지 아닌지 알려준다.
     *
     * @ param : 없음
     * @ return : boolean : 파싱 완료 전이면 true, 완료 후면 falae
     */
    public boolean checkBeforeParsingEblock() {
        return isBeforeParsingEblock;
    }

    /**
     * TIP 메뉴가 파싱 전인지 아닌지 알려준다.
     *
     * @ param : 없음
     * @ return : boolean : 파싱 완료 전이면 true, 완료 후면 falae
     */
    public boolean checkBeforeParsingTIP() {
        return isBeforeParsingTIP;
    }

    /**
     * 월요일 오전 7시부터 시작되는 E동 메뉴의 파싱이 진행중인지 완료됐는지 알려준다.
     *
     * @ param : 없음
     * @ return : boolean : 파싱 완료 전이면 true, 완료 후면 falae
     */
    public boolean checkParsingNowEblock() {
        return isParsingNowEblock;
    }

    /**
     * 월요일 오전 7시부터 시작되는 TIP 메뉴의 파싱이 진행중인지 완료됐는지 알려준다.
     *
     * @ param : 없음
     * @ return : boolean : 파싱 완료 전이면 true, 완료 후면 falae
     */
    public boolean checkParsingNowTIP() {
        return isParsingNowTIP;
    }
}