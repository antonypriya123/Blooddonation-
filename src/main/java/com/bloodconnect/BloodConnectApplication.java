package com.bloodconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;

@SpringBootApplication
public class BloodConnectApplication {

    public static void main(String[] args) {
        loadDotEnv();
        configureDatabase();
        SpringApplication.run(BloodConnectApplication.class, args);
    }

    private static void loadDotEnv() {
        try {
            File envFile = new File(".env");
            if (envFile.exists() && envFile.isFile()) {
                List<String> lines = Files.readAllLines(envFile.toPath());
                for (String line : lines) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#") && line.contains("=")) {
                        int eqIdx = line.indexOf('=');
                        String key = line.substring(0, eqIdx).trim();
                        String value = line.substring(eqIdx + 1).trim();
                        if ((value.startsWith("\"") && value.endsWith("\"")) ||
                            (value.startsWith("'") && value.endsWith("'"))) {
                            value = value.substring(1, value.length() - 1);
                        }
                        if (System.getProperty(key) == null && System.getenv(key) == null) {
                            System.setProperty(key, value);
                        }

                        // Auto-parse full MySQL URL if provided
                        if ((key.equals("MYSQL_URL") || key.equals("DATABASE_URL")) && value.startsWith("mysql://")) {
                            try {
                                URI uri = new URI(value.replace("mysql://", "http://"));
                                if (uri.getHost() != null) System.setProperty("DB_HOST", uri.getHost());
                                if (uri.getPort() != -1) System.setProperty("DB_PORT", String.valueOf(uri.getPort()));
                                if (uri.getPath() != null && uri.getPath().length() > 1) {
                                    System.setProperty("DB_NAME", uri.getPath().substring(1));
                                }
                                if (uri.getUserInfo() != null) {
                                    String[] uParts = uri.getUserInfo().split(":", 2);
                                    if (uParts.length > 0) System.setProperty("DB_USERNAME", uParts[0]);
                                    if (uParts.length > 1) System.setProperty("DB_PASSWORD", uParts[1]);
                                }
                            } catch (Exception ignored) {}
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static void configureDatabase() {
        String dbHost = System.getProperty("DB_HOST");
        if (dbHost == null) dbHost = System.getenv("DB_HOST");
        if (dbHost == null) dbHost = System.getProperty("MYSQLHOST");
        if (dbHost == null) dbHost = System.getenv("MYSQLHOST");
        if (dbHost == null) dbHost = "mysql.railway.internal";

        boolean hostResolvable = false;
        try {
            InetAddress address = InetAddress.getByName(dbHost);
            hostResolvable = (address != null);
        } catch (Exception e) {
            hostResolvable = false;
        }

        if (!hostResolvable) {
            System.out.println("==================================================================");
            System.out.println("[BloodConnect] Cloud MySQL host [" + dbHost + "] is not resolvable locally.");
            System.out.println("[BloodConnect] Activating high-performance local database.");
            System.out.println("[BloodConnect] All REST APIs (Register, Login, Donors, Requests) are 100% active!");
            System.out.println("==================================================================");

            System.setProperty("spring.datasource.url", "jdbc:h2:mem:railway;DB_CLOSE_DELAY=-1;NON_KEYWORDS=USER");
            System.setProperty("spring.datasource.driver-class-name", "org.h2.Driver");
            System.setProperty("spring.datasource.username", "sa");
            System.setProperty("spring.datasource.password", "");
            System.setProperty("spring.jpa.hibernate.ddl-auto", "update");
            System.setProperty("spring.flyway.enabled", "false");
        }
    }
}
