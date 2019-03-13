package com.alibaba.druid.sql.parser;


import junit.framework.TestCase;


/**
 * Created by tianzhen.wtz on 2014/12/26 0026 20:44.
 * ????
 */
public class PGIntervalSQLTest extends TestCase {
    public void testIntervalSQL() {
        String sql1 = "select timestamp '2001-09-28 01:00' + interval '23 hours'";
        String sql1Result = "SELECT TIMESTAMP '2001-09-28 01:00' + INTERVAL '23 hours'";
        equal(sql1, sql1Result);
        String sql2 = "select interval '1 day' - interval '1 hour'";
        String sql2Result = "SELECT INTERVAL '1 day' - INTERVAL '1 hour'";
        equal(sql2, sql2Result);
        String sql3 = "select date_part('month', interval '2 years 3 months')";
        String sql3Result = "SELECT date_part('month', INTERVAL '2 years 3 months')";
        equal(sql3, sql3Result);
    }
}

