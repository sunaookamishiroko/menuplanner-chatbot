package madeby.seoyun.menuplannerchatbotapi.component;

import madeby.seoyun.menuplannerchatbotapi.model.EblockMenu;
import madeby.seoyun.menuplannerchatbotapi.model.FileName;
import madeby.seoyun.menuplannerchatbotapi.model.TipMenu;
import madeby.seoyun.menuplannerchatbotapi.repository.EblockMenuRepository;
import madeby.seoyun.menuplannerchatbotapi.repository.FileNameRepository;
import madeby.seoyun.menuplannerchatbotapi.repository.TipMenuRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class ParsingMenuDataTest {

    @Autowired
    private TipMenuRepository tipMenuRepository;
    @Autowired
    private EblockMenuRepository eblockMenuRepository;
    @Autowired
    private FileNameRepository fileNameRepository;
    private RestTemplate restTemplate = new RestTemplateBuilder().build();

    @Value("${parsing-endpoint}")
    private String endPoint;

    @Test
    void testGetDataAndSaveToDatabaseEblock() {

        // before
        String url = endPoint + "/fileinfo?classify=0";
        Map<String, String> eBlockFileInfo = restTemplate.getForObject(url, Map.class);

        String fileName = eBlockFileInfo.get("fileName");
        String bookCode = eBlockFileInfo.get("bookCode");

        url = endPoint + "/eblock?filename=" + fileName + "&bookcode=" + bookCode;

        Map<String, Map<String, String>> eBlockMenu = restTemplate.getForObject(url, Map.class);
        String[] eBlockMenuKeys = new String[eBlockMenu.keySet().size()];
        eBlockMenuKeys = eBlockMenu.keySet().toArray(eBlockMenuKeys);

        eblockMenuRepository.deleteAll();
        for (String eBlockMenuKey : eBlockMenuKeys) {
            String lunchList = eBlockMenu.get(eBlockMenuKey).get("lunch");
            String dinnerList = eBlockMenu.get(eBlockMenuKey).get("dinner");

            eblockMenuRepository.save(
                    new EblockMenu(eBlockMenuKey, lunchList, dinnerList)
            );
        }
        fileNameRepository.save(new FileName("0", fileName));

        // test
        HashMap<String, HashMap<String, String>> menu = new HashMap<>();
        menu.put("7월 11일", new HashMap<>());
        menu.get("7월 11일").put("lunch", "- 잡곡밥\n- 미역냉국\n- 닭갈비\n- 알감자조림\n- 콩나물무침\n- 포기김치\n- 바나나샐러드");
        menu.get("7월 11일").put("dinner", "미운영");
        menu.put("7월 12일", new HashMap<>());
        menu.get("7월 12일").put("lunch", "- 잡곡밥\n- 곤드레육개장\n- 데리야끼너비아니\n- 편마늘브로컬리볶음\n- 도라지생채\n- 깍두기\n- 연두부&양념장");
        menu.get("7월 12일").put("dinner", "미운영");
        menu.put("7월 13일", new HashMap<>());
        menu.get("7월 13일").put("lunch", "- 잡곡밥\n- 짬뽕순두부\n- 숯불향간장불고기\n- 파프리카가지볶음\n- 짠지무무침\n- 포기김치\n- 구운김");
        menu.get("7월 13일").put("dinner", "미운영");
        menu.put("7월 14일", new HashMap<>());
        menu.get("7월 14일").put("lunch", "- 잡곡밥\n- 등촌샤브칼국수\n- 고구마맛탕\n- 물만두&초간장\n- 간장고추지\n- 열무김치\n- 요구르트");
        menu.get("7월 14일").put("dinner", "미운영");
        menu.put("7월 15일", new HashMap<>());
        menu.get("7월 15일").put("lunch", "- 잡곡밥\n- 눈꽃치즈돈가스\n- 카레덮밥\n- 장국\n- 단무지\n- 포기김치\n- 망고에이드");
        menu.get("7월 15일").put("dinner", "미운영");

        for (String key : menu.keySet()) {
            assertEquals(menu.get(key).get("lunch"), eblockMenuRepository.findByDate(key).getLunch());
            assertEquals(menu.get(key).get("dinner"), eblockMenuRepository.findByDate(key).getDinner());
        }

        assertEquals(fileNameRepository.findByName("0").getFileName(), "교직원식당 식단표(7.11).xlsx");
    }

    @Test
    void getDataAndSaveToDatabaseTip() {

        // before
        String url = endPoint + "/fileinfo?classify=1";
        Map<String, String> tipFileInfo = restTemplate.getForObject(url, Map.class);

        String fileName = tipFileInfo.get("fileName");
        String bookCode = tipFileInfo.get("bookCode");

        url = endPoint + "/tip?filename=" + fileName + "&bookcode=" + bookCode;

        Map<String, Map<String, String>> tipMenu = restTemplate.getForObject(url, Map.class);
        String[] tipMenuKeys = new String[tipMenu.keySet().size()];
        tipMenuKeys = tipMenu.keySet().toArray(tipMenuKeys);

        tipMenuRepository.deleteAll();
        for (String tipMenuKey : tipMenuKeys) {
            String breakFirstList = tipMenu.get(tipMenuKey).get("breakFast");
            String lunchList = tipMenu.get(tipMenuKey).get("lunch");
            String dinnerList = tipMenu.get(tipMenuKey).get("dinner");

            tipMenuRepository.save(
                    new TipMenu(tipMenuKey, breakFirstList, lunchList, dinnerList)
            );
        }
        fileNameRepository.save(new FileName("1", fileName));

        // test
        HashMap<String, HashMap<String, String>> menu = new HashMap<>();
        menu.put("7월 11일", new HashMap<>());
        menu.get("7월 11일").put("breakFirst", "- 셀프라면 코너\n- 셀프라면/밥/김치");
        menu.get("7월 11일").put("lunch", "- 쫄면&삶은달걀\n- 꼬치어묵국\n- 알감자버터구이\n- 배추김치\n- 아이스커피");
        menu.get("7월 11일").put("dinner", "- 뿌리채소영양밥\n- 된장국\n- 미니메밀전병\n- 오이무침\n- 열무김치");
        menu.put("7월 12일", new HashMap<>());
        menu.get("7월 12일").put("breakFirst", "- 셀프라면 코너\n- 셀프라면/밥/김치");
        menu.get("7월 12일").put("lunch", "- 크리미어니언치킨\n- 미역국\n- 두부조림\n- 참나물무침\n- 배추김치");
        menu.get("7월 12일").put("dinner", "- 일식카레덮밥\n- 우동장국\n- 햄카츠\n- 단무지삼색냉채\n- 배추김치");
        menu.put("7월 13일", new HashMap<>());
        menu.get("7월 13일").put("breakFirst", "- 셀프라면 코너\n- 셀프라면/밥/김치");
        menu.get("7월 13일").put("lunch", "- 소불고기야채비빔밥\n- 콩나물국\n- 스크램블에그\n- 열무김치\n- 요구르트");
        menu.get("7월 13일").put("dinner", "- 누들떡볶이\n- 팽이미소국\n- 순대찜&소금\n- 고구마&만두튀김\n- 배추김치");
        menu.put("7월 14일", new HashMap<>());
        menu.get("7월 14일").put("breakFirst", "- 셀프라면 코너\n- 셀프라면/밥/김치");
        menu.get("7월 14일").put("lunch", "- 냉모밀\n- 타코야끼\n- 주먹밥\n- 꼬들단무지\n- 열무김치");
        menu.get("7월 14일").put("dinner", "- 돈육매콤장조림\n- 감자양파국\n- 크랜베리실치볶음\n- 부추적채겉절이\n- 배추김치");
        menu.put("7월 15일", new HashMap<>());
        menu.get("7월 15일").put("breakFirst", "- 셀프라면 코너\n- 셀프라면/밥/김치");
        menu.get("7월 15일").put("lunch", "- 숯불향간장불고기\n- 짬뽕순두부\n- 깐풍가지\n- 미나리무생채\n- 배추김치");
        menu.get("7월 15일").put("dinner", "- 달걀당면부침\n- 장터국밥\n- 편마늘후랑크볶음\n- 치커리사과무침\n- 깍두기");
        menu.put("7월 16일", new HashMap<>());
        menu.get("7월 16일").put("breakFirst", "- 미운영");
        menu.get("7월 16일").put("lunch", "- ★초복★\n- 한방반계탕\n- 오이,당근,풋고추&쌈장\n- 양파절임\n- 깍두기\n- 수박화채");
        menu.get("7월 16일").put("dinner", "- 돈까스비빔밥\n- 들깨무채국\n- 달걀찜\n- 열무김치\n- 리치");
        menu.put("7월 17일", new HashMap<>());
        menu.get("7월 17일").put("breakFirst", "- 미운영");
        menu.get("7월 17일").put("lunch", "미운영");
        menu.get("7월 17일").put("dinner", "미운영");

        for (String key : menu.keySet()) {
            assertEquals(menu.get(key).get("breakFirst"), tipMenuRepository.findByDate(key).getBreakFast());
            assertEquals(menu.get(key).get("lunch"), tipMenuRepository.findByDate(key).getLunch());
            assertEquals(menu.get(key).get("dinner"), tipMenuRepository.findByDate(key).getDinner());
        }

        assertEquals(fileNameRepository.findByName("1").getFileName(), "학생식당 식단표(7.11).xlsx");
    }


}