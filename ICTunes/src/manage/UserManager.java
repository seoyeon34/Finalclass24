package manage;

import model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import main.ApplicationMain;

/*
 * 사용자 정보를 관리하는 클래스.
 * 파일(users.txt)에서 사용자를 로드하고 저장
 */
public class UserManager {
    private List<User> userDatabase;
    private final String userFilePath = ApplicationMain.BASE_RESOURCE_PATH + "users.txt";

    public UserManager() {
        userDatabase = new ArrayList<>();
        loadUsersFromFile();
    }

    // 파일에서 사용자 정보 로드
    private void loadUsersFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(userFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) { 
                    userDatabase.add(new User(parts[0], parts[1], parts[2], parts[3], parts[4]));
                } else if (parts.length >= 4) {
                    userDatabase.add(new User(parts[0], parts[1], parts[2], parts[3], null));
                }
            }
        } catch (IOException e) {
            System.err.println("사용자 정보 파일을 읽는 중 오류 발생 또는 파일 없음: " + e.getMessage());
            try {
                File file = new File(userFilePath);
                if (!file.exists()) {
                    file.createNewFile();
                }
            } catch (IOException ex) {
                System.err.println("사용자 정보 파일 생성 실패: " + ex.getMessage());
            }
        }
    }

    
    private void saveUsersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFilePath))) {
            for (User user : userDatabase) {
                writer.write(String.join(",",
                        user.getUserId(),
                        user.getPassword(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhoneNumber() != null ? user.getPhoneNumber() : ""));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("사용자 정보 파일을 저장하는 중 오류 발생: " + e.getMessage());
        }
    }

    /*
     * 사용자 아이디로 사용자를 찾는다.
     */
    public Optional<User> findUserById(String userId) {
        return userDatabase.stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst();
    }

    /*
     * 특정 사용자의 비밀번호를 재설정.
     */
    public boolean resetPassword(String userId, String newPassword) {
        Optional<User> userOptional = findUserById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(newPassword);
            saveUsersToFile();
            System.out.println("사용자 " + userId + "의 비밀번호가 재설정되었습니다.");
            return true;
        }
        return false;
    }

    public boolean registerUser(User newUser) {
        if (findUserById(newUser.getUserId()).isPresent()) {
            return false;
        }
        userDatabase.add(newUser);
        saveUsersToFile();
        return true;
    }

    public boolean validateLogin(String userId, String password) {
        Optional<User> userOptional = findUserById(userId);
        return userOptional.map(user -> user.getPassword().equals(password)).orElse(false);
    }
}