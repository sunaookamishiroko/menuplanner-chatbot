package madeby.seoyun.menuplannerchatbotapi.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

/**
 * 공통으로 적용되는 메시지 처리를 위한 서비스
 *
 * @filename : DefaultMessageService.java
 * @Author : lsy
 */
@Service
public class DefaultMessageService {

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
     * 월요일 0시가 되어 메뉴 정보가 없을 때 아침에 다시 시도해달라는 메시지를
     * 카카오 챗봇 메시지 형식의 json으로 만든 후 문자열로 반환한다.
     *
     * @ param : 없음
     * @ return String : 카카오 챗봇 메시지 형식 json 문자열
     */
    public String makeMondayJson() {
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

        simpleText.put("text", "아직 메뉴 정보가 수집되지 않았습니다! " +
                "식당으로부터 메뉴가 올라올 때까지 기다려주세요.");

        return json.toJSONString();
    }
}
