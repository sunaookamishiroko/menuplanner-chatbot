package madeby.seoyun.menuplannerchatbotapi.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

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
     * 파일 이름 DB에 데이터가 존재하지않거나, 데이터가 최신이 아니면
     * 메뉴 파싱 데이터 저장 작업을 시작한다.
     *
     * @ param ApplicationArguments args : spring project를 실행할 때 넣는 실행 인자. 여기선 사용하지 않음
     * @ return : 없음
     */
    @Override
    public void run(ApplicationArguments args) throws Exception{
        List<String> parseList = args.getOptionValues("parse");
        List<String> vacationList = args.getOptionValues("vacation");

        // 명령어 갯수에 따라 설정
        switch (parseList.size()) {
            case 0:
                parseList.add("true");
                break;
            case 1:
                break;
            default:
                LogData.printLog("잘못된 명령어입니다. 하나만 입력해주세요. --parse=" + parseList, "run");
                throw new Exception();
        }

        switch (vacationList.size()) {
            case 0:
                vacationList.add("true");
                break;
            case 1:
                break;
            default:
                LogData.printLog("잘못된 명령어입니다. 하나만 입력해주세요. --vacation=" + vacationList, "run");
                throw new Exception();
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
            default:
                LogData.printLog("잘못된 명령어입니다. true나 false로 입력해주세요. --parse=" + parseList.get(0), "run");
                throw new Exception();
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
            default:
                LogData.printLog("잘못된 명령어입니다. true나 false로 입력해주세요. --vacation=" + vacationList.get(0), "run");
                throw new Exception();
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
