/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.access;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::access-embeddable-mapping-example[]
public class ElementCollectionAccessTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.access.Book book = new org.hibernate.userguide.mapping.access.Book();
            book.setId(1L);
            book.setTitle("High-Performance Java Persistence");
            book.getAuthors().add(new org.hibernate.userguide.mapping.access.Author("Vlad", "Mihalcea"));
            entityManager.persist(book);
        });
    }

    // tag::access-element-collection-mapping-example[]
    // tag::access-element-collection-mapping-example[]
    @Entity(name = "Book")
    public static class Book {
        @Id
        private Long id;

        private String title;

        @ElementCollection
        @CollectionTable(name = "book_author", joinColumns = @JoinColumn(name = "book_id"))
        private List<ElementCollectionAccessTest.Author> authors = new ArrayList<>();

        // Getters and setters are omitted for brevity
        // end::access-element-collection-mapping-example[]
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

        public List<ElementCollectionAccessTest.Author> getAuthors() {
            return authors;
        }
    }

    // end::access-element-collection-mapping-example[]
    // tag::access-embeddable-mapping-example[]
    @Embeddable
    @Access(AccessType.PROPERTY)
    public static class Author {
        private String firstName;

        private String lastName;

        public Author() {
        }

        public Author(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
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

