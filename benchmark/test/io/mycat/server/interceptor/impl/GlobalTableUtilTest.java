package io.mycat.server.interceptor.impl;


import junit.framework.Assert;
import org.junit.Test;


public class GlobalTableUtilTest {
    private static final String originSql1 = "CREATE TABLE retl_mark" + ((((("(" + "	ID BIGINT AUTO_INCREMENT,") + "	CHANNEL_ID INT(11),") + "	CHANNEL_INFO varchar(128),") + "	CONSTRAINT RETL_MARK_ID PRIMARY KEY (ID)") + ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");

    private static final String originSql2 = "CREATE TABLE retl_mark" + (((((("(" + "	ID BIGINT AUTO_INCREMENT,") + "	CHANNEL_ID INT(11),") + "	CHANNEL_INFO varchar(128),") + " _MYCAT_OP_TIME int,") + "	CONSTRAINT RETL_MARK_ID PRIMARY KEY (ID)") + ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");

    @Test
    public void addColumnIfCreate() {
        String sql = parseSql(GlobalTableUtilTest.originSql1);
        System.out.println(sql);
        boolean contains = sql.contains("_mycat_op_time ");
        Assert.assertTrue(contains);
        sql = parseSql(GlobalTableUtilTest.originSql2);
        System.out.println(sql);
        Assert.assertFalse(sql.contains("_mycat_op_time int COMMENT '??????????????'"));
    }
}

