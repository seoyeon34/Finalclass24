package playlist;

import model.Music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/*
 * 음악 추천 기능을 관리하는 클래스.
 * 현재 재생 중인 음악을 기반으로 유사한 음악을 추천.
 */

public class RecommendationManager {

    private static final int DEFAULT_RECOMMENDATION_COUNT = 2;

    public List<Music> getRecommendedSongs(Music currentMusic, List<Music> allMusicList) {
        if (currentMusic == null || allMusicList == null || allMusicList.isEmpty()) {
            return Collections.emptyList();
        }

        String currentGenre = currentMusic.getGenre();
        String currentTitle = currentMusic.getTitle();

        List<Music> recommendations = new ArrayList<>();
        int count = 0;

        for (Music m : allMusicList) {
            if (m.getGenre().equals(currentGenre) && !m.getTitle().equals(currentTitle)) {
                recommendations.add(m);
                count++;
                if (count >= DEFAULT_RECOMMENDATION_COUNT) {
                    break;
                }
            }
        }
        
        System.out.println("추천 목록 생성 완료. 현재 곡(" + currentMusic.getTitle() + ")과 같은 장르 " + currentGenre + "의 곡 " + recommendations.size() + "개 추천.");
        return recommendations;
    }

    /*
     * 특정 장르의 음악 목록만 필터링하여 반환하는 유틸리티 메서드.
     * param genre 필터링할 장르
     * param musicList 전체 음악 목록
     * return 해당 장르의 Music 객체 리스트
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