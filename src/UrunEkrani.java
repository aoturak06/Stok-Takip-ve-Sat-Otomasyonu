import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class SepetUrunu {
    int id; String urunAdi; int adet; double satisFiyati; int mevcutStok; 
    public SepetUrunu(int id, String urunAdi, int adet, double satisFiyati, int mevcutStok) {
        this.id = id; this.urunAdi = urunAdi; this.adet = adet; this.satisFiyati = satisFiyati; this.mevcutStok = mevcutStok;
    }
    public double getToplamTutar() { return adet * satisFiyati; }
}

public class UrunEkrani extends JFrame {

    private JTable tblUrunler;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private ArrayList<SepetUrunu> sepetListesi = new ArrayList<>();
    
    private String kullaniciRolu;

    private JTextField txtAd = new JTextField();
    private JTextField txtKategori = new JTextField();
    private JTextField txtFiyat = new JTextField();
    private JTextField txtStok = new JTextField();
    private JTextArea txtAciklama = new JTextArea(3, 20); 
    private JTextField txtAra = new JTextField();
    private JLabel lblSepetBilgi;

    private final Color SIDEBAR_BG = new Color(33, 47, 61);
    private final Color SIDEBAR_TEXT = new Color(236, 240, 241);
    private final Color CONTENT_BG = new Color(248, 249, 250);
    private final Color TABLE_HEADER_BG = new Color(52, 73, 94);
    private final Color TABLE_ALT_ROW = new Color(242, 242, 242);

    private final Color BTN_KAYDET = new Color(46, 204, 113);
    private final Color BTN_GUNCELLE = new Color(52, 152, 219);
    private final Color BTN_SIL = new Color(231, 76, 60);
    private final Color BTN_SEPETE_EKLE = new Color(243, 156, 18); 
    private final Color BTN_SEPETI_ONAYLA = new Color(39, 174, 96); 
    private final Color BTN_GECMIS = new Color(142, 68, 173);

    public UrunEkrani(String rol) {
        this.kullaniciRolu = rol;

        setTitle("Stok Takip - " + rol + " Paneli");
        setSize(1350, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelSol = new JPanel();
        panelSol.setLayout(new BoxLayout(panelSol, BoxLayout.Y_AXIS)); 
        panelSol.setBorder(new EmptyBorder(30, 25, 30, 25)); 
        panelSol.setPreferredSize(new Dimension(360, 0)); 
        panelSol.setBackground(SIDEBAR_BG); 

        JLabel lblLogo = new JLabel("STOK TAKİP");
        lblLogo.setForeground(SIDEBAR_TEXT);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblRol = new JLabel("Giriş Yapan: " + rol);
        lblRol.setForeground(new Color(46, 204, 113));
        lblRol.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblRol.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelSol.add(lblLogo);
        panelSol.add(lblRol);
        panelSol.add(Box.createVerticalStrut(30)); 

        inputEkle(panelSol, "Ürün Adı", txtAd);
        inputEkle(panelSol, "Kategori", txtKategori);
        inputEkle(panelSol, "Birim Fiyat (TL)", txtFiyat);
        inputEkle(panelSol, "Stok Adedi", txtStok);
        
        JLabel lblAciklama = new JLabel("Teknik Özellikler");
        lblAciklama.setForeground(SIDEBAR_TEXT);
        lblAciklama.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblAciklama.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelSol.add(lblAciklama);
        panelSol.add(Box.createVerticalStrut(8));
        
        txtAciklama.setLineWrap(true);
        JScrollPane scrollAciklama = new JScrollPane(txtAciklama);
        scrollAciklama.setBorder(null);
        scrollAciklama.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelSol.add(scrollAciklama);
        panelSol.add(Box.createVerticalStrut(20));

        JPanel panelButon = new JPanel(new GridLayout(3, 2, 8, 8)); 
        panelButon.setBackground(SIDEBAR_BG); 
        panelButon.setMaximumSize(new Dimension(360, 140)); 
        panelButon.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panelButon.add(ozelButonOlustur("ÜRÜN EKLE", BTN_KAYDET)); 
        panelButon.add(ozelButonOlustur("GÜNCELLE", BTN_GUNCELLE));
        panelButon.add(ozelButonOlustur("SİL", BTN_SIL));
        panelButon.add(ozelButonOlustur("SEPETE EKLE", BTN_SEPETE_EKLE));
        panelButon.add(ozelButonOlustur("SEPETİ ONAYLA", BTN_SEPETI_ONAYLA));
        
        JPanel panelGecmis = new JPanel(new GridLayout(1, 1));
        panelGecmis.setBackground(SIDEBAR_BG);
        panelGecmis.setMaximumSize(new Dimension(360, 40));
        panelGecmis.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelGecmis.add(ozelButonOlustur("GEÇMİŞ SATIŞLAR", BTN_GECMIS));
        
        panelSol.add(panelButon);
        panelSol.add(Box.createVerticalStrut(8));
        panelSol.add(panelGecmis);
        
        lblSepetBilgi = new JLabel("Sepet Boş: 0.0 TL");
        lblSepetBilgi.setForeground(new Color(241, 196, 15));
        lblSepetBilgi.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSepetBilgi.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelSol.add(Box.createVerticalStrut(20));
        panelSol.add(lblSepetBilgi);

        add(panelSol, BorderLayout.WEST);

        JPanel panelSag = new JPanel(new BorderLayout());
        panelSag.setBackground(CONTENT_BG);
        panelSag.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel panelUst = new JPanel(new BorderLayout());
        panelUst.setBackground(CONTENT_BG);
        panelUst.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel lblAra = new JLabel("Hızlı Arama:  ");
        lblAra.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblAra.setForeground(SIDEBAR_BG);
        
        txtAra.setPreferredSize(new Dimension(250, 40));
        txtAra.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(5, 10, 5, 10)));

        panelUst.add(lblAra, BorderLayout.WEST);
        panelUst.add(txtAra, BorderLayout.CENTER);
        panelSag.add(panelUst, BorderLayout.NORTH);

        String[] kolonlar = {"ID", "Ürün Adı", "Kategori", "Fiyat", "Stok", "Açıklama"};
        model = new DefaultTableModel(kolonlar, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tblUrunler = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ALT_ROW);
                return c;
            }
        };
        tblUrunler.setRowHeight(35);
        tblUrunler.setShowVerticalLines(false);
        sorter = new TableRowSorter<>(model);
        tblUrunler.setRowSorter(sorter);

        JTableHeader header = tblUrunler.getTableHeader();
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setPreferredSize(new Dimension(0, 45));
        
        JScrollPane scrollPane = new JScrollPane(tblUrunler);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panelSag.add(scrollPane, BorderLayout.CENTER);
        add(panelSag, BorderLayout.CENTER);

        urunleriListele(); 
        
        txtAra.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = txtAra.getText().trim(); 
                if (text.length() == 0) sorter.setRowFilter(null);
                else {
                    String[] words = text.split("\\s+");
                    List<RowFilter<Object, Object>> filters = new ArrayList<>();
                    for (String word : words) filters.add(RowFilter.regexFilter("(?i)" + word));
                    sorter.setRowFilter(RowFilter.andFilter(filters));
                }
            }
        });

        MouseAdapter temizleEvent = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { temizle(); }
        };
        panelSol.addMouseListener(temizleEvent);
        panelSag.addMouseListener(temizleEvent);
        tblUrunler.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent evt) {
                int row = tblUrunler.rowAtPoint(evt.getPoint());
                if (row == -1) temizle();
                else {
                    int secili = tblUrunler.getSelectedRow();
                    if (secili != -1) {
                        int modelRow = tblUrunler.convertRowIndexToModel(secili);
                        txtAd.setText(model.getValueAt(modelRow, 1).toString());
                        txtKategori.setText(model.getValueAt(modelRow, 2).toString());
                        txtFiyat.setText(model.getValueAt(modelRow, 3).toString());
                        txtStok.setText(model.getValueAt(modelRow, 4).toString());
                        Object aciklama = model.getValueAt(modelRow, 5);
                        txtAciklama.setText(aciklama != null ? aciklama.toString() : "");
                    }
                }
            }
        });
    }

    private void inputEkle(JPanel panel, String baslik, JTextField text) {
        JLabel lbl = new JLabel(baslik);
        lbl.setForeground(SIDEBAR_TEXT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(5));
        text.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        text.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); 
        text.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(text);
        panel.add(Box.createVerticalStrut(15));
    }

    private JButton ozelButonOlustur(String yazi, Color renk) {
        JButton btn = new JButton(yazi);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(renk);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        
        if (kullaniciRolu.equals("Personel")) {
            if (yazi.equals("ÜRÜN EKLE") || yazi.equals("GÜNCELLE") || yazi.equals("SİL") || yazi.equals("GEÇMİŞ SATIŞLAR")) {
                btn.setEnabled(false);
                btn.setBackground(Color.GRAY);
                btn.setToolTipText("Bu işlem için yetkiniz yok.");
            }
        }

        btn.addActionListener(e -> {
            if (yazi.equals("ÜRÜN EKLE")) urunEkle(); 
            else if (yazi.equals("GÜNCELLE")) urunGuncelle();
            else if (yazi.equals("SEPETE EKLE")) sepeteEkle();
            else if (yazi.equals("SEPETİ ONAYLA")) sepetiOnayla();
            else if (yazi.equals("SİL")) urunSil();
            else if (yazi.equals("GEÇMİŞ SATIŞLAR")) new SatisGecmisi().setVisible(true);
        });
        return btn;
    }

    public void sepeteEkle() {
        int secili = tblUrunler.getSelectedRow();
        if (secili == -1) { JOptionPane.showMessageDialog(null, "Sepete eklemek için ürün seçiniz."); return; }
        
        int modelRow = tblUrunler.convertRowIndexToModel(secili);
        int id = (int) model.getValueAt(modelRow, 0);
        String ad = (String) model.getValueAt(modelRow, 1);
        double listeFiyati = (double) model.getValueAt(modelRow, 3);
        int mevcutStok = (int) model.getValueAt(modelRow, 4);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("<html><b>Seçilen:</b> " + ad + "<br/>Stok: " + mevcutStok + "</html>"));
        panel.add(new JLabel("Adet:"));
        JTextField txtAdet = new JTextField("1");
        panel.add(txtAdet);
        panel.add(new JLabel("Satış Fiyatı:"));
        JTextField txtFiyat = new JTextField(String.valueOf(listeFiyati));
        panel.add(txtFiyat);

        if (JOptionPane.showConfirmDialog(null, panel, "Sepete Ekle", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                int adet = Integer.parseInt(txtAdet.getText());
                double satisFiyati = Double.parseDouble(txtFiyat.getText().replace(",", "."));
                if (adet <= 0 || satisFiyati < 0) { JOptionPane.showMessageDialog(null, "Geçersiz değerler!"); return; }
                
                int sepettekiAdet = 0;
                for(SepetUrunu u : sepetListesi) if(u.id == id) sepettekiAdet += u.adet;

                if (mevcutStok < (adet + sepettekiAdet)) {
                    JOptionPane.showMessageDialog(null, "Yetersiz Stok! (Sepette zaten " + sepettekiAdet + " adet var)");
                    return;
                }
                sepetListesi.add(new SepetUrunu(id, ad, adet, satisFiyati, mevcutStok));
                sepetiGuncelle();
                JOptionPane.showMessageDialog(null, ad + " sepete eklendi.");
            } catch (Exception e) { JOptionPane.showMessageDialog(null, "Hatalı giriş!"); }
        }
    }

    private void sepetiGuncelle() {
        double toplam = 0; int adet = 0;
        for (SepetUrunu u : sepetListesi) { toplam += u.getToplamTutar(); adet += u.adet; }
        lblSepetBilgi.setText("<html>Sepet: " + adet + " Ürün<br/>Tutar: " + toplam + " TL</html>");
    }

    public void sepetiOnayla() {
        if (sepetListesi.isEmpty()) { JOptionPane.showMessageDialog(null, "Sepetiniz boş!"); return; }

        JDialog dialog = new JDialog(this, "Sepeti Onayla ve Düzenle", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JLabel lblBaslik = new JLabel("SEPET İÇERİĞİ", SwingConstants.CENTER);
        lblBaslik.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblBaslik.setBorder(new EmptyBorder(10, 0, 10, 0));
        dialog.add(lblBaslik, BorderLayout.NORTH);

        String[] cols = {"Ürün Adı", "Adet", "Birim Fiyat", "Toplam"};
        DefaultTableModel sepetModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        for (SepetUrunu u : sepetListesi) {
            sepetModel.addRow(new Object[]{u.urunAdi, u.adet, u.satisFiyati, u.getToplamTutar()});
        }

        JTable tblSepet = new JTable(sepetModel);
        tblSepet.setRowHeight(25);
        dialog.add(new JScrollPane(tblSepet), BorderLayout.CENTER);

        JPanel panelAlt = new JPanel(new BorderLayout());
        panelAlt.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblToplamTutar = new JLabel("TOPLAM: 0.0 TL", SwingConstants.CENTER);
        lblToplamTutar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblToplamTutar.setForeground(new Color(192, 57, 43));
        
        double baslangicToplam = 0;
        for (SepetUrunu u : sepetListesi) baslangicToplam += u.getToplamTutar();
        lblToplamTutar.setText("TOPLAM: " + baslangicToplam + " TL");

        JButton btnCikar = new JButton("SEÇİLİ ÜRÜNÜ SEPETTEN ÇIKAR");
        btnCikar.setBackground(new Color(231, 76, 60));
        btnCikar.setForeground(Color.WHITE);
        btnCikar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        btnCikar.addActionListener(e -> {
            int row = tblSepet.getSelectedRow();
            if (row != -1) {
                sepetListesi.remove(row);
                sepetModel.removeRow(row);
                
                double yeniToplam = 0;
                for (SepetUrunu u : sepetListesi) yeniToplam += u.getToplamTutar();
                lblToplamTutar.setText("TOPLAM: " + yeniToplam + " TL");
                sepetiGuncelle();
            } else {
                JOptionPane.showMessageDialog(dialog, "Lütfen çıkarmak istediğiniz ürünü seçin.");
            }
        });

        JButton btnOnayla = new JButton("SATIŞI TAMAMLA");
        btnOnayla.setBackground(new Color(46, 204, 113));
        btnOnayla.setForeground(Color.WHITE);
        btnOnayla.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnOnayla.setPreferredSize(new Dimension(0, 45));
        
        btnOnayla.addActionListener(e -> {
            if (sepetListesi.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Sepet boş, satış yapılamaz!");
            } else {
                veritabaniSatisIslemi();
                dialog.dispose();
            }
        });

        JPanel panelButonlar = new JPanel(new GridLayout(1, 2, 10, 0));
        panelButonlar.add(btnCikar);
        
        JPanel panelAltContainer = new JPanel(new BorderLayout());
        panelAltContainer.add(lblToplamTutar, BorderLayout.NORTH);
        panelAltContainer.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        
        JPanel panelAction = new JPanel(new BorderLayout());
        panelAction.add(btnCikar, BorderLayout.NORTH);
        panelAction.add(btnOnayla, BorderLayout.SOUTH);
        
        panelAlt.add(panelAltContainer, BorderLayout.NORTH);
        panelAlt.add(panelAction, BorderLayout.SOUTH);

        dialog.add(panelAlt, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void veritabaniSatisIslemi() {
        DbHelper helper = new DbHelper();
        Connection conn = null;
        try {
            conn = helper.getConnection(); conn.setAutoCommit(false); 
            String sqlStok = "UPDATE urunler SET stok_adedi = stok_adedi - ? WHERE id = ?";
            String sqlGecmis = "INSERT INTO satislar (urun_ad, adet, birim_fiyat, toplam_tutar) VALUES (?, ?, ?, ?)";
            PreparedStatement stmtStok = conn.prepareStatement(sqlStok);
            PreparedStatement stmtGecmis = conn.prepareStatement(sqlGecmis);

            for (SepetUrunu u : sepetListesi) {
                stmtStok.setInt(1, u.adet); stmtStok.setInt(2, u.id); stmtStok.addBatch();
                stmtGecmis.setString(1, u.urunAdi); stmtGecmis.setInt(2, u.adet); stmtGecmis.setDouble(3, u.satisFiyati); stmtGecmis.setDouble(4, u.getToplamTutar()); stmtGecmis.addBatch();
            }
            stmtStok.executeBatch(); stmtGecmis.executeBatch(); conn.commit(); 
            JOptionPane.showMessageDialog(null, "Satış başarıyla kaydedildi!");
            sepetListesi.clear(); sepetiGuncelle(); urunleriListele();
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {} 
            e.printStackTrace();
        } finally { try { if (conn != null) conn.close(); } catch (SQLException ex) {} }
    }

    public void urunleriListele() {
        model.setRowCount(0); DbHelper helper = new DbHelper();
        try (Connection conn = helper.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM urunler")) {
            while (rs.next()) model.addRow(new Object[]{rs.getInt("id"), rs.getString("ad"), rs.getString("kategori"), rs.getDouble("fiyat"), rs.getInt("stok_adedi"), rs.getString("aciklama")});
        } catch (SQLException e) { e.printStackTrace(); }
    }
    public void urunEkle() {
        if (!validasyonKontrol()) return; DbHelper helper = new DbHelper();
        try (Connection conn = helper.getConnection(); PreparedStatement stmt = conn.prepareStatement("INSERT INTO urunler (ad, kategori, fiyat, stok_adedi, aciklama) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, txtAd.getText()); stmt.setString(2, txtKategori.getText()); stmt.setDouble(3, Double.parseDouble(txtFiyat.getText().replace(",", "."))); stmt.setInt(4, Integer.parseInt(txtStok.getText())); stmt.setString(5, txtAciklama.getText());
            stmt.executeUpdate(); urunleriListele(); temizle(); JOptionPane.showMessageDialog(null, "Ürün Eklendi");
        } catch (Exception e) { e.printStackTrace(); }
    }
    public void urunGuncelle() {
        int secili = tblUrunler.getSelectedRow(); if (secili == -1) return; if (!validasyonKontrol()) return;
        int id = (int) model.getValueAt(tblUrunler.convertRowIndexToModel(secili), 0); DbHelper helper = new DbHelper();
        try (Connection conn = helper.getConnection(); PreparedStatement stmt = conn.prepareStatement("UPDATE urunler SET ad=?, kategori=?, fiyat=?, stok_adedi=?, aciklama=? WHERE id=?")) {
            stmt.setString(1, txtAd.getText()); stmt.setString(2, txtKategori.getText()); stmt.setDouble(3, Double.parseDouble(txtFiyat.getText().replace(",", "."))); stmt.setInt(4, Integer.parseInt(txtStok.getText())); stmt.setString(5, txtAciklama.getText()); stmt.setInt(6, id);
            stmt.executeUpdate(); urunleriListele(); temizle(); JOptionPane.showMessageDialog(null, "Güncellendi");
        } catch (Exception e) { e.printStackTrace(); }
    }
    public void urunSil() {
        int secili = tblUrunler.getSelectedRow(); if (secili == -1) return; if (JOptionPane.showConfirmDialog(null, "Emin misiniz?", "Onay", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        int id = (int) model.getValueAt(tblUrunler.convertRowIndexToModel(secili), 0); DbHelper helper = new DbHelper();
        try (Connection conn = helper.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM urunler WHERE id=?")) {
            stmt.setInt(1, id); stmt.executeUpdate(); urunleriListele(); temizle(); JOptionPane.showMessageDialog(null, "Silindi");
        } catch (Exception e) { e.printStackTrace(); }
    }
    public void temizle() { txtAd.setText(""); txtKategori.setText(""); txtFiyat.setText(""); txtStok.setText(""); txtAciklama.setText(""); txtAra.setText(""); sorter.setRowFilter(null); tblUrunler.clearSelection(); }
    
    // YENİ GÜNCELLENEN METOD: Validasyon kontrolü (Hata mesajlı)
    private boolean validasyonKontrol() {
        try {
            if (txtFiyat.getText().trim().isEmpty() || txtStok.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurunuz.", "Eksik Veri", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            double fiyat = Double.parseDouble(txtFiyat.getText().replace(",", "."));
            int stok = Integer.parseInt(txtStok.getText());

            if (fiyat < 0 || stok < 0) {
                JOptionPane.showMessageDialog(this, "Fiyat veya stok negatif olamaz!", "Hata", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Lütfen geçerli bir sayı giriniz!", "Hatalı Giriş", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}