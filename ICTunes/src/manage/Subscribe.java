//위 정기구독 시스템은 코드상으로 구현은 해두었지만, 결국 사용하지 않았다.

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
    	
        setTitle("ICTunes 정기구독");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);


        mainPanel.add(createSelectPanel(), "SELECT");
        mainPanel.add(createDetailPanel(), "DETAIL");

        add(mainPanel);


        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    
    private JPanel createSelectPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("정기구독 서비스", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        panel.add(title);

        JButton monthBtn = createStyledButton("월간 구독 (9,900원/월)");
        JButton yearBtn = createStyledButton("연간 구독 (99,000원/년)");
        JButton noSubscribeBtn = createStyledButton("나중에 구독할게요");
        JButton closeBtn = createStyledButton("닫기");

        panel.add(monthBtn);
        panel.add(yearBtn);
        panel.add(noSubscribeBtn);
        panel.add(closeBtn);


        monthBtn.addActionListener(e -> {
            type = "월간";
            price = 9900;
            updateDetailPanel();
            cardLayout.show(mainPanel, "DETAIL");
        });

        yearBtn.addActionListener(e -> {
            type = "연간";
            price = 99000;
            updateDetailPanel();
            cardLayout.show(mainPanel, "DETAIL");
        });

        noSubscribeBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(Subscribe.this, "구독하지 않고 기본 서비스로 이용합니다.", "구독 취소", JOptionPane.INFORMATION_MESSAGE);
            dispose();
           
        });

        closeBtn.addActionListener(e -> {
            dispose();
        });

        return panel;
    }

   
    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        panel.add(titleLabel);

        // 구독 시 제공되는 기능
        panel.add(new JLabel("- 광고 없이 음악 감상", SwingConstants.CENTER));
        panel.add(new JLabel("- 무제한 스트리밍 및 다운로드", SwingConstants.CENTER));
        panel.add(new JLabel("- 고음질 음원 제공", SwingConstants.CENTER));
        
        priceLabel = new JLabel("", SwingConstants.CENTER);
        priceLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        panel.add(priceLabel);

        JButton payBtn = createStyledButton("결제하기");
        panel.add(payBtn);

        // 결제 버튼 액션 리스너
        payBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                Subscribe.this,
                price + "원을 결제하시겠습니까?",
                "결제 확인",
                JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(Subscribe.this, "결제가 완료되었습니다. ICTunes 정기구독 서비스에 오신 것을 환영합니다!", "결제 완료", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                
            }
        });
        
        return panel;
    }

    private void updateDetailPanel() {
        titleLabel.setText(type + " 정기구독");
        priceLabel.setText("가격: " + String.format("%,d원", price));}
    
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        button.setBackground(new Color(60, 60, 60));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }
}