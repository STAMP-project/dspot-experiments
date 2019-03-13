package liquibase.sqlgenerator;


import java.util.SortedSet;
import java.util.TreeSet;
import liquibase.database.core.MockDatabase;
import liquibase.exception.ValidationErrors;
import liquibase.sql.Sql;
import liquibase.statement.core.MockSqlStatement;
import org.junit.Assert;
import org.junit.Test;


public class SqlGeneratorChainTest {
    @Test
    public void generateSql_nullGenerators() {
        SqlGeneratorChain chain = new SqlGeneratorChain(null);
        Assert.assertNull(chain.generateSql(new MockSqlStatement(), new MockDatabase()));
    }

    @Test
    public void generateSql_noGenerators() {
        SortedSet<SqlGenerator> generators = new TreeSet<SqlGenerator>(new SqlGeneratorComparator());
        SqlGeneratorChain chain = new SqlGeneratorChain(generators);
        Assert.assertEquals(0, chain.generateSql(new MockSqlStatement(), new MockDatabase()).length);
    }

    @Test
    public void generateSql_oneGenerators() {
        SortedSet<SqlGenerator> generators = new TreeSet<SqlGenerator>(new SqlGeneratorComparator());
        generators.add(new MockSqlGenerator(1, "A1", "A2"));
        SqlGeneratorChain chain = new SqlGeneratorChain(generators);
        Sql[] sql = chain.generateSql(new MockSqlStatement(), new MockDatabase());
        Assert.assertEquals(2, sql.length);
        Assert.assertEquals("A1", sql[0].toSql());
        Assert.assertEquals("A2", sql[1].toSql());
    }

    @Test
    public void generateSql_twoGenerators() {
        SortedSet<SqlGenerator> generators = new TreeSet<SqlGenerator>(new SqlGeneratorComparator());
        generators.add(new MockSqlGenerator(2, "B1", "B2"));
        generators.add(new MockSqlGenerator(1, "A1", "A2"));
        SqlGeneratorChain chain = new SqlGeneratorChain(generators);
        Sql[] sql = chain.generateSql(new MockSqlStatement(), new MockDatabase());
        Assert.assertEquals(4, sql.length);
        Assert.assertEquals("B1", sql[0].toSql());
        Assert.assertEquals("B2", sql[1].toSql());
        Assert.assertEquals("A1", sql[2].toSql());
        Assert.assertEquals("A2", sql[3].toSql());
    }

    @Test
    public void generateSql_threeGenerators() {
        SortedSet<SqlGenerator> generators = new TreeSet<SqlGenerator>(new SqlGeneratorComparator());
        generators.add(new MockSqlGenerator(2, "B1", "B2"));
        generators.add(new MockSqlGenerator(1, "A1", "A2"));
        generators.add(new MockSqlGenerator(3, "C1", "C2"));
        SqlGeneratorChain chain = new SqlGeneratorChain(generators);
        Sql[] sql = chain.generateSql(new MockSqlStatement(), new MockDatabase());
        Assert.assertEquals(6, sql.length);
        Assert.assertEquals("C1", sql[0].toSql());
        Assert.assertEquals("C2", sql[1].toSql());
        Assert.assertEquals("B1", sql[2].toSql());
        Assert.assertEquals("B2", sql[3].toSql());
        Assert.assertEquals("A1", sql[4].toSql());
        Assert.assertEquals("A2", sql[5].toSql());
    }

    @Test
    public void validate_nullGenerators() {
        SqlGeneratorChain chain = new SqlGeneratorChain(null);
        ValidationErrors validationErrors = chain.validate(new MockSqlStatement(), new MockDatabase());
        Assert.assertFalse(validationErrors.hasErrors());
    }

    @Test
    public void validate_oneGenerators_noErrors() {
        SortedSet<SqlGenerator> generators = new TreeSet<SqlGenerator>(new SqlGeneratorComparator());
        generators.add(new MockSqlGenerator(1, "A1", "A2"));
        SqlGeneratorChain chain = new SqlGeneratorChain(generators);
        ValidationErrors validationErrors = chain.validate(new MockSqlStatement(), new MockDatabase());
        Assert.assertFalse(validationErrors.hasErrors());
    }

    @Test
    public void validate_oneGenerators_hasErrors() {
        SortedSet<SqlGenerator> generators = new TreeSet<SqlGenerator>(new SqlGeneratorComparator());
        generators.add(new MockSqlGenerator(1, "A1", "A2").addValidationError("E1"));
        SqlGeneratorChain chain = new SqlGeneratorChain(generators);
        ValidationErrors validationErrors = chain.validate(new MockSqlStatement(), new MockDatabase());
        Assert.assertTrue(validationErrors.hasErrors());
    }

    @Test
    public void validate_twoGenerators_noErrors() {
        SortedSet<SqlGenerator> generators = new TreeSet<SqlGenerator>(new SqlGeneratorComparator());
        generators.add(new MockSqlGenerator(2, "B1", "B2"));
        generators.add(new MockSqlGenerator(1, "A1", "A2"));
        SqlGeneratorChain chain = new SqlGeneratorChain(generators);
        ValidationErrors validationErrors = chain.validate(new MockSqlStatement(), new MockDatabase());
        Assert.assertFalse(validationErrors.hasErrors());
    }

    @Test
    public void validate_twoGenerators_firstHasErrors() {
        SortedSet<SqlGenerator> generators = new TreeSet<SqlGenerator>(new SqlGeneratorComparator());
        generators.add(new MockSqlGenerator(2, "B1", "B2").addValidationError("E1"));
        generators.add(new MockSqlGenerator(1, "A1", "A2"));
        SqlGeneratorChain chain = new SqlGeneratorChain(generators);
        ValidationErrors validationErrors = chain.validate(new MockSqlStatement(), new MockDatabase());
        Assert.assertTrue(validationErrors.hasErrors());
    }

    @Test
    public void validate_twoGenerators_secondHasErrors() {
        SortedSet<SqlGenerator> generators = new TreeSet<SqlGenerator>(new SqlGeneratorComparator());
        generators.add(new MockSqlGenerator(2, "B1", "B2"));
        generators.add(new MockSqlGenerator(1, "A1", "A2").addValidationError("E1"));
        SqlGeneratorChain chain = new SqlGeneratorChain(generators);
        ValidationErrors validationErrors = chain.validate(new MockSqlStatement(), new MockDatabase());
        Assert.assertTrue(validationErrors.hasErrors());
    }
}

