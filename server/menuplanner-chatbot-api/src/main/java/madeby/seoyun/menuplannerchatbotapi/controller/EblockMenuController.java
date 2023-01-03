package madeby.seoyun.menuplannerchatbotapi.controller;

import madeby.seoyun.menuplannerchatbotapi.service.DefaultMessageService;
import madeby.seoyun.menuplannerchatbotapi.service.EblockMenuService;
import madeby.seoyun.menuplannerchatbotapi.component.ParsingMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    private final EblockMenuService eblockMenuService;
    private final DefaultMessageService defaultMessageService;
    private final ParsingMenu parsingMenu;

    @Value("${parsing-message}")
    private String parsingMessage;

    @Value("${monday-message}")
    private String mondayMessage;

    @Autowired
    public EblockMenuController(EblockMenuService eblockMenuService, DefaultMessageService defaultMessageService,
                                ParsingMenu parsingMenu) {
        this.eblockMenuService = eblockMenuService;
        this.defaultMessageService = defaultMessageService;
        this.parsingMenu = parsingMenu;
    }


    /**
     * E동 식당 메뉴 관련 정보의 json을 문자열 형태로 응답한다.
     * 월요일 메뉴가 올라오기 전에는 식당으로부터 메뉴가 올라올 때까지 기다려달라는 json을 응답한다.
     * 서버가 메뉴를 파싱하고 있을 때는 잠시 후에 다시 시도해달라는 json을 응답한다.
     *
     * @ param : 없음
     * @ return String : 카카오 챗봇 메시지 형식의 문자열
     */
    @PostMapping("/eblock")
    public String getEblockMenu() {
        if (parsingMenu.checkBeforeParsing())
            return defaultMessageService.makeJson(mondayMessage);
        else if (parsingMenu.checkNowParsingEblock())
            return defaultMessageService.makeJson(parsingMessage);
        else
            return eblockMenuService.makeMenuJson();
    }
}
