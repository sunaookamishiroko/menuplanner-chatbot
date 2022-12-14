package madeby.seoyun.menuplannerchatbotapi.component;

import madeby.seoyun.menuplannerchatbotapi.model.RestaurantProperty;
import madeby.seoyun.menuplannerchatbotapi.repository.RestaurantInfoRepository;
import madeby.seoyun.menuplannerchatbotapi.repository.RestaurantPropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 식당 정보, 방학 여부 등을 체크하여 세팅하기 위한 컴포넌트
 *
 * @filename : SettingProperty.java
 * @Author : lsy
 */
@Component
public class SettingProperty {

    private final RestaurantPropertyRepository restaurantPropertyRepository;
    private final RestaurantInfoRepository restaurantInfoRepository;

    private boolean isVacation;

    @Autowired
    public SettingProperty(RestaurantPropertyRepository restaurantPropertyRepository, RestaurantInfoRepository restaurantInfoRepository) {
        this.restaurantPropertyRepository = restaurantPropertyRepository;
        this.restaurantInfoRepository = restaurantInfoRepository;
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
     * 식당 정보가 존재하는지 체크한다.
     *
     * @ param : 없음
     * @ return boolean : 존재하면 true, 존재하지 않으면 false
     */
    @Transactional(readOnly = true)
    public boolean isVacationExist() {
        LogData.printLog("방학 모드 확인...", "isVacationExist");

        if(restaurantInfoRepository.findById(0).isPresent()) {
            LogData.printLog("방학 모드 데이터가 존재합니다...", "isVacationExist");
            return true;
        } else {
            LogData.printLog("방학 모드 데이터가 존재하지 않습니다...", "isVacationExist");
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
     * 방학 모드를 설정한다.
     *
     * @ param : 없음
     * @ return : 없음
     */
    public void setVacation() {
        if(restaurantInfoRepository.findById(0).get().isVacation()) {
            LogData.printLog("방학 모드 = true", "setVacation");
            isVacation = true;
        } else {
            LogData.printLog("방학 모드 = false", "setVacation");
            isVacation = false;
        }
    }

    /**
     * 방학 모드인지 아닌지 알려준다.
     *
     * @ param : 없음
     * @ return : boolean : 방학이면 true, 학기중이면 false
     */
    public boolean checkVacation() {
        return isVacation;
    }
}
