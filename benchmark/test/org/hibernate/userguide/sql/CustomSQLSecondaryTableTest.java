/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.sql;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import org.hibernate.annotations.Loader;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
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
// end::sql-custom-crud-secondary-table-example[]
@RequiresDialect(H2Dialect.class)
@RequiresDialect(PostgreSQL82Dialect.class)
public class CustomSQLSecondaryTableTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test_sql_custom_crud() {
        CustomSQLSecondaryTableTest.Person _person = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.sql.Person person = new org.hibernate.userguide.sql.Person();
            person.setName("John Doe");
            entityManager.persist(person);
            person.setImage(new byte[]{ 1, 2, 3 });
            return person;
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Long postId = _person.getId();
            org.hibernate.userguide.sql.Person person = entityManager.find(.class, postId);
            assertArrayEquals(new byte[]{ 1, 2, 3 }, person.getImage());
            entityManager.remove(person);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Long postId = _person.getId();
            org.hibernate.userguide.sql.Person person = entityManager.find(.class, postId);
            assertNull(person);
        });
    }

    // tag::sql-custom-crud-secondary-table-example[]
    // tag::sql-custom-crud-secondary-table-example[]
    @Entity(name = "Person")
    @Table(name = "person")
    @SQLInsert(sql = "INSERT INTO person (name, id, valid) VALUES (?, ?, true) ")
    @SQLDelete(sql = "UPDATE person SET valid = false WHERE id = ? ")
    @SecondaryTable(name = "person_details", pkJoinColumns = @PrimaryKeyJoinColumn(name = "person_id"))
    @Table(appliesTo = "person_details", sqlInsert = @SQLInsert(sql = "INSERT INTO person_details (image, person_id, valid) VALUES (?, ?, true) ", check = ResultCheckStyle.COUNT), sqlDelete = @SQLDelete(sql = "UPDATE person_details SET valid = false WHERE person_id = ? "))
    @Loader(namedQuery = "find_valid_person")
    @NamedNativeQueries({ @NamedNativeQuery(name = "find_valid_person", query = "SELECT " + ((((("    p.id, " + "    p.name, ") + "    pd.image  ") + "FROM person p  ") + "LEFT OUTER JOIN person_details pd ON p.id = pd.person_id  ") + "WHERE p.id = ? AND p.valid = true AND pd.valid = true"), resultClass = CustomSQLSecondaryTableTest.Person.class) })
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;

        private String name;

        @Column(name = "image", table = "person_details")
        private byte[] image;

        // Getters and setters are omitted for brevity
        // end::sql-custom-crud-secondary-table-example[]
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

        public byte[] getImage() {
            return image;
        }

        public void setImage(byte[] image) {
            this.image = image;
        }
    }
}

