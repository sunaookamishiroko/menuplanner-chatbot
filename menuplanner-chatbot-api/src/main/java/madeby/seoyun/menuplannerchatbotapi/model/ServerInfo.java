package madeby.seoyun.menuplannerchatbotapi.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 서버 운영에 필요한 정보들에 대한 DB 스키마 정의
 *
 * @filename : ServerInfo.java
 * @Author : lsy
 */
@Entity
@Table(name = "serverInfo")
public class ServerInfo implements Serializable {

    @Id
    int id;

    boolean IsVacation;

    public ServerInfo() {}

    public ServerInfo(int id, boolean isVacation) {
        this.id = id;
        IsVacation = isVacation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isVacation() {
        return IsVacation;
    }

    public void setVacation(boolean vacation) {
        IsVacation = vacation;
    }
}
