package io.mycat.route.parser.druid.impl;


import io.mycat.config.model.SchemaConfig;
import io.mycat.route.parser.druid.DruidParser;
import org.junit.Assert;
import org.junit.Test;


/**
 * sql??????
 *
 * @author lian
 * @unknown 2016?12?2?
 */
public class DefaultDruidParserTest {
    private SchemaConfig schema;

    private DruidParser druidParser;

    @Test
    public void testParser() throws Exception {
        Assert.assertArrayEquals(getParseTables("select id as id from company t;"), getArr("company".toUpperCase()));
        Assert.assertArrayEquals(getParseTables("select 1 from (select 1 from company) company;"), getArr("company".toUpperCase()));
    }
}

