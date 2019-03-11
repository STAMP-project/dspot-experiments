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
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.logger.LoggerInspectionRule;
import org.hibernate.testing.logger.Triggerable;
import org.hibernate.testing.util.ExceptionUtil;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-13020")
public class PrivateConstructorTest extends BaseEntityManagerFunctionalTestCase {
    @Rule
    public LoggerInspectionRule logInspection = new LoggerInspectionRule(Logger.getMessageLogger(CoreMessageLogger.class, PrivateConstructorTest.proxyFactoryClass().getName()));

    @Test
    public void test() {
        PrivateConstructorTest.Child child = new PrivateConstructorTest.Child();
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
            Triggerable triggerable = logInspection.watchForLogMessages("HHH000143:");
            PrivateConstructorTest.Child childReference = entityManager.getReference(PrivateConstructorTest.Child.class, childId);
            try {
                Assert.assertEquals(child.getParent().getName(), childReference.getParent().getName());
            } catch (Exception expected) {
                Assert.assertEquals(NoSuchMethodException.class, ExceptionUtil.rootCause(expected).getClass());
                Assert.assertTrue(expected.getMessage().contains("Bytecode enhancement failed because no public, protected or package-private default constructor was found for entity"));
            }
            Assert.assertTrue(triggerable.wasTriggered());
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

        private Parent() {
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

        private PrivateConstructorTest.Parent parent;

        public Child() {
            this.parent = new PrivateConstructorTest.Parent("Name");
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
        public PrivateConstructorTest.Parent getParent() {
            return parent;
        }

        public void setParent(PrivateConstructorTest.Parent parent) {
            this.parent = parent;
        }
    }
}

