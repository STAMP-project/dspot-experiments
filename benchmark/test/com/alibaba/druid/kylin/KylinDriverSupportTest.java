package com.alibaba.druid.kylin;


import JdbcConstants.KYLIN_DRIVER;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author yinheli
 */
public class KylinDriverSupportTest {
    // test config
    private static final String URL = "jdbc:kylin://172.168.1.111:7070/jlkBigData";

    private static final String USER_NAME = "ADMIN";

    private static final String PASSWORD = "KYLIN";

    private static final String VALIDATION_QUERY = "select 1";

    @Test
    public void testDriverClassName() throws SQLException {
        String driverClass = JdbcUtils.getDriverClassName(KylinDriverSupportTest.URL);
        Assert.assertThat("check get driverClassName", driverClass, Is.is(KYLIN_DRIVER));
    }

    @Test
    public void testQuery() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        try {
            dataSource = new DruidDataSource();
            dataSource.setUrl(KylinDriverSupportTest.URL);
            dataSource.setUsername(KylinDriverSupportTest.USER_NAME);
            dataSource.setPassword(KylinDriverSupportTest.PASSWORD);
            dataSource.setValidationQuery(KylinDriverSupportTest.VALIDATION_QUERY);
            Connection conn = dataSource.getConnection();
            PreparedStatement state = conn.prepareStatement(KylinDriverSupportTest.VALIDATION_QUERY);
            ResultSet resultSet = state.executeQuery();
            Assert.assertThat("check result", resultSet, IsNull.notNullValue());
        } finally {
            dataSource.close();
        }
    }
}

