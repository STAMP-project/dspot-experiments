/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.embeddable;


import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.Session;
import org.hibernate.annotations.NaturalId;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.SkipForDialect;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::embeddable-type-association-mapping-example[]
@SkipForDialect(Oracle8iDialect.class)
public class EmbeddableOverrideTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.embeddable.Country canada = new org.hibernate.userguide.mapping.embeddable.Country();
            canada.setName("Canada");
            entityManager.persist(canada);
            org.hibernate.userguide.mapping.embeddable.Country usa = new org.hibernate.userguide.mapping.embeddable.Country();
            usa.setName("USA");
            entityManager.persist(usa);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            org.hibernate.userguide.mapping.embeddable.Country canada = session.byNaturalId(.class).using("name", "Canada").load();
            org.hibernate.userguide.mapping.embeddable.Country usa = session.byNaturalId(.class).using("name", "USA").load();
            org.hibernate.userguide.mapping.embeddable.Book book = new org.hibernate.userguide.mapping.embeddable.Book();
            book.setTitle("High-Performance Java Persistence");
            book.setAuthor("Vlad Mihalcea");
            book.setEbookPublisher(new org.hibernate.userguide.mapping.embeddable.Publisher("Leanpub", canada));
            book.setPaperBackPublisher(new org.hibernate.userguide.mapping.embeddable.Publisher("Amazon", usa));
            entityManager.persist(book);
        });
    }

    // tag::embeddable-type-override-mapping-example[]
    // tag::embeddable-type-override-mapping-example[]
    @Entity(name = "Book")
    @AttributeOverrides({ @AttributeOverride(name = "ebookPublisher.name", column = @Column(name = "ebook_publisher_name")), @AttributeOverride(name = "paperBackPublisher.name", column = @Column(name = "paper_back_publisher_name")) })
    @AssociationOverrides({ @AssociationOverride(name = "ebookPublisher.country", joinColumns = @JoinColumn(name = "ebook_publisher_country_id")), @AssociationOverride(name = "paperBackPublisher.country", joinColumns = @JoinColumn(name = "paper_back_publisher_country_id")) })
    public static class Book {
        @Id
        @GeneratedValue
        private Long id;

        private String title;

        private String author;

        private EmbeddableOverrideTest.Publisher ebookPublisher;

        private EmbeddableOverrideTest.Publisher paperBackPublisher;

        // Getters and setters are omitted for brevity
        // end::embeddable-type-override-mapping-example[]
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

        public EmbeddableOverrideTest.Publisher getEbookPublisher() {
            return ebookPublisher;
        }

        public void setEbookPublisher(EmbeddableOverrideTest.Publisher ebookPublisher) {
            this.ebookPublisher = ebookPublisher;
        }

        public EmbeddableOverrideTest.Publisher getPaperBackPublisher() {
            return paperBackPublisher;
        }

        public void setPaperBackPublisher(EmbeddableOverrideTest.Publisher paperBackPublisher) {
            this.paperBackPublisher = paperBackPublisher;
        }
    }

    // end::embeddable-type-override-mapping-example[]
    // tag::embeddable-type-association-mapping-example[]
    // tag::embeddable-type-association-mapping-example[]
    @Embeddable
    public static class Publisher {
        private String name;

        @ManyToOne(fetch = FetchType.LAZY)
        private EmbeddableOverrideTest.Country country;

        // Getters and setters, equals and hashCode methods omitted for brevity
        // end::embeddable-type-association-mapping-example[]
        public Publisher(String name, EmbeddableOverrideTest.Country country) {
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

        public EmbeddableOverrideTest.Country getCountry() {
            return country;
        }

        public void setCountry(EmbeddableOverrideTest.Country country) {
            this.country = country;
        }
    }

    // tag::embeddable-type-association-mapping-example[]
    @Entity(name = "Country")
    public static class Country {
        @Id
        @GeneratedValue
        private Long id;

        @NaturalId
        private String name;

        // Getters and setters are omitted for brevity
        // end::embeddable-type-association-mapping-example[]
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

