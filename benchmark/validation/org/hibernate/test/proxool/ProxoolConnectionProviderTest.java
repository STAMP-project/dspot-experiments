/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.proxool;


import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 * Test to verify connection pools are closed, and that only the managed one is closed.
 *
 * @author Sanne Grinovero
 */
public class ProxoolConnectionProviderTest extends BaseUnitTestCase {
    @Test
    public void testPoolsClosed() {
        assertDefinedPools();// zero-length-vararg used as parameter

        StandardServiceRegistry serviceRegistry = buildServiceRegistry("pool-one");
        ConnectionProvider providerOne = serviceRegistry.getService(ConnectionProvider.class);
        assertDefinedPools("pool-one");
        StandardServiceRegistry serviceRegistryTwo = buildServiceRegistry("pool-two");
        ConnectionProvider providerTwo = serviceRegistryTwo.getService(ConnectionProvider.class);
        assertDefinedPools("pool-one", "pool-two");
        StandardServiceRegistryBuilder.destroy(serviceRegistry);
        assertDefinedPools("pool-two");
        StandardServiceRegistryBuilder.destroy(serviceRegistryTwo);
        assertDefinedPools();
    }
}

