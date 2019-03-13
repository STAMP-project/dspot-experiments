package liquibase.sqlgenerator.core;


import liquibase.database.core.InformixDatabase;
import liquibase.statement.core.InsertOrUpdateStatement;
import org.junit.Assert;
import org.junit.Test;


public class InsertOrUpdateGeneratorInformixTest {
    private InsertOrUpdateGeneratorInformix generator;

    private InsertOrUpdateStatement statement;

    private InformixDatabase database;

    @Test
    public void getRecordCheck() throws Exception {
        String recordCheck = ((String) (InsertOrUpdateGeneratorInformixTest.invokePrivateMethod(generator, "getRecordCheck", new Object[]{ statement, database, null })));
        Assert.assertNotNull(recordCheck);
        Integer lineNumber = 0;
        String[] lines = recordCheck.split("\n");
        Assert.assertEquals("MERGE INTO mycatalog:myschema.mytable AS dst", lines[lineNumber]);
        lineNumber++;
        Assert.assertEquals("USING (", lines[lineNumber]);
        lineNumber++;
        Assert.assertEquals("\tSELECT 1 AS pk_col1, 2 AS pk_col2, \'value2\' AS col2, NULL::INTEGER AS col3", lines[lineNumber]);
        lineNumber++;
        Assert.assertEquals("\tFROM sysmaster:informix.sysdual", lines[lineNumber]);
        lineNumber++;
        Assert.assertEquals(") AS src", lines[lineNumber]);
        lineNumber++;
        Assert.assertEquals("ON dst.pk_col1 = src.pk_col1 AND dst.pk_col2 = src.pk_col2", lines[lineNumber]);
        lineNumber++;
        Assert.assertEquals("WHEN NOT MATCHED THEN", lines[lineNumber]);
    }

    @Test
    public void getInsert() throws Exception {
        String insertStatement = ((String) (InsertOrUpdateGeneratorInformixTest.invokePrivateMethod(generator, "getInsertStatement", new Object[]{ statement, database, null })));
        Assert.assertNotNull(insertStatement);
        Integer lineNumber = 0;
        String[] lines = insertStatement.split("\n");
        Assert.assertEquals("INSERT (dst.pk_col1, dst.pk_col2, dst.col2, dst.col3) VALUES (src.pk_col1, src.pk_col2, src.col2, src.col3)", lines[lineNumber]);
    }

    @Test
    public void getElse() throws Exception {
        String elseStatement = ((String) (InsertOrUpdateGeneratorInformixTest.invokePrivateMethod(generator, "getElse", new Object[]{ database })));
        Assert.assertNotNull(elseStatement);
        Integer lineNumber = 0;
        String[] lines = elseStatement.split("\n");
        Assert.assertEquals("", lines[lineNumber]);
    }

    @Test
    public void getUpdateStatement() throws Exception {
        String updateStatement = ((String) (InsertOrUpdateGeneratorInformixTest.invokePrivateMethod(generator, "getUpdateStatement", new Object[]{ statement, database, null, null })));
        Assert.assertNotNull(updateStatement);
        Integer lineNumber = 0;
        String[] lines = updateStatement.split("\n");
        Assert.assertEquals("WHEN MATCHED THEN", lines[lineNumber]);
        lineNumber++;
        Assert.assertEquals("UPDATE SET dst.col2 = src.col2, dst.col3 = src.col3", lines[lineNumber]);
    }

    /**
     * When the table data is only keys, there will be no WHEN MATCHED THEN UPDATE... statement.
     *
     * @throws Exception
     * 		Throws exception
     */
    @Test
    public void getUpdateStatementKeysOnly() throws Exception {
        statement = new InsertOrUpdateStatement("mycatalog", "myschema", "mytable", "pk_col1,pk_col2");
        statement.addColumnValue("pk_col1", 1);
        statement.addColumnValue("pk_col2", 2);
        String updateStatement = ((String) (InsertOrUpdateGeneratorInformixTest.invokePrivateMethod(generator, "getUpdateStatement", new Object[]{ statement, database, null, null })));
        Assert.assertNotNull(updateStatement);
        Integer lineNumber = 0;
        String[] lines = updateStatement.split("\n");
        Assert.assertEquals("", lines[lineNumber]);
    }
}

