package org.sqlite;


import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


/**
 * These tests are designed to stress Statements on memory databases.
 */
public class DBMetaDataTest {
    private Connection conn;

    private Statement stat;

    private DatabaseMetaData meta;

    @Test
    public void getTables() throws SQLException {
        ResultSet rs = meta.getTables(null, null, null, null);
        Assert.assertNotNull(rs);
        stat.getGeneratedKeys().close();
        stat.close();
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "test");// 3

        Assert.assertEquals(rs.getString("TABLE_TYPE"), "TABLE");// 4

        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "testView");
        Assert.assertEquals(rs.getString("TABLE_TYPE"), "VIEW");
        rs.close();
        rs = meta.getTables(null, null, "bob", null);
        Assert.assertFalse(rs.next());
        rs.close();
        rs = meta.getTables(null, null, "test", null);
        Assert.assertTrue(rs.next());
        Assert.assertFalse(rs.next());
        rs.close();
        rs = meta.getTables(null, null, "test%", null);
        Assert.assertTrue(rs.next());
        Assert.assertTrue(rs.next());
        rs.close();
        rs = meta.getTables(null, null, null, new String[]{ "table" });
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "test");
        Assert.assertFalse(rs.next());
        rs.close();
        rs = meta.getTables(null, null, null, new String[]{ "view" });
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "testView");
        Assert.assertFalse(rs.next());
        rs.close();
    }

    @Test
    public void getTableTypes() throws SQLException {
        ResultSet rs = meta.getTableTypes();
        Assert.assertNotNull(rs);
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_TYPE"), "GLOBAL TEMPORARY");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_TYPE"), "SYSTEM TABLE");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_TYPE"), "TABLE");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_TYPE"), "VIEW");
        Assert.assertFalse(rs.next());
    }

    @Test
    public void getTypeInfo() throws SQLException {
        ResultSet rs = meta.getTypeInfo();
        Assert.assertNotNull(rs);
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TYPE_NAME"), "BLOB");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TYPE_NAME"), "INTEGER");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TYPE_NAME"), "NULL");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TYPE_NAME"), "REAL");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TYPE_NAME"), "TEXT");
        Assert.assertFalse(rs.next());
    }

    @Test
    public void getColumns() throws SQLException {
        ResultSet rs = meta.getColumns(null, null, "test", "id");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "test");
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "id");
        Assert.assertEquals(rs.getString("IS_NULLABLE"), "YES");
        Assert.assertEquals(rs.getString("COLUMN_DEF"), null);
        Assert.assertEquals(rs.getInt("DATA_TYPE"), Types.INTEGER);
        Assert.assertEquals(rs.getString("IS_AUTOINCREMENT"), "NO");
        Assert.assertFalse(rs.next());
        rs = meta.getColumns(null, null, "test", "fn");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "fn");
        Assert.assertEquals(rs.getInt("DATA_TYPE"), Types.FLOAT);
        Assert.assertEquals(rs.getString("IS_NULLABLE"), "YES");
        Assert.assertEquals(rs.getString("COLUMN_DEF"), "0.0");
        Assert.assertEquals(rs.getString("IS_AUTOINCREMENT"), "NO");
        Assert.assertFalse(rs.next());
        rs = meta.getColumns(null, null, "test", "sn");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "sn");
        Assert.assertEquals(rs.getString("IS_NULLABLE"), "NO");
        Assert.assertEquals(rs.getString("COLUMN_DEF"), null);
        Assert.assertFalse(rs.next());
        rs = meta.getColumns(null, null, "test", "%");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "id");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "fn");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "sn");
        Assert.assertFalse(rs.next());
        rs = meta.getColumns(null, null, "test", "%n");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "fn");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "sn");
        Assert.assertFalse(rs.next());
        rs = meta.getColumns(null, null, "test%", "%");
        // TABLE "test"
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "test");
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "id");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "test");
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "fn");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "test");
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "sn");
        // VIEW "testView"
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "testView");
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "id");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "testView");
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "fn");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "testView");
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "sn");
        Assert.assertFalse(rs.next());
        rs = meta.getColumns(null, null, "%", "%");
        // TABLE "test"
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "test");
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "id");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "fn");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "sn");
        // VIEW "testView"
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "testView");
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "id");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "fn");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "sn");
        Assert.assertFalse(rs.next());
        rs = meta.getColumns(null, null, "doesnotexist", "%");
        Assert.assertFalse(rs.next());
        Assert.assertEquals(24, rs.getMetaData().getColumnCount());
    }

    @Test
    public void numberOfgetImportedKeysCols() throws SQLException {
        stat.executeUpdate("create table parent (id1 integer, id2 integer, primary key(id1, id2))");
        stat.executeUpdate("create table child1 (id1 integer, id2 integer, foreign key(id1) references parent(id1), foreign key(id2) references parent(id2))");
        stat.executeUpdate("create table child2 (id1 integer, id2 integer, foreign key(id2, id1) references parent(id2, id1))");
        ResultSet importedKeys = meta.getImportedKeys(null, null, "child1");
        // child1: 1st fk (simple)
        Assert.assertTrue(importedKeys.next());
        Assert.assertEquals("parent", importedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("id2", importedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertNotNull(importedKeys.getString("PK_NAME"));
        Assert.assertNotNull(importedKeys.getString("FK_NAME"));
        Assert.assertEquals("child1", importedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("id2", importedKeys.getString("FKCOLUMN_NAME"));
        // child1: 2nd fk (simple)
        Assert.assertTrue(importedKeys.next());
        Assert.assertEquals("parent", importedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("id1", importedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertNotNull(importedKeys.getString("PK_NAME"));
        Assert.assertNotNull(importedKeys.getString("FK_NAME"));
        Assert.assertEquals("child1", importedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("id1", importedKeys.getString("FKCOLUMN_NAME"));
        Assert.assertFalse(importedKeys.next());
        importedKeys = meta.getImportedKeys(null, null, "child2");
        // child2: 1st fk (composite)
        Assert.assertTrue(importedKeys.next());
        Assert.assertEquals("parent", importedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("id2", importedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertNotNull(importedKeys.getString("PK_NAME"));
        Assert.assertNotNull(importedKeys.getString("FK_NAME"));
        Assert.assertEquals("child2", importedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("id2", importedKeys.getString("FKCOLUMN_NAME"));
        Assert.assertTrue(importedKeys.next());
        Assert.assertEquals("parent", importedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("id1", importedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertNotNull(importedKeys.getString("PK_NAME"));
        Assert.assertNotNull(importedKeys.getString("FK_NAME"));
        Assert.assertEquals("child2", importedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("id1", importedKeys.getString("FKCOLUMN_NAME"));
        Assert.assertFalse(importedKeys.next());
        importedKeys.close();
    }

    @Test
    public void numberOfgetExportedKeysCols() throws SQLException {
        stat.executeUpdate("create table parent (id1 integer, id2 integer, primary key(id1, id2))");
        stat.executeUpdate("create table child1 (id1 integer, id2 integer,\r\n foreign\tkey(id1) references parent(id1), foreign key(id2) references parent(id2))");
        stat.executeUpdate("create table child2 (id1 integer, id2 integer, foreign key(id2, id1) references parent(id2, id1))");
        ResultSet exportedKeys = meta.getExportedKeys(null, null, "parent");
        // 1st fk (simple) - child1
        Assert.assertTrue(exportedKeys.next());
        Assert.assertEquals("parent", exportedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("id2", exportedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertNotNull(exportedKeys.getString("PK_NAME"));
        Assert.assertNotNull(exportedKeys.getString("FK_NAME"));
        Assert.assertEquals("child1", exportedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("id2", exportedKeys.getString("FKCOLUMN_NAME"));
        // 2nd fk (simple) - child1
        Assert.assertTrue(exportedKeys.next());
        Assert.assertEquals("parent", exportedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("id1", exportedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertNotNull(exportedKeys.getString("PK_NAME"));
        Assert.assertNotNull(exportedKeys.getString("FK_NAME"));
        Assert.assertEquals("child1", exportedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("id1", exportedKeys.getString("FKCOLUMN_NAME"));
        // 3rd fk (composite) - child2
        Assert.assertTrue(exportedKeys.next());
        Assert.assertEquals("parent", exportedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("id2", exportedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertNotNull(exportedKeys.getString("PK_NAME"));
        Assert.assertNotNull(exportedKeys.getString("FK_NAME"));
        Assert.assertEquals("child2", exportedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("id2", exportedKeys.getString("FKCOLUMN_NAME"));
        Assert.assertTrue(exportedKeys.next());
        Assert.assertEquals("parent", exportedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("id1", exportedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertNotNull(exportedKeys.getString("PK_NAME"));
        Assert.assertNotNull(exportedKeys.getString("FK_NAME"));
        Assert.assertEquals("child2", exportedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("id1", exportedKeys.getString("FKCOLUMN_NAME"));
        Assert.assertFalse(exportedKeys.next());
        exportedKeys.close();
    }

    @Test
    public void getExportedKeysColsForNamedKeys() throws SQLException {
        ResultSet exportedKeys;
        // 1. Check for named primary keys
        // SQL is deliberately in uppercase, to make sure case-sensitivity is maintained
        stat.executeUpdate("CREATE TABLE PARENT1 (ID1 INTEGER, DATA1 INTEGER, CONSTRAINT PK_PARENT PRIMARY KEY (ID1))");
        stat.executeUpdate("CREATE TABLE CHILD1 (ID1 INTEGER, DATA2 INTEGER, FOREIGN KEY(ID1) REFERENCES PARENT1(ID1))");
        exportedKeys = meta.getExportedKeys(null, null, "PARENT1");
        Assert.assertTrue(exportedKeys.next());
        Assert.assertEquals("PARENT1", exportedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("ID1", exportedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertEquals("PK_PARENT", exportedKeys.getString("PK_NAME"));
        Assert.assertEquals("", exportedKeys.getString("FK_NAME"));
        Assert.assertEquals("CHILD1", exportedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("ID1", exportedKeys.getString("FKCOLUMN_NAME"));
        Assert.assertFalse(exportedKeys.next());
        exportedKeys.close();
        // 2. Check for named foreign keys
        // SQL is deliberately in mixed case, to make sure case-sensitivity is maintained
        stat.executeUpdate("CREATE TABLE Parent2 (Id1 INTEGER, DATA1 INTEGER, PRIMARY KEY (Id1))");
        stat.executeUpdate("CREATE TABLE Child2 (Id1 INTEGER, DATA2 INTEGER, CONSTRAINT FK_Child2 FOREIGN KEY(Id1) REFERENCES Parent2(Id1))");
        exportedKeys = meta.getExportedKeys(null, null, "Parent2");
        Assert.assertTrue(exportedKeys.next());
        Assert.assertEquals("Parent2", exportedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("Id1", exportedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertEquals("", exportedKeys.getString("PK_NAME"));
        Assert.assertEquals("FK_Child2", exportedKeys.getString("FK_NAME"));
        Assert.assertEquals("Child2", exportedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("Id1", exportedKeys.getString("FKCOLUMN_NAME"));
        Assert.assertFalse(exportedKeys.next());
        exportedKeys.close();
    }

    @Test
    public void getImportedKeysColsForNamedKeys() throws SQLException {
        ResultSet importedKeys;
        // 1. Check for named primary keys
        // SQL is deliberately in uppercase, to make sure case-sensitivity is maintained
        stat.executeUpdate("CREATE TABLE PARENT1 (ID1 INTEGER, DATA1 INTEGER, CONSTRAINT PK_PARENT PRIMARY KEY (ID1))");
        stat.executeUpdate("CREATE TABLE CHILD1 (ID1 INTEGER, DATA2 INTEGER, FOREIGN KEY(ID1) REFERENCES PARENT1(ID1))");
        importedKeys = meta.getImportedKeys(null, null, "CHILD1");
        Assert.assertTrue(importedKeys.next());
        Assert.assertEquals("PARENT1", importedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("ID1", importedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertEquals("PK_PARENT", importedKeys.getString("PK_NAME"));
        Assert.assertEquals("", importedKeys.getString("FK_NAME"));
        Assert.assertEquals("CHILD1", importedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("ID1", importedKeys.getString("FKCOLUMN_NAME"));
        Assert.assertFalse(importedKeys.next());
        importedKeys.close();
        // 2. Check for named foreign keys
        // SQL is deliberately in mixed case, to make sure case-sensitivity is maintained
        stat.executeUpdate("CREATE TABLE Parent2 (Id1 INTEGER, DATA1 INTEGER, PRIMARY KEY (Id1))");
        stat.executeUpdate(("CREATE TABLE Child2 (Id1 INTEGER, DATA2 INTEGER, " + "CONSTRAINT FK_Child2 FOREIGN KEY(Id1) REFERENCES Parent2(Id1))"));
        importedKeys = meta.getImportedKeys(null, null, "Child2");
        Assert.assertTrue(importedKeys.next());
        Assert.assertEquals("Parent2", importedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("Id1", importedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertEquals("", importedKeys.getString("PK_NAME"));
        Assert.assertEquals("FK_Child2", importedKeys.getString("FK_NAME"));
        Assert.assertEquals("Child2", importedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("Id1", importedKeys.getString("FKCOLUMN_NAME"));
        Assert.assertFalse(importedKeys.next());
        importedKeys.close();
    }

    @Test
    public void getImportedKeysColsForMixedCaseDefinition() throws SQLException {
        ResultSet importedKeys;
        // SQL is deliberately in mixed-case, to make sure case-sensitivity is maintained
        stat.executeUpdate("CREATE TABLE PARENT1 (ID1 INTEGER, DATA1 INTEGER, CONSTRAINT PK_PARENT PRIMARY KEY (ID1))");
        stat.executeUpdate(("CREATE TABLE CHILD1 (ID1 INTEGER, DATA2 INTEGER, " + "CONSTRAINT FK_Parent1 FOREIGN KEY(ID1) REFERENCES Parent1(Id1))"));
        importedKeys = meta.getImportedKeys(null, null, "CHILD1");
        Assert.assertTrue(importedKeys.next());
        Assert.assertEquals("Parent1", importedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("Id1", importedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertEquals("PK_PARENT", importedKeys.getString("PK_NAME"));
        Assert.assertEquals("FK_Parent1", importedKeys.getString("FK_NAME"));
        Assert.assertEquals("CHILD1", importedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("ID1", importedKeys.getString("FKCOLUMN_NAME"));
        Assert.assertFalse(importedKeys.next());
        importedKeys.close();
    }

    @Test
    public void getImportedKeysColsForMultipleImports() throws SQLException {
        ResultSet importedKeys;
        stat.executeUpdate("CREATE TABLE PARENT1 (ID1 INTEGER, DATA1 INTEGER, CONSTRAINT PK_PARENT1 PRIMARY KEY (ID1))");
        stat.executeUpdate("CREATE TABLE PARENT2 (ID2 INTEGER, DATA2 INTEGER, CONSTRAINT PK_PARENT2 PRIMARY KEY (ID2))");
        stat.executeUpdate(("CREATE TABLE CHILD1 (ID1 INTEGER, ID2 INTEGER, " + ("CONSTRAINT FK_PARENT1 FOREIGN KEY(ID1) REFERENCES PARENT1(ID1), " + "CONSTRAINT FK_PARENT2 FOREIGN KEY(ID2) REFERENCES PARENT2(ID2))")));
        importedKeys = meta.getImportedKeys(null, null, "CHILD1");
        Assert.assertTrue(importedKeys.next());
        Assert.assertEquals("PARENT1", importedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("ID1", importedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertEquals("PK_PARENT1", importedKeys.getString("PK_NAME"));
        Assert.assertEquals("FK_PARENT1", importedKeys.getString("FK_NAME"));
        Assert.assertEquals("CHILD1", importedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("ID1", importedKeys.getString("FKCOLUMN_NAME"));
        Assert.assertTrue(importedKeys.next());
        Assert.assertEquals("PARENT2", importedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("ID2", importedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertEquals("PK_PARENT2", importedKeys.getString("PK_NAME"));
        Assert.assertEquals("FK_PARENT2", importedKeys.getString("FK_NAME"));
        Assert.assertEquals("CHILD1", importedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("ID2", importedKeys.getString("FKCOLUMN_NAME"));
        Assert.assertFalse(importedKeys.next());
        importedKeys.close();
        // Unnamed foreign keys and unnamed primary keys
        stat.executeUpdate("CREATE TABLE PARENT3 (ID3 INTEGER, DATA3 INTEGER, PRIMARY KEY (ID3))");
        stat.executeUpdate("CREATE TABLE PARENT4 (ID4 INTEGER, DATA4 INTEGER, CONSTRAINT PK_PARENT4 PRIMARY KEY (ID4))");
        stat.executeUpdate(("CREATE TABLE CHILD2 (ID3 INTEGER, ID4 INTEGER, " + ("FOREIGN KEY(ID3) REFERENCES PARENT3(ID3), " + "CONSTRAINT FK_PARENT4 FOREIGN KEY(ID4) REFERENCES PARENT4(ID4))")));
        importedKeys = meta.getImportedKeys(null, null, "CHILD2");
        Assert.assertTrue(importedKeys.next());
        Assert.assertEquals("PARENT3", importedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("ID3", importedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertEquals("", importedKeys.getString("PK_NAME"));
        Assert.assertEquals("", importedKeys.getString("FK_NAME"));
        Assert.assertEquals("CHILD2", importedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("ID3", importedKeys.getString("FKCOLUMN_NAME"));
        Assert.assertTrue(importedKeys.next());
        Assert.assertEquals("PARENT4", importedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("ID4", importedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertEquals("PK_PARENT4", importedKeys.getString("PK_NAME"));
        Assert.assertEquals("FK_PARENT4", importedKeys.getString("FK_NAME"));
        Assert.assertEquals("CHILD2", importedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("ID4", importedKeys.getString("FKCOLUMN_NAME"));
        Assert.assertFalse(importedKeys.next());
        importedKeys.close();
    }

    @Test
    public void getImportedKeysCols2() throws SQLException {
        stat.executeUpdate(("CREATE TABLE Authors (Id INTEGER NOT NULL, Name VARCHAR(20) NOT NULL, " + ("CONSTRAINT PK_Authors PRIMARY KEY (Id)," + "  CONSTRAINT CHECK_UPPERCASE_Name CHECK (Name=UPPER(Name)))")));
        stat.executeUpdate(("CREATE TABLE Books (Id INTEGER NOT NULL, Title VARCHAR(255) NOT NULL, PreviousEditionId INTEGER," + ("CONSTRAINT PK_Books PRIMARY KEY (Id), " + "CONSTRAINT FK_PreviousEdition FOREIGN KEY(PreviousEditionId) REFERENCES Books (Id))")));
        stat.executeUpdate(("CREATE TABLE BookAuthors (BookId INTEGER NOT NULL, AuthorId INTEGER NOT NULL, " + ("CONSTRAINT FK_Y_Book FOREIGN KEY (BookId) REFERENCES Books (Id), " + "CONSTRAINT FK_Z_Author FOREIGN KEY (AuthorId) REFERENCES Authors (Id)) ")));
        ResultSet importedKeys;
        importedKeys = meta.getImportedKeys(null, null, "BookAuthors");
        Assert.assertTrue(importedKeys.next());
        Assert.assertEquals("Authors", importedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("Id", importedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertEquals("PK_Authors", importedKeys.getString("PK_NAME"));
        Assert.assertEquals("FK_Z_Author", importedKeys.getString("FK_NAME"));
        Assert.assertEquals("BookAuthors", importedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("AuthorId", importedKeys.getString("FKCOLUMN_NAME"));
        Assert.assertTrue(importedKeys.next());
        Assert.assertEquals("Books", importedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("Id", importedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertEquals("PK_Books", importedKeys.getString("PK_NAME"));
        Assert.assertEquals("FK_Y_Book", importedKeys.getString("FK_NAME"));
        Assert.assertEquals("BookAuthors", importedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("BookId", importedKeys.getString("FKCOLUMN_NAME"));
        Assert.assertFalse(importedKeys.next());
        importedKeys.close();
        ResultSet exportedKeys;
        exportedKeys = meta.getExportedKeys(null, null, "Authors");
        Assert.assertTrue(exportedKeys.next());
        Assert.assertEquals("Authors", exportedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("Id", exportedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertEquals("PK_Authors", exportedKeys.getString("PK_NAME"));
        Assert.assertEquals("FK_Z_Author", exportedKeys.getString("FK_NAME"));
        Assert.assertEquals("BookAuthors", exportedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("AuthorId", exportedKeys.getString("FKCOLUMN_NAME"));
        Assert.assertFalse(exportedKeys.next());
        exportedKeys.close();
        exportedKeys = meta.getExportedKeys(null, null, "Books");
        Assert.assertTrue(exportedKeys.next());
        Assert.assertEquals("Books", exportedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("Id", exportedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertEquals("PK_Books", exportedKeys.getString("PK_NAME"));
        Assert.assertEquals("FK_Y_Book", exportedKeys.getString("FK_NAME"));
        Assert.assertEquals("BookAuthors", exportedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("BookId", exportedKeys.getString("FKCOLUMN_NAME"));
        Assert.assertTrue(exportedKeys.next());
        Assert.assertEquals("Books", exportedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("Id", exportedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertEquals("PK_Books", exportedKeys.getString("PK_NAME"));
        Assert.assertEquals("FK_PreviousEdition", exportedKeys.getString("FK_NAME"));// ???

        Assert.assertEquals("Books", exportedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("PreviousEditionId", exportedKeys.getString("FKCOLUMN_NAME"));
        Assert.assertFalse(exportedKeys.next());
        exportedKeys.close();
    }

    @Test
    public void getExportedKeysColsForMultipleImports() throws SQLException {
        ResultSet exportedKeys;
        stat.executeUpdate("CREATE TABLE PARENT1 (ID1 INTEGER, ID2 INTEGER, CONSTRAINT PK_PARENT1 PRIMARY KEY (ID1))");
        stat.executeUpdate("CREATE TABLE CHILD1 (ID1 INTEGER, CONSTRAINT FK_PARENT1 FOREIGN KEY(ID1) REFERENCES PARENT1(ID1))");
        stat.executeUpdate("CREATE TABLE CHILD2 (ID2 INTEGER, CONSTRAINT FK_PARENT2 FOREIGN KEY(ID2) REFERENCES PARENT1(ID2))");
        exportedKeys = meta.getExportedKeys(null, null, "PARENT1");
        Assert.assertTrue(exportedKeys.next());
        Assert.assertEquals("PARENT1", exportedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("ID1", exportedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertEquals("PK_PARENT1", exportedKeys.getString("PK_NAME"));
        Assert.assertEquals("FK_PARENT1", exportedKeys.getString("FK_NAME"));
        Assert.assertEquals("CHILD1", exportedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("ID1", exportedKeys.getString("FKCOLUMN_NAME"));
        Assert.assertTrue(exportedKeys.next());
        Assert.assertEquals("PARENT1", exportedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("ID2", exportedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertEquals("", exportedKeys.getString("PK_NAME"));
        Assert.assertEquals("FK_PARENT2", exportedKeys.getString("FK_NAME"));
        Assert.assertEquals("CHILD2", exportedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("ID2", exportedKeys.getString("FKCOLUMN_NAME"));
        Assert.assertFalse(exportedKeys.next());
        exportedKeys.close();
    }

    @Test
    public void columnOrderOfgetTables() throws SQLException {
        stat.executeUpdate("CREATE TABLE TABLE1 (ID1 INTEGER PRIMARY KEY AUTOINCREMENT, ID2 INTEGER)");
        stat.executeUpdate("CREATE TABLE TABLE2 (ID2 INTEGER, DATA2 VARCHAR(20))");
        stat.executeUpdate("CREATE TEMP TABLE TABLE3 (ID3 INTEGER, DATA3 VARCHAR(20))");
        stat.executeUpdate("CREATE VIEW VIEW1 (V1, V2) AS SELECT ID1, ID2 FROM TABLE1");
        ResultSet rsTables = meta.getTables(null, null, null, new String[]{ "TABLE", "VIEW", "GLOBAL TEMPORARY", "SYSTEM TABLE" });
        Assert.assertTrue(rsTables.next());
        // Check order of columns
        ResultSetMetaData rsmeta = rsTables.getMetaData();
        Assert.assertEquals(rsmeta.getColumnCount(), 10);
        Assert.assertEquals(rsmeta.getColumnName(1), "TABLE_CAT");
        Assert.assertEquals(rsmeta.getColumnName(2), "TABLE_SCHEM");
        Assert.assertEquals(rsmeta.getColumnName(3), "TABLE_NAME");
        Assert.assertEquals(rsmeta.getColumnName(4), "TABLE_TYPE");
        Assert.assertEquals(rsmeta.getColumnName(5), "REMARKS");
        Assert.assertEquals(rsmeta.getColumnName(6), "TYPE_CAT");
        Assert.assertEquals(rsmeta.getColumnName(7), "TYPE_SCHEM");
        Assert.assertEquals(rsmeta.getColumnName(8), "TYPE_NAME");
        Assert.assertEquals(rsmeta.getColumnName(9), "SELF_REFERENCING_COL_NAME");
        Assert.assertEquals(rsmeta.getColumnName(10), "REF_GENERATION");
        Assert.assertEquals("TABLE3", rsTables.getString("TABLE_NAME"));
        Assert.assertEquals("GLOBAL TEMPORARY", rsTables.getString("TABLE_TYPE"));
        Assert.assertTrue(rsTables.next());
        Assert.assertEquals("sqlite_sequence", rsTables.getString("TABLE_NAME"));
        Assert.assertEquals("SYSTEM TABLE", rsTables.getString("TABLE_TYPE"));
        Assert.assertTrue(rsTables.next());
        Assert.assertEquals("TABLE1", rsTables.getString("TABLE_NAME"));
        Assert.assertEquals("TABLE", rsTables.getString("TABLE_TYPE"));
        Assert.assertTrue(rsTables.next());
        Assert.assertEquals("TABLE2", rsTables.getString("TABLE_NAME"));
        Assert.assertEquals("TABLE", rsTables.getString("TABLE_TYPE"));
        Assert.assertTrue(rsTables.next());
        Assert.assertTrue(rsTables.next());
        Assert.assertEquals("VIEW1", rsTables.getString("TABLE_NAME"));
        Assert.assertEquals("VIEW", rsTables.getString("TABLE_TYPE"));
        rsTables.close();
    }

    @Test
    public void columnOrderOfgetTableTypes() throws SQLException {
        ResultSet rs = meta.getTableTypes();
        Assert.assertTrue(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        Assert.assertEquals(rsmeta.getColumnCount(), 1);
        Assert.assertEquals(rsmeta.getColumnName(1), "TABLE_TYPE");
    }

    @Test
    public void columnOrderOfgetTypeInfo() throws SQLException {
        ResultSet rs = meta.getTypeInfo();
        Assert.assertTrue(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        Assert.assertEquals(rsmeta.getColumnCount(), 18);
        Assert.assertEquals(rsmeta.getColumnName(1), "TYPE_NAME");
        Assert.assertEquals(rsmeta.getColumnName(2), "DATA_TYPE");
        Assert.assertEquals(rsmeta.getColumnName(3), "PRECISION");
        Assert.assertEquals(rsmeta.getColumnName(4), "LITERAL_PREFIX");
        Assert.assertEquals(rsmeta.getColumnName(5), "LITERAL_SUFFIX");
        Assert.assertEquals(rsmeta.getColumnName(6), "CREATE_PARAMS");
        Assert.assertEquals(rsmeta.getColumnName(7), "NULLABLE");
        Assert.assertEquals(rsmeta.getColumnName(8), "CASE_SENSITIVE");
        Assert.assertEquals(rsmeta.getColumnName(9), "SEARCHABLE");
        Assert.assertEquals(rsmeta.getColumnName(10), "UNSIGNED_ATTRIBUTE");
        Assert.assertEquals(rsmeta.getColumnName(11), "FIXED_PREC_SCALE");
        Assert.assertEquals(rsmeta.getColumnName(12), "AUTO_INCREMENT");
        Assert.assertEquals(rsmeta.getColumnName(13), "LOCAL_TYPE_NAME");
        Assert.assertEquals(rsmeta.getColumnName(14), "MINIMUM_SCALE");
        Assert.assertEquals(rsmeta.getColumnName(15), "MAXIMUM_SCALE");
        Assert.assertEquals(rsmeta.getColumnName(16), "SQL_DATA_TYPE");
        Assert.assertEquals(rsmeta.getColumnName(17), "SQL_DATETIME_SUB");
        Assert.assertEquals(rsmeta.getColumnName(18), "NUM_PREC_RADIX");
    }

    @Test
    public void columnOrderOfgetColumns() throws SQLException {
        ResultSet rs = meta.getColumns(null, null, "test", null);
        Assert.assertTrue(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        Assert.assertEquals(rsmeta.getColumnCount(), 24);
        Assert.assertEquals(rsmeta.getColumnName(1), "TABLE_CAT");
        Assert.assertEquals(rsmeta.getColumnName(2), "TABLE_SCHEM");
        Assert.assertEquals(rsmeta.getColumnName(3), "TABLE_NAME");
        Assert.assertEquals(rsmeta.getColumnName(4), "COLUMN_NAME");
        Assert.assertEquals(rsmeta.getColumnName(5), "DATA_TYPE");
        Assert.assertEquals(rsmeta.getColumnName(6), "TYPE_NAME");
        Assert.assertEquals(rsmeta.getColumnName(7), "COLUMN_SIZE");
        Assert.assertEquals(rsmeta.getColumnName(8), "BUFFER_LENGTH");
        Assert.assertEquals(rsmeta.getColumnName(9), "DECIMAL_DIGITS");
        Assert.assertEquals(rsmeta.getColumnName(10), "NUM_PREC_RADIX");
        Assert.assertEquals(rsmeta.getColumnName(11), "NULLABLE");
        Assert.assertEquals(rsmeta.getColumnName(12), "REMARKS");
        Assert.assertEquals(rsmeta.getColumnName(13), "COLUMN_DEF");
        Assert.assertEquals(rsmeta.getColumnName(14), "SQL_DATA_TYPE");
        Assert.assertEquals(rsmeta.getColumnName(15), "SQL_DATETIME_SUB");
        Assert.assertEquals(rsmeta.getColumnName(16), "CHAR_OCTET_LENGTH");
        Assert.assertEquals(rsmeta.getColumnName(17), "ORDINAL_POSITION");
        Assert.assertEquals(rsmeta.getColumnName(18), "IS_NULLABLE");
        // should be SCOPE_CATALOG, but misspelt in the standard
        Assert.assertEquals(rsmeta.getColumnName(19), "SCOPE_CATLOG");
        Assert.assertEquals(rsmeta.getColumnName(20), "SCOPE_SCHEMA");
        Assert.assertEquals(rsmeta.getColumnName(21), "SCOPE_TABLE");
        Assert.assertEquals(rsmeta.getColumnName(22), "SOURCE_DATA_TYPE");
        Assert.assertEquals(rsmeta.getColumnName(23), "IS_AUTOINCREMENT");
        Assert.assertEquals(rsmeta.getColumnName(24), "IS_GENERATEDCOLUMN");
        Assert.assertEquals(rs.getString("COLUMN_NAME").toUpperCase(), "ID");
        Assert.assertEquals(rs.getInt("ORDINAL_POSITION"), 1);
    }

    // the following functions always return an empty resultset, so
    // do not bother testing their parameters, only the column types
    @Test
    public void columnOrderOfgetProcedures() throws SQLException {
        ResultSet rs = meta.getProcedures(null, null, null);
        Assert.assertFalse(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        Assert.assertEquals(rsmeta.getColumnCount(), 8);
        Assert.assertEquals(rsmeta.getColumnName(1), "PROCEDURE_CAT");
        Assert.assertEquals(rsmeta.getColumnName(2), "PROCEDURE_SCHEM");
        Assert.assertEquals(rsmeta.getColumnName(3), "PROCEDURE_NAME");
        // currently (Java 1.5), cols 4,5,6 are undefined
        Assert.assertEquals(rsmeta.getColumnName(7), "REMARKS");
        Assert.assertEquals(rsmeta.getColumnName(8), "PROCEDURE_TYPE");
    }

    @Test
    public void columnOrderOfgetProcedurColumns() throws SQLException {
        ResultSet rs = meta.getProcedureColumns(null, null, null, null);
        Assert.assertFalse(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        Assert.assertEquals(rsmeta.getColumnCount(), 13);
        Assert.assertEquals(rsmeta.getColumnName(1), "PROCEDURE_CAT");
        Assert.assertEquals(rsmeta.getColumnName(2), "PROCEDURE_SCHEM");
        Assert.assertEquals(rsmeta.getColumnName(3), "PROCEDURE_NAME");
        Assert.assertEquals(rsmeta.getColumnName(4), "COLUMN_NAME");
        Assert.assertEquals(rsmeta.getColumnName(5), "COLUMN_TYPE");
        Assert.assertEquals(rsmeta.getColumnName(6), "DATA_TYPE");
        Assert.assertEquals(rsmeta.getColumnName(7), "TYPE_NAME");
        Assert.assertEquals(rsmeta.getColumnName(8), "PRECISION");
        Assert.assertEquals(rsmeta.getColumnName(9), "LENGTH");
        Assert.assertEquals(rsmeta.getColumnName(10), "SCALE");
        Assert.assertEquals(rsmeta.getColumnName(11), "RADIX");
        Assert.assertEquals(rsmeta.getColumnName(12), "NULLABLE");
        Assert.assertEquals(rsmeta.getColumnName(13), "REMARKS");
    }

    @Test
    public void columnOrderOfgetSchemas() throws SQLException {
        ResultSet rs = meta.getSchemas();
        Assert.assertFalse(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        Assert.assertEquals(rsmeta.getColumnCount(), 2);
        Assert.assertEquals(rsmeta.getColumnName(1), "TABLE_SCHEM");
        Assert.assertEquals(rsmeta.getColumnName(2), "TABLE_CATALOG");
    }

    @Test
    public void columnOrderOfgetCatalogs() throws SQLException {
        ResultSet rs = meta.getCatalogs();
        Assert.assertFalse(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        Assert.assertEquals(rsmeta.getColumnCount(), 1);
        Assert.assertEquals(rsmeta.getColumnName(1), "TABLE_CAT");
    }

    @Test
    public void columnOrderOfgetColumnPrivileges() throws SQLException {
        ResultSet rs = meta.getColumnPrivileges(null, null, null, null);
        Assert.assertFalse(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        Assert.assertEquals(rsmeta.getColumnCount(), 8);
        Assert.assertEquals(rsmeta.getColumnName(1), "TABLE_CAT");
        Assert.assertEquals(rsmeta.getColumnName(2), "TABLE_SCHEM");
        Assert.assertEquals(rsmeta.getColumnName(3), "TABLE_NAME");
        Assert.assertEquals(rsmeta.getColumnName(4), "COLUMN_NAME");
        Assert.assertEquals(rsmeta.getColumnName(5), "GRANTOR");
        Assert.assertEquals(rsmeta.getColumnName(6), "GRANTEE");
        Assert.assertEquals(rsmeta.getColumnName(7), "PRIVILEGE");
        Assert.assertEquals(rsmeta.getColumnName(8), "IS_GRANTABLE");
    }

    @Test
    public void columnOrderOfgetTablePrivileges() throws SQLException {
        ResultSet rs = meta.getTablePrivileges(null, null, null);
        Assert.assertFalse(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        Assert.assertEquals(rsmeta.getColumnCount(), 7);
        Assert.assertEquals(rsmeta.getColumnName(1), "TABLE_CAT");
        Assert.assertEquals(rsmeta.getColumnName(2), "TABLE_SCHEM");
        Assert.assertEquals(rsmeta.getColumnName(3), "TABLE_NAME");
        Assert.assertEquals(rsmeta.getColumnName(4), "GRANTOR");
        Assert.assertEquals(rsmeta.getColumnName(5), "GRANTEE");
        Assert.assertEquals(rsmeta.getColumnName(6), "PRIVILEGE");
        Assert.assertEquals(rsmeta.getColumnName(7), "IS_GRANTABLE");
    }

    @Test
    public void columnOrderOfgetBestRowIdentifier() throws SQLException {
        ResultSet rs = meta.getBestRowIdentifier(null, null, null, 0, false);
        Assert.assertFalse(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        Assert.assertEquals(rsmeta.getColumnCount(), 8);
        Assert.assertEquals(rsmeta.getColumnName(1), "SCOPE");
        Assert.assertEquals(rsmeta.getColumnName(2), "COLUMN_NAME");
        Assert.assertEquals(rsmeta.getColumnName(3), "DATA_TYPE");
        Assert.assertEquals(rsmeta.getColumnName(4), "TYPE_NAME");
        Assert.assertEquals(rsmeta.getColumnName(5), "COLUMN_SIZE");
        Assert.assertEquals(rsmeta.getColumnName(6), "BUFFER_LENGTH");
        Assert.assertEquals(rsmeta.getColumnName(7), "DECIMAL_DIGITS");
        Assert.assertEquals(rsmeta.getColumnName(8), "PSEUDO_COLUMN");
    }

    @Test
    public void columnOrderOfgetVersionColumns() throws SQLException {
        ResultSet rs = meta.getVersionColumns(null, null, null);
        Assert.assertFalse(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        Assert.assertEquals(rsmeta.getColumnCount(), 8);
        Assert.assertEquals(rsmeta.getColumnName(1), "SCOPE");
        Assert.assertEquals(rsmeta.getColumnName(2), "COLUMN_NAME");
        Assert.assertEquals(rsmeta.getColumnName(3), "DATA_TYPE");
        Assert.assertEquals(rsmeta.getColumnName(4), "TYPE_NAME");
        Assert.assertEquals(rsmeta.getColumnName(5), "COLUMN_SIZE");
        Assert.assertEquals(rsmeta.getColumnName(6), "BUFFER_LENGTH");
        Assert.assertEquals(rsmeta.getColumnName(7), "DECIMAL_DIGITS");
        Assert.assertEquals(rsmeta.getColumnName(8), "PSEUDO_COLUMN");
    }

    @Test
    public void viewIngetPrimaryKeys() throws SQLException {
        ResultSet rs;
        stat.executeUpdate("create table t1 (c1, c2, c3);");
        stat.executeUpdate("create view view_nopk (v1, v2) as select c1, c3 from t1;");
        rs = meta.getPrimaryKeys(null, null, "view_nopk");
        Assert.assertFalse(rs.next());
    }

    @Test
    public void moreOfgetColumns() throws SQLException {
        ResultSet rs;
        stat.executeUpdate("create table tabcols1 (col1, col2);");
        // mixed-case table, column and primary key names
        stat.executeUpdate("CREATE TABLE TabCols2 (Col1, Col2);");
        // quoted table, column and primary key names
        stat.executeUpdate("CREATE TABLE `TabCols3` (`Col1`, `Col2`);");
        rs = meta.getColumns(null, null, "tabcols1", "%");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "tabcols1");
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "col1");
        Assert.assertEquals(rs.getInt("DATA_TYPE"), Types.VARCHAR);
        Assert.assertEquals(rs.getString("IS_NULLABLE"), "YES");
        Assert.assertEquals(rs.getString("COLUMN_DEF"), null);
        Assert.assertEquals(rs.getString("IS_AUTOINCREMENT"), "NO");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "tabcols1");
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "col2");
        Assert.assertEquals(rs.getInt("DATA_TYPE"), Types.VARCHAR);
        Assert.assertFalse(rs.next());
        rs = meta.getColumns(null, null, "TabCols2", "%");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "TabCols2");
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "Col1");
        Assert.assertEquals(rs.getInt("DATA_TYPE"), Types.VARCHAR);
        Assert.assertEquals(rs.getString("IS_NULLABLE"), "YES");
        Assert.assertEquals(rs.getString("COLUMN_DEF"), null);
        Assert.assertEquals(rs.getString("IS_AUTOINCREMENT"), "NO");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "TabCols2");
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "Col2");
        Assert.assertEquals(rs.getInt("DATA_TYPE"), Types.VARCHAR);
        Assert.assertFalse(rs.next());
        rs = meta.getColumns(null, null, "TabCols3", "%");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "TabCols3");
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "Col1");
        Assert.assertEquals(rs.getInt("DATA_TYPE"), Types.VARCHAR);
        Assert.assertEquals(rs.getString("IS_NULLABLE"), "YES");
        Assert.assertEquals(rs.getString("COLUMN_DEF"), null);
        Assert.assertEquals(rs.getString("IS_AUTOINCREMENT"), "NO");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "TabCols3");
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "Col2");
        Assert.assertEquals(rs.getInt("DATA_TYPE"), Types.VARCHAR);
        Assert.assertFalse(rs.next());
    }

    @Test
    public void autoincrement() throws SQLException {
        ResultSet rs;
        // no autoincrement no rowid
        stat.executeUpdate("CREATE TABLE TAB1 (COL1 INTEGER NOT NULL PRIMARY KEY, COL2) WITHOUT ROWID;");
        // no autoincrement
        stat.executeUpdate("CREATE TABLE TAB2 (COL1 INTEGER NOT NULL PRIMARY KEY, COL2);");
        // autoincrement
        stat.executeUpdate("CREATE TABLE TAB3 (COL1 INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, COL2);");
        rs = meta.getColumns(null, null, "TAB1", "%");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "TAB1");
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "COL1");
        Assert.assertEquals(rs.getInt("DATA_TYPE"), Types.INTEGER);
        Assert.assertEquals(rs.getString("IS_NULLABLE"), "NO");
        Assert.assertEquals(rs.getString("COLUMN_DEF"), null);
        Assert.assertEquals(rs.getString("IS_AUTOINCREMENT"), "NO");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "TAB1");
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "COL2");
        Assert.assertEquals(rs.getInt("DATA_TYPE"), Types.VARCHAR);
        Assert.assertFalse(rs.next());
        rs = meta.getColumns(null, null, "TAB2", "%");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "TAB2");
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "COL1");
        Assert.assertEquals(rs.getInt("DATA_TYPE"), Types.INTEGER);
        Assert.assertEquals(rs.getString("IS_NULLABLE"), "NO");
        Assert.assertEquals(rs.getString("COLUMN_DEF"), null);
        Assert.assertEquals(rs.getString("IS_AUTOINCREMENT"), "NO");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "TAB2");
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "COL2");
        Assert.assertEquals(rs.getInt("DATA_TYPE"), Types.VARCHAR);
        Assert.assertFalse(rs.next());
        rs = meta.getColumns(null, null, "TAB3", "%");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "TAB3");
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "COL1");
        Assert.assertEquals(rs.getInt("DATA_TYPE"), Types.INTEGER);
        Assert.assertEquals(rs.getString("IS_NULLABLE"), "NO");
        Assert.assertEquals(rs.getString("COLUMN_DEF"), null);
        Assert.assertEquals(rs.getString("IS_AUTOINCREMENT"), "YES");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(rs.getString("TABLE_NAME"), "TAB3");
        Assert.assertEquals(rs.getString("COLUMN_NAME"), "COL2");
        Assert.assertEquals(rs.getInt("DATA_TYPE"), Types.VARCHAR);
        Assert.assertFalse(rs.next());
    }

    @Test
    public void columnOrderOfgetPrimaryKeys() throws Exception {
        ResultSet rs;
        ResultSetMetaData rsmeta;
        stat.executeUpdate("create table nopk (c1, c2, c3, c4);");
        stat.executeUpdate("create table pk1 (col1 primary key, col2, col3);");
        stat.executeUpdate("create table pk2 (col1, col2 primary key, col3);");
        stat.executeUpdate("create table pk3 (col1, col2, col3, col4, primary key (col3, col2  ));");
        // extra spaces and mixed case are intentional, do not remove!
        stat.executeUpdate(("create table pk4 (col1, col2, col3, col4, " + "\r\nCONSTraint\r\nnamed  primary\r\n\t\t key   (col3, col2  ));"));
        // mixed-case table, column and primary key names - GitHub issue #219
        stat.executeUpdate("CREATE TABLE Pk5 (Col1, Col2, Col3, Col4, CONSTRAINT NamedPk PRIMARY KEY (Col3, Col2));");
        // quoted table, column and primary key names - GitHub issue #219
        stat.executeUpdate("CREATE TABLE `Pk6` (`Col1`, `Col2`, `Col3`, `Col4`, CONSTRAINT `NamedPk` PRIMARY KEY (`Col3`, `Col2`));");
        // spaces before and after "primary key" - GitHub issue #236
        stat.executeUpdate("CREATE TABLE pk7 (col1, col2, col3, col4 VARCHAR(10),PRIMARY KEY (col1, col2, col3));");
        stat.executeUpdate("CREATE TABLE pk8 (col1, col2, col3, col4 VARCHAR(10), PRIMARY KEY(col1, col2, col3));");
        stat.executeUpdate("CREATE TABLE pk9 (col1, col2, col3, col4 VARCHAR(10),PRIMARY KEY(col1, col2, col3));");
        stat.executeUpdate("CREATE TABLE `Pk10` (`Col1`, `Col2`, `Col3`, `Col4`, CONSTRAINT `NamedPk`PRIMARY KEY (`Col3`, `Col2`));");
        stat.executeUpdate("CREATE TABLE `Pk11` (`Col1`, `Col2`, `Col3`, `Col4`, CONSTRAINT `NamedPk` PRIMARY KEY(`Col3`, `Col2`));");
        stat.executeUpdate("CREATE TABLE `Pk12` (`Col1`, `Col2`, `Col3`, `Col4`, CONSTRAINT`NamedPk`PRIMARY KEY(`Col3`,`Col2`));");
        stat.executeUpdate("CREATE TABLE \"Pk13\" (\"Col1\", \"Col2\", \"Col3\", \"Col4\", CONSTRAINT \"NamedPk\" PRIMARY KEY(\"Col3\",\"Col2\"));");
        stat.executeUpdate("CREATE TABLE \"Pk14\" (\"Col1\", \"Col2\", \"Col3\", \"Col4\", PRIMARY KEY(\"Col3\"), FOREIGN KEY (\"Col1\") REFERENCES \"pk1\" (\"col1\"))");
        stat.executeUpdate("CREATE TABLE \"Pk15\" (\"Col1\", \"Col2\", \"Col3\", \"Col4\", PRIMARY KEY(\"Col3\", \"Col2\"), FOREIGN KEY (\"Col1\") REFERENCES \"pk1\" (\"col1\"))");
        rs = meta.getPrimaryKeys(null, null, "nopk");
        Assert.assertFalse(rs.next());
        rsmeta = rs.getMetaData();
        Assert.assertEquals(rsmeta.getColumnCount(), 6);
        Assert.assertEquals(rsmeta.getColumnName(1), "TABLE_CAT");
        Assert.assertEquals(rsmeta.getColumnName(2), "TABLE_SCHEM");
        Assert.assertEquals(rsmeta.getColumnName(3), "TABLE_NAME");
        Assert.assertEquals(rsmeta.getColumnName(4), "COLUMN_NAME");
        Assert.assertEquals(rsmeta.getColumnName(5), "KEY_SEQ");
        Assert.assertEquals(rsmeta.getColumnName(6), "PK_NAME");
        rs.close();
        assertPrimaryKey(meta, "pk1", null, "col1");
        assertPrimaryKey(meta, "pk2", null, "col2");
        assertPrimaryKey(meta, "pk3", null, "col3", "col2");
        assertPrimaryKey(meta, "pk4", "named", "col3", "col2");
        assertPrimaryKey(meta, "Pk5", "NamedPk", "Col3", "Col2");
        assertPrimaryKey(meta, "Pk6", "NamedPk", "Col3", "Col2");
        assertPrimaryKey(meta, "pk7", null, "col1", "col2", "col3");
        assertPrimaryKey(meta, "pk8", null, "col1", "col2", "col3");
        assertPrimaryKey(meta, "pk9", null, "col1", "col2", "col3");
        assertPrimaryKey(meta, "Pk10", "NamedPk", "Col3", "Col2");
        assertPrimaryKey(meta, "Pk11", "NamedPk", "Col3", "Col2");
        assertPrimaryKey(meta, "Pk12", "NamedPk", "Col3", "Col2");
        assertPrimaryKey(meta, "Pk13", "NamedPk", "Col3", "Col2");
        assertPrimaryKey(meta, "Pk14", null, "Col3");
        assertPrimaryKey(meta, "Pk15", null, "Col3", "Col2");
    }

    @Test
    public void columnOrderOfgetImportedKeys() throws SQLException {
        stat.executeUpdate("create table person (id integer)");
        stat.executeUpdate("create table address (pid integer, name, foreign key(pid) references person(id))");
        ResultSet importedKeys = meta.getImportedKeys("default", "global", "address");
        Assert.assertTrue(importedKeys.next());
        Assert.assertEquals("default", importedKeys.getString("PKTABLE_CAT"));
        Assert.assertEquals("global", importedKeys.getString("PKTABLE_SCHEM"));
        Assert.assertEquals("default", importedKeys.getString("FKTABLE_CAT"));
        Assert.assertEquals("person", importedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("id", importedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertNotNull(importedKeys.getString("PK_NAME"));
        Assert.assertNotNull(importedKeys.getString("FK_NAME"));
        Assert.assertEquals("address", importedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("pid", importedKeys.getString("FKCOLUMN_NAME"));
        importedKeys.close();
        importedKeys = meta.getImportedKeys(null, null, "person");
        Assert.assertTrue((!(importedKeys.next())));
        importedKeys.close();
    }

    @Test
    public void columnOrderOfgetExportedKeys() throws SQLException {
        stat.executeUpdate("create table person (id integer primary key)");
        stat.executeUpdate("create table address (pid integer, name, foreign key(pid) references person(id))");
        ResultSet exportedKeys = meta.getExportedKeys("default", "global", "person");
        Assert.assertTrue(exportedKeys.next());
        Assert.assertEquals("default", exportedKeys.getString("PKTABLE_CAT"));
        Assert.assertEquals("global", exportedKeys.getString("PKTABLE_SCHEM"));
        Assert.assertEquals("default", exportedKeys.getString("FKTABLE_CAT"));
        Assert.assertEquals("global", exportedKeys.getString("FKTABLE_SCHEM"));
        Assert.assertNotNull(exportedKeys.getString("PK_NAME"));
        Assert.assertNotNull(exportedKeys.getString("FK_NAME"));
        Assert.assertEquals("person", exportedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("id", exportedKeys.getString("PKCOLUMN_NAME"));
        Assert.assertEquals("address", exportedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("pid", exportedKeys.getString("FKCOLUMN_NAME"));
        exportedKeys.close();
        exportedKeys = meta.getExportedKeys(null, null, "address");
        Assert.assertFalse(exportedKeys.next());
        exportedKeys.close();
        // With explicit primary column defined.
        stat.executeUpdate("create table REFERRED (ID integer primary key not null)");
        stat.executeUpdate("create table REFERRING (ID integer, RID integer, constraint fk\r\n foreign\tkey\r\n(RID) references REFERRED(id))");
        exportedKeys = meta.getExportedKeys(null, null, "referred");
        Assert.assertEquals("REFERRED", exportedKeys.getString("PKTABLE_NAME"));
        Assert.assertEquals("REFERRING", exportedKeys.getString("FKTABLE_NAME"));
        Assert.assertEquals("fk", exportedKeys.getString("FK_NAME"));
        exportedKeys.close();
    }

    @Test
    public void columnOrderOfgetCrossReference() throws SQLException {
        stat.executeUpdate("create table person (id integer)");
        stat.executeUpdate("create table address (pid integer, name, foreign key(pid) references person(id))");
        ResultSet cr = meta.getCrossReference(null, null, "person", null, null, "address");
        // assertTrue(cr.next());
    }

    /* TODO

    @Test public void columnOrderOfgetTypeInfo() throws SQLException {
    @Test public void columnOrderOfgetIndexInfo() throws SQLException {
    @Test public void columnOrderOfgetSuperTypes() throws SQLException {
    @Test public void columnOrderOfgetSuperTables() throws SQLException {
    @Test public void columnOrderOfgetAttributes() throws SQLException {
     */
    @Test
    public void columnOrderOfgetUDTs() throws SQLException {
        ResultSet rs = meta.getUDTs(null, null, null, null);
        Assert.assertFalse(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        Assert.assertEquals(rsmeta.getColumnCount(), 7);
        Assert.assertEquals(rsmeta.getColumnName(1), "TYPE_CAT");
        Assert.assertEquals(rsmeta.getColumnName(2), "TYPE_SCHEM");
        Assert.assertEquals(rsmeta.getColumnName(3), "TYPE_NAME");
        Assert.assertEquals(rsmeta.getColumnName(4), "CLASS_NAME");
        Assert.assertEquals(rsmeta.getColumnName(5), "DATA_TYPE");
        Assert.assertEquals(rsmeta.getColumnName(6), "REMARKS");
        Assert.assertEquals(rsmeta.getColumnName(7), "BASE_TYPE");
    }

    @Test
    public void getIndexInfoOnTest() throws SQLException {
        ResultSet rs = meta.getIndexInfo(null, null, "test", false, false);
        Assert.assertNotNull(rs);
    }

    @Test
    public void getIndexInfoIndexedSingle() throws SQLException {
        stat.executeUpdate("create table testindex (id integer primary key, fn float default 0.0, sn not null);");
        stat.executeUpdate("create index testindex_idx on testindex (sn);");
        ResultSet rs = meta.getIndexInfo(null, null, "testindex", false, false);
        ResultSetMetaData rsmd = rs.getMetaData();
        Assert.assertNotNull(rs);
        Assert.assertNotNull(rsmd);
    }

    @Test
    public void getIndexInfoIndexedSingleExpr() throws SQLException {
        stat.executeUpdate("create table testindex (id integer primary key, fn float default 0.0, sn not null);");
        stat.executeUpdate("create index testindex_idx on testindex (sn, fn/2);");
        ResultSet rs = meta.getIndexInfo(null, null, "testindex", false, false);
        ResultSetMetaData rsmd = rs.getMetaData();
        Assert.assertNotNull(rs);
        Assert.assertNotNull(rsmd);
    }

    @Test
    public void getIndexInfoIndexedMulti() throws SQLException {
        stat.executeUpdate("create table testindex (id integer primary key, fn float default 0.0, sn not null);");
        stat.executeUpdate("create index testindex_idx on testindex (sn);");
        stat.executeUpdate("create index testindex_pk_idx on testindex (id);");
        ResultSet rs = meta.getIndexInfo(null, null, "testindex", false, false);
        ResultSetMetaData rsmd = rs.getMetaData();
        Assert.assertNotNull(rs);
        Assert.assertNotNull(rsmd);
    }

    @Test
    public void version() throws Exception {
        File versionFile = new File("./VERSION");
        Properties version = new Properties();
        version.load(new FileReader(versionFile));
        String versionString = version.getProperty("version");
        int majorVersion = Integer.valueOf(versionString.split("\\.")[0]);
        int minorVersion = Integer.valueOf(versionString.split("\\.")[1]);
        Assert.assertTrue("major version check", (majorVersion > 0));
        Assert.assertEquals("driver name", "SQLite JDBC", meta.getDriverName());
        Assert.assertTrue("driver version", meta.getDriverVersion().startsWith(String.format("%d.%d", majorVersion, minorVersion)));
        Assert.assertEquals("driver major version", majorVersion, meta.getDriverMajorVersion());
        Assert.assertEquals("driver minor version", minorVersion, meta.getDriverMinorVersion());
        Assert.assertEquals("db name", "SQLite", meta.getDatabaseProductName());
        Assert.assertEquals("db version", versionString, meta.getDatabaseProductVersion());
        Assert.assertEquals("db major version", majorVersion, meta.getDatabaseMajorVersion());
        Assert.assertEquals("db minor version", minorVersion, meta.getDatabaseMinorVersion());
        Assert.assertEquals("user name", null, meta.getUserName());
    }
}

