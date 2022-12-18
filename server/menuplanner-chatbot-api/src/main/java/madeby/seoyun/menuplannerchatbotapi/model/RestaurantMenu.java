package madeby.seoyun.menuplannerchatbotapi.model;

import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 각각 식당들의 메뉴 DB 스키마 정의
 *
 * @filename : RestaurantMenu.java
 * @Author : lsy
 */
@Entity
@Table(name = "restaurantMenu")
public class RestaurantMenu implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String date;

    @Nullable
    private String breakFast;

    private String lunch;
    private String dinner;

    @OneToOne
    @JoinColumn(name = "restaurantCode")
    private RestaurantProperty restaurantProperty;

    public RestaurantMenu() {}

    public RestaurantMenu(String date, @Nullable String breakFast, String lunch, String dinner, RestaurantProperty restaurantProperty) {
        this.date = date;
        this.breakFast = breakFast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.restaurantProperty = restaurantProperty;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Nullable
    public String getBreakFast() {
        return breakFast;
    }

    public void setBreakFast(@Nullable String breakFast) {
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

    public RestaurantProperty getRestaurantProperty() {
        return restaurantProperty;
    }

    public void setRestaurantProperty(RestaurantProperty restaurantProperty) {
        this.restaurantProperty = restaurantProperty;
    }

}
