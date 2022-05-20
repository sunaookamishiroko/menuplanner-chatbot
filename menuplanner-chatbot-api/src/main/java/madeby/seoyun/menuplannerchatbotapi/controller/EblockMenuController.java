package madeby.seoyun.menuplannerchatbotapi.controller;

import madeby.seoyun.menuplannerchatbotapi.service.EblockMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EblockMenuController {

    private EblockMenuService service;

    @Autowired
    public EblockMenuController(EblockMenuService service) {
        this.service = service;
    }

    @GetMapping("/get-eblock-menu")
    public void getEblockMenu() {
        
    }
}
