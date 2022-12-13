package madeby.seoyun.menuplannerchatbotapi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * TIP 지하 식당 메뉴에 대한 DB 스키마 정의
 *
 * @filename : TipMenu.java
 * @Author : lsy
 */
@Entity
@Table(name = "tipMenu")
public class TipMenu implements Serializable {

    @Id
    private String date;

    @Column(name = "breakFast")
    private String breakFast;
    private String lunch;
    private String dinner;

    public TipMenu() {}

    public TipMenu(String date, String breakFast, String lunch, String dinner) {
        this.date = date;
        this.breakFast = breakFast;
        this.lunch = lunch;
        this.dinner = dinner;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBreakFast() {
        return breakFast;
    }

    public void setBreakFast(String breakFast) {
        this.breakFast = breakFast;
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
