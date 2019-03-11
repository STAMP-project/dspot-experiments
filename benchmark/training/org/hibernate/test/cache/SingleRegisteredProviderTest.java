/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.cache;


import AvailableSettings.USE_SECOND_LEVEL_CACHE;
import java.util.Collection;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.cache.internal.NoCachingRegionFactory;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class SingleRegisteredProviderTest extends BaseUnitTestCase {
    @Test
    public void testCachingExplicitlyDisabled() {
        final StandardServiceRegistry registry = build();
        MatcherAssert.assertThat(registry.getService(RegionFactory.class), CoreMatchers.instanceOf(NoCachingRegionFactory.class));
    }

    @Test
    public void testCachingImplicitlyEnabledRegistered() {
        final BootstrapServiceRegistry bsr = new BootstrapServiceRegistryBuilder().build();
        final Collection<Class<? extends RegionFactory>> implementors = bsr.getService(StrategySelector.class).getRegisteredStrategyImplementors(RegionFactory.class);
        MatcherAssert.assertThat(implementors.size(), CoreMatchers.equalTo(1));
        final StandardServiceRegistry ssr = build();
        MatcherAssert.assertThat(ssr.getService(RegionFactory.class), CoreMatchers.instanceOf(NoCachingRegionFactory.class));
    }

    @Test
    public void testCachingImplicitlyEnabledNoRegistered() {
        final BootstrapServiceRegistry bsr = new BootstrapServiceRegistryBuilder().build();
        final Collection<Class<? extends RegionFactory>> implementors = bsr.getService(StrategySelector.class).getRegisteredStrategyImplementors(RegionFactory.class);
        MatcherAssert.assertThat(implementors.size(), CoreMatchers.equalTo(1));
        bsr.getService(StrategySelector.class).unRegisterStrategyImplementor(RegionFactory.class, implementors.iterator().next());
        final StandardServiceRegistry ssr = build();
        MatcherAssert.assertThat(ssr.getService(RegionFactory.class), CoreMatchers.instanceOf(NoCachingRegionFactory.class));
    }

    @Test
    public void testConnectionsRegistered() {
        final BootstrapServiceRegistry bsr = new BootstrapServiceRegistryBuilder().build();
        final Collection<Class<? extends ConnectionProvider>> implementors = bsr.getService(StrategySelector.class).getRegisteredStrategyImplementors(ConnectionProvider.class);
        MatcherAssert.assertThat(implementors.size(), CoreMatchers.equalTo(0));
        bsr.getService(StrategySelector.class).registerStrategyImplementor(ConnectionProvider.class, "testing", DriverManagerConnectionProviderImpl.class);
        final StandardServiceRegistry ssr = build();
        final ConnectionProvider configuredProvider = ssr.getService(ConnectionProvider.class);
        MatcherAssert.assertThat(configuredProvider, CoreMatchers.instanceOf(DriverManagerConnectionProviderImpl.class));
    }
}

