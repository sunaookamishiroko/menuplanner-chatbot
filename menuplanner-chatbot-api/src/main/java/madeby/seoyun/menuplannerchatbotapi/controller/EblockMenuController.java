package madeby.seoyun.menuplannerchatbotapi.controller;

import madeby.seoyun.menuplannerchatbotapi.service.EblockMenuService;
import madeby.seoyun.menuplannerchatbotapi.service.GetWeekMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EblockMenuController {

    private EblockMenuService service;

    @Autowired
    public EblockMenuController(EblockMenuService service) {
        this.service = service;
    }

    @PostMapping("/get-eblock-menu")
    public String getEblockMenu() {
        if (GetWeekMenuService.isGetWeekMenuServiceWorking)
            return service.makeWorkingNowJson();
        else
            return service.makeMenuJson();
    }
}
