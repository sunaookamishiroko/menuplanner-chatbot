package madeby.seoyun.menuplannerchatbotapi.component;

import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * 로그 출력을 위한 메서드가 있는 컴포넌트
 *
 * @filename : LogData.java
 * @Author : lsy
 */
@Component
public class LogData {

    public LogData() {}

    /**
     * 메시지와 메서드 이름을 받아서 날짜 시간과 함께 출력한다.
     *
     * @ param String msg : 사용자 지정 메시지
     * @ param String method : 메서드 이름
     * @ return : 없음
     */
    public static void printLog(String msg, String method) {
        System.out.println(GetDateTime.localDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                + " : " + msg + " : " + method);
    }
}
