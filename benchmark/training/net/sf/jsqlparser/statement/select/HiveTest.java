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


public class HiveTest {
    @Test
    public void testLeftSemiJoin() throws Exception {
        String sql;
        Statement statement;
        sql = "SELECT\n" + (((("    Something\n" + "FROM\n") + "    Sometable\n") + "LEFT SEMI JOIN\n") + "    Othertable\n");
        statement = CCJSqlParserUtil.parse(sql);
        System.out.println(statement.toString());
        Select select = ((Select) (statement));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals(1, plainSelect.getJoins().size());
        Assert.assertEquals("Othertable", getFullyQualifiedName());
        Assert.assertTrue(plainSelect.getJoins().get(0).isLeft());
        Assert.assertTrue(plainSelect.getJoins().get(0).isSemi());
        TestUtils.assertStatementCanBeDeparsedAs(select, sql, true);
        TestUtils.assertSqlCanBeParsedAndDeparsed(sql, true);
    }
}

