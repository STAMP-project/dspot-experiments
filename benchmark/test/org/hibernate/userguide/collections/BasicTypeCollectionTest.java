/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.collections;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.Type;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::collections-comma-delimited-collection-example[]
public class BasicTypeCollectionTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.userguide.collections.Person person = new org.hibernate.userguide.collections.Person();
            person.id = 1L;
            session.persist(person);
            // tag::collections-comma-delimited-collection-lifecycle-example[]
            person.phones.add("027-123-4567");
            person.phones.add("028-234-9876");
            session.flush();
            person.getPhones().remove(0);
            // end::collections-comma-delimited-collection-lifecycle-example[]
        });
    }

    // tag::collections-comma-delimited-collection-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        @Type(type = "comma_delimited_strings")
        private List<String> phones = new ArrayList<>();

        public List<String> getPhones() {
            return phones;
        }
    }
}

