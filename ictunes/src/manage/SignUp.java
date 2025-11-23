package manage;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SignUp extends JFrame {

    public static String ID = "";
    public static String PW = "";
    public static String PHONE = "";

    JTextField nameField;
    JTextField phoneField;
    JTextField idField;
    JTextField pwField;

    public SignUp() {
        setTitle("Sign Up");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridLayout grid = new GridLayout(6, 2);
        grid.setVgap(5);

        Container c = getContentPane();
        c.setLayout(grid);

        c.add(new JLabel("이름"));
        nameField = new JTextField("");
        c.add(nameField);

        c.add(new JLabel("전화번호"));
        phoneField = new JTextField("");
        c.add(phoneField);

        c.add(new JLabel("아이디"));
        idField = new JTextField("");
        c.add(idField);

        c.add(new JLabel("패스워드"));
        pwField = new JTextField("");
        c.add(pwField);

        JButton completeBtn = new JButton("회원가입 완료");
        c.add(completeBtn);
        c.add(new JLabel(""));

        JButton loginBtn = new JButton("로그인");
        c.add(loginBtn);
        c.add(new JLabel(""));

        completeBtn.addActionListener(new MyActionListener());
        loginBtn.addActionListener(new MyActionListener());

        setSize(300, 250);
        setVisible(true);
    }

    private class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton b = (JButton) e.getSource();

            if (b.getText().equals("회원가입 완료")) {
                ID = idField.getText();
                PW = pwField.getText();
                PHONE = phoneField.getText();

                if (ID.equals("") || PW.equals("") || PHONE.equals("")) {
                    JOptionPane.showMessageDialog(SignUp.this, "정보를 모두 입력하세요.");
                    return;
                }

                JOptionPane.showMessageDialog(SignUp.this, "회원가입이 완료되었습니다.");
            }

            if (b.getText().equals("로그인")) {
                new Login();
            }
        }
    }

    public static void main(String[] args) {
        new SignUp();
    }
}
