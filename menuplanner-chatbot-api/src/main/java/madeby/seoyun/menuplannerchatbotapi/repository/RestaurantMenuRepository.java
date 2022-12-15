package madeby.seoyun.menuplannerchatbotapi.repository;

import madeby.seoyun.menuplannerchatbotapi.model.RestaurantMenu;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 식당 메뉴 쿼리를 위한 JpaRespository를 구현한 인터페이스
 *
 * @filename : RestaurantMenuRepository.java
 * @Author : lsy
 */
@Repository
public interface RestaurantMenuRepository extends JpaRepository<RestaurantMenu, Long> {
    @EntityGraph(attributePaths = "restaurantProperty")
    RestaurantMenu findByRestaurantPropertyIdAndDate(int restaurantCode, String date);

    @Transactional(rollbackFor = Exception.class)
    void deleteAllByRestaurantPropertyId(int restaurantCode);
}
