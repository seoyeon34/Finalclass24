package playlist;

import model.Music; // Music 모델 클래스 import

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 음악 추천 기능을 관리하는 클래스입니다.
 * 현재 재생 중인 음악을 기반으로 유사한 음악을 추천합니다.
 * (현재는 주로 장르 기반 추천을 구현합니다.)
 */
public class RecommendationManager {

    // 추천할 음악의 기본 개수 (기존 RecommendList 로직 반영)
    private static final int DEFAULT_RECOMMENDATION_COUNT = 2;

    /**
     * 현재 재생 중인 음악의 장르를 기반으로 추천 음악 목록을 반환합니다.
     * 현재 재생 중인 음악은 추천 목록에서 제외됩니다.
     *
     * @param currentMusic 현재 재생 중인 Music 객체
     * @param allMusicList 전체 음악 목록
     * @return 추천 Music 객체 리스트
     */
    public List<Music> getRecommendedSongs(Music currentMusic, List<Music> allMusicList) {
        if (currentMusic == null || allMusicList == null || allMusicList.isEmpty()) {
            return Collections.emptyList(); // 유효하지 않은 입력이면 빈 리스트 반환
        }

        String currentGenre = currentMusic.getGenre();
        String currentTitle = currentMusic.getTitle();

        List<Music> recommendations = new ArrayList<>();
        int count = 0;

        for (Music m : allMusicList) {
            // 현재 곡과 장르가 같고, 현재 곡 자체가 아니라면 추천 목록에 추가
            if (m.getGenre().equals(currentGenre) && !m.getTitle().equals(currentTitle)) {
                recommendations.add(m);
                count++;
                if (count >= DEFAULT_RECOMMENDATION_COUNT) { // 설정된 추천 개수만큼 찾으면 중단
                    break;
                }
            }
        }
        
        System.out.println("추천 목록 생성 완료. 현재 곡(" + currentMusic.getTitle() + ")과 같은 장르 " + currentGenre + "의 곡 " + recommendations.size() + "개 추천.");
        return recommendations;
    }

    /**
     * (옵션) 특정 장르의 음악 목록만 필터링하여 반환하는 유틸리티 메서드입니다.
     * @param genre 필터링할 장르
     * @param musicList 전체 음악 목록
     * @return 해당 장르의 Music 객체 리스트
     */
    public List<Music> filterByGenre(String genre, List<Music> musicList) {
        if (genre == null || musicList == null || musicList.isEmpty()) {
            return Collections.emptyList();
        }
        return musicList.stream()
                        .filter(music -> music.getGenre().equalsIgnoreCase(genre))
                        .collect(Collectors.toList());
    }
}