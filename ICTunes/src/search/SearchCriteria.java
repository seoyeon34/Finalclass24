package search;

/*
 * 음악 검색 기준을 정의하는 Enum 클래스.
 * GUI의 콤보박스나 검색 로직에서 사용할 검색 타입을 명확하게 지정함.
 */

public enum SearchCriteria {
    TITLE("제목"),
    ARTIST("가수"),
    GENRE("장르"),
    RELEASE_YEAR("발매년도"),
    ALL("통합");

    private final String displayName;

    SearchCriteria(String displayName) {
        this.displayName = displayName;
    }

    /*
     * Enum 상수에 해당하는 표시 이름을 반환.
     * return 표시 이름
     */
    
    public String getDisplayName() {
        return displayName;
    }

    /*
     * 표시 이름으로 SearchCriteria Enum을 찾아 반환.
     * param displayName 찾을 표시 이름
     * return 해당 SearchCriteria Enum, 없으면 null
     */
    
    public static SearchCriteria fromDisplayName(String displayName) {
        for (SearchCriteria criteria : SearchCriteria.values()) {
            if (criteria.getDisplayName().equals(displayName)) {
                return criteria;
            }
        }
        return null;
    }
}