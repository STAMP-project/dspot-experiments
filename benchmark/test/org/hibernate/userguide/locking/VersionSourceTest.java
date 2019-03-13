/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.locking;


import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import org.hibernate.annotations.Source;
import org.hibernate.annotations.SourceType;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::locking-optimistic-version-timestamp-source-mapping-example[]
public class VersionSourceTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::locking-optimistic-version-timestamp-source-persist-example[]
            org.hibernate.userguide.locking.Person person = new org.hibernate.userguide.locking.Person();
            person.setId(1L);
            person.setFirstName("John");
            person.setLastName("Doe");
            assertNull(person.getVersion());
            entityManager.persist(person);
            assertNotNull(person.getVersion());
            // end::locking-optimistic-version-timestamp-source-persist-example[]
        });
    }

    // tag::locking-optimistic-version-timestamp-source-mapping-example[]
    // tag::locking-optimistic-version-timestamp-source-mapping-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        private String firstName;

        private String lastName;

        @Version
        @Source(SourceType.DB)
        private Date version;

        // end::locking-optimistic-version-timestamp-source-mapping-example[]
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

        public Date getVersion() {
            return version;
        }
    }
}

