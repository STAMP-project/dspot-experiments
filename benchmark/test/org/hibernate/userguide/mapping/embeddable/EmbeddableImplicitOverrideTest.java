/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.embeddable;


import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.NaturalId;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::embeddable-multiple-namingstrategy-entity-mapping[]
public class EmbeddableImplicitOverrideTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.userguide.mapping.embeddable.Country canada = new org.hibernate.userguide.mapping.embeddable.Country();
            canada.setName("Canada");
            session.persist(canada);
            org.hibernate.userguide.mapping.embeddable.Country usa = new org.hibernate.userguide.mapping.embeddable.Country();
            usa.setName("USA");
            session.persist(usa);
        });
        doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.userguide.mapping.embeddable.Country canada = session.byNaturalId(.class).using("name", "Canada").load();
            org.hibernate.userguide.mapping.embeddable.Country usa = session.byNaturalId(.class).using("name", "USA").load();
            org.hibernate.userguide.mapping.embeddable.Book book = new org.hibernate.userguide.mapping.embeddable.Book();
            book.setTitle("High-Performance Java Persistence");
            book.setAuthor("Vlad Mihalcea");
            book.setEbookPublisher(new org.hibernate.userguide.mapping.embeddable.Publisher("Leanpub", canada));
            book.setPaperBackPublisher(new org.hibernate.userguide.mapping.embeddable.Publisher("Amazon", usa));
            session.persist(book);
        });
    }

    // tag::embeddable-multiple-namingstrategy-entity-mapping[]
    // tag::embeddable-multiple-namingstrategy-entity-mapping[]
    @Entity(name = "Book")
    public static class Book {
        @Id
        @GeneratedValue
        private Long id;

        private String title;

        private String author;

        private EmbeddableImplicitOverrideTest.Publisher ebookPublisher;

        private EmbeddableImplicitOverrideTest.Publisher paperBackPublisher;

        // Getters and setters are omitted for brevity
        // end::embeddable-multiple-namingstrategy-entity-mapping[]
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

        public EmbeddableImplicitOverrideTest.Publisher getEbookPublisher() {
            return ebookPublisher;
        }

        public void setEbookPublisher(EmbeddableImplicitOverrideTest.Publisher ebookPublisher) {
            this.ebookPublisher = ebookPublisher;
        }

        public EmbeddableImplicitOverrideTest.Publisher getPaperBackPublisher() {
            return paperBackPublisher;
        }

        public void setPaperBackPublisher(EmbeddableImplicitOverrideTest.Publisher paperBackPublisher) {
            this.paperBackPublisher = paperBackPublisher;
        }
    }

    // tag::embeddable-multiple-namingstrategy-entity-mapping[]
    @Embeddable
    public static class Publisher {
        private String name;

        @ManyToOne(fetch = FetchType.LAZY)
        private EmbeddableImplicitOverrideTest.Country country;

        // Getters and setters, equals and hashCode methods omitted for brevity
        // end::embeddable-multiple-namingstrategy-entity-mapping[]
        public Publisher(String name, EmbeddableImplicitOverrideTest.Country country) {
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

        public EmbeddableImplicitOverrideTest.Country getCountry() {
            return country;
        }

        public void setCountry(EmbeddableImplicitOverrideTest.Country country) {
            this.country = country;
        }
    }

    // tag::embeddable-multiple-namingstrategy-entity-mapping[]
    @Entity(name = "Country")
    public static class Country {
        @Id
        @GeneratedValue
        private Long id;

        @NaturalId
        private String name;

        // Getters and setters are omitted for brevity
        // end::embeddable-multiple-namingstrategy-entity-mapping[]
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

