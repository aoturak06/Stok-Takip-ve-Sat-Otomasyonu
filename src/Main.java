// Ali Oturak 243405116 Musharraf Ahmed Osman 233405002

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LoginEkrani giris = new LoginEkrani();
            giris.setVisible(true);
        });
    }
}