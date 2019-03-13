package liquibase.sqlgenerator.core;


import liquibase.change.ColumnConfig;
import liquibase.database.core.PostgresDatabase;
import liquibase.sql.Sql;
import liquibase.statement.SequenceNextValueFunction;
import liquibase.statement.core.InsertStatement;
import org.junit.Assert;
import org.junit.Test;


public class InsertOrUpdateGeneratorPostgresTest {
    private static final String CATALOG_NAME = "mycatalog";

    private static final String SCHEMA_NAME = "myschema";

    private static final String TABLE_NAME = "mytable";

    private static final String SEQUENCE_NAME = "my_sequence";

    @Test
    public void testInsertSequenceValWithSchema() {
        PostgresDatabase postgresDatabase = new PostgresDatabase();
        InsertGenerator generator = new InsertGenerator();
        InsertStatement statement = new InsertStatement(InsertOrUpdateGeneratorPostgresTest.CATALOG_NAME, InsertOrUpdateGeneratorPostgresTest.SCHEMA_NAME, InsertOrUpdateGeneratorPostgresTest.TABLE_NAME);
        ColumnConfig columnConfig = new ColumnConfig();
        columnConfig.setValueSequenceNext(new SequenceNextValueFunction(InsertOrUpdateGeneratorPostgresTest.SEQUENCE_NAME, InsertOrUpdateGeneratorPostgresTest.SCHEMA_NAME));
        columnConfig.setName("col3");
        statement.addColumn(columnConfig);
        Sql[] sql = generator.generateSql(statement, postgresDatabase, null);
        String theSql = sql[0].toSql();
        Assert.assertEquals(String.format("INSERT INTO %s.%s (col3) VALUES (nextval('%s.%s'))", InsertOrUpdateGeneratorPostgresTest.SCHEMA_NAME, InsertOrUpdateGeneratorPostgresTest.TABLE_NAME, InsertOrUpdateGeneratorPostgresTest.SCHEMA_NAME, InsertOrUpdateGeneratorPostgresTest.SEQUENCE_NAME), theSql);
    }
}

