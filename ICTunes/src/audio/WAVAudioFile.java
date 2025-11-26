package audio;

import model.Music;          // Music 모델 클래스 import
import playback.MusicPlayer; // 실제 WAV 파일 재생을 담당하는 MusicPlayer import

import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * WAV 형식의 오디오 파일을 표현하고 재생을 관리하는 구체적인 클래스입니다.
 * AudioFile 추상 클래스를 상속받으며, 내부적으로 playback.MusicPlayer를 사용하여 실제 오디오 처리를 위임합니다.
 */
public class WAVAudioFile extends AudioFile {
    private MusicPlayer audioPlayer; // 실제 오디오 재생을 담당하는 플레이어

    /**
     * WAVAudioFile의 생성자입니다. model.Music 객체를 받아 초기화하고,
     * 실제 오디오 재생을 위해 playback.MusicPlayer를 로드합니다.
     *
     * @param musicData 이 WAVAudioFile이 표현하는 음악의 메타데이터
     * @throws MusicFileNotFoundException 음악 파일이 존재하지 않거나 유효하지 않을 때 발생
     * @throws MusicPlaybackException     오디오 시스템 오류 등으로 인해 재생 준비에 실패할 때 발생
     */
    public WAVAudioFile(Music musicData) throws MusicFileNotFoundException, MusicPlaybackException {
        super(musicData); // 상위 AudioFile 클래스의 생성자 호출 (파일 경로 유효성 검사 포함)

        // 실제 파일 경로를 다시 확인 (super 생성자에서 기본적인 유효성 검사를 했지만, File 객체 생성 전 다시 확인)
        File file = new File(musicData.getFilePath());
        if (!file.exists() || !file.isFile()) {
            throw new MusicFileNotFoundException("지정된 WAV 음악 파일을 찾을 수 없거나 유효하지 않습니다: " + musicData.getFilePath());
        }
        if (!file.getName().toLowerCase().endsWith(".wav")) {
            throw new MusicFileNotFoundException("지원하지 않는 오디오 파일 형식입니다. WAV 파일만 지원됩니다: " + musicData.getFilePath());
        }

        try {
            audioPlayer = new MusicPlayer();
            // playback.MusicPlayer에 리스너 등록: 재생이 끝나면 자동으로 isPlaying 상태를 false로 변경합니다.
            // 이 리스너는 내부적으로 동작하며, MusicPlayerGUI에 연결되는 리스너와는 별개입니다.
            audioPlayer.addPlaybackListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    // STOP 이벤트가 발생했으나 일시정지가 아닌 경우 (자연스러운 재생 종료)
                    // 현재 음악이 실제로 재생 중이었다면 (isPlaying이 true인 경우) 상태를 업데이트합니다.
                    if (WAVAudioFile.this.isPlaying && !audioPlayer.isPaused()) {
                        WAVAudioFile.this.isPlaying = false;
                        WAVAudioFile.this.isPaused = false;
                        System.out.println("음악 재생 완료: " + getTitle());
                    }
                }
            });
            audioPlayer.load(musicData.getFilePath()); // 오디오 파일을 MusicPlayer에 로드
        } catch (LineUnavailableException e) {
            throw new MusicPlaybackException("오디오 라인을 사용할 수 없습니다: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new MusicPlaybackException("오디오 파일 로드 중 입출력 오류가 발생했습니다: " + e.getMessage(), e);
        } catch (UnsupportedAudioFileException e) {
            throw new MusicPlaybackException("지원하지 않는 오디오 파일 형식입니다. WAV 파일만 지원됩니다: " + e.getMessage(), e);
        }
        
        System.out.println("WAVAudioFile '" + getTitle() + "' 로드 준비 완료.");
    }

    /**
     * 음악 재생을 시작하거나 일시정지된 상태에서 재개합니다.
     * @throws MusicPlaybackException 재생 중 오류 발생 시
     */
    @Override
    public void play() throws MusicPlaybackException {
        if (audioPlayer == null) {
            throw new MusicPlaybackException("오디오 플레이어가 초기화되지 않았습니다.");
        }
        if (audioPlayer.isPaused()) { // 일시정지 상태에서 재개
            audioPlayer.play();
            isPlaying = true;
            isPaused = false;
        } else if (!audioPlayer.isPlaying()) { // 처음 재생하거나 중지 후 재생
            audioPlayer.play();
            isPlaying = true;
            isPaused = false;
        }
        // 이미 재생 중인 경우 아무것도 하지 않음 (또는 예외 발생)
    }

    /**
     * 현재 재생 중인 음악을 일시정지합니다.
     * @throws MusicPlaybackException 일시정지 중 오류 발생 시
     */
    @Override
    public void pause() throws MusicPlaybackException {
        if (audioPlayer == null) {
            throw new MusicPlaybackException("오디오 플레이어가 초기화되지 않았습니다.");
        }
        if (audioPlayer.isPlaying() && !audioPlayer.isPaused()) {
            audioPlayer.pause();
            isPlaying = false;
            isPaused = true;
        }
    }

    /**
     * 현재 재생 중인 음악을 완전히 중지합니다.
     * @throws MusicPlaybackException 중지 중 오류 발생 시
     */
    @Override
    public void stop() throws MusicPlaybackException {
        if (audioPlayer != null && (audioPlayer.isPlaying() || audioPlayer.isPaused())) {
            audioPlayer.stop();
            isPlaying = false;
            isPaused = false;
        }
    }

    /**
     * 이 오디오 파일과 관련된 모든 리소스를 해제합니다.
     */
    @Override
    public void close() {
        if (audioPlayer != null) {
            audioPlayer.stop(); // 먼저 재생 중인 것을 중지
            // audioPlayer.close()와 같은 명시적 메서드가 playback.MusicPlayer에 없으므로
            // stop()이 리소스 해제 역할도 겸하도록 설계된 것으로 판단됩니다.
            // 필요하다면 playback.MusicPlayer에 별도의 close()를 추가할 수 있습니다.
            audioPlayer = null; // 참조 해제
        }
        isPlaying = false;
        isPaused = false;
        System.out.println("WAVAudioFile '" + getTitle() + "' 리소스 해제 완료.");
    }
    
    /**
     * 현재 재생 위치를 반환합니다.
     * @return 현재 재생 위치 (마이크로초 단위), 재생 중이 아니면 0
     */
    @Override
    public long getCurrentPosition() {
        if (audioPlayer != null) {
            return audioPlayer.getCurrentPosition();
        }
        return 0;
    }
    
    /**
     * 전체 재생 길이를 반환합니다.
     * @return 전체 재생 길이 (마이크로초 단위), 로드되지 않았으면 0
     */
    @Override
    public long getDuration() {
        if (audioPlayer != null) {
            return audioPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 지정된 위치로 음악 재생 포인터를 이동합니다.
     * @param microsecondPosition 이동할 위치 (마이크로초 단위)
     */
    @Override
    public void setPosition(long microsecondPosition) {
        if (audioPlayer != null) {
            audioPlayer.setMicrosecondPosition(microsecondPosition);
        }
    }

    /**
     * playback.MusicPlayer의 LineListener를 직접 등록할 수 있도록 합니다.
     * 주로 GUI에서 재생 완료 등의 이벤트를 받기 위함입니다.
     * @param listener 추가할 LineListener
     */
    @Override
    public void addPlaybackListener(LineListener listener) {
        if (audioPlayer != null) {
            audioPlayer.addPlaybackListener(listener);
        }
    }
}