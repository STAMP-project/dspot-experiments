/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.multitenancy;


import ConnectionReleaseMode.AFTER_STATEMENT;
import Environment.MULTI_TENANT;
import Environment.MULTI_TENANT_CONNECTION_PROVIDER;
import Environment.RELEASE_CONNECTIONS;
import MultiTenancyStrategy.SCHEMA;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.service.spi.ServiceException;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.env.ConnectionProviderBuilder;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
@TestForIssue(jiraKey = "HHH-7311")
public class ConfigurationValidationTest extends BaseUnitTestCase {
    @Test(expected = ServiceException.class)
    public void testInvalidConnectionProvider() {
        ServiceRegistryImplementor serviceRegistry = null;
        try {
            serviceRegistry = ((ServiceRegistryImplementor) (new StandardServiceRegistryBuilder().applySetting(MULTI_TENANT, SCHEMA).applySetting(MULTI_TENANT_CONNECTION_PROVIDER, "class.not.present.in.classpath").build()));
            buildMetadata().buildSessionFactory().close();
        } finally {
            if (serviceRegistry != null) {
                try {
                    StandardServiceRegistryBuilder.destroy(serviceRegistry);
                } catch (Exception ignore) {
                }
            }
        }
    }

    @Test
    public void testReleaseMode() {
        ServiceRegistryImplementor serviceRegistry = null;
        try {
            serviceRegistry = ((ServiceRegistryImplementor) (new StandardServiceRegistryBuilder().applySetting(MULTI_TENANT, SCHEMA).applySetting(RELEASE_CONNECTIONS, AFTER_STATEMENT.name()).addService(MultiTenantConnectionProvider.class, new TestingConnectionProvider(new TestingConnectionProvider.NamedConnectionProviderPair("acme", ConnectionProviderBuilder.buildConnectionProvider("acme")))).build()));
            buildMetadata().buildSessionFactory().close();
        } finally {
            if (serviceRegistry != null) {
                try {
                    StandardServiceRegistryBuilder.destroy(serviceRegistry);
                } catch (Exception ignore) {
                }
            }
        }
    }
}

