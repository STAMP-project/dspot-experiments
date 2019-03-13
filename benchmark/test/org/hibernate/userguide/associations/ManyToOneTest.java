/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.associations;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::associations-many-to-one-example[]
public class ManyToOneTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::associations-many-to-one-lifecycle-example[]
            org.hibernate.userguide.associations.Person person = new org.hibernate.userguide.associations.Person();
            entityManager.persist(person);
            org.hibernate.userguide.associations.Phone phone = new org.hibernate.userguide.associations.Phone("123-456-7890");
            phone.setPerson(person);
            entityManager.persist(phone);
            entityManager.flush();
            phone.setPerson(null);
            // end::associations-many-to-one-lifecycle-example[]
        });
    }

    // tag::associations-many-to-one-example[]
    // Getters and setters are omitted for brevity
    @Entity(name = "Person")
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;
    }

    // tag::associations-many-to-one-example[]
    @Entity(name = "Phone")
    public static class Phone {
        @Id
        @GeneratedValue
        private Long id;

        @Column(name = "`number`")
        private String number;

        @ManyToOne
        @JoinColumn(name = "person_id", foreignKey = @ForeignKey(name = "PERSON_ID_FK"))
        private ManyToOneTest.Person person;

        // Getters and setters are omitted for brevity
        // end::associations-many-to-one-example[]
        public Phone() {
        }

        public Phone(String number) {
            this.number = number;
        }

        public Long getId() {
            return id;
        }

        public String getNumber() {
            return number;
        }

        public ManyToOneTest.Person getPerson() {
            return person;
        }

        public void setPerson(ManyToOneTest.Person person) {
            this.person = person;
        }
    }
}

