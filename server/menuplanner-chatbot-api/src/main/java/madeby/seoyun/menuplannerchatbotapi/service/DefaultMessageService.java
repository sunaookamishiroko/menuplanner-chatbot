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
     * 메시지로 보낼 문자열을 받아 카카오 챗봇 메시지 형식 json 문자열을 만든다.
     *
     * @ param String msg : 메시지로 보낼 문자열
     * @ return String : 카카오 챗봇 메시지 형식 json 문자열
     */
    public String makeJson(String msg) {
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

        simpleText.put("text", msg);

        return json.toJSONString();
    }
}
