package audio;

import main.ApplicationMain;
import model.Music;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * WAV 오디오 파일을 관리하고 재생을 제어하는 클래스.
 */

public class WAVAudioFile { 

    private Music musicData; 
    private String filePath; 

    private Clip clip;
    private long lastPosition; 

    private List<LineListener> externalLineListeners; 

    private final String uniqueId; 
    
    private FloatControl gainControl; 

    public WAVAudioFile(Music music) throws MusicFileNotFoundException, MusicPlaybackException {
        this.musicData = music;
        this.filePath = music.getFilePath();
        this.externalLineListeners = new ArrayList<>();
        this.uniqueId = generateUniqueId(music);

        Path path = Paths.get(filePath);
        if (!Files.exists(path) || !Files.isReadable(path)) {
            throw new MusicFileNotFoundException("오디오 파일이 존재하지 않거나 읽을 수 없습니다: " + filePath);
        }

        openAudioStream(); 
    }

    public WAVAudioFile(String filePath) throws MusicFileNotFoundException, MusicPlaybackException {
        this.filePath = filePath;
        File file = new File(filePath);
        String fileName = file.getName();
        String title = fileName.substring(0, fileName.lastIndexOf('.'));
        this.musicData = new Music(title, "알 수 없는 아티스트", "알 수 없는 장르", 0, "알 수 없는 앨범", filePath, ApplicationMain.BASE_RESOURCE_PATH + "images/default_cover.jpg");
        this.externalLineListeners = new ArrayList<>();
        this.uniqueId = generateUniqueId(musicData);

        Path path = Paths.get(filePath);
        if (!Files.exists(path) || !Files.isReadable(path)) {
            throw new MusicFileNotFoundException("오디오 파일이 존재하지 않거나 읽을 수 없습니다: " + filePath);
        }

        openAudioStream();
    }
    
    public void openAudioStream() throws MusicPlaybackException {
    	
        if (clip != null && clip.isOpen()) {
            for(LineListener listener : externalLineListeners) {
                
                clip.removeLineListener(listener); 
            }
            clip.close();
            clip = null; 
        }
        
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath));
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            this.lastPosition = 0; 
            
            // 음량 조절 컨트롤 가져오기
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            } else {
                gainControl = null;
                System.err.println("[WAVAudioFile] MASTER_GAIN 컨트롤이 지원되지 않습니다.");
            }

            
            for(LineListener listener : externalLineListeners) {
                clip.addLineListener(listener);
            }
        } catch (UnsupportedAudioFileException e) {
            throw new MusicPlaybackException("지원되지 않는 오디오 파일 형식입니다: " + filePath, e);
        } catch (IOException e) {
            throw new MusicPlaybackException("오디오 파일 입출력 오류 : " + filePath, e);
        } catch (LineUnavailableException e) {
            throw new MusicPlaybackException("오디오 라인을 사용할 수 없습니다 : " + filePath, e);
        }
    }

    public void play() throws MusicPlaybackException {
        if (clip == null || !clip.isOpen()) {
            throw new MusicPlaybackException("재생을 시작할 수 없습니다. (경로: " + filePath + ")");
        }
        clip.start();
    }

    public void pause() throws MusicPlaybackException {
        if (clip == null || !clip.isOpen()) {
            throw new MusicPlaybackException("일시정지할 수 없습니다. (경로: " + filePath + ")");
        }
        if (clip.isRunning()) {
            lastPosition = clip.getMicrosecondPosition();
            clip.stop();
        }
    }

    public void resume() throws MusicPlaybackException {
        if (clip == null || !clip.isOpen()) {
            throw new MusicPlaybackException("재생을 재개할 수 없습니다. (경로: " + filePath + ")");
        }
        if (!clip.isRunning()) {
            clip.setMicrosecondPosition(lastPosition);
            clip.start();
        }
    }

    public void stopAudio() {
        if (clip != null && clip.isOpen()) {
            try {
                for(LineListener listener : externalLineListeners) {
                    if (clip != null) { 
                        clip.removeLineListener(listener);
                    }
                }
                clip.stop();
                clip.close(); 
                lastPosition = 0;
            } catch (Exception e) {
                System.err.println("clip 정지 또는 닫기 중 오류: " + e.getMessage());
            } finally {
                clip = null; 
            }
        }
    }

    public void setPosition(long microseconds) throws MusicPlaybackException {
        if (clip == null || !clip.isOpen()) {
            throw new MusicPlaybackException("재생 위치를 설정할 수 없습니다. (경로: " + filePath + ")");
        }
        clip.setMicrosecondPosition(microseconds);
        lastPosition = microseconds;
    }
    
    public void setVolume(float volume) {
        if (gainControl == null || clip == null || !clip.isOpen()) {
            return; 
        }
        if (volume < 0f || volume > 1f) {
            throw new IllegalArgumentException("음량은 0.0f에서 1.0f 사이여야 합니다.");
        }
        
        float minGain = gainControl.getMinimum();
        float maxGain = gainControl.getMaximum();
        
        float gain;
        if (volume == 0.0f) { 
            gain = minGain;
        } else {
            final float LOG_MIN_VOLUME_REF = (float)Math.log10(0.0001f); 
            final float LOG_MAX_VOLUME_REF = (float)Math.log10(1.0f);   
            
            float logVolume = (float)Math.log10(volume);
            float linearMappedVolume = (logVolume - LOG_MIN_VOLUME_REF) / (LOG_MAX_VOLUME_REF - LOG_MIN_VOLUME_REF);
            
            gain = minGain + (maxGain - minGain) * linearMappedVolume;
            gain = Math.max(minGain, Math.min(maxGain, gain));
        }
        gainControl.setValue(gain);
    }

    public float getVolume() {
        if (gainControl == null || clip == null || !clip.isOpen()) {
            return 1.0f; 
        }
        float dB = gainControl.getValue();
        float minGain = gainControl.getMinimum();
        float maxGain = gainControl.getMaximum();

        if (dB <= minGain) return 0.0f;
        
        final float LOG_MIN_VOLUME_REF = (float)Math.log10(0.0001f);
        final float LOG_MAX_VOLUME_REF = (float)Math.log10(1.0f);
        
        float linearMappedGain = (dB - minGain) / (maxGain - minGain);
        float logVolume = LOG_MIN_VOLUME_REF + (LOG_MAX_VOLUME_REF - LOG_MIN_VOLUME_REF) * linearMappedGain;
        
        float volume = (float) Math.pow(10, logVolume);
        
        return Math.max(0.0f, Math.min(1.0f, volume));
    }
    
    public void addLineListener(LineListener listener) {
        if (listener != null && !externalLineListeners.contains(listener)) {
            externalLineListeners.add(listener);
            if (clip != null) { 
                clip.addLineListener(listener);
            }
        }
    }
    
    public void removeLineListener(LineListener listener) {
        if (listener != null) {
            externalLineListeners.remove(listener);
            if (clip != null) { 
                clip.removeLineListener(listener);
            }
        }
    }

    public Clip getClip() {
        return clip;
    }

    public boolean isPlaying() { return clip != null && clip.isRunning(); }
    public boolean isOpen() { return clip != null && clip.isOpen(); }
    public long getCurrentPosition() { return clip != null ? clip.getMicrosecondPosition() : 0; }
    public long getDuration() { return clip != null ? clip.getMicrosecondLength() : 0; }
    
    public Music getMusicData() { return musicData; }
    public String getTitle() { return musicData.getTitle(); }
    public String getArtist() { return musicData.getArtist(); }
    public String getGenre() { return musicData.getGenre(); }
    public int getReleaseYear() { return musicData.getReleaseYear(); }
    public String getAlbum() { return musicData.getAlbum(); }
    public String getCoverPath() { return musicData.getCoverPath(); }
    public boolean isLiked() { return musicData.isLiked(); }
    public void setLiked(boolean liked) { musicData.setLiked(liked); }
    
    private String generateUniqueId(Music music) { return music.getFilePath(); }

    @Override public boolean equals(Object o) { if (this == o) return true; if (o == null || getClass() != o.getClass()) return false; WAVAudioFile that = (WAVAudioFile) o; return uniqueId.equals(that.uniqueId); }
    @Override public int hashCode() { return uniqueId.hashCode(); }
}
