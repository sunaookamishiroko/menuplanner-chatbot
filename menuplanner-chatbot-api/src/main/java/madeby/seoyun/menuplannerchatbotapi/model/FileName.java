package madeby.seoyun.menuplannerchatbotapi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 각각 식당들의 메뉴 파일 이름에 대한 DB 스키마 정의
 *
 * @filename : FileName.java
 * @Author : lsy
 */
@Entity
@Table(name = "fileNames")
public class FileName implements Serializable {

    @Id
    String name;

    @Column(name = "fileName")
    String fileName;

    public FileName() {}

    public FileName(String name, String fileName) {
        this.name = name;
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
