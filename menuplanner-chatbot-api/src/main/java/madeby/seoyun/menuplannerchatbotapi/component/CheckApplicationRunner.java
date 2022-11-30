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
public class CheckApplicationRunner implements ApplicationRunner {
    private ParsingMenu parsingMenu;

    @Autowired
    public CheckApplicationRunner(ParsingMenu parsingMenu) {
        this.parsingMenu = parsingMenu;
    }

    /**
     * 옵션에 따라 작업을 수행한다.
     * --v=true  | 방학 모드
     * --v=false | 학기 모드
     * --p=true  | 파싱하기
     * --p=false | 파싱하지 않기
     *
     * @ param ApplicationArguments args : spring project를 실행할 때 넣는 실행 인자.
     * @ return : 없음
     */
    @Override
    public void run(ApplicationArguments args) throws Exception{
        // parse option
        List<String> parseList = args.getOptionValues("p");
        // vacation option
        List<String> vacationList = args.getOptionValues("v");

        // parse 명령어 체크
        if (parseList == null) {
            parseList = new ArrayList<>();
            // 기본 -> 파싱 모드
            parseList.add("true");
        }

        if (parseList.size() > 1) {
            throw new WrongCommandException("잘못된 명령어입니다. 하나만 입력해주세요. --p=" + parseList);
        }

        if (!parseList.get(0).equals("true") && !parseList.get(0).equals("false")) {
            throw new WrongCommandException("잘못된 명령어입니다. true나 false로 입력해주세요. --p=" + parseList.get(0));
        }

        // vacation 명령어 체크
        if (vacationList == null) {
            vacationList = new ArrayList<>();
            // 기본 -> 학기 모드
            vacationList.add("false");
        }

        if (vacationList.size() > 1) {
            throw new WrongCommandException("잘못된 명령어입니다. 하나만 입력해주세요. --v=" + vacationList);
        }

        if (!vacationList.get(0).equals("true") && !vacationList.get(0).equals("false")) {
            throw new WrongCommandException("잘못된 명령어입니다. true나 false로 입력해주세요. --v=" + vacationList.get(0));
        }

        // parse option
        switch (parseList.get(0)) {
            case "true":
                LogData.printLog("파싱을 진행합니다...", "run");
                parse();
                break;
            case "false":
                LogData.printLog("파싱을 진행하지 않습니다...", "run");
                break;
        }

        // vacation option
        switch (vacationList.get(0)) {
            case "true":
                LogData.printLog("방학 모드로 설정했습니다...", "run");
                vacation();
                break;
            case "false":
                LogData.printLog("학기 모드로 설정했습니다...", "run");
                break;
        }

    }

    private void parse() {
        if (!parsingMenu.isDatabaseDataExist() || !parsingMenu.isRecentData()) {
            parsingMenu.getDataAndSaveToDatabase();
        }
    }

    private void vacation() {
        parsingMenu.setVacation(true);
    }
}
