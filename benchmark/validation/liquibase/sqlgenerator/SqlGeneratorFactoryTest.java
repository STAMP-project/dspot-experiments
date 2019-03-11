package liquibase.sqlgenerator;


import java.util.Collection;
import java.util.SortedSet;
import liquibase.database.core.H2Database;
import liquibase.sqlgenerator.core.AddAutoIncrementGenerator;
import liquibase.sqlgenerator.core.AddAutoIncrementGeneratorDB2;
import liquibase.sqlgenerator.core.AddAutoIncrementGeneratorHsqlH2;
import liquibase.sqlgenerator.core.AddColumnGenerator;
import liquibase.statement.core.AddAutoIncrementStatement;
import org.junit.Assert;
import org.junit.Test;


public class SqlGeneratorFactoryTest {
    private AddAutoIncrementStatement statement;

    private H2Database database;

    private SqlGeneratorFactory factory;

    @Test
    public void getInstance() {
        Assert.assertNotNull(SqlGeneratorFactory.getInstance());
        Assert.assertTrue(((SqlGeneratorFactory.getInstance()) == (SqlGeneratorFactory.getInstance())));
    }

    @Test
    public void register() {
        factory.getGenerators().clear();
        Assert.assertEquals(0, factory.getGenerators().size());
        factory.register(new MockSqlGenerator(1, "A1"));
        Assert.assertEquals(1, factory.getGenerators().size());
    }

    @Test
    public void unregisterInstance() {
        factory.getGenerators().clear();
        Assert.assertEquals(0, factory.getGenerators().size());
        AddAutoIncrementGeneratorHsqlH2 sqlGenerator = new AddAutoIncrementGeneratorHsqlH2();
        factory.register(new AddAutoIncrementGenerator());
        factory.register(sqlGenerator);
        factory.register(new AddAutoIncrementGeneratorDB2());
        Assert.assertEquals(3, factory.getGenerators().size());
        factory.unregister(sqlGenerator);
        Assert.assertEquals(2, factory.getGenerators().size());
    }

    @Test
    public void unregisterClass() {
        factory.getGenerators().clear();
        Assert.assertEquals(0, factory.getGenerators().size());
        AddAutoIncrementGeneratorHsqlH2 sqlGenerator = new AddAutoIncrementGeneratorHsqlH2();
        factory.register(new AddAutoIncrementGenerator());
        factory.register(sqlGenerator);
        factory.register(new AddAutoIncrementGeneratorDB2());
        Assert.assertEquals(3, factory.getGenerators().size());
        factory.unregister(AddAutoIncrementGeneratorHsqlH2.class);
        Assert.assertEquals(2, factory.getGenerators().size());
    }

    @Test
    public void unregisterClassDoesNotExist() {
        factory.getGenerators().clear();
        Assert.assertEquals(0, factory.getGenerators().size());
        factory.register(new AddAutoIncrementGenerator());
        factory.register(new AddAutoIncrementGeneratorHsqlH2());
        factory.register(new AddAutoIncrementGeneratorDB2());
        Assert.assertEquals(3, factory.getGenerators().size());
        factory.unregister(AddColumnGenerator.class);
        Assert.assertEquals(3, factory.getGenerators().size());
    }

    @Test
    public void registerWithCache() {
        factory.getGenerators().clear();
        Assert.assertEquals(0, factory.getGenerators(statement, database).size());
        factory.register(new AddAutoIncrementGeneratorHsqlH2());
        Assert.assertEquals(1, factory.getGenerators(statement, database).size());
    }

    @Test
    public void unregisterInstanceWithCache() {
        factory.getGenerators().clear();
        Assert.assertEquals(0, factory.getGenerators(statement, database).size());
        AddAutoIncrementGeneratorHsqlH2 sqlGenerator = new AddAutoIncrementGeneratorHsqlH2();
        factory.register(new SqlGeneratorFactoryTest.CustomAddAutoIncrementGeneratorHsqlH2());
        factory.register(sqlGenerator);
        Assert.assertEquals(2, factory.getGenerators(statement, database).size());
        factory.unregister(sqlGenerator);
        Assert.assertEquals(1, factory.getGenerators(statement, database).size());
    }

    @Test
    public void unregisterClassWithCache() {
        factory.getGenerators().clear();
        Assert.assertEquals(0, factory.getGenerators(statement, database).size());
        SqlGeneratorFactoryTest.CustomAddAutoIncrementGeneratorHsqlH2 sqlGenerator = new SqlGeneratorFactoryTest.CustomAddAutoIncrementGeneratorHsqlH2();
        factory.register(sqlGenerator);
        factory.register(new AddAutoIncrementGeneratorHsqlH2());
        Assert.assertEquals(2, factory.getGenerators(statement, database).size());
        factory.unregister(AddAutoIncrementGeneratorHsqlH2.class);
        Assert.assertEquals(1, factory.getGenerators(statement, database).size());
    }

    @Test
    public void unregisterClassDoesNotExistWithCache() {
        factory.getGenerators().clear();
        Assert.assertEquals(0, factory.getGenerators(statement, database).size());
        factory.register(new SqlGeneratorFactoryTest.CustomAddAutoIncrementGeneratorHsqlH2());
        factory.register(new AddAutoIncrementGeneratorHsqlH2());
        Assert.assertEquals(2, factory.getGenerators(statement, database).size());
        factory.unregister(AddColumnGenerator.class);
        Assert.assertEquals(2, factory.getGenerators(statement, database).size());
    }

    @Test
    public void reset() {
        SqlGeneratorFactory.reset();
        Assert.assertFalse(((factory) == (SqlGeneratorFactory.getInstance())));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void builtInGeneratorsAreFound() {
        Collection<SqlGenerator> generators = factory.getGenerators();
        Assert.assertTrue(((generators.size()) > 10));
    }

    @Test
    public void getGenerators() {
        SortedSet<SqlGenerator> allGenerators = factory.getGenerators(statement, database);
        Assert.assertNotNull(allGenerators);
        Assert.assertEquals(1, allGenerators.size());
    }

    private class CustomAddAutoIncrementGeneratorHsqlH2 extends AddAutoIncrementGeneratorHsqlH2 {
        @Override
        public int getPriority() {
            return (super.getPriority()) + 1;
        }
    }
}

