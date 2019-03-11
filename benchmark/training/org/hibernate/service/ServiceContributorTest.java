/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.service;


import AvailableSettings.CACHE_REGION_FACTORY;
import java.util.Map;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cache.internal.RegionFactoryInitiator;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.testing.cache.CachingRegionFactory;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class ServiceContributorTest extends BaseUnitTestCase {
    @Test
    public void overrideCachingInitiator() {
        StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
        ssrb.clearSettings();
        final ServiceContributorTest.MyRegionFactoryInitiator initiator = new ServiceContributorTest.MyRegionFactoryInitiator();
        ssrb.addInitiator(initiator);
        final ServiceRegistryImplementor registry = ((ServiceRegistryImplementor) (ssrb.build()));
        try {
            final RegionFactory regionFactory = registry.getService(RegionFactory.class);
            Assert.assertTrue(initiator.called);
            ExtraAssertions.assertTyping(ServiceContributorTest.MyRegionFactory.class, regionFactory);
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    @Test
    public void overrideCachingInitiatorExplicitSet() {
        StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
        final ServiceContributorTest.MyRegionFactoryInitiator initiator = new ServiceContributorTest.MyRegionFactoryInitiator();
        ssrb.addInitiator(initiator);
        ssrb.applySetting(CACHE_REGION_FACTORY, new ServiceContributorTest.MyRegionFactory());
        final ServiceRegistryImplementor registry = ((ServiceRegistryImplementor) (ssrb.build()));
        try {
            registry.getService(RegionFactory.class);
            Assert.assertFalse(initiator.called);
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    // @Override
    // public RegionFactory initiateService(
    // Map configurationValues,
    // ServiceRegistryImplementor registry) {
    // called = true;
    // return super.initiateService( configurationValues, registry );
    // }
    class MyRegionFactoryInitiator extends RegionFactoryInitiator {
        private boolean called = false;

        @Override
        protected RegionFactory getFallback(Map configurationValues, ServiceRegistryImplementor registry) {
            called = true;
            return new ServiceContributorTest.MyRegionFactory();
        }
    }

    class MyRegionFactory extends CachingRegionFactory {}
}

