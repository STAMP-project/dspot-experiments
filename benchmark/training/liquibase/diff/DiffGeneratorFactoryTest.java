package liquibase.diff;


import liquibase.diff.core.StandardDiffGenerator;
import liquibase.exception.DatabaseException;
import org.junit.Assert;
import org.junit.Test;


public class DiffGeneratorFactoryTest {
    @Test
    public void getGenerator() throws DatabaseException {
        DiffGenerator generator = DiffGeneratorFactory.getInstance().getGenerator(new liquibase.database.core.H2Database(), new liquibase.database.core.H2Database());
        Assert.assertNotNull(generator);
        Assert.assertTrue((generator instanceof StandardDiffGenerator));
    }
}

