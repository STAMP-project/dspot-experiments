package liquibase.dbtest;


import LogType.LOG;
import LogType.WRITE_SQL;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.core.OracleDatabase;
import liquibase.datatype.DataTypeFactory;
import liquibase.diff.DiffGeneratorFactory;
import liquibase.diff.DiffResult;
import liquibase.diff.compare.CompareControl;
import liquibase.diff.output.DiffOutputControl;
import liquibase.diff.output.changelog.DiffToChangeLog;
import liquibase.exception.ChangeLogParseException;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.exception.ValidationFailedException;
import liquibase.executor.Executor;
import liquibase.executor.ExecutorService;
import liquibase.lockservice.LockService;
import liquibase.lockservice.LockServiceFactory;
import liquibase.logging.LogService;
import liquibase.logging.Logger;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.SnapshotControl;
import liquibase.snapshot.SnapshotGeneratorFactory;
import liquibase.test.DatabaseTestContext;
import liquibase.util.RegexMatcher;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static CatalogAndSchema.DEFAULT;


/**
 * Base class for all database integration tests.  There is an AbstractIntegrationTest subclass for each supported database.
 * The database is assumed to exist at the host returned by getDatabaseServerHostname.  Currently this is hardcoded to an integration test server
 * at liquibase world headquarters.  Feel free to change the return value, but don't check it in.  We are going to improve the config of this at some point.
 */
public abstract class AbstractIntegrationTest {
    @Rule
    public TemporaryFolder tempDirectory = new TemporaryFolder();

    protected String completeChangeLog;

    protected String contexts = "test, context-b";

    protected String username;

    protected String password;

    Set<String> emptySchemas = new TreeSet<>();

    Logger logger;

    private String rollbackChangeLog;

    private String includedChangeLog;

    private String encodingChangeLog;

    private String externalfkInitChangeLog;

    private String externalEntityChangeLog;

    private String externalEntityChangeLog2;

    private String invalidReferenceChangeLog;

    private String objectQuotingStrategyChangeLog;

    private Database database;

    private String jdbcUrl;

    private String defaultSchemaName;

    protected AbstractIntegrationTest(String changelogDir, Database dbms) throws Exception {
        this.completeChangeLog = ("changelogs/" + changelogDir) + "/complete/root.changelog.xml";
        this.rollbackChangeLog = ("changelogs/" + changelogDir) + "/rollback/rollbackable.changelog.xml";
        this.includedChangeLog = ("changelogs/" + changelogDir) + "/complete/included.changelog.xml";
        this.encodingChangeLog = "changelogs/common/encoding.changelog.xml";
        this.externalfkInitChangeLog = "changelogs/common/externalfk.init.changelog.xml";
        this.externalEntityChangeLog = "changelogs/common/externalEntity.changelog.xml";
        this.externalEntityChangeLog2 = "com/example/nonIncluded/externalEntity.changelog.xml";
        this.invalidReferenceChangeLog = "changelogs/common/invalid.reference.changelog.xml";
        this.objectQuotingStrategyChangeLog = "changelogs/common/object.quoting.strategy.changelog.xml";
        logger = LogService.getLog(getClass());
        // Get the integration test properties for both global settings and (if applicable) local overrides.
        Properties integrationTestProperties;
        integrationTestProperties = new Properties();
        integrationTestProperties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("liquibase/liquibase.integrationtest.properties"));
        InputStream localProperties = Thread.currentThread().getContextClassLoader().getResourceAsStream("liquibase/liquibase.integrationtest.local.properties");
        if (localProperties != null)
            integrationTestProperties.load(localProperties);

        // Login username
        String username = integrationTestProperties.getProperty((("integration.test." + (dbms.getShortName())) + ".username"));
        if (username == null)
            username = integrationTestProperties.getProperty("integration.test.username");

        this.setUsername(username);
        // Login password
        String password = integrationTestProperties.getProperty((("integration.test." + (dbms.getShortName())) + ".password"));
        if (password == null)
            password = integrationTestProperties.getProperty("integration.test.password");

        this.setPassword(password);
        // JDBC URL (no global default so all databases!)
        String url = integrationTestProperties.getProperty((("integration.test." + (dbms.getShortName())) + ".url"));
        if ((url == null) || ((url.length()) == 0)) {
            throw new LiquibaseException(("No JDBC URL found for integration test of database type " + (dbms.getShortName())));
        }
        this.setJdbcUrl(url);
        Scope.setScopeManager(new TestScopeManager());
    }

    // Successful execution qualifies as test success.
    @Test
    @SuppressWarnings("squid:S2699")
    public void testBatchInsert() throws Exception {
        if ((this.getDatabase()) == null) {
            return;
        }
        clearDatabase();
        createLiquibase("changelogs/common/batchInsert.changelog.xml").update(this.contexts);
        // ChangeLog already contains the verification code
    }

    @Test
    public void testDatabaseIsReachableIfRequired() {
        if (isDatabaseProvidedByTravisCI()) {
            Assert.assertNotNull(("This integration test is expected to pass on Travis CI.\n" + ((("If you are running on a dev machine and do not have the required\n" + "database installed, you may choose to ignore this failed test.\n") + "To run this test on a dev machine, you will need to install the corresponding\n") + "database and configure liquibase.integrationtest.local.properties")), getDatabase());
        } else {
            Assume.assumeNotNull(this.getDatabase());
        }
    }

    // Successful execution qualifies as test success.
    @Test
    @SuppressWarnings("squid:S2699")
    public void testRunChangeLog() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        runCompleteChangeLog();
    }

    // Successful execution qualifies as test success.
    @Test
    @SuppressWarnings("squid:S2699")
    public void testRunUpdateOnOldChangelogTableFormat() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        Liquibase liquibase = createLiquibase(completeChangeLog);
        clearDatabase();
        String nullableKeyword = (database.requiresExplicitNullForColumns()) ? " NULL" : "";
        String sql = (((((((((((((((((((((("CREATE TABLE " + (database.escapeTableName(database.getDefaultCatalogName(), database.getDefaultSchemaName(), "DATABASECHANGELOG"))) + " (id varchar(150) NOT NULL, ") + "author VARCHAR(150) NOT NULL, ") + "filename VARCHAR(255) NOT NULL, ") + "dateExecuted ") + (DataTypeFactory.getInstance().fromDescription("datetime", database).toDatabaseDataType(database))) + " NOT NULL, ") + "md5sum VARCHAR(32)") + nullableKeyword) + ", ") + "description VARCHAR(255)") + nullableKeyword) + ", ") + "comments VARCHAR(255)") + nullableKeyword) + ", ") + "tag VARCHAR(255)") + nullableKeyword) + ", ") + "liquibase VARCHAR(10)") + nullableKeyword) + ", ") + "PRIMARY KEY (id, author, filename))";
        LogService.getLog(getClass()).info(WRITE_SQL, sql);
        Connection conn = getUnderlyingConnection();
        boolean savedAcSetting = conn.getAutoCommit();
        conn.setAutoCommit(false);
        conn.createStatement().execute(sql);
        conn.commit();
        conn.setAutoCommit(savedAcSetting);
        liquibase = createLiquibase(completeChangeLog);
        liquibase.setChangeLogParameter("loginuser", getUsername());
        liquibase.update(this.contexts);
    }

    @Test
    public void testOutputChangeLog() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        StringWriter output = new StringWriter();
        Liquibase liquibase;
        clearDatabase();
        liquibase = createLiquibase(completeChangeLog);
        liquibase.setChangeLogParameter("loginuser", getUsername());
        liquibase.update(this.contexts, output);
        String outputResult = output.getBuffer().toString();
        Assert.assertNotNull("generated output change log must not be empty", outputResult);
        Assert.assertTrue("generated output change log is at least 100 bytes long", ((outputResult.length()) > 100));
        // TODO should better written to a file so CI servers can pick it up as test artifacts.
        System.out.println(outputResult);
        Assert.assertTrue(("create databasechangelog command not found in: \n" + outputResult), outputResult.contains(("CREATE TABLE " + (database.escapeTableName(database.getLiquibaseCatalogName(), database.getLiquibaseSchemaName(), database.getDatabaseChangeLogTableName())))));
        Assert.assertTrue(("create databasechangeloglock command not found in: \n" + outputResult), outputResult.contains(("CREATE TABLE " + (database.escapeTableName(database.getLiquibaseCatalogName(), database.getLiquibaseSchemaName(), database.getDatabaseChangeLogLockTableName())))));
        Assert.assertTrue("generated output contains a correctly encoded Euro sign", outputResult.contains("?"));
        DatabaseSnapshot snapshot = SnapshotGeneratorFactory.getInstance().createSnapshot(database.getDefaultSchema(), database, new SnapshotControl(database));
        Assert.assertEquals("no database objects were actually created during creation of the output changelog", 0, snapshot.get(Schema.class).iterator().next().getDatabaseObjects(Table.class).size());
    }

    // Successful execution qualifies as test success.
    @Test
    @SuppressWarnings("squid:S2699")
    public void testUpdateTwice() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        Liquibase liquibase = createLiquibase(completeChangeLog);
        clearDatabase();
        liquibase = createLiquibase(completeChangeLog);
        liquibase.setChangeLogParameter("loginuser", getUsername());
        liquibase.update(this.contexts);
        liquibase.update(this.contexts);
    }

    // Successful execution qualifies as test success.
    @Test
    @SuppressWarnings("squid:S2699")
    public void testUpdateClearUpdate() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        Liquibase liquibase = createLiquibase(completeChangeLog);
        clearDatabase();
        liquibase = createLiquibase(completeChangeLog);
        liquibase.setChangeLogParameter("loginuser", getUsername());
        liquibase.update(this.contexts);
        clearDatabase();
        liquibase = createLiquibase(completeChangeLog);
        liquibase.setChangeLogParameter("loginuser", getUsername());
        liquibase.update(this.contexts);
    }

    // Successful execution qualifies as test success.
    @Test
    @SuppressWarnings("squid:S2699")
    public void testRollbackableChangeLog() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        Liquibase liquibase = createLiquibase(rollbackChangeLog);
        clearDatabase();
        liquibase = createLiquibase(rollbackChangeLog);
        liquibase.update(this.contexts);
        liquibase = createLiquibase(rollbackChangeLog);
        liquibase.rollback(new Date(0), this.contexts);
        liquibase = createLiquibase(rollbackChangeLog);
        liquibase.update(this.contexts);
        liquibase = createLiquibase(rollbackChangeLog);
        liquibase.rollback(new Date(0), this.contexts);
    }

    // Successful execution qualifies as test success.
    @Test
    @SuppressWarnings("squid:S2699")
    public void testRollbackableChangeLogScriptOnExistingDatabase() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        Liquibase liquibase = createLiquibase(rollbackChangeLog);
        clearDatabase();
        liquibase = createLiquibase(rollbackChangeLog);
        liquibase.update(this.contexts);
        StringWriter writer = new StringWriter();
        liquibase = createLiquibase(rollbackChangeLog);
        liquibase.rollback(new Date(0), this.contexts, writer);
    }

    // Successful execution qualifies as test success.
    @Test
    @SuppressWarnings("squid:S2699")
    public void testRollbackableChangeLogScriptOnFutureDatabase() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        StringWriter writer = new StringWriter();
        Liquibase liquibase = createLiquibase(rollbackChangeLog);
        clearDatabase();
        liquibase = createLiquibase(rollbackChangeLog);
        liquibase.futureRollbackSQL(new Contexts(this.contexts), new LabelExpression(), writer);
    }

    // Successful execution qualifies as test success.
    @Test
    @SuppressWarnings("squid:S2699")
    public void testTag() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        Liquibase liquibase = createLiquibase(completeChangeLog);
        clearDatabase();
        liquibase = createLiquibase(completeChangeLog);
        liquibase.setChangeLogParameter("loginuser", getUsername());
        liquibase.update(this.contexts);
        liquibase.tag("Test Tag");
    }

    @Test
    public void testDiff() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        runCompleteChangeLog();
        CompareControl compareControl = new CompareControl();
        compareControl.addSuppressedField(Column.class, "defaultValue");// database returns different data even if the same

        compareControl.addSuppressedField(Column.class, "autoIncrementInformation");// database returns different data even if the same

        DiffResult diffResult = DiffGeneratorFactory.getInstance().compare(database, database, compareControl);
        try {
            Assert.assertTrue("comapring a database with itself should return a result of 'DBs are equal'", diffResult.areEqual());
        } catch (AssertionError e) {
            print();
            throw e;
        }
    }

    @Test
    public void testRerunDiffChangeLog() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        for (int run = 0; run < 2; run++) {
            // run once outputting data as insert, once as csv
            boolean outputCsv = run == 1;
            runCompleteChangeLog();
            SnapshotControl snapshotControl = new SnapshotControl(database);
            DatabaseSnapshot originalSnapshot = SnapshotGeneratorFactory.getInstance().createSnapshot(database.getDefaultSchema(), database, snapshotControl);
            CompareControl compareControl = new CompareControl();
            compareControl.addSuppressedField(Column.class, "defaultValue");// database returns different data even if the same

            compareControl.addSuppressedField(Column.class, "autoIncrementInformation");// database returns different data even if the same

            if ((database) instanceof OracleDatabase) {
                compareControl.addSuppressedField(Column.class, "type");// database returns different nvarchar2 info even though they are the same

                compareControl.addSuppressedField(Column.class, "nullable");// database returns different nullable on views, e.g. v_person.id

            }
            DiffOutputControl diffOutputControl = new DiffOutputControl();
            File tempFile = tempDirectory.getRoot().createTempFile("liquibase-test", ".xml");
            if (outputCsv) {
                diffOutputControl.setDataDir(new File(tempFile.getParentFile(), "liquibase-data").getCanonicalPath().replaceFirst("\\w:", ""));
            }
            DiffResult diffResult = DiffGeneratorFactory.getInstance().compare(database, null, compareControl);
            FileOutputStream output = new FileOutputStream(tempFile);
            try {
                new DiffToChangeLog(diffResult, new DiffOutputControl()).print(new PrintStream(output));
                output.flush();
            } finally {
                output.close();
            }
            Liquibase liquibase = createLiquibase(tempFile.getName());
            clearDatabase();
            DatabaseSnapshot emptySnapshot = SnapshotGeneratorFactory.getInstance().createSnapshot(database.getDefaultSchema(), database, new SnapshotControl(database));
            // run again to test changelog testing logic
            liquibase = createLiquibase(tempFile.getName());
            try {
                liquibase.update(this.contexts);
            } catch (ValidationFailedException e) {
                e.printDescriptiveError(System.out);
                throw e;
            }
            DatabaseSnapshot migratedSnapshot = SnapshotGeneratorFactory.getInstance().createSnapshot(database.getDefaultSchema(), database, new SnapshotControl(database));
            DiffResult finalDiffResult = DiffGeneratorFactory.getInstance().compare(originalSnapshot, migratedSnapshot, compareControl);
            try {
                Assert.assertTrue(("recreating the database from the generated change log should cause both 'before' and " + "'after' snapshots to be equal."), finalDiffResult.areEqual());
            } catch (AssertionError e) {
                print();
                throw e;
            }
            // diff to empty and drop all
            DiffResult emptyDiffResult = DiffGeneratorFactory.getInstance().compare(emptySnapshot, migratedSnapshot, compareControl);
            output = new FileOutputStream(tempFile);
            try {
                new DiffToChangeLog(emptyDiffResult, new DiffOutputControl(true, true, true, null)).print(new PrintStream(output));
                output.flush();
            } finally {
                output.close();
            }
            liquibase = createLiquibase(tempFile.getName());
            LogService.getLog(getClass()).info(LOG, ("updating from " + (tempFile.getCanonicalPath())));
            try {
                liquibase.update(this.contexts);
            } catch (LiquibaseException e) {
                throw e;
            }
            DatabaseSnapshot emptyAgainSnapshot = SnapshotGeneratorFactory.getInstance().createSnapshot(database.getDefaultSchema(), database, new SnapshotControl(database));
            Assert.assertEquals(("a database that was 'updated' to an empty snapshot should only have 2 tables left: " + "the database change log table and the lock table."), 2, emptyAgainSnapshot.get(Table.class).size());
            Assert.assertEquals("a database that was 'updated' to an empty snapshot should not contain any views.", 0, emptyAgainSnapshot.get(View.class).size());
        }
    }

    @Test
    public void testRerunDiffChangeLogAltSchema() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        if (database.getShortName().equalsIgnoreCase("mssql")) {
            return;// not possible on MSSQL.

        }
        if (!(database.supportsSchemas())) {
            return;
        }
        Liquibase liquibase = createLiquibase(includedChangeLog);
        database.setDefaultSchemaName("lbcat2");
        clearDatabase();
        LockService lockService = LockServiceFactory.getInstance().getLockService(database);
        lockService.forceReleaseLock();
        liquibase.update(includedChangeLog);
        DatabaseSnapshot originalSnapshot = SnapshotGeneratorFactory.getInstance().createSnapshot(database.getDefaultSchema(), database, new SnapshotControl(database));
        CompareControl compareControl = new CompareControl(new CompareControl.SchemaComparison[]{ new CompareControl.SchemaComparison(DEFAULT, new CatalogAndSchema(null, "lbcat2")) }, originalSnapshot.getSnapshotControl().getTypesToInclude());
        DiffResult diffResult = DiffGeneratorFactory.getInstance().compare(database, null, compareControl);
        File tempFile = File.createTempFile("liquibase-test", ".xml");
        FileOutputStream output = new FileOutputStream(tempFile);
        try {
            new DiffToChangeLog(diffResult, new DiffOutputControl()).print(new PrintStream(output));
            output.flush();
        } finally {
            output.close();
        }
        liquibase = createLiquibase(tempFile.getName());
        clearDatabase();
        // run again to test changelog testing logic
        Executor executor = ExecutorService.getInstance().getExecutor(database);
        try {
            executor.execute(new liquibase.statement.core.DropTableStatement("lbcat2", "lbcat2", database.getDatabaseChangeLogTableName(), false));
        } catch (DatabaseException e) {
            // ok
        }
        try {
            executor.execute(new liquibase.statement.core.DropTableStatement("lbcat2", "lbcat2", database.getDatabaseChangeLogLockTableName(), false));
        } catch (DatabaseException e) {
            // ok
        }
        database.commit();
        DatabaseConnection connection = DatabaseTestContext.getInstance().getConnection(getJdbcUrl(), username, password);
        database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);
        database.setDefaultSchemaName("lbcat2");
        liquibase = createLiquibase(tempFile.getName());
        try {
            liquibase.update(this.contexts);
        } catch (ValidationFailedException e) {
            e.printDescriptiveError(System.out);
            throw e;
        }
        tempFile.deleteOnExit();
        DatabaseSnapshot finalSnapshot = SnapshotGeneratorFactory.getInstance().createSnapshot(database.getDefaultSchema(), database, new SnapshotControl(database));
        CompareControl finalCompareControl = new CompareControl();
        finalCompareControl.addSuppressedField(Column.class, "autoIncrementInformation");
        DiffResult finalDiffResult = DiffGeneratorFactory.getInstance().compare(originalSnapshot, finalSnapshot, finalCompareControl);
        print();
        Assert.assertTrue(("running the same change log two times against an alternative schema should produce " + "equal snapshots."), finalDiffResult.areEqual());
    }

    // Successful execution qualifies as test success.
    @Test
    @SuppressWarnings("squid:S2699")
    public void testClearChecksums() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        Liquibase liquibase = createLiquibase(completeChangeLog);
        clearDatabase();
        liquibase = createLiquibase(completeChangeLog);
        clearDatabase();
        liquibase = createLiquibase(completeChangeLog);
        liquibase.setChangeLogParameter("loginuser", getUsername());
        liquibase.update(this.contexts);
        liquibase.clearCheckSums();
    }

    // Successful execution qualifies as test success.
    @Test
    @SuppressWarnings("squid:S2699")
    public void testTagEmptyDatabase() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        Liquibase liquibase = createLiquibase(completeChangeLog);
        clearDatabase();
        liquibase = createLiquibase(completeChangeLog);
        liquibase.checkLiquibaseTables(false, null, new Contexts(), new LabelExpression());
        liquibase.tag("empty");
        liquibase = createLiquibase(rollbackChangeLog);
        liquibase.update(new Contexts());
        liquibase.rollback("empty", new Contexts());
    }

    @Test
    public void testUnrunChangeSetsEmptyDatabase() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        Liquibase liquibase = createLiquibase(completeChangeLog);
        liquibase.setChangeLogParameter("loginuser", getUsername());
        clearDatabase();
        liquibase = createLiquibase(completeChangeLog);
        liquibase.setChangeLogParameter("loginuser", getUsername());
        List<ChangeSet> list = liquibase.listUnrunChangeSets(new Contexts(this.contexts), new LabelExpression());
        Assert.assertTrue("querying the changelog table on an empty target should return at least 1 un-run change set", (!(list.isEmpty())));
    }

    // Successful execution qualifies as test success.
    @Test
    @SuppressWarnings("squid:S2699")
    public void testAbsolutePathChangeLog() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        Set<String> urls = new JUnitResourceAccessor().list(null, includedChangeLog, true, false, true);
        String absolutePathOfChangeLog = urls.iterator().next();
        absolutePathOfChangeLog = absolutePathOfChangeLog.replaceFirst("file:\\/", "");
        if (System.getProperty("os.name").startsWith("Windows ")) {
            absolutePathOfChangeLog = absolutePathOfChangeLog.replace('/', '\\');
        } else {
            absolutePathOfChangeLog = "/" + absolutePathOfChangeLog;
        }
        Liquibase liquibase = createLiquibase(absolutePathOfChangeLog, new FileSystemResourceAccessor());
        clearDatabase();
        liquibase.update(this.contexts);
        liquibase.update(this.contexts);// try again, make sure there are no errors

        clearDatabase();
    }

    // Successful execution qualifies as test success.
    @Test
    @SuppressWarnings("squid:S2699")
    public void testRollbackToChange() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        Liquibase liquibase = createLiquibase(rollbackChangeLog);
        wipeDatabase();
        liquibase = createLiquibase(rollbackChangeLog);
        liquibase.update(this.contexts);
        liquibase = createLiquibase(rollbackChangeLog);
        liquibase.rollback(8, this.contexts);
        liquibase.tag("testRollbackToChange");
        liquibase = createLiquibase(rollbackChangeLog);
        liquibase.update(this.contexts);
        liquibase = createLiquibase(rollbackChangeLog);
        liquibase.rollback("testRollbackToChange", this.contexts);
    }

    // Successful execution qualifies as test success.
    @Test
    @SuppressWarnings("squid:S2699")
    public void testDbDoc() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        Liquibase liquibase;
        clearDatabase();
        liquibase = createLiquibase(completeChangeLog);
        liquibase.setChangeLogParameter("loginuser", getUsername());
        liquibase.update(this.contexts);
        Path outputDir = tempDirectory.newFolder().toPath().normalize();
        logger.debug(LOG, ("Database documentation will be written to this temporary folder: " + outputDir));
        liquibase = createLiquibase(completeChangeLog);
        liquibase.setChangeLogParameter("loginuser", getUsername());
        liquibase.generateDocumentation(outputDir.toAbsolutePath().toString(), this.contexts);
    }

    /**
     * Create an SQL script from a change set which inserts data from CSV files. The first CSV file is encoded in
     * UTF-8, the second is encoded in Latin-1. The test is successful if the CSV data is converted into correct
     * INSERT INTO statements in the final generated SQL file.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEncodingUpdating2SQL() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        Liquibase liquibase = createLiquibase(encodingChangeLog);
        StringWriter writer = new StringWriter();
        liquibase.update(this.contexts, writer);
        Assert.assertTrue("Update to SQL preserves encoding", new RegexMatcher(writer.toString(), new String[]{ // For the UTF-8 encoded cvs
        "^.*INSERT.*VALUES.*\u00e0\u00e8\u00ec\u00f2\u00f9\u00e1\u00e9\u00ed\u00f3\u00fa\u00c0\u00c8\u00cc\u00d2\u00d9\u00c1\u00c9\u00cd\u00d3\u00da\u00e2\u00ea\u00ee\u00f4\u00fb\u00e4\u00eb\u00ef\u00f6\u00fc.*?\\)", "???", // For the latin1 one
        "^.*INSERT.*VALUES.*\u00e0\u00e8\u00ec\u00f2\u00f9\u00e1\u00e9\u00ed\u00f3\u00fa\u00c0\u00c8\u00cc\u00d2\u00d9\u00c1\u00c9\u00cd\u00d3\u00da\u00e2\u00ea\u00ee\u00f4\u00fb\u00e4\u00eb\u00ef\u00f6\u00fc.*?\\)", "???" }).allMatchedInSequentialOrder());
    }

    /**
     * Test that diff is capable to detect foreign keys to external schemas that doesn't appear in the changelog
     */
    // Successful execution qualifies as test success.
    @Test
    @SuppressWarnings("squid:S2699")
    public void testDiffExternalForeignKeys() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        clearDatabase();
        Liquibase liquibase = createLiquibase(externalfkInitChangeLog);
        liquibase.update(contexts);
        DiffResult diffResult = liquibase.diff(database, null, new CompareControl());
        DiffResultAssert.assertThat(diffResult).containsMissingForeignKeyWithName("fk_person_country");
    }

    @Test
    public void testInvalidIncludeDoesntBreakLiquibase() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        Liquibase liquibase = createLiquibase(invalidReferenceChangeLog);
        try {
            liquibase.update(new Contexts());
            Assert.fail("Did not fail with invalid include");
        } catch (ChangeLogParseException ignored) {
            // expected
        }
        LockService lockService = LockServiceFactory.getInstance().getLockService(database);
        Assert.assertFalse(lockService.hasChangeLogLock());
    }

    @Test
    public void testContextsWithHyphensWorkInFormattedSql() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        Liquibase liquibase = createLiquibase("changelogs/common/sqlstyle/formatted.changelog.sql");
        liquibase.update("hyphen-context-using-sql,camelCaseContextUsingSql");
        SnapshotGeneratorFactory tableSnapshotGenerator = SnapshotGeneratorFactory.getInstance();
        Assert.assertNotNull(tableSnapshotGenerator.has(new Table().setName("hyphen_context"), database));
        Assert.assertNotNull(tableSnapshotGenerator.has(new Table().setName("camel_context"), database));
        Assert.assertNotNull(tableSnapshotGenerator.has(new Table().setName("bar_id"), database));
        Assert.assertNotNull(tableSnapshotGenerator.has(new Table().setName("foo_id"), database));
    }

    // Successful execution qualifies as test success.
    @Test
    @SuppressWarnings("squid:S2699")
    public void testObjectQuotingStrategy() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        if (!(Arrays.asList("oracle,h2,hsqldb,postgresql,mysql").contains(database.getShortName()))) {
            return;
        }
        Liquibase liquibase = createLiquibase(objectQuotingStrategyChangeLog);
        clearDatabase();
        liquibase.update(contexts);
        clearDatabase();
    }

    @Test
    public void testOutputChangeLogIgnoringSchema() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        String schemaName = getDatabase().getDefaultSchemaName();
        if (schemaName == null) {
            return;
        }
        getDatabase().setOutputDefaultSchema(false);
        getDatabase().setOutputDefaultCatalog(false);
        StringWriter output = new StringWriter();
        Liquibase liquibase = createLiquibase(includedChangeLog);
        clearDatabase();
        liquibase = createLiquibase(includedChangeLog);
        liquibase.update(contexts, output);
        String outputResult = output.getBuffer().toString();
        Assert.assertNotNull("generated SQL may not be empty", outputResult);
        Assert.assertTrue("Expect at least 100 bytes of output in generated SQL", ((outputResult.length()) > 100));
        CharSequence expected = "CREATE TABLE " + (getDatabase().escapeTableName(getDatabase().getLiquibaseCatalogName(), getDatabase().getLiquibaseSchemaName(), getDatabase().getDatabaseChangeLogTableName()));
        Assert.assertTrue(("create databasechangelog command not found in: \n" + outputResult), outputResult.contains(expected));
        Assert.assertTrue(("create databasechangeloglock command not found in: \n" + outputResult), outputResult.contains(expected));
        Assert.assertFalse(((("the schema name '" + schemaName) + "\' should be ignored\n\n") + outputResult), outputResult.contains((schemaName + ".")));
    }

    @Test
    public void testGenerateChangeLogWithNoChanges() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        runCompleteChangeLog();
        DiffResult diffResult = DiffGeneratorFactory.getInstance().compare(database, database, new CompareControl());
        DiffToChangeLog changeLogWriter = new DiffToChangeLog(diffResult, new DiffOutputControl(false, false, false, null));
        List<ChangeSet> changeSets = changeLogWriter.generateChangeSets();
        Assert.assertEquals(("generating two change logs without any changes in between should result in an empty generated " + "differential change set."), 0, changeSets.size());
    }
}

