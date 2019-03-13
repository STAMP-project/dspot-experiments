/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.identifier;


import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::identifiers-generators-uuid-mapping-example[]
public class UuidGeneratedValueTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        UuidGeneratedValueTest.Book book = new UuidGeneratedValueTest.Book();
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            book.setTitle("High-Performance Java Persistence");
            book.setAuthor("Vlad Mihalcea");
            entityManager.persist(book);
        });
        Assert.assertNotNull(book.getId());
    }

    // tag::identifiers-generators-uuid-mapping-example[]
    // tag::identifiers-generators-uuid-mapping-example[]
    @Entity(name = "Book")
    public static class Book {
        @Id
        @GeneratedValue
        private UUID id;

        private String title;

        private String author;

        // Getters and setters are omitted for brevity
        // end::identifiers-generators-uuid-mapping-example[]
        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
    }
}

