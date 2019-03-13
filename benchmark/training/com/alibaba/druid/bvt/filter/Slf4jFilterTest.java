package com.alibaba.druid.bvt.filter;


import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import junit.framework.TestCase;
import org.junit.Assert;


public class Slf4jFilterTest extends TestCase {
    private DruidDataSource dataSource;

    public void test_slf4j() throws Exception {
        dataSource.init();
        Slf4jLogFilter filter = dataSource.unwrap(Slf4jLogFilter.class);
        Assert.assertNotNull(filter);
        Connection conn = dataSource.getConnection();
        conn.close();
    }
}

