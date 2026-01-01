// Ali Oturak 243405116 Musharraf Ahmed Osman 233405002

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SatisGecmisi extends JFrame {

    private JTable tblSatislar;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter; 
    private JTextField txtAra; 

    private final Color HEADER_BG = new Color(52, 73, 94);
    private final Color ALT_ROW_BG = new Color(242, 242, 242);
    private final Color DELETE_ALL_BG = new Color(192, 57, 43); 
    private final Color DELETE_ONE_BG = new Color(230, 126, 34); 

    public SatisGecmisi() {
        setTitle("Satış Geçmişi");
        setSize(950, 650); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        
        JPanel panelUstContainer = new JPanel();
        panelUstContainer.setLayout(new BoxLayout(panelUstContainer, BoxLayout.Y_AXIS));
        panelUstContainer.setBackground(Color.WHITE);
        panelUstContainer.setBorder(new EmptyBorder(10, 20, 10, 20));

        
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(Color.WHITE);
        
        JLabel lblBaslik = new JLabel("GEÇMİŞ SATIŞLAR");
        lblBaslik.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBaslik.setForeground(HEADER_BG);

        JPanel panelButonlar = new JPanel(new GridLayout(1, 2, 10, 0));
        panelButonlar.setBackground(Color.WHITE);

        JButton btnSeciliSil = new JButton("SEÇİLİ SATIŞI SİL");
        btnSeciliSil.setBackground(DELETE_ONE_BG);
        btnSeciliSil.setForeground(Color.WHITE);
        btnSeciliSil.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSeciliSil.setFocusPainted(false);
        btnSeciliSil.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSeciliSil.setPreferredSize(new Dimension(150, 35));

        JButton btnTemizle = new JButton("TÜM GEÇMİŞİ SİL");
        btnTemizle.setBackground(DELETE_ALL_BG);
        btnTemizle.setForeground(Color.WHITE);
        btnTemizle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTemizle.setFocusPainted(false);
        btnTemizle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTemizle.setPreferredSize(new Dimension(150, 35));
        
        btnSeciliSil.addActionListener(e -> seciliSatisiSil());
        btnTemizle.addActionListener(e -> gecmisiTemizle());

        panelButonlar.add(btnSeciliSil);
        panelButonlar.add(btnTemizle);

        panelHeader.add(lblBaslik, BorderLayout.WEST);
        panelHeader.add(panelButonlar, BorderLayout.EAST);

        
        JPanel panelArama = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 15));
        panelArama.setBackground(Color.WHITE);

        JLabel lblAra = new JLabel("Satış Ara: ");
        lblAra.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAra.setForeground(Color.DARK_GRAY);

        txtAra = new JTextField();
        txtAra.setPreferredSize(new Dimension(300, 35));
        txtAra.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtAra.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(5, 10, 5, 10)));
        
        
        txtAra.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrele(txtAra.getText());
            }
        });

        panelArama.add(lblAra);
        panelArama.add(txtAra);

        panelUstContainer.add(panelHeader);
        panelUstContainer.add(panelArama);
        
        add(panelUstContainer, BorderLayout.NORTH);

        
        String[] kolonlar = {"ID", "Ürün Adı", "Adet", "Birim Fiyat", "Toplam Tutar", "Tarih"};
        model = new DefaultTableModel(kolonlar, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tblSatislar = new JTable(model) {
            @Override public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) c.setBackground(row % 2 == 0 ? Color.WHITE : ALT_ROW_BG);
                return c;
            }
        };
        
        tblSatislar.setRowHeight(30);
        tblSatislar.setShowVerticalLines(false);
        tblSatislar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        
        sorter = new TableRowSorter<>(model);
        tblSatislar.setRowSorter(sorter);
        
        JTableHeader header = tblSatislar.getTableHeader();
        header.setBackground(HEADER_BG);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(0, 40));

        JScrollPane scrollPane = new JScrollPane(tblSatislar);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        verileriGetir();
    }

    

    private void filtrele(String metin) {
        if (metin.trim().length() == 0) {
            sorter.setRowFilter(null); 
        } else {
           
            try {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + metin));
            } catch (java.util.regex.PatternSyntaxException e) {
                return;
            }
        }
    }

    private void verileriGetir() {
        model.setRowCount(0);
        DbHelper helper = new DbHelper();
        try (Connection conn = helper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM satislar ORDER BY tarih DESC")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("urun_ad"),
                        rs.getInt("adet"),
                        rs.getDouble("birim_fiyat"),
                        rs.getDouble("toplam_tutar"),
                        rs.getTimestamp("tarih")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void seciliSatisiSil() {
        int seciliSatir = tblSatislar.getSelectedRow();

        if (seciliSatir == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen silinecek satışı tablodan seçiniz.", "Seçim Yok", JOptionPane.WARNING_MESSAGE);
            return;
        }

        
        int modelRow = tblSatislar.convertRowIndexToModel(seciliSatir);
        int id = (int) model.getValueAt(modelRow, 0);

        int cevap = JOptionPane.showConfirmDialog(this, 
                "Seçili satış kaydı silinecek. Emin misiniz?", 
                "Silme Onayı", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (cevap == JOptionPane.YES_OPTION) {
            DbHelper helper = new DbHelper();
            try (Connection conn = helper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM satislar WHERE id = ?")) {
                
                stmt.setInt(1, id);
                stmt.executeUpdate();
                
                model.removeRow(modelRow);
                JOptionPane.showMessageDialog(this, "Kayıt silindi.");
                
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Hata: " + e.getMessage());
            }
        }
    }

    private void gecmisiTemizle() {
        int cevap = JOptionPane.showConfirmDialog(this, 
                "TÜM satış geçmişi kalıcı olarak silinecek.\nBu işlem geri alınamaz! Emin misiniz?", 
                "Geçmişi Temizle", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (cevap == JOptionPane.YES_OPTION) {
            DbHelper helper = new DbHelper();
            try (Connection conn = helper.getConnection();
                 Statement stmt = conn.createStatement()) {
                
                stmt.executeUpdate("TRUNCATE TABLE satislar");
                verileriGetir(); 
                JOptionPane.showMessageDialog(this, "Satış geçmişi tamamen temizlendi.");
                
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Silme işlemi sırasında hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}