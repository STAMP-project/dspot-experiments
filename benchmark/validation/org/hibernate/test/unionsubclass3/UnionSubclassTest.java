/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.unionsubclass3;


import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author pholvs
 */
public class UnionSubclassTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12114")
    public void testUnionSubclassClassResults() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.unionsubclass3.Father father = new org.hibernate.test.unionsubclass3.Father();
            father.id = 1L;
            father.fathersDay = "FD1";
            org.hibernate.test.unionsubclass3.Child child1 = new org.hibernate.test.unionsubclass3.Child();
            child1.id = 2L;
            child1.parent = father;
            org.hibernate.test.unionsubclass3.Mother mother1 = new org.hibernate.test.unionsubclass3.Mother();
            mother1.id = 3L;
            mother1.mothersDay = "MD1";
            org.hibernate.test.unionsubclass3.Child child2 = new org.hibernate.test.unionsubclass3.Child();
            child2.id = 4L;
            child2.parent = mother1;
            org.hibernate.test.unionsubclass3.Mother mother2 = new org.hibernate.test.unionsubclass3.Mother();
            mother2.id = 5L;
            mother2.mothersDay = "MD2";
            org.hibernate.test.unionsubclass3.Child child3 = new org.hibernate.test.unionsubclass3.Child();
            child3.id = 6L;
            child3.parent = mother2;
            org.hibernate.test.unionsubclass3.Mother mother4 = new org.hibernate.test.unionsubclass3.Mother();
            mother4.id = 7L;
            mother4.mothersDay = "MD3";
            org.hibernate.test.unionsubclass3.Child child4 = new org.hibernate.test.unionsubclass3.Child();
            child4.id = 8L;
            child4.parent = mother4;
            session.persist(father);
            session.persist(mother1);
            session.persist(mother2);
            session.persist(mother4);
            session.persist(child1);
            session.persist(child2);
            session.persist(child3);
            session.persist(child4);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List results = session.createQuery(("select c " + (((((("from Child c" + " left join c.parent p1 ") + " left join c.parent p2 ") + " where ") + "		(TYPE(p1) = Father and p1.fathersDay = 'FD1') ") + "	or ") + "  	(TYPE(p2) = Mother and p2.mothersDay = 'MD1')"))).getResultList();
            assertEquals(2, results.size());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12565")
    public void typeOfLeafTPC() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List results = session.createQuery(("select TYPE(f) " + ("from Father f" + " where f.id = -1"))).getResultList();
            assertEquals(0, results.size());
        });
    }

    @Entity(name = "Child")
    public static class Child {
        @Id
        Long id;

        @ManyToOne
        UnionSubclassTest.Parent parent;
    }

    @Entity(name = "Father")
    public static class Father extends UnionSubclassTest.Parent {
        @Column
        String fathersDay;
    }

    @Entity(name = "Mother")
    public static class Mother extends UnionSubclassTest.Parent {
        @Column
        String mothersDay;
    }

    @Entity(name = "Parent")
    @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
    public abstract static class Parent {
        @Id
        public Long id;
    }
}

