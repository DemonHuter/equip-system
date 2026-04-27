package com.equip.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Component
public class DatabaseInitializer implements ApplicationRunner {

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("=== 开始数据库初始化 ===");
        File dataDir = new File("./data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
            System.out.println("创建data目录");
        }
        System.out.println("data目录路径: " + dataDir.getAbsolutePath());

        // 检查是否有表，没有表才执行schema.sql
        System.out.println("检查数据库中是否有表");
        if (!hasAnyTable()) {
            System.out.println("数据库中没有表，执行schema.sql");
            executeSchemaSql();
        } else {
            System.out.println("数据库中已有表，跳过初始化");
        }
        
        System.out.println("=== 数据库初始化完成 ===");
    }

    private boolean hasAnyTable() {
        try {
            Connection connection = DataSourceUtils.getConnection(dataSource);
            Statement statement = connection.createStatement();
            // 使用SQLite查询用户表，排除系统表sqlite_sequence
            ResultSet rs = statement.executeQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name != 'sqlite_sequence'"
            );
            boolean exists = rs.next();
            rs.close();
            statement.close();
            DataSourceUtils.releaseConnection(connection, dataSource);
            return exists;
        } catch (Exception e) {
            return false;
        }
    }

    private void executeSchemaSql() {
        Connection connection = null;
        Statement stmt = null;
        try {
            connection = DataSourceUtils.getConnection(dataSource);
            connection.setAutoCommit(false);
            stmt = connection.createStatement();

            // 读取schema.sql文件
            String sqlContent = readFileToString(getClass().getClassLoader().getResourceAsStream("schema.sql"));

            // 分割SQL语句
            String[] sqlStatements = sqlContent.split(";\\s*");
            for (String sql : sqlStatements) {
                if (!sql.trim().isEmpty()) {
                    stmt.addBatch(sql);
                }
            }

            // 执行批量SQL语句
            int[] counts = stmt.executeBatch();
            connection.commit();
            System.out.println("成功执行schema.sql，导入数据，执行了 " + counts.length + " 条语句");
            
            // 查询数据量
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM measurement_ledger");
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("数据库中数据量: " + count);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (connection != null) connection.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (connection != null) {
                    connection.setAutoCommit(true);
                    DataSourceUtils.releaseConnection(connection, dataSource);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String readFileToString(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, java.nio.charset.StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }



}