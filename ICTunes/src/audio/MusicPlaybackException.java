package audio;

/**
 * 음악 재생 중 발생할 수 있는 일반적인 예외를 처리하는 커스텀 예외 클래스입니다.
 */
public class MusicPlaybackException extends Exception {
    public MusicPlaybackException(String message) {
        super(message);
    }

    public MusicPlaybackException(String message, Throwable cause) {
        super(message, cause);
    }
}