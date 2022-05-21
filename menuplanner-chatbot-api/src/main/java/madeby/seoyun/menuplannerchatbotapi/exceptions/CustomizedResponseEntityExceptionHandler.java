package madeby.seoyun.menuplannerchatbotapi.exceptions;

import madeby.seoyun.menuplannerchatbotapi.component.LogData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * controller와 service의 과정에서 발생하는 에러를 처리하는 handler
 *
 * @filename : CustomizedResponseEntityExceptionHandler.java
 * @Author : lsy
 */
@RestController
@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * default exception handler
     *
     * @ param Exception ex : 발생한 Exception 객체
     * @ return ResponseEntity<String> : 클라이언트에게 카카오 챗봇 메시지 형식으로 에러 코드와 함께 전송
     */
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<String> handleAllExceptions(Exception ex) {
        LogData.printLog("알 수 없는 에러 발생... " + ex.getMessage(), "handleAllExceptions");
        String msg = "알 수 없는 에러가 발생했습니다. 개발자에게 문의해주세요! code:-1";
        return new ResponseEntity(makeExceptionJson(msg), HttpStatus.OK);
    }

    /**
     * DatabaseConnectFailedException exception handler
     *
     * @ param Exception ex : 발생한 Exception 객체
     * @ return ResponseEntity<String> : 클라이언트에게 카카오 챗봇 메시지 형식으로 에러 코드와 함께 전송
     */
    @ExceptionHandler(DatabaseConnectFailedException.class)
    public final ResponseEntity<String> handleDatabaseConnectFailedException(Exception ex) {
        LogData.printLog("DB 연결 실패","handleDatabaseConnectFailedException");
        return new ResponseEntity(makeExceptionJson(ex.getMessage()), HttpStatus.OK);
    }

    /**
     * 메시지로 보낼 문자열을 받아 카카오 챗봇 메시지 형식 json 문자열을 만든다.
     *
     * @ param String msg : 메시지로 보낼 문자열
     * @ return String : 카카오 챗봇 메시지 형식 json 문자열
     */
    private String makeExceptionJson(String msg) {
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
