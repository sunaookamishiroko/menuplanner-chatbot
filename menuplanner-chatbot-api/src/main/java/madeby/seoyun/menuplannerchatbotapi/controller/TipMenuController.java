package madeby.seoyun.menuplannerchatbotapi.controller;

import madeby.seoyun.menuplannerchatbotapi.service.GetWeekMenuService;
import madeby.seoyun.menuplannerchatbotapi.service.TipMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TipMenuController {

    private TipMenuService service;

    @Autowired
    public TipMenuController(TipMenuService service) {
        this.service = service;
    }

    @PostMapping("/get-tip-menu")
    public String getTipMenu() {
        if (GetWeekMenuService.isGetWeekMenuServiceWorking)
            return service.makeWorkingNowJson();
        else
            return service.makeMenuJson();
    }
}
