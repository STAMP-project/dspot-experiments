/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations;


import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-13256")
public class InMemoryUpdateTimestampTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.annotations.Person person = new org.hibernate.test.annotations.Person();
            person.setId(1L);
            person.setFirstName("Jon");
            person.setLastName("Doe");
            entityManager.persist(person);
            entityManager.flush();
            Assert.assertNotNull(person.getUpdatedOn());
        });
        AtomicReference<Date> beforeTimestamp = new AtomicReference<>();
        sleep(1);
        InMemoryUpdateTimestampTest.Person _person = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.annotations.Person person = entityManager.find(.class, 1L);
            beforeTimestamp.set(person.getUpdatedOn());
            person.setLastName("Doe Jr.");
            return person;
        });
        Assert.assertTrue(_person.getUpdatedOn().after(beforeTimestamp.get()));
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        private String firstName;

        private String lastName;

        @Column(nullable = false)
        @UpdateTimestamp
        private Date updatedOn;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Date getUpdatedOn() {
            return updatedOn;
        }
    }
}

