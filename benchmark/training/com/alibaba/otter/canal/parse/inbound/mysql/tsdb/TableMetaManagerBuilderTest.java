package com.alibaba.otter.canal.parse.inbound.mysql.tsdb;


import org.junit.Test;
import org.springframework.util.Assert;


/**
 *
 *
 * @author agapple 2017?10?12? ??10:50:00
 * @since 1.0.25
 */
public class TableMetaManagerBuilderTest {
    @Test
    public void testSimple() {
        TableMetaTSDB tableMetaTSDB = TableMetaTSDBBuilder.build("test", "classpath:tsdb/mysql-tsdb.xml");
        Assert.notNull(tableMetaTSDB);
        TableMetaTSDBBuilder.destory("test");
    }
}

