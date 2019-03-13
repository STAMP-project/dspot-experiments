/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.proxy;


import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import org.hibernate.Hibernate;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


public class HibernateUnproxyTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testInitializedProxyCanBeUnproxied() {
        HibernateUnproxyTest.Parent p = new HibernateUnproxyTest.Parent();
        HibernateUnproxyTest.Child c = new HibernateUnproxyTest.Child();
        p.setChild(c);
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.persist(p);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.proxy.Parent parent = entityManager.find(.class, p.getId());
            org.hibernate.test.proxy.Child child = parent.getChild();
            assertFalse(Hibernate.isInitialized(child));
            Hibernate.initialize(child);
            org.hibernate.test.proxy.Child unproxiedChild = ((org.hibernate.test.proxy.Child) (Hibernate.unproxy(child)));
            assertEquals(.class, unproxiedChild.getClass());
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.proxy.Parent parent = entityManager.find(.class, p.getId());
            org.hibernate.test.proxy.Child child = parent.getChild();
            assertFalse(Hibernate.isInitialized(child));
            Hibernate.initialize(child);
            org.hibernate.test.proxy.Child unproxiedChild = Hibernate.unproxy(child, .class);
            assertEquals(.class, unproxiedChild.getClass());
        });
    }

    @Test
    public void testNotInitializedProxyCanBeUnproxiedWithInitialization() {
        HibernateUnproxyTest.Parent p = new HibernateUnproxyTest.Parent();
        HibernateUnproxyTest.Child c = new HibernateUnproxyTest.Child();
        p.setChild(c);
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.persist(p);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.proxy.Parent parent = entityManager.find(.class, p.getId());
            org.hibernate.test.proxy.Child child = parent.getChild();
            assertFalse(Hibernate.isInitialized(child));
            org.hibernate.test.proxy.Child unproxiedChild = ((org.hibernate.test.proxy.Child) (Hibernate.unproxy(child)));
            assertTrue(Hibernate.isInitialized(child));
            assertEquals(.class, unproxiedChild.getClass());
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.proxy.Parent parent = entityManager.find(.class, p.getId());
            org.hibernate.test.proxy.Child child = parent.getChild();
            assertFalse(Hibernate.isInitialized(child));
            org.hibernate.test.proxy.Child unproxiedChild = Hibernate.unproxy(child, .class);
            assertTrue(Hibernate.isInitialized(child));
            assertEquals(.class, unproxiedChild.getClass());
        });
    }

    @Test
    public void testNotHibernateProxyShouldThrowException() {
        HibernateUnproxyTest.Parent p = new HibernateUnproxyTest.Parent();
        HibernateUnproxyTest.Child c = new HibernateUnproxyTest.Child();
        p.setChild(c);
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.persist(p);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.proxy.Parent parent = entityManager.find(.class, p.getId());
            assertSame(parent, Hibernate.unproxy(parent));
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.proxy.Parent parent = entityManager.find(.class, p.getId());
            assertSame(parent, Hibernate.unproxy(parent, .class));
        });
    }

    @Test
    public void testNullUnproxyReturnsNull() {
        Assert.assertNull(Hibernate.unproxy(null));
        Assert.assertNull(Hibernate.unproxy(null, HibernateUnproxyTest.Parent.class));
    }

    @Test
    public void testProxyEquality() {
        HibernateUnproxyTest.Parent parent = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.proxy.Parent p = new org.hibernate.test.proxy.Parent();
            p.name = "John Doe";
            entityManager.persist(p);
            return p;
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.proxy.Parent p = entityManager.getReference(.class, parent.getId());
            assertFalse(parent.equals(p));
            assertTrue(parent.equals(Hibernate.unproxy(p)));
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.proxy.Parent p = entityManager.getReference(.class, parent.getId());
            assertFalse(parent.equals(p));
            assertTrue(parent.equals(Hibernate.unproxy(p, .class)));
        });
    }

    @Entity(name = "Parent")
    public static class Parent {
        @Id
        @GeneratedValue
        private Integer id;

        private String name;

        @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private HibernateUnproxyTest.Child child;

        public Integer getId() {
            return id;
        }

        public void setChild(HibernateUnproxyTest.Child child) {
            this.child = child;
            child.setParent(this);
        }

        public HibernateUnproxyTest.Child getChild() {
            return child;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            HibernateUnproxyTest.Parent parent = ((HibernateUnproxyTest.Parent) (o));
            return Objects.equals(name, parent.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    @Entity(name = "Child")
    public static class Child {
        @Id
        @GeneratedValue
        private Integer id;

        @OneToOne(fetch = FetchType.LAZY)
        private HibernateUnproxyTest.Parent parent;

        public Integer getId() {
            return id;
        }

        public void setParent(HibernateUnproxyTest.Parent parent) {
            this.parent = parent;
        }

        public HibernateUnproxyTest.Parent getParent() {
            return parent;
        }
    }
}

