/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.cfg.cache;


import AccessType.READ_ONLY;
import AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Immutable;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class DefaultCacheConcurrencyPropertyTest extends BaseUnitTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-9763")
    @FailureExpected(jiraKey = "HHH-9763")
    public void testExplicitDefault() {
        final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().applySetting(DEFAULT_CACHE_CONCURRENCY_STRATEGY, "read-only").build();
        try {
            Assert.assertEquals("read-only", ssr.getService(ConfigurationService.class).getSettings().get(DEFAULT_CACHE_CONCURRENCY_STRATEGY));
            final MetadataImplementor metadata = ((MetadataImplementor) (addAnnotatedClass(DefaultCacheConcurrencyPropertyTest.TheEntity.class).buildMetadata()));
            Assert.assertEquals(READ_ONLY, metadata.getMetadataBuildingOptions().getMappingDefaults().getImplicitCacheAccessType());
            final SessionFactoryImplementor sf = ((SessionFactoryImplementor) (metadata.buildSessionFactory()));
            try {
                final EntityPersister persister = sf.getMetamodel().entityPersister(DefaultCacheConcurrencyPropertyTest.TheEntity.class.getName());
                Assert.assertTrue(persister.canReadFromCache());
                Assert.assertTrue(persister.canWriteToCache());
                Assert.assertNotNull(persister.getCacheAccessStrategy());
            } finally {
                sf.close();
            }
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }

    @Entity(name = "TheEntity")
    @Table(name = "THE_ENTITY")
    @Cacheable
    @Immutable
    public static class TheEntity {
        @Id
        public Long id;
    }
}

