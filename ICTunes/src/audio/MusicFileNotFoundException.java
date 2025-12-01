package audio;

public class MusicFileNotFoundException extends Exception {
    public MusicFileNotFoundException(String message) {
        super(message);
    }
    public MusicFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}