package madeby.seoyun.menuplannerchatbotapi.exceptions;

/**
 * 파싱에 실패할 때 발생하는 exception
 *
 * @filename : ParsingDataFailedException.java
 * @Author : lsy
 */
public class ParsingDataFailedException extends RuntimeException{
    public ParsingDataFailedException(String msg) {
        super(msg);
    }
}
