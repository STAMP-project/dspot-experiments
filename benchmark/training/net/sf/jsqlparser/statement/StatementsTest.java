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
package net.sf.jsqlparser.statement;


import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.parser.StringProvider;
import net.sf.jsqlparser.statement.select.Select;
import org.junit.Assert;
import org.junit.Test;


public class StatementsTest {
    public StatementsTest() {
    }

    @Test
    public void testStatements() throws JSQLParserException {
        String sqls = "select * from mytable; select * from mytable2;";
        Statements parseStatements = CCJSqlParserUtil.parseStatements(sqls);
        Assert.assertEquals("SELECT * FROM mytable;\nSELECT * FROM mytable2;\n", parseStatements.toString());
        Assert.assertTrue(((parseStatements.getStatements().get(0)) instanceof Select));
        Assert.assertTrue(((parseStatements.getStatements().get(1)) instanceof Select));
    }

    @Test
    public void testStatementsProblem() throws JSQLParserException {
        String sqls = ";;select * from mytable;;select * from mytable2;;;";
        Statements parseStatements = CCJSqlParserUtil.parseStatements(sqls);
        Assert.assertEquals("SELECT * FROM mytable;\nSELECT * FROM mytable2;\n", parseStatements.toString());
        Assert.assertTrue(((parseStatements.getStatements().get(0)) instanceof Select));
        Assert.assertTrue(((parseStatements.getStatements().get(1)) instanceof Select));
    }

    @Test
    public void testStatementsErrorRecovery() throws JSQLParserException, ParseException {
        String sqls = "select * from mytable; select * from;";
        CCJSqlParser parser = new CCJSqlParser(new StringProvider(sqls));
        parser.setErrorRecovery(true);
        Statements parseStatements = parser.Statements();
        Assert.assertEquals(2, parseStatements.getStatements().size());
        Assert.assertTrue(((parseStatements.getStatements().get(0)) instanceof Select));
        Assert.assertNull(parseStatements.getStatements().get(1));
    }

    @Test
    public void testStatementsErrorRecovery2() throws JSQLParserException, ParseException {
        String sqls = "select * from1 table;";
        CCJSqlParser parser = new CCJSqlParser(new StringProvider(sqls));
        parser.setErrorRecovery(true);
        Statements parseStatements = parser.Statements();
        Assert.assertEquals(1, parseStatements.getStatements().size());
        Assert.assertTrue(((parseStatements.getStatements().get(0)) instanceof Select));
        Assert.assertEquals(1, parser.getParseErrors().size());
    }

    @Test
    public void testStatementsErrorRecovery3() throws JSQLParserException, ParseException {
        String sqls = "select * from mytable; select * from;select * from mytable2";
        CCJSqlParser parser = new CCJSqlParser(new StringProvider(sqls));
        parser.setErrorRecovery(true);
        Statements parseStatements = parser.Statements();
        Assert.assertEquals(2, parseStatements.getStatements().size());
        Assert.assertTrue(((parseStatements.getStatements().get(0)) instanceof Select));
        Assert.assertNull(parseStatements.getStatements().get(1));
        Assert.assertEquals(2, parser.getParseErrors().size());
    }
}

