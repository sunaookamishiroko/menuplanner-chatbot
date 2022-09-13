package madeby.seoyun.menuplannerchatbotapi.exceptions;

import madeby.seoyun.menuplannerchatbotapi.component.LogData;
import madeby.seoyun.menuplannerchatbotapi.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public final DefaultMessageService defaultMessageService;

    @Autowired
    public CustomizedResponseEntityExceptionHandler(DefaultMessageService defaultMessageService) {
        this.defaultMessageService = defaultMessageService;
    }

    /**
     * default exception handler
     *
     * @ param Exception ex : 발생한 Exception 객체
     * @ return ResponseEntity<String> : 클라이언트에게 카카오 챗봇 메시지 형식으로 에러 코드와 함께 전송
     */
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<String> handleAllExceptions(Exception ex) {
        LogData.printLog("알 수 없는 에러 발생... " + ex.getMessage(), "handleAllExceptions");
        return new ResponseEntity<>(
                defaultMessageService.makeJson("알 수 없는 에러가 발생했습니다. 개발자에게 문의해주세요! code:-1"),
                HttpStatus.OK);
    }

    /**
     * DatabaseConnectFailedException exception handler
     *
     * @ param Exception ex : 발생한 Exception 객체
     * @ return ResponseEntity<String> : 클라이언트에게 카카오 챗봇 메시지 형식으로 에러 코드와 함께 전송
     */
    @ExceptionHandler(DatabaseConnectFailedException.class)
    public final ResponseEntity<String> handleDatabaseConnectFailedException(Exception ex) {
        LogData.printLog("DB 연결 실패", "handleDatabaseConnectFailedException");
        return new ResponseEntity<>(defaultMessageService.makeJson(ex.getMessage()), HttpStatus.OK);
    }

}
