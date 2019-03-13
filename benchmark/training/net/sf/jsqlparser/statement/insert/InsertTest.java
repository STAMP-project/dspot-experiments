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
package net.sf.jsqlparser.statement.insert;


import java.io.StringReader;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class InsertTest {
    private CCJSqlParserManager parserManager = new CCJSqlParserManager();

    @Test
    public void testRegularInsert() throws JSQLParserException {
        String statement = "INSERT INTO mytable (col1, col2, col3) VALUES (?, 'sadfsd', 234)";
        Insert insert = ((Insert) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals("mytable", getName());
        Assert.assertEquals(3, insert.getColumns().size());
        Assert.assertEquals("col1", getColumnName());
        Assert.assertEquals("col2", getColumnName());
        Assert.assertEquals("col3", getColumnName());
        Assert.assertEquals(3, getExpressions().size());
        Assert.assertTrue(((getExpressions().get(0)) instanceof JdbcParameter));
        Assert.assertEquals("sadfsd", getValue());
        Assert.assertEquals(234, getValue());
        Assert.assertEquals(statement, ("" + insert));
        statement = "INSERT INTO myschema.mytable VALUES (?, ?, 2.3)";
        insert = ((Insert) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals("myschema.mytable", insert.getTable().getFullyQualifiedName());
        Assert.assertEquals(3, getExpressions().size());
        Assert.assertTrue(((getExpressions().get(0)) instanceof JdbcParameter));
        Assert.assertEquals(2.3, getValue(), 0.0);
        Assert.assertEquals(statement, ("" + insert));
    }

    @Test
    public void testInsertWithKeywordValue() throws JSQLParserException {
        String statement = "INSERT INTO mytable (col1) VALUE ('val1')";
        Insert insert = ((Insert) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals("mytable", getName());
        Assert.assertEquals(1, insert.getColumns().size());
        Assert.assertEquals("col1", getColumnName());
        Assert.assertEquals("val1", getValue());
        Assert.assertEquals("INSERT INTO mytable (col1) VALUES ('val1')", insert.toString());
    }

    @Test
    public void testInsertFromSelect() throws JSQLParserException {
        String statement = "INSERT INTO mytable (col1, col2, col3) SELECT * FROM mytable2";
        Insert insert = ((Insert) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals("mytable", getName());
        Assert.assertEquals(3, insert.getColumns().size());
        Assert.assertEquals("col1", getColumnName());
        Assert.assertEquals("col2", getColumnName());
        Assert.assertEquals("col3", getColumnName());
        Assert.assertNull(insert.getItemsList());
        Assert.assertNotNull(insert.getSelect());
        Assert.assertEquals("mytable2", getName());
        // toString uses brakets
        String statementToString = "INSERT INTO mytable (col1, col2, col3) SELECT * FROM mytable2";
        Assert.assertEquals(statementToString, ("" + insert));
    }

    @Test
    public void testInsertFromSet() throws JSQLParserException {
        String statement = "INSERT INTO mytable SET col1 = 12, col2 = name1 * name2";
        Insert insert = ((Insert) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals("mytable", getName());
        Assert.assertEquals(2, insert.getSetColumns().size());
        Assert.assertEquals("col1", getColumnName());
        Assert.assertEquals("col2", getColumnName());
        Assert.assertEquals(2, insert.getSetExpressionList().size());
        Assert.assertEquals("12", insert.getSetExpressionList().get(0).toString());
        Assert.assertEquals("name1 * name2", insert.getSetExpressionList().get(1).toString());
        Assert.assertEquals(statement, ("" + insert));
    }

    @Test
    public void testInsertValuesWithDuplicateElimination() throws JSQLParserException {
        String statement = "INSERT INTO TEST (ID, COUNTER) VALUES (123, 0) " + "ON DUPLICATE KEY UPDATE COUNTER = COUNTER + 1";
        Insert insert = ((Insert) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals("TEST", getName());
        Assert.assertEquals(2, insert.getColumns().size());
        Assert.assertTrue(insert.isUseValues());
        Assert.assertEquals("ID", getColumnName());
        Assert.assertEquals("COUNTER", getColumnName());
        Assert.assertEquals(2, getExpressions().size());
        Assert.assertEquals(123, getValue());
        Assert.assertEquals(0, getValue());
        Assert.assertEquals(1, insert.getDuplicateUpdateColumns().size());
        Assert.assertEquals("COUNTER", getColumnName());
        Assert.assertEquals(1, insert.getDuplicateUpdateExpressionList().size());
        Assert.assertEquals("COUNTER + 1", insert.getDuplicateUpdateExpressionList().get(0).toString());
        Assert.assertFalse(insert.isUseSelectBrackets());
        Assert.assertTrue(insert.isUseDuplicate());
        Assert.assertEquals(statement, ("" + insert));
    }

    @Test
    public void testInsertFromSetWithDuplicateElimination() throws JSQLParserException {
        String statement = "INSERT INTO mytable SET col1 = 122 " + "ON DUPLICATE KEY UPDATE col2 = col2 + 1, col3 = 'saint'";
        Insert insert = ((Insert) (parserManager.parse(new StringReader(statement))));
        Assert.assertEquals("mytable", getName());
        Assert.assertEquals(1, insert.getSetColumns().size());
        Assert.assertEquals("col1", getColumnName());
        Assert.assertEquals(1, insert.getSetExpressionList().size());
        Assert.assertEquals("122", insert.getSetExpressionList().get(0).toString());
        Assert.assertEquals(2, insert.getDuplicateUpdateColumns().size());
        Assert.assertEquals("col2", getColumnName());
        Assert.assertEquals("col3", getColumnName());
        Assert.assertEquals(2, insert.getDuplicateUpdateExpressionList().size());
        Assert.assertEquals("col2 + 1", insert.getDuplicateUpdateExpressionList().get(0).toString());
        Assert.assertEquals("'saint'", insert.getDuplicateUpdateExpressionList().get(1).toString());
        Assert.assertEquals(statement, ("" + insert));
    }

    @Test
    public void testInsertMultiRowValue() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT INTO mytable (col1, col2) VALUES (a, b), (d, e)");
    }

    @Test
    public void testInsertMultiRowValueDifferent() throws JSQLParserException {
        try {
            TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT INTO mytable (col1, col2) VALUES (a, b), (d, e, c)");
        } catch (Exception e) {
            return;
        }
        Assert.fail("should not work");
    }

    @Test
    public void testSimpleInsert() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT INTO example (num, name, address, tel) VALUES (1, 'name', 'test ', '1234-1234')");
    }

    @Test
    public void testInsertWithReturning() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT INTO mytable (mycolumn) VALUES ('1') RETURNING id");
    }

    @Test
    public void testInsertWithReturning2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT INTO mytable (mycolumn) VALUES ('1') RETURNING *");
    }

    @Test
    public void testInsertWithReturning3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT INTO mytable (mycolumn) VALUES ('1') RETURNING id AS a1, id2 AS a2");
    }

    @Test
    public void testInsertSelect() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT INTO mytable (mycolumn) SELECT mycolumn FROM mytable");
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT INTO mytable (mycolumn) (SELECT mycolumn FROM mytable)");
    }

    @Test
    public void testInsertWithSelect() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT INTO mytable (mycolumn) WITH a AS (SELECT mycolumn FROM mytable) SELECT mycolumn FROM a");
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT INTO mytable (mycolumn) (WITH a AS (SELECT mycolumn FROM mytable) SELECT mycolumn FROM a)");
    }

    @Test
    public void testInsertWithKeywords() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT INTO kvPair (value, key) VALUES (?, ?)");
    }

    @Test
    public void testHexValues() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT INTO TABLE2 VALUES (\'1\', \"DSDD\", x\'EFBFBDC7AB\')");
    }

    @Test
    public void testHexValues2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT INTO TABLE2 VALUES (\'1\', \"DSDD\", 0xEFBFBDC7AB)");
    }

    @Test
    public void testHexValues3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT INTO TABLE2 VALUES (\'1\', \"DSDD\", 0xabcde)");
    }

    @Test
    public void testDuplicateKey() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT INTO Users0 (UserId, Key, Value) VALUES (51311, 'T_211', 18) ON DUPLICATE KEY UPDATE Value = 18");
    }

    @Test
    public void testModifierIgnore() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT IGNORE INTO `AoQiSurvey_FlashVersion_Single` VALUES (302215163, 'WIN 16,0,0,235')");
    }

    @Test
    public void testModifierPriority1() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT DELAYED INTO kvPair (value, key) VALUES (?, ?)");
    }

    @Test
    public void testModifierPriority2() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT LOW_PRIORITY INTO kvPair (value, key) VALUES (?, ?)");
    }

    @Test
    public void testModifierPriority3() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT HIGH_PRIORITY INTO kvPair (value, key) VALUES (?, ?)");
    }

    @Test
    public void testIssue223() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT INTO user VALUES (2001, \'\\\'Clark\\\'\', \'Kent\')");
    }

    @Test
    public void testKeywordPrecisionIssue363() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT INTO test (user_id, precision) VALUES (1, '111')");
    }

    @Test
    public void testWithDeparsingIssue406() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("insert into mytab3 (a,b,c) select a,b,c from mytab where exists(with t as (select * from mytab2) select * from t)", true);
    }

    @Test
    public void testInsertSetInDeparsing() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT INTO mytable SET col1 = 12, col2 = name1 * name2");
    }

    @Test
    public void testInsertValuesWithDuplicateEliminationInDeparsing() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed(("INSERT INTO TEST (ID, COUNTER) VALUES (123, 0) " + "ON DUPLICATE KEY UPDATE COUNTER = COUNTER + 1"));
    }

    @Test
    public void testInsertSetWithDuplicateEliminationInDeparsing() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed(("INSERT INTO mytable SET col1 = 122 " + "ON DUPLICATE KEY UPDATE col2 = col2 + 1, col3 = 'saint'"));
    }

    @Test
    public void testInsertTableWithAliasIssue526() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT INTO account t (name, addr, phone) SELECT * FROM user");
    }

    @Test
    public void testInsertKeyWordEnableIssue592() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT INTO T_USER (ID, EMAIL_VALIDATE, ENABLE, PASSWORD) VALUES (?, ?, ?, ?)");
    }

    @Test
    public void testInsertKeyWordIntervalIssue682() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT INTO BILLING_TASKS (TIMEOUT, INTERVAL, RETRY_UPON_FAILURE, END_DATE, MAX_RETRY_COUNT, CONTINUOUS, NAME, LAST_RUN, START_TIME, NEXT_RUN, ID, UNIQUE_NAME, INTERVAL_TYPE) VALUES (?, ?, ?, ?, ?, ?, ?, NULL, ?, ?, ?, ?, ?)");
    }

    @Test
    public void testNextVal() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("INSERT INTO tracker (monitor_id, user_id, module_name, item_id, item_summary, team_id, date_modified, action, visible, id) VALUES (?, ?, ?, ?, ?, ?, to_date(?, 'YYYY-MM-DD HH24:MI:SS'), ?, ?, NEXTVAL FOR TRACKER_ID_SEQ)");
    }
}

