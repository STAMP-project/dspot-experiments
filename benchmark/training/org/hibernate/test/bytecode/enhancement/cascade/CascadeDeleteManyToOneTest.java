/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.cascade;


import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.Hibernate;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Luis Barreiro
 */
@TestForIssue(jiraKey = "HHH-10252")
@RunWith(BytecodeEnhancerRunner.class)
public class CascadeDeleteManyToOneTest extends BaseCoreFunctionalTestCase {
    private CascadeDeleteManyToOneTest.Child originalChild;

    @Test
    public void testManagedWithUninitializedAssociation() {
        // Delete the Child
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.cascade.Child loadedChild = ((org.hibernate.test.bytecode.enhancement.cascade.Child) (s.createQuery("SELECT c FROM Child c WHERE name=:name").setParameter("name", "CHILD").uniqueResult()));
            checkInterceptor(loadedChild, false);
            assertFalse(Hibernate.isPropertyInitialized(loadedChild, "parent"));
            s.delete(loadedChild);
        });
        // Explicitly check that both got deleted
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            assertNull(s.createQuery("FROM Child c").uniqueResult());
            assertNull(s.createQuery("FROM Parent p").uniqueResult());
        });
    }

    @Test
    public void testManagedWithInitializedAssociation() {
        // Delete the Child
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.cascade.Child loadedChild = ((org.hibernate.test.bytecode.enhancement.cascade.Child) (s.createQuery("SELECT c FROM Child c WHERE name=:name").setParameter("name", "CHILD").uniqueResult()));
            checkInterceptor(loadedChild, false);
            loadedChild.getParent();
            assertTrue(Hibernate.isPropertyInitialized(loadedChild, "parent"));
            s.delete(loadedChild);
        });
        // Explicitly check that both got deleted
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            assertNull(s.createQuery("FROM Child c").uniqueResult());
            assertNull(s.createQuery("FROM Parent p").uniqueResult());
        });
    }

    @Test
    public void testDetachedWithUninitializedAssociation() {
        final CascadeDeleteManyToOneTest.Child detachedChild = TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            return s.get(.class, originalChild.getId());
        });
        Assert.assertFalse(Hibernate.isPropertyInitialized(detachedChild, "parent"));
        checkInterceptor(detachedChild, false);
        // Delete the detached Child with uninitialized parent
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            s.delete(detachedChild);
        });
        // Explicitly check that both got deleted
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            assertNull(s.createQuery("FROM Child c").uniqueResult());
            assertNull(s.createQuery("FROM Parent p").uniqueResult());
        });
    }

    @Test
    public void testDetachedWithInitializedAssociation() {
        final CascadeDeleteManyToOneTest.Child detachedChild = TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.cascade.Child child = s.get(.class, originalChild.getId());
            assertFalse(Hibernate.isPropertyInitialized(child, "parent"));
            // initialize parent before detaching
            child.getParent();
            return child;
        });
        Assert.assertTrue(Hibernate.isPropertyInitialized(detachedChild, "parent"));
        checkInterceptor(detachedChild, false);
        // Delete the detached Child with initialized parent
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            s.delete(detachedChild);
        });
        // Explicitly check that both got deleted
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            assertNull(s.createQuery("FROM Child c").uniqueResult());
            assertNull(s.createQuery("FROM Parent p").uniqueResult());
        });
    }

    @Test
    public void testDetachedOriginal() {
        // originalChild#parent should be initialized
        Assert.assertTrue(Hibernate.isPropertyInitialized(originalChild, "parent"));
        checkInterceptor(originalChild, true);
        // Delete the Child
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            s.delete(originalChild);
        });
        // Explicitly check that both got deleted
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            assertNull(s.createQuery("FROM Child c").uniqueResult());
            assertNull(s.createQuery("FROM Parent p").uniqueResult());
        });
    }

    // --- //
    @Entity(name = "Parent")
    @Table(name = "PARENT")
    public static class Parent {
        Long id;

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        Long getId() {
            return id;
        }

        void setId(Long id) {
            this.id = id;
        }
    }

    @Entity(name = "Child")
    @Table(name = "CHILD")
    private static class Child {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        Long id;

        String name;

        @ManyToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, fetch = FetchType.LAZY)
        @JoinColumn(name = "parent_id")
        @LazyToOne(LazyToOneOption.NO_PROXY)
        CascadeDeleteManyToOneTest.Parent parent;

        @Basic(fetch = FetchType.LAZY)
        String lazy;

        Long getId() {
            return id;
        }

        void setId(Long id) {
            this.id = id;
        }

        String getName() {
            return name;
        }

        void setName(String name) {
            this.name = name;
        }

        CascadeDeleteManyToOneTest.Parent getParent() {
            return parent;
        }

        void setParent(CascadeDeleteManyToOneTest.Parent parent) {
            this.parent = parent;
        }

        String getLazy() {
            return lazy;
        }

        void setLazy(String lazy) {
            this.lazy = lazy;
        }

        void makeParent() {
            parent = new CascadeDeleteManyToOneTest.Parent();
        }
    }
}

