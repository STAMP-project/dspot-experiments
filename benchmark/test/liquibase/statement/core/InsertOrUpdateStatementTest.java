package liquibase.statement.core;


import org.junit.Assert;
import org.junit.Test;


public class InsertOrUpdateStatementTest extends InsertStatementTest {
    @Test
    public void setPrimaryKey() {
        String primaryKey = "PRIMARYKEY";
        InsertOrUpdateStatement statement = new InsertOrUpdateStatement("CATALOG", "SCHEMA", "TABLE", primaryKey);
        Assert.assertEquals(primaryKey, statement.getPrimaryKey());
        Assert.assertEquals(Boolean.FALSE, statement.getOnlyUpdate());
    }

    @Test
    public void setOnlyUpdate() {
        String primaryKey = "PRIMARYKEY";
        InsertOrUpdateStatement statement = new InsertOrUpdateStatement("CATALOG", "SCHEMA", "TABLE", primaryKey, true);
        Assert.assertEquals(Boolean.TRUE, statement.getOnlyUpdate());
    }

    @Test
    public void setOnlyUpdateToNull() {
        String primaryKey = "PRIMARYKEY";
        InsertOrUpdateStatement statement = new InsertOrUpdateStatement("CATALOG", "SCHEMA", "TABLE", primaryKey);
        statement.setOnlyUpdate(null);
        Assert.assertEquals(Boolean.FALSE, statement.getOnlyUpdate());
    }
}

