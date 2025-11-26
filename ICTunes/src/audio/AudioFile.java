package audio;

import model.Music;

import javax.sound.sampled.LineListener;

/**
 * 모든 오디오 파일 객체가 가져야 할 공통적인 속성과 행위를 정의하는 추상 클래스.
 * model.Music 객체를 포함하여 음악 메타데이터를 관리하며,
 * 실제 재생 로직은 하위 클래스에서 구현함.
 */

public abstract class AudioFile {
    protected Music musicData;
    protected boolean isPlaying = false; // 현재 재생 중인지 여부
    protected boolean isPaused = false;  // 현재 일시정지 중인지 여부

    /**
     * 추상 클래스의 생성자는 model.Music 객체를 받아 초기화.
     * @param musicData 음악 메타데이터 (제목, 가수, 파일 경로 등 포함)
     * @throws MusicFileNotFoundException 음악 파일 경로가 유효하지 않을 때 발생
     */
    
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

    
    /**
     * 이 AudioFile과 관련된 Music 메타데이터 객체를 반환합니다.
     * @return Music 객체
     */
    public Music getMusicData() {
        return musicData;
    }
    

    // --- 메타데이터 접근자 (model.Music으로부터 프록시) ---
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
    
    /**
     * AudioFile 객체의 문자열 표현을 반환합니다.
     * model.Music의 toString()을 활용하여 좋아요 상태도 표시합니다.
     * @return AudioFile 객체의 정보가 담긴 문자열
     */
    @Override
    public String toString() {
        return musicData.toString();
    }
}