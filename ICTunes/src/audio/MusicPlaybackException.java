package audio;

public class MusicPlaybackException extends Exception {
    public MusicPlaybackException(String message) {
        super(message);
    }
    public MusicPlaybackException(String message, Throwable cause) {
        super(message, cause);
    }
}