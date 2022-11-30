package madeby.seoyun.menuplannerchatbotapi.component;

import madeby.seoyun.menuplannerchatbotapi.repository.EblockMenuRepository;
import madeby.seoyun.menuplannerchatbotapi.repository.FileNameRepository;
import madeby.seoyun.menuplannerchatbotapi.repository.TipMenuRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Calendar;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class ParsingMenuTest {

    @Autowired
    private TipMenuRepository tipMenuRepository;
    @Autowired
    private EblockMenuRepository eblockMenuRepository;
    @Autowired
    private FileNameRepository fileNameRepository;
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

        System.out.println("파일 이름 : " + fileNameRepository.findByName("0").getFileName());
        for (String date : arr) {
            System.out.println("날짜 : " + date);
            System.out.println("점심 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
            System.out.println(eblockMenuRepository.findByDate(date).getLunch());
            System.out.println("저녁 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
            System.out.println(eblockMenuRepository.findByDate(date).getDinner());
            System.out.println();
        }
    }

    @Test
    void getDataAndSaveToDatabaseTip() {
        String[] arr = getDays(6);
        for(String s: arr) {
            System.out.println(s);
        }
        parsingMenu.getDataAndSaveToDatabaseTip();

        System.out.println("파일 이름 : " + fileNameRepository.findByName("1").getFileName());
        for (String date : arr) {
            System.out.println("날짜 : " + date);
            System.out.println("아침 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
            System.out.println(tipMenuRepository.findByDate(date).getBreakFast());
            System.out.println("점심 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
            System.out.println(tipMenuRepository.findByDate(date).getLunch());
            System.out.println("저녁 ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
            System.out.println(tipMenuRepository.findByDate(date).getDinner());
            System.out.println();
        }

    }


}