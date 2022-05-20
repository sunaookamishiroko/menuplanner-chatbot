package madeby.seoyun.menuplannerchatbotapi.repository;

import madeby.seoyun.menuplannerchatbotapi.model.EblockMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * E동 식당 메뉴 DB의 쿼리를 위한 JpaRespository를 구현한 인터페이스
 *
 * @filename : EblockMenuRepository.java
 * @Author : lsy
 */
@Repository
public interface EblockMenuRepository extends JpaRepository<EblockMenu, String> {
    EblockMenu findByDate(String date);
}
