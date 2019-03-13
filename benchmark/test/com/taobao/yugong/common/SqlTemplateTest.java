package com.taobao.yugong.common;


import SqlTemplates.COMMON;
import SqlTemplates.MYSQL;
import com.taobao.yugong.common.db.meta.ColumnMeta;
import java.util.List;
import org.junit.Test;


/**
 *
 *
 * @author agapple 2014?2?25? ??11:38:06
 * @since 1.0.0
 */
public class SqlTemplateTest {
    @Test
    public void testSimple() {
        List<ColumnMeta> columns = buildColumns("col", 10);
        System.out.println(COMMON.makeColumn(columns));
        System.out.println(COMMON.makeInsert(columns));
        System.out.println(COMMON.makeUpdate(columns));
        System.out.println(COMMON.makeWhere(columns));
        System.out.println(COMMON.makeRange("col"));
        System.out.println(COMMON.makeIn("col", 10));
    }

    @Test
    public void testMysql() {
        List<ColumnMeta> columns = buildColumns("col", 10);
        System.out.println(MYSQL.makeColumn(columns));
        System.out.println(MYSQL.makeInsert(columns));
        System.out.println(MYSQL.makeUpdate(columns));
        System.out.println(MYSQL.makeWhere(columns));
        System.out.println(MYSQL.makeRange("col"));
        System.out.println(MYSQL.makeIn("col", 10));
        System.out.println(MYSQL.getSelectSql("schema", "table", new String[]{  }, new String[]{ "cm1", "cm2" }));
        // System.out.println(SqlTemplates.MYSQL.getSelectInSql("schema",
        // "table", new String[] {}, new String[] { "cm1",
        // "cm2" }, 5));
    }
}

