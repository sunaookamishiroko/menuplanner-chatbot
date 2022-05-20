package madeby.seoyun.menuplannerchatbotapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
public class GetDatabaseApplicationRunner implements ApplicationRunner {
    private GetWeekMenuService getWeekMenuService;

    @Autowired
    public GetDatabaseApplicationRunner(GetWeekMenuService getWeekMenuService) {
        this.getWeekMenuService = getWeekMenuService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception{
        //getWeekMenuService.getDataAndSaveToDatabase();
    }
}
