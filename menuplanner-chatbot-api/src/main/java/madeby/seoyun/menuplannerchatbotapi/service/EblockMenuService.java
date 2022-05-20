package madeby.seoyun.menuplannerchatbotapi.service;

import madeby.seoyun.menuplannerchatbotapi.model.EblockMenu;
import madeby.seoyun.menuplannerchatbotapi.repository.EblockMenuRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * EblockMenuController의 E동 식당 메뉴 요청 처리를 위한 서비스
 *
 * @filename : EblockMenuService.java
 * @Author : lsy
 */
@Service
public class EblockMenuService {
    private EblockMenuRepository repository;

    @Autowired
    public EblockMenuService(EblockMenuRepository repository) {
        this.repository = repository;
    }

    public String makeMenuJson() {
        JSONObject json = new JSONObject();
        json.put("version", "2.0");

        JSONObject template = new JSONObject();
        json.put("template", template);

        JSONArray outputs = new JSONArray();
        template.put("outputs", outputs);

        JSONObject noNamed = new JSONObject();
        outputs.add(noNamed);

        JSONObject itemCard = new JSONObject();
        noNamed.put("itemCard", itemCard);

        itemCard.put("title", getTodayTipMenu());
        itemCard.put("description", "");
        itemCard.put("head", "오늘(" + getDate() + ") 메뉴");

        JSONArray itemList = new JSONArray();
        itemCard.put("itemList", itemList);

        JSONObject lunchTime = new JSONObject();
        lunchTime.put("title", "중식 시간");
        lunchTime.put("description", "11:30 - 13:50");

        JSONObject dinnerTime = new JSONObject();
        dinnerTime.put("title", "석식 시간");
        dinnerTime.put("description", "16:50 - 18:40");

        JSONObject price = new JSONObject();
        price.put("title", "가격");
        price.put("description", "5500원");

        itemList.add(lunchTime);
        itemList.add(dinnerTime);
        itemList.add(price);

        itemCard.put("itemListAlignment", "right");

        JSONArray buttons = new JSONArray();
        itemCard.put("buttons", buttons);

        JSONObject buttonsInfo = new JSONObject();
        buttons.add(buttonsInfo);
        buttonsInfo.put("label", "식단표 모두 보기");
        buttonsInfo.put("action", "webLink");
        buttonsInfo.put("webLinkUrl", "https://ibook.kpu.ac.kr/Viewer/menu01");

        itemCard.put("buttonLayout", "vertical");

        return json.toJSONString().replace("\\/", "/") ;
    }

    public String makeWorkingNowJson() {
        JSONObject json = new JSONObject();
        json.put("version", "2.0");

        JSONObject template = new JSONObject();
        json.put("template", template);

        JSONArray outputs = new JSONArray();
        template.put("outputs", outputs);

        JSONObject noNamed = new JSONObject();
        outputs.add(noNamed);

        JSONObject simpleText = new JSONObject();
        noNamed.put("simpleText", simpleText);

        simpleText.put("text", "지금은 서버가 정보 수집중이에요! 잠시후에 다시 시도해주세요!");

        return json.toJSONString();
    }

    private String getTodayTipMenu() {
        String today = getDate();
        EblockMenu eblockMenu = repository.findByDate(today);

        String lunch = "미운영";
        String dinner = "미운영";

        if (eblockMenu != null) {
            if (!eblockMenu.getLunch().equals(""))
                lunch = eblockMenu.getLunch();

            if (!eblockMenu.getDinner().equals(""))
                dinner = eblockMenu.getDinner();
        }

        String menu = "중식\n\n" + lunch + "\n\n석식\n\n" + dinner;

        return menu;
    }

    private String getDate() {
        LocalDate now = LocalDate.now();

        String today = Integer.toString(now.getMonth().getValue()) + "월 "
                + Integer.toString(now.getDayOfMonth()) + "일";

        return today;
    }


}
