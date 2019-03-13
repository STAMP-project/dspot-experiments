package liquibase;


import ObjectQuotingStrategy.LEGACY;
import java.util.HashMap;
import java.util.Map;
import liquibase.changelog.ChangeLogIterator;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.changelog.filter.ContextChangeSetFilter;
import liquibase.changelog.filter.DbmsChangeSetFilter;
import liquibase.changelog.filter.IgnoreChangeSetFilter;
import liquibase.changelog.filter.LabelChangeSetFilter;
import liquibase.changelog.filter.ShouldRunChangeSetFilter;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.core.MockDatabase;
import liquibase.exception.ChangeLogParseException;
import liquibase.exception.LiquibaseException;
import liquibase.exception.LockException;
import liquibase.lockservice.LockService;
import liquibase.lockservice.LockServiceFactory;
import liquibase.logging.Logger;
import liquibase.parser.ChangeLogParser;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.sdk.resource.MockResourceAccessor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static liquibase.test.Assert.assertListsEqual;


public class LiquibaseTest {
    private MockResourceAccessor mockResourceAccessor;

    private Database mockDatabase;

    private LockServiceFactory mockLockServiceFactory;

    private LockService mockLockService;

    private ChangeLogParserFactory mockChangeLogParserFactory;

    private ChangeLogParser mockChangeLogParser;

    private DatabaseChangeLog mockChangeLog;

    private ChangeLogIterator mockChangeLogIterator;

    private Logger mockLogger;

    @Test
    public void testConstructor() throws Exception {
        MockResourceAccessor resourceAccessor = this.mockResourceAccessor;
        MockDatabase database = new MockDatabase();
        Liquibase liquibase = new Liquibase("com/example/test.xml", resourceAccessor, database);
        Assert.assertNotNull("change log object may not be null", liquibase.getLog());
        Assert.assertEquals("correct name of the change log file is returned", "com/example/test.xml", liquibase.getChangeLogFile());
        Assert.assertSame("ressourceAccessor property is set as requested", resourceAccessor, liquibase.getResourceAccessor());
        Assert.assertNotNull("parameters list for the change log is not null", liquibase.getChangeLogParameters());
        Assert.assertEquals("Standard database changelog parameters were not set", "DATABASECHANGELOGLOCK", liquibase.getChangeLogParameters().getValue("database.databaseChangeLogLockTableName", null));
        Assert.assertSame("database object for the change log is set as requested", database, liquibase.getDatabase());
    }

    @Test
    public void testConstructorChangelogPathsStandardize() throws Exception {
        Liquibase liquibase = new Liquibase("path\\with\\windows\\separators.xml", mockResourceAccessor, new MockDatabase());
        Assert.assertEquals("Windows path separators are translated correctly", "path/with/windows/separators.xml", liquibase.getChangeLogFile());
        liquibase = new Liquibase("path/with/unix/separators.xml", mockResourceAccessor, new MockDatabase());
        Assert.assertEquals("Unix path separators are left intact", "path/with/unix/separators.xml", liquibase.getChangeLogFile());
        liquibase = new Liquibase("/absolute/path/remains.xml", mockResourceAccessor, new MockDatabase());
        Assert.assertEquals("An absolute path is left intact", "/absolute/path/remains.xml", liquibase.getChangeLogFile());
    }

    @Test
    public void testConstructorCreateDatabaseInstanceFromConnection() throws LiquibaseException {
        DatabaseConnection databaseConnection = Mockito.mock(DatabaseConnection.class);
        Database database = mockDatabase;
        try {
            DatabaseFactory.setInstance(Mockito.mock(DatabaseFactory.class));
            Mockito.when(DatabaseFactory.getInstance().findCorrectDatabaseImplementation(databaseConnection)).thenReturn(database);
            Liquibase liquibase = new Liquibase("com/example/test.xml", mockResourceAccessor, databaseConnection);
            Assert.assertSame("Liquibase constructor passing connection did not find the correct database implementation", database, liquibase.getDatabase());
        } finally {
            DatabaseFactory.reset();
        }
    }

    @Test
    public void testGetResourceAccessor() throws LiquibaseException {
        Liquibase liquibase = new Liquibase("com/example/test.xml", mockResourceAccessor, mockDatabase);
        Assert.assertSame("ressourceAccessor is set as requested", liquibase.getResourceAccessor(), liquibase.getResourceAccessor());
    }

    @Test
    public void testSetCurrentDateTimeFunction() throws LiquibaseException {
        Database database = mockDatabase;
        String testFunction = "GetMyTime";
        getDatabase().setCurrentDateTimeFunction(testFunction);
        Mockito.verify(database).setCurrentDateTimeFunction(testFunction);
    }

    @Test
    public void testUpdatePassedStringContext() throws LiquibaseException {
        LiquibaseTest.LiquibaseDelegate liquibase = new LiquibaseTest.LiquibaseDelegate() {
            @Override
            public void update(Contexts contexts) throws LiquibaseException {
                objectToVerify = contexts;
            }
        };
        update("test");
        Assert.assertEquals("context is set correctly", "test", liquibase.objectToVerify.toString());
        liquibase.reset();
        update("");
        Assert.assertEquals("context is set correctly", "", liquibase.objectToVerify.toString());
        liquibase.reset();
        update(((String) (null)));
        Assert.assertEquals("context is set correctly", "", liquibase.objectToVerify.toString());
        liquibase.reset();
        update("test1, test2");
        Assert.assertEquals("context is set correctly", "test1,test2", liquibase.objectToVerify.toString());
        liquibase.reset();
    }

    @Test(expected = LockException.class)
    public void testUpdateExceptionGettingLock() throws LiquibaseException {
        Mockito.doThrow(LockException.class).when(mockLockService).waitForLock();
        Liquibase liquibase = new Liquibase("com/example/test.xml", mockResourceAccessor, mockDatabase);
        try {
            liquibase.update(((Contexts) (null)));
        } finally {
            Mockito.verify(mockLockService).waitForLock();
            // should not call anything else, even releaseLock()
        }
    }

    @Test(expected = ChangeLogParseException.class)
    public void testUpdateExceptionDoingUpdate() throws LiquibaseException {
        Contexts contexts = new Contexts("a,b");
        Liquibase liquibase = new Liquibase("com/example/test.xml", mockResourceAccessor, mockDatabase);
        Mockito.doThrow(ChangeLogParseException.class).when(mockChangeLogParser).parse("com/example/test.xml", liquibase.getChangeLogParameters(), mockResourceAccessor);
        try {
            liquibase.update(contexts);
        } finally {
            Mockito.verify(mockLockService).waitForLock();
            Mockito.verify(mockLockService).releaseLock();// should still call

            Mockito.verify(mockDatabase).setObjectQuotingStrategy(LEGACY);// should still call

            Mockito.verify(mockChangeLogParser).parse("com/example/test.xml", liquibase.getChangeLogParameters(), mockResourceAccessor);
        }
    }

    /* False positive: We do have an assertion in this test. */
    @Test
    @SuppressWarnings("squid:S2699")
    public void testGetStandardChangelogIterator() throws LiquibaseException {
        ChangeLogIterator iterator = new Liquibase("com/example/changelog.xml", mockResourceAccessor, mockDatabase).getStandardChangelogIterator(new Contexts("a", "b"), new LabelExpression("x", "y"), mockChangeLog);
        assertListsEqual(new Class[]{ ShouldRunChangeSetFilter.class, ContextChangeSetFilter.class, LabelChangeSetFilter.class, DbmsChangeSetFilter.class, IgnoreChangeSetFilter.class }, iterator.getChangeSetFilters(), new liquibase.test.Assert.AssertFunction() {
            @Override
            public void check(String message, Object expected, Object actual) {
                Assert.assertEquals(message, expected, actual.getClass());
            }
        });
    }

    /**
     * Convenience helper class for testing Liquibase methods that simply delegate to another.
     * To use, create a subclass that overrides the method delegated to with an implementation that stores whatever params are being passed.
     * After calling the delegating method in your test, assert against the objectToVerify
     */
    private static class LiquibaseDelegate extends Liquibase {
        /**
         * If using multiple parameters, store them here
         */
        protected final Map<String, Object> objectsToVerify = new HashMap<>();

        /**
         * If using a single parameter, store in here
         */
        protected Object objectToVerify;

        private LiquibaseDelegate() throws LiquibaseException {
            super("com/example/test.xml", new MockResourceAccessor(), Mockito.mock(Database.class));
        }

        /**
         * Resets the object(s)ToVerify so this delegate can be reused in a test.
         */
        public void reset() {
            objectToVerify = null;
            objectsToVerify.clear();
        }
    }
}

