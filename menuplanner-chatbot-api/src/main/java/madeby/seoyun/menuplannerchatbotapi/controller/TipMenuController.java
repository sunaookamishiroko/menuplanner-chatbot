package madeby.seoyun.menuplannerchatbotapi.controller;

import madeby.seoyun.menuplannerchatbotapi.component.ParsingMenuData;
import madeby.seoyun.menuplannerchatbotapi.service.TipMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TIP 지하 식당 메뉴에 대한 요청을 처리하는 controller
 *
 * @filename : TipMenuController.java
 * @Author : lsy
 */
@RestController
public class TipMenuController {

    private TipMenuService service;

    @Autowired
    public TipMenuController(TipMenuService service) {
        this.service = service;
    }

    /**
     * TIP 지하 식당 메뉴 관련 정보의 json을 문자열 형태로 응답한다.
     * 서버가 메뉴를 파싱하고 있을 때는 잠시 후에 다시 시도해달라는 문자열을 응답한다.
     *
     * @ param : 없음
     * @ return String : 카카오 챗봇 메시지 형식의 문자열
     */
    @PostMapping("/get-tip-menu")
    public String getTipMenu() {
        if (ParsingMenuData.isParsingMenuDataWorking)
            return service.makeWorkingNowJson();
        else
            return service.makeMenuJson();
    }
}
