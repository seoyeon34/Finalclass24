package manage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Subscribe extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private int price = 0;
    private String type = "";

    private JLabel priceLabel;
    private JLabel titleLabel;
    
    public Subscribe() {
        setTitle("정기구독");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createSelectPanel(), "SELECT");
        mainPanel.add(createDetailPanel(), "DETAIL");

        add(mainPanel);

        setSize(320, 300);
        setVisible(true);
    }

    private JPanel createSelectPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 5, 5));

        JLabel title = new JLabel("정기구독 서비스", SwingConstants.CENTER);
        panel.add(title);

        JButton monthBtn = new JButton("월간 구독");
        JButton yearBtn = new JButton("연간 구독");
        JButton noSubscribeBtn = new JButton("구독 안 함");
        JButton closeBtn = new JButton("닫기");

        panel.add(monthBtn);
        panel.add(yearBtn);
        panel.add(noSubscribeBtn);
        panel.add(closeBtn);

        monthBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                type = "월간";
                price = 9900;
                updateDetailPanel();
                cardLayout.show(mainPanel, "DETAIL");
            }
        });

        yearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                type = "연간";
                price = 99000;
                updateDetailPanel();
                cardLayout.show(mainPanel, "DETAIL");
            }
        });
        
        noSubscribeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });

        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        return panel;
    }

    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 5, 5));

        titleLabel = new JLabel("", SwingConstants.CENTER);
        panel.add(titleLabel);

        panel.add(new JLabel("(구독 기능 나열)"));

        priceLabel = new JLabel("", SwingConstants.CENTER);
        panel.add(priceLabel);

        JButton payBtn = new JButton("결제하기");
        panel.add(payBtn);

        payBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(
                    Subscribe.this,
                    price + "원을 결제하시겠습니까?",
                    "결제 확인",
                    JOptionPane.YES_NO_OPTION
                );

                if (result == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(Subscribe.this, "결제 완료");
                }
            }
        });

        return panel;
    }

    private void updateDetailPanel() {
        titleLabel.setText(type + " 정기구독");
        priceLabel.setText("가격: " + price + "원");
    }
}