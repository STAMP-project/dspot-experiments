/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.cascade;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.Hibernate;
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
public class CascadeDeleteCollectionTest extends BaseCoreFunctionalTestCase {
    private CascadeDeleteCollectionTest.Parent originalParent;

    @Test
    public void testManagedWithUninitializedAssociation() {
        // Delete the Parent
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.cascade.Parent loadedParent = ((org.hibernate.test.bytecode.enhancement.cascade.Parent) (s.createQuery("SELECT p FROM Parent p WHERE name=:name").setParameter("name", "PARENT").uniqueResult()));
            checkInterceptor(loadedParent, false);
            assertFalse(Hibernate.isPropertyInitialized(loadedParent, "children"));
            s.delete(loadedParent);
        });
        // If the lazy relation is not fetch on cascade there is a constraint violation on commit
    }

    @Test
    @TestForIssue(jiraKey = "HHH-13129")
    public void testManagedWithInitializedAssociation() {
        // Delete the Parent
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.cascade.Parent loadedParent = ((org.hibernate.test.bytecode.enhancement.cascade.Parent) (s.createQuery("SELECT p FROM Parent p WHERE name=:name").setParameter("name", "PARENT").uniqueResult()));
            checkInterceptor(loadedParent, false);
            loadedParent.getChildren();
            assertTrue(Hibernate.isPropertyInitialized(loadedParent, "children"));
            s.delete(loadedParent);
        });
        // If the lazy relation is not fetch on cascade there is a constraint violation on commit
    }

    @Test
    @TestForIssue(jiraKey = "HHH-13129")
    public void testDetachedWithUninitializedAssociation() {
        final CascadeDeleteCollectionTest.Parent detachedParent = TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            return s.get(.class, originalParent.getId());
        });
        Assert.assertFalse(Hibernate.isPropertyInitialized(detachedParent, "children"));
        checkInterceptor(detachedParent, false);
        // Delete the detached Parent with uninitialized children
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            s.delete(detachedParent);
        });
        // If the lazy relation is not fetch on cascade there is a constraint violation on commit
    }

    @Test
    @TestForIssue(jiraKey = "HHH-13129")
    public void testDetachedWithInitializedAssociation() {
        final CascadeDeleteCollectionTest.Parent detachedParent = TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.cascade.Parent parent = s.get(.class, originalParent.getId());
            assertFalse(Hibernate.isPropertyInitialized(parent, "children"));
            // initialize collection before detaching
            parent.getChildren();
            return parent;
        });
        Assert.assertTrue(Hibernate.isPropertyInitialized(detachedParent, "children"));
        checkInterceptor(detachedParent, false);
        // Delete the detached Parent with initialized children
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            s.delete(detachedParent);
        });
        // If the lazy relation is not fetch on cascade there is a constraint violation on commit
    }

    @Test
    @TestForIssue(jiraKey = "HHH-13129")
    public void testDetachedOriginal() {
        // originalParent#children should be initialized
        Assert.assertTrue(Hibernate.isPropertyInitialized(originalParent, "children"));
        checkInterceptor(originalParent, true);
        // Delete the Parent
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            s.delete(originalParent);
        });
        // If the lazy relation is not fetch on cascade there is a constraint violation on commit
    }

    // --- //
    @Entity(name = "Parent")
    @Table(name = "PARENT")
    public static class Parent {
        Long id;

        String name;

        List<CascadeDeleteCollectionTest.Child> children = new ArrayList<>();

        String lazy;

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        Long getId() {
            return id;
        }

        void setId(Long id) {
            this.id = id;
        }

        @OneToMany(mappedBy = "parent", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, fetch = FetchType.LAZY)
        List<CascadeDeleteCollectionTest.Child> getChildren() {
            return Collections.unmodifiableList(children);
        }

        void setChildren(List<CascadeDeleteCollectionTest.Child> children) {
            this.children = children;
        }

        String getName() {
            return name;
        }

        void setName(String name) {
            this.name = name;
        }

        @Basic(fetch = FetchType.LAZY)
        String getLazy() {
            return lazy;
        }

        void setLazy(String lazy) {
            this.lazy = lazy;
        }

        void makeChild() {
            CascadeDeleteCollectionTest.Child c = new CascadeDeleteCollectionTest.Child();
            c.setParent(this);
            children.add(c);
        }
    }

    @Entity
    @Table(name = "CHILD")
    private static class Child {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        Long id;

        @ManyToOne(optional = false)
        @JoinColumn(name = "parent_id")
        CascadeDeleteCollectionTest.Parent parent;

        Long getId() {
            return id;
        }

        void setId(Long id) {
            this.id = id;
        }

        CascadeDeleteCollectionTest.Parent getParent() {
            return parent;
        }

        void setParent(CascadeDeleteCollectionTest.Parent parent) {
            this.parent = parent;
        }
    }
}

