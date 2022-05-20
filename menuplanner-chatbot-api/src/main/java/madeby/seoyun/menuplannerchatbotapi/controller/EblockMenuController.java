package madeby.seoyun.menuplannerchatbotapi.controller;

import madeby.seoyun.menuplannerchatbotapi.service.EblockMenuService;
import madeby.seoyun.menuplannerchatbotapi.service.GetWeekMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * E동 식당 메뉴에 대한 요청을 처리하는 controller
 *
 * @filename : EblockMenuController.java
 * @Author : lsy
 */
@RestController
public class EblockMenuController {

    private EblockMenuService service;

    @Autowired
    public EblockMenuController(EblockMenuService service) {
        this.service = service;
    }

    /**
     * E동 식당 메뉴 / 시간 / 가격을
     * 카카오 챗봇 메시지 형식을 문자열로 반환한다.
     *
     * @ param : 없음
     * @ return : 카카오 챗봇 메시지 형식의 문자열
     * @ exception 예외사항
     */
    @PostMapping("/get-eblock-menu")
    public String getEblockMenu() {
        if (GetWeekMenuService.isGetWeekMenuServiceWorking)
            return service.makeWorkingNowJson();
        else
            return service.makeMenuJson();
    }
}
