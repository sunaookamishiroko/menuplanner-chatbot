package madeby.seoyun.menuplannerchatbotapi.component;

import madeby.seoyun.menuplannerchatbotapi.model.RestaurantProperty;
import madeby.seoyun.menuplannerchatbotapi.model.ServerInfo;
import madeby.seoyun.menuplannerchatbotapi.repository.ServerInfoRepository;
import madeby.seoyun.menuplannerchatbotapi.repository.RestaurantPropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 식당 정보, 서버 운영 정보를 체크하여 세팅하기 위한 컴포넌트
 *
 * @filename : SettingProperty.java
 * @Author : lsy
 */
@Component
public class SettingProperty {

    private final RestaurantPropertyRepository restaurantPropertyRepository;
    private final ServerInfoRepository serverInfoRepository;

    @Autowired
    public SettingProperty(RestaurantPropertyRepository restaurantPropertyRepository,
                           ServerInfoRepository serverInfoRepository) {
        this.restaurantPropertyRepository = restaurantPropertyRepository;
        this.serverInfoRepository = serverInfoRepository;
    }


    /**
     * 식당 정보가 존재하는지 체크한다.
     *
     * @ param : 없음
     * @ return boolean : 존재하면 true, 존재하지 않으면 false
     */
    @Transactional(readOnly = true)
    public boolean isPropertyExist() {
        LogData.printLog("식당 정보가 존재하는지 확인...", "isPropertyExist");

        if(restaurantPropertyRepository.findById(0).isPresent()&&
                restaurantPropertyRepository.findById(1).isPresent()) {
            LogData.printLog("식당 정보가 존재합니다.", "isPropertyExist");
            return true;
        } else {
            LogData.printLog("식당 정보가 존재하지 않습니다.", "isPropertyExist");
            return false;
        }
    }

    /**
     * 서버 운영에 필요한 정보가 존재하는지 체크한다.
     *
     * @ param : 없음
     * @ return boolean : 존재하면 true, 존재하지 않으면 false
     */
    @Transactional(readOnly = true)
    public boolean isServerInfoExist() {
        LogData.printLog("서버 운영 정보가 존재하는지 확인...", "isServerInfoExist");

        if(serverInfoRepository.findById(0).isPresent()&&
                serverInfoRepository.findById(1).isPresent()) {
            LogData.printLog("서버 운영 정보가 존재합니다.", "isServerInfoExist");
            return true;
        } else {
            LogData.printLog("서버 운영 정보가 존재하지 않습니다.", "isServerInfoExist");
            return false;
        }
    }

    /**
     * 임의의 식당 정보를 저장한다.
     *
     * @ param : 없음
     * @ return : 없음
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveProperty() {
        LogData.printLog("임의의 식당 정보 저장중...", "saveProperty");

        RestaurantProperty eBlockProperty =
                new RestaurantProperty(
                        0,
                        null, "11:30 - 13:50", "16:50 - 18:40",
                        null, "6500원",
                        "https://ibook.kpu.ac.kr/Viewer/menu01");

        RestaurantProperty tipProperty =
                new RestaurantProperty(
                        1,
                        "09:30 - 11:00", "11:30 - 14:30", "17:00 - 18:50",
                        "3000원", "6000원",
                        "https://ibook.kpu.ac.kr/Viewer/menu02"
                );

        restaurantPropertyRepository.save(eBlockProperty);
        restaurantPropertyRepository.save(tipProperty);

        LogData.printLog("저장 완료", "saveProperty");
    }

    /**
     * 임의의 서버 운영 정보를 저장한다.
     *
     * @ param : 없음
     * @ return : 없음
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveServerInfo() {
        LogData.printLog("서버 운영 정보 저장중...", "saveServerInfo");

        serverInfoRepository.save(new ServerInfo(0, false));
        serverInfoRepository.save(new ServerInfo(1, false));

        LogData.printLog("저장 완료", "saveServerInfo");
    }

    /**
     * 인자로 받은 식당의 방학 모드 여부를 return한다.
     *
     * @ param int id : 식당 식별 번호
     * @ return boolean : 방학이면 true, 학기중이면 false
     */
    @Transactional(readOnly = true)
    public boolean checkIsVacation(int id) {
        if(serverInfoRepository.findById(id).orElseThrow().isVacation()) {
            return true;
        } else {
            return false;
        }
    }
}
