/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.collection.delayedOperation;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
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
public class BagDelayedOperationTest extends BaseCoreFunctionalTestCase {
    private Long parentId;

    @Test
    @TestForIssue(jiraKey = "HHH-5855")
    public void testSimpleAddDetached() {
        // Create 2 detached Child objects.
        Session s = openSession();
        s.getTransaction().begin();
        BagDelayedOperationTest.Child c1 = new BagDelayedOperationTest.Child("Darwin");
        s.persist(c1);
        BagDelayedOperationTest.Child c2 = new BagDelayedOperationTest.Child("Comet");
        s.persist(c2);
        s.getTransaction().commit();
        s.close();
        // Now Child c is detached.
        s = openSession();
        s.getTransaction().begin();
        BagDelayedOperationTest.Parent p = s.get(BagDelayedOperationTest.Parent.class, parentId);
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        // add detached Child c
        p.addChild(c1);
        // collection should still be uninitialized
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        s.getTransaction().commit();
        s.close();
        // Add a detached Child and commit
        s = openSession();
        s.getTransaction().begin();
        p = s.get(BagDelayedOperationTest.Parent.class, parentId);
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        Assert.assertEquals(3, p.getChildren().size());
        s.getTransaction().commit();
        s.close();
        // Add another detached Child, merge, and commit
        s = openSession();
        s.getTransaction().begin();
        p = s.get(BagDelayedOperationTest.Parent.class, parentId);
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        p.addChild(c2);
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        p = ((BagDelayedOperationTest.Parent) (s.merge(p)));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        p = s.get(BagDelayedOperationTest.Parent.class, parentId);
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        Assert.assertEquals(4, p.getChildren().size());
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-5855")
    public void testSimpleAddTransient() {
        // Add a transient Child and commit.
        Session s = openSession();
        s.getTransaction().begin();
        BagDelayedOperationTest.Parent p = s.get(BagDelayedOperationTest.Parent.class, parentId);
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        // add transient Child
        p.addChild(new BagDelayedOperationTest.Child("Darwin"));
        // collection should still be uninitialized
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        p = s.get(BagDelayedOperationTest.Parent.class, parentId);
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        Assert.assertEquals(3, p.getChildren().size());
        s.getTransaction().commit();
        s.close();
        // Add another transient Child and commit again.
        s = openSession();
        s.getTransaction().begin();
        p = s.get(BagDelayedOperationTest.Parent.class, parentId);
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        // add transient Child
        p.addChild(new BagDelayedOperationTest.Child("Comet"));
        // collection should still be uninitialized
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        s.merge(p);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        p = s.get(BagDelayedOperationTest.Parent.class, parentId);
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        Assert.assertEquals(4, p.getChildren().size());
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-5855")
    public void testSimpleAddManaged() {
        // Add 2 Child entities
        Session s = openSession();
        s.getTransaction().begin();
        BagDelayedOperationTest.Child c1 = new BagDelayedOperationTest.Child("Darwin");
        s.persist(c1);
        BagDelayedOperationTest.Child c2 = new BagDelayedOperationTest.Child("Comet");
        s.persist(c2);
        s.getTransaction().commit();
        s.close();
        // Add a managed Child and commit
        s = openSession();
        s.getTransaction().begin();
        BagDelayedOperationTest.Parent p = s.get(BagDelayedOperationTest.Parent.class, parentId);
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        // get the first Child so it is managed; add to collection
        p.addChild(s.get(BagDelayedOperationTest.Child.class, c1.getId()));
        // collection should still be uninitialized
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        p = s.get(BagDelayedOperationTest.Parent.class, parentId);
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        Assert.assertEquals(3, p.getChildren().size());
        s.getTransaction().commit();
        s.close();
        // Add the other managed Child, merge and commit.
        s = openSession();
        s.getTransaction().begin();
        p = s.get(BagDelayedOperationTest.Parent.class, parentId);
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        // get the second Child so it is managed; add to collection
        p.addChild(s.get(BagDelayedOperationTest.Child.class, c2.getId()));
        // collection should still be uninitialized
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        s.merge(p);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        p = s.get(BagDelayedOperationTest.Parent.class, parentId);
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
        BagDelayedOperationTest.Parent p = s.get(BagDelayedOperationTest.Parent.class, parentId);
        Assert.assertFalse(Hibernate.isInitialized(p.getChildren()));
        // initialize
        Hibernate.initialize(p.getChildren());
        Assert.assertTrue(Hibernate.isInitialized(p.getChildren()));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        p = ((BagDelayedOperationTest.Parent) (s.merge(p)));
        Assert.assertTrue(Hibernate.isInitialized(p.getChildren()));
        BagDelayedOperationTest.Child c = new BagDelayedOperationTest.Child("Zeke");
        c.setParent(p);
        s.persist(c);
        p.getChildren().size();
        p.getChildren().add(c);
        s.getTransaction().commit();
        s.close();
        // Merge detached Parent with initialized children
        s = openSession();
        s.getTransaction().begin();
        p = ((BagDelayedOperationTest.Parent) (s.merge(p)));
        // after merging, p#children will be initialized
        Assert.assertTrue(Hibernate.isInitialized(p.getChildren()));
        Assert.assertFalse(hasQueuedOperations());
        s.getTransaction().commit();
        s.close();
        // Merge detached Parent
        s = openSession();
        s.getTransaction().begin();
        p = ((BagDelayedOperationTest.Parent) (s.merge(p)));
        Assert.assertTrue(Hibernate.isInitialized(p.getChildren()));
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
        @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
        private List<BagDelayedOperationTest.Child> children = new ArrayList<BagDelayedOperationTest.Child>();

        public Parent() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public List<BagDelayedOperationTest.Child> getChildren() {
            return children;
        }

        public void addChild(BagDelayedOperationTest.Child child) {
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
        private BagDelayedOperationTest.Parent parent;

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

        public BagDelayedOperationTest.Parent getParent() {
            return parent;
        }

        public void setParent(BagDelayedOperationTest.Parent parent) {
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
            BagDelayedOperationTest.Child child = ((BagDelayedOperationTest.Child) (o));
            return name.equals(child.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }
}

