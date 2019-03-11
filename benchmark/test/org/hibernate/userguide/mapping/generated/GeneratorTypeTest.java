/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.generated;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.Session;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.tuple.ValueGenerator;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::mapping-generated-GeneratorType-example[]
public class GeneratorTypeTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        // tag::mapping-generated-GeneratorType-persist-example[]
        GeneratorTypeTest.CurrentUser.INSTANCE.logIn("Alice");
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.generated.Person person = new org.hibernate.userguide.mapping.generated.Person();
            person.setId(1L);
            person.setFirstName("John");
            person.setLastName("Doe");
            entityManager.persist(person);
        });
        GeneratorTypeTest.CurrentUser.INSTANCE.logOut();
        // end::mapping-generated-GeneratorType-persist-example[]
        // tag::mapping-generated-GeneratorType-update-example[]
        GeneratorTypeTest.CurrentUser.INSTANCE.logIn("Bob");
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.generated.Person person = entityManager.find(.class, 1L);
            person.setFirstName("Mr. John");
        });
        GeneratorTypeTest.CurrentUser.INSTANCE.logOut();
        // end::mapping-generated-GeneratorType-update-example[]
    }

    // tag::mapping-generated-GeneratorType-example[]
    public static class CurrentUser {
        public static final GeneratorTypeTest.CurrentUser INSTANCE = new GeneratorTypeTest.CurrentUser();

        private static final ThreadLocal<String> storage = new ThreadLocal<>();

        public void logIn(String user) {
            GeneratorTypeTest.CurrentUser.storage.set(user);
        }

        public void logOut() {
            GeneratorTypeTest.CurrentUser.storage.remove();
        }

        public String get() {
            return GeneratorTypeTest.CurrentUser.storage.get();
        }
    }

    public static class LoggedUserGenerator implements ValueGenerator<String> {
        @Override
        public String generateValue(Session session, Object owner) {
            return GeneratorTypeTest.CurrentUser.INSTANCE.get();
        }
    }

    // tag::mapping-generated-GeneratorType-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        private String firstName;

        private String lastName;

        @GeneratorType(type = GeneratorTypeTest.LoggedUserGenerator.class, when = GenerationTime.INSERT)
        private String createdBy;

        @GeneratorType(type = GeneratorTypeTest.LoggedUserGenerator.class, when = GenerationTime.ALWAYS)
        private String updatedBy;

        // end::mapping-generated-GeneratorType-example[]
        public Person() {
        }

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

        public String getCreatedBy() {
            return createdBy;
        }

        public String getUpdatedBy() {
            return updatedBy;
        }
    }
}

