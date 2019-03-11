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
package net.sf.jsqlparser.statement.comment;


import java.io.StringReader;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class CommentTest {
    @Test
    public void testCommentTable() throws JSQLParserException {
        String statement = "COMMENT ON TABLE table1 IS 'comment1'";
        Comment comment = ((Comment) (CCJSqlParserUtil.parse(new StringReader(statement))));
        Table table = comment.getTable();
        Assert.assertEquals("table1", table.getName());
        Assert.assertEquals("comment1", comment.getComment().getValue());
        Assert.assertEquals(statement, ("" + comment));
    }

    @Test
    public void testCommentTable2() throws JSQLParserException {
        String statement = "COMMENT ON TABLE schema1.table1 IS 'comment1'";
        Comment comment = ((Comment) (CCJSqlParserUtil.parse(new StringReader(statement))));
        Table table = comment.getTable();
        Assert.assertEquals("schema1", table.getSchemaName());
        Assert.assertEquals("table1", table.getName());
        Assert.assertEquals("comment1", comment.getComment().getValue());
        Assert.assertEquals(statement, ("" + comment));
    }

    @Test
    public void testCommentTableDeparse() throws JSQLParserException {
        String statement = "COMMENT ON TABLE table1 IS 'comment1'";
        TestUtils.assertSqlCanBeParsedAndDeparsed(statement);
    }

    @Test
    public void testCommentColumn() throws JSQLParserException {
        String statement = "COMMENT ON COLUMN table1.column1 IS 'comment1'";
        Comment comment = ((Comment) (CCJSqlParserUtil.parse(new StringReader(statement))));
        Column column = comment.getColumn();
        Assert.assertEquals("table1", column.getTable().getName());
        Assert.assertEquals("column1", column.getColumnName());
        Assert.assertEquals("comment1", comment.getComment().getValue());
        Assert.assertEquals(statement, ("" + comment));
    }

    @Test
    public void testCommentColumnDeparse() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("COMMENT ON COLUMN table1.column1 IS 'comment1'");
    }

    @Test
    public void testToString() {
        Comment comment = new Comment();
        Assert.assertEquals("COMMENT ON IS null", comment.toString());
    }

    @Test
    public void testCommentColumnDeparseIssue696() throws JSQLParserException {
        TestUtils.assertSqlCanBeParsedAndDeparsed("COMMENT ON COLUMN hotels.hotelid IS 'Primary key of the table'");
    }
}

