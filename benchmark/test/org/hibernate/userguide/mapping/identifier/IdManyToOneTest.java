/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.identifier;


import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::identifiers-composite-id-mapping-example[]
public class IdManyToOneTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        IdManyToOneTest.Author author = new IdManyToOneTest.Author();
        IdManyToOneTest.Publisher publisher = new IdManyToOneTest.Publisher();
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            author.setName("Vlad Mihalcea");
            entityManager.persist(author);
            publisher.setName("Amazon");
            entityManager.persist(publisher);
            org.hibernate.userguide.mapping.identifier.Book book = new org.hibernate.userguide.mapping.identifier.Book();
            book.setAuthor(author);
            book.setPublisher(publisher);
            book.setTitle("High-Performance Java Persistence");
            entityManager.persist(book);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::identifiers-composite-id-fetching-example[]
            org.hibernate.userguide.mapping.identifier.Book book = entityManager.find(.class, new org.hibernate.userguide.mapping.identifier.Book(author, publisher, "High-Performance Java Persistence"));
            assertEquals("Vlad Mihalcea", book.getAuthor().getName());
            // end::identifiers-composite-id-fetching-example[]
        });
    }

    // tag::identifiers-composite-id-mapping-example[]
    @Entity(name = "Book")
    public static class Book implements Serializable {
        @Id
        @ManyToOne(fetch = FetchType.LAZY)
        private IdManyToOneTest.Author author;

        @Id
        @ManyToOne(fetch = FetchType.LAZY)
        private IdManyToOneTest.Publisher publisher;

        @Id
        private String title;

        public Book(IdManyToOneTest.Author author, IdManyToOneTest.Publisher publisher, String title) {
            this.author = author;
            this.publisher = publisher;
            this.title = title;
        }

        private Book() {
        }

        // Getters and setters are omitted for brevity
        // end::identifiers-composite-id-mapping-example[]
        public IdManyToOneTest.Author getAuthor() {
            return author;
        }

        public void setAuthor(IdManyToOneTest.Author author) {
            this.author = author;
        }

        public IdManyToOneTest.Publisher getPublisher() {
            return publisher;
        }

        public void setPublisher(IdManyToOneTest.Publisher publisher) {
            this.publisher = publisher;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        // tag::identifiers-composite-id-mapping-example[]
        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            IdManyToOneTest.Book book = ((IdManyToOneTest.Book) (o));
            return ((Objects.equals(author, book.author)) && (Objects.equals(publisher, book.publisher))) && (Objects.equals(title, book.title));
        }

        @Override
        public int hashCode() {
            return Objects.hash(author, publisher, title);
        }
    }

    @Entity(name = "Author")
    public static class Author implements Serializable {
        @Id
        private String name;

        // Getters and setters are omitted for brevity
        // end::identifiers-composite-id-mapping-example[]
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        // tag::identifiers-composite-id-mapping-example[]
        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            IdManyToOneTest.Author author = ((IdManyToOneTest.Author) (o));
            return Objects.equals(name, author.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    @Entity(name = "Publisher")
    public static class Publisher implements Serializable {
        @Id
        private String name;

        // Getters and setters are omitted for brevity
        // end::identifiers-composite-id-mapping-example[]
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        // tag::identifiers-composite-id-mapping-example[]
        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            IdManyToOneTest.Publisher publisher = ((IdManyToOneTest.Publisher) (o));
            return Objects.equals(name, publisher.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }
}

