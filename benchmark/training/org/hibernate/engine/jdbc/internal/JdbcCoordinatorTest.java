package org.hibernate.engine.jdbc.internal;


import AvailableSettings.CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT;
import PhysicalConnectionHandlingMode.IMMEDIATE_ACQUISITION_AND_HOLD;
import StandardConverters.BOOLEAN;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.resource.jdbc.spi.JdbcObserver;
import org.hibernate.resource.jdbc.spi.JdbcSessionContext;
import org.hibernate.resource.jdbc.spi.JdbcSessionOwner;
import org.hibernate.service.ServiceRegistry;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class JdbcCoordinatorTest {
    @Test
    public void testConnectionClose() throws IllegalAccessException, NoSuchFieldException, SQLException {
        Connection connection = Mockito.mock(Connection.class);
        JdbcSessionOwner sessionOwner = Mockito.mock(JdbcSessionOwner.class);
        JdbcConnectionAccess jdbcConnectionAccess = Mockito.mock(JdbcConnectionAccess.class);
        Mockito.when(jdbcConnectionAccess.obtainConnection()).thenReturn(connection);
        Mockito.when(jdbcConnectionAccess.supportsAggressiveRelease()).thenReturn(false);
        JdbcSessionContext sessionContext = Mockito.mock(JdbcSessionContext.class);
        Mockito.when(sessionOwner.getJdbcSessionContext()).thenReturn(sessionContext);
        Mockito.when(sessionOwner.getJdbcConnectionAccess()).thenReturn(jdbcConnectionAccess);
        ServiceRegistry serviceRegistry = Mockito.mock(ServiceRegistry.class);
        Mockito.when(sessionContext.getServiceRegistry()).thenReturn(serviceRegistry);
        Mockito.when(sessionContext.getPhysicalConnectionHandlingMode()).thenReturn(IMMEDIATE_ACQUISITION_AND_HOLD);
        JdbcObserver jdbcObserver = Mockito.mock(JdbcObserver.class);
        Mockito.when(sessionContext.getObserver()).thenReturn(jdbcObserver);
        JdbcServices jdbcServices = Mockito.mock(JdbcServices.class);
        Mockito.when(serviceRegistry.getService(ArgumentMatchers.eq(JdbcServices.class))).thenReturn(jdbcServices);
        ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
        Mockito.when(serviceRegistry.getService(ArgumentMatchers.eq(ConfigurationService.class))).thenReturn(configurationService);
        Mockito.when(configurationService.getSetting(ArgumentMatchers.eq(CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT), ArgumentMatchers.same(BOOLEAN), ArgumentMatchers.eq(false))).thenReturn(false);
        SqlExceptionHelper sqlExceptionHelper = Mockito.mock(SqlExceptionHelper.class);
        Mockito.when(jdbcServices.getSqlExceptionHelper()).thenReturn(sqlExceptionHelper);
        JdbcCoordinatorImpl jdbcCoordinator = new JdbcCoordinatorImpl(null, sessionOwner);
        Batch currentBatch = Mockito.mock(Batch.class);
        Field currentBatchField = JdbcCoordinatorImpl.class.getDeclaredField("currentBatch");
        currentBatchField.setAccessible(true);
        currentBatchField.set(jdbcCoordinator, currentBatch);
        Mockito.doThrow(IllegalStateException.class).when(currentBatch).release();
        try {
            jdbcCoordinator.close();
            Assert.fail("Should throw IllegalStateException");
        } catch (Exception expected) {
            Assert.assertEquals(IllegalStateException.class, expected.getClass());
        }
        Mockito.verify(jdbcConnectionAccess, Mockito.times(1)).releaseConnection(ArgumentMatchers.same(connection));
    }
}

