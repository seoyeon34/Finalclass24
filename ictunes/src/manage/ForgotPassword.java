package manage;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ForgotPassword extends JFrame {

    JTextField phoneField;

    public ForgotPassword() {
        setTitle("비밀번호 찾기");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GridLayout grid = new GridLayout(3, 2);
        grid.setVgap(5);

        Container c = getContentPane();
        c.setLayout(grid);

        c.add(new JLabel("전화번호 입력"));
        phoneField = new JTextField("");
        c.add(phoneField);

        JButton findBtn = new JButton("비밀번호 찾기");
        c.add(findBtn);
        c.add(new JLabel(""));

        findBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputPhone = phoneField.getText();

                if (inputPhone.equals("")) {
                    JOptionPane.showMessageDialog(ForgotPassword.this, "전화번호를 입력하세요.");
                    return;
                }

                if (inputPhone.equals(SignUp.PHONE)) {
                    JOptionPane.showMessageDialog(ForgotPassword.this,
                        "아이디: " + SignUp.ID + "\n비밀번호: " + SignUp.PW);
                } else {
                    JOptionPane.showMessageDialog(ForgotPassword.this, "일치하는 정보가 없습니다.");
                }
            }
        });

        setSize(300, 150);
        setVisible(true);
    }
}
