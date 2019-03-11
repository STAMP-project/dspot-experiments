package liquibase.database;


import org.junit.Assert;
import org.junit.Test;


public class DatabaseFactoryTest {
    @Test
    public void getInstance() {
        Assert.assertNotNull(DatabaseFactory.getInstance());
    }
}

