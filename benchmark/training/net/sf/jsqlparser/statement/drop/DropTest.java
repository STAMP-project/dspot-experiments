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
package net.sf.jsqlparser.statement.drop;


import java.io.StringReader;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class DropTest {
    private final CCJSqlParserManager parserManager = new CCJSqlParserManager();

    @Test
    public void testDrop() throws JSQLParserException {
        String statement = "DROP TABLE mytab";
        Drop drop = ((Drop) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals("TABLE", drop.getType());
        Assert.assertEquals("mytab", drop.getName().getFullyQualifiedName());
        Assert.assertEquals(statement, ("" + drop));
        statement = "DROP INDEX myindex CASCADE";
        drop = ((Drop) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals("INDEX", drop.getType());
        Assert.assertEquals("myindex", drop.getName().getFullyQualifiedName());
        Assert.assertEquals("CASCADE", drop.getParameters().get(0));
        Assert.assertEquals(statement, ("" + drop));
    }

    @Test
    public void testDrop2() throws JSQLParserException {
        Drop drop = ((Drop) (parserManager.parse(new StringReader("DROP TABLE \"testtable\""))));
        Assert.assertEquals("TABLE", drop.getType());
        Assert.assertEquals("\"testtable\"", drop.getName().getFullyQualifiedName());
    }

    @Test
    public void testDropIfExists() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("DROP TABLE IF EXISTS my_table");
    }

    @Test
    public void testDropRestrictIssue510() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("DROP TABLE TABLE2 RESTRICT");
    }

    @Test
    public void testDropViewIssue545() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("DROP VIEW myview");
    }

    @Test
    public void testDropViewIssue545_2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("DROP VIEW IF EXISTS myview");
    }
}

