/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.cache;


import Status.DELETED;
import Status.GONE;
import Status.READ_ONLY;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Proxy;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.ManagedEntity;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author John O'Hara
 */
public class ByteCodeEnhancedImmutableReferenceCacheTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testUseOfDirectReferencesInCache() throws Exception {
        EntityPersister persister = ((EntityPersister) (sessionFactory().getClassMetadata(ByteCodeEnhancedImmutableReferenceCacheTest.MyEnhancedReferenceData.class)));
        Assert.assertFalse(persister.isMutable());
        Assert.assertTrue(persister.buildCacheEntry(null, null, null, null).isReferenceEntry());
        Assert.assertFalse(persister.hasProxy());
        final ByteCodeEnhancedImmutableReferenceCacheTest.MyEnhancedReferenceData myReferenceData = new ByteCodeEnhancedImmutableReferenceCacheTest.MyEnhancedReferenceData(1, "first item", "abc");
        // save a reference in one session
        Session s = openSession();
        s.beginTransaction();
        s.save(myReferenceData);
        s.getTransaction().commit();
        s.close();
        Assert.assertNotNull(myReferenceData.$$_hibernate_getEntityEntry());
        // now load it in another
        s = openSession();
        s.beginTransaction();
        // MyEnhancedReferenceData loaded = (MyEnhancedReferenceData) s.get( MyEnhancedReferenceData.class, 1 );
        ByteCodeEnhancedImmutableReferenceCacheTest.MyEnhancedReferenceData loaded = ((ByteCodeEnhancedImmutableReferenceCacheTest.MyEnhancedReferenceData) (s.load(ByteCodeEnhancedImmutableReferenceCacheTest.MyEnhancedReferenceData.class, 1)));
        s.getTransaction().commit();
        s.close();
        // the 2 instances should be the same (==)
        Assert.assertTrue("The two instances were different references", (myReferenceData == loaded));
        // now try query caching
        s = openSession();
        s.beginTransaction();
        ByteCodeEnhancedImmutableReferenceCacheTest.MyEnhancedReferenceData queried = ((ByteCodeEnhancedImmutableReferenceCacheTest.MyEnhancedReferenceData) (s.createQuery("from MyEnhancedReferenceData").setCacheable(true).list().get(0)));
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

    @Test
    @TestForIssue(jiraKey = "HHH-10795")
    public void testAssociatedWithMultiplePersistenceContexts() {
        ByteCodeEnhancedImmutableReferenceCacheTest.MyEnhancedReferenceData myReferenceData = new ByteCodeEnhancedImmutableReferenceCacheTest.MyEnhancedReferenceData(1, "first item", "abc");
        ByteCodeEnhancedImmutableReferenceCacheTest.MyEnhancedReferenceData myOtherReferenceData = new ByteCodeEnhancedImmutableReferenceCacheTest.MyEnhancedReferenceData(2, "second item", "def");
        // save a reference in one session
        Session s1 = openSession();
        s1.beginTransaction();
        s1.save(myReferenceData);
        s1.save(myOtherReferenceData);
        s1.getTransaction().commit();
        s1.close();
        Assert.assertNotNull(myReferenceData.$$_hibernate_getEntityEntry());
        Assert.assertNotNull(myOtherReferenceData.$$_hibernate_getEntityEntry());
        // now associate myReferenceData with 2 sessions
        s1 = openSession();
        s1.beginTransaction();
        myReferenceData = s1.get(ByteCodeEnhancedImmutableReferenceCacheTest.MyEnhancedReferenceData.class, myReferenceData.getId());
        myOtherReferenceData = s1.get(ByteCodeEnhancedImmutableReferenceCacheTest.MyEnhancedReferenceData.class, myOtherReferenceData.getId());
        Assert.assertTrue(s1.contains(myReferenceData));
        Assert.assertTrue(s1.contains(myOtherReferenceData));
        // prev/next references should be null; entityEntry should be non-null;
        Assert.assertNull(myReferenceData.$$_hibernate_getPreviousManagedEntity());
        Assert.assertNull(myReferenceData.$$_hibernate_getNextManagedEntity());
        Assert.assertNull(myOtherReferenceData.$$_hibernate_getPreviousManagedEntity());
        Assert.assertNull(myOtherReferenceData.$$_hibernate_getNextManagedEntity());
        Assert.assertSame(myReferenceData.$$_hibernate_getEntityEntry(), getPersistenceContext().getEntry(myReferenceData));
        Assert.assertSame(myOtherReferenceData.$$_hibernate_getEntityEntry(), getPersistenceContext().getEntry(myOtherReferenceData));
        Session s2 = openSession();
        s2.beginTransaction();
        // s2 should contains no entities
        Assert.assertFalse(s2.contains(myReferenceData));
        Assert.assertFalse(s2.contains(myOtherReferenceData));
        Assert.assertNull(getPersistenceContext().getEntry(myReferenceData));
        Assert.assertNull(getPersistenceContext().getEntry(myOtherReferenceData));
        // evict should do nothing, since p is not associated with s2
        s2.evict(myReferenceData);
        s2.evict(myOtherReferenceData);
        Assert.assertSame(myReferenceData, s2.get(ByteCodeEnhancedImmutableReferenceCacheTest.MyEnhancedReferenceData.class, myReferenceData.getId()));
        Assert.assertSame(myOtherReferenceData, s2.get(ByteCodeEnhancedImmutableReferenceCacheTest.MyEnhancedReferenceData.class, myOtherReferenceData.getId()));
        Assert.assertTrue(s2.contains(myReferenceData));
        Assert.assertTrue(s2.contains(myOtherReferenceData));
        // still associated with s1
        Assert.assertTrue(s1.contains(myReferenceData));
        Assert.assertTrue(s1.contains(myOtherReferenceData));
        s2.evict(myReferenceData);
        s2.evict(myOtherReferenceData);
        Assert.assertFalse(s2.contains(myReferenceData));
        Assert.assertFalse(s2.contains(myOtherReferenceData));
        s2.getTransaction().commit();
        s2.close();
        // still associated with s1
        Assert.assertTrue(s1.contains(myReferenceData));
        Assert.assertTrue(s1.contains(myOtherReferenceData));
        s1.clear();
        Assert.assertFalse(s1.contains(myReferenceData));
        Assert.assertFalse(s1.contains(myOtherReferenceData));
        s1.close();
        // EntityEntry should still be set
        Assert.assertNotNull(myReferenceData.$$_hibernate_getEntityEntry());
        Assert.assertNotNull(myOtherReferenceData.$$_hibernate_getEntityEntry());
        // load them into 2 sessions
        s1 = openSession();
        s1.getTransaction().begin();
        Assert.assertSame(myReferenceData, s1.get(ByteCodeEnhancedImmutableReferenceCacheTest.MyEnhancedReferenceData.class, myReferenceData.getId()));
        Assert.assertSame(myOtherReferenceData, s1.get(ByteCodeEnhancedImmutableReferenceCacheTest.MyEnhancedReferenceData.class, myOtherReferenceData.getId()));
        s2 = openSession();
        s2.getTransaction().begin();
        Assert.assertSame(myReferenceData, s2.get(ByteCodeEnhancedImmutableReferenceCacheTest.MyEnhancedReferenceData.class, myReferenceData.getId()));
        Assert.assertSame(myOtherReferenceData, s2.get(ByteCodeEnhancedImmutableReferenceCacheTest.MyEnhancedReferenceData.class, myOtherReferenceData.getId()));
        Assert.assertEquals(READ_ONLY, myReferenceData.$$_hibernate_getEntityEntry().getStatus());
        Assert.assertEquals(READ_ONLY, myOtherReferenceData.$$_hibernate_getEntityEntry().getStatus());
        // delete myReferenceData from s1
        s1.delete(myReferenceData);
        Assert.assertEquals(DELETED, myReferenceData.$$_hibernate_getEntityEntry().getStatus());
        Assert.assertEquals(READ_ONLY, myOtherReferenceData.$$_hibernate_getEntityEntry().getStatus());
        // delete myOtherReferenceData from s2
        s2.delete(myOtherReferenceData);
        Assert.assertEquals(DELETED, myReferenceData.$$_hibernate_getEntityEntry().getStatus());
        Assert.assertEquals(DELETED, myOtherReferenceData.$$_hibernate_getEntityEntry().getStatus());
        s1.getTransaction().commit();
        s1.close();
        Assert.assertEquals(GONE, myReferenceData.$$_hibernate_getEntityEntry().getStatus());
        Assert.assertEquals(DELETED, myOtherReferenceData.$$_hibernate_getEntityEntry().getStatus());
        s2.getTransaction().commit();
        s2.close();
        Assert.assertEquals(GONE, myReferenceData.$$_hibernate_getEntityEntry().getStatus());
        Assert.assertEquals(GONE, myOtherReferenceData.$$_hibernate_getEntityEntry().getStatus());
    }

    @Entity(name = "MyEnhancedReferenceData")
    @Immutable
    @Cacheable
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    @Proxy(lazy = false)
    @SuppressWarnings("UnusedDeclaration")
    public static class MyEnhancedReferenceData implements ManagedEntity {
        @Id
        private Integer id;

        private String name;

        private String theValue;

        @Transient
        private transient EntityEntry entityEntry;

        @Transient
        private transient ManagedEntity previous;

        @Transient
        private transient ManagedEntity next;

        public MyEnhancedReferenceData(Integer id, String name, String theValue) {
            this.id = id;
            this.name = name;
            this.theValue = theValue;
        }

        protected MyEnhancedReferenceData() {
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

        @Override
        public Object $$_hibernate_getEntityInstance() {
            return this;
        }

        @Override
        public EntityEntry $$_hibernate_getEntityEntry() {
            return entityEntry;
        }

        @Override
        public void $$_hibernate_setEntityEntry(EntityEntry entityEntry) {
            this.entityEntry = entityEntry;
        }

        @Override
        public ManagedEntity $$_hibernate_getNextManagedEntity() {
            return next;
        }

        @Override
        public void $$_hibernate_setNextManagedEntity(ManagedEntity next) {
            this.next = next;
        }

        @Override
        public ManagedEntity $$_hibernate_getPreviousManagedEntity() {
            return previous;
        }

        @Override
        public void $$_hibernate_setPreviousManagedEntity(ManagedEntity previous) {
            this.previous = previous;
        }
    }
}

