package madeby.seoyun.menuplannerchatbotapi.exceptions;

/**
 * DB에 쿼리할 때 응답이 없을 경우 발생하는 exception
 *
 * @filename : DatabaseConnectFailedException.java
 * @Author : lsy
 */
public class DatabaseConnectFailedException extends RuntimeException{
    public DatabaseConnectFailedException(String message) {
        super(message);
    }
}
