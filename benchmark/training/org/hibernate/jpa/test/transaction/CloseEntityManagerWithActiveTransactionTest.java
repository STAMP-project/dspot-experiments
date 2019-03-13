/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.transaction;


import TestingJtaPlatformImpl.INSTANCE;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.transaction.Status;
import javax.transaction.TransactionManager;
import org.hamcrest.core.Is;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.internal.JdbcCoordinatorImpl;
import org.hibernate.internal.SessionImpl;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class CloseEntityManagerWithActiveTransactionTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-10942")
    public void testPersistThenCloseWithAnActiveTransaction() throws Exception {
        INSTANCE.getTransactionManager().begin();
        EntityManager em = getOrCreateEntityManager();
        try {
            CloseEntityManagerWithActiveTransactionTest.Box box = new CloseEntityManagerWithActiveTransactionTest.Box();
            box.setColor("red-and-white");
            em.persist(box);
            em.close();
            INSTANCE.getTransactionManager().commit();
        } catch (Exception e) {
            final TransactionManager transactionManager = INSTANCE.getTransactionManager();
            if (((transactionManager.getTransaction()) != null) && ((transactionManager.getTransaction().getStatus()) == (Status.STATUS_ACTIVE))) {
                INSTANCE.getTransactionManager().rollback();
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
        em = getOrCreateEntityManager();
        try {
            final List results = em.createQuery("from Box").getResultList();
            Assert.assertThat(results.size(), Is.is(1));
        } finally {
            em.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11166")
    public void testMergeThenCloseWithAnActiveTransaction() throws Exception {
        INSTANCE.getTransactionManager().begin();
        EntityManager em = getOrCreateEntityManager();
        try {
            CloseEntityManagerWithActiveTransactionTest.Box box = new CloseEntityManagerWithActiveTransactionTest.Box();
            box.setColor("red-and-white");
            em.persist(box);
            em.close();
            INSTANCE.getTransactionManager().commit();
            INSTANCE.getTransactionManager().begin();
            em = getOrCreateEntityManager();
            CloseEntityManagerWithActiveTransactionTest.Muffin muffin = new CloseEntityManagerWithActiveTransactionTest.Muffin();
            muffin.setKind("blueberry");
            box.addMuffin(muffin);
            em.merge(box);
            em.close();
            INSTANCE.getTransactionManager().commit();
        } catch (Exception e) {
            final TransactionManager transactionManager = INSTANCE.getTransactionManager();
            if (((transactionManager.getTransaction()) != null) && ((transactionManager.getTransaction().getStatus()) == (Status.STATUS_ACTIVE))) {
                INSTANCE.getTransactionManager().rollback();
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
        em = getOrCreateEntityManager();
        try {
            final List<CloseEntityManagerWithActiveTransactionTest.Box> boxes = em.createQuery("from Box").getResultList();
            Assert.assertThat(boxes.size(), Is.is(1));
            Assert.assertThat(boxes.get(0).getMuffinSet().size(), Is.is(1));
        } finally {
            em.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11269")
    public void testMergeWithDeletionOrphanRemovalThenCloseWithAnActiveTransaction() throws Exception {
        INSTANCE.getTransactionManager().begin();
        EntityManager em = getOrCreateEntityManager();
        try {
            CloseEntityManagerWithActiveTransactionTest.Muffin muffin = new CloseEntityManagerWithActiveTransactionTest.Muffin();
            muffin.setKind("blueberry");
            CloseEntityManagerWithActiveTransactionTest.SmallBox box = new CloseEntityManagerWithActiveTransactionTest.SmallBox(muffin);
            box.setColor("red-and-white");
            em.persist(box);
            em.close();
            INSTANCE.getTransactionManager().commit();
            INSTANCE.getTransactionManager().begin();
            em = getOrCreateEntityManager();
            box.emptyBox();
            em.merge(box);
            em.close();
            INSTANCE.getTransactionManager().commit();
        } catch (Exception e) {
            final TransactionManager transactionManager = INSTANCE.getTransactionManager();
            if (((transactionManager.getTransaction()) != null) && ((transactionManager.getTransaction().getStatus()) == (Status.STATUS_ACTIVE))) {
                INSTANCE.getTransactionManager().rollback();
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
        em = getOrCreateEntityManager();
        try {
            final List<CloseEntityManagerWithActiveTransactionTest.SmallBox> boxes = em.createQuery("from SmallBox").getResultList();
            Assert.assertThat(boxes.size(), Is.is(1));
            Assert.assertTrue(boxes.get(0).isEmpty());
        } finally {
            em.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11166")
    public void testUpdateThenCloseWithAnActiveTransaction() throws Exception {
        INSTANCE.getTransactionManager().begin();
        EntityManager em = getOrCreateEntityManager();
        try {
            CloseEntityManagerWithActiveTransactionTest.Box box = new CloseEntityManagerWithActiveTransactionTest.Box();
            box.setColor("red-and-white");
            em.persist(box);
            em.close();
            INSTANCE.getTransactionManager().commit();
            INSTANCE.getTransactionManager().begin();
            em = getOrCreateEntityManager();
            box = em.find(CloseEntityManagerWithActiveTransactionTest.Box.class, box.getId());
            CloseEntityManagerWithActiveTransactionTest.Muffin muffin = new CloseEntityManagerWithActiveTransactionTest.Muffin();
            muffin.setKind("blueberry");
            box.addMuffin(muffin);
            em.close();
            INSTANCE.getTransactionManager().commit();
        } catch (Exception e) {
            final TransactionManager transactionManager = INSTANCE.getTransactionManager();
            if (((transactionManager.getTransaction()) != null) && ((transactionManager.getTransaction().getStatus()) == (Status.STATUS_ACTIVE))) {
                INSTANCE.getTransactionManager().rollback();
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
        em = getOrCreateEntityManager();
        try {
            final List<CloseEntityManagerWithActiveTransactionTest.Box> boxes = em.createQuery("from Box").getResultList();
            Assert.assertThat(boxes.size(), Is.is(1));
            Assert.assertThat(boxes.get(0).getMuffinSet().size(), Is.is(1));
        } finally {
            em.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11166")
    public void testRemoveThenCloseWithAnActiveTransaction() throws Exception {
        INSTANCE.getTransactionManager().begin();
        EntityManager em = getOrCreateEntityManager();
        try {
            CloseEntityManagerWithActiveTransactionTest.Box box = new CloseEntityManagerWithActiveTransactionTest.Box();
            box.setColor("red-and-white");
            em.persist(box);
            CloseEntityManagerWithActiveTransactionTest.Muffin muffin = new CloseEntityManagerWithActiveTransactionTest.Muffin();
            muffin.setKind("blueberry");
            box.addMuffin(muffin);
            em.close();
            INSTANCE.getTransactionManager().commit();
            INSTANCE.getTransactionManager().begin();
            em = getOrCreateEntityManager();
            box = em.find(CloseEntityManagerWithActiveTransactionTest.Box.class, box.getId());
            em.remove(box);
            em.close();
            INSTANCE.getTransactionManager().commit();
        } catch (Exception e) {
            final TransactionManager transactionManager = INSTANCE.getTransactionManager();
            if (((transactionManager.getTransaction()) != null) && ((transactionManager.getTransaction().getStatus()) == (Status.STATUS_ACTIVE))) {
                INSTANCE.getTransactionManager().rollback();
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
        em = getOrCreateEntityManager();
        try {
            final List<CloseEntityManagerWithActiveTransactionTest.Box> boxes = em.createQuery("from Box").getResultList();
            Assert.assertThat(boxes.size(), Is.is(0));
        } finally {
            em.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11099")
    public void testCommitReleasesLogicalConnection() throws Exception {
        INSTANCE.getTransactionManager().begin();
        EntityManager em = getOrCreateEntityManager();
        try {
            CloseEntityManagerWithActiveTransactionTest.Box box = new CloseEntityManagerWithActiveTransactionTest.Box();
            box.setColor("red-and-white");
            em.persist(box);
            final SessionImpl session = ((SessionImpl) (em.unwrap(Session.class)));
            final JdbcCoordinatorImpl jdbcCoordinator = ((JdbcCoordinatorImpl) (session.getJdbcCoordinator()));
            em.close();
            INSTANCE.getTransactionManager().commit();
            Assert.assertThat("The logical connection is still open after commit", jdbcCoordinator.getLogicalConnection().isOpen(), Is.is(false));
        } catch (Exception e) {
            final TransactionManager transactionManager = INSTANCE.getTransactionManager();
            if (((transactionManager.getTransaction()) != null) && ((transactionManager.getTransaction().getStatus()) == (Status.STATUS_ACTIVE))) {
                INSTANCE.getTransactionManager().rollback();
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Entity(name = "Container")
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    public static class Container {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        private String color;

        public Long getId() {
            return id;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

    @Entity(name = "Box")
    public static class Box extends CloseEntityManagerWithActiveTransactionTest.Container {
        @OneToMany(mappedBy = "box", cascade = { CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.PERSIST }, fetch = FetchType.LAZY)
        private Set<CloseEntityManagerWithActiveTransactionTest.Muffin> muffinSet;

        public Box() {
        }

        public void addMuffin(CloseEntityManagerWithActiveTransactionTest.Muffin muffin) {
            muffin.setBox(this);
            if ((muffinSet) == null) {
                muffinSet = new HashSet<>();
            }
            muffinSet.add(muffin);
        }

        public Set<CloseEntityManagerWithActiveTransactionTest.Muffin> getMuffinSet() {
            return muffinSet;
        }
    }

    @Entity(name = "SmallBox")
    public static class SmallBox extends CloseEntityManagerWithActiveTransactionTest.Container {
        @OneToOne(cascade = { CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.PERSIST }, orphanRemoval = true)
        private CloseEntityManagerWithActiveTransactionTest.Muffin muffin;

        public SmallBox() {
        }

        public SmallBox(CloseEntityManagerWithActiveTransactionTest.Muffin muffin) {
            this.muffin = muffin;
        }

        public void emptyBox() {
            muffin = null;
        }

        public boolean isEmpty() {
            return (muffin) == null;
        }
    }

    @Entity(name = "Muffin")
    public static class Muffin {
        @Id
        @GeneratedValue
        private Long muffinId;

        @ManyToOne
        private CloseEntityManagerWithActiveTransactionTest.Box box;

        private String kind;

        public Muffin() {
        }

        public CloseEntityManagerWithActiveTransactionTest.Box getBox() {
            return box;
        }

        public void setBox(CloseEntityManagerWithActiveTransactionTest.Box box) {
            this.box = box;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }
    }

    @FunctionalInterface
    public interface Action {
        void execute();
    }
}

