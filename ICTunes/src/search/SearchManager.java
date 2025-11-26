package search;

import model.Music; // Music 모델 클래스 import (패키지명 변경 반영)
import java.util.List;
import java.util.stream.Collectors; // Java 8 Stream API를 사용하여 간결한 코드 작성

/**
 * 음악 데이터에서 다양한 조건으로 음악을 검색하는 기능을 제공하는 클래스입니다.
 * 스트림 API를 활용하여 검색 로직을 구현합니다.
 */
public class SearchManager {

    /**
     * 음악의 제목을 기준으로 검색합니다. 부분 일치 검색을 지원하며, 대소문자를 구분하지 않습니다.
     * @param keyword 검색할 제목 키워드
     * @param musicList 검색 대상이 되는 전체 음악 리스트
     * @return 검색 키워드를 포함하는 제목의 Music 객체 리스트
     */
    public List<Music> searchByTitle(String keyword, List<Music> musicList) {
        String lowerCaseKeyword = keyword.toLowerCase();
        return musicList.stream()
                        .filter(music -> music.getTitle().toLowerCase().contains(lowerCaseKeyword))
                        .collect(Collectors.toList());
    }

    /**
     * 가수의 이름을 기준으로 검색합니다. 부분 일치 검색을 지원하며, 대소문자를 구분하지 않습니다.
     * @param keyword 검색할 가수 키워드
     * @param musicList 검색 대상이 되는 전체 음악 리스트
     * @return 검색 키워드를 포함하는 가수의 Music 객체 리스트
     */
    public List<Music> searchByArtist(String keyword, List<Music> musicList) {
        String lowerCaseKeyword = keyword.toLowerCase();
        return musicList.stream()
                        .filter(music -> music.getArtist().toLowerCase().contains(lowerCaseKeyword))
                        .collect(Collectors.toList());
    }

    /**
     * 장르(카테고리)를 기준으로 검색합니다. 부분 일치 검색을 지원하며, 대소문자를 구분하지 않습니다.
     * @param keyword 검색할 장르 키워드
     * @param musicList 검색 대상이 되는 전체 음악 리스트
     * @return 검색 키워드를 포함하는 장르의 Music 객체 리스트
     */
    public List<Music> searchByGenre(String keyword, List<Music> musicList) {
        String lowerCaseKeyword = keyword.toLowerCase();
        return musicList.stream()
                        .filter(music -> music.getGenre().toLowerCase().contains(lowerCaseKeyword))
                        .collect(Collectors.toList());
    }

    /**
     * 발매년도를 기준으로 검색합니다. 정확히 일치하는 연도만 검색합니다.
     * @param year 검색할 발매년도
     * @param musicList 검색 대상이 되는 전체 음악 리스트
     * @return 지정된 발매년도와 일치하는 Music 객체 리스트
     */
    public List<Music> searchByReleaseYear(int year, List<Music> musicList) {
        return musicList.stream()
                        .filter(music -> music.getReleaseYear() == year)
                        .collect(Collectors.toList());
    }

    /**
     * 제목, 가수, 장르 세 가지 필드에서 동시에 키워드를 검색합니다. 부분 일치 검색을 지원하며, 대소문자를 구분하지 않습니다.
     * @param keyword 검색할 키워드
     * @param musicList 검색 대상이 되는 전체 음악 리스트
     * @return 키워드를 포함하는 Music 객체 리스트 (제목, 가수, 장르 중 하나라도 포함하는 경우)
     */
    public List<Music> searchAll(String keyword, List<Music> musicList) {
        String lowerCaseKeyword = keyword.toLowerCase();
        return musicList.stream()
                        .filter(music -> music.getTitle().toLowerCase().contains(lowerCaseKeyword) ||
                                         music.getArtist().toLowerCase().contains(lowerCaseKeyword) ||
                                         music.getGenre().toLowerCase().contains(lowerCaseKeyword))
                        .collect(Collectors.toList());
    }
}