package madeby.seoyun.menuplannerchatbotapi.service;

import madeby.seoyun.menuplannerchatbotapi.repository.TipMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TipMenuService {
    private TipMenuRepository repository;

    @Autowired
    public TipMenuService(TipMenuRepository repository) {
        this.repository = repository;
    }



}
