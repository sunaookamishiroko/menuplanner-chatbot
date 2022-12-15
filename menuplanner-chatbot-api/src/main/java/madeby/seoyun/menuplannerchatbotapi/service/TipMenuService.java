package madeby.seoyun.menuplannerchatbotapi.service;

import madeby.seoyun.menuplannerchatbotapi.component.GetDateTime;
import madeby.seoyun.menuplannerchatbotapi.component.ParsingMenu;
import madeby.seoyun.menuplannerchatbotapi.exceptions.DatabaseConnectFailedException;
import madeby.seoyun.menuplannerchatbotapi.model.RestaurantMenu;
import madeby.seoyun.menuplannerchatbotapi.repository.RestaurantMenuRepository;
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
    private final RestaurantMenuRepository repository;
    private final ParsingMenu parsingMenu;

    @Autowired
    public TipMenuService(RestaurantMenuRepository repository, ParsingMenu parsingMenu) {
        this.repository = repository;
        this.parsingMenu = parsingMenu;
    }

    /**
     * TIP 지하 식당 메뉴 / 시간 / 가격 / 식단표 주소를
     * 카카오 챗봇 메시지 형식의 json으로 만든 후 문자열로 반환한다.
     *
     * @ param : 없음
     * @ return String : 카카오 챗봇 메시지 형식 json 문자열
     */
    public String makeMenuJson() {
        RestaurantMenu menu = getTodayTipMenu();

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

        itemCard.put("title", getMenuStr(menu));
        itemCard.put("description", "");
        itemCard.put("head", "오늘(" + GetDateTime.getDate() + " " + GetDateTime.getDayOfWeek() + ") 메뉴");

        JSONArray itemList = new JSONArray();
        itemCard.put("itemList", itemList);

        JSONObject breakFastTime = new JSONObject();
        breakFastTime.put("title", "조식 시간");
        breakFastTime.put("description", menu.getRestaurantProperty().getBreakFastTime());

        JSONObject lunchTime = new JSONObject();
        lunchTime.put("title", "중식 시간");
        lunchTime.put("description", menu.getRestaurantProperty().getLunchTime());

        JSONObject dinnerTime = new JSONObject();
        dinnerTime.put("title", "석식 시간");
        dinnerTime.put("description", menu.getRestaurantProperty().getDinnerTime());

        JSONObject breakFastPrice = new JSONObject();
        breakFastPrice.put("title", "셀프라면 가격");
        breakFastPrice.put("description", menu.getRestaurantProperty().getBreakFastPrice());

        JSONObject lunchDinnerPrice = new JSONObject();
        lunchDinnerPrice.put("title", "중식/석식 가격");
        lunchDinnerPrice.put("description", menu.getRestaurantProperty().getCommonPrice());

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
        buttonsInfo.put("webLinkUrl", menu.getRestaurantProperty().getMenuUrl());

        itemCard.put("buttonLayout", "vertical");

        return json.toJSONString().replace("\\/", "/") ;
    }

    /**
     * 오늘 날짜의 TIP 지하 식당 메뉴와 정보를 가져와서 객체로 반환한다.
     *
     * @ param : 없음
     * @ return RestaurantMenu : 메뉴 정보 객체
     */
    @Transactional(readOnly = true)
    public RestaurantMenu getTodayTipMenu() {
        String today = GetDateTime.getDate();
        RestaurantMenu menu;

        try {
            menu = repository.findByRestaurantPropertyIdAndDate(1, today);
        } catch (Exception e) {
            throw new DatabaseConnectFailedException("서버의 응답이 없습니다. 개발자에게 문의해주세요! code:1");
        }

        return menu;
    }

    /**
     * RestaurantMenu의 메뉴 내용을 문자열로 만들어 반환한다.
     *
     * @ param : RestaurantMenu : DB에서 가져온 메뉴 정보 객체
     * @ return String : 메뉴 문자열
     */
    public String getMenuStr(RestaurantMenu menu) {
        return "조식 ▼\n\n" + menu.getBreakFast() +
                "\n\n중식 ▼\n\n" + menu.getLunch() +
                "\n\n석식 ▼\n\n" + menu.getDinner();
    }
}
