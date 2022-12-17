package madeby.seoyun.menuplannerchatbotapi.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.Clock;
import java.time.Instant;
import java.util.Calendar;

@SpringBootTest
@AutoConfigureMockMvc
public class TipMenuControllerTest {

    @Autowired
    private MockMvc mvc;

    @SpyBean
    private Clock clock;

    // 한 주의 날짜 구하기
    public static String[] getDays(int days){
        String[] arr = new String[days];
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        for(int i = 2; i <= days + 1; i++) {
            if (i == 8) {
                c.add(Calendar.DATE, 7);
                c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            } else
                c.set(Calendar.DAY_OF_WEEK, i);

            arr[i - 2] = formatter.format(c.getTime()) + "T00:00:00.000Z";
        }
        return arr;
    }

    @Test
    public void menuTest() throws Exception{
        String[] arr = getDays(7);

        for(int i = 0; i < 7; i++) {
            Mockito.when(clock.instant()).thenReturn(Instant.parse(arr[i]));

            mvc.perform(MockMvcRequestBuilders
                    .post("/tip")).andDo(MockMvcResultHandlers.print());
        }
    }
}
