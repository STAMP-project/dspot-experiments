/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.identifier;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.NaturalId;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::naturalid-simple-basic-attribute-mapping-example[]
public class SimpleNaturalIdTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.identifier.Book book = new org.hibernate.userguide.mapping.identifier.Book();
            book.setId(1L);
            book.setTitle("High-Performance Java Persistence");
            book.setAuthor("Vlad Mihalcea");
            book.setIsbn("978-9730228236");
            entityManager.persist(book);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::naturalid-simple-load-access-example[]
            org.hibernate.userguide.mapping.identifier.Book book = entityManager.unwrap(.class).bySimpleNaturalId(.class).load("978-9730228236");
            // end::naturalid-simple-load-access-example[]
            assertEquals("High-Performance Java Persistence", book.getTitle());
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::naturalid-load-access-example[]
            org.hibernate.userguide.mapping.identifier.Book book = entityManager.unwrap(.class).byNaturalId(.class).using("isbn", "978-9730228236").load();
            // end::naturalid-load-access-example[]
            assertEquals("High-Performance Java Persistence", book.getTitle());
        });
    }

    // tag::naturalid-simple-basic-attribute-mapping-example[]
    // tag::naturalid-simple-basic-attribute-mapping-example[]
    @Entity(name = "Book")
    public static class Book {
        @Id
        private Long id;

        private String title;

        private String author;

        @NaturalId
        private String isbn;

        // Getters and setters are omitted for brevity
        // end::naturalid-simple-basic-attribute-mapping-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
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

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }
    }
}

