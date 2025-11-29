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

       GridBagLayout layout = new GridBagLayout();
       GridBagConstraints gbc = new GridBagConstraints();

        Container c = getContentPane();
        c.setLayout(layout);
        
        gbc.insets = new Insets(10,10,10,10); 
        
        // 아이디, 패스워드 각각 라벨이랑 입력 필드 분리해 작성함
        
        // 아이디 라벨 
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        c.add(new JLabel("아이디"), gbc);
        
        // 아이디 입력 필드
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        idField = new JTextField("");
        idField.setPreferredSize(new Dimension(180, 28)); 
        c.add(idField, gbc);
        
        
        // 패스워드 라벨
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        c.add(new JLabel("패스워드"), gbc);
        
        // 패스워드 입력 필드
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        pwField = new JPasswordField("");
        pwField.setPreferredSize(new Dimension(180, 28)); 
        c.add(pwField, gbc);

        // 로그인 버튼 추가
        JButton loginBtn = new JButton("로그인");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; 
        gbc.anchor = GridBagConstraints.CENTER;
        c.add(loginBtn, gbc);

        // 회원가입 버튼 추가
        JButton signUpBtn = new JButton("회원가입");
        gbc.gridy = 3;
        c.add(signUpBtn, gbc);

        // 비번 찾기 버튼 추가
        JButton forgotBtn = new JButton("비밀번호 찾기");
        gbc.gridy = 4;
        c.add(forgotBtn, gbc);

        
        loginBtn.addActionListener(new MyActionListener());
        signUpBtn.addActionListener(new MyActionListener());
        forgotBtn.addActionListener(new MyActionListener());
        
        setSize(450, 350);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton b = (JButton) e.getSource();

            
            if (b.getText().equals("로그인")) {
                String inputID = idField.getText();
                String inputPW = new String(pwField.getPassword()); 

               
                User registeredUser = getUserFromFile(); 

                if (registeredUser == null) {
                    JOptionPane.showMessageDialog(Login.this, "등록된 회원이 없습니다. 회원가입을 먼저 해주세요.", "로그인 오류", JOptionPane.WARNING_MESSAGE);
                } else if (inputID.equals(registeredUser.getId()) && inputPW.equals(registeredUser.getPassword())) {
                    JOptionPane.showMessageDialog(Login.this, "로그인 성공! " + registeredUser.getName() + "님 환영합니다.", "로그인 성공", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); 
                    new MusicPlayerGUI();
                } else {
                    JOptionPane.showMessageDialog(Login.this, "아이디 또는 비밀번호가 틀렸습니다. 다시 입력해주세요.", "로그인 실패", JOptionPane.WARNING_MESSAGE);
                }
            }

            if (b.getText().equals("회원가입")) {
                new SignUp();
            }
            if (b.getText().equals("비밀번호 찾기")) {
                new ForgotPassword();
            }
        }

        
        private User getUserFromFile() {
            String filePath = ApplicationMain.BASE_RESOURCE_PATH + "users.txt"; 
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                if ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) { 
                       
                        return new User(parts[0], parts[1], parts[2], parts[3]);
                    }
                }
            } catch (IOException e) {
                System.err.println("사용자 정보 파일을 읽는 중 오류 발생: " + e.getMessage());
            }
            return null; 
        }
    }
}
