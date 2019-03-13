/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.associations;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import org.hibernate.annotations.NaturalId;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class DerivedIdentifierTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        Long personId = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.associations.Person person = new org.hibernate.userguide.associations.Person("ABC-123");
            org.hibernate.userguide.associations.PersonDetails details = new org.hibernate.userguide.associations.PersonDetails();
            details.setPerson(person);
            entityManager.persist(person);
            entityManager.persist(details);
            return person.getId();
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.associations.PersonDetails details = entityManager.find(.class, personId);
            Assert.assertNotNull(details);
        });
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;

        @NaturalId
        private String registrationNumber;

        public Person() {
        }

        public Person(String registrationNumber) {
            this.registrationNumber = registrationNumber;
        }

        public Long getId() {
            return id;
        }

        public String getRegistrationNumber() {
            return registrationNumber;
        }
    }

    @Entity(name = "PersonDetails")
    public static class PersonDetails {
        @Id
        private Long id;

        private String nickName;

        @ManyToOne
        @MapsId
        private DerivedIdentifierTest.Person person;

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public DerivedIdentifierTest.Person getPerson() {
            return person;
        }

        public void setPerson(DerivedIdentifierTest.Person person) {
            this.person = person;
        }
    }
}

