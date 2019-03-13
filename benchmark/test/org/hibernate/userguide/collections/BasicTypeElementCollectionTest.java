/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.collections;


import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::collections-collection-proxy-entity-example[]
public class BasicTypeElementCollectionTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testProxies() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            try {
                // tag::collections-collection-proxy-usage-example[]
                org.hibernate.userguide.collections.Person person = entityManager.find(.class, 1L);
                // Throws java.lang.ClassCastException: org.hibernate.collection.internal.PersistentBag cannot be cast to java.util.ArrayList
                ArrayList<String> phones = ((ArrayList<String>) (person.getPhones()));
                // end::collections-collection-proxy-usage-example[]
            } catch ( expected) {
                log.error("Failure", expected);
            }
        });
    }

    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.collections.Person person = entityManager.find(.class, 1L);
            log.info("Clear element collection and add element");
            // tag::collections-value-type-collection-lifecycle-example[]
            person.getPhones().clear();
            person.getPhones().add("123-456-7890");
            person.getPhones().add("456-000-1234");
            // end::collections-value-type-collection-lifecycle-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.collections.Person person = entityManager.find(.class, 1L);
            log.info("Remove one element");
            // tag::collections-value-type-collection-remove-example[]
            person.getPhones().remove(0);
            // end::collections-value-type-collection-remove-example[]
        });
    }

    // tag::collections-collection-proxy-entity-example[]
    // tag::collections-collection-proxy-entity-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        @ElementCollection
        private List<String> phones = new java.util.ArrayList<>();

        // Getters and setters are omitted for brevity
        // end::collections-collection-proxy-entity-example[]
        public List<String> getPhones() {
            return phones;
        }
    }
}

