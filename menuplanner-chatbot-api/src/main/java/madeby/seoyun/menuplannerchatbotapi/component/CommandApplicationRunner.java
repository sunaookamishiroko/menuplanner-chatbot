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
 * @filename : CommandApplicationRunner.java
 * @Author : lsy
 */
@Component
public class CommandApplicationRunner implements ApplicationRunner {
    private final ParsingMenu parsingMenu;
    private final SettingProperty settingProperty;

    @Autowired
    public CommandApplicationRunner(ParsingMenu parsingMenu, SettingProperty settingProperty) {
        this.parsingMenu = parsingMenu;
        this.settingProperty = settingProperty;
    }

    /**
     * 옵션에 따라 작업을 수행한다.
     * --parse=t  | 파싱하기
     * --parse=f | 파싱하지 않기
     * --forse   | 최근 데이터 / DB에 데이터 존재 유무 상관없이 강제로 파싱하기
     * @ param ApplicationArguments args : spring project를 실행할 때 넣는 실행 인자.
     * @ return : 없음
     */
    @Override
    public void run(ApplicationArguments args) throws Exception{

        // parse option
        List<String> parseList = args.getOptionValues("--parse");
        // parse force option
        List<String> forceList = args.getOptionValues("--force");

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

        // 식당 정보, 서버 운영 정보 존재 체크
        setting();

        // parse option
        switch (parseList.get(0)) {
            case "t":
                parse(forceList.get(0));
                break;
            case "f":
                LogData.printLog("파싱을 진행하지 않습니다...", "run");
                break;
        }
    }

    /**
     * 옵션에 따라 파싱을 수행한다.
     *
     * @ param String force : true면 파싱 강제 실행
     * @ return : 없음
     */
    private void parse(String force) {
        if (force.equals("t")) {
            LogData.printLog("파싱을 진행합니다...", "parse");
            parsingMenu.getDataAndSaveToDatabase();
        }
        else if (!parsingMenu.isDatabaseDataExist() || !parsingMenu.isRecentData()) {
            LogData.printLog("파싱을 진행합니다...", "parse");
            parsingMenu.getDataAndSaveToDatabase();
        }
    }

    /**
     * 식당 정보, 서버 운영 정보가 존재하지 않으면 기본 정보를 생성한다.
     *
     * @ param : 없음
     * @ return : 없음
     */
    private void setting() {
        if (!settingProperty.isPropertyExist()) {
            settingProperty.saveProperty();
        }

        if (!settingProperty.isServerInfoExist()) {
            settingProperty.saveServerInfo();
        }
    }

}
