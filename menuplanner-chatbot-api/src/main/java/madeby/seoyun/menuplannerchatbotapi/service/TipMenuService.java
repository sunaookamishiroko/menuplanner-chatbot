package madeby.seoyun.menuplannerchatbotapi.service;

import madeby.seoyun.menuplannerchatbotapi.model.TipMenu;
import madeby.seoyun.menuplannerchatbotapi.repository.TipMenuRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Locale;

@Service
public class TipMenuService {
    private TipMenuRepository repository;

    @Autowired
    public TipMenuService(TipMenuRepository repository) {
        this.repository = repository;
    }

    public String makeJson() {
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

        JSONObject breakFastTime = new JSONObject();
        breakFastTime.put("title", "조식 시간");
        breakFastTime.put("description", "8:30 - 9:30");

        JSONObject lunchTime = new JSONObject();
        lunchTime.put("title", "중식 시간");
        lunchTime.put("description", "11:30 - 14:30");

        JSONObject dinnerTime = new JSONObject();
        dinnerTime.put("title", "석식 시간");
        dinnerTime.put("description", "16:50 - 18:50");

        itemList.add(breakFastTime);
        itemList.add(lunchTime);
        itemList.add(dinnerTime);

        itemCard.put("itemListAlignment", "right");

        JSONArray buttons = new JSONArray();
        itemCard.put("buttons", buttons);

        JSONObject buttonsInfo = new JSONObject();
        buttons.add(buttonsInfo);
        buttonsInfo.put("label", "식단표 모두 보기");
        buttonsInfo.put("action", "webLink");
        buttonsInfo.put("webLinkUrl", "https://ibook.kpu.ac.kr/Viewer/menu02");

        itemCard.put("buttonLayout", "vertical");

        return json.toJSONString().replace("\\/", "/") ;
    }

    private String getTodayTipMenu() {
        String today = getDate();
        System.out.println(today);
        TipMenu tipMenu = repository.findByDate(today);

        String breakFast = "미운영";
        String lunch = "미운영";
        String dinner = "미운영";

        if (!tipMenu.getBreakFast().equals("")) {
            breakFast = tipMenu.getBreakFast();
        }

        if (!tipMenu.getLunch().equals("")) {
            lunch = tipMenu.getLunch();
        }

        if (!tipMenu.getDinner().equals("")) {
            dinner = tipMenu.getDinner();
        }

        String menu = "조식\n\n" + breakFast + "\n\n중식\n\n" + lunch + "\n\n석식\n\n" + dinner;

        return menu;
    }

    private String getDate() {
        LocalDate now = LocalDate.now();

        String today = Integer.toString(now.getMonth().getValue()) + "월 "
                + Integer.toString(now.getDayOfMonth()) + "일";

        return today;
    }


}
