/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.ecid;


import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class CompositeIdAssociationsWithEmbeddedCompositeIdTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-13114")
    public void testQueries() {
        CompositeIdAssociationsWithEmbeddedCompositeIdTest.Parent parent1 = new CompositeIdAssociationsWithEmbeddedCompositeIdTest.Parent("Jane", 0);
        CompositeIdAssociationsWithEmbeddedCompositeIdTest.Parent parent2 = new CompositeIdAssociationsWithEmbeddedCompositeIdTest.Parent("Jim", 1);
        CompositeIdAssociationsWithEmbeddedCompositeIdTest.Person person = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.ecid.Person p = new org.hibernate.test.ecid.Person();
            p.setParent1(parent1);
            p.setParent2(parent2);
            p.setBirthOrder(0);
            session.persist(parent1);
            session.persist(parent2);
            session.persist(p);
            return p;
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            checkResult(session.get(.class, person));
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            checkResult(session.createQuery("from Person p", .class).getSingleResult());
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Iterator<org.hibernate.test.ecid.Person> iterator = session.createQuery("from Person p", .class).iterate();
            assertTrue(iterator.hasNext());
            org.hibernate.test.ecid.Person p = iterator.next();
            checkResult(p);
            assertFalse(iterator.hasNext());
        });
    }

    @Entity(name = "Person")
    public static class Person implements Serializable {
        @Id
        @JoinColumns({ @JoinColumn(name = "p1Name"), @JoinColumn(name = "p1Index") })
        @ManyToOne
        private CompositeIdAssociationsWithEmbeddedCompositeIdTest.Parent parent1;

        @Id
        @JoinColumns({ @JoinColumn(name = "p2Name"), @JoinColumn(name = "p2Index") })
        @ManyToOne
        private CompositeIdAssociationsWithEmbeddedCompositeIdTest.Parent parent2;

        @Id
        private int birthOrder;

        private String name;

        public Person() {
        }

        public Person(String name, CompositeIdAssociationsWithEmbeddedCompositeIdTest.Parent parent1, CompositeIdAssociationsWithEmbeddedCompositeIdTest.Parent parent2) {
            this();
            setName(name);
            this.parent1 = parent1;
            this.parent2 = parent2;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public CompositeIdAssociationsWithEmbeddedCompositeIdTest.Parent getParent1() {
            return parent1;
        }

        public void setParent1(CompositeIdAssociationsWithEmbeddedCompositeIdTest.Parent parent1) {
            this.parent1 = parent1;
        }

        public CompositeIdAssociationsWithEmbeddedCompositeIdTest.Parent getParent2() {
            return parent2;
        }

        public void setParent2(CompositeIdAssociationsWithEmbeddedCompositeIdTest.Parent parent2) {
            this.parent2 = parent2;
        }

        public int getBirthOrder() {
            return birthOrder;
        }

        public void setBirthOrder(int birthOrder) {
            this.birthOrder = birthOrder;
        }
    }

    @Entity(name = "Parent")
    public static class Parent implements Serializable {
        @Id
        private String name;

        @Id
        @Column(name = "ind")
        private int index;

        public Parent() {
        }

        public Parent(String name, int index) {
            this.name = name;
            this.index = index;
        }
    }
}

