package madeby.seoyun.menuplannerchatbotapi.service;

import madeby.seoyun.menuplannerchatbotapi.exceptions.DatabaseConnectFailedException;
import madeby.seoyun.menuplannerchatbotapi.model.EblockMenu;
import madeby.seoyun.menuplannerchatbotapi.repository.EblockMenuRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * EblockMenuController의 E동 식당 메뉴 요청 처리를 위한 서비스
 *
 * @filename : EblockMenuService.java
 * @Author : lsy
 */
@Service
public class EblockMenuService {
    private EblockMenuRepository repository;
    private LocalDate now;

    @Autowired
    public EblockMenuService(EblockMenuRepository repository) {
        this.repository = repository;
        now  = LocalDate.now();
    }

    /**
     * E동 식당 메뉴 / 시간 / 가격 / 식단표 주소를
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

        itemCard.put("title", getTodayEblockMenu());
        itemCard.put("description", "");
        itemCard.put("head", "오늘(" + getDate() + " " + getDayOfWeek() + ") 메뉴");

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

    /**
     * 서버가 메뉴 파싱중일 때 잠시 후에 다시 시도해달라는 메시지를
     * 카카오 챗봇 메시지 형식의 json으로 만든 후 문자열로 반환한다.
     *
     * @ param : 없음
     * @ return String : 카카오 챗봇 메시지 형식 json 문자열
     */
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

    /**
     * 서버의 날짜의 E동 식당 메뉴를 DB에서 가져와서 메뉴 정보 문자열 생성 후 반환한다.
     *
     * @ param : 없음
     * @ return String : 메뉴 정보를 합친 문자열
     */
    @Transactional(readOnly = true)
    public String getTodayEblockMenu() {
        String today = getDate();
        EblockMenu eblockMenu;

        try {
            eblockMenu = repository.findByDate(today);
        } catch (Exception e) {
            throw new DatabaseConnectFailedException("서버의 응답이 없습니다. 개발자에게 문의해주세요! code:1");
        }

        String lunch = "미운영";
        String dinner = "미운영";

        if (eblockMenu != null) {
            if (!eblockMenu.getLunch().equals(""))
                lunch = eblockMenu.getLunch();

            if (!eblockMenu.getDinner().equals(""))
                dinner = eblockMenu.getDinner();
        }

        String menu = "중식 ▼\n\n" + lunch + "\n\n석식 ▼\n\n" + dinner;

        return menu;
    }

    /**
     * 오늘의 날짜 (월, 일)을 "x월 x일"의 형태로 반환한다.
     *
     * @ param : 없음
     * @ return String : "x월 x일" 형태의 문자열
     */
    private String getDate() {
        String today = now.getMonth().getValue() + "월 " + now.getDayOfMonth() + "일";
        return today;
    }

    /**
     * 오늘의 요일을 "월", "화" 등의 형태로 반환한다.
     *
     * @ param : 없음
     * @ return String : "월", "화" 등의 형태의 문자열
     */
    private String getDayOfWeek() {
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN);
    }


}
