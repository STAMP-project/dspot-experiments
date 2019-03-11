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


import First.Keyword.FIRST;
import First.Keyword.LIMIT;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import net.sf.jsqlparser.test.TestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


public class SelectTest {
    @Rule
    public TestName name = new TestName();

    private final CCJSqlParserManager parserManager = new CCJSqlParserManager();

    // From statement multipart
    @Test
    public void testMultiPartTableNameWithServerNameAndDatabaseNameAndSchemaName() throws Exception {
        final String statement = "SELECT columnName FROM [server-name\\server-instance].databaseName.schemaName.tableName";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testMultiPartTableNameWithServerNameAndDatabaseName() throws Exception {
        final String statement = "SELECT columnName FROM [server-name\\server-instance].databaseName..tableName";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testMultiPartTableNameWithServerNameAndSchemaName() throws Exception {
        final String statement = "SELECT columnName FROM [server-name\\server-instance]..schemaName.tableName";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testMultiPartTableNameWithServerProblem() throws Exception {
        final String statement = "SELECT * FROM LINK_100.htsac.dbo.t_transfer_num a";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testMultiPartTableNameWithServerName() throws Exception {
        final String statement = "SELECT columnName FROM [server-name\\server-instance]...tableName";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testMultiPartTableNameWithDatabaseNameAndSchemaName() throws Exception {
        final String statement = "SELECT columnName FROM databaseName.schemaName.tableName";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testMultiPartTableNameWithDatabaseName() throws Exception {
        final String statement = "SELECT columnName FROM databaseName..tableName";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testMultiPartTableNameWithSchemaName() throws Exception {
        final String statement = "SELECT columnName FROM schemaName.tableName";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testMultiPartTableNameWithColumnName() throws Exception {
        final String statement = "SELECT columnName FROM tableName";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    // Select statement statement multipart
    @Test
    public void testMultiPartColumnNameWithDatabaseNameAndSchemaNameAndTableName() throws Exception {
        final String statement = "SELECT databaseName.schemaName.tableName.columnName FROM tableName";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testMultiPartColumnNameWithDatabaseNameAndTableName() throws Exception {
        final String statement = "SELECT databaseName..tableName.columnName FROM tableName";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        checkMultipartIdentifier(select, "columnName", "databaseName..tableName.columnName");
    }

    @Test
    public void testMultiPartColumnNameWithSchemaNameAndTableName() throws Exception {
        final String statement = "SELECT schemaName.tableName.columnName FROM tableName";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        checkMultipartIdentifier(select, "columnName", "schemaName.tableName.columnName");
    }

    @Test
    public void testMultiPartColumnNameWithTableName() throws Exception {
        final String statement = "SELECT tableName.columnName FROM tableName";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        checkMultipartIdentifier(select, "columnName", "tableName.columnName");
    }

    @Test
    public void testMultiPartColumnName() throws Exception {
        final String statement = "SELECT columnName FROM tableName";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        checkMultipartIdentifier(select, "columnName", "columnName");
    }

    @Test
    public void testAllColumnsFromTable() throws Exception {
        final String statement = "SELECT tableName.* FROM tableName";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        Assert.assertTrue(((getSelectItems().get(0)) instanceof AllTableColumns));
    }

    @Test
    public void testSimpleSigns() throws JSQLParserException {
        final String statement = "SELECT +1, -1 FROM tableName";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testSimpleAdditionsAndSubtractionsWithSigns() throws JSQLParserException {
        final String statement = "SELECT 1 - 1, 1 + 1, -1 - 1, -1 + 1, +1 + 1, +1 - 1 FROM tableName";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testOperationsWithSigns() throws JSQLParserException {
        Expression expr = CCJSqlParserUtil.parseExpression("1 - -1");
        Assert.assertEquals("1 - -1", expr.toString());
        Assert.assertTrue((expr instanceof Subtraction));
        Subtraction sub = ((Subtraction) (expr));
        Assert.assertTrue(((sub.getLeftExpression()) instanceof LongValue));
        Assert.assertTrue(((sub.getRightExpression()) instanceof SignedExpression));
        SignedExpression sexpr = ((SignedExpression) (sub.getRightExpression()));
        Assert.assertEquals('-', sexpr.getSign());
        Assert.assertEquals("1", sexpr.getExpression().toString());
    }

    @Test
    public void testSignedColumns() throws JSQLParserException {
        final String statement = "SELECT -columnName, +columnName, +(columnName), -(columnName) FROM tableName";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testSigns() throws Exception {
        final String statement = "SELECT (-(1)), -(1), (-(columnName)), -(columnName), (-1), -1, (-columnName), -columnName FROM tableName";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testLimit() throws JSQLParserException {
        String statement = "SELECT * FROM mytable WHERE mytable.col = 9 LIMIT 3, ?";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        Expression offset = getLimit().getOffset();
        Expression rowCount = getLimit().getRowCount();
        Assert.assertEquals(3, getValue());
        Assert.assertTrue((rowCount instanceof JdbcParameter));
        Assert.assertFalse(getLimit().isLimitAll());
        // toString uses standard syntax
        statement = "SELECT * FROM mytable WHERE mytable.col = 9 LIMIT ? OFFSET 3";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
        statement = "SELECT * FROM mytable WHERE mytable.col = 9 OFFSET ?";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        Assert.assertNull(getLimit());
        Assert.assertNotNull(getOffset());
        Assert.assertEquals("?", getOffset().getOffsetJdbcParameter().toString());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        statement = "(SELECT * FROM mytable WHERE mytable.col = 9 OFFSET ?) UNION " + "(SELECT * FROM mytable2 WHERE mytable2.col = 9 OFFSET ?) LIMIT 3, 4";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        SetOperationList setList = ((SetOperationList) (select.getSelectBody()));
        offset = getOffset();
        rowCount = setList.getLimit().getRowCount();
        Assert.assertEquals(3, getValue());
        Assert.assertEquals(4, getValue());
        // toString uses standard syntax
        statement = "(SELECT * FROM mytable WHERE mytable.col = 9 OFFSET ?) UNION " + "(SELECT * FROM mytable2 WHERE mytable2.col = 9 OFFSET ?) LIMIT 4 OFFSET 3";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
        statement = "(SELECT * FROM mytable WHERE mytable.col = 9 OFFSET ?) UNION ALL " + ("(SELECT * FROM mytable2 WHERE mytable2.col = 9 OFFSET ?) UNION ALL " + "(SELECT * FROM mytable3 WHERE mytable4.col = 9 OFFSET ?) LIMIT 4 OFFSET 3");
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testLimit2() throws JSQLParserException {
        String statement = "SELECT * FROM mytable WHERE mytable.col = 9 LIMIT 3, ?";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        Expression offset = getLimit().getOffset();
        Expression rowCount = getLimit().getRowCount();
        Assert.assertEquals(3, getValue());
        Assert.assertNotNull(getIndex());
        Assert.assertFalse(isUseFixedIndex());
        Assert.assertFalse(getLimit().isLimitAll());
        Assert.assertFalse(getLimit().isLimitNull());
        // toString uses standard syntax
        statement = "SELECT * FROM mytable WHERE mytable.col = 9 LIMIT ? OFFSET 3";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
        statement = "SELECT * FROM mytable WHERE mytable.col = 9 LIMIT NULL OFFSET 3";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        offset = getOffset();
        rowCount = getLimit().getRowCount();
        Assert.assertNull(offset);
        Assert.assertNull(rowCount);
        Assert.assertEquals(3, getOffset());
        Assert.assertFalse(getLimit().isLimitAll());
        Assert.assertTrue(getLimit().isLimitNull());
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
        statement = "SELECT * FROM mytable WHERE mytable.col = 9 LIMIT 0 OFFSET 3";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        offset = getOffset();
        rowCount = getLimit().getRowCount();
        Assert.assertNull(offset);
        Assert.assertEquals(0, getValue());
        Assert.assertEquals(3, getOffset());
        Assert.assertFalse(getLimit().isLimitAll());
        Assert.assertFalse(getLimit().isLimitNull());
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
        statement = "SELECT * FROM mytable WHERE mytable.col = 9 OFFSET ?";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        Assert.assertNull(getLimit());
        Assert.assertNotNull(getOffset());
        Assert.assertEquals("?", getOffset().getOffsetJdbcParameter().toString());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        statement = "(SELECT * FROM mytable WHERE mytable.col = 9 OFFSET ?) UNION " + "(SELECT * FROM mytable2 WHERE mytable2.col = 9 OFFSET ?) LIMIT 3, 4";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        SetOperationList setList = ((SetOperationList) (select.getSelectBody()));
        Assert.assertEquals(3, getValue());
        Assert.assertEquals(4, getValue());
        // toString uses standard syntax
        statement = "(SELECT * FROM mytable WHERE mytable.col = 9 OFFSET ?) UNION " + "(SELECT * FROM mytable2 WHERE mytable2.col = 9 OFFSET ?) LIMIT 4 OFFSET 3";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
        statement = "(SELECT * FROM mytable WHERE mytable.col = 9 OFFSET ?) UNION ALL " + ("(SELECT * FROM mytable2 WHERE mytable2.col = 9 OFFSET ?) UNION ALL " + "(SELECT * FROM mytable3 WHERE mytable4.col = 9 OFFSET ?) LIMIT 4 OFFSET 3");
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testLimit3() throws JSQLParserException {
        String statement = "SELECT * FROM mytable WHERE mytable.col = 9 LIMIT ?1, 2";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        Expression offset = getLimit().getOffset();
        Expression rowCount = getLimit().getRowCount();
        Assert.assertEquals(1, ((int) (getIndex())));
        Assert.assertEquals(2, getValue());
        Assert.assertFalse(getLimit().isLimitAll());
        statement = "SELECT * FROM mytable WHERE mytable.col = 9 LIMIT 1, ?2";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        offset = getOffset();
        rowCount = getLimit().getRowCount();
        Assert.assertEquals(1, getValue());
        Assert.assertEquals(2, ((int) (getIndex())));
        Assert.assertFalse(getLimit().isLimitAll());
        statement = "SELECT * FROM mytable WHERE mytable.col = 9 LIMIT ?1, ?2";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        offset = getOffset();
        rowCount = getLimit().getRowCount();
        Assert.assertEquals(2, ((int) (getIndex())));
        Assert.assertEquals(1, ((int) (getIndex())));
        Assert.assertFalse(getLimit().isLimitAll());
        statement = "SELECT * FROM mytable WHERE mytable.col = 9 LIMIT 1, ?";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        offset = getOffset();
        rowCount = getLimit().getRowCount();
        Assert.assertEquals(1, getValue());
        Assert.assertNotNull(getIndex());
        Assert.assertFalse(isUseFixedIndex());
        Assert.assertFalse(getLimit().isLimitAll());
        statement = "SELECT * FROM mytable WHERE mytable.col = 9 LIMIT ?, ?";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        offset = getOffset();
        rowCount = getLimit().getRowCount();
        Assert.assertNotNull(getIndex());
        Assert.assertFalse(isUseFixedIndex());
        Assert.assertNotNull(getIndex());
        Assert.assertFalse(isUseFixedIndex());
        Assert.assertFalse(getLimit().isLimitAll());
        statement = "SELECT * FROM mytable WHERE mytable.col = 9 LIMIT ?1";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        offset = getOffset();
        rowCount = getLimit().getRowCount();
        Assert.assertNull(offset);
        Assert.assertEquals(1, getIndex().intValue());
        Assert.assertFalse(getLimit().isLimitAll());
    }

    @Test
    public void testLimit4() throws JSQLParserException {
        String statement = "SELECT * FROM mytable WHERE mytable.col = 9 LIMIT :some_name, 2";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        Expression offset = getLimit().getOffset();
        Expression rowCount = getLimit().getRowCount();
        Assert.assertEquals("some_name", getName());
        Assert.assertEquals(2, getValue());
        Assert.assertFalse(getLimit().isLimitAll());
        statement = "SELECT * FROM mytable WHERE mytable.col = 9 LIMIT 1, :some_name";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        offset = getOffset();
        rowCount = getLimit().getRowCount();
        Assert.assertEquals(1, getValue());
        Assert.assertEquals("some_name", getName());
        Assert.assertFalse(getLimit().isLimitAll());
        statement = "SELECT * FROM mytable WHERE mytable.col = 9 LIMIT :name1, :name2";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        offset = getOffset();
        rowCount = getLimit().getRowCount();
        Assert.assertEquals("name2", getName());
        Assert.assertEquals("name1", getName());
        Assert.assertFalse(getLimit().isLimitAll());
        statement = "SELECT * FROM mytable WHERE mytable.col = 9 LIMIT ?1, :name1";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        offset = getOffset();
        rowCount = getLimit().getRowCount();
        Assert.assertEquals(1, ((int) (getIndex())));
        Assert.assertEquals("name1", getName());
        Assert.assertFalse(getLimit().isLimitAll());
        statement = "SELECT * FROM mytable WHERE mytable.col = 9 LIMIT :name1, ?1";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        offset = getOffset();
        rowCount = getLimit().getRowCount();
        Assert.assertEquals(1, ((int) (getIndex())));
        Assert.assertEquals("name1", getName());
        Assert.assertFalse(getLimit().isLimitAll());
        statement = "SELECT * FROM mytable WHERE mytable.col = 9 LIMIT :param_name";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        offset = getOffset();
        rowCount = getLimit().getRowCount();
        Assert.assertNull(offset);
        Assert.assertEquals("param_name", getName());
        Assert.assertFalse(getLimit().isLimitAll());
    }

    @Test
    public void testLimitSqlServer1() throws JSQLParserException {
        String statement = "SELECT * FROM mytable WHERE mytable.col = 9 ORDER BY mytable.id OFFSET 3 ROWS FETCH NEXT 5 ROWS ONLY";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        Assert.assertNotNull(getOffset());
        Assert.assertEquals("ROWS", getOffset().getOffsetParam());
        Assert.assertNotNull(getFetch());
        Assert.assertEquals("ROWS", getFetch().getFetchParam());
        Assert.assertFalse(getFetch().isFetchParamFirst());
        Assert.assertNull(getOffset().getOffsetJdbcParameter());
        Assert.assertNull(getFetch().getFetchJdbcParameter());
        Assert.assertEquals(3, getOffset());
        Assert.assertEquals(5, getFetch().getRowCount());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testLimitSqlServer2() throws JSQLParserException {
        // Alternative with the other keywords
        String statement = "SELECT * FROM mytable WHERE mytable.col = 9 ORDER BY mytable.id OFFSET 3 ROW FETCH FIRST 5 ROW ONLY";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        Assert.assertNotNull(getOffset());
        Assert.assertNotNull(getFetch());
        Assert.assertEquals("ROW", getOffset().getOffsetParam());
        Assert.assertEquals("ROW", getFetch().getFetchParam());
        Assert.assertTrue(getFetch().isFetchParamFirst());
        Assert.assertEquals(3, getOffset());
        Assert.assertEquals(5, getFetch().getRowCount());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testLimitSqlServer3() throws JSQLParserException {
        // Query with no Fetch
        String statement = "SELECT * FROM mytable WHERE mytable.col = 9 ORDER BY mytable.id OFFSET 3 ROWS";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        Assert.assertNotNull(getOffset());
        Assert.assertNull(getFetch());
        Assert.assertEquals("ROWS", getOffset().getOffsetParam());
        Assert.assertEquals(3, getOffset());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testLimitSqlServer4() throws JSQLParserException {
        // For Oracle syntax, query with no offset
        String statement = "SELECT * FROM mytable WHERE mytable.col = 9 ORDER BY mytable.id FETCH NEXT 5 ROWS ONLY";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        Assert.assertNull(getOffset());
        Assert.assertNotNull(getFetch());
        Assert.assertEquals("ROWS", getFetch().getFetchParam());
        Assert.assertFalse(getFetch().isFetchParamFirst());
        Assert.assertEquals(5, getFetch().getRowCount());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testLimitSqlServerJdbcParameters() throws JSQLParserException {
        String statement = "SELECT * FROM mytable WHERE mytable.col = 9 ORDER BY mytable.id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        Assert.assertNotNull(getOffset());
        Assert.assertEquals("ROWS", getOffset().getOffsetParam());
        Assert.assertNotNull(getFetch());
        Assert.assertEquals("ROWS", getFetch().getFetchParam());
        Assert.assertFalse(getFetch().isFetchParamFirst());
        Assert.assertEquals("?", getOffset().getOffsetJdbcParameter().toString());
        Assert.assertEquals("?", getFetch().getFetchJdbcParameter().toString());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testLimitPR404() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM mytable WHERE mytable.col = 9 LIMIT ?1");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM mytable WHERE mytable.col = 9 LIMIT :param_name");
    }

    @Test
    public void testLimitOffsetIssue462() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM mytable LIMIT ?1");
    }

    @Test
    public void testLimitOffsetIssue462_2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM mytable LIMIT ?1 OFFSET ?2");
    }

    @Test
    public void testTop() throws JSQLParserException {
        String statement = "SELECT TOP 3 * FROM mytable WHERE mytable.col = 9";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals(3, getValue());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        statement = "select top 5 foo from bar";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals(5, getValue());
    }

    @Test
    public void testTopWithParenthesis() throws JSQLParserException {
        final String firstColumnName = "alias.columnName1";
        final String secondColumnName = "alias.columnName2";
        final String statement = ((((("SELECT TOP (5) PERCENT " + firstColumnName) + ", ") + secondColumnName) + " FROM schemaName.tableName alias ORDER BY ") + secondColumnName) + " DESC";
        final Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        final PlainSelect selectBody = ((PlainSelect) (select.getSelectBody()));
        final Top top = selectBody.getTop();
        Assert.assertEquals("5", top.getExpression().toString());
        Assert.assertTrue(top.hasParenthesis());
        Assert.assertTrue(top.isPercentage());
        final List<SelectItem> selectItems = selectBody.getSelectItems();
        Assert.assertEquals(2, selectItems.size());
        Assert.assertEquals(firstColumnName, selectItems.get(0).toString());
        Assert.assertEquals(secondColumnName, selectItems.get(1).toString());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testTopWithJdbcParameter() throws JSQLParserException {
        String statement = "SELECT TOP ?1 * FROM mytable WHERE mytable.col = 9";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals(1, ((int) (getIndex())));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        statement = "select top :name1 foo from bar";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals("name1", getName());
        statement = "select top ? foo from bar";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        Assert.assertNotNull(getIndex());
        Assert.assertFalse(isUseFixedIndex());
    }

    @Test
    public void testSkip() throws JSQLParserException {
        final String firstColumnName = "alias.columnName1";
        final String secondColumnName = "alias.columnName2";
        final String statement = ((((("SELECT SKIP 5 " + firstColumnName) + ", ") + secondColumnName) + " FROM schemaName.tableName alias ORDER BY ") + secondColumnName) + " DESC";
        final Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        final PlainSelect selectBody = ((PlainSelect) (select.getSelectBody()));
        final Skip skip = selectBody.getSkip();
        Assert.assertEquals(((long) (5)), ((long) (skip.getRowCount())));
        Assert.assertNull(skip.getJdbcParameter());
        Assert.assertNull(skip.getVariable());
        final List<SelectItem> selectItems = selectBody.getSelectItems();
        Assert.assertEquals(2, selectItems.size());
        Assert.assertEquals(firstColumnName, selectItems.get(0).toString());
        Assert.assertEquals(secondColumnName, selectItems.get(1).toString());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        final String statement2 = "SELECT SKIP skipVar c1, c2 FROM t";
        final Select select2 = ((Select) (parserManager.parse(new StringReader(statement2))));
        final PlainSelect selectBody2 = ((PlainSelect) (select2.getSelectBody()));
        final Skip skip2 = selectBody2.getSkip();
        Assert.assertNull(skip2.getRowCount());
        Assert.assertNull(skip2.getJdbcParameter());
        Assert.assertEquals("skipVar", skip2.getVariable());
        final List<SelectItem> selectItems2 = selectBody2.getSelectItems();
        Assert.assertEquals(2, selectItems2.size());
        Assert.assertEquals("c1", selectItems2.get(0).toString());
        Assert.assertEquals("c2", selectItems2.get(1).toString());
        TestUtils.assertStatementCanBeDeparsedAs(select2, statement2);
    }

    @Test
    public void testFirst() throws JSQLParserException {
        final String firstColumnName = "alias.columnName1";
        final String secondColumnName = "alias.columnName2";
        final String statement = ((((("SELECT FIRST 5 " + firstColumnName) + ", ") + secondColumnName) + " FROM schemaName.tableName alias ORDER BY ") + secondColumnName) + " DESC";
        final Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        final PlainSelect selectBody = ((PlainSelect) (select.getSelectBody()));
        final First limit = selectBody.getFirst();
        Assert.assertEquals(((long) (5)), ((long) (limit.getRowCount())));
        Assert.assertNull(limit.getJdbcParameter());
        Assert.assertEquals(FIRST, limit.getKeyword());
        final List<SelectItem> selectItems = selectBody.getSelectItems();
        Assert.assertEquals(2, selectItems.size());
        Assert.assertEquals(firstColumnName, selectItems.get(0).toString());
        Assert.assertEquals(secondColumnName, selectItems.get(1).toString());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        final String statement2 = "SELECT FIRST firstVar c1, c2 FROM t";
        final Select select2 = ((Select) (parserManager.parse(new StringReader(statement2))));
        final PlainSelect selectBody2 = ((PlainSelect) (select2.getSelectBody()));
        final First first2 = selectBody2.getFirst();
        Assert.assertNull(first2.getRowCount());
        Assert.assertNull(first2.getJdbcParameter());
        Assert.assertEquals("firstVar", first2.getVariable());
        final List<SelectItem> selectItems2 = selectBody2.getSelectItems();
        Assert.assertEquals(2, selectItems2.size());
        Assert.assertEquals("c1", selectItems2.get(0).toString());
        Assert.assertEquals("c2", selectItems2.get(1).toString());
        TestUtils.assertStatementCanBeDeparsedAs(select2, statement2);
    }

    @Test
    public void testFirstWithKeywordLimit() throws JSQLParserException {
        final String firstColumnName = "alias.columnName1";
        final String secondColumnName = "alias.columnName2";
        final String statement = ((((("SELECT LIMIT ? " + firstColumnName) + ", ") + secondColumnName) + " FROM schemaName.tableName alias ORDER BY ") + secondColumnName) + " DESC";
        final Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        final PlainSelect selectBody = ((PlainSelect) (select.getSelectBody()));
        final First limit = selectBody.getFirst();
        Assert.assertNull(limit.getRowCount());
        Assert.assertNotNull(limit.getJdbcParameter());
        Assert.assertNotNull(getIndex());
        Assert.assertFalse(isUseFixedIndex());
        Assert.assertEquals(LIMIT, limit.getKeyword());
        final List<SelectItem> selectItems = selectBody.getSelectItems();
        Assert.assertEquals(2, selectItems.size());
        Assert.assertEquals(firstColumnName, selectItems.get(0).toString());
        Assert.assertEquals(secondColumnName, selectItems.get(1).toString());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testSkipFirst() throws JSQLParserException {
        final String statement = "SELECT SKIP ?1 FIRST f1 c1, c2 FROM t1";
        final Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        final PlainSelect selectBody = ((PlainSelect) (select.getSelectBody()));
        final Skip skip = selectBody.getSkip();
        Assert.assertNotNull(skip.getJdbcParameter());
        Assert.assertNotNull(getIndex());
        Assert.assertTrue(isUseFixedIndex());
        Assert.assertEquals(((int) (1)), ((int) (getIndex())));
        Assert.assertNull(skip.getVariable());
        final First first = selectBody.getFirst();
        Assert.assertNull(first.getJdbcParameter());
        Assert.assertNull(first.getRowCount());
        Assert.assertEquals("f1", first.getVariable());
        final List<SelectItem> selectItems = selectBody.getSelectItems();
        Assert.assertEquals(2, selectItems.size());
        Assert.assertEquals("c1", selectItems.get(0).toString());
        Assert.assertEquals("c2", selectItems.get(1).toString());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testSelectItems() throws JSQLParserException {
        String statement = "SELECT myid AS MYID, mycol, tab.*, schema.tab.*, mytab.mycol2, myschema.mytab.mycol, myschema.mytab.* FROM mytable WHERE mytable.col = 9";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        final List<SelectItem> selectItems = plainSelect.getSelectItems();
        Assert.assertEquals("MYID", getName());
        Assert.assertEquals("mycol", getColumnName());
        Assert.assertEquals("tab", getName());
        Assert.assertEquals("schema", getTable().getSchemaName());
        Assert.assertEquals("schema.tab", getFullyQualifiedName());
        Assert.assertEquals("mytab.mycol2", getFullyQualifiedName());
        Assert.assertEquals("myschema.mytab.mycol", getFullyQualifiedName());
        Assert.assertEquals("myschema.mytab", getFullyQualifiedName());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        statement = "SELECT myid AS MYID, (SELECT MAX(ID) AS myid2 FROM mytable2) AS myalias FROM mytable WHERE mytable.col = 9";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals("myalias", getName());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        statement = "SELECT (myid + myid2) AS MYID FROM mytable WHERE mytable.col = 9";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals("MYID", getName());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testUnion() throws JSQLParserException {
        String statement = "SELECT * FROM mytable WHERE mytable.col = 9 UNION " + ("SELECT * FROM mytable3 WHERE mytable3.col = ? UNION " + "SELECT * FROM mytable2 LIMIT 3, 4");
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        SetOperationList setList = ((SetOperationList) (select.getSelectBody()));
        Assert.assertEquals(3, setList.getSelects().size());
        Assert.assertEquals("mytable", getName());
        Assert.assertEquals("mytable3", getName());
        Assert.assertEquals("mytable2", getName());
        Assert.assertEquals(3, getValue());
        // use brakets for toString
        // use standard limit syntax
        String statementToString = "SELECT * FROM mytable WHERE mytable.col = 9 UNION " + ("SELECT * FROM mytable3 WHERE mytable3.col = ? UNION " + "SELECT * FROM mytable2 LIMIT 3, 4");
        TestUtils.assertStatementCanBeDeparsedAs(select, statementToString);
    }

    @Test
    public void testUnion2() throws JSQLParserException {
        String statement = "SELECT * FROM mytable WHERE mytable.col = 9 UNION " + ("SELECT * FROM mytable3 WHERE mytable3.col = ? UNION " + "SELECT * FROM mytable2 LIMIT 3 OFFSET 4");
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        SetOperationList setList = ((SetOperationList) (select.getSelectBody()));
        Assert.assertEquals(3, setList.getSelects().size());
        Assert.assertEquals("mytable", getName());
        Assert.assertEquals("mytable3", getName());
        Assert.assertEquals("mytable2", getName());
        Assert.assertEquals(3, getValue());
        Assert.assertNull(getOffset());
        Assert.assertEquals(4, getOffset());
        // use brakets for toString
        // use standard limit syntax
        String statementToString = "SELECT * FROM mytable WHERE mytable.col = 9 UNION " + ("SELECT * FROM mytable3 WHERE mytable3.col = ? UNION " + "SELECT * FROM mytable2 LIMIT 3 OFFSET 4");
        TestUtils.assertStatementCanBeDeparsedAs(select, statementToString);
    }

    @Test
    public void testDistinct() throws JSQLParserException {
        String statement = "SELECT DISTINCT ON (myid) myid, mycol FROM mytable WHERE mytable.col = 9";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals("myid", getColumnName());
        Assert.assertEquals("mycol", getColumnName());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testDistinctTop() throws JSQLParserException {
        String statement = "SELECT DISTINCT TOP 5 myid, mycol FROM mytable WHERE mytable.col = 9";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals("myid", getColumnName());
        Assert.assertEquals("mycol", getColumnName());
        Assert.assertNotNull(plainSelect.getTop());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testDistinctTop2() {
        String statement = "SELECT TOP 5 DISTINCT myid, mycol FROM mytable WHERE mytable.col = 9";
        try {
            parserManager.parse(new StringReader(statement));
            Assert.fail("sould not work");
        } catch (JSQLParserException ex) {
            // expected to fail
        }
    }

    @Test
    public void testFrom() throws JSQLParserException {
        String statement = "SELECT * FROM mytable as mytable0, mytable1 alias_tab1, mytable2 as alias_tab2, (SELECT * FROM mytable3) AS mytable4 WHERE mytable.col = 9";
        String statementToString = "SELECT * FROM mytable AS mytable0, mytable1 alias_tab1, mytable2 AS alias_tab2, (SELECT * FROM mytable3) AS mytable4 WHERE mytable.col = 9";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals(3, plainSelect.getJoins().size());
        Assert.assertEquals("mytable0", getName());
        Assert.assertEquals("alias_tab1", getName());
        Assert.assertEquals("alias_tab2", getName());
        Assert.assertEquals("mytable4", getName());
        TestUtils.assertStatementCanBeDeparsedAs(select, statementToString);
    }

    @Test
    public void testJoin() throws JSQLParserException {
        String statement = "SELECT * FROM tab1 LEFT OUTER JOIN tab2 ON tab1.id = tab2.id";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals(1, plainSelect.getJoins().size());
        Assert.assertEquals("tab2", getFullyQualifiedName());
        Assert.assertEquals("tab1.id", getFullyQualifiedName());
        Assert.assertTrue(plainSelect.getJoins().get(0).isOuter());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        statement = "SELECT * FROM tab1 LEFT OUTER JOIN tab2 ON tab1.id = tab2.id INNER JOIN tab3";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals(2, plainSelect.getJoins().size());
        Assert.assertEquals("tab3", getFullyQualifiedName());
        Assert.assertFalse(plainSelect.getJoins().get(1).isOuter());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        statement = "SELECT * FROM tab1 LEFT OUTER JOIN tab2 ON tab1.id = tab2.id JOIN tab3";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals(2, plainSelect.getJoins().size());
        Assert.assertEquals("tab3", getFullyQualifiedName());
        Assert.assertFalse(plainSelect.getJoins().get(1).isOuter());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        // implicit INNER
        statement = "SELECT * FROM tab1 LEFT OUTER JOIN tab2 ON tab1.id = tab2.id INNER JOIN tab3";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        statement = "SELECT * FROM TA2 LEFT OUTER JOIN O USING (col1, col2) WHERE D.OasSD = 'asdf' AND (kj >= 4 OR l < 'sdf')";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        statement = "SELECT * FROM tab1 INNER JOIN tab2 USING (id, id2)";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals(1, plainSelect.getJoins().size());
        Assert.assertEquals("tab2", getFullyQualifiedName());
        Assert.assertFalse(plainSelect.getJoins().get(0).isOuter());
        Assert.assertEquals(2, plainSelect.getJoins().get(0).getUsingColumns().size());
        Assert.assertEquals("id2", getFullyQualifiedName());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        statement = "SELECT * FROM tab1 RIGHT OUTER JOIN tab2 USING (id, id2)";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
        statement = "SELECT * FROM foo AS f LEFT OUTER JOIN (bar AS b RIGHT OUTER JOIN baz AS z ON f.id = z.id) ON f.id = b.id";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        statement = "SELECT * FROM foo AS f, OUTER bar AS b WHERE f.id = b.id";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals(1, plainSelect.getJoins().size());
        Assert.assertTrue(plainSelect.getJoins().get(0).isOuter());
        Assert.assertTrue(plainSelect.getJoins().get(0).isSimple());
        Assert.assertEquals("bar", getFullyQualifiedName());
        Assert.assertEquals("b", getName());
    }

    @Test
    public void testFunctions() throws JSQLParserException {
        String statement = "SELECT MAX(id) AS max FROM mytable WHERE mytable.col = 9";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals("max", getName());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        statement = "SELECT substring(id, 2, 3), substring(id from 2 for 3), substring(id from 2), trim(BOTH ' ' from 'foo bar '), trim(LEADING ' ' from 'foo bar '), trim(TRAILING ' ' from 'foo bar '), trim(' ' from 'foo bar '), position('foo' in 'bar'), overlay('foo' placing 'bar' from 1), overlay('foo' placing 'bar' from 1 for 2) FROM my table";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        statement = "SELECT MAX(id), AVG(pro) AS myavg FROM mytable WHERE mytable.col = 9 GROUP BY pro";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals("myavg", getName());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        statement = "SELECT MAX(a, b, c), COUNT(*), D FROM tab1 GROUP BY D";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        plainSelect = ((PlainSelect) (select.getSelectBody()));
        Function fun = ((Function) (getExpression()));
        Assert.assertEquals("MAX", fun.getName());
        Assert.assertEquals("b", getFullyQualifiedName());
        Assert.assertTrue(isAllColumns());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        statement = "SELECT {fn MAX(a, b, c)}, COUNT(*), D FROM tab1 GROUP BY D";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        plainSelect = ((PlainSelect) (select.getSelectBody()));
        fun = ((Function) (getExpression()));
        Assert.assertTrue(fun.isEscaped());
        Assert.assertEquals("MAX", fun.getName());
        Assert.assertEquals("b", getFullyQualifiedName());
        Assert.assertTrue(isAllColumns());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        statement = "SELECT ab.MAX(a, b, c), cd.COUNT(*), D FROM tab1 GROUP BY D";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        plainSelect = ((PlainSelect) (select.getSelectBody()));
        fun = ((Function) (getExpression()));
        Assert.assertEquals("ab.MAX", fun.getName());
        Assert.assertEquals("b", getFullyQualifiedName());
        fun = ((Function) (getExpression()));
        Assert.assertEquals("cd.COUNT", fun.getName());
        Assert.assertTrue(fun.isAllColumns());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testEscapedFunctionsIssue647() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT {fn test(0)} AS COL");
        // assertSqlCanBeParsedAndDeparsed("SELECT {fn current_timestamp(0)} AS COL");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT {fn concat(a, b)} AS COL");
    }

    @Test
    public void testEscapedFunctionsIssue753() throws JSQLParserException {
        Statement stmt = CCJSqlParserUtil.parse("SELECT { fn test(0)} AS COL");
        Assert.assertEquals("SELECT {fn test(0)} AS COL", stmt.toString());
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT fn FROM fn");
    }

    @Test
    public void testNamedParametersPR702() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT substring(id, 2, 3), substring(id from 2 for 3), substring(id from 2), trim(BOTH ' ' from 'foo bar '), trim(LEADING ' ' from 'foo bar '), trim(TRAILING ' ' from 'foo bar '), trim(' ' from 'foo bar '), position('foo' in 'bar'), overlay('foo' placing 'bar' from 1), overlay('foo' placing 'bar' from 1 for 2) FROM my table");
    }

    @Test
    public void testNamedParametersPR702_2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT substring(id, 2, 3) FROM mytable");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT substring(id from 2 for 3) FROM mytable");
    }

    @Test
    public void testWhere() throws JSQLParserException {
        final String statement = "SELECT * FROM tab1 WHERE";
        String whereToString = "(a + b + c / d + e * f) * (a / b * (a + b)) > ?";
        PlainSelect plainSelect = ((PlainSelect) (getSelectBody()));
        Assert.assertTrue(((plainSelect.getWhere()) instanceof GreaterThan));
        Assert.assertTrue(((getLeftExpression()) instanceof Multiplication));
        Assert.assertEquals(((statement + " ") + whereToString), plainSelect.toString());
        TestUtils.assertExpressionCanBeDeparsedAs(plainSelect.getWhere(), whereToString);
        whereToString = "(7 * s + 9 / 3) NOT BETWEEN 3 AND ?";
        plainSelect = ((PlainSelect) (getSelectBody()));
        TestUtils.assertExpressionCanBeDeparsedAs(plainSelect.getWhere(), whereToString);
        Assert.assertEquals(((statement + " ") + whereToString), plainSelect.toString());
        whereToString = "a / b NOT IN (?, 's''adf', 234.2)";
        plainSelect = ((PlainSelect) (getSelectBody()));
        TestUtils.assertExpressionCanBeDeparsedAs(plainSelect.getWhere(), whereToString);
        Assert.assertEquals(((statement + " ") + whereToString), plainSelect.toString());
        whereToString = " NOT 0 = 0";
        plainSelect = ((PlainSelect) (getSelectBody()));
        whereToString = " NOT (0 = 0)";
        plainSelect = ((PlainSelect) (getSelectBody()));
        TestUtils.assertExpressionCanBeDeparsedAs(plainSelect.getWhere(), whereToString.trim());
        Assert.assertEquals((statement + whereToString), plainSelect.toString());
    }

    @Test
    public void testGroupBy() throws JSQLParserException {
        String statement = "SELECT * FROM tab1 WHERE a > 34 GROUP BY tab1.b";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals(1, plainSelect.getGroupBy().getGroupByExpressions().size());
        Assert.assertEquals("tab1.b", getFullyQualifiedName());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        statement = "SELECT * FROM tab1 WHERE a > 34 GROUP BY 2, 3";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals(2, plainSelect.getGroupBy().getGroupByExpressions().size());
        Assert.assertEquals(2, getValue());
        Assert.assertEquals(3, getValue());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testHaving() throws JSQLParserException {
        String statement = "SELECT MAX(tab1.b) FROM tab1 WHERE a > 34 GROUP BY tab1.b HAVING MAX(tab1.b) > 56";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertTrue(((plainSelect.getHaving()) instanceof GreaterThan));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        statement = "SELECT MAX(tab1.b) FROM tab1 WHERE a > 34 HAVING MAX(tab1.b) IN (56, 32, 3, ?)";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertTrue(((plainSelect.getHaving()) instanceof InExpression));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testExists() throws JSQLParserException {
        String statement = "SELECT * FROM tab1 WHERE ";
        String where = "EXISTS (SELECT * FROM tab2)";
        statement += where;
        Statement parsed = parserManager.parse(new StringReader(statement));
        Assert.assertEquals(statement, parsed.toString());
        PlainSelect plainSelect = ((PlainSelect) (getSelectBody()));
        TestUtils.assertExpressionCanBeDeparsedAs(plainSelect.getWhere(), where);
    }

    @Test
    public void testOrderBy() throws JSQLParserException {
        // TODO: should there be a DESC marker in the OrderByElement class?
        String statement = "SELECT * FROM tab1 WHERE a > 34 GROUP BY tab1.b ORDER BY tab1.a DESC, tab1.b ASC";
        String statementToString = "SELECT * FROM tab1 WHERE a > 34 GROUP BY tab1.b ORDER BY tab1.a DESC, tab1.b ASC";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals(2, plainSelect.getOrderByElements().size());
        Assert.assertEquals("tab1.a", getFullyQualifiedName());
        Assert.assertEquals("b", getColumnName());
        Assert.assertTrue(plainSelect.getOrderByElements().get(1).isAsc());
        Assert.assertFalse(plainSelect.getOrderByElements().get(0).isAsc());
        TestUtils.assertStatementCanBeDeparsedAs(select, statementToString);
        statement = "SELECT * FROM tab1 WHERE a > 34 GROUP BY tab1.b ORDER BY tab1.a, 2";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals(2, plainSelect.getOrderByElements().size());
        Assert.assertEquals("a", getColumnName());
        Assert.assertEquals(2, getValue());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testOrderByNullsFirst() throws JSQLParserException {
        String statement = "SELECT a FROM tab1 ORDER BY a NULLS FIRST";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testTimestamp() throws JSQLParserException {
        String statement = "SELECT * FROM tab1 WHERE a > {ts '2004-04-30 04:05:34.56'}";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals("2004-04-30 04:05:34.56", getValue().toString());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testTime() throws JSQLParserException {
        String statement = "SELECT * FROM tab1 WHERE a > {t '04:05:34'}";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals("04:05:34", getValue().toString());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testBetweenDate() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM mytable WHERE col BETWEEN {d '2015-09-19'} AND {d '2015-09-24'}");
    }

    @Test
    public void testCase() throws JSQLParserException {
        String statement = "SELECT a, CASE b WHEN 1 THEN 2 END FROM tab1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
        statement = "SELECT a, (CASE WHEN (a > 2) THEN 3 END) AS b FROM tab1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
        statement = "SELECT a, (CASE WHEN a > 2 THEN 3 ELSE 4 END) AS b FROM tab1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
        statement = "SELECT a, (CASE b WHEN 1 THEN 2 WHEN 3 THEN 4 ELSE 5 END) FROM tab1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
        statement = "SELECT a, (CASE " + (("WHEN b > 1 THEN 'BBB' " + "WHEN a = 3 THEN 'AAA' ") + "END) FROM tab1");
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
        statement = "SELECT a, (CASE " + (((((("WHEN b > 1 THEN 'BBB' " + "WHEN a = 3 THEN 'AAA' ") + "END) FROM tab1 ") + "WHERE c = (CASE ") + "WHEN d <> 3 THEN 5 ") + "ELSE 10 ") + "END)");
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
        statement = "SELECT a, CASE a " + (("WHEN 'b' THEN 'BBB' " + "WHEN 'a' THEN 'AAA' ") + "END AS b FROM tab1");
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
        statement = "SELECT a FROM tab1 WHERE CASE b WHEN 1 THEN 2 WHEN 3 THEN 4 ELSE 5 END > 34";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
        statement = "SELECT a FROM tab1 WHERE CASE b WHEN 1 THEN 2 + 3 ELSE 4 END > 34";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
        statement = "SELECT a, (CASE " + ("WHEN (CASE a WHEN 1 THEN 10 ELSE 20 END) > 15 THEN 'BBB' " + // "WHEN (SELECT c FROM tab2 WHERE d = 2) = 3 THEN 'AAA' " +
        "END) FROM tab1");
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testIssue371SimplifiedCase() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CASE col + 4 WHEN 2 THEN 1 ELSE 0 END");
    }

    @Test
    public void testIssue371SimplifiedCase2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CASE col > 4 WHEN true THEN 1 ELSE 0 END");
    }

    @Test
    public void testIssue235SimplifiedCase3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CASE WHEN (CASE WHEN (CASE WHEN (1) THEN 0 END) THEN 0 END) THEN 0 END FROM a");
    }

    @Test
    public void testIssue235SimplifiedCase4() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CASE WHEN (CASE WHEN (CASE WHEN (CASE WHEN (1) THEN 0 END) THEN 0 END) THEN 0 END) THEN 0 END FROM a");
    }

    @Test
    public void testReplaceAsFunction() throws JSQLParserException {
        String statement = "SELECT REPLACE(a, 'b', c) FROM tab1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
        Statement stmt = CCJSqlParserUtil.parse(statement);
        Select select = ((Select) (stmt));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals(1, plainSelect.getSelectItems().size());
        Expression expression = ((SelectExpressionItem) (plainSelect.getSelectItems().get(0))).getExpression();
        Assert.assertTrue((expression instanceof Function));
        Function func = ((Function) (expression));
        Assert.assertEquals("REPLACE", func.getName());
        Assert.assertEquals(3, func.getParameters().getExpressions().size());
    }

    @Test
    public void testLike() throws JSQLParserException {
        String statement = "SELECT * FROM tab1 WHERE a LIKE 'test'";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals("test", getValue());
        statement = "SELECT * FROM tab1 WHERE a LIKE 'test' ESCAPE 'test2'";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals("test", getValue());
        Assert.assertEquals("test2", getEscape());
    }

    @Test
    public void testNotLike() throws JSQLParserException {
        String statement = "SELECT * FROM tab1 WHERE a NOT LIKE 'test'";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals("test", getValue());
        Assert.assertEquals(true, ((boolean) (isNot())));
    }

    @Test
    public void testNotLikeWithNotBeforeExpression() throws JSQLParserException {
        String statement = "SELECT * FROM tab1 WHERE NOT a LIKE 'test'";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertTrue(((plainSelect.getWhere()) instanceof NotExpression));
        NotExpression notExpr = ((NotExpression) (plainSelect.getWhere()));
        Assert.assertEquals("test", getValue());
        Assert.assertEquals(false, ((boolean) (isNot())));
    }

    @Test
    public void testIlike() throws JSQLParserException {
        String statement = "SELECT col1 FROM table1 WHERE col1 ILIKE '%hello%'";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testSelectOrderHaving() throws JSQLParserException {
        String statement = "SELECT units, count(units) AS num FROM currency GROUP BY units HAVING count(units) > 1 ORDER BY num";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testDouble() throws JSQLParserException {
        String statement = "SELECT 1e2, * FROM mytable WHERE mytable.col = 9";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals(100.0, getValue(), 0);
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        statement = "SELECT * FROM mytable WHERE mytable.col = 1.e2";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals(100.0, getValue(), 0);
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        statement = "SELECT * FROM mytable WHERE mytable.col = 1.2e2";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals(120.0, getValue(), 0);
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
        statement = "SELECT * FROM mytable WHERE mytable.col = 2e2";
        select = ((Select) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals(200.0, getValue(), 0);
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testDouble2() throws JSQLParserException {
        String statement = "SELECT 1.e22 FROM mytable";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals(1.0E22, getValue(), 0);
    }

    @Test
    public void testDouble3() throws JSQLParserException {
        String statement = "SELECT 1. FROM mytable";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals(1.0, getValue(), 0);
    }

    @Test
    public void testDouble4() throws JSQLParserException {
        String statement = "SELECT 1.2e22 FROM mytable";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals(1.2E22, getValue(), 0);
    }

    @Test
    public void testWith() throws JSQLParserException {
        String statement = "WITH DINFO (DEPTNO, AVGSALARY, EMPCOUNT) AS " + (((("(SELECT OTHERS.WORKDEPT, AVG(OTHERS.SALARY), COUNT(*) FROM EMPLOYEE AS OTHERS " + "GROUP BY OTHERS.WORKDEPT), DINFOMAX AS (SELECT MAX(AVGSALARY) AS AVGMAX FROM DINFO) ") + "SELECT THIS_EMP.EMPNO, THIS_EMP.SALARY, DINFO.AVGSALARY, DINFO.EMPCOUNT, DINFOMAX.AVGMAX ") + "FROM EMPLOYEE AS THIS_EMP INNER JOIN DINFO INNER JOIN DINFOMAX ") + "WHERE THIS_EMP.JOB = 'SALESREP' AND THIS_EMP.WORKDEPT = DINFO.DEPTNO");
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testWithRecursive() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("WITH RECURSIVE t (n) AS ((SELECT 1) UNION ALL (SELECT n + 1 FROM t WHERE n < 100)) SELECT sum(n) FROM t");
    }

    @Test
    public void testSelectAliasInQuotes() throws JSQLParserException {
        String statement = "SELECT mycolumn AS \"My Column Name\" FROM mytable";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testSelectAliasWithoutAs() throws JSQLParserException {
        String statement = "SELECT mycolumn \"My Column Name\" FROM mytable";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testSelectJoinWithComma() throws JSQLParserException {
        String statement = "SELECT cb.Genus, cb.Species FROM Coleccion_de_Briofitas AS cb, unigeoestados AS es " + "WHERE es.nombre = \"Tamaulipas\" AND cb.the_geom = es.geom";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testDeparser() throws JSQLParserException {
        String statement = "SELECT a.OWNERLASTNAME, a.OWNERFIRSTNAME " + ("FROM ANTIQUEOWNERS AS a, ANTIQUES AS b " + "WHERE b.BUYERID = a.OWNERID AND b.ITEM = 'Chair'");
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
        statement = "SELECT count(DISTINCT f + 4) FROM a";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
        statement = "SELECT count(DISTINCT f, g, h) FROM a";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testCount2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT count(ALL col1 + col2) FROM mytable");
    }

    @Test
    public void testMysqlQuote() throws JSQLParserException {
        String statement = "SELECT `a.OWNERLASTNAME`, `OWNERFIRSTNAME` " + ("FROM `ANTIQUEOWNERS` AS a, ANTIQUES AS b " + "WHERE b.BUYERID = a.OWNERID AND b.ITEM = 'Chair'");
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testConcat() throws JSQLParserException {
        String statement = "SELECT a || b || c + 4 FROM t";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testConcatProblem2() throws JSQLParserException {
        String stmt = "SELECT MAX(((((" + ((("(SPA.SOORTAANLEVERPERIODE)::VARCHAR (2) || (VARCHAR(SPA.AANLEVERPERIODEJAAR))::VARCHAR (4)" + ") || TO_CHAR(SPA.AANLEVERPERIODEVOLGNR, 'FM09'::VARCHAR)") + ") || TO_CHAR((10000 - SPA.VERSCHIJNINGSVOLGNR), 'FM0999'::VARCHAR)") + ") || (SPA.GESLACHT)::VARCHAR (1))) AS GESLACHT_TMP FROM testtable");
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testConcatProblem2_1() throws JSQLParserException {
        String stmt = "SELECT TO_CHAR(SPA.AANLEVERPERIODEVOLGNR, 'FM09'::VARCHAR) FROM testtable";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testConcatProblem2_2() throws JSQLParserException {
        String stmt = "SELECT MAX((SPA.SOORTAANLEVERPERIODE)::VARCHAR (2) || (VARCHAR(SPA.AANLEVERPERIODEJAAR))::VARCHAR (4)) AS GESLACHT_TMP FROM testtable";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testConcatProblem2_3() throws JSQLParserException {
        String stmt = "SELECT TO_CHAR((10000 - SPA.VERSCHIJNINGSVOLGNR), 'FM0999'::VARCHAR) FROM testtable";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testConcatProblem2_4() throws JSQLParserException {
        String stmt = "SELECT (SPA.GESLACHT)::VARCHAR (1) FROM testtable";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testConcatProblem2_5() throws JSQLParserException {
        String stmt = "SELECT max((a || b) || c) FROM testtable";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testConcatProblem2_5_1() throws JSQLParserException {
        String stmt = "SELECT (a || b) || c FROM testtable";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testConcatProblem2_5_2() throws JSQLParserException {
        String stmt = "SELECT (a + b) + c FROM testtable";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testConcatProblem2_6() throws JSQLParserException {
        String stmt = "SELECT max(a || b || c) FROM testtable";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testMatches() throws JSQLParserException {
        String statement = "SELECT * FROM team WHERE team.search_column @@ to_tsquery('new & york & yankees')";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testGroupByExpression() throws JSQLParserException {
        String statement = "SELECT col1, col2, col1 + col2, sum(col8)" + (" FROM table1 " + "GROUP BY col1, col2, col1 + col2");
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testBitwise() throws JSQLParserException {
        String statement = "SELECT col1 & 32, col2 ^ col1, col1 | col2" + " FROM table1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testSelectFunction() throws JSQLParserException {
        String statement = "SELECT 1 + 2 AS sum";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testWeirdSelect() throws JSQLParserException {
        String sql = "select r.reviews_id, substring(rd.reviews_text, 100) as reviews_text, r.reviews_rating, r.date_added, r.customers_name from reviews r, reviews_description rd where r.products_id = '19' and r.reviews_id = rd.reviews_id and rd.languages_id = '1' and r.reviews_status = 1 order by r.reviews_id desc limit 0, 6";
        parserManager.parse(new StringReader(sql));
    }

    @Test
    public void testCast() throws JSQLParserException {
        String stmt = "SELECT CAST(a AS varchar) FROM tabelle1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
        stmt = "SELECT CAST(a AS varchar2) FROM tabelle1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testCastInCast() throws JSQLParserException {
        String stmt = "SELECT CAST(CAST(a AS numeric) AS varchar) FROM tabelle1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testCastInCast2() throws JSQLParserException {
        String stmt = "SELECT CAST('test' + CAST(assertEqual AS numeric) AS varchar) FROM tabelle1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testCastTypeProblem() throws JSQLParserException {
        String stmt = "SELECT CAST(col1 AS varchar (256)) FROM tabelle1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testCastTypeProblem2() throws JSQLParserException {
        String stmt = "SELECT col1::varchar FROM tabelle1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testCastTypeProblem3() throws JSQLParserException {
        String stmt = "SELECT col1::varchar (256) FROM tabelle1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testCastTypeProblem4() throws JSQLParserException {
        String stmt = "SELECT 5::varchar (256) FROM tabelle1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testCastTypeProblem5() throws JSQLParserException {
        String stmt = "SELECT 5.67::varchar (256) FROM tabelle1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testCastTypeProblem6() throws JSQLParserException {
        String stmt = "SELECT 'test'::character varying FROM tabelle1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testCastTypeProblem7() throws JSQLParserException {
        String stmt = "SELECT CAST('test' AS character varying) FROM tabelle1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testCastTypeProblem8() throws JSQLParserException {
        String stmt = "SELECT CAST('123' AS double precision) FROM tabelle1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testCaseElseAddition() throws JSQLParserException {
        String stmt = "SELECT CASE WHEN 1 + 3 > 20 THEN 0 ELSE 1000 + 1 END AS d FROM dual";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testBrackets() throws JSQLParserException {
        String stmt = "SELECT table_a.name AS [Test] FROM table_a";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testBrackets2() throws JSQLParserException {
        String stmt = "SELECT [a] FROM t";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testBrackets3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM \"2016\"");
    }

    @Test
    public void testProblemSqlServer_Modulo_Proz() throws Exception {
        String stmt = "SELECT 5 % 2 FROM A";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testProblemSqlServer_Modulo_mod() throws Exception {
        String stmt = "SELECT mod(5, 2) FROM A";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testProblemSqlServer_Modulo() throws Exception {
        String stmt = "SELECT convert(varchar(255), DATEDIFF(month, year1, abc_datum) / 12) + ' year, ' + convert(varchar(255), DATEDIFF(month, year2, abc_datum) % 12) + ' month' FROM test_table";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testIsNot() throws JSQLParserException {
        String stmt = "SELECT * FROM test WHERE a IS NOT NULL";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testIsNot2() throws JSQLParserException {
        // the deparser delivers always a IS NOT NULL even for NOT a IS NULL
        String stmt = "SELECT * FROM test WHERE NOT a IS NULL";
        Statement parsed = parserManager.parse(new StringReader(stmt));
        TestUtils.assertStatementCanBeDeparsedAs(parsed, "SELECT * FROM test WHERE NOT a IS NULL");
    }

    @Test
    public void testProblemSqlAnalytic() throws JSQLParserException {
        String stmt = "SELECT a, row_number() OVER (ORDER BY a) AS n FROM table1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testProblemSqlAnalytic2() throws JSQLParserException {
        String stmt = "SELECT a, row_number() OVER (ORDER BY a, b) AS n FROM table1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testProblemSqlAnalytic3() throws JSQLParserException {
        String stmt = "SELECT a, row_number() OVER (PARTITION BY c ORDER BY a, b) AS n FROM table1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testProblemSqlAnalytic4EmptyOver() throws JSQLParserException {
        String stmt = "SELECT a, row_number() OVER () AS n FROM table1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testProblemSqlAnalytic5AggregateColumnValue() throws JSQLParserException {
        String stmt = "SELECT a, sum(b) OVER () AS n FROM table1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testProblemSqlAnalytic6AggregateColumnValue() throws JSQLParserException {
        String stmt = "SELECT a, sum(b + 5) OVER (ORDER BY a) AS n FROM table1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testProblemSqlAnalytic7Count() throws JSQLParserException {
        String stmt = "SELECT count(*) OVER () AS n FROM table1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testProblemSqlAnalytic8Complex() throws JSQLParserException {
        String stmt = "SELECT ID, NAME, SALARY, SUM(SALARY) OVER () AS SUM_SAL, AVG(SALARY) OVER () AS AVG_SAL, MIN(SALARY) OVER () AS MIN_SAL, MAX(SALARY) OVER () AS MAX_SAL, COUNT(*) OVER () AS ROWS2 FROM STAFF WHERE ID < 60 ORDER BY ID";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testProblemSqlAnalytic9CommaListPartition() throws JSQLParserException {
        String stmt = "SELECT a, row_number() OVER (PARTITION BY c, d ORDER BY a, b) AS n FROM table1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testProblemSqlAnalytic10Lag() throws JSQLParserException {
        String stmt = "SELECT a, lag(a, 1) OVER (PARTITION BY c ORDER BY a, b) AS n FROM table1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testProblemSqlAnalytic11Lag() throws JSQLParserException {
        String stmt = "SELECT a, lag(a, 1, 0) OVER (PARTITION BY c ORDER BY a, b) AS n FROM table1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testAnalyticFunction12() throws JSQLParserException {
        String statement = "SELECT SUM(a) OVER (PARTITION BY b ORDER BY c) FROM tab1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testAnalyticFunction13() throws JSQLParserException {
        String statement = "SELECT SUM(a) OVER () FROM tab1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testAnalyticFunction14() throws JSQLParserException {
        String statement = "SELECT SUM(a) OVER (PARTITION BY b ) FROM tab1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testAnalyticFunction15() throws JSQLParserException {
        String statement = "SELECT SUM(a) OVER (ORDER BY c) FROM tab1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testAnalyticFunction16() throws JSQLParserException {
        String statement = "SELECT SUM(a) OVER (ORDER BY c NULLS FIRST) FROM tab1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testAnalyticFunction17() throws JSQLParserException {
        String statement = "SELECT AVG(sal) OVER (PARTITION BY deptno ORDER BY sal ROWS BETWEEN 0 PRECEDING AND 0 PRECEDING) AS avg_of_current_sal FROM emp";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testAnalyticFunction18() throws JSQLParserException {
        String statement = "SELECT AVG(sal) OVER (PARTITION BY deptno ORDER BY sal RANGE CURRENT ROW) AS avg_of_current_sal FROM emp";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testAnalyticFunctionProblem1() throws JSQLParserException {
        String statement = "SELECT last_value(s.revenue_hold) OVER (PARTITION BY s.id_d_insertion_order, s.id_d_product_ad_attr, trunc(s.date_id, 'mm') ORDER BY s.date_id) AS col FROM s";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testAnalyticFunction19() throws JSQLParserException {
        String statement = "SELECT count(DISTINCT CASE WHEN client_organic_search_drop_flag = 1 THEN brand END) OVER (PARTITION BY client, category_1, category_2, category_3, category_4 ) AS client_brand_org_drop_count FROM sometable";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testAnalyticFunctionProblem1b() throws JSQLParserException {
        String statement = "SELECT last_value(s.revenue_hold) OVER (PARTITION BY s.id_d_insertion_order, s.id_d_product_ad_attr, trunc(s.date_id, 'mm') ORDER BY s.date_id ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS col FROM s";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testAnalyticFunctionIssue670() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT last_value(some_column IGNORE NULLS) OVER (PARTITION BY some_other_column_1, some_other_column_2 ORDER BY some_other_column_3 ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) column_alias FROM some_table");
    }

    @Test
    public void testFunctionLeft() throws JSQLParserException {
        String statement = "SELECT left(table1.col1, 4) FROM table1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testFunctionRight() throws JSQLParserException {
        String statement = "SELECT right(table1.col1, 4) FROM table1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testOracleJoin() throws JSQLParserException {
        String stmt = "SELECT * FROM tabelle1, tabelle2 WHERE tabelle1.a = tabelle2.b(+)";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testOracleJoin2() throws JSQLParserException {
        String stmt = "SELECT * FROM tabelle1, tabelle2 WHERE tabelle1.a(+) = tabelle2.b";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testOracleJoin2_1() throws JSQLParserException {
        String[] values = new String[]{ "(+)", "( +)", "(+ )", "( + )", " (+) " };
        for (String value : values) {
            TestUtils.assertSqlCanBeParsedAndDeparsed((("SELECT * FROM tabelle1, tabelle2 WHERE tabelle1.a" + value) + " = tabelle2.b"), true);
        }
    }

    @Test
    public void testOracleJoin2_2() throws JSQLParserException {
        String[] values = new String[]{ "(+)", "( +)", "(+ )", "( + )", " (+) " };
        for (String value : values) {
            TestUtils.assertSqlCanBeParsedAndDeparsed(("SELECT * FROM tabelle1, tabelle2 WHERE tabelle1.a = tabelle2.b" + value), true);
        }
    }

    @Test
    public void testOracleJoin3() throws JSQLParserException {
        String stmt = "SELECT * FROM tabelle1, tabelle2 WHERE tabelle1.a(+) > tabelle2.b";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testOracleJoin3_1() throws JSQLParserException {
        String stmt = "SELECT * FROM tabelle1, tabelle2 WHERE tabelle1.a > tabelle2.b(+)";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testOracleJoin4() throws JSQLParserException {
        String stmt = "SELECT * FROM tabelle1, tabelle2 WHERE tabelle1.a(+) = tabelle2.b AND tabelle1.b(+) IN ('A', 'B')";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testOracleJoinIssue318() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM TBL_A, TBL_B, TBL_C WHERE TBL_A.ID(+) = TBL_B.ID AND TBL_C.ROOM(+) = TBL_B.ROOM");
    }

    @Test
    public void testProblemSqlIntersect() throws Exception {
        String stmt = "(SELECT * FROM a) INTERSECT (SELECT * FROM b)";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
        stmt = "SELECT * FROM a INTERSECT SELECT * FROM b";
        Statement parsed = parserManager.parse(new StringReader(stmt));
        TestUtils.assertStatementCanBeDeparsedAs(parsed, "SELECT * FROM a INTERSECT SELECT * FROM b");
    }

    @Test
    public void testProblemSqlExcept() throws Exception {
        String stmt = "(SELECT * FROM a) EXCEPT (SELECT * FROM b)";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
        stmt = "SELECT * FROM a EXCEPT SELECT * FROM b";
        Statement parsed = parserManager.parse(new StringReader(stmt));
        TestUtils.assertStatementCanBeDeparsedAs(parsed, "SELECT * FROM a EXCEPT SELECT * FROM b");
    }

    @Test
    public void testProblemSqlMinus() throws Exception {
        String stmt = "(SELECT * FROM a) MINUS (SELECT * FROM b)";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
        stmt = "SELECT * FROM a MINUS SELECT * FROM b";
        Statement parsed = parserManager.parse(new StringReader(stmt));
        TestUtils.assertStatementCanBeDeparsedAs(parsed, "SELECT * FROM a MINUS SELECT * FROM b");
    }

    @Test
    public void testProblemSqlCombinedSets() throws Exception {
        String stmt = "(SELECT * FROM a) INTERSECT (SELECT * FROM b) UNION (SELECT * FROM c)";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testWithStatement() throws JSQLParserException {
        String stmt = "WITH test AS (SELECT mslink FROM feature) SELECT * FROM feature WHERE mslink IN (SELECT mslink FROM test)";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testWithUnionProblem() throws JSQLParserException {
        String stmt = "WITH test AS ((SELECT mslink FROM tablea) UNION (SELECT mslink FROM tableb)) SELECT * FROM tablea WHERE mslink IN (SELECT mslink FROM test)";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testWithUnionAllProblem() throws JSQLParserException {
        String stmt = "WITH test AS ((SELECT mslink FROM tablea) UNION ALL (SELECT mslink FROM tableb)) SELECT * FROM tablea WHERE mslink IN (SELECT mslink FROM test)";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testWithUnionProblem3() throws JSQLParserException {
        String stmt = "WITH test AS ((SELECT mslink, CAST(tablea.fname AS varchar) FROM tablea INNER JOIN tableb ON tablea.mslink = tableb.mslink AND tableb.deleted = 0 WHERE tablea.fname IS NULL AND 1 = 0) UNION ALL (SELECT mslink FROM tableb)) SELECT * FROM tablea WHERE mslink IN (SELECT mslink FROM test)";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testWithUnionProblem4() throws JSQLParserException {
        String stmt = "WITH hist AS ((SELECT gl.mslink, ba.gl_name AS txt, ba.gl_nummer AS nr, 0 AS level, CAST(gl.mslink AS VARCHAR) AS path, ae.feature FROM tablea AS gl INNER JOIN tableb AS ba ON gl.mslink = ba.gl_mslink INNER JOIN tablec AS ae ON gl.mslink = ae.mslink AND ae.deleted = 0 WHERE gl.parent IS NULL AND gl.mslink <> 0) UNION ALL (SELECT gl.mslink, ba.gl_name AS txt, ba.gl_nummer AS nr, hist.level + 1 AS level, CAST(hist.path + '.' + CAST(gl.mslink AS VARCHAR) AS VARCHAR) AS path, ae.feature FROM tablea AS gl INNER JOIN tableb AS ba ON gl.mslink = ba.gl_mslink INNER JOIN tablec AS ae ON gl.mslink = ae.mslink AND ae.deleted = 0 INNER JOIN hist ON gl.parent = hist.mslink WHERE gl.mslink <> 0)) SELECT mslink, space(level * 4) + txt AS txt, nr, feature, path FROM hist WHERE EXISTS (SELECT feature FROM tablec WHERE mslink = 0 AND ((feature IN (1, 2) AND hist.feature = 3) OR (feature IN (4) AND hist.feature = 2)))";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testWithUnionProblem5() throws JSQLParserException {
        String stmt = "WITH hist AS ((SELECT gl.mslink, ba.gl_name AS txt, ba.gl_nummer AS nr, 0 AS level, CAST(gl.mslink AS VARCHAR) AS path, ae.feature FROM tablea AS gl INNER JOIN tableb AS ba ON gl.mslink = ba.gl_mslink INNER JOIN tablec AS ae ON gl.mslink = ae.mslink AND ae.deleted = 0 WHERE gl.parent IS NULL AND gl.mslink <> 0) UNION ALL (SELECT gl.mslink, ba.gl_name AS txt, ba.gl_nummer AS nr, hist.level + 1 AS level, CAST(hist.path + '.' + CAST(gl.mslink AS VARCHAR) AS VARCHAR) AS path, 5 AS feature FROM tablea AS gl INNER JOIN tableb AS ba ON gl.mslink = ba.gl_mslink INNER JOIN tablec AS ae ON gl.mslink = ae.mslink AND ae.deleted = 0 INNER JOIN hist ON gl.parent = hist.mslink WHERE gl.mslink <> 0)) SELECT * FROM hist";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testExtractFrom1() throws JSQLParserException {
        String stmt = "SELECT EXTRACT(month FROM datecolumn) FROM testtable";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testExtractFrom2() throws JSQLParserException {
        String stmt = "SELECT EXTRACT(year FROM now()) FROM testtable";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testExtractFrom3() throws JSQLParserException {
        String stmt = "SELECT EXTRACT(year FROM (now() - 2)) FROM testtable";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testExtractFrom4() throws JSQLParserException {
        String stmt = "SELECT EXTRACT(minutes FROM now() - '01:22:00') FROM testtable";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    // @Test
    // public void testExtractFromIssue673() throws JSQLParserException {
    // String stmt = "select EXTRACT(DAY FROM (SYSDATE - to_date('20180101', 'YYYYMMDD' ) ) DAY TO SECOND) from dual";
    // assertSqlCanBeParsedAndDeparsed(stmt);
    // }
    @Test
    public void testProblemFunction() throws JSQLParserException {
        String stmt = "SELECT test() FROM testtable";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
        Statement parsed = CCJSqlParserUtil.parse(stmt);
        Select select = ((Select) (parsed));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        SelectItem get = plainSelect.getSelectItems().get(0);
        SelectExpressionItem item = ((SelectExpressionItem) (get));
        Assert.assertTrue(((item.getExpression()) instanceof Function));
        Assert.assertEquals("test", getName());
    }

    @Test
    public void testProblemFunction2() throws JSQLParserException {
        String stmt = "SELECT sysdate FROM testtable";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testProblemFunction3() throws JSQLParserException {
        String stmt = "SELECT TRUNCATE(col) FROM testtable";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testAdditionalLettersGerman() throws JSQLParserException {
        String stmt = "SELECT col?, col?, col? FROM testtable???";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
        stmt = "SELECT colA, col?, col? FROM testtable???";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
        stmt = "SELECT ?col FROM testtable???";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
        stmt = "SELECT ?col? FROM testtable?";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testAdditionalLettersSpanish() throws JSQLParserException {
        String stmt = "SELECT * FROM a?os";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testMultiTableJoin() throws JSQLParserException {
        String stmt = "SELECT * FROM taba INNER JOIN tabb ON taba.a = tabb.a, tabc LEFT JOIN tabd ON tabc.c = tabd.c";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testTableCrossJoin() throws JSQLParserException {
        String stmt = "SELECT * FROM taba CROSS JOIN tabb";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testLateral1() throws JSQLParserException {
        String stmt = "SELECT O.ORDERID, O.CUSTNAME, OL.LINETOTAL FROM ORDERS AS O, LATERAL(SELECT SUM(NETAMT) AS LINETOTAL FROM ORDERLINES AS LINES WHERE LINES.ORDERID = O.ORDERID) AS OL";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testLateralComplex1() throws IOException, JSQLParserException {
        String stmt = IOUtils.toString(SelectTest.class.getResourceAsStream("complex-lateral-select-request.txt"));
        Select select = ((Select) (parserManager.parse(new StringReader(stmt))));
        Assert.assertEquals("SELECT O.ORDERID, O.CUSTNAME, OL.LINETOTAL, OC.ORDCHGTOTAL, OT.TAXTOTAL FROM ORDERS O, LATERAL(SELECT SUM(NETAMT) AS LINETOTAL FROM ORDERLINES LINES WHERE LINES.ORDERID = O.ORDERID) AS OL, LATERAL(SELECT SUM(CHGAMT) AS ORDCHGTOTAL FROM ORDERCHARGES CHARGES WHERE LINES.ORDERID = O.ORDERID) AS OC, LATERAL(SELECT SUM(TAXAMT) AS TAXTOTAL FROM ORDERTAXES TAXES WHERE TAXES.ORDERID = O.ORDERID) AS OT", select.toString());
    }

    @Test
    public void testValues() throws JSQLParserException {
        String stmt = "SELECT * FROM (VALUES (1, 2), (3, 4)) AS test";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testValues2() throws JSQLParserException {
        String stmt = "SELECT * FROM (VALUES 1, 2, 3, 4) AS test";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testValues3() throws JSQLParserException {
        String stmt = "SELECT * FROM (VALUES 1, 2, 3, 4) AS test(a)";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testValues4() throws JSQLParserException {
        String stmt = "SELECT * FROM (VALUES (1, 2), (3, 4)) AS test(a, b)";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testValues5() throws JSQLParserException {
        String stmt = "SELECT X, Y FROM (VALUES (0, 'a'), (1, 'b')) AS MY_TEMP_TABLE(X, Y)";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testValues6BothVariants() throws JSQLParserException {
        String stmt = "SELECT I FROM (VALUES 1, 2, 3) AS MY_TEMP_TABLE(I) WHERE I IN (SELECT * FROM (VALUES 1, 2) AS TEST)";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testInterval1() throws JSQLParserException {
        String stmt = "SELECT 5 + INTERVAL '3 days'";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testInterval2() throws JSQLParserException {
        String stmt = "SELECT to_timestamp(to_char(now() - INTERVAL '45 MINUTE', 'YYYY-MM-DD-HH24:')) AS START_TIME FROM tab1";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
        Statement st = CCJSqlParserUtil.parse(stmt);
        Select select = ((Select) (st));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertEquals(1, plainSelect.getSelectItems().size());
        SelectExpressionItem item = ((SelectExpressionItem) (plainSelect.getSelectItems().get(0)));
        Function function = ((Function) (item.getExpression()));
        Assert.assertEquals("to_timestamp", function.getName());
        Assert.assertEquals(1, function.getParameters().getExpressions().size());
        Function func2 = ((Function) (function.getParameters().getExpressions().get(0)));
        Assert.assertEquals("to_char", func2.getName());
        Assert.assertEquals(2, func2.getParameters().getExpressions().size());
        Subtraction sub = ((Subtraction) (func2.getParameters().getExpressions().get(0)));
        Assert.assertTrue(((sub.getRightExpression()) instanceof IntervalExpression));
        IntervalExpression iexpr = ((IntervalExpression) (sub.getRightExpression()));
        Assert.assertEquals("'45 MINUTE'", iexpr.getParameter());
    }

    @Test
    public void testInterval3() throws JSQLParserException {
        String stmt = "SELECT 5 + INTERVAL '3' day";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testInterval4() throws JSQLParserException {
        String stmt = "SELECT '2008-12-31 23:59:59' + INTERVAL 1 SECOND";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testInterval5_Issue228() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT ADDDATE(timeColumn1, INTERVAL 420 MINUTES) AS timeColumn1 FROM tbl");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT ADDDATE(timeColumn1, INTERVAL -420 MINUTES) AS timeColumn1 FROM tbl");
    }

    @Test
    public void testMultiValueIn() throws JSQLParserException {
        String stmt = "SELECT * FROM mytable WHERE (a, b, c) IN (SELECT a, b, c FROM mytable2)";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testMultiValueIn2() throws JSQLParserException {
        String stmt = "SELECT * FROM mytable WHERE (trim(a), trim(b)) IN (SELECT a, b FROM mytable2)";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testPivot1() throws JSQLParserException {
        String stmt = "SELECT * FROM mytable PIVOT (count(a) FOR b IN ('val1'))";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testPivot2() throws JSQLParserException {
        String stmt = "SELECT * FROM mytable PIVOT (count(a) FOR b IN (10, 20, 30))";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testPivot3() throws JSQLParserException {
        String stmt = "SELECT * FROM mytable PIVOT (count(a) AS vals FOR b IN (10 AS d1, 20, 30 AS d3))";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testPivot4() throws JSQLParserException {
        String stmt = "SELECT * FROM mytable PIVOT (count(a), sum(b) FOR b IN (10, 20, 30))";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testPivot5() throws JSQLParserException {
        String stmt = "SELECT * FROM mytable PIVOT (count(a) FOR (b, c) IN ((10, 'a'), (20, 'b'), (30, 'c')))";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testPivotXml1() throws JSQLParserException {
        String stmt = "SELECT * FROM mytable PIVOT XML (count(a) FOR b IN ('val1'))";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testPivotXml2() throws JSQLParserException {
        String stmt = "SELECT * FROM mytable PIVOT XML (count(a) FOR b IN (SELECT vals FROM myothertable))";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testPivotXml3() throws JSQLParserException {
        String stmt = "SELECT * FROM mytable PIVOT XML (count(a) FOR b IN (ANY))";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testPivotXmlSubquery1() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM (SELECT times_purchased, state_code FROM customers t) PIVOT (count(state_code) FOR state_code IN ('NY', 'CT', 'NJ', 'FL', 'MO')) ORDER BY times_purchased");
    }

    @Test
    public void testPivotFunction() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT to_char((SELECT col1 FROM (SELECT times_purchased, state_code FROM customers t) PIVOT (count(state_code) FOR state_code IN ('NY', 'CT', 'NJ', 'FL', 'MO')) ORDER BY times_purchased)) FROM DUAL");
    }

    @Test
    public void testPivotWithAlias() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM (SELECT * FROM mytable LEFT JOIN mytable2 ON Factor_ID = Id) f PIVOT (max(f.value) FOR f.factoryCode IN (ZD, COD, SW, PH))");
    }

    @Test
    public void testPivotWithAlias2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM (SELECT * FROM mytable LEFT JOIN mytable2 ON Factor_ID = Id) f PIVOT (max(f.value) FOR f.factoryCode IN (ZD, COD, SW, PH)) d");
    }

    @Test
    public void testPivotWithAlias3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM (SELECT * FROM mytable LEFT JOIN mytable2 ON Factor_ID = Id) PIVOT (max(f.value) FOR f.factoryCode IN (ZD, COD, SW, PH)) d");
    }

    @Test
    public void testPivotWithAlias4() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed(("SELECT * FROM (" + (((("SELECT a.Station_ID stationId, b.Factor_Code factoryCode, a.Value value" + " FROM T_Data_Real a") + " LEFT JOIN T_Bas_Factor b ON a.Factor_ID = b.Id") + ") f ") + "PIVOT (max(f.value) FOR f.factoryCode IN (ZD, COD, SW, PH)) d")));
    }

    @Test
    public void testRegexpLike1() throws JSQLParserException {
        String stmt = "SELECT * FROM mytable WHERE REGEXP_LIKE(first_name, '^Ste(v|ph)en$')";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testRegexpLike2() throws JSQLParserException {
        String stmt = "SELECT CASE WHEN REGEXP_LIKE(first_name, '^Ste(v|ph)en$') THEN 1 ELSE 2 END FROM mytable";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testRegexpMySQL() throws JSQLParserException {
        String stmt = "SELECT * FROM mytable WHERE first_name REGEXP '^Ste(v|ph)en$'";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testRegexpBinaryMySQL() throws JSQLParserException {
        String stmt = "SELECT * FROM mytable WHERE first_name REGEXP BINARY '^Ste(v|ph)en$'";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testRlike() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM mytable WHERE first_name RLIKE '^Ste(v|ph)en$'");
    }

    @Test
    public void testBooleanFunction1() throws JSQLParserException {
        String stmt = "SELECT * FROM mytable WHERE test_func(col1)";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testNamedParameter() throws JSQLParserException {
        String stmt = "SELECT * FROM mytable WHERE b = :param";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
        Statement st = CCJSqlParserUtil.parse(stmt);
        Select select = ((Select) (st));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Expression exp = getRightExpression();
        Assert.assertTrue((exp instanceof JdbcNamedParameter));
        JdbcNamedParameter namedParameter = ((JdbcNamedParameter) (exp));
        Assert.assertEquals("param", namedParameter.getName());
    }

    @Test
    public void testNamedParameter2() throws JSQLParserException {
        String stmt = "SELECT * FROM mytable WHERE a = :param OR a = :param2 AND b = :param3";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
        Statement st = CCJSqlParserUtil.parse(stmt);
        Select select = ((Select) (st));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Expression exp_l = ((BinaryExpression) (plainSelect.getWhere())).getLeftExpression();
        Expression exp_r = getRightExpression();
        Expression exp_rl = ((BinaryExpression) (exp_r)).getLeftExpression();
        Expression exp_rr = getRightExpression();
        Expression exp_param1 = getRightExpression();
        Expression exp_param2 = getRightExpression();
        Expression exp_param3 = getRightExpression();
        Assert.assertTrue((exp_param1 instanceof JdbcNamedParameter));
        Assert.assertTrue((exp_param2 instanceof JdbcNamedParameter));
        Assert.assertTrue((exp_param3 instanceof JdbcNamedParameter));
        JdbcNamedParameter namedParameter1 = ((JdbcNamedParameter) (exp_param1));
        JdbcNamedParameter namedParameter2 = ((JdbcNamedParameter) (exp_param2));
        JdbcNamedParameter namedParameter3 = ((JdbcNamedParameter) (exp_param3));
        Assert.assertEquals("param", namedParameter1.getName());
        Assert.assertEquals("param2", namedParameter2.getName());
        Assert.assertEquals("param3", namedParameter3.getName());
    }

    @Test
    public void testNamedParameter3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM t WHERE c = :from");
    }

    @Test
    public void testComplexUnion1() throws IOException, JSQLParserException {
        String stmt = "(SELECT 'abc-' || coalesce(mytab.a::varchar, '') AS a, mytab.b, mytab.c AS st, mytab.d, mytab.e FROM mytab WHERE mytab.del = 0) UNION (SELECT 'cde-' || coalesce(mytab2.a::varchar, '') AS a, mytab2.b, mytab2.bezeichnung AS c, 0 AS d, 0 AS e FROM mytab2 WHERE mytab2.del = 0)";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testOracleHierarchicalQuery() throws JSQLParserException {
        String stmt = "SELECT last_name, employee_id, manager_id FROM employees CONNECT BY employee_id = manager_id ORDER BY last_name";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testOracleHierarchicalQuery2() throws JSQLParserException {
        String stmt = "SELECT employee_id, last_name, manager_id FROM employees CONNECT BY PRIOR employee_id = manager_id";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testOracleHierarchicalQuery3() throws JSQLParserException {
        String stmt = "SELECT last_name, employee_id, manager_id, LEVEL FROM employees START WITH employee_id = 100 CONNECT BY PRIOR employee_id = manager_id ORDER SIBLINGS BY last_name";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testOracleHierarchicalQuery4() throws JSQLParserException {
        String stmt = "SELECT last_name, employee_id, manager_id, LEVEL FROM employees CONNECT BY PRIOR employee_id = manager_id START WITH employee_id = 100 ORDER SIBLINGS BY last_name";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testOracleHierarchicalQueryIssue196() throws JSQLParserException {
        String stmt = "SELECT num1, num2, level FROM carol_tmp START WITH num2 = 1008 CONNECT BY num2 = PRIOR num1 ORDER BY level DESC";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testPostgreSQLRegExpCaseSensitiveMatch() throws JSQLParserException {
        String stmt = "SELECT a, b FROM foo WHERE a ~ '[help].*'";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testPostgreSQLRegExpCaseSensitiveMatch2() throws JSQLParserException {
        String stmt = "SELECT a, b FROM foo WHERE a ~* '[help].*'";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testPostgreSQLRegExpCaseSensitiveMatch3() throws JSQLParserException {
        String stmt = "SELECT a, b FROM foo WHERE a !~ '[help].*'";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testPostgreSQLRegExpCaseSensitiveMatch4() throws JSQLParserException {
        String stmt = "SELECT a, b FROM foo WHERE a !~* '[help].*'";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testReservedKeyword() throws JSQLParserException {
        final String statement = "SELECT cast, do, extract, first, following, last, materialized, nulls, partition, range, row, rows, siblings, value, xml FROM tableName";// all of these are legal in SQL server; 'row' and 'rows' are not legal on Oracle, though;

        final Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testReservedKeyword2() throws JSQLParserException {
        final String stmt = "SELECT open FROM tableName";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testReservedKeyword3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM mytable1 t JOIN mytable2 AS prior ON t.id = prior.id");
    }

    @Test
    public void testCharacterSetClause() throws JSQLParserException {
        String stmt = "SELECT DISTINCT CAST(`view0`.`nick2` AS CHAR (8000) CHARACTER SET utf8) AS `v0` FROM people `view0` WHERE `view0`.`nick2` IS NOT NULL";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testNotEqualsTo() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM foo WHERE a != b");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM foo WHERE a <> b");
    }

    @Test
    public void testJsonExpression() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT data->'images'->'thumbnail'->'url' AS thumb FROM instagram");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM sales WHERE sale->'items'->>'description' = 'milk'");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM sales WHERE sale->'items'->>'quantity' = 12::TEXT");
        // assertSqlCanBeParsedAndDeparsed("SELECT * FROM sales WHERE CAST(sale->'items'->>'quantity' AS integer)  = 2");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT SUM(CAST(sale->'items'->>'quantity' AS integer)) AS total_quantity_sold FROM sales");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT sale->>'items' FROM sales");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT json_typeof(sale->'items'), json_typeof(sale->'items'->'quantity') FROM sales");
        // The following staments can be parsed but not deparsed
        for (String statement : new String[]{ "SELECT doc->\'site_name\' FROM websites WHERE doc @> \'{\"tags\":[{\"term\":\"paris\"}, {\"term\":\"food\"}]}\'", "SELECT * FROM sales where sale ->\'items\' @> \'[{\"count\":0}]\'", "SELECT * FROM sales where sale ->'items' ? 'name'", "SELECT * FROM sales where sale ->'items' -# 'name'" }) {
            Select select = ((Select) (parserManager.parse(new StringReader(statement))));
            TestUtils.assertStatementCanBeDeparsedAs(select, statement, true);
        }
    }

    @Test
    public void testSqlNoCache() throws JSQLParserException {
        String stmt = "SELECT SQL_NO_CACHE sales.date FROM sales";
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt);
    }

    @Test
    public void testSelectForUpdate() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM user_table FOR UPDATE");
    }

    @Test
    public void testSelectForUpdate2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM emp WHERE empno = ? FOR UPDATE");
    }

    @Test
    public void testSelectJoin() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed(("SELECT pg_class.relname, pg_attribute.attname, pg_constraint.conname " + ((("FROM pg_constraint JOIN pg_class ON pg_class.oid = pg_constraint.conrelid" + " JOIN pg_attribute ON pg_attribute.attrelid = pg_constraint.conrelid") + " WHERE pg_constraint.contype = 'u' AND (pg_attribute.attnum = ANY(pg_constraint.conkey))") + " ORDER BY pg_constraint.conname")));
    }

    @Test
    public void testSelectJoin2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM pg_constraint WHERE pg_attribute.attnum = ANY(pg_constraint.conkey)");
    }

    @Test
    public void testAnyConditionSubSelect() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT e1.empno, e1.sal FROM emp e1 WHERE e1.sal > ANY (SELECT e2.sal FROM emp e2 WHERE e2.deptno = 10)");
    }

    @Test
    public void testAllConditionSubSelect() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT e1.empno, e1.sal FROM emp e1 WHERE e1.sal > ALL (SELECT e2.sal FROM emp e2 WHERE e2.deptno = 10)");
    }

    @Test
    public void testSelectOracleColl() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM the_table tt WHERE TT.COL1 = lines(idx).COL1");
    }

    @Test
    public void testSelectInnerWith() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM (WITH actor AS (SELECT 'a' aid FROM DUAL) SELECT aid FROM actor)");
    }

    @Test
    public void testSelectWithinGroup() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT LISTAGG(col1, '##') WITHIN GROUP (ORDER BY col1) FROM table1");
    }

    @Test
    public void testSelectUserVariable() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT @col FROM t1");
    }

    @Test
    public void testSelectNumericBind() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT a FROM b WHERE c = :1");
    }

    @Test
    public void testSelectBrackets() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT avg((123.250)::numeric)");
    }

    @Test
    public void testSelectBrackets2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT (EXTRACT(epoch FROM age(d1, d2)) / 2)::numeric");
    }

    @Test
    public void testSelectBrackets3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT avg((EXTRACT(epoch FROM age(d1, d2)) / 2)::numeric)");
    }

    @Test
    public void testSelectBrackets4() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT (1 / 2)::numeric");
    }

    @Test
    public void testSelectForUpdateOfTable() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT foo.*, bar.* FROM foo, bar WHERE foo.id = bar.foo_id FOR UPDATE OF foo");
    }

    @Test
    public void testSelectWithBrackets() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("(SELECT 1 FROM mytable)");
    }

    @Test
    public void testSelectWithBrackets2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("(SELECT 1)");
    }

    @Test
    public void testSelectWithoutFrom() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT footable.foocolumn");
    }

    @Test
    public void testSelectKeywordPercent() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT percent FROM MY_TABLE");
    }

    @Test
    public void testSelectJPQLPositionalParameter() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT email FROM users WHERE (type LIKE 'B') AND (username LIKE ?1)");
    }

    @Test
    public void testSelectKeep() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT col1, min(col2) KEEP (DENSE_RANK FIRST ORDER BY col3), col4 FROM table1 GROUP BY col5 ORDER BY col3");
    }

    @Test
    public void testSelectKeepOver() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT MIN(salary) KEEP (DENSE_RANK FIRST ORDER BY commission_pct) OVER (PARTITION BY department_id ) \"Worst\" FROM employees ORDER BY department_id, salary");
    }

    @Test
    public void testGroupConcat() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT student_name, GROUP_CONCAT(DISTINCT test_score ORDER BY test_score DESC SEPARATOR ' ') FROM student GROUP BY student_name");
    }

    @Test
    public void testRowConstructor1() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM t1 WHERE (col1, col2) = (SELECT col3, col4 FROM t2 WHERE id = 10)");
    }

    @Test
    public void testRowConstructor2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM t1 WHERE ROW(col1, col2) = (SELECT col3, col4 FROM t2 WHERE id = 10)");
    }

    @Test
    public void testIssue154() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT d.id, d.uuid, d.name, d.amount, d.percentage, d.modified_time FROM discount d LEFT OUTER JOIN discount_category dc ON d.id = dc.discount_id WHERE merchant_id = ? AND deleted = ? AND dc.discount_id IS NULL AND modified_time < ? AND modified_time >= ? ORDER BY modified_time");
    }

    @Test
    public void testIssue154_2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT r.id, r.uuid, r.name, r.system_role FROM role r WHERE r.merchant_id = ? AND r.deleted_time IS NULL ORDER BY r.id DESC");
    }

    @Test
    public void testIssue160_signedParameter() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT start_date WHERE start_date > DATEADD(HH, -?, GETDATE())");
    }

    @Test
    public void testIssue160_signedParameter2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM mytable WHERE -? = 5");
    }

    @Test
    public void testIssue162_doubleUserVar() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT @@SPID AS ID, SYSTEM_USER AS \"Login Name\", USER AS \"User Name\"");
    }

    @Test
    public void testIssue167_singleQuoteEscape() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT 'a'");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT ''''");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT \'\\\'\'");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT 'ab''ab'");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT \'ab\\\'ab\'");
    }

    @Test
    public void testIssue167_singleQuoteEscape2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT \'\\\'\'\'");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT \'\\\\\\\'\'");
    }

    @Test
    public void testIssue77_singleQuoteEscape2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT \'test\\\'\' FROM dual");
    }

    @Test
    public void testIssue223_singleQuoteEscape() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT \'\\\'test\\\'\'");
    }

    @Test
    public void testOracleHint() throws JSQLParserException {
        TestUtils.assertOracleHintExists("SELECT /*+ SOMEHINT */ * FROM mytable", true, "SOMEHINT");
        TestUtils.assertOracleHintExists("SELECT /*+ MORE HINTS POSSIBLE */ * FROM mytable", true, "MORE HINTS POSSIBLE");
        TestUtils.assertOracleHintExists("SELECT /*+   MORE\nHINTS\t\nPOSSIBLE  */ * FROM mytable", true, "MORE\nHINTS\t\nPOSSIBLE");
        TestUtils.assertOracleHintExists("SELECT /*+ leading(sn di md sh ot) cardinality(ot 1000) */ c, b FROM mytable", true, "leading(sn di md sh ot) cardinality(ot 1000)");
        TestUtils.assertOracleHintExists(("SELECT /*+ ORDERED INDEX (b, jl_br_balances_n1) USE_NL (j b) \n" + ((("           USE_NL (glcc glf) USE_MERGE (gp gsb) */\n" + " b.application_id\n") + "FROM  jl_br_journals j,\n") + "      po_vendors p")), true, ("ORDERED INDEX (b, jl_br_balances_n1) USE_NL (j b) \n" + "           USE_NL (glcc glf) USE_MERGE (gp gsb)"));
        TestUtils.assertOracleHintExists(("SELECT /*+ROWID(emp)*/ /*+ THIS IS NOT HINT! ***/ * \n" + ("FROM emp \n" + "WHERE rowid > 'AAAAtkAABAAAFNTAAA' AND empno = 155")), false, "ROWID(emp)");
        TestUtils.assertOracleHintExists(("SELECT /*+ INDEX(patients sex_index) use sex_index because there are few\n" + (("   male patients  */ name, height, weight\n" + "FROM patients\n") + "WHERE sex = 'm'")), true, "INDEX(patients sex_index) use sex_index because there are few\n   male patients");
        TestUtils.assertOracleHintExists(("SELECT /*+INDEX_COMBINE(emp sal_bmi hiredate_bmi)*/ * \n" + ("FROM emp  \n" + "WHERE sal < 50000 AND hiredate < '01-JAN-1990'")), true, "INDEX_COMBINE(emp sal_bmi hiredate_bmi)");
        TestUtils.assertOracleHintExists(("SELECT --+ CLUSTER \n" + ((("emp.ename, deptno\n" + "FROM emp, dept\n") + "WHERE deptno = 10 \n") + "AND emp.deptno = dept.deptno")), true, "CLUSTER");
        TestUtils.assertOracleHintExists("SELECT --+ CLUSTER \n --+ some other comment, not hint\n /* even more comments */ * from dual", false, "CLUSTER");
        TestUtils.assertOracleHintExists("(SELECT * from t1) UNION (select /*+ CLUSTER */ * from dual)", true, null, "CLUSTER");
        TestUtils.assertOracleHintExists("(SELECT * from t1) UNION (select /*+ CLUSTER */ * from dual) UNION (select * from dual)", true, null, "CLUSTER", null);
        TestUtils.assertOracleHintExists("(SELECT --+ HINT1 HINT2 HINT3\n * from t1) UNION (select /*+ HINT4 HINT5 */ * from dual)", true, "HINT1 HINT2 HINT3", "HINT4 HINT5");
    }

    @Test
    public void testOracleHintExpression() throws JSQLParserException {
        String statement = "SELECT --+ HINT\n * FROM tab1";
        Statement parsed = parserManager.parse(new StringReader(statement));
        Assert.assertEquals(statement, parsed.toString());
        PlainSelect plainSelect = ((PlainSelect) (getSelectBody()));
        TestUtils.assertExpressionCanBeDeparsedAs(plainSelect.getOracleHint(), "--+ HINT\n");
    }

    @Test
    public void testTableFunctionWithNoParams() throws Exception {
        final String statement = "SELECT f2 FROM SOME_FUNCTION()";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertTrue(((plainSelect.getFromItem()) instanceof TableFunction));
        TableFunction fromItem = ((TableFunction) (plainSelect.getFromItem()));
        Function function = fromItem.getFunction();
        Assert.assertNotNull(function);
        Assert.assertEquals("SOME_FUNCTION", function.getName());
        Assert.assertNull(function.getParameters());
        Assert.assertNull(fromItem.getAlias());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testTableFunctionWithParams() throws Exception {
        final String statement = "SELECT f2 FROM SOME_FUNCTION(1, 'val')";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertTrue(((plainSelect.getFromItem()) instanceof TableFunction));
        TableFunction fromItem = ((TableFunction) (plainSelect.getFromItem()));
        Function function = fromItem.getFunction();
        Assert.assertNotNull(function);
        Assert.assertEquals("SOME_FUNCTION", function.getName());
        // verify params
        Assert.assertNotNull(function.getParameters());
        List<Expression> expressions = function.getParameters().getExpressions();
        Assert.assertEquals(2, expressions.size());
        Expression firstParam = expressions.get(0);
        Assert.assertNotNull(firstParam);
        Assert.assertTrue((firstParam instanceof LongValue));
        Assert.assertEquals(1L, getValue());
        Expression secondParam = expressions.get(1);
        Assert.assertNotNull(secondParam);
        Assert.assertTrue((secondParam instanceof StringValue));
        Assert.assertEquals("val", getValue());
        Assert.assertNull(fromItem.getAlias());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testTableFunctionWithAlias() throws Exception {
        final String statement = "SELECT f2 FROM SOME_FUNCTION() AS z";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        PlainSelect plainSelect = ((PlainSelect) (select.getSelectBody()));
        Assert.assertTrue(((plainSelect.getFromItem()) instanceof TableFunction));
        TableFunction fromItem = ((TableFunction) (plainSelect.getFromItem()));
        Function function = fromItem.getFunction();
        Assert.assertNotNull(function);
        Assert.assertEquals("SOME_FUNCTION", function.getName());
        Assert.assertNull(function.getParameters());
        Assert.assertNotNull(fromItem.getAlias());
        Assert.assertEquals("z", getName());
        TestUtils.assertStatementCanBeDeparsedAs(select, statement);
    }

    @Test
    public void testIssue151_tableFunction() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM tables a LEFT JOIN getdata() b ON a.id = b.id");
    }

    @Test
    public void testIssue217_keywordSeparator() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT Separator");
    }

    @Test
    public void testIssue215_possibleEndlessParsing() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT (CASE WHEN ((value LIKE '%t1%') OR (value LIKE '%t2%')) THEN 't1s' WHEN ((((((((((((((((((((((((((((value LIKE '%t3%') OR (value LIKE '%t3%')) OR (value LIKE '%t3%')) OR (value LIKE '%t4%')) OR (value LIKE '%t4%')) OR (value LIKE '%t5%')) OR (value LIKE '%t6%')) OR (value LIKE '%t6%')) OR (value LIKE '%t7%')) OR (value LIKE '%t7%')) OR (value LIKE '%t7%')) OR (value LIKE '%t8%')) OR (value LIKE '%t8%')) OR (value LIKE '%CTO%')) OR (value LIKE '%cto%')) OR (value LIKE '%Cto%')) OR (value LIKE '%t9%')) OR (value LIKE '%t9%')) OR (value LIKE '%COO%')) OR (value LIKE '%coo%')) OR (value LIKE '%Coo%')) OR (value LIKE '%t10%')) OR (value LIKE '%t10%')) OR (value LIKE '%CIO%')) OR (value LIKE '%cio%')) OR (value LIKE '%Cio%')) OR (value LIKE '%t11%')) OR (value LIKE '%t11%')) THEN 't' WHEN ((((value LIKE '%t12%') OR (value LIKE '%t12%')) OR (value LIKE '%VP%')) OR (value LIKE '%vp%')) THEN 'Vice t12s' WHEN ((((((value LIKE '% IT %') OR (value LIKE '%t13%')) OR (value LIKE '%t13%')) OR (value LIKE '% it %')) OR (value LIKE '%tech%')) OR (value LIKE '%Tech%')) THEN 'IT' WHEN ((((value LIKE '%Analyst%') OR (value LIKE '%t14%')) OR (value LIKE '%Analytic%')) OR (value LIKE '%analytic%')) THEN 'Analysts' WHEN ((value LIKE '%Manager%') OR (value LIKE '%manager%')) THEN 't15' ELSE 'Other' END) FROM tab1");
    }

    @Test
    public void testIssue215_possibleEndlessParsing2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT (CASE WHEN ((value LIKE '%t1%') OR (value LIKE '%t2%')) THEN 't1s' ELSE 'Other' END) FROM tab1");
    }

    @Test
    public void testIssue215_possibleEndlessParsing3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM mytable WHERE ((((((((((((((((((((((((((((value LIKE '%t3%') OR (value LIKE '%t3%')) OR (value LIKE '%t3%')) OR (value LIKE '%t4%')) OR (value LIKE '%t4%')) OR (value LIKE '%t5%')) OR (value LIKE '%t6%')) OR (value LIKE '%t6%')) OR (value LIKE '%t7%')) OR (value LIKE '%t7%')) OR (value LIKE '%t7%')) OR (value LIKE '%t8%')) OR (value LIKE '%t8%')) OR (value LIKE '%CTO%')) OR (value LIKE '%cto%')) OR (value LIKE '%Cto%')) OR (value LIKE '%t9%')) OR (value LIKE '%t9%')) OR (value LIKE '%COO%')) OR (value LIKE '%coo%')) OR (value LIKE '%Coo%')) OR (value LIKE '%t10%')) OR (value LIKE '%t10%')) OR (value LIKE '%CIO%')) OR (value LIKE '%cio%')) OR (value LIKE '%Cio%')) OR (value LIKE '%t11%')) OR (value LIKE '%t11%'))");
    }

    @Test
    public void testIssue215_possibleEndlessParsing4() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM mytable WHERE ((value LIKE '%t3%') OR (value LIKE '%t3%'))");
    }

    @Test
    public void testIssue215_possibleEndlessParsing5() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM mytable WHERE ((((((value LIKE '%t3%') OR (value LIKE '%t3%')) OR (value LIKE '%t3%')) OR (value LIKE '%t4%')) OR (value LIKE '%t4%')) OR (value LIKE '%t5%'))");
    }

    @Test
    public void testIssue215_possibleEndlessParsing6() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM mytable WHERE (((((((((((((value LIKE '%t3%') OR (value LIKE '%t3%')) OR (value LIKE '%t3%')) OR (value LIKE '%t4%')) OR (value LIKE '%t4%')) OR (value LIKE '%t5%')) OR (value LIKE '%t6%')) OR (value LIKE '%t6%')) OR (value LIKE '%t7%')) OR (value LIKE '%t7%')) OR (value LIKE '%t7%')) OR (value LIKE '%t8%')) OR (value LIKE '%t8%'))");
    }

    @Test
    public void testIssue215_possibleEndlessParsing7() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM mytable WHERE (((((((((((((((((((((value LIKE '%t3%') OR (value LIKE '%t3%')) OR (value LIKE '%t3%')) OR (value LIKE '%t4%')) OR (value LIKE '%t4%')) OR (value LIKE '%t5%')) OR (value LIKE '%t6%')) OR (value LIKE '%t6%')) OR (value LIKE '%t7%')) OR (value LIKE '%t7%')) OR (value LIKE '%t7%')) OR (value LIKE '%t8%')) OR (value LIKE '%t8%')) OR (value LIKE '%CTO%')) OR (value LIKE '%cto%')) OR (value LIKE '%Cto%')) OR (value LIKE '%t9%')) OR (value LIKE '%t9%')) OR (value LIKE '%COO%')) OR (value LIKE '%coo%')) OR (value LIKE '%Coo%'))");
    }

    @Test
    public void testIssue230_cascadeKeyword() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT t.cascade AS cas FROM t");
    }

    @Test
    public void testBooleanValue() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT col FROM t WHERE a");
    }

    @Test
    public void testBooleanValue2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT col FROM t WHERE 3 < 5 AND a");
    }

    @Test
    public void testNotWithoutParenthesisIssue234() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT count(*) FROM \"Persons\" WHERE NOT \"F_NAME\" = \'John\'");
    }

    @Test
    public void testWhereIssue240_1() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT count(*) FROM mytable WHERE 1");
    }

    @Test
    public void testWhereIssue240_0() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT count(*) FROM mytable WHERE 0");
    }

    @Test
    public void testCastToSignedInteger() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CAST(contact_id AS SIGNED INTEGER) FROM contact WHERE contact_id = 20");
    }

    // @Test
    // public void testWhereIssue240_notBoolean() {
    // try {
    // CCJSqlParserUtil.parse("SELECT count(*) FROM mytable WHERE 5");
    // fail("should not be parsed");
    // } catch (JSQLParserException ex) {
    // //expected to fail
    // }
    // }
    @Test
    public void testWhereIssue240_true() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT count(*) FROM mytable WHERE true");
    }

    @Test
    public void testWhereIssue240_false() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT count(*) FROM mytable WHERE false");
    }

    @Test
    public void testWhereIssue241KeywordEnd() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT l.end FROM lessons l");
    }

    @Test
    public void testSpeedTestIssue235() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM tbl WHERE (ROUND((((((period_diff(date_format(tbl.CD, '%Y%m'), date_format(SUBTIME(CURRENT_TIMESTAMP(), 25200), '%Y%m')) + month(SUBTIME(CURRENT_TIMESTAMP(), 25200))) - MONTH('2012-02-01')) - 1) / 3) - ROUND((((month(SUBTIME(CURRENT_TIMESTAMP(),25200)) - MONTH('2012-02-01')) - 1) / 3)))) = -3)", true);
    }

    @Test
    public void testSpeedTestIssue235_2() throws IOException, JSQLParserException {
        String stmt = IOUtils.toString(SelectTest.class.getResourceAsStream("large-sql-issue-235.txt"));
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt, true);
    }

    @Test
    public void testCastVarCharMaxIssue245() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CAST('foo' AS NVARCHAR (MAX))");
    }

    @Test
    public void testNestedFunctionCallIssue253() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT (replace_regex(replace_regex(replace_regex(get_json_string(a_column, \'value\'), \'\\n\', \' \'), \'\\r\', \' \'), \'\\\\\', \'\\\\\\\\\')) FROM a_table WHERE b_column = \'value\'");
    }

    @Test
    public void testEscapedBackslashIssue253() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT replace_regex(\'test\', \'\\\\\', \'\\\\\\\\\')");
    }

    @Test
    public void testKeywordTableIssue261() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT column_value FROM table(VARCHAR_LIST_TYPE())");
    }

    @Test
    public void testTopExpressionIssue243() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT TOP (? + 1) * FROM MyTable");
    }

    @Test
    public void testTopExpressionIssue243_2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT TOP (CAST(? AS INT)) * FROM MyTable");
    }

    @Test
    public void testFunctionIssue284() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT NVL((SELECT 1 FROM DUAL), 1) AS A FROM TEST1");
    }

    @Test
    public void testFunctionDateTimeValues() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM tab1 WHERE a > TIMESTAMP '2004-04-30 04:05:34.56'");
    }

    @Test
    public void testPR73() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT date_part('day', TIMESTAMP '2001-02-16 20:38:40')");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT EXTRACT(year FROM DATE '2001-02-16')");
    }

    @Test
    public void testUniqueInsteadOfDistinctIssue299() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT UNIQUE trunc(timez(ludate)+ 8/24) bus_dt, j.object j_name , timez(j.starttime) START_TIME , timez(j.endtime) END_TIME FROM TEST_1 j", true);
    }

    @Test
    public void testProblemSqlIssue265() throws IOException, JSQLParserException {
        String sqls = IOUtils.toString(SelectTest.class.getResourceAsStream("large-sql-with-issue-265.txt"));
        Statements stmts = CCJSqlParserUtil.parseStatements(sqls);
        Assert.assertEquals(2, stmts.getStatements().size());
    }

    @Test
    public void testProblemSqlIssue330() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT COUNT(*) FROM C_Invoice WHERE IsSOTrx='Y' AND (Processed='N' OR Updated>(current_timestamp - CAST('90 days' AS interval))) AND C_Invoice.AD_Client_ID IN(0,1010016) AND C_Invoice.AD_Org_ID IN(0,1010053,1010095,1010094)", true);
    }

    @Test
    public void testProblemSqlIssue330_2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CAST('90 days' AS interval)");
    }

    // won't fix due to lookahead impact on parser
    // @Test public void testKeywordOrderAsColumnnameIssue333() throws JSQLParserException {
    // assertSqlCanBeParsedAndDeparsed("SELECT choice.response_choice_id AS uuid, choice.digit AS digit, choice.text_response AS textResponse, choice.voice_prompt AS voicePrompt, choice.action AS action, choice.contribution AS contribution, choice.order_num AS order, choice.description AS description, choice.is_join_conference AS joinConference, choice.voice_prompt_language_code AS voicePromptLanguageCode, choice.text_response_language_code AS textResponseLanguageCode, choice.description_language_code AS descriptionLanguageCode, choice.rec_phrase AS recordingPhrase FROM response_choices choice WHERE choice.presentation_id = ? ORDER BY choice.order_num", true);
    // }
    @Test
    public void testProblemKeywordCommitIssue341() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT id, commit FROM table1");
    }

    @Test
    public void testProblemSqlIssue352() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT @rowNO from (SELECT @rowNO from dual) r", true);
    }

    @Test
    public void testProblemIsIssue331() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT C_DocType.C_DocType_ID,NULL,COALESCE(C_DocType_Trl.Name,C_DocType.Name) AS Name,C_DocType.IsActive FROM C_DocType LEFT JOIN C_DocType_TRL ON (C_DocType.C_DocType_ID=C_DocType_Trl.C_DocType_ID AND C_DocType_Trl.AD_Language='es_AR') WHERE C_DocType.AD_Client_ID=1010016 AND C_DocType.AD_Client_ID IN (0,1010016) AND C_DocType.c_doctype_id in ( select c_doctype2.c_doctype_id from c_doctype as c_doctype2 where substring( c_doctype2.printname,6, length(c_doctype2.printname) ) = ( select letra from c_letra_comprobante as clc where clc.c_letra_comprobante_id = 1010039) ) AND ( (1010094!=0 AND C_DocType.ad_org_id = 1010094) OR 1010094=0 ) ORDER BY 3 LIMIT 2000", true);
    }

    @Test
    public void testProblemIssue375() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("select n.nspname, c.relname, a.attname, a.atttypid, t.typname, a.attnum, a.attlen, a.atttypmod, a.attnotnull, c.relhasrules, c.relkind, c.oid, pg_get_expr(d.adbin, d.adrelid), case t.typtype when 'd' then t.typbasetype else 0 end, t.typtypmod, c.relhasoids from (((pg_catalog.pg_class c inner join pg_catalog.pg_namespace n on n.oid = c.relnamespace and c.relname = 'business' and n.nspname = 'public') inner join pg_catalog.pg_attribute a on (not a.attisdropped) and a.attnum > 0 and a.attrelid = c.oid) inner join pg_catalog.pg_type t on t.oid = a.atttypid) left outer join pg_attrdef d on a.atthasdef and d.adrelid = a.attrelid and d.adnum = a.attnum order by n.nspname, c.relname, attnum", true);
    }

    @Test
    public void testProblemIssue375Simplified() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("select * from (((pg_catalog.pg_class c inner join pg_catalog.pg_namespace n on n.oid = c.relnamespace and c.relname = 'business' and n.nspname = 'public') inner join pg_catalog.pg_attribute a on (not a.attisdropped) and a.attnum > 0 and a.attrelid = c.oid) inner join pg_catalog.pg_type t on t.oid = a.atttypid) left outer join pg_attrdef d on a.atthasdef and d.adrelid = a.attrelid and d.adnum = a.attnum order by n.nspname, c.relname, attnum", true);
    }

    @Test
    public void testProblemIssue375Simplified2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("select * from (pg_catalog.pg_class c inner join pg_catalog.pg_namespace n on n.oid = c.relnamespace and c.relname = 'business' and n.nspname = 'public') inner join pg_catalog.pg_attribute a on (not a.attisdropped) and a.attnum > 0 and a.attrelid = c.oid", true);
    }

    // @Test public void testProblemIssue377() throws Exception {
    // try {
    // assertSqlCanBeParsedAndDeparsed("select 'yelp'::name as pktable_cat, n2.nspname as pktable_schem, c2.relname as pktable_name, a2.attname as pkcolumn_name, 'yelp'::name as fktable_cat, n1.nspname as fktable_schem, c1.relname as fktable_name, a1.attname as fkcolumn_name, i::int2 as key_seq, case ref.confupdtype when 'c' then 0::int2 when 'n' then 2::int2 when 'd' then 4::int2 when 'r' then 1::int2 else 3::int2 end as update_rule, case ref.confdeltype when 'c' then 0::int2 when 'n' then 2::int2 when 'd' then 4::int2 when 'r' then 1::int2 else 3::int2 end as delete_rule, ref.conname as fk_name, cn.conname as pk_name, case when ref.condeferrable then case when ref.condeferred then 5::int2 else 6::int2 end else 7::int2 end as deferrablity from ((((((( (select cn.oid, conrelid, conkey, confrelid, confkey, generate_series(array_lower(conkey, 1), array_upper(conkey, 1)) as i, confupdtype, confdeltype, conname, condeferrable, condeferred from pg_catalog.pg_constraint cn, pg_catalog.pg_class c, pg_catalog.pg_namespace n where contype = 'f' and conrelid = c.oid and relname = 'business' and n.oid = c.relnamespace and n.nspname = 'public' ) ref inner join pg_catalog.pg_class c1 on c1.oid = ref.conrelid) inner join pg_catalog.pg_namespace n1 on n1.oid = c1.relnamespace) inner join pg_catalog.pg_attribute a1 on a1.attrelid = c1.oid and a1.attnum = conkey[i]) inner join pg_catalog.pg_class c2 on c2.oid = ref.confrelid) inner join pg_catalog.pg_namespace n2 on n2.oid = c2.relnamespace) inner join pg_catalog.pg_attribute a2 on a2.attrelid = c2.oid and a2.attnum = confkey[i]) left outer join pg_catalog.pg_constraint cn on cn.conrelid = ref.confrelid and cn.contype = 'p') order by ref.oid, ref.i", true);
    // } catch (Exception ex) {
    // ex.printStackTrace();
    // throw ex;
    // }
    // }
    @Test
    public void testProblemInNotInProblemIssue379() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT rank FROM DBObjects WHERE rank NOT IN (0, 1)");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT rank FROM DBObjects WHERE rank IN (0, 1)");
    }

    @Test
    public void testProblemLargeNumbersIssue390() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM student WHERE student_no = 20161114000000035001");
    }

    @Test
    public void testKeyWorkInsertIssue393() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT insert(\"aaaabbb\", 4, 4, \"****\")");
    }

    @Test
    public void testKeyWorkReplaceIssue393() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT replace(\"aaaabbb\", 4, 4, \"****\")");
    }

    /**
     * Validates that a SELECT with FOR UPDATE WAIT <TIMEOUT> can be parsed and deparsed
     */
    @Test
    public void testForUpdateWaitParseDeparse() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM mytable FOR UPDATE WAIT 60");
    }

    /**
     * Validates that a SELECT with FOR UPDATE WAIT <TIMEOUT> correctly sets a {@link Wait} with the
     * correct timeout value.
     */
    @Test
    public void testForUpdateWaitWithTimeout() throws JSQLParserException {
        String statement = "SELECT * FROM mytable FOR UPDATE WAIT 60";
        Select select = ((Select) (parserManager.parse(new StringReader(statement))));
        PlainSelect ps = ((PlainSelect) (select.getSelectBody()));
        Wait wait = ps.getWait();
        Assert.assertNotNull("wait should not be null", wait);
        long waitTime = wait.getTimeout();
        Assert.assertEquals("wait time should be 60", waitTime, 60L);
    }

    // @Test public void testSubSelectFailsIssue394() throws JSQLParserException {
    // assertSqlCanBeParsedAndDeparsed("select aa.* , t.* from accenter.all aa, (select a.* from pacioli.emc_plan a) t");
    // }
    // 
    // @Test public void testSubSelectFailsIssue394_2() throws JSQLParserException {
    // assertSqlCanBeParsedAndDeparsed("select * from all");
    // }
    @Test
    public void testMysqlIndexHints() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT column FROM testtable AS t0 USE INDEX (index1)");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT column FROM testtable AS t0 IGNORE INDEX (index1)");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT column FROM testtable AS t0 FORCE INDEX (index1)");
    }

    @Test
    public void testMysqlIndexHintsWithJoins() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT column FROM table0 t0 INNER JOIN table1 t1 USE INDEX (index1)");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT column FROM table0 t0 INNER JOIN table1 t1 IGNORE INDEX (index1)");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT column FROM table0 t0 INNER JOIN table1 t1 FORCE INDEX (index1)");
    }

    @Test
    public void testMysqlMultipleIndexHints() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT column FROM testtable AS t0 USE INDEX (index1,index2)");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT column FROM testtable AS t0 IGNORE INDEX (index1,index2)");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT column FROM testtable AS t0 FORCE INDEX (index1,index2)");
    }

    @Test
    public void testProblemIssue435() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT if(z, 'a', 'b') AS business_type FROM mytable1");
    }

    @Test
    public void testProblemIssue437Index() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("select count(id) from p_custom_data ignore index(pri) where tenant_id=28257 and entity_id=92609 and delete_flg=0 and ( (dbc_relation_2 = 52701) and (dbc_relation_2 in ( select id from a_order where tenant_id = 28257 and 1=1 ) ) ) order by id desc, id desc", true);
    }

    @Test
    public void testProblemIssue445() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT E.ID_NUMBER, row_number() OVER (PARTITION BY E.ID_NUMBER ORDER BY E.DEFINED_UPDATED DESC) rn FROM T_EMPLOYMENT E");
    }

    @Test
    public void testProblemIssue485Date() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM tab WHERE tab.date = :date");
    }

    @Test
    public void testGroupByProblemIssue482() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT SUM(orderTotalValue) AS value, MONTH(invoiceDate) AS month, YEAR(invoiceDate) AS year FROM invoice.Invoices WHERE projectID = 1 GROUP BY MONTH(invoiceDate), YEAR(invoiceDate) ORDER BY YEAR(invoiceDate) DESC, MONTH(invoiceDate) DESC");
    }

    @Test
    public void testIssue512() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM #tab1");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM tab#tab1");
    }

    @Test
    public void testIssue512_2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM $tab1");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM #$tab#tab1");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM #$tab1#");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM $#tab1#");
    }

    @Test
    public void testIssue514() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT listagg(c1, ';') WITHIN GROUP (PARTITION BY 1 ORDER BY 1) col FROM dual");
    }

    @Test
    public void testIssue508LeftRightBitwiseShift() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT 1 << 1");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT 1 >> 1");
    }

    @Test
    public void testIssue522() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CASE mr.required_quantity - mr.quantity_issued WHEN 0 THEN NULL ELSE CASE SIGN(mr.required_quantity) WHEN -1 * SIGN(mr.quantity_issued) THEN mr.required_quantity - mr.quantity_issued ELSE CASE SIGN(ABS(mr.required_quantity) - ABS(mr.quantity_issued)) WHEN -1 THEN NULL ELSE mr.required_quantity - mr.quantity_issued END END END quantity_open FROM mytable", true);
    }

    @Test
    public void testIssue522_2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT -1 * SIGN(mr.quantity_issued) FROM mytable");
    }

    @Test
    public void testIssue522_3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CASE SIGN(mr.required_quantity) WHEN -1 * SIGN(mr.quantity_issued) THEN mr.required_quantity - mr.quantity_issued  ELSE 5 END quantity_open FROM mytable", true);
    }

    @Test
    public void testIssue522_4() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CASE a + b WHEN -1 * 5 THEN 1 ELSE CASE b + c WHEN -1 * 6 THEN 2 ELSE 3 END END");
    }

    @Test
    public void testIssue554() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT T.INDEX AS INDEX133_ FROM myTable T");
    }

    @Test
    public void testIssue567KeywordPrimary() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT primary, secondary FROM info");
    }

    @Test
    public void testIssue572TaskReplacement() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT task_id AS \"Task Id\" FROM testtable");
    }

    @Test
    public void testIssue566LargeView() throws IOException, JSQLParserException {
        String stmt = IOUtils.toString(SelectTest.class.getResourceAsStream("large-sql-issue-566.txt"));
        TestUtils.assertSqlCanBeParsedAndDeparsed(stmt, true);
    }

    @Test
    public void testIssue566PostgreSQLEscaped() throws IOException, JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT E'test'");
    }

    @Test
    public void testEscaped() throws IOException, JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT _utf8'testvalue'");
    }

    @Test
    public void testIssue563MultiSubJoin() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT c FROM ((SELECT a FROM t) JOIN (SELECT b FROM t2) ON a = B JOIN (SELECT c FROM t3) ON b = c)");
    }

    @Test
    public void testIssue563MultiSubJoin_2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT c FROM ((SELECT a FROM t))");
    }

    @Test
    public void testIssue582NumericConstants() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT x'009fd'");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT X'009fd'");
    }

    @Test
    public void testIssue583CharacterLiteralAsAlias() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CASE WHEN T.ISC = 1 THEN T.EXTDESC WHEN T.b = 2 THEN '2' ELSE T.C END AS 'Test' FROM T");
    }

    @Test
    public void testIssue266KeywordTop() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT @top");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT @TOP");
    }

    @Test
    public void testIssue584MySQLValueListExpression() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT a, b FROM T WHERE (T.a, T.b) = (c, d)");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT a FROM T WHERE (T.a) = (SELECT b FROM T, c, d)");
    }

    @Test
    public void testIssue588NotNull() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM mytable WHERE col1 ISNULL");
    }

    @Test
    public void testParenthesisAroundFromItem() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM (mytable)");
    }

    @Test
    public void testParenthesisAroundFromItem2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM (mytable myalias)");
    }

    @Test
    public void testParenthesisAroundFromItem3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM (mytable) myalias");
    }

    @Test
    public void testJoinerExpressionIssue596() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM a JOIN (b JOIN c ON b.id = c.id) ON a.id = c.id");
    }

    // @Test public void testJoinerExpressionIssue596_2() throws JSQLParserException {
    // assertSqlCanBeParsedAndDeparsed("SELECT * FROM a JOIN b JOIN c ON b.id = c.id ON a.id = c.id");
    // }
    @Test
    public void testProblemSqlIssue603() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CASE WHEN MAX(CAST(a.jobNum AS INTEGER)) IS NULL THEN '1000' ELSE MAX(CAST(a.jobNum AS INTEGER)) + 1 END FROM user_employee a");
    }

    @Test
    public void testProblemSqlIssue603_2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CAST(col1 AS UNSIGNED INTEGER) FROM mytable");
    }

    @Test
    public void testProblemSqlFuncParamIssue605() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT p.id, pt.name, array_to_string( array( select pc.name from product_category pc ), ',' ) AS categories FROM product p", true);
    }

    @Test
    public void testProblemSqlFuncParamIssue605_2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT func(SELECT col1 FROM mytable)");
    }

    @Test
    public void testSqlContainIsNullFunctionShouldBeParsed() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT name, age, ISNULL(home, 'earn more money') FROM person");
    }

    @Test
    public void testNestedCast() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT acolumn::bit (64)::bigint FROM mytable");
    }

    @Test
    public void testAndOperator() throws JSQLParserException {
        String stmt = "SELECT name from customers where name = 'John' && lastname = 'Doh'";
        Statement parsed = parserManager.parse(new StringReader(stmt));
        TestUtils.assertStatementCanBeDeparsedAs(parsed, "SELECT name FROM customers WHERE name = 'John' AND lastname = 'Doh'");
    }

    @Test
    public void testNamedParametersIssue612() throws Exception {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT a FROM b LIMIT 10 OFFSET :param");
    }

    @Test
    public void testMissingOffsetIssue620() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT a, b FROM test OFFSET 0");
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT a, b FROM test LIMIT 1 OFFSET 0");
    }

    @Test
    public void testMultiPartNames1() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT a.b");
    }

    @Test
    public void testMultiPartNames2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT a.b.*");
    }

    @Test
    public void testMultiPartNames3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT a.*");
    }

    @Test
    public void testMultiPartNames4() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT a.b.c.d.e.f.g.h");
    }

    @Test
    public void testMultiPartNames5() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM a.b.c.d.e.f.g.h");
    }

    @Test
    public void testMultiPartNamesIssue163() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT mymodel.name FROM com.myproject.MyModelClass AS mymodel");
    }

    @Test
    public void testMultiPartNamesIssue608() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT @@session.tx_read_only");
    }

    // Teradata allows SEL to be used in place of SELECT
    // Deparse to the non-contracted form
    @Test
    public void testSelContraction() throws JSQLParserException {
        final String statementSrc = "SEL name, age FROM person";
        final String statementTgt = "SELECT name, age FROM person";
        Select select = ((Select) (parserManager.parse(new StringReader(statementSrc))));
        TestUtils.assertStatementCanBeDeparsedAs(select, statementTgt);
    }

    @Test
    public void testMultiPartNamesIssue643() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT id, bid, pid, devnum, pointdesc, sysid, zone, sort FROM fault ORDER BY id DESC LIMIT ?, ?");
    }

    @Test
    public void testNotNotIssue() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT VALUE1, VALUE2 FROM FOO WHERE NOT BAR LIKE '*%'");
    }

    @Test
    public void testCharNotParsedIssue718() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT a FROM x WHERE a LIKE '%' + char(9) + '%'");
    }

    @Test
    public void testTrueFalseLiteral() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM tbl WHERE true OR clm1 = 3");
    }

    @Test
    public void testTopKeyWord() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT top.date AS mycol1 FROM mytable top WHERE top.myid = :myid AND top.myid2 = 123");
    }

    @Test
    public void testTopKeyWord2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT top.date");
    }

    @Test
    public void testTopKeyWord3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM mytable top");
    }

    @Test
    public void testNotProblem1() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM mytab WHERE NOT v IN (1, 2, 3, 4, 5, 6, 7)");
    }

    @Test
    public void testNotProblem2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM mytab WHERE NOT func(5)");
    }

    @Test
    public void testCaseThenCondition() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM mytable WHERE CASE WHEN a = 'c' THEN a IN (1, 2, 3) END = 1");
    }

    @Test
    public void testCaseThenCondition2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM mytable WHERE CASE WHEN a = 'c' THEN a IN (1, 2, 3) END");
    }

    @Test
    public void testCaseThenCondition3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CASE WHEN a > 0 THEN b + a ELSE 0 END p FROM mytable");
    }

    @Test
    public void testCaseThenCondition4() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM col WHERE CASE WHEN a = 'c' THEN a IN (SELECT id FROM mytable) END");
    }

    @Test
    public void testCaseThenCondition5() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM col WHERE CASE WHEN a = 'c' THEN a IN (SELECT id FROM mytable) ELSE b IN (SELECT id FROM mytable) END");
    }

    @Test
    public void testOptimizeForIssue348() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM EMP ORDER BY SALARY DESC OPTIMIZE FOR 20 ROWS");
    }

    @Test
    public void testFuncConditionParameter() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT if(a < b)");
    }

    @Test
    public void testFuncConditionParameter2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT if(a < b, c)");
    }

    @Test
    public void testFuncConditionParameter3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CAST((MAX(CAST(IIF(isnumeric(license_no) = 1, license_no, 0) AS INT)) + 2) AS varchar) FROM lcps.t_license WHERE profession_id = 60 and license_type = 100 and YEAR(issue_date) % 2 = case when YEAR(issue_date) % 2 = 0 then 0 else 1 end and ISNUMERIC(license_no) = 1", true);
    }

    @Test
    public void testSqlContainIsNullFunctionShouldBeParsed3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT name, age FROM person WHERE NOT ISNULL(home, 'earn more money')");
    }

    @Test
    public void testForXmlPath() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT '|' + person_name FROM person JOIN person_group ON person.person_id = person_group.person_id WHERE person_group.group_id = 1 FOR XML PATH('')");
    }

    // @Test
    // public void testForXmlPath2() throws JSQLParserException {
    // assertSqlCanBeParsedAndDeparsed("SELECT ( STUFF( (SELECT '|' + person_name FROM person JOIN person_group ON person.person_id = person_group.person_id WHERE person_group.group_id = 1 FOR XML PATH(''), TYPE).value('.', 'varchar(max)'),1,1,'')) AS person_name");
    // }
    @Test
    public void testChainedunctions() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT func('').func2('') AS foo FROM some_tables");
    }

    @Test
    public void testCollateExprIssue164() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT u.name COLLATE Latin1_General_CI_AS AS User FROM users u");
    }

    // @Test
    // public void testIntervalExpression() throws JSQLParserException {
    // assertSqlCanBeParsedAndDeparsed("SELECT count(emails.id) FROM emails WHERE (emails.date_entered + 30 DAYS) > CURRENT_DATE");
    // }
    @Test
    public void testNotVariant() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT ! (1 + 1)");
    }

    @Test
    public void testNotVariant2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT ! 1 + 1");
    }

    @Test
    public void testNotVariant3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT NOT (1 + 1)");
    }

    @Test
    public void testDateArithmentic() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CURRENT_DATE + (1 DAY) FROM SYSIBM.SYSDUMMY1");
    }

    @Test
    public void testDateArithmentic2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CURRENT_DATE + 1 DAY AS NEXT_DATE FROM SYSIBM.SYSDUMMY1");
    }

    @Test
    public void testDateArithmentic3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CURRENT_DATE + 1 DAY NEXT_DATE FROM SYSIBM.SYSDUMMY1");
    }

    @Test
    public void testDateArithmentic4() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CURRENT_DATE - 1 DAY + 1 YEAR - 1 MONTH FROM SYSIBM.SYSDUMMY1");
    }

    @Test
    public void testDateArithmentic5() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CASE WHEN CURRENT_DATE BETWEEN (CURRENT_DATE - 1 DAY) AND ('2019-01-01') THEN 1 ELSE 0 END FROM SYSIBM.SYSDUMMY1");
    }

    @Test
    public void testDateArithmentic6() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CURRENT_DATE + HOURS_OFFSET HOUR AS NEXT_DATE FROM SYSIBM.SYSDUMMY1");
    }

    @Test
    public void testDateArithmentic7() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CURRENT_DATE + MINUTE_OFFSET MINUTE AS NEXT_DATE FROM SYSIBM.SYSDUMMY1");
    }

    @Test
    public void testDateArithmentic8() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CURRENT_DATE + SECONDS_OFFSET SECOND AS NEXT_DATE FROM SYSIBM.SYSDUMMY1");
    }

    @Test
    public void testNotProblemIssue721() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT * FROM dual WHERE NOT regexp_like(\'a\', \'[\\w]+\')");
    }

    @Test
    public void testDateArithmentic9() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT CURRENT_DATE + (RAND() * 12 MONTH) AS new_date FROM mytable");
    }

    @Test
    public void testDateArithmentic10() throws JSQLParserException {
        String sql = "select CURRENT_DATE + CASE WHEN CAST(RAND() * 3 AS INTEGER) = 1 THEN 100 ELSE 0 END DAY AS NEW_DATE from mytable";
        TestUtils.assertSqlCanBeParsedAndDeparsed(sql, true);
        Select select = ((Select) (CCJSqlParserUtil.parse(sql)));
    }

    @Test
    public void testDateArithmentic11() throws JSQLParserException {
        String sql = "select CURRENT_DATE + (dayofweek(MY_DUE_DATE) + 5) DAY FROM mytable";
        TestUtils.assertSqlCanBeParsedAndDeparsed(sql, true);
        Select select = ((Select) (CCJSqlParserUtil.parse(sql)));
        final List<SelectItem> list = new ArrayList<>();
        select.getSelectBody().accept(new SelectVisitorAdapter() {
            @Override
            public void visit(PlainSelect plainSelect) {
                list.addAll(plainSelect.getSelectItems());
            }
        });
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(((list.get(0)) instanceof SelectExpressionItem));
        SelectExpressionItem item = ((SelectExpressionItem) (list.get(0)));
        Assert.assertTrue(((item.getExpression()) instanceof Addition));
        Addition add = ((Addition) (item.getExpression()));
        Assert.assertTrue(((add.getRightExpression()) instanceof IntervalExpression));
    }

    @Test
    public void testDateArithmentic12() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("select CASE WHEN CAST(RAND() * 3 AS INTEGER) = 1 THEN NULL ELSE CURRENT_DATE + (month_offset MONTH) END FROM mytable", true);
    }

    @Test
    public void testDateArithmentic13() throws JSQLParserException {
        String sql = "SELECT INTERVAL 5 MONTH MONTH FROM mytable";
        TestUtils.assertSqlCanBeParsedAndDeparsed(sql);
        Select select = ((Select) (CCJSqlParserUtil.parse(sql)));
        final List<SelectItem> list = new ArrayList<>();
        select.getSelectBody().accept(new SelectVisitorAdapter() {
            @Override
            public void visit(PlainSelect plainSelect) {
                list.addAll(plainSelect.getSelectItems());
            }
        });
        Assert.assertEquals(1, list.size());
        Assert.assertTrue(((list.get(0)) instanceof SelectExpressionItem));
        SelectExpressionItem item = ((SelectExpressionItem) (list.get(0)));
        Assert.assertTrue(((item.getExpression()) instanceof IntervalExpression));
        IntervalExpression interval = ((IntervalExpression) (item.getExpression()));
        Assert.assertEquals("INTERVAL 5 MONTH", interval.toString());
        Assert.assertEquals("MONTH", getName());
    }

    @Test
    public void testRawStringExpressionIssue656() throws JSQLParserException {
        for (String c : new String[]{ "u", "e", "n", "r", "b", "rb" }) {
            final String prefix = c;
            String sql = ("select " + c) + "'test' from foo";
            Statement statement = CCJSqlParserUtil.parse(sql);
            Assert.assertNotNull(statement);
            statement.accept(new StatementVisitorAdapter() {
                @Override
                public void visit(Select select) {
                    select.getSelectBody().accept(new SelectVisitorAdapter() {
                        @Override
                        public void visit(PlainSelect plainSelect) {
                            SelectExpressionItem typedExpression = ((SelectExpressionItem) (plainSelect.getSelectItems().get(0)));
                            Assert.assertNotNull(typedExpression);
                            Assert.assertNull(typedExpression.getAlias());
                            StringValue value = ((StringValue) (typedExpression.getExpression()));
                            Assert.assertEquals(prefix.toUpperCase(), value.getPrefix());
                            Assert.assertEquals("test", value.getValue());
                        }
                    });
                }
            });
        }
    }

    @Test
    public void testGroupingSets1() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed(("SELECT COL_1, COL_2, COL_3, COL_4, COL_5, COL_6 FROM TABLE_1 " + ("GROUP BY " + "GROUPING SETS ((COL_1, COL_2, COL_3, COL_4), (COL_5, COL_6))")));
    }

    @Test
    public void testGroupingSets2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT COL_1 FROM TABLE_1 GROUP BY GROUPING SETS (COL_1)");
    }

    @Test
    public void testGroupingSets3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("SELECT COL_1 FROM TABLE_1 GROUP BY GROUPING SETS (COL_1, ())");
    }
}

