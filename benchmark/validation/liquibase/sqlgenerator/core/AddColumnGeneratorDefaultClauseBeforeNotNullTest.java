package liquibase.sqlgenerator.core;


import liquibase.exception.ValidationErrors;
import liquibase.sqlgenerator.AbstractSqlGeneratorTest;
import liquibase.sqlgenerator.MockSqlGeneratorChain;
import liquibase.statement.AutoIncrementConstraint;
import org.junit.Assert;
import org.junit.Test;


public class AddColumnGeneratorDefaultClauseBeforeNotNullTest extends AddColumnGeneratorTest {
    public AddColumnGeneratorDefaultClauseBeforeNotNullTest() throws Exception {
        super(new AddColumnGeneratorDefaultClauseBeforeNotNull());
    }

    @Test
    public void validate_noAutoIncrementWithDerby() {
        ValidationErrors validationErrors = generatorUnderTest.validate(new liquibase.statement.core.AddColumnStatement(null, null, "table_name", "column_name", "int", null, new AutoIncrementConstraint("column_name")), new DerbyDatabase(), new MockSqlGeneratorChain());
        Assert.assertTrue(validationErrors.getErrorMessages().contains("Cannot add an identity column to derby"));
    }
}

