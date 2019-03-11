/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.onetoone;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class OneToOneMapsIdJoinColumnTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        OneToOneMapsIdJoinColumnTest.Person _person = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.annotations.onetoone.Person person = new org.hibernate.test.annotations.onetoone.Person("ABC-123");
            org.hibernate.test.annotations.onetoone.PersonDetails details = new org.hibernate.test.annotations.onetoone.PersonDetails();
            details.setNickName("John Doe");
            person.setDetails(details);
            entityManager.persist(person);
            return person;
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.annotations.onetoone.Person person = entityManager.find(.class, _person.getId());
            org.hibernate.test.annotations.onetoone.PersonDetails details = entityManager.find(.class, _person.getId());
        });
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        private String id;

        @OneToOne(mappedBy = "person", cascade = CascadeType.PERSIST, optional = false)
        private OneToOneMapsIdJoinColumnTest.PersonDetails details;

        public Person() {
        }

        public Person(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setDetails(OneToOneMapsIdJoinColumnTest.PersonDetails details) {
            this.details = details;
            details.setPerson(this);
        }
    }

    @Entity(name = "PersonDetails")
    public static class PersonDetails {
        @Id
        private String id;

        private String nickName;

        @OneToOne
        @MapsId
        @JoinColumn(name = "person_id")
        private OneToOneMapsIdJoinColumnTest.Person person;

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public OneToOneMapsIdJoinColumnTest.Person getPerson() {
            return person;
        }

        public void setPerson(OneToOneMapsIdJoinColumnTest.Person person) {
            this.person = person;
        }
    }
}

