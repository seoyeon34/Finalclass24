package player;

import audio.MusicFileNotFoundException;
import audio.MusicPlaybackException;
import audio.WAVAudioFile;
import gui.MusicPlayerGUI;
import model.Music;

import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.Clip; 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/*
 * 음악 재생을 관리하는 클래스.
 * 재생 목록, 현재 재생 상태, 셔플/반복 모드 등을 처리.
 */

public class AppMusicPlayer implements LineListener {

    private MusicPlayerGUI gui; 
    private List<WAVAudioFile> playlist; 
    private List<WAVAudioFile> originalPlaylist; 
    private List<Music> allMusicList; 

    private WAVAudioFile currentAudioFile; 
    private int currentSongIndex; 

    private boolean isPlaying; 
    private boolean isPaused; 
    private boolean shuffleMode; 
    private boolean repeatMode; 
    private Random random; 
    
    private float masterVolume = 0.5f; 

    private static final int MAX_PLAYLIST_SIZE = 15; 

    public AppMusicPlayer(MusicPlayerGUI gui, List<Music> allMusicList) {
        this.gui = gui;
        this.allMusicList = allMusicList;
        this.playlist = new ArrayList<>();
        this.originalPlaylist = new ArrayList<>();
        this.currentSongIndex = -1;
        this.isPlaying = false;
        this.isPaused = false;
        this.shuffleMode = false;
        this.repeatMode = false;
        this.random = new Random();
    }

    public void setPlaylist(List<Music> musicList) throws MusicPlaybackException {
        stopPlaybackAndClearInternalLists();

        for (model.Music music : musicList) {
            try {
                if (originalPlaylist.size() >= MAX_PLAYLIST_SIZE) {
                    System.err.println("플레이리스트가 가득 찼습니다. " + MAX_PLAYLIST_SIZE + "곡까지만 추가할 수 있습니다.");
                    break; 
                }
                WAVAudioFile audioFile = new WAVAudioFile(music);
                audioFile.addLineListener(this); 
                this.originalPlaylist.add(audioFile); 
            } catch (MusicFileNotFoundException | MusicPlaybackException e) {
                System.err.println("음악 파일 로드 실패 (경로: " + music.getFilePath() + "): " + e.getMessage());
            }
        }
        
        this.playlist.addAll(this.originalPlaylist);

        if (!this.playlist.isEmpty()) {
            currentSongIndex = 0;
            currentAudioFile = playlist.get(currentSongIndex);
            currentAudioFile.setVolume(masterVolume);
        } else {
            currentSongIndex = -1;
            currentAudioFile = null;
        }
        gui.updatePlayerUI();
    }

    public void addSong(WAVAudioFile audioFile) throws MusicPlaybackException {
        if (originalPlaylist.size() >= MAX_PLAYLIST_SIZE) {
            throw new MusicPlaybackException("플레이리스트가 가득 찼습니다. " + MAX_PLAYLIST_SIZE + "곡까지만 추가할 수 있습니다.");
        }
        if (originalPlaylist.contains(audioFile)) { 
            throw new MusicPlaybackException("'" + audioFile.getTitle() + "'은(는) 이미 재생 목록에 있습니다.");
        }
        audioFile.addLineListener(this); 
        audioFile.setVolume(masterVolume);

        originalPlaylist.add(audioFile);
        playlist.add(audioFile); 

        if (currentAudioFile == null && playlist.size() == 1) {
            currentSongIndex = 0;
            currentAudioFile = playlist.get(currentSongIndex);
            currentAudioFile.setVolume(masterVolume);
        }
        
        if(shuffleMode){
            WAVAudioFile playingBeforeShuffle = currentAudioFile; 
            Collections.shuffle(playlist, random); 
            
            if(playingBeforeShuffle != null) {
                int newIndex = -1;
                 for (int i = 0; i < playlist.size(); i++) {
                    if (playlist.get(i).equals(playingBeforeShuffle)) {
                        newIndex = i;
                        break;
                    }
                }
                if (newIndex != -1) {
                    currentSongIndex = newIndex;
                    currentAudioFile = playlist.get(currentSongIndex);
                }
                else if (!playlist.isEmpty()){ 
                    currentSongIndex = 0;
                    currentAudioFile = playlist.get(0);
                } else { 
                    currentSongIndex = -1;
                    currentAudioFile = null;
                }
            } else if (!playlist.isEmpty()){ 
                currentSongIndex = 0;
                currentAudioFile = playlist.get(0);
            } else { 
                currentSongIndex = -1;
                currentAudioFile = null;
            }
        }
    }
    
    public void removeSong(WAVAudioFile audioFile) throws MusicPlaybackException {
        if (!originalPlaylist.contains(audioFile)) {
            throw new MusicPlaybackException("'" + audioFile.getTitle() + "'은(는) 재생 목록에 없습니다.");
        }

        if (currentAudioFile != null && currentAudioFile.equals(audioFile)) {
            try {
                stop();
            } catch (MusicPlaybackException e) {
                System.err.println("제거하려는 곡 재생 중지 중 오류: " + e.getMessage());
            }
        }
        
        originalPlaylist.remove(audioFile);
        playlist.remove(audioFile);

        if (currentAudioFile != null && !playlist.isEmpty()) {
            int newIndex = -1;
             for (int i = 0; i < playlist.size(); i++) {
                if (playlist.get(i).equals(currentAudioFile)) {
                    newIndex = i;
                    break;
                }
            }
            if (newIndex != -1) {
                currentSongIndex = newIndex; 
                currentAudioFile = playlist.get(currentSongIndex);
            } else { 
                if(!playlist.isEmpty()){
                    currentSongIndex = 0; 
                    currentAudioFile = playlist.get(0);
                    currentAudioFile.setVolume(masterVolume);
                } else {
                    currentSongIndex = -1;
                    currentAudioFile = null;
                    isPlaying = false;
                    isPaused = false;
                }
            }
        } else if (playlist.isEmpty()) { 
            currentSongIndex = -1;
            currentAudioFile = null;
            isPlaying = false;
            isPaused = false;
        } else {
            currentSongIndex = 0;
            currentAudioFile = playlist.get(0);
            currentAudioFile.setVolume(masterVolume);
        }
        
        if(shuffleMode){
            WAVAudioFile playingBeforeShuffle = currentAudioFile;
            Collections.shuffle(playlist, random);
            if(playingBeforeShuffle != null) {
                int newIndex = -1;
                for (int i = 0; i < playlist.size(); i++) {
                    if (playlist.get(i).equals(playingBeforeShuffle)) {
                        newIndex = i;
                        break;
                    }
                }
                if (newIndex != -1) {
                    currentSongIndex = newIndex;
                    currentAudioFile = playlist.get(currentSongIndex);
                }
                else if (!playlist.isEmpty()) {
                    currentSongIndex = 0;
                    currentAudioFile = playlist.get(0);
                }
                else { currentSongIndex = -1; currentAudioFile = null; }
            } else if (!playlist.isEmpty()) {
                currentSongIndex = 0;
                currentAudioFile = playlist.get(0);
            } else {
                currentSongIndex = -1;
                currentAudioFile = null;
            }
        }
        
        gui.updatePlayerUI();
    }

    public void clearPlaylist() {
        stopPlaybackAndClearInternalLists();
        gui.updatePlayerUI();
    }

    private void stopPlaybackAndClearInternalLists() {
        if (currentAudioFile != null) {
            currentAudioFile.stopAudio(); 
        }
        for (WAVAudioFile audioFile : originalPlaylist) { 
            audioFile.stopAudio();
            audioFile.removeLineListener(this); 
        }
        playlist.clear();
        originalPlaylist.clear();
        currentAudioFile = null;
        isPlaying = false;
        isPaused = false;
        currentSongIndex = -1;
    }
    
    /*
     * 음악을 재생하거나 일시정지 상태에서 재개.
     */
    
    public void play() throws MusicPlaybackException {
        if (currentAudioFile == null) {
            if (playlist.isEmpty()) {
                throw new MusicPlaybackException("재생할 음악이 없습니다.");
            }
            if (currentSongIndex == -1) currentSongIndex = 0;
            currentAudioFile = playlist.get(currentSongIndex);
        }

        if (currentAudioFile.isPlaying()) {
             isPlaying = true; 
             isPaused = false; 
             gui.updatePlayerUI(); 
             return;
        }

        if (isPaused) { 
            currentAudioFile.resume();
        } else { 
            currentAudioFile.stopAudio(); 
            currentAudioFile.openAudioStream(); 
            currentAudioFile.play();
        }

        isPlaying = true; 
        isPaused = false;
        currentAudioFile.setVolume(masterVolume); 
        gui.updatePlayerUI(); 
    }

    public void play(int index) throws MusicPlaybackException {
        if (index < 0 || index >= playlist.size()) {
            throw new MusicPlaybackException("유효하지 않은 재생 목록 인덱스입니다: " + index);
        }
        
        if (currentAudioFile != null && currentAudioFile.equals(playlist.get(index)) && isPlaying) {
             return; 
        }

        if (currentAudioFile != null) {
            currentAudioFile.stopAudio(); 
        }
        
        currentSongIndex = index; 
        currentAudioFile = playlist.get(currentSongIndex);
        
        isPlaying = false; 
        isPaused = false;
        
        play(); 
    }

    public void pause() throws MusicPlaybackException {
        if (currentAudioFile == null) {
            throw new MusicPlaybackException("재생 중인 음악이 없습니다.");
        }
        if (currentAudioFile.isPlaying()) { 
            currentAudioFile.pause();
            isPlaying = false; 
            isPaused = true; 
            gui.updatePlayerUI();
        } else if (isPaused){ 
            return;
        } else { 
            throw new MusicPlaybackException("음악이 재생 중이 아니므로 일시정지할 수 없습니다.");
        }
    }

    public void stop() throws MusicPlaybackException {
        if (currentAudioFile == null) {
            isPlaying = false;
            isPaused = false;
            gui.updatePlayerUI();
            return;
        }
        
        currentAudioFile.stopAudio();
        isPlaying = false;
        isPaused = false;
        gui.updatePlayerUI();
    }

    public void next() throws MusicPlaybackException {
        if (playlist.isEmpty()) {
            throw new MusicPlaybackException("재생할 다음 곡이 없습니다 (재생 목록 비어있음).");
        }
        
        if (currentAudioFile != null) {
            currentAudioFile.stopAudio();
        }

        if (shuffleMode) {
            currentSongIndex = random.nextInt(playlist.size());
        } else {
            currentSongIndex++;
            if (currentSongIndex >= playlist.size()) {
                if (repeatMode) {
                    currentSongIndex = 0;
                } else {
                    currentSongIndex = -1; 
                    currentAudioFile = null;
                    isPlaying = false;
                    isPaused = false;
                    gui.displayInfoMessage("재생 종료", "재생 목록의 마지막 곡입니다.");
                    gui.updatePlayerUI();
                    return; 
                }
            }
        }
        
        if (currentSongIndex != -1) {
            currentAudioFile = playlist.get(currentSongIndex);
            currentAudioFile.setVolume(masterVolume); 
            
            isPlaying = false; 
            isPaused = false;
            
            play(); 
        } else {
            gui.updatePlayerUI();
        }
    }

    public void previous() throws MusicPlaybackException {
        if (playlist.isEmpty()) {
            throw new MusicPlaybackException("재생할 이전 곡이 없습니다 (재생 목록 비어있음).");
        }

        if (currentAudioFile != null) {
            currentAudioFile.stopAudio();
        }

        if (shuffleMode) {
            currentSongIndex = random.nextInt(playlist.size());
        } else {
            currentSongIndex--;
            if (currentSongIndex < 0) {
                 if (repeatMode) {
                    currentSongIndex = playlist.size() - 1;
                } else {
                    currentSongIndex = -1; 
                    currentAudioFile = null;
                    isPlaying = false;
                    isPaused = false;
                    gui.displayInfoMessage("재생 종료", "재생 목록의 첫 곡입니다.");
                    gui.updatePlayerUI();
                    return; 
                }
            }
        }

        if (currentSongIndex != -1) {
            currentAudioFile = playlist.get(currentSongIndex);
            currentAudioFile.setVolume(masterVolume);
            
            isPlaying = false;
            isPaused = false;

            play();
        } else {
            gui.updatePlayerUI();
        }
    }

    public List<WAVAudioFile> getPlaylist() {
        return playlist;
    }

    public WAVAudioFile getCurrentAudioFile() {
        return currentAudioFile;
    }

    public int getCurrentSongIndex() { 
        if (currentAudioFile != null && !playlist.isEmpty()) {
            return playlist.indexOf(currentAudioFile);
        }
        return -1;
    }
    
    public void setVolume(float volume) {
        if (volume < 0f || volume > 1f) {
            throw new IllegalArgumentException("음량은 0.0f에서 1.0f 사이여야 합니다.");
        }
        this.masterVolume = volume;
        if (currentAudioFile != null) {
            currentAudioFile.setVolume(masterVolume);
        }
        gui.updatePlayerUI();
    }

    public float getVolume() {
        return masterVolume;
    }

    public void toggleShuffle() {
        shuffleMode = !shuffleMode;
        if (playlist.isEmpty()) {
            gui.updatePlayerUI();
            return;
        }

        boolean wasPlaying = isPlaying; 
        long currentPosition = 0; 
        WAVAudioFile currentlyPlayingInstance = currentAudioFile; 

        if (currentlyPlayingInstance != null && wasPlaying) {
            currentPosition = currentlyPlayingInstance.getCurrentPosition();
            try {
                currentlyPlayingInstance.stopAudio();
                isPlaying = false;
                isPaused = false;
            } catch (Exception e) {
                System.err.println("셔플 중 오디오 정지 오류: " + e.getMessage());
            }
        }

        if (shuffleMode) {
            Collections.shuffle(playlist, random);
        } else {
            playlist.clear();
            playlist.addAll(originalPlaylist); 
        }

        if (currentlyPlayingInstance != null) {
            int newIndex = -1;
            for (int i = 0; i < playlist.size(); i++) {
                if (playlist.get(i).equals(currentlyPlayingInstance)) { 
                    newIndex = i;
                    break;
                }
            }

            if (newIndex != -1) {
                currentSongIndex = newIndex;
                currentAudioFile = playlist.get(currentSongIndex); 
                
                if (wasPlaying) {
                    try {
                        play();     
                        currentAudioFile.setPosition(currentPosition); 
                    } catch (MusicPlaybackException e) {
                        System.err.println("셔플 후 재생 재개 오류: " + e.getMessage());
                    }
                }
            } else { 
                if (!playlist.isEmpty()) {
                    currentSongIndex = 0; 
                    currentAudioFile = playlist.get(0);
                    if (wasPlaying) {
                        try {
                            play();
                        } catch (MusicPlaybackException e) {
                            System.err.println("셔플 후 재생 재개 오류: " + e.getMessage());
                        }
                    }
                } else {
                    currentSongIndex = -1; 
                    currentAudioFile = null;
                    isPlaying = false; 
                    isPaused = false;
                }
            }
        } else if (!playlist.isEmpty()) { 
            currentSongIndex = 0;
            currentAudioFile = playlist.get(0);
            isPlaying = false;
            isPaused = false;
        } else { 
            currentSongIndex = -1; 
            currentAudioFile = null;
            isPlaying = false; 
            isPaused = false;
        }
        
        gui.updatePlaylistList();
        gui.updatePlayerUI();
    }

    public void toggleRepeat() {
        repeatMode = !repeatMode;
        gui.updatePlayerUI();
    }

    public void toggleLike(WAVAudioFile audioFile) {
        audioFile.setLiked(!audioFile.isLiked());
        
        for (Music music : allMusicList) {
            if (music.getFilePath().equals(audioFile.getMusicData().getFilePath())) {
                music.setLiked(audioFile.isLiked());
                break;
            }
        }
        gui.updatePlayerUI();
    }

    public List<WAVAudioFile> getLikedSongs() {
        return allMusicList.stream()
                .filter(Music::isLiked)
                .map(music -> {
                    try {
                        WAVAudioFile likedWav = new WAVAudioFile(music);
                        likedWav.setVolume(masterVolume);
                        likedWav.setLiked(true); 
                        return likedWav;
                    } catch (MusicFileNotFoundException | MusicPlaybackException e) {
                        System.err.println("좋아요 곡 로드 실패: " + music.getTitle() + " - " + e.getMessage());
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    public List<WAVAudioFile> getRecommendedSongs() {
        if (currentAudioFile == null || currentAudioFile.getGenre() == null ||
            currentAudioFile.getGenre().trim().isEmpty() ||
            currentAudioFile.getGenre().equals("알 수 없는 장르")) {
            return Collections.emptyList();
        }

        String targetGenre = currentAudioFile.getGenre();

        return allMusicList.stream()
                .filter(music -> !music.equals(currentAudioFile.getMusicData()))
                .filter(music -> !music.isLiked()) 
                .filter(music -> targetGenre.equals(music.getGenre()))
                .map(music -> {
                    try {
                        WAVAudioFile recommendedWav = new WAVAudioFile(music);
                        recommendedWav.setVolume(masterVolume); 
                        return recommendedWav;
                    } catch (MusicFileNotFoundException | MusicPlaybackException e) {
                        System.err.println("추천 곡 로드 실패: " + music.getTitle() + " - " + e.getMessage());
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    public boolean isPlaying() { return isPlaying; }
    public boolean isPaused() { return isPaused; }
    public boolean isShuffleMode() { return shuffleMode; }
    public boolean isRepeatMode() { return repeatMode; }

    public void closeAllAudioFiles() {
        stopPlaybackAndClearInternalLists();
    }

    /*
     * LineListener 인터페이스 구현: 오디오 라인 이벤트를 처리.
     */
    
    @Override
    public void update(LineEvent event) {
        Clip eventClip = (Clip)event.getSource(); 
        if (currentAudioFile == null || currentAudioFile.getClip() != eventClip) {
            return; 
        }

        if (event.getType() == LineEvent.Type.STOP) {
            if (isPlaying && !isPaused && !currentAudioFile.isPlaying() && 
                currentAudioFile.getCurrentPosition() >= (currentAudioFile.getDuration() - 1000)) { 
                
                this.isPlaying = false; 
                this.isPaused = false;
                try {
                    next(); 
                } catch (MusicPlaybackException e) {
                    System.err.println("자동 다음 곡 재생 중 오류: " + e.getMessage());
                    gui.displayErrorMessage("자동 재생 오류", "다음 곡을 재생할 수 없습니다: " + e.getMessage());
                    gui.updatePlayerUI();
                }
            } else {
                gui.updatePlayerUI();
            }
        } else if (event.getType() == LineEvent.Type.START) {
            if (!isPlaying) { 
                isPlaying = true;
                isPaused = false;
                gui.updatePlayerUI(); 
            }
        } else if (event.getType() == LineEvent.Type.OPEN) {
            if (currentAudioFile != null && currentAudioFile.getClip() == eventClip) {
                currentAudioFile.setVolume(masterVolume);
            }
        }
    }
}