/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.collection.delayedOperation;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests delayed operations that are queued for a PersistentBag. The Bag does not have
 * to be extra-lazy to queue the operations.
 *
 * @author Gail Badner
 */
public class BagDelayedOperationNoCascadeTest extends BaseCoreFunctionalTestCase {
    private Long parentId;

    @Test
    @TestForIssue(jiraKey = "HHH-5855")
    public void testSimpleAddManaged() {
        // Add 2 Child entities
        Session s = openSession();
        s.getTransaction().begin();
        BagDelayedOperationNoCascadeTest.Child c1 = new BagDelayedOperationNoCascadeTest.Child("Darwin");
        s.persist(c1);
        BagDelayedOperationNoCascadeTest.Child c2 = new BagDelayedOperationNoCascadeTest.Child("Comet");
        s.persist(c2);
        s.getTransaction().commit();
        s.close();
        // Add a managed Child and commit
        s = openSession();
        s.getTransaction().begin();
        BagDelayedOperationNoCascadeTest.Parent p = s.get(BagDelayedOperationNoCascadeTest.Parent.class, parentId);
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        // get the first Child so it is managed; add to collection
        p.addChild(s.get(BagDelayedOperationNoCascadeTest.Child.class, c1.getId()));
        // collection should still be uninitialized
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        p = s.get(BagDelayedOperationNoCascadeTest.Parent.class, parentId);
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        Assert.assertEquals(3, p.getChildren().size());
        s.getTransaction().commit();
        s.close();
        // Add the other managed Child, merge and commit.
        s = openSession();
        s.getTransaction().begin();
        p = s.get(BagDelayedOperationNoCascadeTest.Parent.class, parentId);
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        // get the second Child so it is managed; add to collection
        p.addChild(s.get(BagDelayedOperationNoCascadeTest.Child.class, c2.getId()));
        // collection should still be uninitialized
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        s.merge(p);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        p = s.get(BagDelayedOperationNoCascadeTest.Parent.class, parentId);
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        Assert.assertEquals(4, p.getChildren().size());
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11209")
    public void testMergeInitializedBagAndRemerge() {
        Session s = openSession();
        s.getTransaction().begin();
        BagDelayedOperationNoCascadeTest.Parent p = s.get(BagDelayedOperationNoCascadeTest.Parent.class, parentId);
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        // initialize
        Hibernate.initialize(p.getChildren());
        Assert.assertTrue(Hibernate.isInitialized(p.getChildren()));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        p = ((BagDelayedOperationNoCascadeTest.Parent) (s.merge(p)));
        BagDelayedOperationNoCascadeTest.Child c = new BagDelayedOperationNoCascadeTest.Child("Zeke");
        c.setParent(p);
        s.persist(c);
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        p.getChildren().size();
        p.getChildren().add(c);
        s.getTransaction().commit();
        s.close();
        // Merge detached Parent with initialized children
        s = openSession();
        s.getTransaction().begin();
        p = ((BagDelayedOperationNoCascadeTest.Parent) (s.merge(p)));
        // after merging, p#children will be uninitialized
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        Assert.assertTrue(hasQueuedOperations());
        s.getTransaction().commit();
        Assert.assertFalse(hasQueuedOperations());
        s.close();
        // Merge detached Parent, now with uninitialized children no queued operations
        s = openSession();
        s.getTransaction().begin();
        p = ((BagDelayedOperationNoCascadeTest.Parent) (s.merge(p)));
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        Assert.assertFalse(hasQueuedOperations());
        s.getTransaction().commit();
        s.close();
    }

    @Entity(name = "Parent")
    public static class Parent {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        // Don't need extra-lazy to delay add operations to a bag.
        @OneToMany(mappedBy = "parent")
        private List<BagDelayedOperationNoCascadeTest.Child> children = new ArrayList<BagDelayedOperationNoCascadeTest.Child>();

        public Parent() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public List<BagDelayedOperationNoCascadeTest.Child> getChildren() {
            return children;
        }

        public void addChild(BagDelayedOperationNoCascadeTest.Child child) {
            children.add(child);
            child.setParent(this);
        }
    }

    @Entity(name = "Child")
    public static class Child {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        @Column(nullable = false)
        private String name;

        @ManyToOne
        private BagDelayedOperationNoCascadeTest.Parent parent;

        public Child() {
        }

        public Child(String name) {
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BagDelayedOperationNoCascadeTest.Parent getParent() {
            return parent;
        }

        public void setParent(BagDelayedOperationNoCascadeTest.Parent parent) {
            this.parent = parent;
        }

        @Override
        public String toString() {
            return ((((("Child{" + "id=") + (id)) + ", name='") + (name)) + '\'') + '}';
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            BagDelayedOperationNoCascadeTest.Child child = ((BagDelayedOperationNoCascadeTest.Child) (o));
            return name.equals(child.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }
}

