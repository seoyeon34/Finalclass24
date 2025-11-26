package manage;

import gui.MusicPlayerGUI;
import main.ApplicationMain;
import model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.awt.*;


public class Login extends JFrame {

    JTextField idField;
    JPasswordField pwField;

    public Login() {
        
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridLayout grid = new GridLayout(6, 2);
        grid.setVgap(5);

        Container c = getContentPane();
        c.setLayout(grid);

        // 아이디 비번 입력 필드, 라벨 추가
        c.add(new JLabel("아이디"));
        idField = new JTextField("");
        c.add(idField);

        c.add(new JLabel("패스워드"));
        pwField = new JPasswordField("");
        c.add(pwField);

        // 로그인 버튼 추가
        JButton loginBtn = new JButton("로그인");
        c.add(loginBtn);
        c.add(new JLabel(""));

        // 회원가입 버튼 추가
        JButton signUpBtn = new JButton("회원가입");
        c.add(signUpBtn);
        c.add(new JLabel(""));

        // 비번 찾기 버튼 추가
        JButton forgotBtn = new JButton("비밀번호 찾기");
        c.add(forgotBtn);
        c.add(new JLabel(""));

        // Listener 연결
        loginBtn.addActionListener(new MyActionListener());
        signUpBtn.addActionListener(new MyActionListener());
        forgotBtn.addActionListener(new MyActionListener());
        
        setSize(300, 250);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton b = (JButton) e.getSource();

            // 로그인 버튼 눌렀을 때
            if (b.getText().equals("로그인")) {
                String inputID = idField.getText();
                String inputPW = new String(pwField.getPassword()); // JPasswordField에서 비밀번호 가져오기

                // users.txt 파일에서 사용자 정보 읽기
                User registeredUser = getUserFromFile(); 

                if (registeredUser == null) {
                    JOptionPane.showMessageDialog(Login.this, "등록된 회원이 없습니다. 회원가입을 먼저 해주세요.", "로그인 오류", JOptionPane.WARNING_MESSAGE);
                } else if (inputID.equals(registeredUser.getId()) && inputPW.equals(registeredUser.getPassword())) {
                    // 로그인 성공 시 환영 메시지에 사용자 '이름' 표시
                    JOptionPane.showMessageDialog(Login.this, "로그인 성공! " + registeredUser.getName() + "님 환영합니다.", "로그인 성공", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // 로그인 창 닫기

                    new MusicPlayerGUI();
                } else {
                    JOptionPane.showMessageDialog(Login.this, "아이디 또는 비밀번호가 틀렸습니다. 다시 입력해주세요.", "로그인 실패", JOptionPane.WARNING_MESSAGE);
                }
            }

            // 회원가입 누르면 회원가입 창 띄우기
            if (b.getText().equals("회원가입")) {
                new SignUp();
            }
            // 비번 찾기 누르면 비번찾기 창 띄우기
            if (b.getText().equals("비밀번호 찾기")) {
                new ForgotPassword();
            }
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
}