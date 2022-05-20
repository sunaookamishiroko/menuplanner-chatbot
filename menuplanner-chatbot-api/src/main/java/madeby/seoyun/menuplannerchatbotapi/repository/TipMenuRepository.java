package madeby.seoyun.menuplannerchatbotapi.repository;

import madeby.seoyun.menuplannerchatbotapi.model.TipMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * TIP 지하 식당 메뉴 DB의 쿼리를 위한 JpaRespository를 구현한 인터페이스
 *
 * @filename : TipMenuRepository.java
 * @Author : lsy
 */
@Repository
public interface TipMenuRepository extends JpaRepository<TipMenu, String> {
    TipMenu findByDate(String date);
}
