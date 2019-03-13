/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.cache.ehcache.test;


import AvailableSettings.CACHE_REGION_FACTORY;
import ConfigSettings.EHCACHE_CONFIGURATION_RESOURCE_NAME;
import java.util.function.Consumer;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-12869")
public class SingletonEhCacheRegionFactoryClasspathConfigurationFileTest {
    @Test
    public void testCacheInitialization() {
        try (SessionFactoryImplementor sessionFactory = TestHelper.buildStandardSessionFactory(( builder) -> builder.applySetting(CACHE_REGION_FACTORY, "ehcache-singleton").applySetting(EHCACHE_CONFIGURATION_RESOURCE_NAME, "/hibernate-config/ehcache-configuration.xml"))) {
            Assert.assertNotNull(sessionFactory);
        }
    }
}

