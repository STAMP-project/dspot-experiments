/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.identifier;


import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import org.hibernate.annotations.NaturalId;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::entity-pojo-natural-id-equals-hashcode-example[]
public class NaturalIdEqualsHashCodeEntityTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testPersist() {
        // tag::entity-pojo-natural-id-equals-hashcode-persist-example[]
        NaturalIdEqualsHashCodeEntityTest.Book book1 = new NaturalIdEqualsHashCodeEntityTest.Book();
        book1.setTitle("High-Performance Java Persistence");
        book1.setIsbn("978-9730228236");
        NaturalIdEqualsHashCodeEntityTest.Library library = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.identifier.Library _library = entityManager.find(.class, 1L);
            _library.getBooks().add(book1);
            return _library;
        });
        Assert.assertTrue(library.getBooks().contains(book1));
        // end::entity-pojo-natural-id-equals-hashcode-persist-example[]
    }

    // tag::entity-pojo-natural-id-equals-hashcode-example[]
    // tag::entity-pojo-natural-id-equals-hashcode-example[]
    @Entity(name = "Library")
    public static class Library {
        @Id
        private Long id;

        private String name;

        @OneToMany(cascade = CascadeType.ALL)
        @JoinColumn(name = "book_id")
        private Set<NaturalIdEqualsHashCodeEntityTest.Book> books = new HashSet<>();

        // Getters and setters are omitted for brevity
        // end::entity-pojo-natural-id-equals-hashcode-example[]
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

        public Set<NaturalIdEqualsHashCodeEntityTest.Book> getBooks() {
            return books;
        }
    }

    @Entity(name = "Book")
    public static class Book {
        @Id
        @GeneratedValue
        private Long id;

        private String title;

        private String author;

        @NaturalId
        private String isbn;

        // Getters and setters are omitted for brevity
        // end::entity-pojo-natural-id-equals-hashcode-example[]
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

        // tag::entity-pojo-natural-id-equals-hashcode-example[]
        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            NaturalIdEqualsHashCodeEntityTest.Book book = ((NaturalIdEqualsHashCodeEntityTest.Book) (o));
            return Objects.equals(isbn, book.isbn);
        }

        @Override
        public int hashCode() {
            return Objects.hash(isbn);
        }
    }
}

