/**
 * -
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.statement.select;


import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class KSQLTest {
    @Test
    public void testKSQLWindowedJoin() throws Exception {
        String sql;
        Statement statement;
        sql = "SELECT *\n" + ((("FROM table1 t1\n" + "INNER JOIN table2 t2\n") + "WITHIN (5 HOURS)\n") + "ON t1.id = t2.id\n");
        statement = CCJSqlParserUtil.parse(sql);
        System.out.println(statement.toString());
        Select select = ((Select) (statement));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals(1, plainSelect.getJoins().size());
        Assert.assertEquals("table2", getFullyQualifiedName());
        Assert.assertTrue(plainSelect.getJoins().get(0).isWindowJoin());
        Assert.assertEquals(5L, plainSelect.getJoins().get(0).getJoinWindow().getDuration());
        Assert.assertEquals("HOURS", plainSelect.getJoins().get(0).getJoinWindow().getTimeUnit().toString());
        Assert.assertFalse(plainSelect.getJoins().get(0).getJoinWindow().isBeforeAfterWindow());
        TestUtils.assertStatementCanBeDeparsedAs(select, sql, true);
        TestUtils.assertSqlCanBeParsedAndDeparsed(sql, true);
    }

    @Test
    public void testKSQLBeforeAfterWindowedJoin() throws Exception {
        String sql;
        Statement statement;
        sql = "SELECT *\n" + ((("FROM table1 t1\n" + "INNER JOIN table2 t2\n") + "WITHIN (2 MINUTES, 5 MINUTES)\n") + "ON t1.id = t2.id\n");
        statement = CCJSqlParserUtil.parse(sql);
        System.out.println(statement.toString());
        Select select = ((Select) (statement));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals(1, plainSelect.getJoins().size());
        Assert.assertEquals("table2", getFullyQualifiedName());
        Assert.assertTrue(plainSelect.getJoins().get(0).isWindowJoin());
        Assert.assertEquals(2L, plainSelect.getJoins().get(0).getJoinWindow().getBeforeDuration());
        Assert.assertEquals("MINUTES", plainSelect.getJoins().get(0).getJoinWindow().getBeforeTimeUnit().toString());
        Assert.assertEquals(5L, plainSelect.getJoins().get(0).getJoinWindow().getAfterDuration());
        Assert.assertEquals("MINUTES", plainSelect.getJoins().get(0).getJoinWindow().getAfterTimeUnit().toString());
        Assert.assertTrue(plainSelect.getJoins().get(0).getJoinWindow().isBeforeAfterWindow());
        TestUtils.assertStatementCanBeDeparsedAs(select, sql, true);
        TestUtils.assertSqlCanBeParsedAndDeparsed(sql, true);
    }
}

