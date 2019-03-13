/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.associations;


import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::associations-JoinFormula-example[]
@RequiresDialect(PostgreSQL82Dialect.class)
public class JoinFormulaTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        // tag::associations-JoinFormula-persistence-example[]
        JoinFormulaTest.Country US = new JoinFormulaTest.Country();
        US.setId(1);
        US.setName("United States");
        JoinFormulaTest.Country Romania = new JoinFormulaTest.Country();
        Romania.setId(40);
        Romania.setName("Romania");
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.persist(US);
            entityManager.persist(Romania);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.associations.User user1 = new org.hibernate.userguide.associations.User();
            user1.setId(1L);
            user1.setFirstName("John");
            user1.setLastName("Doe");
            user1.setPhoneNumber("+1-234-5678");
            entityManager.persist(user1);
            org.hibernate.userguide.associations.User user2 = new org.hibernate.userguide.associations.User();
            user2.setId(2L);
            user2.setFirstName("Vlad");
            user2.setLastName("Mihalcea");
            user2.setPhoneNumber("+40-123-4567");
            entityManager.persist(user2);
        });
        // end::associations-JoinFormula-persistence-example[]
        // tag::associations-JoinFormula-fetching-example[]
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            log.info("Fetch User entities");
            org.hibernate.userguide.associations.User john = entityManager.find(.class, 1L);
            assertEquals(US, john.getCountry());
            org.hibernate.userguide.associations.User vlad = entityManager.find(.class, 2L);
            assertEquals(Romania, vlad.getCountry());
        });
        // end::associations-JoinFormula-fetching-example[]
    }

    // tag::associations-JoinFormula-example[]
    // tag::associations-JoinFormula-example[]
    @Entity(name = "User")
    @Table(name = "users")
    public static class User {
        @Id
        private Long id;

        private String firstName;

        private String lastName;

        private String phoneNumber;

        @ManyToOne
        @JoinFormula("REGEXP_REPLACE(phoneNumber, \'\\+(\\d+)-.*\', \'\\1\')::int")
        private JoinFormulaTest.Country country;

        // Getters and setters omitted for brevity
        // end::associations-JoinFormula-example[]
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

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public JoinFormulaTest.Country getCountry() {
            return country;
        }
    }

    // end::associations-JoinFormula-example[]
    // tag::associations-JoinFormula-example[]
    // tag::associations-JoinFormula-example[]
    @Entity(name = "Country")
    @Table(name = "countries")
    public static class Country {
        @Id
        private Integer id;

        private String name;

        // Getters and setters, equals and hashCode methods omitted for brevity
        // end::associations-JoinFormula-example[]
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof JoinFormulaTest.Country)) {
                return false;
            }
            JoinFormulaTest.Country country = ((JoinFormulaTest.Country) (o));
            return Objects.equals(getId(), country.getId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getId());
        }
    }
}

