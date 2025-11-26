package manage;

import main.ApplicationMain;
import model.User;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ForgotPassword extends JFrame {

    JTextField phoneField;

    public ForgotPassword() {
        setTitle("ICTunes 비밀번호 찾기");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(350, 200);
        setLocationRelativeTo(null);

        GridLayout grid = new GridLayout(3, 2, 10, 10);
        Container c = getContentPane();
        c.setLayout(grid);
        ((JPanel)c).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        c.add(new JLabel("전화번호 입력:"));
        phoneField = new JTextField(20);
        c.add(phoneField);

        JButton findBtn = new JButton("비밀번호 찾기");
        c.add(findBtn);
        c.add(new JLabel(""));

        findBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputPhone = phoneField.getText().trim();

                if (inputPhone.isEmpty()) {
                    JOptionPane.showMessageDialog(ForgotPassword.this, "전화번호를 입력하세요.", "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (!inputPhone.matches("\\d+")) {
                    JOptionPane.showMessageDialog(ForgotPassword.this, "전화번호는 숫자만 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                User registeredUser = getUserFromFile();

                if (registeredUser != null && inputPhone.equals(registeredUser.getPhone())) {
                    // 사용자 아이디와 이름을 함께 표시
                    JOptionPane.showMessageDialog(ForgotPassword.this,
                        "아이디: " + registeredUser.getId() + "\n비밀번호: " + registeredUser.getPassword() + "\n이름: " + registeredUser.getName(),
                        "비밀번호 찾기 성공", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(ForgotPassword.this, "입력하신 전화번호로 등록된 정보를 찾을 수 없습니다.", "정보 불일치", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // 파일에서 사용자 정보 읽어오는 메서드
    private User getUserFromFile() {
        String filePath = ApplicationMain.BASE_RESOURCE_PATH + "users.txt"; 
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            if ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                // 파일 저장 포맷이 ID,PW,PHONE,NAME 이므로 4개 필드
                if (parts.length >= 4) { 
                    
                    return new User(parts[0], parts[1], parts[2], parts[3]);
                }
            }
        } catch (IOException e) {
            System.err.println("사용자 정보 파일을 읽는 중 오류 발생: " + e.getMessage());
        }
        return null; // 사용자 정보가 없거나 오류 발생 시
    }
}