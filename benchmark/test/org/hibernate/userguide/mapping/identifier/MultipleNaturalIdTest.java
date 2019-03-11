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
import org.hibernate.annotations.NaturalId;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::naturalid-multiple-attribute-mapping-example[]
public class MultipleNaturalIdTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.identifier.Publisher publisher = new org.hibernate.userguide.mapping.identifier.Publisher();
            publisher.setId(1L);
            publisher.setName("Amazon");
            entityManager.persist(publisher);
            org.hibernate.userguide.mapping.identifier.Book book = new org.hibernate.userguide.mapping.identifier.Book();
            book.setId(1L);
            book.setTitle("High-Performance Java Persistence");
            book.setAuthor("Vlad Mihalcea");
            book.setProductNumber("973022823X");
            book.setPublisher(publisher);
            entityManager.persist(book);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.identifier.Publisher publisher = entityManager.getReference(.class, 1L);
            // tag::naturalid-load-access-example[]
            org.hibernate.userguide.mapping.identifier.Book book = entityManager.unwrap(.class).byNaturalId(.class).using("productNumber", "973022823X").using("publisher", publisher).load();
            // end::naturalid-load-access-example[]
            assertEquals("High-Performance Java Persistence", book.getTitle());
        });
    }

    // tag::naturalid-multiple-attribute-mapping-example[]
    // tag::naturalid-multiple-attribute-mapping-example[]
    @Entity(name = "Book")
    public static class Book {
        @Id
        private Long id;

        private String title;

        private String author;

        @NaturalId
        private String productNumber;

        @NaturalId
        @ManyToOne(fetch = FetchType.LAZY)
        private MultipleNaturalIdTest.Publisher publisher;

        // Getters and setters are omitted for brevity
        // end::naturalid-multiple-attribute-mapping-example[]
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

        public String getProductNumber() {
            return productNumber;
        }

        public void setProductNumber(String productNumber) {
            this.productNumber = productNumber;
        }

        public MultipleNaturalIdTest.Publisher getPublisher() {
            return publisher;
        }

        public void setPublisher(MultipleNaturalIdTest.Publisher publisher) {
            this.publisher = publisher;
        }
    }

    @Entity(name = "Publisher")
    public static class Publisher implements Serializable {
        @Id
        private Long id;

        private String name;

        // Getters and setters are omitted for brevity
        // end::naturalid-multiple-attribute-mapping-example[]
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

        // tag::naturalid-multiple-attribute-mapping-example[]
        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            MultipleNaturalIdTest.Publisher publisher = ((MultipleNaturalIdTest.Publisher) (o));
            return (Objects.equals(id, publisher.id)) && (Objects.equals(name, publisher.name));
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }
}

