package madeby.seoyun.menuplannerchatbotapi.exceptions;

/**
 * 명령어를 잘못 설정했을 때 발생하는 exception
 *
 * @filename : WrongCommandException.java
 * @Author : lsy
 */
public class WrongCommandException extends Exception{
    public WrongCommandException(String msg) {
        super(msg);
    }
}
