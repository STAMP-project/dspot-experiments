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
package net.sf.jsqlparser.statement.create;


import java.io.StringReader;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class CreateIndexTest {
    private final CCJSqlParserManager parserManager = new CCJSqlParserManager();

    @Test
    public void testCreateIndex() throws JSQLParserException {
        String statement = "CREATE INDEX myindex ON mytab (mycol, mycol2)";
        CreateIndex createIndex = ((CreateIndex) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals(2, createIndex.getIndex().getColumnsNames().size());
        Assert.assertEquals("myindex", createIndex.getIndex().getName());
        Assert.assertNull(createIndex.getIndex().getType());
        Assert.assertEquals("mytab", createIndex.getTable().getFullyQualifiedName());
        Assert.assertEquals("mycol", createIndex.getIndex().getColumnsNames().get(0));
        Assert.assertEquals(statement, ("" + createIndex));
    }

    @Test
    public void testCreateIndex2() throws JSQLParserException {
        String statement = "CREATE mytype INDEX myindex ON mytab (mycol, mycol2)";
        CreateIndex createIndex = ((CreateIndex) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals(2, createIndex.getIndex().getColumnsNames().size());
        Assert.assertEquals("myindex", createIndex.getIndex().getName());
        Assert.assertEquals("mytype", createIndex.getIndex().getType());
        Assert.assertEquals("mytab", createIndex.getTable().getFullyQualifiedName());
        Assert.assertEquals("mycol2", createIndex.getIndex().getColumnsNames().get(1));
        Assert.assertEquals(statement, ("" + createIndex));
    }

    @Test
    public void testCreateIndex3() throws JSQLParserException {
        String statement = "CREATE mytype INDEX myindex ON mytab (mycol ASC, mycol2, mycol3)";
        CreateIndex createIndex = ((CreateIndex) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals(3, createIndex.getIndex().getColumnsNames().size());
        Assert.assertEquals("myindex", createIndex.getIndex().getName());
        Assert.assertEquals("mytype", createIndex.getIndex().getType());
        Assert.assertEquals("mytab", createIndex.getTable().getFullyQualifiedName());
        Assert.assertEquals("mycol3", createIndex.getIndex().getColumnsNames().get(2));
    }

    @Test
    public void testCreateIndex4() throws JSQLParserException {
        String statement = "CREATE mytype INDEX myindex ON mytab (mycol ASC, mycol2 (75), mycol3)";
        CreateIndex createIndex = ((CreateIndex) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals(3, createIndex.getIndex().getColumnsNames().size());
        Assert.assertEquals("myindex", createIndex.getIndex().getName());
        Assert.assertEquals("mytype", createIndex.getIndex().getType());
        Assert.assertEquals("mytab", createIndex.getTable().getFullyQualifiedName());
        Assert.assertEquals("mycol3", createIndex.getIndex().getColumnsNames().get(2));
    }

    @Test
    public void testCreateIndex5() throws JSQLParserException {
        String statement = "CREATE mytype INDEX myindex ON mytab (mycol ASC, mycol2 (75), mycol3) mymodifiers";
        CreateIndex createIndex = ((CreateIndex) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals(3, createIndex.getIndex().getColumnsNames().size());
        Assert.assertEquals("myindex", createIndex.getIndex().getName());
        Assert.assertEquals("mytype", createIndex.getIndex().getType());
        Assert.assertEquals("mytab", createIndex.getTable().getFullyQualifiedName());
        Assert.assertEquals("mycol3", createIndex.getIndex().getColumnsNames().get(2));
    }

    @Test
    public void testCreateIndex6() throws JSQLParserException {
        String stmt = "CREATE INDEX myindex ON mytab (mycol, mycol2)";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }
}

