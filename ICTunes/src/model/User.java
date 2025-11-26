package model;

/**
 * 사용자 정보를 담는 모델 클래스.
 * 로그인 및 회원가입 시 사용자 데이터를 관리하는 데 사용.
 */

public class User {
    private String id;
    private String password;
    private String phone;
    private String name;

    
    public User(String id, String password, String phone, String name) {
        this.id = id;
        this.password = password;
        this.phone = phone;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return "User{" +
               "id='" + id + '\'' +
               ", password='" + password + '\'' +
               ", phone='" + phone + '\'' +
               ", name='" + name + '\'' +
               '}';
    }
}