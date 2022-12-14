package madeby.seoyun.menuplannerchatbotapi.repository;

import madeby.seoyun.menuplannerchatbotapi.model.RestaurantProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 식당 메뉴 정보 쿼리를 위한 JpaRespository를 구현한 인터페이스
 *
 * @filename : RestaurantPropertyRepository.java
 * @Author : lsy
 */
@Repository
public interface RestaurantPropertyRepository extends JpaRepository<RestaurantProperty, Integer> {
}
