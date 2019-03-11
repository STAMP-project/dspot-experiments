package liquibase.sqlgenerator.core;


import liquibase.database.core.MSSQLDatabase;
import liquibase.statement.core.InsertOrUpdateStatement;
import org.junit.Assert;
import org.junit.Test;


public class InsertOrUpdateGeneratorMSSQLTest {
    @Test
    public void getRecordCheck() {
        InsertOrUpdateGeneratorMSSQL generator = new InsertOrUpdateGeneratorMSSQL();
        MSSQLDatabase database = new MSSQLDatabase();
        InsertOrUpdateStatement statement = new InsertOrUpdateStatement("mycatalog", "myschema", "mytable", "pk_col1");
        statement.addColumnValue("pk_col1", "value1");
        statement.addColumnValue("col2", "value2");
        String where = "1 = 1";
        String recordCheck = ((String) (InsertOrUpdateGeneratorMSSQLTest.invokePrivateMethod(generator, "getRecordCheck", new Object[]{ statement, database, where })));
        Integer lineNumber = 0;
        String[] lines = recordCheck.split("\n");
        Assert.assertEquals("DECLARE @reccount integer", lines[lineNumber]);
        lineNumber++;
        Assert.assertEquals(("SELECT @reccount = count(*) FROM mycatalog.myschema.mytable WHERE " + where), lines[lineNumber]);
        lineNumber++;
        Assert.assertEquals("IF @reccount = 0", lines[lineNumber]);
    }

    @Test
    public void getInsert() {
        InsertOrUpdateGeneratorMSSQL generator = new InsertOrUpdateGeneratorMSSQL();
        MSSQLDatabase database = new MSSQLDatabase();
        InsertOrUpdateStatement statement = new InsertOrUpdateStatement("mycatalog", "myschema", "mytable", "pk_col1");
        statement.addColumnValue("pk_col1", "value1");
        statement.addColumnValue("col2", "value2");
        String where = "1 = 1";
        Class c = InsertOrUpdateGenerator.class.getClass();
        // InsertOrUpdateStatement insertOrUpdateStatement, Database database, SqlGeneratorChain sqlGeneratorChain
        String insertStatement = ((String) (InsertOrUpdateGeneratorMSSQLTest.invokePrivateMethod(generator, "getInsertStatement", new Object[]{ statement, database, null })));
        Integer lineNumber = 0;
        String[] lines = insertStatement.split("\n");
        Assert.assertEquals("BEGIN", lines[lineNumber]);
        lineNumber++;
        Assert.assertTrue(lines[lineNumber].startsWith("INSERT"));
        lineNumber++;
        Assert.assertEquals("END", lines[lineNumber]);
    }

    @Test
    public void getElse() {
        InsertOrUpdateGeneratorMSSQL generator = new InsertOrUpdateGeneratorMSSQL();
        MSSQLDatabase database = new MSSQLDatabase();
        InsertOrUpdateStatement statement = new InsertOrUpdateStatement("mycatalog", "myschema", "mytable", "pk_col1");
        statement.addColumnValue("pk_col1", "value1");
        statement.addColumnValue("col2", "value2");
        String where = "1 = 1";
        Class c = InsertOrUpdateGenerator.class.getClass();
        // InsertOrUpdateStatement insertOrUpdateStatement, Database database, SqlGeneratorChain sqlGeneratorChain
        String insertStatement = ((String) (InsertOrUpdateGeneratorMSSQLTest.invokePrivateMethod(generator, "getElse", new Object[]{ database })));
        Integer lineNumber = 0;
        String[] lines = insertStatement.split("\n");
        Assert.assertEquals("ELSE", lines[lineNumber]);
    }

    @Test
    public void getUpdate() {
        InsertOrUpdateGeneratorMSSQL generator = new InsertOrUpdateGeneratorMSSQL();
        MSSQLDatabase database = new MSSQLDatabase();
        InsertOrUpdateStatement statement = new InsertOrUpdateStatement("mycatalog", "myschema", "mytable", "pk_col1");
        statement.addColumnValue("col2", "value2");
        String where = "1 = 1";
        Class c = InsertOrUpdateGenerator.class.getClass();
        // InsertOrUpdateStatement insertOrUpdateStatement, Database database, String whereClause, SqlGeneratorChain sqlGeneratorChain
        String insertStatement = ((String) (InsertOrUpdateGeneratorMSSQLTest.invokePrivateMethod(generator, "getUpdateStatement", new Object[]{ statement, database, where, null })));
        Integer lineNumber = 0;
        String[] lines = insertStatement.split("\n");
        Assert.assertEquals("BEGIN", lines[lineNumber]);
        lineNumber++;
        Assert.assertTrue(lines[lineNumber].startsWith("UPDATE"));
        lineNumber++;
        Assert.assertEquals("END", lines[lineNumber]);
    }
}

