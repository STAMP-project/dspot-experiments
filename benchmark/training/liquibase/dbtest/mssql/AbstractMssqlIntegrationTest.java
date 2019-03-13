package liquibase.dbtest.mssql;


import java.util.Date;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.dbtest.AbstractIntegrationTest;
import liquibase.exception.ValidationFailedException;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * Template for different Microsoft SQL integration tests (regular, case-sensitive etc.)
 *
 * @author lujop
 */
public abstract class AbstractMssqlIntegrationTest extends AbstractIntegrationTest {
    public AbstractMssqlIntegrationTest(String changelogDir, Database dbms) throws Exception {
        super(changelogDir, dbms);
    }

    @Test
    public void impossibleDefaultSchema() {
        Exception caughtException = null;
        try {
            getDatabase().setDefaultSchemaName("lbuser");
        } catch (Exception ex) {
            caughtException = ex;
        }
        Assert.assertNotNull("Must not allow using a defaultSchemaName that is different from the DB user's login schema.", caughtException);
    }

    @Test
    public void smartDataLoad() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        Liquibase liquibase = createLiquibase("changelogs/common/smartDataLoad.changelog.xml");
        clearDatabase();
        try {
            liquibase.update(this.contexts);
        } catch (ValidationFailedException e) {
            e.printDescriptiveError(System.out);
            throw e;
        }
        try {
            liquibase.rollback(new Date(0), this.contexts);
        } catch (ValidationFailedException e) {
            e.printDescriptiveError(System.out);
            throw e;
        }
    }
}

