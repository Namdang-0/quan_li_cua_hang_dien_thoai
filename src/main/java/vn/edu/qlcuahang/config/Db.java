package vn.edu.qlcuahang.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Quản lý kết nối JDBC đến SQL Server. Không lưu mật khẩu thật trong mã nguồn. */
public final class Db {
    private Db() { }

    public static Connection getConnection() throws SQLException {
        Properties p = loadProperties();
        String url = envOrProperty("DB_URL", p, "db.url");
        String user = envOrProperty("DB_USER", p, "db.user");
        String password = envOrProperty("DB_PASSWORD", p, "db.password");

        if (url == null || url.isBlank() || user == null || user.isBlank()) {
            throw new SQLException("Chưa cấu hình kết nối SQL Server. Hãy sao chép "
                    + "config/db.properties.example thành config/db.properties rồi sửa db.url, db.user, db.password.");
        }
        try {
            return DriverManager.getConnection(url.trim(), user.trim(), password == null ? "" : password);
        } catch (SQLException ex) {
            String detail = ex.getMessage() == null ? "" : ex.getMessage();
            if (detail.contains("No suitable driver")) {
                throw new SQLException("Không tìm thấy JDBC Driver. Hãy mở dự án bằng Maven/IntelliJ và tải các dependency trong pom.xml.", ex);
            }
            throw ex;
        }
    }

    private static Properties loadProperties() {
        Properties p = new Properties();
        Path path = Path.of("config", "db.properties");
        if (Files.isRegularFile(path)) {
            try (var in = Files.newInputStream(path)) {
                p.load(in);
            } catch (IOException ignored) {
                // Lỗi đọc cấu hình sẽ được thể hiện ở thông báo cấu hình/kết nối.
            }
        }
        return p;
    }

    private static String envOrProperty(String env, Properties p, String key) {
        String value = System.getenv(env);
        if (value != null && !value.isBlank()) return value;
        value = System.getProperty(key);
        if (value != null && !value.isBlank()) return value;
        return p.getProperty(key);
    }
}
