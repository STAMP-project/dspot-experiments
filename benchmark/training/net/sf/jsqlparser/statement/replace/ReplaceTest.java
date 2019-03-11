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
package net.sf.jsqlparser.statement.replace;


import java.io.StringReader;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class ReplaceTest {
    private static final CCJSqlParserManager PARSER_MANAGER = new CCJSqlParserManager();

    @Test
    public void testReplaceSyntax1() throws JSQLParserException {
        String statement = "REPLACE mytable SET col1='as', col2=?, col3=565";
        Replace replace = ((Replace) (ReplaceTest.PARSER_MANAGER.parse(new StringReader(statement))));
        Assert.assertEquals("mytable", replace.getTable().getName());
        Assert.assertEquals(3, replace.getColumns().size());
        Assert.assertEquals("col1", getColumnName());
        Assert.assertEquals("col2", getColumnName());
        Assert.assertEquals("col3", getColumnName());
        Assert.assertEquals("as", getValue());
        Assert.assertTrue(((replace.getExpressions().get(1)) instanceof JdbcParameter));
        Assert.assertEquals(565, getValue());
        Assert.assertEquals(statement, ("" + replace));
    }

    @Test
    public void testReplaceSyntax2() throws JSQLParserException {
        String statement = "REPLACE mytable (col1, col2, col3) VALUES ('as', ?, 565)";
        Replace replace = ((Replace) (ReplaceTest.PARSER_MANAGER.parse(new StringReader(statement))));
        Assert.assertEquals("mytable", replace.getTable().getName());
        Assert.assertEquals(3, replace.getColumns().size());
        Assert.assertEquals("col1", getColumnName());
        Assert.assertEquals("col2", getColumnName());
        Assert.assertEquals("col3", getColumnName());
        Assert.assertEquals("as", getValue());
        Assert.assertTrue(((getExpressions().get(1)) instanceof JdbcParameter));
        Assert.assertEquals(565, getValue());
        Assert.assertEquals(statement, ("" + replace));
    }

    @Test
    public void testReplaceSyntax3() throws JSQLParserException {
        String statement = "REPLACE mytable (col1, col2, col3) SELECT * FROM mytable3";
        Replace replace = ((Replace) (ReplaceTest.PARSER_MANAGER.parse(new StringReader(statement))));
        Assert.assertEquals("mytable", replace.getTable().getName());
        Assert.assertEquals(3, replace.getColumns().size());
        Assert.assertEquals("col1", getColumnName());
        Assert.assertEquals("col2", getColumnName());
        Assert.assertEquals("col3", getColumnName());
        Assert.assertTrue(((replace.getItemsList()) instanceof SubSelect));
    }

    @Test
    public void testProblemReplaceParseDeparse() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("REPLACE a_table (ID, A, B) SELECT A_ID, A, B FROM b_table", false);
    }

    @Test
    public void testProblemMissingIntoIssue389() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("REPLACE INTO mytable (key, data) VALUES (1, \"aaa\")");
    }

    @Test
    public void testMultipleValues() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("REPLACE INTO mytable (col1, col2, col3) VALUES (1, \"aaa\", now()), (2, \"bbb\", now())");
    }
}

