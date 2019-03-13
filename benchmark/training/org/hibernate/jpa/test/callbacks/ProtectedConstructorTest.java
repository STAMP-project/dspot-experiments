/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.callbacks;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-13020")
public class ProtectedConstructorTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        ProtectedConstructorTest.Child child = new ProtectedConstructorTest.Child();
        EntityManager entityManager = null;
        EntityTransaction txn = null;
        try {
            entityManager = createEntityManager();
            txn = entityManager.getTransaction();
            txn.begin();
            entityManager.persist(child);
            txn.commit();
            entityManager.clear();
            Integer childId = child.getId();
            ProtectedConstructorTest.Child childReference = entityManager.getReference(ProtectedConstructorTest.Child.class, childId);
            Assert.assertEquals(child.getParent().getName(), childReference.getParent().getName());
        } catch (Throwable e) {
            if ((txn != null) && (txn.isActive())) {
                txn.rollback();
            }
            throw e;
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    @Entity(name = "Parent")
    public static class Parent {
        private Integer id;

        private String name;

        protected Parent() {
            name = "Empty";
        }

        public Parent(String s) {
            this.name = s;
        }

        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.AUTO)
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Entity(name = "Child")
    public static class Child {
        private Integer id;

        private ProtectedConstructorTest.Parent parent;

        public Child() {
            this.parent = new ProtectedConstructorTest.Parent("Name");
        }

        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.AUTO)
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
        public ProtectedConstructorTest.Parent getParent() {
            return parent;
        }

        public void setParent(ProtectedConstructorTest.Parent parent) {
            this.parent = parent;
        }
    }
}

