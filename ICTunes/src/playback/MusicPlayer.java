package playback; // 패키지명 변경: playback -> ictunes.playback

import javax.sound.sampled.*; // 오디오 시스템 관련 클래스 임포트
import java.io.File;
import java.io.IOException;

/**
 * .wav 형식의 음악 파일을 재생, 일시정지, 중지하는 저수준 기능을 제공하는 클래스입니다.
 * javax.sound.sampled 패키지를 활용합니다.
 */
public class MusicPlayer {
    private Clip clip; // 오디오 데이터를 로드하고 제어하는 데 사용되는 Clip 인터페이스
    private AudioInputStream audioInputStream; // 오디오 데이터 스트림

    private boolean isPlaying = false;    // 현재 재생 중인지 여부
    private boolean isPaused = false;     // 현재 일시정지 중인지 여부
    private long clipTimePosition = 0;    // 일시정지 시 클립의 현재 위치 (마이크로초 단위)

    private LineListener externalLineListener; // 외부에서 추가할 수 있는 리스너

    /**
     * 음악 파일을 로드하고 재생 준비를 마칩니다.
     * @param filePath 재생할 .wav 파일의 경로
     * @throws LineUnavailableException 라인이 사용 불가능할 때 발생
     * @throws IOException            파일 입출력 오류 발생 시
     * @throws UnsupportedAudioFileException 지원하지 않는 오디오 파일 형식일 때 발생
     */
    public void load(String filePath) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        stop(); // 이전에 재생 중이거나 로드된 클립이 있다면 중지하고 닫습니다.
        
        File musicFile = new File(filePath);
        if (!musicFile.exists() || !musicFile.isFile()) {
            throw new IOException("음악 파일을 찾을 수 없거나 유효하지 않습니다: " + filePath);
        }

        audioInputStream = AudioSystem.getAudioInputStream(musicFile);
        clip = AudioSystem.getClip(); // 시스템으로부터 클립 객체 얻기

        // 내부 LineListener를 먼저 등록하여 재생 완료 시 isPlaying 상태를 관리합니다.
        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP && isPlaying) {
                // 재생이 자연스럽게 끝나거나 stop() 메서드로 완전히 중지되었을 때만 처리합니다.
                // pause()로 일시정지된 경우에는 isPlaying을 false로 만들지 않습니다.
                if (!isPaused) { // 일시정지가 아닐 때만 재생 중이 아님으로 처리
                    isPlaying = false;
                    clipTimePosition = 0; // 재생 완료 시 위치 초기화
                }
            }
            // 외부 리스너가 있다면 이벤트를 전달합니다.
            if (externalLineListener != null) {
                externalLineListener.update(event);
            }
        });

        clip.open(audioInputStream); // 오디오 스트림 열기

        isPlaying = false;
        isPaused = false;
        clipTimePosition = 0; // 새 파일 로드 시 재생 위치 초기화
        System.out.println("playback.MusicPlayer 로드 완료: " + filePath);
    }

    /**
     * 현재 로드된 음악을 재생합니다. 일시정지 상태였다면 이어서 재생됩니다.
     */
    public void play() {
        if (clip == null) {
            System.err.println("재생할 음악이 로드되지 않았습니다.");
            return;
        }
        if (isPlaying) { // 이미 재생 중이라면 아무것도 하지 않습니다.
            return;
        }

        if (isPaused) { // 일시정지 상태였다면 저장된 위치부터 이어서 재생
            clip.setMicrosecondPosition(clipTimePosition);
            isPaused = false; // 일시정지 상태 해제
        } else { // 처음 재생하거나 중지 후 재생
            clip.setFramePosition(0); // 시작 위치로 이동
            clipTimePosition = 0; // 처음부터 재생하므로 위치 초기화
        }
        
        clip.start(); // 재생 시작
        isPlaying = true; // 재생 중 상태 설정
        System.out.println("playback.MusicPlayer 재생 시작.");
    }

    /**
     * 현재 재생 중인 음악을 일시정지합니다.
     */
    public void pause() {
        if (isPlaying && !isPaused) {
            clipTimePosition = clip.getMicrosecondPosition(); // 현재 재생 위치 저장
            clip.stop(); // 재생 일시 중지
            isPlaying = false; // 재생 중 상태 해제
            isPaused = true; // 일시정지 상태 설정
            System.out.println("playback.MusicPlayer 재생 일시정지.");
        }
    }

    /**
     * 현재 재생 중인 음악을 완전히 중지하고 클립 리소스를 해제합니다.
     */
    public void stop() {
        if (clip != null) {
            clip.stop(); // 재생 중지
            clip.close(); // 클립 리소스 해제
            clip = null; // 참조 해제
        }
        if (audioInputStream != null) {
            try {
                audioInputStream.close(); // 오디오 스트림 닫기
            } catch (IOException e) {
                System.err.println("오디오 스트림 종료 중 오류 발생: " + e.getMessage());
            }
            audioInputStream = null; // 참조 해제
        }
        isPlaying = false; // 재생 중 상태 해제
        isPaused = false; // 일시정지 상태 해제
        clipTimePosition = 0; // 재생 위치 초기화
        System.out.println("playback.MusicPlayer 재생 중지 및 리소스 해제.");
    }

    /**
     * 현재 음악이 재생 중인지 여부를 반환합니다.
     * @return 재생 중이면 true, 아니면 false
     */
    public boolean isPlaying() {
        // 클립이 null이 아니면서 재생 중인지 확인합니다.
        // 클립 내부 상태와 isPlaying 플래그를 모두 고려합니다.
        return clip != null && clip.isRunning();
    }

    /**
     * 현재 음악이 일시정지 중인지 여부를 반환합니다.
     * @return 일시정지 중이면 true, 아니면 false
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * 클립에 외부 LineListener를 추가하여 재생 완료 등의 이벤트를 받을 수 있도록 합니다.
     * (내부 리스너는 항상 등록되어 MusicPlayer의 상태를 관리합니다.)
     * @param listener 추가할 LineListener
     */
    public void addPlaybackListener(LineListener listener) {
        this.externalLineListener = listener;
    }
    
    /**
     * 현재 재생 위치를 마이크로초 단위로 반환합니다.
     * @return 현재 재생 위치 (마이크로초 단위), 로드된 클립이 없으면 0
     */
    public long getCurrentPosition() {
        if (clip != null) {
            return clip.getMicrosecondPosition();
        }
        return 0;
    }

    /**
     * 클립의 총 재생 길이를 마이크로초 단위로 반환합니다.
     * @return 총 재생 길이 (마이크로초 단위), 로드된 클립이 없으면 0
     */
    public long getDuration() {
        if (clip != null) {
            return clip.getMicrosecondLength();
        }
        return 0;
    }
    
    /**
     * 지정된 마이크로초 위치로 재생 헤드를 이동시킵니다.
     * @param microsecondPosition 이동할 마이크로초 위치
     */
    public void setMicrosecondPosition(long microsecondPosition) {
        if (clip != null) {
            clip.setMicrosecondPosition(microsecondPosition);
            // 만약 현재 일시정지 상태에서 위치만 바꾼다면, 재생하지 않도록 주의
            if (isPlaying()) { // 재생 중이라면 바로 그 위치부터 재생 시작
                 clip.start();
            }
        }
    }
}