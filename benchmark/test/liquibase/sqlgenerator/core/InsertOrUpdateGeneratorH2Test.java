/**
 *
 */
package liquibase.sqlgenerator.core;


import liquibase.change.ColumnConfig;
import liquibase.database.Database;
import liquibase.database.core.H2Database;
import liquibase.exception.LiquibaseException;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.statement.core.InsertOrUpdateStatement;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author luciano.boschi
 */
public class InsertOrUpdateGeneratorH2Test {
    private static final String CATALOG_NAME = "mycatalog";

    private static final String SCHEMA_NAME = "myschema";

    private static final String TABLE_NAME = "MYTABLE";

    // private static final String SEQUENCE_NAME = "my_sequence";
    /**
     * Test method for {@link InsertOrUpdateGeneratorH2#getUpdateStatement(InsertOrUpdateStatement, Database, String, SqlGeneratorChain)}.
     *
     * @throws LiquibaseException
     * 		
     */
    @Test
    public void testGetUpdateStatement_notOnlyUpdate() throws LiquibaseException {
        final InsertOrUpdateGeneratorH2 generator = new InsertOrUpdateGeneratorH2();
        final InsertOrUpdateStatement insertOrUpdateStatement = new InsertOrUpdateStatement(InsertOrUpdateGeneratorH2Test.CATALOG_NAME, InsertOrUpdateGeneratorH2Test.SCHEMA_NAME, InsertOrUpdateGeneratorH2Test.TABLE_NAME, "pk1");
        final Database database = new H2Database();
        final SqlGeneratorChain sqlGeneratorChain = null;
        ColumnConfig columnConfig;
        columnConfig = new ColumnConfig();
        columnConfig.setValue("value0");
        columnConfig.setName("col0");
        insertOrUpdateStatement.addColumn(columnConfig);
        columnConfig = new ColumnConfig();
        columnConfig.setValue("keyvalue1");
        columnConfig.setName("pk1");
        insertOrUpdateStatement.addColumn(columnConfig);
        final String whereClause = generator.getWhereClause(insertOrUpdateStatement, database);
        String result = generator.getUpdateStatement(insertOrUpdateStatement, database, whereClause, sqlGeneratorChain);
        Assert.assertEquals("", result);
    }

    /**
     * Test method for {@link InsertOrUpdateGeneratorH2#getUpdateStatement(InsertOrUpdateStatement, Database, String, SqlGeneratorChain)}.
     *
     * @throws LiquibaseException
     * 		
     */
    @Test
    public void testGetUpdateStatement_onlyUpdate() throws LiquibaseException {
        final InsertOrUpdateGeneratorH2 generator = new InsertOrUpdateGeneratorH2();
        final InsertOrUpdateStatement insertOrUpdateStatement = new InsertOrUpdateStatement(InsertOrUpdateGeneratorH2Test.CATALOG_NAME, InsertOrUpdateGeneratorH2Test.SCHEMA_NAME, InsertOrUpdateGeneratorH2Test.TABLE_NAME, "pk1", true);
        final Database database = new H2Database();
        final SqlGeneratorChain sqlGeneratorChain = null;
        ColumnConfig columnConfig;
        columnConfig = new ColumnConfig();
        columnConfig.setValue("value0");
        columnConfig.setName("col0");
        insertOrUpdateStatement.addColumn(columnConfig);
        columnConfig = new ColumnConfig();
        columnConfig.setValue("keyvalue1");
        columnConfig.setName("pk1");
        insertOrUpdateStatement.addColumn(columnConfig);
        final String whereClause = generator.getWhereClause(insertOrUpdateStatement, database);
        String result = generator.getUpdateStatement(insertOrUpdateStatement, database, whereClause, sqlGeneratorChain);
        Assert.assertEquals(String.format("UPDATE %s.%s SET %s = \'%s\' WHERE %s = \'%s\';\n", InsertOrUpdateGeneratorH2Test.SCHEMA_NAME, InsertOrUpdateGeneratorH2Test.TABLE_NAME, "col0", "value0", "pk1", "keyvalue1"), result);
    }

    /**
     * Test method for {@link InsertOrUpdateGenerator#generateSql(InsertOrUpdateStatement, Database, SqlGeneratorChain)}.
     */
    @Test
    public void testGenerateSql_notOnlyUpdate() {
        final InsertOrUpdateGeneratorH2 generator = new InsertOrUpdateGeneratorH2();
        final InsertOrUpdateStatement insertOrUpdateStatement = new InsertOrUpdateStatement(InsertOrUpdateGeneratorH2Test.CATALOG_NAME, InsertOrUpdateGeneratorH2Test.SCHEMA_NAME, InsertOrUpdateGeneratorH2Test.TABLE_NAME, "pk1");
        final Database database = new H2Database();
        final SqlGeneratorChain sqlGeneratorChain = null;
        ColumnConfig columnConfig;
        columnConfig = new ColumnConfig();
        columnConfig.setValue("keyvalue1");
        columnConfig.setName("pk1");
        insertOrUpdateStatement.addColumn(columnConfig);
        columnConfig = new ColumnConfig();
        columnConfig.setValue("value0");
        columnConfig.setName("col0");
        insertOrUpdateStatement.addColumn(columnConfig);
        // final String whereClause = generator.getWhereClause(insertOrUpdateStatement, database);
        Sql[] results = generator.generateSql(insertOrUpdateStatement, database, sqlGeneratorChain);
        Assert.assertThat(results, Matchers.is(Matchers.arrayWithSize(1)));
        Assert.assertEquals(String.format("MERGE INTO %s.%s (%s, %s) KEY(%s) VALUES ('%s', '%s');", InsertOrUpdateGeneratorH2Test.SCHEMA_NAME, InsertOrUpdateGeneratorH2Test.TABLE_NAME, "pk1", "col0", "pk1", "keyvalue1", "value0"), results[0].toSql());
    }
}

