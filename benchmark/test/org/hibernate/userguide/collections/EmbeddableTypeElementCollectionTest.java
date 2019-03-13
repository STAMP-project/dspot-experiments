/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.collections;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::collections-embeddable-type-collection-lifecycle-entity-example[]
public class EmbeddableTypeElementCollectionTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.collections.Person person = new org.hibernate.userguide.collections.Person();
            person.id = 1L;
            // tag::collections-embeddable-type-collection-lifecycle-example[]
            person.getPhones().add(new org.hibernate.userguide.collections.Phone("landline", "028-234-9876"));
            person.getPhones().add(new org.hibernate.userguide.collections.Phone("mobile", "072-122-9876"));
            // end::collections-embeddable-type-collection-lifecycle-example[]
            entityManager.persist(person);
        });
    }

    // tag::collections-embeddable-type-collection-lifecycle-entity-example[]
    // tag::collections-embeddable-type-collection-lifecycle-entity-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        @ElementCollection
        private List<EmbeddableTypeElementCollectionTest.Phone> phones = new ArrayList<>();

        // Getters and setters are omitted for brevity
        // end::collections-embeddable-type-collection-lifecycle-entity-example[]
        public List<EmbeddableTypeElementCollectionTest.Phone> getPhones() {
            return phones;
        }
    }

    // tag::collections-embeddable-type-collection-lifecycle-entity-example[]
    @Embeddable
    public static class Phone {
        private String type;

        @Column(name = "`number`")
        private String number;

        // Getters and setters are omitted for brevity
        // end::collections-embeddable-type-collection-lifecycle-entity-example[]
        public Phone() {
        }

        public Phone(String type, String number) {
            this.type = type;
            this.number = number;
        }

        public String getType() {
            return type;
        }

        public String getNumber() {
            return number;
        }
    }
}

