package playback; 

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/*
	.wav 형식의 음악 파일을 재생, 일시정지, 중지하는 기능을 제공하는 클래스.
 */
public class MusicPlayer {
    private Clip clip;
    private AudioInputStream audioInputStream;

    private boolean isPlaying = false;
    private boolean isPaused = false;
    private long clipTimePosition = 0;

    private LineListener externalLineListener;

    
    //음악 파일을 로드하고 재생 준비를 마치는...
    public void load(String filePath) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        stop();
        
        File musicFile = new File(filePath);
        if (!musicFile.exists() || !musicFile.isFile()) {
            throw new IOException("음악 파일을 찾을 수 없거나 유효하지 않습니다: " + filePath);
        }

        audioInputStream = AudioSystem.getAudioInputStream(musicFile);
        clip = AudioSystem.getClip();

        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP && isPlaying) {
                if (!isPaused) {
                    isPlaying = false;
                    clipTimePosition = 0;
                }
            }
            
            if (externalLineListener != null) {
                externalLineListener.update(event);
            }
        });

        clip.open(audioInputStream);

        isPlaying = false;
        isPaused = false;
        clipTimePosition = 0;
        System.out.println("playback.MusicPlayer 로드 완료: " + filePath);
    }


     //현재 로드된 음악을 재생. 일시정지 상태였다면 이어서 재생.
    public void play() {
        if (clip == null) {
            System.err.println("재생할 음악이 로드되지 않았습니다.");
            return;
        }
        if (isPlaying) {
            return;
        }

        if (isPaused) {
            clip.setMicrosecondPosition(clipTimePosition);
            isPaused = false;
        } else {
            clip.setFramePosition(0);
            clipTimePosition = 0;
        }
        
        clip.start();
        isPlaying = true;
        System.out.println("playback.MusicPlayer 재생 시작.");
    }

    
    //현재 재생 중인 음악을 일시정지.
    public void pause() {
        if (isPlaying && !isPaused) {
            clipTimePosition = clip.getMicrosecondPosition();
            clip.stop();
            isPlaying = false;
            isPaused = true;
            System.out.println("playback.MusicPlayer 재생 일시정지.");
        }
    }

    
    //현재 재생 중인 음악을 완전히 중지하고 클립 리소스를 해제.
    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
        if (audioInputStream != null) {
            try {
                audioInputStream.close();
            } catch (IOException e) {
                System.err.println("오디오 스트림 종료 중 오류 발생: " + e.getMessage());
            }
            audioInputStream = null;
        }
        isPlaying = false;
        isPaused = false;
        clipTimePosition = 0;
        System.out.println("playback.MusicPlayer 재생 중지 및 리소스 해제.");
    }

    /*
     * 현재 음악이 재생 중인지 여부를 반환.
     * return 재생 중이면 true, 아니면 false
     */
    public boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    /*
     * 현재 음악이 일시정지 중인지 여부를 반환.
     * return 일시정지 중이면 true, 아니면 false
     */
    public boolean isPaused() {
        return isPaused;
    }

    /*
     * 클립에 외부 LineListener를 추가하여 재생 완료 등의 이벤트를 받을 수 있도록 함.
     * param listener 추가할 LineListener
     */
    public void addPlaybackListener(LineListener listener) {
        this.externalLineListener = listener;
    }
    
    /*
     * 현재 재생 위치를 마이크로초 단위로 반환.
     * return 현재 재생 위치 (마이크로초 단위), 로드된 클립이 없으면 0
     */
    public long getCurrentPosition() {
        if (clip != null) {
            return clip.getMicrosecondPosition();
        }
        return 0;
    }

    /*
     * 클립의 총 재생 길이를 마이크로초 단위로 반환.
     * return 총 재생 길이 (마이크로초 단위), 로드된 클립이 없으면 0
     */
    public long getDuration() {
        if (clip != null) {
            return clip.getMicrosecondLength();
        }
        return 0;
    }
    
    /*
     * 지정된 마이크로초 위치로 재생 헤드를 이동.
     * param microsecondPosition 이동할 마이크로초 위치
     */
    public void setMicrosecondPosition(long microsecondPosition) {
        if (clip != null) {
            clip.setMicrosecondPosition(microsecondPosition);
            if (isPlaying()) {
                 clip.start();
            }
        }
    }
}