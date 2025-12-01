package audio;

import model.Music;

import javax.sound.sampled.LineListener;

/*
 * 오디오 파일 재생을 위한 인터페이스
 * 모든 오디오 파일 구현체가 따라야 할 계약을 정의.
 */

public abstract class AudioFile {
    protected Music musicData;
    protected boolean isPlaying = false; // 현재 재생 중인지 여부
    protected boolean isPaused = false;  // 현재 일시정지 중인지 여부
    
    public AudioFile(Music musicData) throws MusicFileNotFoundException {
        
    	
        if (musicData == null || musicData.getFilePath() == null || musicData.getFilePath().trim().isEmpty()) {
            
            if (!"empty_file_path".equals(musicData.getFilePath())) {
                throw new MusicFileNotFoundException("유효한 음악 파일 경로가 지정되지 않았습니다.");
            }
        }
        this.musicData = musicData;
    }

    // --- 추상 메서드: 하위 클래스에서 반드시 구현해야 할 재생 관련 기능 ---
    public abstract void play() throws MusicPlaybackException;
    public abstract void pause() throws MusicPlaybackException;
    public abstract void stop() throws MusicPlaybackException;
    public abstract void close(); // 사용된 리소스 해제
    public abstract void addPlaybackListener(LineListener listener); // 재생 상태 변화 리스너 추가
    public abstract long getCurrentPosition(); // 현재 재생 위치 (마이크로초 단위)
    public abstract long getDuration(); // 전체 재생 길이 (마이크로초 단위)
    public abstract void setPosition(long microsecondPosition); // 특정 위치로 이동 (마이크로초 단위)

    
    public Music getMusicData() {
        return musicData;
    }
    

    // --- 접근자 (model.Music으로부터 프록시) ---
    public String getTitle() {
        return musicData.getTitle();
    }

    public String getArtist() {
        return musicData.getArtist();
    }

    public String getGenre() {
        return musicData.getGenre();
    }

    public int getReleaseYear() {
        return musicData.getReleaseYear();
    }

    public String getAlbum() {
        return musicData.getAlbum();
    }

    public String getFilePath() {
        return musicData.getFilePath();
    }
    
    public String getCoverPath() {
        return musicData.getCoverPath();
    }

    // --- 재생 상태 접근자 ---
    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isPaused() {
        return isPaused;
    }

    // --- 좋아요 상태 관리 ---
    public boolean isLiked() {
        return musicData.isLiked();
    }

    public void setLiked(boolean liked) {
        musicData.setLiked(liked);
    }
    
    
    @Override
    public String toString() {
        return musicData.toString();
    }
}