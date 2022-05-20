package madeby.seoyun.menuplannerchatbotapi.controller;

import madeby.seoyun.menuplannerchatbotapi.service.TipMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TipMenuController {

    private TipMenuService service;

    @Autowired
    public TipMenuController(TipMenuService service) {
        this.service = service;
    }

    @GetMapping("/get-tip-menu")
    public String getTipMenu() {
        return service.makeJson();
    }
}
