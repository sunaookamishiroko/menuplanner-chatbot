package madeby.seoyun.menuplannerchatbotapi.repository;

import madeby.seoyun.menuplannerchatbotapi.model.EblockMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EblockMenuRepository extends JpaRepository<EblockMenu, String> {
    EblockMenu findByDate(String date);
}
