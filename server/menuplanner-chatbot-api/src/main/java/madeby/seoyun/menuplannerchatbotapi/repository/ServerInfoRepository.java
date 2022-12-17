package madeby.seoyun.menuplannerchatbotapi.repository;

import madeby.seoyun.menuplannerchatbotapi.model.ServerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 서버 운영 정보 쿼리를 위한 JpaRespository를 구현한 인터페이스
 *
 * @filename : ServerInfoRepository.java
 * @Author : lsy
 */
@Repository
public interface ServerInfoRepository extends JpaRepository<ServerInfo, Integer> {
}
