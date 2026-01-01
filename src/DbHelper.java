// Ali Oturak 243405116 Musharraf Ahmed Osman 233405002

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbHelper {
    private String userName = "root";
    private String password = "";
    private String dbUrl = "jdbc:mysql://localhost:3306/stok_takip";

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, userName, password);
    }

    public void showErrorMessage(SQLException exception){
        System.out.println("Hata: " + exception.getMessage());
        System.out.println("Hata kodu: " + exception.getErrorCode());
    }
}