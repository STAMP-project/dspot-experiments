/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.onetoone;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import org.hibernate.annotations.NaturalId;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class OneToOneMapsIdTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        OneToOneMapsIdTest.Person _person = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.annotations.onetoone.Person person = new org.hibernate.test.annotations.onetoone.Person("ABC-123");
            entityManager.persist(person);
            return person;
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.annotations.onetoone.Person person = entityManager.find(.class, _person.getId());
            org.hibernate.test.annotations.onetoone.PersonDetails personDetails = new org.hibernate.test.annotations.onetoone.PersonDetails();
            personDetails.setNickName("John Doe");
            personDetails.setPerson(person);
            entityManager.persist(personDetails);
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

        public void setId(Long id) {
            this.id = id;
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

        @OneToOne
        @MapsId
        private OneToOneMapsIdTest.Person person;

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public OneToOneMapsIdTest.Person getPerson() {
            return person;
        }

        public void setPerson(OneToOneMapsIdTest.Person person) {
            this.person = person;
        }
    }
}

