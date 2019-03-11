/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.userguide.util;


import LoadState.UNKNOWN;
import PersistenceUtilHelper.MetadataCache;
import javax.persistence.Persistence;
import org.hibernate.jpa.internal.util.PersistenceUtilHelper;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class ProviderUtilTest extends BaseEntityManagerFunctionalTestCase {
    private final MetadataCache cache = new PersistenceUtilHelper.MetadataCache();

    @Test
    public void testIsLoadedOnUnknownClass() {
        final Object entity = new Object();
        Assert.assertTrue(Persistence.getPersistenceUtil().isLoaded(entity));
        Assert.assertEquals(UNKNOWN, PersistenceUtilHelper.isLoaded(entity));
    }

    @Test
    public void testIsLoadedOnKnownClass() {
        final Author entity = new Author();
        Assert.assertTrue(Persistence.getPersistenceUtil().isLoaded(entity));
        Assert.assertEquals(UNKNOWN, PersistenceUtilHelper.isLoaded(entity));
    }

    @Test
    public void testIsLoadedWithoutReferenceOnUnknownClass() {
        final Object entity = new Object();
        Assert.assertEquals(UNKNOWN, PersistenceUtilHelper.isLoadedWithoutReference(entity, "attribute", cache));
    }

    @Test
    public void testIsLoadedWithoutReferenceOnKnownClass() {
        final Author entity = new Author();
        Assert.assertEquals(UNKNOWN, PersistenceUtilHelper.isLoadedWithoutReference(entity, "attribute", cache));
    }

    @Test
    public void testIsLoadedWithReferenceOnUnknownClass() {
        final Object entity = new Object();
        Assert.assertEquals(UNKNOWN, PersistenceUtilHelper.isLoadedWithReference(entity, "attribute", cache));
    }

    @Test
    public void testIsLoadedWithReferenceOnKnownClass() {
        final Author entity = new Author();
        Assert.assertEquals(UNKNOWN, PersistenceUtilHelper.isLoadedWithReference(entity, "attribute", cache));
    }
}

