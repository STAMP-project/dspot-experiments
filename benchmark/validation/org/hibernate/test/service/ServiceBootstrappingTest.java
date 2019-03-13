/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.service;


import Environment.DIALECT;
import Environment.SHOW_SQL;
import java.util.Properties;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.internal.UserSuppliedConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator.ConnectionProviderJdbcConnectionAccess;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.env.ConnectionProviderBuilder;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@RequiresDialect(H2Dialect.class)
public class ServiceBootstrappingTest extends BaseUnitTestCase {
    @Test
    public void testBasicBuild() {
        // this test requires that SHOW_SQL property isn't passed from the outside (eg. via Gradle)
        final String showSqlPropertyFromOutside = System.getProperty(SHOW_SQL);
        Assume.assumeFalse("true".equals(showSqlPropertyFromOutside));
        final StandardServiceRegistryImpl serviceRegistry = ((StandardServiceRegistryImpl) (new StandardServiceRegistryBuilder().applySettings(ConnectionProviderBuilder.getConnectionProviderProperties()).build()));
        try {
            final JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
            Assert.assertTrue(((jdbcServices.getDialect()) instanceof H2Dialect));
            final ConnectionProviderJdbcConnectionAccess connectionAccess = ExtraAssertions.assertTyping(ConnectionProviderJdbcConnectionAccess.class, jdbcServices.getBootstrapJdbcConnectionAccess());
            Assert.assertTrue(connectionAccess.getConnectionProvider().isUnwrappableAs(DriverManagerConnectionProviderImpl.class));
            Assert.assertFalse(jdbcServices.getSqlStatementLogger().isLogToStdout());
        } finally {
            serviceRegistry.destroy();
        }
    }

    @Test
    public void testBuildWithLogging() {
        Properties props = ConnectionProviderBuilder.getConnectionProviderProperties();
        props.put(SHOW_SQL, "true");
        StandardServiceRegistryImpl serviceRegistry = ((StandardServiceRegistryImpl) (new StandardServiceRegistryBuilder().applySettings(props).build()));
        try {
            JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
            Assert.assertTrue(((jdbcServices.getDialect()) instanceof H2Dialect));
            final ConnectionProviderJdbcConnectionAccess connectionAccess = ExtraAssertions.assertTyping(ConnectionProviderJdbcConnectionAccess.class, jdbcServices.getBootstrapJdbcConnectionAccess());
            Assert.assertTrue(connectionAccess.getConnectionProvider().isUnwrappableAs(DriverManagerConnectionProviderImpl.class));
            Assert.assertTrue(jdbcServices.getSqlStatementLogger().isLogToStdout());
        } finally {
            serviceRegistry.destroy();
        }
    }

    @Test
    public void testBuildWithServiceOverride() {
        StandardServiceRegistryImpl serviceRegistry = ((StandardServiceRegistryImpl) (new StandardServiceRegistryBuilder().applySettings(ConnectionProviderBuilder.getConnectionProviderProperties()).build()));
        Properties props = ConnectionProviderBuilder.getConnectionProviderProperties();
        props.setProperty(DIALECT, H2Dialect.class.getName());
        try {
            JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
            Assert.assertTrue(((jdbcServices.getDialect()) instanceof H2Dialect));
            ConnectionProviderJdbcConnectionAccess connectionAccess = ExtraAssertions.assertTyping(ConnectionProviderJdbcConnectionAccess.class, jdbcServices.getBootstrapJdbcConnectionAccess());
            Assert.assertTrue(connectionAccess.getConnectionProvider().isUnwrappableAs(DriverManagerConnectionProviderImpl.class));
        } finally {
            serviceRegistry.destroy();
        }
        try {
            serviceRegistry = ((StandardServiceRegistryImpl) (new StandardServiceRegistryBuilder().applySettings(props).addService(ConnectionProvider.class, new UserSuppliedConnectionProviderImpl()).build()));
            JdbcServices jdbcServices = serviceRegistry.getService(JdbcServices.class);
            Assert.assertTrue(((jdbcServices.getDialect()) instanceof H2Dialect));
            ConnectionProviderJdbcConnectionAccess connectionAccess = ExtraAssertions.assertTyping(ConnectionProviderJdbcConnectionAccess.class, jdbcServices.getBootstrapJdbcConnectionAccess());
            Assert.assertTrue(connectionAccess.getConnectionProvider().isUnwrappableAs(UserSuppliedConnectionProviderImpl.class));
        } finally {
            serviceRegistry.destroy();
        }
    }
}

