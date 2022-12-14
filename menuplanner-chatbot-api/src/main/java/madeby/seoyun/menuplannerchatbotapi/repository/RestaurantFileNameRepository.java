package madeby.seoyun.menuplannerchatbotapi.repository;

import madeby.seoyun.menuplannerchatbotapi.model.RestaurantFileName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 식당 파일 이름 DB 쿼리를 위한 JpaRespository를 구현한 인터페이스
 *
 * @filename : RestaurantFileNameRepository.java
 * @Author : lsy
 */
@Repository
public interface RestaurantFileNameRepository extends JpaRepository<RestaurantFileName, Integer> {
}
