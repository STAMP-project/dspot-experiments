package com.alibaba.druid.test;


import javax.sql.DataSource;
import junit.framework.TestCase;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;


public class NutzTransactionTest extends TestCase {
    private DataSource dataSource;

    public void test_trans() throws Exception {
        Dao dao = new NutDao(dataSource);
        dao.clear("test");
        // doTran1(dao);
        doTran2(dao);
    }
}

