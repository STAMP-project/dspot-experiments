/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.test.collection.multisession;


import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class MultipleSessionCollectionTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-9518")
    public void testSaveOrUpdateOwnerWithCollectionInNewSessionBeforeFlush() {
        MultipleSessionCollectionTest.Parent p = new MultipleSessionCollectionTest.Parent();
        MultipleSessionCollectionTest.Child c = new MultipleSessionCollectionTest.Child();
        p.children.add(c);
        Session s1 = openSession();
        s1.getTransaction().begin();
        s1.saveOrUpdate(p);
        // try to save the same entity in a new session before flushing the first session
        Session s2 = openSession();
        s2.getTransaction().begin();
        try {
            s2.saveOrUpdate(p);
            s2.getTransaction().commit();
            Assert.fail("should have thrown HibernateException");
        } catch (HibernateException ex) {
            log.error(ex);
            s2.getTransaction().rollback();
        } finally {
            s2.close();
        }
        // should still be able to commit in first session
        s1.getTransaction().commit();
        s1.close();
        s1 = openSession();
        s1.getTransaction().begin();
        MultipleSessionCollectionTest.Parent pGet = s1.get(MultipleSessionCollectionTest.Parent.class, p.id);
        Assert.assertEquals(c.id, pGet.children.iterator().next().id);
        session.delete(pGet);
        s1.getTransaction().commit();
        session.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9518")
    public void testSaveOrUpdateOwnerWithCollectionInNewSessionAfterFlush() {
        MultipleSessionCollectionTest.Parent p = new MultipleSessionCollectionTest.Parent();
        MultipleSessionCollectionTest.Child c = new MultipleSessionCollectionTest.Child();
        p.children.add(c);
        Session s1 = openSession();
        s1.getTransaction().begin();
        s1.saveOrUpdate(p);
        s1.flush();
        // try to save the same entity in a new session after flushing the first session
        Session s2 = openSession();
        s2.getTransaction().begin();
        try {
            s2.saveOrUpdate(p);
            s2.getTransaction().commit();
            Assert.fail("should have thrown HibernateException");
        } catch (HibernateException ex) {
            log.error(ex);
            s2.getTransaction().rollback();
        } finally {
            s2.close();
        }
        // should still be able to commit in first session
        s1.getTransaction().commit();
        s1.close();
        s1 = openSession();
        s1.getTransaction().begin();
        MultipleSessionCollectionTest.Parent pGet = s1.get(MultipleSessionCollectionTest.Parent.class, p.id);
        Assert.assertEquals(c.id, pGet.children.iterator().next().id);
        session.delete(pGet);
        s1.getTransaction().commit();
        session.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9518")
    public void testSaveOrUpdateOwnerWithUninitializedCollectionInNewSession() {
        MultipleSessionCollectionTest.Parent p = new MultipleSessionCollectionTest.Parent();
        MultipleSessionCollectionTest.Child c = new MultipleSessionCollectionTest.Child();
        p.children.add(c);
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(p);
        s.getTransaction().commit();
        s.close();
        Session s1 = openSession();
        s1.getTransaction().begin();
        p = s1.get(MultipleSessionCollectionTest.Parent.class, p.id);
        Assert.assertFalse(Hibernate.isInitialized(p.children));
        // try to save the same entity (with an uninitialized collection) in a new session
        Session s2 = openSession();
        s2.getTransaction().begin();
        try {
            s2.saveOrUpdate(p);
            s2.getTransaction().commit();
            Assert.fail("should have thrown HibernateException");
        } catch (HibernateException ex) {
            log.error(ex);
            s2.getTransaction().rollback();
        } finally {
            s2.close();
        }
        // should still be able to initialize collection, modify and commit in first session
        Assert.assertFalse(Hibernate.isInitialized(p.children));
        Hibernate.initialize(p.children);
        p.children.add(new MultipleSessionCollectionTest.Child());
        s1.getTransaction().commit();
        s1.close();
        s1 = openSession();
        s1.getTransaction().begin();
        MultipleSessionCollectionTest.Parent pGet = s1.get(MultipleSessionCollectionTest.Parent.class, p.id);
        Assert.assertEquals(2, pGet.children.size());
        session.delete(pGet);
        s1.getTransaction().commit();
        session.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9518")
    public void testSaveOrUpdateOwnerWithInitializedCollectionInNewSession() {
        MultipleSessionCollectionTest.Parent p = new MultipleSessionCollectionTest.Parent();
        MultipleSessionCollectionTest.Child c = new MultipleSessionCollectionTest.Child();
        p.children.add(c);
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(p);
        s.getTransaction().commit();
        s.close();
        Session s1 = openSession();
        s1.getTransaction().begin();
        p = s1.get(MultipleSessionCollectionTest.Parent.class, p.id);
        Hibernate.initialize(p.children);
        // try to save the same entity (with an initialized collection) in a new session
        Session s2 = openSession();
        s2.getTransaction().begin();
        try {
            s2.saveOrUpdate(p);
            s2.getTransaction().commit();
            Assert.fail("should have thrown HibernateException");
        } catch (HibernateException ex) {
            log.error(ex);
            s2.getTransaction().rollback();
        } finally {
            s2.close();
        }
        // should still be able to commit in first session
        s1.getTransaction().commit();
        s1.close();
        s1 = openSession();
        s1.getTransaction().begin();
        MultipleSessionCollectionTest.Parent pGet = s1.get(MultipleSessionCollectionTest.Parent.class, p.id);
        Assert.assertEquals(c.id, pGet.children.iterator().next().id);
        session.delete(pGet);
        s1.getTransaction().commit();
        session.close();
    }

    // 
    @Test
    @TestForIssue(jiraKey = "HHH-9518")
    public void testCopyPersistentCollectionReferenceBeforeFlush() {
        MultipleSessionCollectionTest.Parent p = new MultipleSessionCollectionTest.Parent();
        MultipleSessionCollectionTest.Child c = new MultipleSessionCollectionTest.Child();
        p.children.add(c);
        Session s1 = openSession();
        s1.getTransaction().begin();
        s1.persist(p);
        // Copy p.children into a different Parent before flush and try to save in new session.
        MultipleSessionCollectionTest.Parent pWithSameChildren = new MultipleSessionCollectionTest.Parent();
        pWithSameChildren.children = p.children;
        Session s2 = openSession();
        s2.getTransaction().begin();
        try {
            s2.saveOrUpdate(pWithSameChildren);
            s2.getTransaction().commit();
            Assert.fail("should have thrown HibernateException");
        } catch (HibernateException ex) {
            log.error(ex);
            s2.getTransaction().rollback();
        } finally {
            s2.close();
        }
        // should still be able to commit in first session
        s1.getTransaction().commit();
        s1.close();
        s1 = openSession();
        s1.getTransaction().begin();
        MultipleSessionCollectionTest.Parent pGet = s1.get(MultipleSessionCollectionTest.Parent.class, p.id);
        Assert.assertEquals(c.id, pGet.children.iterator().next().id);
        session.delete(pGet);
        s1.getTransaction().commit();
        session.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9518")
    public void testCopyPersistentCollectionReferenceAfterFlush() {
        MultipleSessionCollectionTest.Parent p = new MultipleSessionCollectionTest.Parent();
        MultipleSessionCollectionTest.Child c = new MultipleSessionCollectionTest.Child();
        p.children.add(c);
        Session s1 = openSession();
        s1.getTransaction().begin();
        s1.persist(p);
        s1.flush();
        // Copy p.children into a different Parent after flush and try to save in new session.
        MultipleSessionCollectionTest.Parent pWithSameChildren = new MultipleSessionCollectionTest.Parent();
        pWithSameChildren.children = p.children;
        Session s2 = openSession();
        s2.getTransaction().begin();
        try {
            s2.saveOrUpdate(pWithSameChildren);
            s2.getTransaction().commit();
            Assert.fail("should have thrown HibernateException");
        } catch (HibernateException ex) {
            log.error(ex);
            s2.getTransaction().rollback();
        } finally {
            s2.close();
        }
        // should still be able to commit in first session
        s1.getTransaction().commit();
        s1.close();
        s1 = openSession();
        s1.getTransaction().begin();
        MultipleSessionCollectionTest.Parent pGet = s1.get(MultipleSessionCollectionTest.Parent.class, p.id);
        Assert.assertEquals(c.id, pGet.children.iterator().next().id);
        session.delete(pGet);
        s1.getTransaction().commit();
        session.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9518")
    public void testCopyUninitializedCollectionReferenceAfterGet() {
        MultipleSessionCollectionTest.Parent p = new MultipleSessionCollectionTest.Parent();
        MultipleSessionCollectionTest.Child c = new MultipleSessionCollectionTest.Child();
        p.children.add(c);
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(p);
        s.getTransaction().commit();
        s.close();
        Session s1 = openSession();
        s1.getTransaction().begin();
        p = s1.get(MultipleSessionCollectionTest.Parent.class, p.id);
        Assert.assertFalse(Hibernate.isInitialized(p.children));
        // Copy p.children (uninitialized) into a different Parent and try to save in new session.
        MultipleSessionCollectionTest.Parent pWithSameChildren = new MultipleSessionCollectionTest.Parent();
        pWithSameChildren.children = p.children;
        Session s2 = openSession();
        s2.getTransaction().begin();
        try {
            s2.saveOrUpdate(pWithSameChildren);
            s2.getTransaction().commit();
            Assert.fail("should have thrown HibernateException");
        } catch (HibernateException ex) {
            log.error(ex);
            s2.getTransaction().rollback();
        } finally {
            s2.close();
        }
        // should still be able to commit in first session
        s1.getTransaction().commit();
        s1.close();
        s1 = openSession();
        s1.getTransaction().begin();
        MultipleSessionCollectionTest.Parent pGet = s1.get(MultipleSessionCollectionTest.Parent.class, p.id);
        Assert.assertEquals(c.id, pGet.children.iterator().next().id);
        session.delete(pGet);
        s1.getTransaction().commit();
        session.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9518")
    public void testCopyInitializedCollectionReferenceAfterGet() {
        MultipleSessionCollectionTest.Parent p = new MultipleSessionCollectionTest.Parent();
        MultipleSessionCollectionTest.Child c = new MultipleSessionCollectionTest.Child();
        p.children.add(c);
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(p);
        s.getTransaction().commit();
        s.close();
        Session s1 = openSession();
        s1.getTransaction().begin();
        p = s1.get(MultipleSessionCollectionTest.Parent.class, p.id);
        Hibernate.initialize(p.children);
        // Copy p.children (initialized) into a different Parent.children and try to save in new session.
        MultipleSessionCollectionTest.Parent pWithSameChildren = new MultipleSessionCollectionTest.Parent();
        pWithSameChildren.children = p.children;
        Session s2 = openSession();
        s2.getTransaction().begin();
        try {
            s2.saveOrUpdate(pWithSameChildren);
            s2.getTransaction().commit();
            Assert.fail("should have thrown HibernateException");
        } catch (HibernateException ex) {
            log.error(ex);
            s2.getTransaction().rollback();
        } finally {
            s2.close();
        }
        // should still be able to commit in first session
        s1.getTransaction().commit();
        s1.close();
        s1 = openSession();
        s1.getTransaction().begin();
        MultipleSessionCollectionTest.Parent pGet = s1.get(MultipleSessionCollectionTest.Parent.class, p.id);
        Assert.assertEquals(c.id, pGet.children.iterator().next().id);
        session.delete(pGet);
        s1.getTransaction().commit();
        session.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9518")
    public void testCopyInitializedCollectionReferenceToNewEntityCollectionRoleAfterGet() {
        MultipleSessionCollectionTest.Parent p = new MultipleSessionCollectionTest.Parent();
        MultipleSessionCollectionTest.Child c = new MultipleSessionCollectionTest.Child();
        p.children.add(c);
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(p);
        s.getTransaction().commit();
        s.close();
        Session s1 = openSession();
        s1.getTransaction().begin();
        p = s1.get(MultipleSessionCollectionTest.Parent.class, p.id);
        Hibernate.initialize(p.children);
        // Copy p.children (initialized) into a different Parent.oldChildren (note different collection role)
        // and try to save in new session.
        MultipleSessionCollectionTest.Parent pWithSameChildren = new MultipleSessionCollectionTest.Parent();
        pWithSameChildren.oldChildren = p.children;
        Session s2 = openSession();
        s2.getTransaction().begin();
        try {
            s2.saveOrUpdate(pWithSameChildren);
            s2.getTransaction().commit();
            Assert.fail("should have thrown HibernateException");
        } catch (HibernateException ex) {
            log.error(ex);
            s2.getTransaction().rollback();
        } finally {
            s2.close();
        }
        // should still be able to commit in first session
        s1.getTransaction().commit();
        s1.close();
        s1 = openSession();
        s1.getTransaction().begin();
        MultipleSessionCollectionTest.Parent pGet = s1.get(MultipleSessionCollectionTest.Parent.class, p.id);
        Assert.assertEquals(c.id, pGet.children.iterator().next().id);
        session.delete(pGet);
        s1.getTransaction().commit();
        session.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9518")
    public void testDeleteCommitCopyToNewOwnerInNewSession() {
        MultipleSessionCollectionTest.Parent p1 = new MultipleSessionCollectionTest.Parent();
        p1.nickNames.add("nick");
        MultipleSessionCollectionTest.Parent p2 = new MultipleSessionCollectionTest.Parent();
        Session s1 = openSession();
        s1.getTransaction().begin();
        s1.save(p1);
        s1.save(p2);
        s1.getTransaction().commit();
        s1.close();
        s1 = openSession();
        s1.getTransaction().begin();
        s1.delete(p1);
        s1.flush();
        s1.getTransaction().commit();
        // need to commit after flushing; otherwise, will get lock failure when try to move the collection below
        Assert.assertNull(getPersistenceContext().getEntry(p1));
        CollectionEntry ceChildren = getPersistenceContext().getCollectionEntry(((PersistentCollection) (p1.children)));
        CollectionEntry ceNickNames = getPersistenceContext().getCollectionEntry(((PersistentCollection) (p1.nickNames)));
        Assert.assertNull(ceChildren);
        Assert.assertNull(ceNickNames);
        Assert.assertNull(getSession());
        Assert.assertNull(getSession());
        Assert.assertNull(getSession());
        Assert.assertNull(getSession());
        // Assign the deleted collection to a different entity with same collection role (p2.nickNames)
        p2.nickNames = p1.nickNames;
        Session s2 = openSession();
        s2.getTransaction().begin();
        s2.saveOrUpdate(p2);
        s2.getTransaction().commit();
        s2.close();
        s1.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9518")
    public void testDeleteCommitCopyToNewOwnerNewCollectionRoleInNewSession() {
        MultipleSessionCollectionTest.Parent p1 = new MultipleSessionCollectionTest.Parent();
        p1.nickNames.add("nick");
        MultipleSessionCollectionTest.Parent p2 = new MultipleSessionCollectionTest.Parent();
        Session s1 = openSession();
        s1.getTransaction().begin();
        s1.save(p1);
        s1.save(p2);
        s1.getTransaction().commit();
        s1.close();
        s1 = openSession();
        s1.getTransaction().begin();
        s1.delete(p1);
        s1.flush();
        s1.getTransaction().commit();
        // need to commit after flushing; otherwise, will get lock failure when try to move the collection below
        Assert.assertNull(getPersistenceContext().getEntry(p1));
        CollectionEntry ceChildren = getPersistenceContext().getCollectionEntry(((PersistentCollection) (p1.children)));
        CollectionEntry ceNickNames = getPersistenceContext().getCollectionEntry(((PersistentCollection) (p1.nickNames)));
        Assert.assertNull(ceChildren);
        Assert.assertNull(ceNickNames);
        Assert.assertNull(getSession());
        Assert.assertNull(getSession());
        Assert.assertNull(getSession());
        Assert.assertNull(getSession());
        // Assign the deleted collection to a different entity with different collection role (p2.oldNickNames)
        p2.oldNickNames = p1.nickNames;
        Session s2 = openSession();
        s2.getTransaction().begin();
        s2.saveOrUpdate(p2);
        s2.getTransaction().commit();
        s2.close();
        s1.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9518")
    public void testDeleteCopyToNewOwnerInNewSessionBeforeFlush() {
        MultipleSessionCollectionTest.Parent p1 = new MultipleSessionCollectionTest.Parent();
        p1.nickNames.add("nick");
        MultipleSessionCollectionTest.Parent p2 = new MultipleSessionCollectionTest.Parent();
        Session s1 = openSession();
        s1.getTransaction().begin();
        s1.save(p1);
        s1.save(p2);
        s1.getTransaction().commit();
        s1.close();
        s1 = openSession();
        s1.getTransaction().begin();
        s1.delete(p1);
        // Assign the deleted collection to a different entity with same collection role (p2.nickNames)
        // before committing delete.
        p2.nickNames = p1.nickNames;
        Session s2 = openSession();
        s2.getTransaction().begin();
        try {
            s2.saveOrUpdate(p2);
            Assert.fail("should have thrown HibernateException");
        } catch (HibernateException ex) {
            log.error(ex);
            s2.getTransaction().rollback();
        } finally {
            s2.close();
        }
        // should still be able to commit the original delete
        s1.getTransaction().commit();
        s1.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9518")
    public void testDeleteCopyToNewOwnerNewCollectionRoleInNewSessionBeforeFlush() {
        MultipleSessionCollectionTest.Parent p1 = new MultipleSessionCollectionTest.Parent();
        p1.nickNames.add("nick");
        MultipleSessionCollectionTest.Parent p2 = new MultipleSessionCollectionTest.Parent();
        Session s1 = openSession();
        s1.getTransaction().begin();
        s1.save(p1);
        s1.save(p2);
        s1.getTransaction().commit();
        s1.close();
        s1 = openSession();
        s1.getTransaction().begin();
        s1.delete(p1);
        // Assign the deleted collection to a different entity with different collection role (p2.oldNickNames)
        // before committing delete.
        p2.oldNickNames = p1.nickNames;
        Session s2 = openSession();
        s2.getTransaction().begin();
        try {
            s2.saveOrUpdate(p2);
            Assert.fail("should have thrown HibernateException");
        } catch (HibernateException ex) {
            log.error(ex);
            s2.getTransaction().rollback();
        } finally {
            s2.close();
        }
        // should still be able to commit the original delete
        s1.getTransaction().commit();
        s1.close();
    }

    @Entity
    @Table(name = "Parent")
    public static class Parent {
        @Id
        @GeneratedValue
        private Long id;

        @ElementCollection
        private Set<String> nickNames = new HashSet<String>();

        @ElementCollection
        private Set<String> oldNickNames = new HashSet<String>();

        @OneToMany(cascade = CascadeType.ALL)
        @JoinColumn
        private Set<MultipleSessionCollectionTest.Child> children = new HashSet<MultipleSessionCollectionTest.Child>();

        @OneToMany(cascade = CascadeType.ALL)
        @JoinColumn
        private Set<MultipleSessionCollectionTest.Child> oldChildren = new HashSet<MultipleSessionCollectionTest.Child>();
    }

    @Entity
    @Table(name = "Child")
    public static class Child {
        @Id
        @GeneratedValue
        private Long id;
    }
}

