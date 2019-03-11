/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.locking;


import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::locking-optimistic-lock-type-dirty-example[]
public class OptimisticLockTypeDirtyTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.locking.Person person = new org.hibernate.userguide.locking.Person();
            person.setId(1L);
            person.setName("John Doe");
            person.setCountry("US");
            person.setCity("New York");
            person.setCreatedOn(new Timestamp(System.currentTimeMillis()));
            entityManager.persist(person);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::locking-optimistic-lock-type-dirty-update-example[]
            org.hibernate.userguide.locking.Person person = entityManager.find(.class, 1L);
            person.setCity("Washington D.C.");
            // end::locking-optimistic-lock-type-dirty-update-example[]
        });
    }

    // tag::locking-optimistic-lock-type-dirty-example[]
    // tag::locking-optimistic-lock-type-dirty-example[]
    @Entity(name = "Person")
    @OptimisticLocking(type = OptimisticLockType.DIRTY)
    @DynamicUpdate
    @SelectBeforeUpdate
    public static class Person {
        @Id
        private Long id;

        @Column(name = "`name`")
        private String name;

        private String country;

        private String city;

        @Column(name = "created_on")
        private Timestamp createdOn;

        // Getters and setters are omitted for brevity
        // end::locking-optimistic-lock-type-dirty-example[]
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

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public Timestamp getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(Timestamp createdOn) {
            this.createdOn = createdOn;
        }
    }
}

