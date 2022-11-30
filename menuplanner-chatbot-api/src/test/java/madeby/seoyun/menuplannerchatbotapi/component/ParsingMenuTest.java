package madeby.seoyun.menuplannerchatbotapi.component;

import madeby.seoyun.menuplannerchatbotapi.repository.EblockMenuRepository;
import madeby.seoyun.menuplannerchatbotapi.repository.FileNameRepository;
import madeby.seoyun.menuplannerchatbotapi.repository.TipMenuRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

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

    @Test
    void testGetDataAndSaveToDatabaseEblock() {
        parsingMenu.getDataAndSaveToDatabaseEblock();
        String[] arr = {"10월 17일", "10월 18일", "10월 19일", "10월 20일", "10월 21일"};
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
        parsingMenu.getDataAndSaveToDatabaseTip();
        String[] arr = {"9월 12일", "9월 13일", "9월 14일", "9월 15일", "9월 16일", "9월 17일", "9월 18일"};

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