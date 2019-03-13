/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.identifier;


import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
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
// end::naturalid-single-embedded-attribute-mapping-example[]
public class CompositeNaturalIdTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.identifier.Book book = new org.hibernate.userguide.mapping.identifier.Book();
            book.setId(1L);
            book.setTitle("High-Performance Java Persistence");
            book.setAuthor("Vlad Mihalcea");
            book.setIsbn(new org.hibernate.userguide.mapping.identifier.Isbn("973022823X", "978-9730228236"));
            entityManager.persist(book);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::naturalid-simple-load-access-example[]
            org.hibernate.userguide.mapping.identifier.Book book = entityManager.unwrap(.class).bySimpleNaturalId(.class).load(new org.hibernate.userguide.mapping.identifier.Isbn("973022823X", "978-9730228236"));
            // end::naturalid-simple-load-access-example[]
            assertEquals("High-Performance Java Persistence", book.getTitle());
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::naturalid-load-access-example[]
            org.hibernate.userguide.mapping.identifier.Book book = entityManager.unwrap(.class).byNaturalId(.class).using("isbn", new org.hibernate.userguide.mapping.identifier.Isbn("973022823X", "978-9730228236")).load();
            // end::naturalid-load-access-example[]
            assertEquals("High-Performance Java Persistence", book.getTitle());
        });
    }

    // tag::naturalid-single-embedded-attribute-mapping-example[]
    // tag::naturalid-single-embedded-attribute-mapping-example[]
    @Entity(name = "Book")
    public static class Book {
        @Id
        private Long id;

        private String title;

        private String author;

        @NaturalId
        @Embedded
        private CompositeNaturalIdTest.Isbn isbn;

        // Getters and setters are omitted for brevity
        // end::naturalid-single-embedded-attribute-mapping-example[]
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

        public CompositeNaturalIdTest.Isbn getIsbn() {
            return isbn;
        }

        public void setIsbn(CompositeNaturalIdTest.Isbn isbn) {
            this.isbn = isbn;
        }
    }

    @Embeddable
    public static class Isbn implements Serializable {
        private String isbn10;

        private String isbn13;

        // Getters and setters are omitted for brevity
        // end::naturalid-single-embedded-attribute-mapping-example[]
        public Isbn() {
        }

        public Isbn(String isbn10, String isbn13) {
            this.isbn10 = isbn10;
            this.isbn13 = isbn13;
        }

        public String getIsbn10() {
            return isbn10;
        }

        public void setIsbn10(String isbn10) {
            this.isbn10 = isbn10;
        }

        public String getIsbn13() {
            return isbn13;
        }

        public void setIsbn13(String isbn13) {
            this.isbn13 = isbn13;
        }

        // tag::naturalid-single-embedded-attribute-mapping-example[]
        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            CompositeNaturalIdTest.Isbn isbn = ((CompositeNaturalIdTest.Isbn) (o));
            return (Objects.equals(isbn10, isbn.isbn10)) && (Objects.equals(isbn13, isbn.isbn13));
        }

        @Override
        public int hashCode() {
            return Objects.hash(isbn10, isbn13);
        }
    }
}

