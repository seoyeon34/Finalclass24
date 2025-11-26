package search;

/**
 * 음악 검색 기준을 정의하는 Enum 클래스입니다.
 * GUI의 콤보박스나 검색 로직에서 사용할 검색 타입을 명확하게 지정합니다.
 */
public enum SearchCriteria {
    TITLE("제목"),
    ARTIST("가수"),
    GENRE("장르"),
    RELEASE_YEAR("발매년도"),
    ALL("통합"); // 제목, 가수, 장르를 통합하여 검색

    private final String displayName; // GUI 등에 표시될 이름

    SearchCriteria(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Enum 상수에 해당하는 표시 이름을 반환합니다.
     * @return 표시 이름
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 표시 이름(displayName)으로 SearchCriteria Enum을 찾아 반환합니다.
     * @param displayName 찾을 표시 이름
     * @return 해당 SearchCriteria Enum, 없으면 null
     */
    public static SearchCriteria fromDisplayName(String displayName) {
        for (SearchCriteria criteria : SearchCriteria.values()) {
            if (criteria.getDisplayName().equals(displayName)) {
                return criteria;
            }
        }
        return null; // 해당 표시 이름에 맞는 기준을 찾을 수 없는 경우
    }
}