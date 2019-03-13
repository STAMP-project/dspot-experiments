/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.embeddable;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::embeddable-type-mapping-example[]
public class SimpleEmbeddableTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.embeddable.Book book = new org.hibernate.userguide.mapping.embeddable.Book();
            book.setTitle("High-Performance Java Persistence");
            book.setAuthor("Vlad Mihalcea");
            book.setPublisher(new org.hibernate.userguide.mapping.embeddable.Publisher("Amazon", "USA"));
            entityManager.persist(book);
        });
    }

    // tag::embeddable-type-mapping-example[]
    // tag::embeddable-type-mapping-example[]
    @Entity(name = "Book")
    public static class Book {
        @Id
        @GeneratedValue
        private Long id;

        private String title;

        private String author;

        private SimpleEmbeddableTest.Publisher publisher;

        // Getters and setters are omitted for brevity
        // end::embeddable-type-mapping-example[]
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

        public SimpleEmbeddableTest.Publisher getPublisher() {
            return publisher;
        }

        public void setPublisher(SimpleEmbeddableTest.Publisher publisher) {
            this.publisher = publisher;
        }
    }

    // tag::embeddable-type-mapping-example[]
    @Embeddable
    public static class Publisher {
        @Column(name = "publisher_name")
        private String name;

        @Column(name = "publisher_country")
        private String country;

        // Getters and setters, equals and hashCode methods omitted for brevity
        // end::embeddable-type-mapping-example[]
        public Publisher(String name, String country) {
            this.name = name;
            this.country = country;
        }

        private Publisher() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }
}

