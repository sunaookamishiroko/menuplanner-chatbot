package madeby.seoyun.menuplannerchatbotapi.component;

import madeby.seoyun.menuplannerchatbotapi.repository.RestaurantFileNameRepository;
import madeby.seoyun.menuplannerchatbotapi.repository.RestaurantMenuRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Calendar;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan({"madeby.seoyun.menuplannerchatbotapi.component", "madeby.seoyun.menuplannerchatbotapi.config"})
@DataJpaTest
class ParsingMenuTest {

    @Autowired
    private RestaurantMenuRepository restaurantMenuRepository;
    @Autowired
    private RestaurantFileNameRepository restaurantFileNameRepository;
    @SpyBean
    private ParsingMenu parsingMenu;

    // 한 주의 날짜 구하기
    public static String[] getDays(int days){
        String[] arr = new String[days];
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("MM월 d일");
        Calendar c = Calendar.getInstance();

        for(int i = 2; i <= days + 1; i++) {
            if (i == 8) {
                c.add(Calendar.DATE, 7);
                c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            } else
                c.set(Calendar.DAY_OF_WEEK, i);

            arr[i - 2] = formatter.format(c.getTime());
        }
        return arr;
    }

    @Test
    void testGetDataAndSaveToDatabaseEblock() {
        String[] arr = getDays(5);
        parsingMenu.getDataAndSaveToDatabaseEblock();

        System.out.println("파일 이름 : " + restaurantFileNameRepository.findById(0).orElseThrow().getFileName());
        for (String date : arr) {
            System.out.println("날짜 : " + date);
            System.out.println("점심 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
            System.out.println(restaurantMenuRepository.findByRestaurantPropertyIdAndDate(0, date).getLunch());
            System.out.println("저녁 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
            System.out.println(restaurantMenuRepository.findByRestaurantPropertyIdAndDate(0, date).getDinner());
            System.out.println();
        }
    }

    @Test
    void getDataAndSaveToDatabaseTip() {
        String[] arr = getDays(6);
        parsingMenu.getDataAndSaveToDatabaseTip();

        System.out.println("파일 이름 : " + restaurantFileNameRepository.findById(1).orElseThrow().getFileName());
        for (String date : arr) {
            System.out.println("날짜 : " + date);
            System.out.println("아침 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
            System.out.println(restaurantMenuRepository.findByRestaurantPropertyIdAndDate(1, date).getBreakFast());
            System.out.println("점심 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
            System.out.println(restaurantMenuRepository.findByRestaurantPropertyIdAndDate(1, date).getLunch());
            System.out.println("저녁 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
            System.out.println(restaurantMenuRepository.findByRestaurantPropertyIdAndDate(1, date).getDinner());
            System.out.println();
        }

    }

}