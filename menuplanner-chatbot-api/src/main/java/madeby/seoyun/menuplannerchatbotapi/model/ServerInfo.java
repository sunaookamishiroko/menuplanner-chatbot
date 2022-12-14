package madeby.seoyun.menuplannerchatbotapi.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 각각 식당들의 정보에 대한 DB 스키마 정의
 *
 * @filename : RestaurantInfo.java
 * @Author : lsy
 */
@Entity
@Table(name = "restaurantInfo")
public class ServerInfo implements Serializable {

    @Id
    int id;

    boolean IsVacation;

    public ServerInfo() {}

    public ServerInfo(int id, boolean isVacation) {
        this.id = id;
        IsVacation = isVacation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isVacation() {
        return IsVacation;
    }

    public void setVacation(boolean vacation) {
        IsVacation = vacation;
    }
}
