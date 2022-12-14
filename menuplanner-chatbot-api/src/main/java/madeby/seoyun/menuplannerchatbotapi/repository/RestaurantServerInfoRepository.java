package madeby.seoyun.menuplannerchatbotapi.repository;

import madeby.seoyun.menuplannerchatbotapi.model.RestaurantServerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 식당 정보 DB의 쿼리를 위한 JpaRespository를 구현한 인터페이스
 *
 * @filename : RestaurantInfoRepository.java
 * @Author : lsy
 */
@Repository
public interface RestaurantServerInfoRepository extends JpaRepository<madeby.seoyun.menuplannerchatbotapi.model.RestaurantServerInfo, Integer> {
}
