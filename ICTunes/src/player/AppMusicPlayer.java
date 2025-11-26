package player;

import audio.WAVAudioFile;          // 재생 가능한 WAV 파일 객체
import audio.MusicPlaybackException; // 음악 재생 예외
import audio.MusicFileNotFoundException; // 음악 파일 없음 예외
import model.Music;                 // 음악 메타데이터 (WAVAudioFile 내부에서 사용)
import playlist.RecommendationManager; // 추천 음악 관리자
import gui.MusicPlayerGUI;          // GUI 업데이트를 위해 MusicPlayerGUI에 대한 참조 (옵션, 필요한 경우 사용)

import javax.sound.sampled.LineEvent;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * ICTunes 음악 스트리밍 애플리케이션의 핵심 재생 로직을 관리하는 클래스입니다.
 * 재생 목록 관리, 재생 제어(재생/일시정지/정지), 반복/셔플 모드, 좋아요 기능, 추천 기능 등을 통합합니다.
 */
public class AppMusicPlayer {

    private List<WAVAudioFile> playlist;         // 현재 재생 목록
    private int currentPlayingIndex = -1;        // 현재 재생 중인 곡의 인덱스
    private WAVAudioFile currentAudioFile;       // 현재 재생 중인 WAVAudioFile 객체

    private boolean isShuffleMode = false;       // 셔플 모드 여부
    private boolean isRepeatMode = false;        // 반복 모드 여부 (단일 곡 반복)
    private Random random;                       // 셔플을 위한 Random 객체

    private RecommendationManager recommendationManager; // 추천 관리자
    private MusicPlayerGUI gui; // GUI에 특정 이벤트를 통지하기 위한 참조 (선택 사항)

    public AppMusicPlayer(MusicPlayerGUI gui) {
        this.playlist = new ArrayList<>();
        this.random = new Random();
        this.recommendationManager = new RecommendationManager(); // 추천 관리자 초기화
        this.gui = gui; // GUI 참조 설정
    }

    /**
     * AppMusicPlayer를 초기화할 때 MusicPlayerGUI 참조 없이 초기화하는 경우 (필요에 따라)
     */
    public AppMusicPlayer() {
        this(null); // 다른 생성자 호출
    }

    /**
     * 재생 목록에 WAVAudioFile을 추가합니다.
     * @param audioFile 추가할 WAVAudioFile 객체
     */
    public void addSong(WAVAudioFile audioFile) {
        if (audioFile == null) return;
        // WAVAudioFile 내부의 playback.MusicPlayer에 리스너를 추가합니다.
        // 이 리스너는 곡이 끝났을 때 다음 곡으로 넘어가도록 지시합니다.
        audioFile.addPlaybackListener(event -> {
            if (event.getType() == LineEvent.Type.STOP && currentAudioFile != null && !currentAudioFile.isPaused()) {
                // 이 이벤트가 발생했으나 일시정지 상태가 아니라면 (즉, 재생이 자연스럽게 끝났다면)
                SwingUtilities.invokeLater(() -> {
                    try {
                        next(); // 다음 곡으로 자동으로 넘어갑니다.
                        if (gui != null) gui.updatePlayerUI(); // GUI 업데이트 요청
                    } catch (MusicPlaybackException e) {
                        System.err.println("다음 곡 자동 재생 중 오류 발생: " + e.getMessage());
                        // GUI에 오류 메시지를 표시할 수도 있습니다.
                        if (gui != null) gui.displayErrorMessage("자동 재생 오류", "다음 곡 재생 중 오류: " + e.getMessage());
                    }
                });
            }
        });
        playlist.add(audioFile);
        System.out.println("재생 목록에 추가: " + audioFile.getTitle());
    }
    
    /**
     * 주어진 Music 객체 리스트를 재생 목록으로 설정합니다. 기존 목록은 지워집니다.
     * @param musicList 설정할 Music 객체 리스트
     */
    public void setPlaylist(List<Music> musicList) throws MusicFileNotFoundException, MusicPlaybackException {
        closeAllAudioFiles(); // 기존 재생 목록 리소스 해제
        this.playlist.clear(); // 기존 목록 지우기
        for (Music m : musicList) {
            // WAVAudioFile 생성자에서 파일 유효성을 검사합니다.
            // "empty_file_path"와 같은 더미 경로인 경우, 실제 파일 재생은 시도하지 않습니다.
            // AppMusicPlayer는 실제 재생 가능한 WAVAudioFile만 관리해야 하므로, 임시 Music 객체는 여기에 추가하지 않습니다.
            // 다만, GUI JList에서 임시 메시지를 WAVAudioFile로 보여주기 위해 "empty_file_path" Music 객체 생성을 허용한 것이므로,
            // 이곳에서는 실제 파일 경로가 있는 Music 객체만 WAVAudioFile로 생성하여 추가하는 것이 좋습니다.
            if (m.getFilePath() != null && !m.getFilePath().trim().isEmpty() && !"empty_file_path".equals(m.getFilePath())) {
                 this.playlist.add(new WAVAudioFile(m)); // Music 객체를 WAVAudioFile로 변환하여 추가
            }
        }
        if (!playlist.isEmpty()) {
            currentPlayingIndex = 0; // 새 재생 목록의 첫 곡으로 인덱스 설정
        } else {
            currentPlayingIndex = -1;
        }
        currentAudioFile = null; // 현재 재생 중인 파일 초기화
        System.out.println("새 재생 목록 설정 완료. 총 " + playlist.size() + "곡.");
    }

    /**
     * 현재 인덱스의 음악을 재생합니다.
     * @throws MusicPlaybackException 재생 중 오류 발생 시
     */
    public void play() throws MusicPlaybackException {
        if (playlist.isEmpty()) {
            throw new MusicPlaybackException("재생 목록이 비어 있습니다.");
        }
        if (currentPlayingIndex == -1) { // 재생 목록이 있으나 아직 시작되지 않았을 때
            currentPlayingIndex = 0;
        }

        // 현재 곡이 이미 재생 중이라면 중복 재생 방지
        if (currentAudioFile != null && currentAudioFile.isPlaying()) {
            return;
        }

        // 현재 재생 중인 곡이 일시정지 상태였으면 이어서 재생
        if (currentAudioFile != null && currentAudioFile.isPaused()) {
            currentAudioFile.play();
            System.out.println("재개: " + currentAudioFile.getTitle());
            return;
        }
        
        // 새로 재생 시작 (다른 곡을 선택하거나 완전히 정지된 상태에서)
        play(currentPlayingIndex);
    }
    
    /**
     * 지정된 인덱스의 음악을 재생합니다.
     * @param index 재생할 음악의 재생 목록 내 인덱스
     * @throws MusicPlaybackException 재생 중 오류 발생 시
     */
    public void play(int index) throws MusicPlaybackException {
        if (playlist.isEmpty() || index < 0 || index >= playlist.size()) {
            throw new MusicPlaybackException("유효하지 않은 재생 인덱스입니다: " + index);
        }

        // 이전에 재생 중이던 곡이 있다면 중지하고 리소스 해제
        if (currentAudioFile != null) {
            currentAudioFile.stop();
        }

        currentPlayingIndex = index;
        currentAudioFile = playlist.get(currentPlayingIndex);
        
        // WAVAudioFile이 실제 파일 경로가 아닌 메시지를 담은 임시 객체일 경우 재생 시도하지 않음
        if ("empty_file_path".equals(currentAudioFile.getFilePath())) {
            throw new MusicPlaybackException("이 음악은 메시지이며 재생할 수 없습니다.");
        }

        currentAudioFile.play(); // 해당 WAVAudioFile 객체의 play 메서드 호출
        System.out.println("재생 시작: " + currentAudioFile.getTitle());
    }

    /**
     * 현재 재생 중인 음악을 일시정지합니다.
     * @throws MusicPlaybackException 일시정지 중 오류 발생 시
     */
    public void pause() throws MusicPlaybackException {
        if (currentAudioFile == null) {
            throw new MusicPlaybackException("현재 재생 중인 곡이 없습니다.");
        }
        if (currentAudioFile.isPlaying()) { // 재생 중일 때만 일시정지
            currentAudioFile.pause();
            System.out.println("일시정지: " + currentAudioFile.getTitle());
        }
    }

    /**
     * 현재 재생 중인 음악을 완전히 중지하고 초기화합니다.
     * @throws MusicPlaybackException 중지 중 오류 발생 시
     */
    public void stop() throws MusicPlaybackException {
        if (currentAudioFile == null) {
            System.out.println("현재 재생 중인 곡이 없어 중지할 것이 없습니다.");
            return;
        }
        currentAudioFile.stop();
        System.out.println("중지: " + currentAudioFile.getTitle());
    }

    /**
     * 다음 곡을 재생합니다. 셔플/반복 모드를 반영합니다.
     * @throws MusicPlaybackException 재생 중 오류 발생 시
     */
    public void next() throws MusicPlaybackException {
        if (playlist.isEmpty()) {
            throw new MusicPlaybackException("재생 목록이 비어 있습니다.");
        }

        // 단일 곡 반복 모드일 경우 현재 곡을 처음부터 다시 재생
        if (isRepeatMode) {
            if (currentAudioFile != null) {
                currentAudioFile.stop(); // 현재 곡 중지 후
                currentAudioFile.play(); // 다시 재생
                System.out.println("단일 곡 반복: " + currentAudioFile.getTitle());
            } else { // 현재 재생 중인 곡이 없으면 그냥 첫 곡 재생
                play(0);
            }
            return;
        }

        int nextIndex;
        if (isShuffleMode) {
            nextIndex = random.nextInt(playlist.size());
            // 현재 곡과 같은 곡이 나오지 않도록 할 수 있지만, 무작위성을 위해 허용합니다.
            // 필요하다면 `while (nextIndex == currentPlayingIndex)` 로직을 추가할 수 있습니다.
        } else {
            nextIndex = currentPlayingIndex + 1;
            if (nextIndex >= playlist.size()) { // 마지막 곡일 경우 처음으로 돌아갑니다.
                nextIndex = 0;
            }
        }
        play(nextIndex);
    }

    /**
     * 이전 곡을 재생합니다. 셔플/반복 모드를 반영합니다.
     * @throws MusicPlaybackException 재생 중 오류 발생 시
     */
    public void previous() throws MusicPlaybackException {
        if (playlist.isEmpty()) {
            throw new MusicPlaybackException("재생 목록이 비어 있습니다.");
        }

        // 단일 곡 반복 모드일 경우 현재 곡을 처음부터 다시 재생
        if (isRepeatMode) {
            if (currentAudioFile != null) {
                currentAudioFile.stop();
                currentAudioFile.play();
                System.out.println("단일 곡 반복: " + currentAudioFile.getTitle());
            } else {
                play(0);
            }
            return;
        }

        int prevIndex;
        if (isShuffleMode) {
            prevIndex = random.nextInt(playlist.size());
        } else {
            prevIndex = currentPlayingIndex - 1;
            if (prevIndex < 0) { // 첫 곡일 경우 마지막 곡으로 돌아갑니다.
                prevIndex = playlist.size() - 1;
            }
        }
        play(prevIndex);
    }

    /**
     * 셔플 모드를 토글합니다.
     */
    public void toggleShuffle() {
        isShuffleMode = !isShuffleMode;
        System.out.println("셔플 모드: " + (isShuffleMode ? "ON" : "OFF"));
    }

    /**
     * 반복 모드를 토글합니다.
     */
    public void toggleRepeat() {
        isRepeatMode = !isRepeatMode;
        System.out.println("반복 모드: " + (isRepeatMode ? "ON" : "OFF"));
    }
    
    /**
     * 특정 WAVAudioFile의 '좋아요' 상태를 토글합니다.
     * @param audioFile '좋아요' 상태를 변경할 WAVAudioFile 객체
     * @return 변경된 '좋아요' 상태 (true: 좋아요, false: 좋아요 취소)
     */
    public boolean toggleLike(WAVAudioFile audioFile) {
        if (audioFile == null) return false;
        audioFile.setLiked(!audioFile.isLiked());
        System.out.println("'" + audioFile.getTitle() + "' 좋아요 상태: " + (audioFile.isLiked() ? "ON" : "OFF"));
        return audioFile.isLiked();
    }

    /**
     * '좋아요'된 모든 곡들을 반환합니다.
     * @return '좋아요'된 WAVAudioFile 객체들의 리스트
     */
    public List<WAVAudioFile> getLikedSongs() {
        return playlist.stream()
                .filter(WAVAudioFile::isLiked) // isLiked()가 true인 곡만 필터링
                .collect(Collectors.toList());
    }

    /**
     * 현재 재생 중인 곡을 기준으로 추천 음악 목록을 가져옵니다.
     * @return 추천 WAVAudioFile 객체 리스트
     */
    public List<WAVAudioFile> getRecommendedSongs() {
        if (currentAudioFile == null) {
            return new ArrayList<>(); 
        }
        
        // <<<<<<<<<<<<<<<< musicData 필드 대신 getMusicData() 메서드 사용 >>>>>>>>>>>>>>>>>>
        Music currentMusic = currentAudioFile.getMusicData();
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

        List<Music> allMusicData = playlist.stream()
                                        // <<<<<<<<<<<<<<<< musicData 필드 대신 getMusicData() 메서드 사용 >>>>>>>>>>>>>>>>>>
                                        .map(WAVAudioFile::getMusicData)
                                        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                                        .collect(Collectors.toList());
        
        List<Music> recommendedMusicData = recommendationManager.getRecommendedSongs(currentMusic, allMusicData);
        
        List<WAVAudioFile> recommendedWAVAudioFiles = new ArrayList<>();
        for (Music recMusic : recommendedMusicData) {
            for (WAVAudioFile wavFile : playlist) {
                // Music 클래스의 equals/hashCode가 오버라이딩되어 있다면 더 정확합니다.
                // <<<<<<<<<<<<<<<< musicData 필드 대신 getMusicData() 메서드 사용 >>>>>>>>>>>>>>>>>>
                if (wavFile.getMusicData().equals(recMusic)) { 
                // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                    recommendedWAVAudioFiles.add(wavFile);
                    break;
                }
            }
        }
        System.out.println("추천 목록 생성 완료. 현재 곡(" + currentMusic.getTitle() + ")과 같은 장르 " + currentMusic.getGenre() + "의 곡 " + recommendedWAVAudioFiles.size() + "개 추천.");
        return recommendedWAVAudioFiles;
    }


    /**
     * 현재 재생 중인 WAVAudioFile 객체를 반환합니다.
     * @return 현재 재생 중인 WAVAudioFile, 없으면 null
     */
    public WAVAudioFile getCurrentAudioFile() {
        return currentAudioFile;
    }
    
    /**
     * 현재 재생 중인지 여부를 반환합니다.
     */
    public boolean isPlaying() {
        return currentAudioFile != null && currentAudioFile.isPlaying();
    }
    
    /**
     * 현재 일시정지 중인지 여부를 반환합니다.
     */
    public boolean isPaused() {
        return currentAudioFile != null && currentAudioFile.isPaused();
    }

    /**
     * 셔플 모드 활성화 여부를 반환합니다.
     */
    public boolean isShuffleMode() {
        return isShuffleMode;
    }

    /**
     * 반복 모드 활성화 여부를 반환합니다.
     */
    public boolean isRepeatMode() {
        return isRepeatMode;
    }

    /**
     * 현재 재생 목록을 반환합니다.
     * @return 재생 목록 (WAVAudioFile 리스트)
     */
    public List<WAVAudioFile> getPlaylist() {
        return Collections.unmodifiableList(playlist); // 외부에서 직접 수정 불가능하도록 unmodifiableList 반환
    }

    /**
     * 모든 WAVAudioFile 리소스를 해제합니다. 애플리케이션 종료 시 호출해야 합니다.
     */
    public void closeAllAudioFiles() {
        System.out.println("모든 오디오 파일 리소스 해제 시작.");
        if (currentAudioFile != null) {
            try {
                stop(); // 현재 재생 중인 파일 중지
            } catch (MusicPlaybackException e) {
                System.err.println("현재 곡 중지 중 오류: " + e.getMessage());
            }
        }
        for (WAVAudioFile audioFile : playlist) {
            // "empty_file_path"와 같은 임시 Music 객체는 MusicPlayer를 가지지 않으므로 close 호출을 건너뜁니다.
            if (!"empty_file_path".equals(audioFile.getFilePath())) {
                audioFile.close(); // 각 WAVAudioFile 리소스 해제
            }
        }
        playlist.clear();
        currentPlayingIndex = -1;
        currentAudioFile = null;
        System.out.println("모든 오디오 파일 리소스 해제 완료.");
    }
}