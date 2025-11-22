package manage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Login extends JFrame {

    JTextField idField;
    JTextField pwField;

    public Login() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridLayout grid = new GridLayout(6, 2);
        grid.setVgap(5);

        Container c = getContentPane();
        c.setLayout(grid);

        c.add(new JLabel("아이디"));
        idField = new JTextField("");
        c.add(idField);

        c.add(new JLabel("패스워드"));
        pwField = new JTextField("");
        c.add(pwField);

        JButton loginBtn = new JButton("로그인");
        c.add(loginBtn);
        c.add(new JLabel(""));

        JButton signUpBtn = new JButton("회원가입");
        c.add(signUpBtn);
        c.add(new JLabel(""));

        loginBtn.addActionListener(new MyActionListener());
        signUpBtn.addActionListener(new MyActionListener());

        setSize(300, 250);
        setVisible(true);
    }

    private class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton b = (JButton) e.getSource();

            if (b.getText().equals("로그인")) {
                String inputID = idField.getText();
                String inputPW = pwField.getText();

                if (SignUp.ID.equals("") || SignUp.PW.equals("")) {
                    JOptionPane.showMessageDialog(Login.this, "회원가입을 먼저 해주세요.");
                } else if (inputID.equals(SignUp.ID) && inputPW.equals(SignUp.PW)) {
                    JOptionPane.showMessageDialog(Login.this, "로그인이 완료되었습니다. ");
                } else {
                    JOptionPane.showMessageDialog(Login.this, "아이디 또는 비밀번호가 틀렸습니다. 다시 입력해주세요.");
                }
            }

            if (b.getText().equals("회원가입")) {
                new SignUp();
            }
        }
    }

    public static void main(String[] args) {
        new Login();
    }
}
