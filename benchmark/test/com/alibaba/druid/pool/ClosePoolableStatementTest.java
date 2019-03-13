package com.alibaba.druid.pool;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * ????PSCache???DruidPooledPreparedStatement????
 *
 * @author DigitalSonic
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:com/alibaba/druid/pool/dataSource.xml")
public class ClosePoolableStatementTest {
    @Autowired
    private DruidDataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    /**
     * ????????????PreparedStatement????????????SQL
     */
    @Test
    public void testClose() throws Exception {
        insertData(1, "a");
        try {
            insertData(1, "a");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof DuplicateKeyException));
        }
        try {
            insertData(1, "a");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof DuplicateKeyException));
            Assert.assertEquals((-1), e.getMessage().indexOf("closed"));
        }
    }
}

