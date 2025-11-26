package audio;

import java.io.FileNotFoundException;
// import java.lang.Throwable; // << 이 줄은 필요 없으므로 제거합니다.

/**
 * 음악 파일을 찾을 수 없을 때 발생하는 예외를 처리하는 커스텀 예외 클래스입니다.
 * FileNotFoundException을 확장하며, 발생 원인(cause)을 포함할 수 있도록 개선되었습니다.
 */
public class MusicFileNotFoundException extends FileNotFoundException {
    public MusicFileNotFoundException(String message) {
        super(message);
    }

    public MusicFileNotFoundException(String message, Throwable cause) {
        // FileNotFoundException은 (String, Throwable) 생성자를 직접 제공하지 않으므로,
        // 부모 클래스의 (String) 생성자를 호출하고 initCause() 메서드로 원인을 설정합니다.
        super(message);
        if (cause != null) { // cause가 null이 아닐 경우에만 설정
            this.initCause(cause);
        }
    }
}