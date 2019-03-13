/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.sql;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import org.hibernate.annotations.Loader;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;
import org.hibernate.annotations.Where;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::sql-custom-crud-example[]
@RequiresDialect(H2Dialect.class)
@RequiresDialect(PostgreSQL82Dialect.class)
public class CustomSQLTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test_sql_custom_crud() {
        CustomSQLTest.Person _person = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.sql.Person person = new org.hibernate.userguide.sql.Person();
            person.setName("John Doe");
            entityManager.persist(person);
            person.getPhones().add("123-456-7890");
            person.getPhones().add("123-456-0987");
            return person;
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Long postId = _person.getId();
            org.hibernate.userguide.sql.Person person = entityManager.find(.class, postId);
            assertEquals(2, person.getPhones().size());
            person.getPhones().remove(0);
            person.setName("Mr. John Doe");
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Long postId = _person.getId();
            org.hibernate.userguide.sql.Person person = entityManager.find(.class, postId);
            assertEquals(1, person.getPhones().size());
            entityManager.remove(person);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Long postId = _person.getId();
            org.hibernate.userguide.sql.Person person = entityManager.find(.class, postId);
            assertNull(person);
        });
    }

    // tag::sql-custom-crud-example[]
    // tag::sql-custom-crud-example[]
    @Entity(name = "Person")
    @SQLInsert(sql = "INSERT INTO person (name, id, valid) VALUES (?, ?, true) ", check = ResultCheckStyle.COUNT)
    @SQLUpdate(sql = "UPDATE person SET name = ? where id = ? ")
    @SQLDelete(sql = "UPDATE person SET valid = false WHERE id = ? ")
    @Loader(namedQuery = "find_valid_person")
    @NamedNativeQueries({ @NamedNativeQuery(name = "find_valid_person", query = "SELECT id, name " + ("FROM person " + "WHERE id = ? and valid = true"), resultClass = CustomSQLTest.Person.class) })
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;

        private String name;

        @ElementCollection
        @SQLInsert(sql = "INSERT INTO person_phones (person_id, phones, valid) VALUES (?, ?, true) ")
        @SQLDeleteAll(sql = "UPDATE person_phones SET valid = false WHERE person_id = ?")
        @Where(clause = "valid = true")
        private List<String> phones = new ArrayList<>();

        // Getters and setters are omitted for brevity
        // end::sql-custom-crud-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getPhones() {
            return phones;
        }
    }
}

