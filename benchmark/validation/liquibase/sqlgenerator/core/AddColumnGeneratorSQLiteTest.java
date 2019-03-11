package liquibase.sqlgenerator.core;


import liquibase.database.core.MariaDBDatabase;
import liquibase.database.core.SQLiteDatabase;
import liquibase.statement.core.AddColumnStatement;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class AddColumnGeneratorSQLiteTest {
    @Test
    public void supportsSQLLite() {
        AddColumnStatement any = Mockito.mock(AddColumnStatement.class);
        Assert.assertTrue(new AddColumnGeneratorSQLite().supports(any, new SQLiteDatabase()));
    }

    @Test
    public void doesNotSupportMariaDB() {
        AddColumnStatement any = Mockito.mock(AddColumnStatement.class);
        Assert.assertFalse(new AddColumnGeneratorSQLite().supports(any, new MariaDBDatabase()));
    }
}

