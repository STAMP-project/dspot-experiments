/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.sql;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import org.hibernate.annotations.Loader;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(Oracle8iDialect.class)
public class OracleCustomSQLWithStoredProcedureTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test_sql_custom_crud() {
        OracleCustomSQLWithStoredProcedureTest.Person _person = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.sql.Person person = new org.hibernate.userguide.sql.Person();
            person.setName("John Doe");
            entityManager.persist(person);
            return person;
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Long postId = _person.getId();
            org.hibernate.userguide.sql.Person person = entityManager.find(.class, postId);
            assertNotNull(person);
            entityManager.remove(person);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Long postId = _person.getId();
            org.hibernate.userguide.sql.Person person = entityManager.find(.class, postId);
            assertNull(person);
        });
    }

    // tag::sql-sp-custom-crud-example[]
    // end::sql-sp-custom-crud-example[]
    @Entity(name = "Person")
    @SQLInsert(sql = "INSERT INTO person (name, id, valid) VALUES (?, ?, 1) ", check = ResultCheckStyle.COUNT)
    @SQLDelete(sql = "{ call sp_delete_person( ? ) } ", callable = true)
    @Loader(namedQuery = "find_valid_person")
    @NamedNativeQueries({ @NamedNativeQuery(name = "find_valid_person", query = "SELECT id, name " + ("FROM person " + "WHERE id = ? and valid = 1"), resultClass = OracleCustomSQLWithStoredProcedureTest.Person.class) })
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;

        private String name;

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
    }
}

