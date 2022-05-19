package madeby.seoyun.menuplannerchatbotapi.repository;

import madeby.seoyun.menuplannerchatbotapi.model.TipMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipMenuRepository extends JpaRepository<TipMenu, String> {
}
