/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.identifier;


import java.util.HashSet;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
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
// end::entity-pojo-mapping-example[]
public class SimpleEntityTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testIdentityScope() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::entity-pojo-identity-scope-example[]
            org.hibernate.userguide.mapping.identifier.Book book1 = entityManager.find(.class, 1L);
            org.hibernate.userguide.mapping.identifier.Book book2 = entityManager.find(.class, 1L);
            assertTrue((book1 == book2));
            // end::entity-pojo-identity-scope-example[]
        });
    }

    @Test
    public void testSetIdentityScope() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::entity-pojo-set-identity-scope-example[]
            org.hibernate.userguide.mapping.identifier.Library library = entityManager.find(.class, 1L);
            org.hibernate.userguide.mapping.identifier.Book book1 = entityManager.find(.class, 1L);
            org.hibernate.userguide.mapping.identifier.Book book2 = entityManager.find(.class, 1L);
            library.getBooks().add(book1);
            library.getBooks().add(book2);
            assertEquals(1, library.getBooks().size());
            // end::entity-pojo-set-identity-scope-example[]
        });
    }

    @Test
    public void testMultiSessionIdentityScope() {
        // tag::entity-pojo-multi-session-identity-scope-example[]
        SimpleEntityTest.Book book1 = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            return entityManager.find(.class, 1L);
        });
        SimpleEntityTest.Book book2 = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            return entityManager.find(.class, 1L);
        });
        Assert.assertFalse((book1 == book2));
        // end::entity-pojo-multi-session-identity-scope-example[]
    }

    @Test
    public void testMultiSessionSetIdentityScope() {
        SimpleEntityTest.Book book1 = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            return entityManager.find(.class, 1L);
        });
        SimpleEntityTest.Book book2 = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            return entityManager.find(.class, 1L);
        });
        // tag::entity-pojo-multi-session-set-identity-scope-example[]
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Set<org.hibernate.userguide.mapping.identifier.Book> books = new HashSet<>();
            books.add(book1);
            books.add(book2);
            assertEquals(2, books.size());
        });
        // end::entity-pojo-multi-session-set-identity-scope-example[]
    }

    @Test
    public void testTransientSetIdentityScope() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::entity-pojo-transient-set-identity-scope-example[]
            org.hibernate.userguide.mapping.identifier.Library library = entityManager.find(.class, 1L);
            org.hibernate.userguide.mapping.identifier.Book book1 = new org.hibernate.userguide.mapping.identifier.Book();
            book1.setId(100L);
            book1.setTitle("High-Performance Java Persistence");
            org.hibernate.userguide.mapping.identifier.Book book2 = new org.hibernate.userguide.mapping.identifier.Book();
            book2.setId(101L);
            book2.setTitle("Java Persistence with Hibernate");
            library.getBooks().add(book1);
            library.getBooks().add(book2);
            assertEquals(2, library.getBooks().size());
            // end::entity-pojo-transient-set-identity-scope-example[]
        });
    }

    // tag::entity-pojo-set-mapping-example[]
    // tag::entity-pojo-set-mapping-example[]
    @Entity(name = "Library")
    public static class Library {
        @Id
        private Long id;

        private String name;

        @OneToMany(cascade = CascadeType.ALL)
        @JoinColumn(name = "book_id")
        private java.util.Set<SimpleEntityTest.Book> books = new HashSet<>();

        // Getters and setters are omitted for brevity
        // end::entity-pojo-set-mapping-example[]
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

        public java.util.Set<SimpleEntityTest.Book> getBooks() {
            return books;
        }
    }

    // end::entity-pojo-set-mapping-example[]
    // tag::entity-pojo-mapping-example[]
    // tag::entity-pojo-mapping-example[]
    @Entity(name = "Book")
    public static class Book {
        // tag::entity-pojo-identifier-mapping-example[]
        @Id
        private Long id;

        // end::entity-pojo-identifier-mapping-example[]
        private String title;

        private String author;

        // Getters and setters are omitted for brevity
        // end::entity-pojo-mapping-example[]
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
    }
}

