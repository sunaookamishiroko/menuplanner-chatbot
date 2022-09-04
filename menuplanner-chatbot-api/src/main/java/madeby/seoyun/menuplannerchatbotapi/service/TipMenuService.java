package madeby.seoyun.menuplannerchatbotapi.service;

import madeby.seoyun.menuplannerchatbotapi.component.GetDateTime;
import madeby.seoyun.menuplannerchatbotapi.exceptions.DatabaseConnectFailedException;
import madeby.seoyun.menuplannerchatbotapi.model.TipMenu;
import madeby.seoyun.menuplannerchatbotapi.repository.TipMenuRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * TipMenuController의 TIP 지하 식당 메뉴 요청 처리를 위한 서비스
 *
 * @filename : TipMenuService.java
 * @Author : lsy
 */
@Service
public class TipMenuService {
    private TipMenuRepository repository;

    @Autowired
    public TipMenuService(TipMenuRepository repository) {
        this.repository = repository;
    }

    /**
     * TIP 지하 식당 메뉴 / 시간 / 가격 / 식단표 주소를
     * 카카오 챗봇 메시지 형식의 json으로 만든 후 문자열로 반환한다.
     *
     * @ param : 없음
     * @ return String : 카카오 챗봇 메시지 형식 json 문자열
     */
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
        itemCard.put("head", "오늘(" + GetDateTime.getDate() + " " + GetDateTime.getDayOfWeek() + ") 메뉴");

        JSONArray itemList = new JSONArray();
        itemCard.put("itemList", itemList);

        JSONObject breakFastTime = new JSONObject();
        breakFastTime.put("title", "조식 시간");
        //breakFastTime.put("description", "08:30 - 09:30");
        breakFastTime.put("description", "09:30 - 11:00");

        JSONObject lunchTime = new JSONObject();
        lunchTime.put("title", "중식 시간");
        lunchTime.put("description", "11:30 - 14:30");
        //lunchTime.put("description", "11:30 - 14:00");

        JSONObject dinnerTime = new JSONObject();
        dinnerTime.put("title", "석식 시간(주중, 주말)");
        //주중
        dinnerTime.put("description", "17:50 - 18:50");
        //주말
        dinnerTime.put("description", "17:00 - 18:00");
        //dinnerTime.put("description", "17:30 - 18:30");

        JSONObject breakFastPrice = new JSONObject();
        breakFastPrice.put("title", "셀프라면 가격");
        breakFastPrice.put("description", "3000원");

        JSONObject lunchDinnerPrice = new JSONObject();
        lunchDinnerPrice.put("title", "중식/석식 가격");
        lunchDinnerPrice.put("description", "6000원");

        itemList.add(breakFastTime);
        itemList.add(lunchTime);
        itemList.add(dinnerTime);
        itemList.add(breakFastPrice);
        itemList.add(lunchDinnerPrice);

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

    /**
     * 서버의 날짜의 TIP 지하 식당 메뉴를 DB에서 가져와서 메뉴 정보 문자열 생성 후 반환한다.
     *
     * @ param : 없음
     * @ return String menu : 메뉴 정보를 합친 문자열
     */
    @Transactional(readOnly = true)
    public String getTodayTipMenu() {
        String today = GetDateTime.getDate();
        TipMenu tipMenu;

        try {
            tipMenu = repository.findByDate(today);
        } catch (Exception e) {
            throw new DatabaseConnectFailedException("서버의 응답이 없습니다. 개발자에게 문의해주세요! code:1");
        }

        String breakFast = "미운영";
        String lunch = "미운영";
        String dinner = "미운영";

        if (tipMenu != null) {
            breakFast = tipMenu.getBreakFast();
            lunch = tipMenu.getLunch();
            dinner = tipMenu.getDinner();
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("조식 ▼\n\n")
                .append(breakFast)
                .append("\n\n중식 ▼\n\n")
                .append(lunch)
                .append("\n\n석식 ▼\n\n")
                .append(dinner);

        return stringBuilder.toString();
    }
}
