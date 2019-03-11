/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.cache.cid;


import AvailableSettings.USE_SECOND_LEVEL_CACHE;
import SharedCacheMode.ENABLE_SELECTIVE;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class NonAggregatedCompositeIdCachingTest extends BaseUnitTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-9913")
    public void testNonAggregatedCompositeId() {
        // HHH-9913 reports a NPE when bootstrapping a SF with non-aggregated composite identifiers
        // in org.hibernate.cache.internal.CacheDataDescriptionImpl#decode
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().applySetting(USE_SECOND_LEVEL_CACHE, true).build();
        try {
            addAnnotatedClass(It.class).getMetadataBuilder().applySharedCacheMode(ENABLE_SELECTIVE).build().buildSessionFactory();
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9913")
    public void testNonAggregatedCompositeIdWithPkClass() {
        // HHH-9913 reports a NPE when bootstrapping a SF with non-aggregated composite identifiers
        // in org.hibernate.cache.internal.CacheDataDescriptionImpl#decode
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
        try {
            addAnnotatedClass(ItWithPkClass.class).getMetadataBuilder().applySharedCacheMode(ENABLE_SELECTIVE).build().buildSessionFactory();
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }
}

