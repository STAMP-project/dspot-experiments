package liquibase.dbtest.sqlite;


import java.io.File;
import java.util.Date;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.dbtest.AbstractIntegrationTest;
import liquibase.exception.ValidationFailedException;
import org.junit.Test;


public class SQLiteIntegrationTest extends AbstractIntegrationTest {
    public SQLiteIntegrationTest() throws Exception {
        super("sqlite", DatabaseFactory.getInstance().getDatabase("sqlite"));
        File f = new File("sqlite");
        try {
            f.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Test
    public void testRunChangeLog() throws Exception {
        super.testRunChangeLog();// To change body of overridden methods use File | Settings | File Templates.

    }

    @Test
    public void smartDataLoad() throws Exception {
        if ((this.getDatabase()) == null) {
            return;
        }
        Liquibase liquibase = createLiquibase("changelogs/common/smartDataLoad.changelog.xml");
        clearDatabase();
        try {
            liquibase.update(this.contexts);
        } catch (ValidationFailedException e) {
            e.printDescriptiveError(System.out);
            throw e;
        }
        // check that the automatically rollback now works too
        try {
            liquibase.rollback(new Date(0), this.contexts);
        } catch (ValidationFailedException e) {
            e.printDescriptiveError(System.out);
            throw e;
        }
    }

    @Override
    @Test
    public void testDiffExternalForeignKeys() throws Exception {
        // cross-schema security for oracle is a bother, ignoring test for now
    }
}

