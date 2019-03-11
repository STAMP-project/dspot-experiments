package liquibase.lockservice;


import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.core.MySQLDatabase;
import liquibase.database.core.OracleDatabase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author John Sanda
 */
public class LockServiceFactoryTest {
    @Test
    public void getInstance() {
        Assert.assertNotNull(LockServiceFactory.getInstance());
        Assert.assertTrue(((LockServiceFactory.getInstance()) == (LockServiceFactory.getInstance())));
        // Collection<LockService> lockServices = LockServiceFactory.getInstance().getLockServices();
        // assertEquals(0, lockServices.size());
    }

    @Test
    public void getLockService() {
        final Database oracle1 = new OracleDatabase() {
            @Override
            public boolean equals(Object o) {
                return o == (this);
            }
        };
        final Database oracle2 = new OracleDatabase() {
            @Override
            public boolean equals(Object o) {
                return o == (this);
            }
        };
        final Database mysql = new MySQLDatabase() {
            @Override
            public boolean equals(Object o) {
                return o == (this);
            }
        };
        DatabaseFactory databaseFactory = DatabaseFactory.getInstance();
        databaseFactory.register(oracle1);
        databaseFactory.register(oracle2);
        databaseFactory.register(mysql);
        LockServiceFactory lockServiceFactory = LockServiceFactory.getInstance();
        Assert.assertNotNull(lockServiceFactory.getLockService(oracle1));
        Assert.assertNotNull(lockServiceFactory.getLockService(oracle2));
        Assert.assertNotNull(lockServiceFactory.getLockService(mysql));
        Assert.assertTrue(((lockServiceFactory.getLockService(oracle1)) == (lockServiceFactory.getLockService(oracle1))));
        Assert.assertTrue(((lockServiceFactory.getLockService(oracle2)) == (lockServiceFactory.getLockService(oracle2))));
        Assert.assertTrue(((lockServiceFactory.getLockService(mysql)) == (lockServiceFactory.getLockService(mysql))));
        Assert.assertTrue(((lockServiceFactory.getLockService(oracle1)) != (lockServiceFactory.getLockService(oracle2))));
        Assert.assertTrue(((lockServiceFactory.getLockService(oracle1)) != (lockServiceFactory.getLockService(mysql))));
        Assert.assertTrue(((lockServiceFactory.getLockService(getMockDatabase())) instanceof MockLockService));
    }
}

