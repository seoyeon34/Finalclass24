package manage;

import main.ApplicationMain;
import model.User;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SignUp extends JFrame {

    private JTextField nameField;
    private JTextField phoneField;
    private JTextField idField;
    private JPasswordField pwField;

    public SignUp() {

        setTitle("Sign Up");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        Container c = getContentPane();
        c.setLayout(layout);

        gbc.insets = new Insets(10, 10, 10, 10);

        Dimension fieldSize = new Dimension(180, 28);


        // 한글 입력 차단 기능 추가함
        KeyListener blockKorean = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char ch = e.getKeyChar();
                if (Character.toString(ch).matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")) {
                    e.consume();
                    JOptionPane.showMessageDialog(null,
                        "아이디/비밀번호에는 한글을 입력할 수 없습니다.",
                        "입력 오류",
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        };


        // 이름 라벨
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        c.add(new JLabel("이름"), gbc);

        // 이름 입력 필드
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        nameField = new JTextField("");
        nameField.setPreferredSize(fieldSize);
        c.add(nameField, gbc);

        // 전화번호 라벨
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        c.add(new JLabel("전화번호"), gbc);

        // 전화번호 입력 필드
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        phoneField = new JTextField("");
        phoneField.setPreferredSize(fieldSize);
        c.add(phoneField, gbc);

        // 아이디 라벨
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        c.add(new JLabel("아이디"), gbc);

        // 아이디 입력 필드
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        idField = new JTextField("");
        idField.setPreferredSize(fieldSize);
        c.add(idField, gbc);

        // 아이디에 한글 금지
        idField.addKeyListener(blockKorean);

        // 패스워드 라벨
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        c.add(new JLabel("패스워드"), gbc);

        // 패스워드 입력 필드
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        pwField = new JPasswordField("");
        pwField.setPreferredSize(fieldSize);
        c.add(pwField, gbc);

        // 비밀번호에 한글 금지
        pwField.addKeyListener(blockKorean);


        // 회원가입 완료 버튼
        JButton completeBtn = new JButton("회원가입 완료");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        c.add(completeBtn, gbc);

        // 로그인 버튼
        JButton loginBtn = new JButton("로그인");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        c.add(loginBtn, gbc);


        completeBtn.addActionListener(new MyActionListener());
        loginBtn.addActionListener(new MyActionListener());

        setSize(450, 350);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    
    private class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            JButton b = (JButton) e.getSource();

            if (b.getText().equals("회원가입 완료")) {

                String id = idField.getText().trim();
                String pw = new String(pwField.getPassword());
                String phone = phoneField.getText().trim();
                String name = nameField.getText().trim();

          
                if (id.isEmpty() || pw.isEmpty() || phone.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(SignUp.this,
                        "모든 필드를 입력하세요.",
                        "회원가입 오류",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 비밀번호 8글자 이상 입력해야하는 기능 추가
                if (pw.length() < 8) {
                    JOptionPane.showMessageDialog(SignUp.this,
                        "비밀번호는 최소 8자 이상 입력해야 합니다.",
                        "비밀번호 오류",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!phone.matches("\\d+")) {
                    JOptionPane.showMessageDialog(SignUp.this,
                        "전화번호는 숫자만 입력해주세요.",
                        "회원가입 오류",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

               
                String filePath = ApplicationMain.BASE_RESOURCE_PATH + "users.txt";

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                    writer.write(id + "," + pw + "," + phone + "," + name);
                    writer.flush();
                    JOptionPane.showMessageDialog(SignUp.this,
                        "회원가입이 완료되었습니다!",
                        "성공",
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(SignUp.this,
                        "저장 중 오류 발생: " + ex.getMessage(),
                        "오류",
                        JOptionPane.ERROR_MESSAGE);
                }
            }


            if (b.getText().equals("로그인")) {
                new Login();
                dispose();
            }
        }
    }
}
