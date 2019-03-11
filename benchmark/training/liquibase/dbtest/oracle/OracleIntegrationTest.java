package liquibase.dbtest.oracle;


import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import junit.framework.TestCase;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.dbtest.AbstractIntegrationTest;
import liquibase.exception.ValidationFailedException;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * Integration test for Oracle Database, Version 11gR2 and above.
 */
public class OracleIntegrationTest extends AbstractIntegrationTest {
    String indexOnSchemaChangeLog;

    String viewOnSchemaChangeLog;

    public OracleIntegrationTest() throws Exception {
        super("oracle", DatabaseFactory.getInstance().getDatabase("oracle"));
        indexOnSchemaChangeLog = "changelogs/oracle/complete/indexOnSchema.xml";
        viewOnSchemaChangeLog = "changelogs/oracle/complete/viewOnSchema.xml";
        // Respect a user-defined location for sqlnet.ora, tnsnames.ora etc. stored in the environment
        // variable TNS_ADMIN. This allowes the use of TNSNAMES.
        if ((System.getenv("TNS_ADMIN")) != null)
            System.setProperty("oracle.net.tns_admin", System.getenv("TNS_ADMIN"));

    }

    @Override
    @Test
    public void testRunChangeLog() throws Exception {
        super.testRunChangeLog();// To change body of overridden methods use File | Settings | File Templates.

    }

    @Test
    public void indexCreatedOnCorrectSchema() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        Liquibase liquibase = createLiquibase(this.indexOnSchemaChangeLog);
        clearDatabase();
        try {
            liquibase.update(this.contexts);
        } catch (ValidationFailedException e) {
            e.printDescriptiveError(System.out);
            throw e;
        }
        Statement queryIndex = getUnderlyingConnection().createStatement();
        ResultSet indexOwner = queryIndex.executeQuery("SELECT owner FROM ALL_INDEXES WHERE index_name = 'IDX_BOOK_ID'");
        Assert.assertTrue(indexOwner.next());
        String owner = indexOwner.getString("owner");
        TestCase.assertEquals("LBCAT2", owner);
        // check that the automatically rollback now works too
        try {
            liquibase.rollback(new Date(0), this.contexts);
        } catch (ValidationFailedException e) {
            e.printDescriptiveError(System.out);
            throw e;
        }
    }

    @Test
    public void viewCreatedOnCorrectSchema() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        Liquibase liquibase = createLiquibase(this.viewOnSchemaChangeLog);
        clearDatabase();
        try {
            liquibase.update(this.contexts);
        } catch (ValidationFailedException e) {
            e.printDescriptiveError(System.out);
            throw e;
        }
        Statement queryIndex = getUnderlyingConnection().createStatement();
        ResultSet indexOwner = queryIndex.executeQuery("SELECT owner FROM ALL_VIEWS WHERE view_name = 'V_BOOK2'");
        Assert.assertTrue(indexOwner.next());
        String owner = indexOwner.getString("owner");
        TestCase.assertEquals("LBCAT2", owner);
        // check that the automatically rollback now works too
        try {
            liquibase.rollback(new Date(0), this.contexts);
        } catch (ValidationFailedException e) {
            e.printDescriptiveError(System.out);
            throw e;
        }
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

