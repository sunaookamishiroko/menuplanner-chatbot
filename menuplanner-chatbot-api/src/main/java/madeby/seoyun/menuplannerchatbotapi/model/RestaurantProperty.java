package madeby.seoyun.menuplannerchatbotapi.model;

import org.springframework.lang.Nullable;

import javax.persistence.*;

/**
 * 각각 식당들의 운영 시간, 가격 DB 스키마 정의
 *
 * @filename : FileName.java
 * @Author : lsy
 */
@Entity
@Table(name = "restaurantProperty")
public class RestaurantProperty {
    @Id
    private int id;

    @Nullable
    private String breakFastTime;

    private String lunchTime;
    private String dinnerTime;

    @Nullable
    private String breakFastPrice;

    private String commonPrice;

    private String menuUrl;

    @OneToOne
    @PrimaryKeyJoinColumn
    private RestaurantMenu restaurantMenu;

    public RestaurantProperty() {}

    public RestaurantProperty(int id, @Nullable String breakFastTime, String lunchTime, String dinnerTime, @Nullable String breakFastPrice, String commonPrice, String menuUrl) {
        this.id = id;
        this.breakFastTime = breakFastTime;
        this.lunchTime = lunchTime;
        this.dinnerTime = dinnerTime;
        this.breakFastPrice = breakFastPrice;
        this.commonPrice = commonPrice;
        this.menuUrl = menuUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Nullable
    public String getBreakFastTime() {
        return breakFastTime;
    }

    public void setBreakFastTime(@Nullable String breakFastTime) {
        this.breakFastTime = breakFastTime;
    }

    public String getLunchTime() {
        return lunchTime;
    }

    public void setLunchTime(String lunchTime) {
        this.lunchTime = lunchTime;
    }

    public String getDinnerTime() {
        return dinnerTime;
    }

    public void setDinnerTime(String dinnerTime) {
        this.dinnerTime = dinnerTime;
    }

    @Nullable
    public String getBreakFastPrice() {
        return breakFastPrice;
    }

    public void setBreakFastPrice(@Nullable String breakFastPrice) {
        this.breakFastPrice = breakFastPrice;
    }

    public String getCommonPrice() {
        return commonPrice;
    }

    public void setCommonPrice(String commonPrice) {
        this.commonPrice = commonPrice;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    public RestaurantMenu getRestaurantMenu() {
        return restaurantMenu;
    }

    public void setRestaurantMenu(RestaurantMenu restaurantMenu) {
        this.restaurantMenu = restaurantMenu;
    }
}