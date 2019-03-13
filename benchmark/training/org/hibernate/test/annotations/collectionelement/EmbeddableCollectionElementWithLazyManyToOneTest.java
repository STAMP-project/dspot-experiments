/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.collectionelement;


import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.Hibernate;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class EmbeddableCollectionElementWithLazyManyToOneTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "???")
    public void testLazyManyToOneInEmbeddable() {
        EmbeddableCollectionElementWithLazyManyToOneTest.Parent p = new EmbeddableCollectionElementWithLazyManyToOneTest.Parent();
        p.containedChild = new EmbeddableCollectionElementWithLazyManyToOneTest.ContainedChild(new EmbeddableCollectionElementWithLazyManyToOneTest.Child());
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(p);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.annotations.collectionelement.Parent pRead = session.get(.class, p.id);
            assertFalse(Hibernate.isInitialized(pRead.containedChild.child));
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.delete(p);
        });
    }

    @Test
    @TestForIssue(jiraKey = "???")
    public void testLazyManyToOneInCollectionElementEmbeddable() {
        EmbeddableCollectionElementWithLazyManyToOneTest.Parent p = new EmbeddableCollectionElementWithLazyManyToOneTest.Parent();
        p.containedChildren.add(new EmbeddableCollectionElementWithLazyManyToOneTest.ContainedChild(new EmbeddableCollectionElementWithLazyManyToOneTest.Child()));
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(p);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.annotations.collectionelement.Parent pRead = session.get(.class, p.id);
            assertFalse(Hibernate.isInitialized(pRead.containedChildren));
            assertEquals(1, pRead.containedChildren.size());
            assertTrue(Hibernate.isInitialized(pRead.containedChildren));
            assertFalse(Hibernate.isInitialized(pRead.containedChildren.iterator().next().child));
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.delete(p);
        });
    }

    @Test
    @TestForIssue(jiraKey = "???")
    public void testLazyBoth() {
        EmbeddableCollectionElementWithLazyManyToOneTest.Parent p = new EmbeddableCollectionElementWithLazyManyToOneTest.Parent();
        EmbeddableCollectionElementWithLazyManyToOneTest.ContainedChild containedChild = new EmbeddableCollectionElementWithLazyManyToOneTest.ContainedChild(new EmbeddableCollectionElementWithLazyManyToOneTest.Child());
        p.containedChild = containedChild;
        p.containedChildren.add(containedChild);
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(p);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.annotations.collectionelement.Parent pRead = session.get(.class, p.id);
            assertFalse(Hibernate.isInitialized(pRead.containedChild.child));
            assertFalse(Hibernate.isInitialized(pRead.containedChildren));
            assertEquals(1, pRead.containedChildren.size());
            assertTrue(Hibernate.isInitialized(pRead.containedChildren));
            assertFalse(Hibernate.isInitialized(pRead.containedChildren.iterator().next().child));
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.delete(p);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-13045")
    public void testAccessIdOfManyToOneInEmbeddable() {
        EmbeddableCollectionElementWithLazyManyToOneTest.Parent p = new EmbeddableCollectionElementWithLazyManyToOneTest.Parent();
        p.containedChildren.add(new EmbeddableCollectionElementWithLazyManyToOneTest.ContainedChild(new EmbeddableCollectionElementWithLazyManyToOneTest.Child()));
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(p);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            assertFalse(session.createQuery("from Parent p join p.containedChildren c where c.child.id is not null").getResultList().isEmpty());
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.delete(p);
        });
    }

    @Entity(name = "Parent")
    public static class Parent {
        @Id
        @GeneratedValue
        private int id;

        private EmbeddableCollectionElementWithLazyManyToOneTest.ContainedChild containedChild;

        @ElementCollection
        private Set<EmbeddableCollectionElementWithLazyManyToOneTest.ContainedChild> containedChildren = new HashSet<EmbeddableCollectionElementWithLazyManyToOneTest.ContainedChild>();
    }

    @Entity(name = "Child")
    public static class Child {
        @Id
        @GeneratedValue
        private int id;
    }

    @Embeddable
    public static class ContainedChild {
        @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        private EmbeddableCollectionElementWithLazyManyToOneTest.Child child;

        ContainedChild() {
        }

        ContainedChild(EmbeddableCollectionElementWithLazyManyToOneTest.Child child) {
            this.child = child;
        }
    }
}

