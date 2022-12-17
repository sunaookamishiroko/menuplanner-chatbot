package madeby.seoyun.menuplannerchatbotapi.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * 현재 날짜, 시간을 얻기 위한 LocalDateTime 객체와 메서드가 있는 곳
 *
 * @filename : GetDateTime.java
 * @Author : lsy
 */
@Component
public class GetDateTime {
    private LocalDateTime localDateTime;
    private final Clock clock;

    @Autowired
    public GetDateTime(Clock clock) {
        this.clock = clock;
    }

    /**
     * 오늘의 날짜 (월, 일)을 "x월 x일"의 형태로 반환한다.
     *
     * @ param : 없음
     * @ return String : "x월 x일" 형태의 문자열
     */
    public String getDate() {
        localDateTime = LocalDateTime.now(clock);
        return localDateTime.getMonth().getValue() + "월 " + localDateTime.getDayOfMonth() + "일";
    }

    /**
     * 오늘의 요일을 "월", "화" 등의 형태로 반환한다.
     *
     * @ param : 없음
     * @ return String : "월", "화" 등의 형태의 문자열
     */
    public String getDayOfWeek() {
        localDateTime = LocalDateTime.now(clock);
        DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
        return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN);
    }
}
