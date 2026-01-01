// Ali Oturak 243405116 Musharraf Ahmed Osman 233405002

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;

public class LoginEkrani extends JFrame {

    private JTextField txtKullanici;
    private JPasswordField txtSifre;

    private final Color ARKA_PLAN = new Color(33, 47, 61);
    private final Color KUTU_PLAN = new Color(255, 255, 255);
    private final Color BUTON_RENK = new Color(46, 204, 113);

    public LoginEkrani() {
        setTitle("Güvenli Giriş");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout()); 
        mainPanel.setBackground(ARKA_PLAN);
        add(mainPanel);

        JPanel panelLogin = new JPanel();
        panelLogin.setLayout(new BoxLayout(panelLogin, BoxLayout.Y_AXIS));
        panelLogin.setBackground(KUTU_PLAN);
        panelLogin.setBorder(new EmptyBorder(40, 30, 40, 30)); 
        panelLogin.setPreferredSize(new Dimension(400, 450));
        
        JLabel lblBaslik = new JLabel("HOŞ GELDİNİZ");
        lblBaslik.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblBaslik.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblAlt = new JLabel("Personel Giriş Ekranı");
        lblAlt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblAlt.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setBackground(KUTU_PLAN);
        panelForm.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblUser = new JLabel("Kullanıcı Adı");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtKullanici = new JTextField();
        txtKullanici.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtKullanici.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        txtKullanici.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtKullanici.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200)), 
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));

        JLabel lblPass = new JLabel("Şifre");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtSifre = new JPasswordField();
        txtSifre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtSifre.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        txtSifre.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtSifre.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200)), 
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));

        panelForm.add(lblUser);
        panelForm.add(Box.createVerticalStrut(5));
        panelForm.add(txtKullanici);
        panelForm.add(Box.createVerticalStrut(15));
        panelForm.add(lblPass);
        panelForm.add(Box.createVerticalStrut(5));
        panelForm.add(txtSifre);

        JButton btnGiris = new JButton("GİRİŞ YAP");
        btnGiris.setBackground(BUTON_RENK);
        btnGiris.setForeground(Color.WHITE);
        btnGiris.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGiris.setFocusPainted(false);
        btnGiris.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnGiris.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGiris.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panelLogin.add(lblBaslik);
        panelLogin.add(lblAlt);
        panelLogin.add(Box.createVerticalStrut(40)); 
        
        panelLogin.add(panelForm);
        
        panelLogin.add(Box.createVerticalStrut(40));
        panelLogin.add(btnGiris);

        mainPanel.add(panelLogin); 

        btnGiris.addActionListener(e -> veritabaniGirisKontrol());
        
        KeyAdapter enterGiris = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) veritabaniGirisKontrol();
            }
        };
        txtKullanici.addKeyListener(enterGiris);
        txtSifre.addKeyListener(enterGiris);
    }

    private void veritabaniGirisKontrol() {
        String kAdi = txtKullanici.getText();
        String sifre = new String(txtSifre.getPassword());

        DbHelper helper = new DbHelper();
        try (Connection conn = helper.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM kullanicilar WHERE kullanici_adi = ? AND sifre = ?")) {
            
            stmt.setString(1, kAdi);
            stmt.setString(2, sifre);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String rol = rs.getString("rol");
                this.dispose(); 
                SwingUtilities.invokeLater(() -> new UrunEkrani(rol).setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this, "Kullanıcı adı veya şifre hatalı!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı bağlantı hatası!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
}