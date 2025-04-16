package com.mysql.test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySQLConnectionTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // Load properties file

        Logger logger = LoggerFactory.getLogger(MySQLConnectionTest.class);
        logger.info("INFO {}", ": Logging Started");
        String sUrl = null;

        //FileReader resources= new FileReader("config.properties");
        Properties properties = new Properties();
        try (InputStream input = MySQLConnectionTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            } 
//         try (FileReader input = new FileReader("config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        // HikariCP 설정
        HikariConfig config = new HikariConfig();
        if (args.length > 0) {
            sUrl = "jdbc:mysql://" + args[0] + ":3306/test?serverTimezone=Asia/Seoul";
            config.setJdbcUrl(sUrl);
        }
        else config.setJdbcUrl(properties.getProperty("db.url"));
        config.setUsername(properties.getProperty("db.username"));
        config.setPassword(properties.getProperty("db.password"));
        config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("db.pool.size")));
        config.setMinimumIdle(2);
        config.setConnectionTimeout(5000);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        HikariDataSource dataSource = new HikariDataSource(config);

        long startTime = System.currentTimeMillis();
        String sTime = null;

        try (Connection connection = dataSource.getConnection()) {
            long connectionTime = System.currentTimeMillis();
            System.out.print("Connection Time: " + (connectionTime - startTime) + " ms, ");

            /* // 샘플 쿼리 실행 (select count(1) from performance_schema.processlist)
            String query = "select count(1) from performance_schema.processlist";
            try (PreparedStatement pstmt = connection.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {

                long queryStartTime = System.currentTimeMillis();
                if (rs.next()) {
                    int result = rs.getInt(1);
                    long queryEndTime = System.currentTimeMillis();
                    System.out.println("Query Result: " + result);
                    System.out.println("Query Execution Time: " + (queryEndTime - queryStartTime) + " ms");
                }
            } */

            // 샘플 쿼리 실행 (select count(1) from performance_schema.processlist)
            String query = "select now()";
            try (PreparedStatement pstmt = connection.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {

                long queryStartTime = System.currentTimeMillis();
                if (rs.next()) {
                    sTime = rs.getString(1);
                    long queryEndTime = System.currentTimeMillis();
                    System.out.print("Query Result: " + sTime + ", ");
                    System.out.println("Query Execution Time: " + (queryEndTime - queryStartTime) + " ms");
                }
            }            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dataSource.close(); // Connection Pool 종료
        }
    }
}