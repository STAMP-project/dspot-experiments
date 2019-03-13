package com.taobao.yugong.common;


import com.taobao.yugong.BaseDbTest;
import com.taobao.yugong.common.db.DataSourceFactory;
import javax.sql.DataSource;
import junit.framework.Assert;
import org.junit.Test;


/**
 *
 *
 * @author agapple 2014?2?25? ??11:38:06
 * @since 1.0.0
 */
public class DataSourceFactoryTest extends BaseDbTest {
    @Test
    public void testOracle() {
        DataSourceFactory dataSourceFactory = new DataSourceFactory();
        dataSourceFactory.start();
        DataSource oracle1 = dataSourceFactory.getDataSource(getOracleConfig());
        DataSource oracle2 = dataSourceFactory.getDataSource(getOracleConfig());
        Assert.assertTrue((oracle1 == oracle2));
        testConnection(oracle1);
        dataSourceFactory.stop();
    }

    @Test
    public void testMysql() {
        DataSourceFactory dataSourceFactory = new DataSourceFactory();
        dataSourceFactory.start();
        DataSource mysql1 = dataSourceFactory.getDataSource(getMysqlConfig());
        DataSource mysql2 = dataSourceFactory.getDataSource(getMysqlConfig());
        Assert.assertTrue((mysql1 == mysql2));
        testConnection(mysql1);
        dataSourceFactory.stop();
    }
}

