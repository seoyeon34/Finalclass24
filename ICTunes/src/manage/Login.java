package manage;

import gui.MusicPlayerGUI; 
import main.ApplicationMain;
import model.User; 

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.*;
import java.util.Optional; 

public class Login extends JFrame {

    JTextField idField;
    JPasswordField pwField;
    private UserManager userManager; 

    public Login() {
        this.userManager = new UserManager(); 

        setTitle("Login");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 

        GridBagLayout layout = new GridBagLayout();
        Container c = getContentPane();
        c.setLayout(layout);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10); 

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
        gbc.fill = GridBagConstraints.HORIZONTAL; 
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

        // Listener 연결
        loginBtn.addActionListener(new MyActionListener()); 
        
        // 회원가입 버튼 리스너
        signUpBtn.addActionListener(e -> { 
            this.setVisible(false);
            SignUp signUpDialog = new SignUp(this, userManager);
            signUpDialog.showDialog();
        });
        
        // 비밀번호 찾기 버튼 리스너
        forgotBtn.addActionListener(e -> { 
            this.setVisible(false);
            ForgotPassword forgotPasswordDialog = new ForgotPassword(this, userManager);
            forgotPasswordDialog.showDialog();
        });
        
        setSize(450, 350); 
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton b = (JButton) e.getSource();

            // 로그인 버튼 눌렀을 때만 처리
            if (b.getText().equals("로그인")) {
                String inputID = idField.getText();
                String inputPW = new String(pwField.getPassword());

                // UserManager를 사용하여 로그인 검증
                if (userManager.validateLogin(inputID, inputPW)) { 
                    Optional<User> loggedInUser = userManager.findUserById(inputID);
                    if (loggedInUser.isPresent()) {
                        JOptionPane.showMessageDialog(Login.this, "로그인 성공! " + loggedInUser.get().getName() + "님 환영합니다.", "로그인 성공", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        new MusicPlayerGUI();
                    } else {
                        JOptionPane.showMessageDialog(Login.this, "알 수 없는 오류가 발생했습니다. 사용자 정보를 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(Login.this, "아이디 또는 비밀번호가 틀렸습니다. 다시 입력해주세요.", "로그인 실패", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }
}