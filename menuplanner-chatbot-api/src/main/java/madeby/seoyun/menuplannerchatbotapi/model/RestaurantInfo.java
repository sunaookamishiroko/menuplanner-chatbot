package madeby.seoyun.menuplannerchatbotapi.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 각각 식당들의 메뉴 파일 이름에 대한 DB 스키마 정의
 *
 * @filename : RestaurantInfo.java
 * @Author : lsy
 */
@Entity
@Table(name = "restaurantInfo")
public class RestaurantInfo implements Serializable {

    @Id
    int id;

    String fileName;
    boolean IsVacation;

    public RestaurantInfo() {}

    public RestaurantInfo(int id, String fileName, boolean isVacation) {
        this.id = id;
        this.fileName = fileName;
        IsVacation = isVacation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isVacation() {
        return IsVacation;
    }

    public void setVacation(boolean vacation) {
        IsVacation = vacation;
    }
}
