/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.schema;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::schema-generation-columns-index-mapping-example[]
public class IndexTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.schema.Author author = new org.hibernate.userguide.schema.Author();
            author.setFirstName("Vlad");
            author.setLastName("Mihalcea");
            entityManager.persist(author);
        });
    }

    // tag::schema-generation-columns-index-mapping-example[]
    // tag::schema-generation-columns-index-mapping-example[]
    @Entity
    @Table(name = "author", indexes = @Index(name = "idx_author_first_last_name", columnList = "first_name, last_name", unique = false))
    public static class Author {
        @Id
        @GeneratedValue
        private Long id;

        @Column(name = "first_name")
        private String firstName;

        @Column(name = "last_name")
        private String lastName;

        // Getter and setters omitted for brevity
        // end::schema-generation-columns-index-mapping-example[]
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
    }
}

