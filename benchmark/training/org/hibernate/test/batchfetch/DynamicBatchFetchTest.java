/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.batchfetch;


import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


public class DynamicBatchFetchTest extends BaseCoreFunctionalTestCase {
    private static int currentId = 1;

    @Test
    public void testDynamicBatchFetch() {
        Integer aId1 = createAAndB();
        Integer aId2 = createAAndB();
        Session s = openSession();
        s.getTransaction().begin();
        List resultList = s.createQuery((((("from A where id in (" + aId1) + ",") + aId2) + ") order by id")).list();
        A a1 = ((A) (resultList.get(0)));
        A a2 = ((A) (resultList.get(1)));
        Assert.assertEquals(aId1, a1.getId());
        Assert.assertEquals(aId2, a2.getId());
        Assert.assertFalse(Hibernate.isInitialized(a1.getB()));
        Assert.assertFalse(Hibernate.isInitialized(a2.getB()));
        Assert.assertEquals("foo", a1.getB().getOtherProperty());
        Assert.assertTrue(Hibernate.isInitialized(a1.getB()));
        // a2.getB() is still uninitialized
        Assert.assertFalse(Hibernate.isInitialized(a2.getB()));
        // the B entity has been loaded, but is has not been made the target of a2.getB() yet.
        Assert.assertTrue(getPersistenceContext().containsEntity(new org.hibernate.engine.spi.EntityKey(getContextEntityIdentifier(a2.getB()), getFactory().getEntityPersister(B.class.getName()))));
        // a2.getB() is still uninitialized; getting the ID for a2.getB() did not initialize it.
        Assert.assertFalse(Hibernate.isInitialized(a2.getB()));
        Assert.assertEquals("foo", a2.getB().getOtherProperty());
        // now it's initialized.
        Assert.assertTrue(Hibernate.isInitialized(a2.getB()));
        s.getTransaction().commit();
        s.close();
    }
}

