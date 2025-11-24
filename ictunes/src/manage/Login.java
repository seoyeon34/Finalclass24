package manage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Login extends JFrame {

    JTextField idField;
    JTextField pwField;

    public Login() {
        //GUI 설정
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridLayout grid = new GridLayout(6, 2);
        grid.setVgap(5);

        Container c = getContentPane();
        c.setLayout(grid);

        //아이디 비번 입력 필드, 라벨 추가
        c.add(new JLabel("아이디"));
        idField = new JTextField("");
        c.add(idField);

        c.add(new JLabel("패스워드"));
        pwField = new JTextField("");
        c.add(pwField);

        //로그인 버튼 추가
        JButton loginBtn = new JButton("로그인");
        c.add(loginBtn);
        c.add(new JLabel(""));

        //회원가입 버튼 추가
        JButton signUpBtn = new JButton("회원가입");
        c.add(signUpBtn);
        c.add(new JLabel(""));

        //비번 찾기 버튼 추가
        JButton forgotBtn = new JButton("비밀번호 찾기");
        c.add(forgotBtn);
        c.add(new JLabel(""));

        //Listener 연결
        loginBtn.addActionListener(new MyActionListener());
        signUpBtn.addActionListener(new MyActionListener());
        forgotBtn.addActionListener(new MyActionListener());
        
        setSize(300, 250);
        setVisible(true);
    }

    private class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton b = (JButton) e.getSource();

            //로그인 버튼 눌렀을 때
            if (b.getText().equals("로그인")) {
                String inputID = idField.getText();
                String inputPW = pwField.getText();

                //회원가입 했는지 확인
                if (SignUp.ID.equals("") || SignUp.PW.equals("")) {
                    JOptionPane.showMessageDialog(Login.this, "회원가입을 먼저 해주세요.");
                //로그인 정보랑 회원가입 정보 비교 
                } else if (inputID.equals(SignUp.ID) && inputPW.equals(SignUp.PW)) {
                    JOptionPane.showMessageDialog(Login.this, "로그인이 완료되었습니다. ");
                    
                    new Subscribe();
                } else {
                    JOptionPane.showMessageDialog(Login.this, "아이디 또는 비밀번호가 틀렸습니다. 다시 입력해주세요.");
                }
            }

            //회원가입 누르면 회원가입 창 띄우기
            if (b.getText().equals("회원가입")) {
                new SignUp();
            }
            //비번 찾기 누르면 비번찾기 창 띄우기
            if (b.getText().equals("비밀번호 찾기")) {
                new ForgotPassword();
            }
        }
    }
    
    public static void main(String[] args) {
        new Login();
    }
}
