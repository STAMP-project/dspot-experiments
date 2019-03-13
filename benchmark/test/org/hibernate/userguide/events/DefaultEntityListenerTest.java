/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.events;


import javax.persistence.Entity;
import javax.persistence.ExcludeDefaultListeners;
import javax.persistence.ExcludeSuperclassListeners;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::events-exclude-default-listener-mapping-example[]
public class DefaultEntityListenerTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::events-default-listener-persist-example[]
            org.hibernate.userguide.events.Person author = new org.hibernate.userguide.events.Person();
            author.setId(1L);
            author.setName("Vlad Mihalcea");
            entityManager.persist(author);
            org.hibernate.userguide.events.Book book = new org.hibernate.userguide.events.Book();
            book.setId(1L);
            book.setTitle("High-Performance Java Persistence");
            book.setAuthor(author);
            entityManager.persist(book);
            // end::events-default-listener-persist-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::events-default-listener-update-example[]
            org.hibernate.userguide.events.Person author = entityManager.find(.class, 1L);
            author.setName("Vlad-Alexandru Mihalcea");
            org.hibernate.userguide.events.Book book = entityManager.find(.class, 1L);
            book.setTitle("High-Performance Java Persistence 2nd Edition");
            // end::events-default-listener-update-example[]
        });
    }

    @Test
    public void testExclude() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::events-exclude-default-listener-persist-example[]
            org.hibernate.userguide.events.Publisher publisher = new org.hibernate.userguide.events.Publisher();
            publisher.setId(1L);
            publisher.setName("Amazon");
            entityManager.persist(publisher);
            // end::events-exclude-default-listener-persist-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.events.Publisher publisher = entityManager.find(.class, 1L);
            assertNull(publisher.getCreatedOn());
        });
    }

    // tag::events-default-listener-mapping-example[]
    // tag::events-default-listener-mapping-example[]
    @Entity(name = "Person")
    public static class Person extends BaseEntity {
        @Id
        private Long id;

        private String name;

        // Getters and setters omitted for brevity
        // end::events-default-listener-mapping-example[]
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
    }

    // tag::events-default-listener-mapping-example[]
    @Entity(name = "Book")
    public static class Book extends BaseEntity {
        @Id
        private Long id;

        private String title;

        @ManyToOne
        private DefaultEntityListenerTest.Person author;

        // Getters and setters omitted for brevity
        // end::events-default-listener-mapping-example[]
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

        public DefaultEntityListenerTest.Person getAuthor() {
            return author;
        }

        public void setAuthor(DefaultEntityListenerTest.Person author) {
            this.author = author;
        }
    }

    // end::events-default-listener-mapping-example[]
    // tag::events-exclude-default-listener-mapping-example[]
    // tag::events-exclude-default-listener-mapping-example[]
    @Entity(name = "Publisher")
    @ExcludeDefaultListeners
    @ExcludeSuperclassListeners
    public static class Publisher extends BaseEntity {
        @Id
        private Long id;

        private String name;

        // Getters and setters omitted for brevity
        // end::events-exclude-default-listener-mapping-example[]
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
    }
}

