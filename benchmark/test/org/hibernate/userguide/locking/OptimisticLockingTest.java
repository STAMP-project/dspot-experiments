/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.locking;


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class OptimisticLockingTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        OptimisticLockingTest.Phone _phone = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.locking.Person person = new org.hibernate.userguide.locking.Person();
            person.setName("John Doe");
            entityManager.persist(person);
            org.hibernate.userguide.locking.Phone phone = new org.hibernate.userguide.locking.Phone();
            phone.setNumber("123-456-7890");
            phone.setPerson(person);
            entityManager.persist(phone);
            return phone;
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.locking.Person person = entityManager.find(.class, _phone.getPerson().getId());
            person.setName(person.getName().toUpperCase());
            org.hibernate.userguide.locking.Phone phone = entityManager.find(.class, _phone.getId());
            phone.setNumber(phone.getNumber().replace("-", " "));
        });
    }

    // tag::locking-optimistic-entity-mapping-example[]
    // tag::locking-optimistic-entity-mapping-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;

        @Column(name = "`name`")
        private String name;

        // tag::locking-optimistic-version-number-example[]
        @Version
        private long version;

        // end::locking-optimistic-version-number-example[]
        // Getters and setters are omitted for brevity
        // end::locking-optimistic-entity-mapping-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getVersion() {
            return version;
        }

        public void setVersion(long version) {
            this.version = version;
        }
    }

    // end::locking-optimistic-entity-mapping-example[]
    @Entity(name = "Phone")
    public static class Phone {
        @Id
        @GeneratedValue
        private Long id;

        @Column(name = "`number`")
        private String number;

        @ManyToOne
        private OptimisticLockingTest.Person person;

        // tag::locking-optimistic-version-timestamp-example[]
        @Version
        private Date version;

        // end::locking-optimistic-version-timestamp-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public OptimisticLockingTest.Person getPerson() {
            return person;
        }

        public void setPerson(OptimisticLockingTest.Person person) {
            this.person = person;
        }

        public Date getVersion() {
            return version;
        }
    }
}

