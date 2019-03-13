/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.session;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.proxy.AbstractLazyInitializer;
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
public class AssociateEntityWithTwoSessionsTest extends BaseEntityManagerFunctionalTestCase {
    @Rule
    public LoggerInspectionRule logInspection = new LoggerInspectionRule(Logger.getMessageLogger(CoreMessageLogger.class, AbstractLazyInitializer.class.getName()));

    @Test
    @TestForIssue(jiraKey = "HHH-12216")
    public void test() {
        final AssociateEntityWithTwoSessionsTest.Location location = new AssociateEntityWithTwoSessionsTest.Location();
        location.setCity("Cluj");
        final AssociateEntityWithTwoSessionsTest.Event event = new AssociateEntityWithTwoSessionsTest.Event();
        event.setLocation(location);
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.persist(location);
            entityManager.persist(event);
        });
        final Triggerable triggerable = logInspection.watchForLogMessages("HHH000485");
        triggerable.reset();
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.session.Event event1 = entityManager.find(.class, event.id);
            org.hibernate.session.Location location1 = event1.getLocation();
            try {
                doInJPA(this::entityManagerFactory, ( _entityManager) -> {
                    _entityManager.unwrap(.class).update(location1);
                });
                fail("Should have thrown a HibernateException");
            } catch ( expected) {
            }
        });
        Assert.assertEquals("HHH000485: Illegally attempted to associate a proxy for entity [org.hibernate.session.AssociateEntityWithTwoSessionsTest$Location] with id [1] with two open sessions.", triggerable.triggerMessage());
    }

    @Entity(name = "Location")
    public static class Location {
        @Id
        @GeneratedValue
        public Long id;

        public String city;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }

    @Entity(name = "Event")
    public static class Event {
        @Id
        @GeneratedValue
        public Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        private AssociateEntityWithTwoSessionsTest.Location location;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public AssociateEntityWithTwoSessionsTest.Location getLocation() {
            return location;
        }

        public void setLocation(AssociateEntityWithTwoSessionsTest.Location location) {
            this.location = location;
        }
    }
}

