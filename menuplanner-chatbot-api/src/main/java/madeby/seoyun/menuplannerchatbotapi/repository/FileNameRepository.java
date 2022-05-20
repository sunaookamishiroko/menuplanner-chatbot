package madeby.seoyun.menuplannerchatbotapi.repository;

import madeby.seoyun.menuplannerchatbotapi.model.FileName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 파일 이름 DB의 쿼리를 위한 JpaRespository를 구현한 인터페이스
 *
 * @filename : FileNameRepository.java
 * @Author : lsy
 */
@Repository
public interface FileNameRepository extends JpaRepository<FileName, String> {
    FileName findByName(String name);
}
