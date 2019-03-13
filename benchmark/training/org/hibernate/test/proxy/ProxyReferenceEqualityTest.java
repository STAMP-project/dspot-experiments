/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.proxy;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Christian Beikov
 */
@TestForIssue(jiraKey = "HHH-9638")
public class ProxyReferenceEqualityTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testProxyFromQuery() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.proxy.A a = new org.hibernate.test.proxy.A();
            a.id = 1L;
            a.b = new org.hibernate.test.proxy.B();
            a.b.id = 1L;
            s.persist(a);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.proxy.A a = s.find(.class, 1L);
            List<org.hibernate.test.proxy.B> result = s.createQuery((("FROM " + (.class.getName())) + " b"), .class).getResultList();
            assertEquals(1, result.size());
            assertTrue((a.b == (result.get(0))));
        });
    }

    @Entity
    public static class A {
        @Id
        Long id;

        @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        @LazyToOne(LazyToOneOption.NO_PROXY)
        ProxyReferenceEqualityTest.B b;
    }

    @Entity
    public static class B {
        @Id
        Long id;
    }
}

