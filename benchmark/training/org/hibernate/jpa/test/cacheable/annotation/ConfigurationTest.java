/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.cacheable.annotation;


import SharedCacheMode.ALL;
import SharedCacheMode.DISABLE_SELECTIVE;
import SharedCacheMode.ENABLE_SELECTIVE;
import SharedCacheMode.NONE;
import SharedCacheMode.UNSPECIFIED;
import javax.persistence.EntityManagerFactory;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.testing.cache.CachingRegionFactory;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * this is hacky transient step until EMF building is integrated with metamodel
 *
 * @author Steve Ebersole
 */
public class ConfigurationTest extends BaseUnitTestCase {
    private EntityManagerFactory emf;

    @Test
    public void testSharedCacheModeNone() {
        MetadataImplementor metadata = buildMetadata(NONE);
        PersistentClass pc = metadata.getEntityBinding(ExplicitlyCacheableEntity.class.getName());
        Assert.assertFalse(pc.isCached());
        pc = metadata.getEntityBinding(ExplicitlyNonCacheableEntity.class.getName());
        Assert.assertFalse(pc.isCached());
        pc = metadata.getEntityBinding(NoCacheableAnnotationEntity.class.getName());
        Assert.assertFalse(pc.isCached());
    }

    @Test
    public void testSharedCacheModeUnspecified() {
        MetadataImplementor metadata = buildMetadata(UNSPECIFIED);
        PersistentClass pc = metadata.getEntityBinding(ExplicitlyCacheableEntity.class.getName());
        Assert.assertFalse(pc.isCached());
        pc = metadata.getEntityBinding(ExplicitlyNonCacheableEntity.class.getName());
        Assert.assertFalse(pc.isCached());
        pc = metadata.getEntityBinding(NoCacheableAnnotationEntity.class.getName());
        Assert.assertFalse(pc.isCached());
    }

    @Test
    public void testSharedCacheModeAll() {
        MetadataImplementor metadata = buildMetadata(ALL);
        PersistentClass pc = metadata.getEntityBinding(ExplicitlyCacheableEntity.class.getName());
        Assert.assertTrue(pc.isCached());
        pc = metadata.getEntityBinding(ExplicitlyNonCacheableEntity.class.getName());
        Assert.assertTrue(pc.isCached());
        pc = metadata.getEntityBinding(NoCacheableAnnotationEntity.class.getName());
        Assert.assertTrue(pc.isCached());
    }

    @Test
    public void testSharedCacheModeEnable() {
        MetadataImplementor metadata = buildMetadata(ENABLE_SELECTIVE);
        PersistentClass pc = metadata.getEntityBinding(ExplicitlyCacheableEntity.class.getName());
        Assert.assertTrue(pc.isCached());
        pc = metadata.getEntityBinding(ExplicitlyNonCacheableEntity.class.getName());
        Assert.assertFalse(pc.isCached());
        pc = metadata.getEntityBinding(NoCacheableAnnotationEntity.class.getName());
        Assert.assertFalse(pc.isCached());
    }

    @Test
    public void testSharedCacheModeDisable() {
        MetadataImplementor metadata = buildMetadata(DISABLE_SELECTIVE);
        PersistentClass pc = metadata.getEntityBinding(ExplicitlyCacheableEntity.class.getName());
        Assert.assertTrue(pc.isCached());
        pc = metadata.getEntityBinding(ExplicitlyNonCacheableEntity.class.getName());
        Assert.assertFalse(pc.isCached());
        pc = metadata.getEntityBinding(NoCacheableAnnotationEntity.class.getName());
        Assert.assertTrue(pc.isCached());
    }

    public static class CustomRegionFactory extends CachingRegionFactory {
        public CustomRegionFactory() {
        }

        @Override
        public AccessType getDefaultAccessType() {
            return AccessType.READ_WRITE;
        }
    }
}

