package madeby.seoyun.menuplannerchatbotapi.service;

import madeby.seoyun.menuplannerchatbotapi.repository.EblockMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EblockMenuService {
    private EblockMenuRepository repository;

    @Autowired
    public EblockMenuService(EblockMenuRepository repository) {
        this.repository = repository;
    }


}
