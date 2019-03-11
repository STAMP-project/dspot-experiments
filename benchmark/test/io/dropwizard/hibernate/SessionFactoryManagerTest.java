package io.dropwizard.hibernate;


import io.dropwizard.db.ManagedDataSource;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class SessionFactoryManagerTest {
    private final SessionFactory factory = Mockito.mock(SessionFactory.class);

    private final ManagedDataSource dataSource = Mockito.mock(ManagedDataSource.class);

    private final SessionFactoryManager manager = new SessionFactoryManager(factory, dataSource);

    @Test
    public void closesTheFactoryOnStopping() throws Exception {
        manager.stop();
        Mockito.verify(factory).close();
    }

    @Test
    public void stopsTheDataSourceOnStopping() throws Exception {
        manager.stop();
        Mockito.verify(dataSource).stop();
    }

    @Test
    public void startsTheDataSourceOnStarting() throws Exception {
        manager.start();
        Mockito.verify(dataSource).start();
    }
}

