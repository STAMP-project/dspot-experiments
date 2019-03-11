/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.cache;


import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Proxy;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class ReferenceCacheTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testUseOfDirectReferencesInCache() throws Exception {
        EntityPersister persister = ((EntityPersister) (sessionFactory().getClassMetadata(ReferenceCacheTest.MyReferenceData.class)));
        Assert.assertFalse(persister.isMutable());
        Assert.assertTrue(persister.buildCacheEntry(null, null, null, null).isReferenceEntry());
        Assert.assertFalse(persister.hasProxy());
        final ReferenceCacheTest.MyReferenceData myReferenceData = new ReferenceCacheTest.MyReferenceData(1, "first item", "abc");
        // save a reference in one session
        Session s = openSession();
        s.beginTransaction();
        s.save(myReferenceData);
        s.getTransaction().commit();
        s.close();
        // now load it in another
        s = openSession();
        s.beginTransaction();
        // MyReferenceData loaded = (MyReferenceData) s.get( MyReferenceData.class, 1 );
        ReferenceCacheTest.MyReferenceData loaded = ((ReferenceCacheTest.MyReferenceData) (s.load(ReferenceCacheTest.MyReferenceData.class, 1)));
        s.getTransaction().commit();
        s.close();
        // the 2 instances should be the same (==)
        Assert.assertTrue("The two instances were different references", (myReferenceData == loaded));
        // now try query caching
        s = openSession();
        s.beginTransaction();
        ReferenceCacheTest.MyReferenceData queried = ((ReferenceCacheTest.MyReferenceData) (s.createQuery("from MyReferenceData").setCacheable(true).list().get(0)));
        s.getTransaction().commit();
        s.close();
        // the 2 instances should be the same (==)
        Assert.assertTrue("The two instances were different references", (myReferenceData == queried));
        // cleanup
        s = openSession();
        s.beginTransaction();
        s.delete(myReferenceData);
        s.getTransaction().commit();
        s.close();
    }

    @Entity(name = "MyReferenceData")
    @Immutable
    @Cacheable
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    @Proxy(lazy = false)
    @SuppressWarnings("UnusedDeclaration")
    public static class MyReferenceData {
        @Id
        private Integer id;

        private String name;

        private String theValue;

        public MyReferenceData(Integer id, String name, String theValue) {
            this.id = id;
            this.name = name;
            this.theValue = theValue;
        }

        protected MyReferenceData() {
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTheValue() {
            return theValue;
        }

        public void setTheValue(String theValue) {
            this.theValue = theValue;
        }
    }
}

