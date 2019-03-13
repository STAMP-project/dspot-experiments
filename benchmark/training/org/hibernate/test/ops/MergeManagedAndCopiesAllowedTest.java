/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.ops;


import java.util.HashSet;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 * A  1 ------------> 1 B 1 ----------> 1 C
 *                      1                 1
 *                      |                 |
 *                      |                 |
 *                      V                 V
 *                      1                 N
 *                      D 1------------>N E
 *
 * @author Gail Badner
 */
public class MergeManagedAndCopiesAllowedTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testIt() {
        MergeManagedAndCopiesAllowedTest.A a = new MergeManagedAndCopiesAllowedTest.A();
        a.b = new MergeManagedAndCopiesAllowedTest.B();
        a.b.d = new MergeManagedAndCopiesAllowedTest.D();
        a.b.d.dEs.add(new MergeManagedAndCopiesAllowedTest.E());
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(a);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.ops.A aGet = session.get(.class, a.id);
            aGet.b.c = new org.hibernate.test.ops.C();
            Set<org.hibernate.test.ops.E> copies = new HashSet<>();
            for (org.hibernate.test.ops.E e : aGet.b.d.dEs) {
                copies.add(new org.hibernate.test.ops.E(e.id, "description"));
            }
            aGet.b.c.cEs.addAll(copies);
            session.merge(aGet);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.ops.A aGet = session.get(.class, a.id);
            org.hibernate.test.ops.E e = aGet.b.c.cEs.iterator().next();
            assertSame(e, aGet.b.d.dEs.iterator().next());
            assertEquals("description", e.description);
        });
    }

    @Entity(name = "A")
    public static class A {
        @Id
        @GeneratedValue
        private int id;

        @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
        private MergeManagedAndCopiesAllowedTest.B b;
    }

    @Entity(name = "B")
    public static class B {
        @Id
        @GeneratedValue
        private int id;

        @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
        private MergeManagedAndCopiesAllowedTest.C c;

        @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
        private MergeManagedAndCopiesAllowedTest.D d;
    }

    @Entity(name = "C")
    public static class C {
        @Id
        @GeneratedValue
        private int id;

        @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
        @JoinColumn(nullable = true)
        private java.util.Set<MergeManagedAndCopiesAllowedTest.E> cEs = new HashSet<>();
    }

    @Entity(name = "D")
    public static class D {
        @Id
        @GeneratedValue
        private int id;

        @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
        @JoinColumn(nullable = false)
        private java.util.Set<MergeManagedAndCopiesAllowedTest.E> dEs = new HashSet<>();
    }

    @Entity(name = "E")
    public static class E {
        @Id
        @GeneratedValue
        private int id;

        private String description;

        E() {
        }

        E(int id, String description) {
            this.id = id;
            this.description = description;
        }
    }
}

