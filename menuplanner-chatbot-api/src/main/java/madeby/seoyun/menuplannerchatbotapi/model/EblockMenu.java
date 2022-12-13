package madeby.seoyun.menuplannerchatbotapi.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * E동 식당 메뉴에 대한 DB 스키마 정의
 *
 * @filename : EblockMenu.java
 * @Author : lsy
 */
@Entity
@Table(name = "eBlockMenu")
public class EblockMenu implements Serializable {

    @Id
    private String date;

    private String lunch;
    private String dinner;

    public EblockMenu() {}

    public EblockMenu(String date, String lunch, String dinner) {
        this.date = date;
        this.lunch = lunch;
        this.dinner = dinner;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLunch() {
        return lunch;
    }

    public void setLunch(String lunch) {
        this.lunch = lunch;
    }

    public String getDinner() {
        return dinner;
    }

    public void setDinner(String dinner) {
        this.dinner = dinner;
    }
}
