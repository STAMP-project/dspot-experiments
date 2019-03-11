/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.inheritance.discriminator;


import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Christian Beikov
 */
public class MultiSingleTableLoadTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-5954")
    public void testEagerLoadMultipleHoldersWithDifferentSubtypes() {
        createTestData();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.inheritance.discriminator.Holder task1 = session.find(.class, 1L);
            org.hibernate.test.inheritance.discriminator.Holder task2 = session.find(.class, 2L);
            assertNotNull(task1);
            assertNotNull(task2);
        });
    }

    @Test
    public void testFetchJoinLoadMultipleHoldersWithDifferentSubtypes() {
        createTestData();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.inheritance.discriminator.Holder task1 = session.createQuery("FROM Holder h JOIN FETCH h.a WHERE h.id = :id", .class).setParameter("id", 1L).getSingleResult();
            org.hibernate.test.inheritance.discriminator.Holder task2 = session.createQuery("FROM Holder h JOIN FETCH h.a WHERE h.id = :id", .class).setParameter("id", 2L).getSingleResult();
            assertNotNull(task1);
            assertNotNull(task2);
            assertTrue((task1.a instanceof org.hibernate.test.inheritance.discriminator.B));
            assertTrue((task2.a instanceof org.hibernate.test.inheritance.discriminator.C));
        });
    }

    @Entity(name = "Holder")
    @Table(name = "holder")
    public static class Holder implements Serializable {
        @Id
        private long id;

        @ManyToOne(optional = false, cascade = CascadeType.ALL)
        @JoinColumn(name = "a_id")
        private MultiSingleTableLoadTest.A a;

        public Holder() {
        }

        public Holder(long id, MultiSingleTableLoadTest.A a) {
            this.id = id;
            this.a = a;
        }
    }

    @Entity
    @Table(name = "tbl_a")
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    public abstract static class A implements Serializable {
        @Id
        private long id;

        public A() {
        }

        public A(long id) {
            this.id = id;
        }
    }

    @Entity
    @DiscriminatorValue("B")
    public static class B extends MultiSingleTableLoadTest.A {
        @ManyToOne(optional = true, cascade = CascadeType.ALL)
        @JoinColumn(name = "x_id")
        private MultiSingleTableLoadTest.Y x;

        public B() {
        }

        public B(long id, MultiSingleTableLoadTest.Y x) {
            super(id);
            this.x = x;
        }
    }

    @Entity
    @DiscriminatorValue("C")
    public static class C extends MultiSingleTableLoadTest.A {
        @ManyToOne(optional = true, cascade = CascadeType.ALL)
        @JoinColumn(name = "x_id")
        private MultiSingleTableLoadTest.X x;

        public C() {
        }

        public C(long id, MultiSingleTableLoadTest.X x) {
            super(id);
            this.x = x;
        }
    }

    @Entity
    @Table(name = "tbl_x")
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    public abstract static class X implements Serializable {
        @Id
        private long id;

        public X() {
        }

        public X(long id) {
            this.id = id;
        }
    }

    @Entity
    @DiscriminatorValue("Y")
    public static class Y extends MultiSingleTableLoadTest.X {
        public Y() {
        }

        public Y(long id) {
            super(id);
        }
    }

    @Entity
    @DiscriminatorValue("Z")
    public static class Z extends MultiSingleTableLoadTest.X {
        public Z() {
        }

        public Z(long id) {
            super(id);
        }
    }
}

