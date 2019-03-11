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
package net.sf.jsqlparser.statement.delete;


import java.io.StringReader;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class DeleteTest {
    private final CCJSqlParserManager parserManager = new CCJSqlParserManager();

    @Test
    public void testDelete() throws JSQLParserException {
        String statement = "DELETE FROM mytable WHERE mytable.col = 9";
        Delete delete = ((Delete) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals("mytable", delete.getTable().getName());
        Assert.assertEquals(statement, ("" + delete));
    }

    @Test
    public void testDeleteWhereProblem1() throws JSQLParserException {
        String stmt = "DELETE FROM tablename WHERE a = 1 AND b = 1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testDeleteWithLimit() throws JSQLParserException {
        String stmt = "DELETE FROM tablename WHERE a = 1 AND b = 1 LIMIT 5";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test(expected = JSQLParserException.class)
    public void testDeleteDoesNotAllowLimitOffset() throws JSQLParserException {
        String statement = "DELETE FROM table1 WHERE A.cod_table = 'YYY' LIMIT 3,4";
        parserManager.parse(new StringReader(statement));
    }

    @Test
    public void testDeleteWithOrderBy() throws JSQLParserException {
        String stmt = "DELETE FROM tablename WHERE a = 1 AND b = 1 ORDER BY col";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testDeleteWithOrderByAndLimit() throws JSQLParserException {
        String stmt = "DELETE FROM tablename WHERE a = 1 AND b = 1 ORDER BY col LIMIT 10";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testDeleteFromTableUsingInnerJoinToAnotherTable() throws JSQLParserException {
        String stmt = "DELETE Table1 FROM Table1 INNER JOIN Table2 ON Table1.ID = Table2.ID";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testDeleteFromTableUsingLeftJoinToAnotherTable() throws JSQLParserException {
        String stmt = "DELETE g FROM Table1 AS g LEFT JOIN Table2 ON Table1.ID = Table2.ID";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testDeleteFromTableUsingInnerJoinToAnotherTableWithAlias() throws JSQLParserException {
        String stmt = "DELETE gc FROM guide_category AS gc LEFT JOIN guide AS g ON g.id_guide = gc.id_guide WHERE g.title IS NULL LIMIT 5";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }
}

