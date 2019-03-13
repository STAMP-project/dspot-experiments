/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.userguide.util;


import LoadState.LOADED;
import LoadState.UNKNOWN;
import PersistenceUtilHelper.MetadataCache;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;
import org.hibernate.jpa.internal.util.PersistenceUtilHelper;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for HHH-5094 and HHH-5334
 *
 * @author Hardy Ferentschik
 */
public class PersistenceUtilHelperTest {
    private final MetadataCache cache = new PersistenceUtilHelper.MetadataCache();

    public static class FieldAccessBean extends PersistenceUtilHelperTest.FieldAccessBeanBase {
        protected String protectedAccessProperty;

        private String privateAccessProperty;
    }

    public static class FieldAccessBeanBase {
        public String publicAccessProperty;
    }

    public static class MethodAccessBean extends PersistenceUtilHelperTest.MethodAccessBeanBase {
        private String protectedAccessProperty;

        private String privateAccessProperty;

        protected String getProtectedAccessPropertyValue() {
            return protectedAccessProperty;
        }

        private String getPrivateAccessPropertyValue() {
            return privateAccessProperty;
        }
    }

    public static class MethodAccessBeanBase {
        private String publicAccessProperty;

        public String getPublicAccessPropertyValue() {
            return publicAccessProperty;
        }
    }

    @Test
    public void testIsLoadedWithReferencePublicField() {
        Assert.assertEquals(UNKNOWN, PersistenceUtilHelper.isLoadedWithReference(new PersistenceUtilHelperTest.FieldAccessBean(), "publicAccessProperty", cache));
    }

    @Test
    public void testIsLoadedWithReferencePublicMethod() {
        Assert.assertEquals(UNKNOWN, PersistenceUtilHelper.isLoadedWithReference(new PersistenceUtilHelperTest.MethodAccessBean(), "publicAccessPropertyValue", cache));
    }

    @Test
    public void testIsLoadedWithReferenceProtectedField() {
        Assert.assertEquals(UNKNOWN, PersistenceUtilHelper.isLoadedWithReference(new PersistenceUtilHelperTest.FieldAccessBean(), "protectedAccessProperty", cache));
    }

    @Test
    public void testIsLoadedWithReferenceProtectedMethod() {
        Assert.assertEquals(UNKNOWN, PersistenceUtilHelper.isLoadedWithReference(new PersistenceUtilHelperTest.MethodAccessBean(), "protectedAccessPropertyValue", cache));
    }

    @Test
    public void testIsLoadedWithReferencePrivateField() {
        Assert.assertEquals(UNKNOWN, PersistenceUtilHelper.isLoadedWithReference(new PersistenceUtilHelperTest.FieldAccessBean(), "privateAccessProperty", cache));
    }

    @Test
    public void testIsLoadedWithReferencePrivateMethod() {
        Assert.assertEquals(UNKNOWN, PersistenceUtilHelper.isLoadedWithReference(new PersistenceUtilHelperTest.MethodAccessBean(), "privateAccessPropertyValue", cache));
    }

    @Test
    public void testIsLoadedWithNullInterceptor() {
        Assert.assertEquals(LOADED, PersistenceUtilHelper.isLoaded(new PersistentAttributeInterceptable() {
            @Override
            public PersistentAttributeInterceptor $$_hibernate_getInterceptor() {
                return null;
            }

            @Override
            public void $$_hibernate_setInterceptor(PersistentAttributeInterceptor interceptor) {
            }
        }));
    }
}

