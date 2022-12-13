package madeby.seoyun.menuplannerchatbotapi.component;

import madeby.seoyun.menuplannerchatbotapi.exceptions.WrongCommandException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 서버가 시작될 때 명령어를 받아 수행하는 컴포넌트
 *
 * @filename : CheckDatabaseApplicationRunner.java
 * @Author : lsy
 */
@Component
public class CommandApplicationRunner implements ApplicationRunner {
    private ParsingMenu parsingMenu;

    @Autowired
    public CommandApplicationRunner(ParsingMenu parsingMenu) {
        this.parsingMenu = parsingMenu;
    }

    /**
     * 옵션에 따라 작업을 수행한다.
     * --vacation=t  | 방학 모드
     * --vacation=f | 학기 모드
     * --parse=t  | 파싱하기
     * --parse=f | 파싱하지 않기
     * --forse   | 최근 데이터 / DB에 데이터 존재 유무 상관없이 강제로 파싱하기
     * @ param ApplicationArguments args : spring project를 실행할 때 넣는 실행 인자.
     * @ return : 없음
     */
    @Override
    public void run(ApplicationArguments args) throws Exception{

        // vacation option
        List<String> vacationList = args.getOptionValues("--vacation");
        // parse option
        List<String> parseList = args.getOptionValues("--parse");
        // parse force option
        List<String> forceList = args.getOptionValues("--force");


        // vacation 명령어 체크
        if (vacationList == null) {
            vacationList = new ArrayList<>();
            // 기본 -> 학기 모드
            vacationList.add("f");
        }

        if (vacationList.size() > 1) {
            throw new WrongCommandException("잘못된 명령어입니다. 하나만 입력해주세요. --vacation=" + vacationList);
        }

        if (!vacationList.get(0).equals("t") && !vacationList.get(0).equals("f")) {
            throw new WrongCommandException("잘못된 명령어입니다. true나 false로 입력해주세요. --vacation=" + vacationList.get(0));
        }

        // parse 명령어 체크
        if (parseList == null) {
            parseList = new ArrayList<>();
            // 기본 -> 파싱 모드
            parseList.add("t");
        }

        if (parseList.size() > 1) {
            throw new WrongCommandException("잘못된 명령어입니다. 하나만 입력해주세요. --parse=" + parseList);
        }

        if (!parseList.get(0).equals("t") && !parseList.get(0).equals("f")) {
            throw new WrongCommandException("잘못된 명령어입니다. true나 false로 입력해주세요. --parse=" + parseList.get(0));
        }

        // force 명령어 체크
        if (forceList == null) {
            forceList = new ArrayList<>();
            // 기본 -> 강제 아님
            forceList.add("f");
        } else {
            forceList.add(0, "t");
        }

        // vacation option
        switch (vacationList.get(0)) {
            case "t":
                LogData.printLog("방학 모드로 설정했습니다...", "run");
                vacation();
                break;
            case "f":
                LogData.printLog("학기 모드로 설정했습니다...", "run");
                break;
        }

        // parse option
        switch (parseList.get(0)) {
            case "t":
                LogData.printLog("파싱을 진행합니다...", "run");
                parse(forceList.get(0));
                break;
            case "f":
                LogData.printLog("파싱을 진행하지 않습니다...", "run");
                break;
        }

    }

    private void parse(String force) {
        if (force.equals("t")) {
            parsingMenu.getDataAndSaveToDatabase();
        }
        else if (!parsingMenu.isDatabaseDataExist() || !parsingMenu.isRecentData()) {
            parsingMenu.getDataAndSaveToDatabase();
        }
    }

    private void vacation() {
        parsingMenu.setVacation(true);
    }
}
