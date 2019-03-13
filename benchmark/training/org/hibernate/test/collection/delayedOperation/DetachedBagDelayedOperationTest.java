/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.collection.delayedOperation;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityExistsException;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.Hibernate;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.logger.LoggerInspectionRule;
import org.hibernate.testing.logger.Triggerable;
import org.hibernate.testing.transaction.TransactionUtil;
import org.hibernate.type.CollectionType;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 * Tests merge of detached PersistentBag
 *
 * @author Gail Badner
 */
public class DetachedBagDelayedOperationTest extends BaseCoreFunctionalTestCase {
    @Rule
    public LoggerInspectionRule logInspectionCollectionType = new LoggerInspectionRule(Logger.getMessageLogger(CoreMessageLogger.class, CollectionType.class.getName()));

    @Rule
    public LoggerInspectionRule logInspectionAbstractPersistentCollection = new LoggerInspectionRule(Logger.getMessageLogger(CoreMessageLogger.class, AbstractPersistentCollection.class.getName()));

    private Triggerable triggerableIgnoreQueuedOperationsOnMerge;

    private Triggerable triggerableQueuedOperationWhenAttachToSession;

    private Triggerable triggerableQueuedOperationWhenDetachFromSession;

    private Triggerable triggerableQueuedOperationOnRollback;

    @Test
    @TestForIssue(jiraKey = "HHH-11209")
    public void testMergeDetachedCollectionWithQueuedOperations() {
        final DetachedBagDelayedOperationTest.Parent pOriginal = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.delayedOperation.Parent p = session.get(.class, 1L);
            assertFalse(Hibernate.isInitialized(p.getChildren()));
            // initialize
            Hibernate.initialize(p.getChildren());
            assertTrue(Hibernate.isInitialized(p.getChildren()));
            return p;
        });
        final DetachedBagDelayedOperationTest.Parent pWithQueuedOperations = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.delayedOperation.Parent p = ((org.hibernate.test.collection.delayedOperation.Parent) (session.merge(pOriginal)));
            org.hibernate.test.collection.delayedOperation.Child c = new org.hibernate.test.collection.delayedOperation.Child("Zeke");
            c.setParent(p);
            session.persist(c);
            assertFalse(Hibernate.isInitialized(p.getChildren()));
            p.getChildren().add(c);
            assertFalse(Hibernate.isInitialized(p.getChildren()));
            assertTrue(hasQueuedOperations());
            checkTriggerablesNotTriggered();
            session.detach(p);
            assertTrue(triggerableQueuedOperationWhenDetachFromSession.wasTriggered());
            assertEquals("HHH000496: Detaching an uninitialized collection with queued operations from a session: [org.hibernate.test.collection.delayedOperation.DetachedBagDelayedOperationTest$Parent.children#1]", triggerableQueuedOperationWhenDetachFromSession.triggerMessage());
            triggerableQueuedOperationWhenDetachFromSession.reset();
            // Make sure nothing else got triggered
            checkTriggerablesNotTriggered();
            return p;
        });
        checkTriggerablesNotTriggered();
        Assert.assertTrue(hasQueuedOperations());
        // Merge detached Parent with uninitialized collection with queued operations
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            checkTriggerablesNotTriggered();
            assertFalse(triggerableIgnoreQueuedOperationsOnMerge.wasTriggered());
            org.hibernate.test.collection.delayedOperation.Parent p = ((org.hibernate.test.collection.delayedOperation.Parent) (session.merge(pWithQueuedOperations)));
            assertTrue(triggerableIgnoreQueuedOperationsOnMerge.wasTriggered());
            assertEquals("HHH000494: Attempt to merge an uninitialized collection with queued operations; queued operations will be ignored: [org.hibernate.test.collection.delayedOperation.DetachedBagDelayedOperationTest$Parent.children#1]", triggerableIgnoreQueuedOperationsOnMerge.triggerMessage());
            triggerableIgnoreQueuedOperationsOnMerge.reset();
            assertFalse(Hibernate.isInitialized(p.getChildren()));
            assertFalse(hasQueuedOperations());
            // When initialized, p.children will not include the new Child ("Zeke"),
            // because that Child was flushed without a parent before being detached
            // along with its parent.
            Hibernate.initialize(p.getChildren());
            final Set<String> childNames = new HashSet<String>(Arrays.asList(new String[]{ "Yogi", "Sherman" }));
            assertEquals(childNames.size(), p.getChildren().size());
            for (org.hibernate.test.collection.delayedOperation.Child child : p.getChildren()) {
                childNames.remove(child.getName());
            }
            assertEquals(0, childNames.size());
        });
        checkTriggerablesNotTriggered();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11209")
    public void testSaveOrUpdateDetachedCollectionWithQueuedOperations() {
        final DetachedBagDelayedOperationTest.Parent pOriginal = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.delayedOperation.Parent p = session.get(.class, 1L);
            assertFalse(Hibernate.isInitialized(p.getChildren()));
            // initialize
            Hibernate.initialize(p.getChildren());
            assertTrue(Hibernate.isInitialized(p.getChildren()));
            return p;
        });
        final DetachedBagDelayedOperationTest.Parent pAfterDetachWithQueuedOperations = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.delayedOperation.Parent p = ((org.hibernate.test.collection.delayedOperation.Parent) (session.merge(pOriginal)));
            org.hibernate.test.collection.delayedOperation.Child c = new org.hibernate.test.collection.delayedOperation.Child("Zeke");
            c.setParent(p);
            session.persist(c);
            assertFalse(Hibernate.isInitialized(p.getChildren()));
            p.getChildren().add(c);
            assertFalse(Hibernate.isInitialized(p.getChildren()));
            assertTrue(hasQueuedOperations());
            checkTriggerablesNotTriggered();
            session.detach(p);
            assertTrue(triggerableQueuedOperationWhenDetachFromSession.wasTriggered());
            assertEquals("HHH000496: Detaching an uninitialized collection with queued operations from a session: [org.hibernate.test.collection.delayedOperation.DetachedBagDelayedOperationTest$Parent.children#1]", triggerableQueuedOperationWhenDetachFromSession.triggerMessage());
            triggerableQueuedOperationWhenDetachFromSession.reset();
            // Make sure nothing else got triggered
            checkTriggerablesNotTriggered();
            return p;
        });
        checkTriggerablesNotTriggered();
        Assert.assertTrue(hasQueuedOperations());
        // Save detached Parent with uninitialized collection with queued operations
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            checkTriggerablesNotTriggered();
            assertFalse(triggerableQueuedOperationWhenAttachToSession.wasTriggered());
            session.saveOrUpdate(pAfterDetachWithQueuedOperations);
            assertTrue(triggerableQueuedOperationWhenAttachToSession.wasTriggered());
            assertEquals("HHH000495: Attaching an uninitialized collection with queued operations to a session: [org.hibernate.test.collection.delayedOperation.DetachedBagDelayedOperationTest$Parent.children#1]", triggerableQueuedOperationWhenAttachToSession.triggerMessage());
            triggerableQueuedOperationWhenAttachToSession.reset();
            // Make sure nothing else got triggered
            checkTriggerablesNotTriggered();
            assertFalse(Hibernate.isInitialized(pAfterDetachWithQueuedOperations.getChildren()));
            assertTrue(hasQueuedOperations());
            // Queued operations will be executed when the collection is initialized,
            // After initialization, the collection will contain the Child that was added as a
            // queued operation before being detached above.
            Hibernate.initialize(pAfterDetachWithQueuedOperations.getChildren());
            final Set<String> childNames = new HashSet<String>(Arrays.asList(new String[]{ "Yogi", "Sherman", "Zeke" }));
            assertEquals(childNames.size(), pAfterDetachWithQueuedOperations.getChildren().size());
            for (org.hibernate.test.collection.delayedOperation.Child child : pAfterDetachWithQueuedOperations.getChildren()) {
                childNames.remove(child.getName());
            }
            assertEquals(0, childNames.size());
        });
        checkTriggerablesNotTriggered();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11209")
    public void testCollectionWithQueuedOperationsOnRollback() {
        final DetachedBagDelayedOperationTest.Parent pOriginal = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.collection.delayedOperation.Parent p = session.get(.class, 1L);
            assertFalse(Hibernate.isInitialized(p.getChildren()));
            // initialize
            Hibernate.initialize(p.getChildren());
            assertTrue(Hibernate.isInitialized(p.getChildren()));
            return p;
        });
        try {
            TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
                org.hibernate.test.collection.delayedOperation.Parent p = ((org.hibernate.test.collection.delayedOperation.Parent) (session.merge(pOriginal)));
                org.hibernate.test.collection.delayedOperation.Child c = new org.hibernate.test.collection.delayedOperation.Child("Zeke");
                c.setParent(p);
                session.persist(c);
                assertFalse(Hibernate.isInitialized(p.getChildren()));
                p.getChildren().add(c);
                assertFalse(Hibernate.isInitialized(p.getChildren()));
                assertTrue(hasQueuedOperations());
                checkTriggerablesNotTriggered();
                // save a new Parent with the same ID to throw an exception.
                org.hibernate.test.collection.delayedOperation.Parent pDup = new org.hibernate.test.collection.delayedOperation.Parent();
                pDup.id = 1L;
                session.persist(pDup);
            });
            Assert.fail("should have thrown EntityExistsException");
        } catch (EntityExistsException expected) {
        }
        Assert.assertTrue(triggerableQueuedOperationOnRollback.wasTriggered());
        triggerableQueuedOperationOnRollback.reset();
        checkTriggerablesNotTriggered();
    }

    @Entity(name = "Parent")
    public static class Parent {
        @Id
        private Long id;

        // Don't need extra-lazy to delay add operations to a bag.
        @OneToMany(mappedBy = "parent", cascade = CascadeType.DETACH)
        private List<DetachedBagDelayedOperationTest.Child> children;

        public Parent() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public List<DetachedBagDelayedOperationTest.Child> getChildren() {
            return children;
        }

        public void addChild(DetachedBagDelayedOperationTest.Child child) {
            if ((children) == null) {
                children = new ArrayList<>();
            }
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
        private DetachedBagDelayedOperationTest.Parent parent;

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

        public DetachedBagDelayedOperationTest.Parent getParent() {
            return parent;
        }

        public void setParent(DetachedBagDelayedOperationTest.Parent parent) {
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
            DetachedBagDelayedOperationTest.Child child = ((DetachedBagDelayedOperationTest.Child) (o));
            return name.equals(child.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }
}

