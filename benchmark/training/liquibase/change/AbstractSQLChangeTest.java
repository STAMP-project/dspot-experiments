package liquibase.change;


import java.io.IOException;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.core.MSSQLDatabase;
import liquibase.exception.DatabaseException;
import liquibase.statement.SqlStatement;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;


public class AbstractSQLChangeTest {
    @Test
    public void constructor() {
        AbstractSQLChange change = new AbstractSQLChangeTest.ExampleAbstractSQLChange();
        Assert.assertFalse(change.isStripComments());
        Assert.assertTrue(change.isSplitStatements());
        Assert.assertNull(change.getEndDelimiter());
    }

    @Test
    public void supports() {
        Assert.assertTrue("AbstractSQLChange automatically supports all databases", new AbstractSQLChangeTest.ExampleAbstractSQLChange().supports(mock(Database.class)));
    }

    @Test
    public void setStrippingComments() {
        AbstractSQLChange change = new AbstractSQLChangeTest.ExampleAbstractSQLChange();
        change.setStripComments(true);
        Assert.assertTrue(change.isStripComments());
        change.setStripComments(false);
        Assert.assertFalse(change.isStripComments());
        change.setStripComments(null);
        Assert.assertFalse(change.isStripComments());
    }

    @Test
    public void setSplittingStatements() {
        AbstractSQLChange change = new AbstractSQLChangeTest.ExampleAbstractSQLChange();
        change.setSplitStatements(true);
        Assert.assertTrue(change.isSplitStatements());
        change.setSplitStatements(false);
        Assert.assertFalse(change.isSplitStatements());
        change.setSplitStatements(null);
        Assert.assertTrue(change.isSplitStatements());
    }

    @Test
    public void setSql() {
        AbstractSQLChange sql = new AbstractSQLChangeTest.ExampleAbstractSQLChange();
        sql.setSql("SOME SQL");
        Assert.assertEquals("SOME SQL", sql.getSql());
        sql.setSql("   SOME SQL   ");
        Assert.assertEquals("setSql should trim", "SOME SQL", sql.getSql());
        sql.setSql("   ");
        Assert.assertNull("setSql should set empty strings to null", sql.getSql());
    }

    @Test
    public void setEndDelmiter() {
        AbstractSQLChange change = new AbstractSQLChangeTest.ExampleAbstractSQLChange();
        change.setEndDelimiter("GO");
        Assert.assertEquals("GO", change.getEndDelimiter());
        change.setEndDelimiter(";");
        Assert.assertEquals(";", change.getEndDelimiter());
    }

    @Test
    public void generateCheckSum_lineEndingIndependent() {
        CheckSum sql = generateCheckSum();
        CheckSum sqlCRLF = generateCheckSum();
        CheckSum sqlLF = generateCheckSum();
        CheckSum sqlDifferent = generateCheckSum();
        Assert.assertEquals(sql.toString(), sqlCRLF.toString());
        Assert.assertEquals(sql.toString(), sqlLF.toString());
        Assert.assertFalse(sql.toString().equals(sqlDifferent.toString()));
    }

    @Test
    public void generateCheckSum_nullSql() {
        Assert.assertNotNull(new AbstractSQLChangeTest.ExampleAbstractSQLChange().generateCheckSum());
    }

    @Test
    public void generateCheckSum_changesBasedOnParams() {
        CheckSum baseCheckSum = generateCheckSum();
        AbstractSQLChangeTest.ExampleAbstractSQLChange change = new AbstractSQLChangeTest.ExampleAbstractSQLChange("SOME SQL");
        setSplitStatements(false);
        Assert.assertFalse(baseCheckSum.toString().equals(change.generateCheckSum().toString()));
        change = new AbstractSQLChangeTest.ExampleAbstractSQLChange("SOME SQL");
        setEndDelimiter("X");
        Assert.assertFalse(baseCheckSum.toString().equals(change.generateCheckSum().toString()));
        change = new AbstractSQLChangeTest.ExampleAbstractSQLChange("SOME SQL");
        setStripComments(true);
        Assert.assertFalse(baseCheckSum.toString().equals(change.generateCheckSum().toString()));
    }

    @Test
    public void generateStatements_nullSqlMakesNoStatements() {
        Assert.assertEquals(0, new AbstractSQLChangeTest.ExampleAbstractSQLChange(null).generateStatements(mock(Database.class)).length);
    }

    @Test
    public void generateStatements() {
        AbstractSQLChangeTest.ExampleAbstractSQLChange change = new AbstractSQLChangeTest.ExampleAbstractSQLChange("LINE 1;\n--a comment\nLINE 2;\nLINE 3;");
        setSplitStatements(true);
        setStripComments(true);
        SqlStatement[] statements = change.generateStatements(mock(Database.class));
        Assert.assertEquals(3, statements.length);
        Assert.assertEquals("LINE 1", getSql());
        Assert.assertEquals("LINE 2", getSql());
        Assert.assertEquals("LINE 3", getSql());
    }

    @Test
    public void generateStatements_crlfEndingStandardizes() {
        AbstractSQLChangeTest.ExampleAbstractSQLChange change = new AbstractSQLChangeTest.ExampleAbstractSQLChange("LINE 1;\r\n--a comment\r\nLINE 2;\r\nLINE 3;");
        setSplitStatements(true);
        setStripComments(true);
        SqlStatement[] statements = change.generateStatements(mock(Database.class));
        Assert.assertEquals(3, statements.length);
        Assert.assertEquals("LINE 1", getSql());
        Assert.assertEquals("LINE 2", getSql());
        Assert.assertEquals("LINE 3", getSql());
    }

    @Test
    public void generateStatements_convertsEndingsOnSqlServer() {
        AbstractSQLChangeTest.ExampleAbstractSQLChange change = new AbstractSQLChangeTest.ExampleAbstractSQLChange("LINE 1;\n--a comment\nLINE 2;\nLINE 3;");
        setSplitStatements(false);
        setStripComments(true);
        SqlStatement[] statements = change.generateStatements(new MSSQLDatabase());
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals("LINE 1;\r\n\r\nLINE 2;\r\nLINE 3;", getSql());
    }

    @Test
    public void generateStatements_keepComments() {
        AbstractSQLChangeTest.ExampleAbstractSQLChange change = new AbstractSQLChangeTest.ExampleAbstractSQLChange("LINE 1;\n--a comment\nLINE 2;\nLINE 3;");
        setSplitStatements(true);
        setStripComments(false);
        SqlStatement[] statements = change.generateStatements(mock(Database.class));
        Assert.assertEquals(3, statements.length);
        Assert.assertEquals("LINE 1", getSql());
        Assert.assertEquals("--a comment\nLINE 2", getSql());
        Assert.assertEquals("LINE 3", getSql());
    }

    @Test
    public void generateStatements_noSplit() {
        AbstractSQLChangeTest.ExampleAbstractSQLChange change = new AbstractSQLChangeTest.ExampleAbstractSQLChange("LINE 1;\n--a comment\nLINE 2;\nLINE 3;");
        setSplitStatements(false);
        setStripComments(true);
        SqlStatement[] statements = change.generateStatements(mock(Database.class));
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals("LINE 1;\n\nLINE 2;\nLINE 3;", getSql());
    }

    @Test
    public void generateStatements_noSplitKeepComments() {
        AbstractSQLChangeTest.ExampleAbstractSQLChange change = new AbstractSQLChangeTest.ExampleAbstractSQLChange("LINE 1;\n--a comment\nLINE 2;\nLINE 3;");
        setSplitStatements(false);
        setStripComments(false);
        SqlStatement[] statements = change.generateStatements(mock(Database.class));
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals("LINE 1;\n--a comment\nLINE 2;\nLINE 3;", getSql());
    }

    @Test
    public void normalizeSql() throws IOException {
        assertNormalizingStreamCorrect("single line String", "single line String");
        assertNormalizingStreamCorrect("single line string with whitespace", "single line string with      whitespace");
        assertNormalizingStreamCorrect("multiple line string", "\r\nmultiple\r\nline\r\nstring\r\n");
        assertNormalizingStreamCorrect("multiple line string", "\rmultiple\rline\rstring\r");
        assertNormalizingStreamCorrect("multiple line string", "\nmultiple\nline\nstring\n");
        assertNormalizingStreamCorrect("a line with double newlines", "\n\na\nline \n with \r\n \r\n double \n \n \n \n newlines");
        // assertNormalizingStreamCorrect("", null);
        assertNormalizingStreamCorrect("", "    ");
        assertNormalizingStreamCorrect("", " \n \n \n   \n  ");
        // test quickBuffer -> resizingBuffer handoff
        String longSpaceString = "a line with a lot of: wait for it....                                                                                                                                                                                                                                                                                         spaces";
        assertNormalizingStreamCorrect("a line with a lot of: wait for it.... spaces", longSpaceString);
        String versionNormalized = "INSERT INTO recommendation_list(instanceId, name, publicId) SELECT DISTINCT instanceId, \"default\" as name, \"default\" as publicId FROM recommendation;";
        String version1 = "INSERT INTO recommendation_list(instanceId, name, publicId)\n" + ("SELECT DISTINCT instanceId, \"default\" as name, \"default\" as publicId\n" + "FROM recommendation;");
        assertNormalizingStreamCorrect(versionNormalized, version1);
        String version2 = "INSERT INTO \n" + ((((((("    recommendation_list(instanceId, name, publicId)\n" + "SELECT \n") + "    DISTINCT \n") + "        instanceId, \n") + "          \"default\" as name, \n") + "          \"default\" as publicId\n") + "   FROM \n") + "       recommendation;");
        assertNormalizingStreamCorrect(versionNormalized, version2);
    }

    @Test
    public void generateStatements_willCallNativeSqlIfPossible() throws DatabaseException {
        AbstractSQLChangeTest.ExampleAbstractSQLChange change = new AbstractSQLChangeTest.ExampleAbstractSQLChange("SOME SQL");
        Database database = mock(Database.class);
        DatabaseConnection connection = Mockito.mock(DatabaseConnection.class);
        Mockito.when(database.getConnection()).thenReturn(connection);
        Mockito.when(connection.nativeSQL("SOME SQL")).thenReturn("SOME NATIVE SQL");
        SqlStatement[] statements = change.generateStatements(database);
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals("SOME NATIVE SQL", getSql());
        // If there is an error, it falls back to passed SQL
        Mockito.when(connection.nativeSQL("SOME SQL")).thenThrow(new DatabaseException("Testing exception"));
        statements = change.generateStatements(database);
        Assert.assertEquals(1, statements.length);
        Assert.assertEquals("SOME SQL", getSql());
    }

    @DatabaseChange(name = "exampleAbstractSQLChange", description = "Used for the AbstractSQLChangeTest unit test", priority = 1)
    private static class ExampleAbstractSQLChange extends AbstractSQLChange {
        private ExampleAbstractSQLChange() {
        }

        private ExampleAbstractSQLChange(String sql) {
            setSql(sql);
        }

        @Override
        public String getConfirmationMessage() {
            return "Example SQL Change Message";
        }

        @Override
        public String getSerializedObjectNamespace() {
            return STANDARD_CHANGELOG_NAMESPACE;
        }
    }
}

