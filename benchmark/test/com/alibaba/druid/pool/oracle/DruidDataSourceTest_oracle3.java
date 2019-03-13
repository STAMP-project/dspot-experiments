package com.alibaba.druid.pool.oracle;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.vendor.OracleValidConnectionChecker;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import junit.framework.TestCase;
import org.junit.Assert;


/**
 * ??????minIdle > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_oracle3 extends TestCase {
    private DruidDataSource dataSource;

    public void test_error() throws Exception {
        dataSource.init();
        Assert.assertTrue(dataSource.isOracle());
        Assert.assertTrue(((dataSource.getValidConnectionChecker()) instanceof OracleValidConnectionChecker));
        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1 FROM DUAL");
            while (rs.next()) {
            } 
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1 FROM DUAL");
            while (rs.next()) {
            } 
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1 FROM DUAL");
            while (rs.next()) {
            } 
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

