/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.cache.ehcache.test;


import AvailableSettings.CACHE_REGION_FACTORY;
import java.util.Collection;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.cache.ehcache.internal.EhcacheRegionFactory;
import org.hibernate.cache.ehcache.internal.SingletonEhcacheRegionFactory;
import org.hibernate.cache.spi.RegionFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class SmokeTest {
    @Test
    public void testStrategySelectorRegistrations() {
        final BootstrapServiceRegistry registry = new BootstrapServiceRegistryBuilder().build();
        final Collection<Class<? extends RegionFactory>> implementors = registry.getService(StrategySelector.class).getRegisteredStrategyImplementors(RegionFactory.class);
        Assert.assertTrue(implementors.contains(EhcacheRegionFactory.class));
        Assert.assertTrue(implementors.contains(SingletonEhcacheRegionFactory.class));
    }

    @Test
    public void testEhcacheShortName() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().applySetting(CACHE_REGION_FACTORY, "ehcache").build();
        MatcherAssert.assertThat(registry.getService(RegionFactory.class), CoreMatchers.instanceOf(EhcacheRegionFactory.class));
    }

    @Test
    public void testSingletonEhcacheShortName() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().applySetting(CACHE_REGION_FACTORY, "ehcache-singleton").build();
        MatcherAssert.assertThat(registry.getService(RegionFactory.class), CoreMatchers.instanceOf(SingletonEhcacheRegionFactory.class));
    }
}

