package madeby.seoyun.menuplannerchatbotapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

/**
 * Spring project가 시작될 때 시작되는 서비스로 데이터가 존재하거나 최신 데이터인지 알아내어
 * DB에 데이터를 저장하거나 저장하지 않는 역할을 수행하는 서비스
 *
 * @filename : CheckDatabaseApplicationRunner.java
 * @Author : lsy
 */
@Service
public class CheckDatabaseApplicationRunner implements ApplicationRunner {
    private GetWeekMenuService getWeekMenuService;

    @Autowired
    public CheckDatabaseApplicationRunner(GetWeekMenuService getWeekMenuService) {
        this.getWeekMenuService = getWeekMenuService;
    }

    /**
     * 파일 이름 DB에 데이터가 존재하거나, 데이터가 최신이 아니면
     * 메뉴 파싱 데이터 저장 작업을 시작한다.
     *
     * @ param ApplicationArguments args : spring project를 실행할 때 넣는 실행 인자. 여기선 사용하지 않음
     * @ return : 없음
     * @ exception : 예외 발생시 서버 중지
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!getWeekMenuService.isDatabaseDataExist() || getWeekMenuService.isRecentData())
            getWeekMenuService.getDataAndSaveToDatabase();
    }
}
