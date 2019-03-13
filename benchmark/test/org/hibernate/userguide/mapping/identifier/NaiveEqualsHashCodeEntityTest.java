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
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::entity-pojo-naive-equals-hashcode-example[]
public class NaiveEqualsHashCodeEntityTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testPersist() {
        // tag::entity-pojo-naive-equals-hashcode-persist-example[]
        NaiveEqualsHashCodeEntityTest.Book book1 = new NaiveEqualsHashCodeEntityTest.Book();
        book1.setTitle("High-Performance Java Persistence");
        NaiveEqualsHashCodeEntityTest.Book book2 = new NaiveEqualsHashCodeEntityTest.Book();
        book2.setTitle("Java Persistence with Hibernate");
        NaiveEqualsHashCodeEntityTest.Library library = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.identifier.Library _library = entityManager.find(.class, 1L);
            _library.getBooks().add(book1);
            _library.getBooks().add(book2);
            return _library;
        });
        Assert.assertFalse(library.getBooks().contains(book1));
        Assert.assertFalse(library.getBooks().contains(book2));
        // end::entity-pojo-naive-equals-hashcode-persist-example[]
    }

    @Test
    public void testPersistForceFlush() {
        // tag::entity-pojo-naive-equals-hashcode-persist-force-flush-example[]
        NaiveEqualsHashCodeEntityTest.Book book1 = new NaiveEqualsHashCodeEntityTest.Book();
        book1.setTitle("High-Performance Java Persistence");
        NaiveEqualsHashCodeEntityTest.Book book2 = new NaiveEqualsHashCodeEntityTest.Book();
        book2.setTitle("Java Persistence with Hibernate");
        NaiveEqualsHashCodeEntityTest.Library library = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.identifier.Library _library = entityManager.find(.class, 1L);
            entityManager.persist(book1);
            entityManager.persist(book2);
            entityManager.flush();
            _library.getBooks().add(book1);
            _library.getBooks().add(book2);
            return _library;
        });
        Assert.assertTrue(library.getBooks().contains(book1));
        Assert.assertTrue(library.getBooks().contains(book2));
        // end::entity-pojo-naive-equals-hashcode-persist-force-flush-example[]
    }

    // tag::entity-pojo-naive-equals-hashcode-example[]
    // tag::entity-pojo-naive-equals-hashcode-example[]
    @Entity(name = "Library")
    public static class Library {
        @Id
        private Long id;

        private String name;

        @OneToMany(cascade = CascadeType.ALL)
        @JoinColumn(name = "book_id")
        private Set<NaiveEqualsHashCodeEntityTest.Book> books = new HashSet<>();

        // Getters and setters are omitted for brevity
        // end::entity-pojo-naive-equals-hashcode-example[]
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

        public Set<NaiveEqualsHashCodeEntityTest.Book> getBooks() {
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

        // Getters and setters are omitted for brevity
        // end::entity-pojo-naive-equals-hashcode-example[]
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

        // tag::entity-pojo-naive-equals-hashcode-example[]
        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            NaiveEqualsHashCodeEntityTest.Book book = ((NaiveEqualsHashCodeEntityTest.Book) (o));
            return Objects.equals(id, book.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}

