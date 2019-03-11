package org.sqlite;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import org.junit.Assert;
import org.junit.Test;


public class RSMetaDataTest {
    private Connection conn;

    private Statement stat;

    private ResultSetMetaData meta;

    @Test
    public void catalogName() throws SQLException {
        Assert.assertEquals(meta.getCatalogName(1), "People");
    }

    @Test
    public void columns() throws SQLException {
        Assert.assertEquals(meta.getColumnCount(), 3);
        Assert.assertEquals(meta.getColumnName(1), "pid");
        Assert.assertEquals(meta.getColumnName(2), "firstname");
        Assert.assertEquals(meta.getColumnName(3), "surname");
        Assert.assertEquals(meta.getColumnType(1), Types.INTEGER);
        Assert.assertEquals(meta.getColumnType(2), Types.VARCHAR);
        Assert.assertEquals(meta.getColumnType(3), Types.VARCHAR);
        Assert.assertTrue(meta.isAutoIncrement(1));
        Assert.assertFalse(meta.isAutoIncrement(2));
        Assert.assertFalse(meta.isAutoIncrement(3));
        Assert.assertEquals(meta.isNullable(1), ResultSetMetaData.columnNoNulls);
        Assert.assertEquals(meta.isNullable(2), ResultSetMetaData.columnNullable);
        Assert.assertEquals(meta.isNullable(3), ResultSetMetaData.columnNullable);
    }

    @Test
    public void columnTypes() throws SQLException {
        stat.executeUpdate(("create table tbl (col1 INT, col2 INTEGER, col3 TINYINT, " + ((((((("col4 SMALLINT, col5 MEDIUMINT, col6 BIGINT, col7 UNSIGNED BIG INT, " + "col8 INT2, col9 INT8, col10 CHARACTER(20), col11 VARCHAR(255), ") + "col12 VARYING CHARACTER(255), col13 NCHAR(55), ") + "col14 NATIVE CHARACTER(70), col15 NVARCHAR(100), col16 TEXT, ") + "col17 CLOB, col18 BLOB, col19 REAL, col20 DOUBLE, ") + "col21 DOUBLE PRECISION, col22 FLOAT, col23 NUMERIC, ") + "col24 DECIMAL(10,5), col25 BOOLEAN, col26 DATE, col27 DATETIME, ") + "col28 TIMESTAMP, col29 CHAR(70), col30 TEXT)")));
        // insert empty data into table otherwise getColumnType returns null
        stat.executeUpdate(("insert into tbl values (1, 2, 3, 4, 5, 6, 7, 8, 9," + ("'c', 'varchar', 'varying', 'n', 'n','nvarchar', 'text', 'clob'," + "null, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 0, 12345, 123456, 0, 'char', 'some text')")));
        meta = stat.executeQuery(("select col1, col2, col3, col4, col5, col6, col7, col8, col9, " + ((("col10, col11, col12, col13, col14, col15, col16, col17, col18, " + "col19, col20, col21, col22, col23, col24, col25, col26, col27, ") + "col28, col29, col30, ") + "cast(col1 as boolean) from tbl"))).getMetaData();
        Assert.assertEquals(Types.INTEGER, meta.getColumnType(1));
        Assert.assertEquals(Types.INTEGER, meta.getColumnType(2));
        Assert.assertEquals(Types.TINYINT, meta.getColumnType(3));
        Assert.assertEquals(Types.SMALLINT, meta.getColumnType(4));
        Assert.assertEquals(Types.INTEGER, meta.getColumnType(5));
        Assert.assertEquals(Types.BIGINT, meta.getColumnType(6));
        Assert.assertEquals(Types.BIGINT, meta.getColumnType(7));
        Assert.assertEquals(Types.SMALLINT, meta.getColumnType(8));
        Assert.assertEquals(Types.BIGINT, meta.getColumnType(9));
        Assert.assertEquals(Types.CHAR, meta.getColumnType(10));
        Assert.assertEquals(Types.VARCHAR, meta.getColumnType(11));
        Assert.assertEquals(Types.VARCHAR, meta.getColumnType(12));
        Assert.assertEquals(Types.CHAR, meta.getColumnType(13));
        Assert.assertEquals(Types.CHAR, meta.getColumnType(14));
        Assert.assertEquals(Types.VARCHAR, meta.getColumnType(15));
        Assert.assertEquals(Types.VARCHAR, meta.getColumnType(16));
        Assert.assertEquals(Types.CLOB, meta.getColumnType(17));
        Assert.assertEquals(Types.BLOB, meta.getColumnType(18));
        Assert.assertEquals(Types.REAL, meta.getColumnType(19));
        Assert.assertEquals(Types.DOUBLE, meta.getColumnType(20));
        Assert.assertEquals(Types.DOUBLE, meta.getColumnType(21));
        Assert.assertEquals(Types.FLOAT, meta.getColumnType(22));
        Assert.assertEquals(Types.NUMERIC, meta.getColumnType(23));
        Assert.assertEquals(Types.DECIMAL, meta.getColumnType(24));
        Assert.assertEquals(Types.BOOLEAN, meta.getColumnType(25));
        Assert.assertEquals(Types.DATE, meta.getColumnType(26));
        Assert.assertEquals(Types.DATE, meta.getColumnType(27));
        Assert.assertEquals(Types.TIMESTAMP, meta.getColumnType(28));
        Assert.assertEquals(Types.CHAR, meta.getColumnType(29));
        Assert.assertEquals(Types.VARCHAR, meta.getColumnType(30));
        Assert.assertEquals(Types.BOOLEAN, meta.getColumnType(31));
        Assert.assertEquals(10, meta.getPrecision(24));
        Assert.assertEquals(5, meta.getScale(24));
    }

    @Test
    public void differentRS() throws SQLException {
        meta = stat.executeQuery("select * from people;").getMetaData();
        Assert.assertEquals(meta.getColumnCount(), 4);
        Assert.assertEquals(meta.getColumnName(1), "pid");
        Assert.assertEquals(meta.getColumnName(2), "firstname");
        Assert.assertEquals(meta.getColumnName(3), "surname");
        Assert.assertEquals(meta.getColumnName(4), "dob");
    }

    @Test
    public void nullable() throws SQLException {
        meta = stat.executeQuery("select null;").getMetaData();
        Assert.assertEquals(meta.isNullable(1), ResultSetMetaData.columnNullable);
    }

    @Test(expected = SQLException.class)
    public void badCatalogIndex() throws SQLException {
        meta.getCatalogName(4);
    }

    @Test(expected = SQLException.class)
    public void badColumnIndex() throws SQLException {
        meta.getColumnName(4);
    }

    @Test
    public void scale() throws SQLException {
        Assert.assertEquals(0, meta.getScale(2));
        Assert.assertEquals(5, meta.getScale(3));
    }

    @Test
    public void tableName() throws SQLException {
        final ResultSet rs = stat.executeQuery("SELECT pid, time(dob) as some_time from people");
        Assert.assertEquals("People", rs.getMetaData().getTableName(1));
        Assert.assertEquals("", rs.getMetaData().getTableName(2));
        rs.close();
    }
}

