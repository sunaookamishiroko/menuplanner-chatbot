package madeby.seoyun.menuplannerchatbotapi.service;

import madeby.seoyun.menuplannerchatbotapi.model.EblockMenu;
import madeby.seoyun.menuplannerchatbotapi.model.FileName;
import madeby.seoyun.menuplannerchatbotapi.model.TipMenu;
import madeby.seoyun.menuplannerchatbotapi.repository.EblockMenuRepository;
import madeby.seoyun.menuplannerchatbotapi.repository.FileNameRepository;
import madeby.seoyun.menuplannerchatbotapi.repository.TipMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class GetWeekMenuService {
    private TipMenuRepository tipMenuRepository;
    private EblockMenuRepository eblockMenuRepository;
    private FileNameRepository fileNameRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public GetWeekMenuService(TipMenuRepository tipMenuRepository, EblockMenuRepository eblockMenuRepository,
                              FileNameRepository fileNameRepository, RestTemplateBuilder restTemplateBuilder) {
        this.tipMenuRepository = tipMenuRepository;
        this.eblockMenuRepository = eblockMenuRepository;
        this.fileNameRepository = fileNameRepository;
        this.restTemplate = restTemplateBuilder.build();
    }

    public void getDataAndSaveToDatabase() {

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
        System.out.println("DB 저장 완료");
    }

    public boolean isDatabaseDataExist() {
        if (fileNameRepository.findByName("0") != null
                && fileNameRepository.findByName("1") != null) {
            return true;
        } else {
            return false;
        }
    }

    private Map<String, String> getFileName(String num) {
        if(num.equals("0")) {
            System.out.println("E동 메뉴 파일 이름 파싱중...");
        } else if(num.equals("1")) {
            System.out.println("TIP 메뉴 파일 이름 파싱중...");
        }

        String url = ""
                + num;
        return this.restTemplate.getForObject(url, HashMap.class);
    }

    private Map<String, Map<String, String>> getEblockMenu(String fileName) {
        System.out.println("E동 메뉴 불러오는중...");
        String url = ""
                + fileName;
        return this.restTemplate.getForObject(url, HashMap.class);
    }

    private Map<String, Map<String, String>>  getTipMenu(String fileName) {
        System.out.println("TIP 메뉴 불러오는중...");
        String url = ""
                + fileName;
        return this.restTemplate.getForObject(url, HashMap.class);
    }

    private void saveToEblockDatabase(
            Map<String, Map<String, String>> eBlockMenu, Object[] eBlockMenuKeys) {
        System.out.println("E동 메뉴 DB에 저장중...");
        eblockMenuRepository.deleteAll();

        for(int i = 0; i < eBlockMenuKeys.length; i++) {
            String tempLunchList = eBlockMenu.get(eBlockMenuKeys[i]).get("lunch");
            String tempDinnerList = eBlockMenu.get(eBlockMenuKeys[i]).get("dinner");

            eblockMenuRepository.save(
                    new EblockMenu(eBlockMenuKeys[i].toString(), tempLunchList, tempDinnerList)
            );
        }

        System.out.println("E동 메뉴 DB에 저장완료");
    }

    private void saveToTipDatabase(
            Map<String, Map<String, String>> tipMenu, Object[] tipMenuKeys) {
        System.out.println("TIP 메뉴 DB에 저장중...");
        tipMenuRepository.deleteAll();

        for(int i = 0; i < tipMenuKeys.length; i++) {
            String tempbreakFastList = tipMenu.get(tipMenuKeys[i]).get("breakFast");
            String tempLunchList = tipMenu.get(tipMenuKeys[i]).get("lunch");
            String tempDinnerList = tipMenu.get(tipMenuKeys[i]).get("dinner");

            tipMenuRepository.save(
                    new TipMenu(tipMenuKeys[i].toString(), tempbreakFastList, tempLunchList, tempDinnerList)
            );
        }

        System.out.println("TIP 메뉴 DB에 저장완료");
    }

    private void saveToFileNameDatabase(String eBlockFileName, String tipBlockFileName) {
        fileNameRepository.save(new FileName("0", eBlockFileName));
        fileNameRepository.save(new FileName("1", tipBlockFileName));
        System.out.println("파일 이름 DB에 저장완료");
    }

}
