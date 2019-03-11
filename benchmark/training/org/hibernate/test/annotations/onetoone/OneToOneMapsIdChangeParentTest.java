/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.onetoone;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.logger.LoggerInspectionRule;
import org.hibernate.testing.logger.Triggerable;
import org.hibernate.testing.transaction.TransactionUtil;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-13228")
public class OneToOneMapsIdChangeParentTest extends BaseEntityManagerFunctionalTestCase {
    @Rule
    public LoggerInspectionRule logInspection = new LoggerInspectionRule(Logger.getMessageLogger(CoreMessageLogger.class, AbstractEntityPersister.class.getName()));

    private Triggerable triggerable = logInspection.watchForLogMessages("HHH000502:");

    @Test
    public void test() {
        OneToOneMapsIdChangeParentTest.Child _child = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.annotations.onetoone.Parent firstParent = new org.hibernate.test.annotations.onetoone.Parent();
            firstParent.setId(1L);
            entityManager.persist(firstParent);
            org.hibernate.test.annotations.onetoone.Child child = new org.hibernate.test.annotations.onetoone.Child();
            child.setParent(firstParent);
            entityManager.persist(child);
            return child;
        });
        triggerable.reset();
        Assert.assertFalse(triggerable.wasTriggered());
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.annotations.onetoone.Parent secondParent = new org.hibernate.test.annotations.onetoone.Parent();
            secondParent.setId(2L);
            entityManager.persist(secondParent);
            _child.setParent(secondParent);
            entityManager.merge(_child);
        });
        Assert.assertTrue(triggerable.wasTriggered());
    }

    @Entity(name = "Parent")
    public static class Parent {
        @Id
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    @Entity(name = "Child")
    public static class Child {
        @Id
        private Long id;

        @OneToOne
        @MapsId
        private OneToOneMapsIdChangeParentTest.Parent parent;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public OneToOneMapsIdChangeParentTest.Parent getParent() {
            return parent;
        }

        public void setParent(OneToOneMapsIdChangeParentTest.Parent parent) {
            this.parent = parent;
        }
    }
}

