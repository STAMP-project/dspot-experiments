/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.ids.embeddedid;


import RevisionType.DEL;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.envers.Audited;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test an audited entity with an embeddable composite key that has an association
 * to a non-audited entity type.
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-12498")
public class RelationInsideEmbeddableNotAuditedTest extends BaseEnversJPAFunctionalTestCase {
    private Integer authorId;

    private RelationInsideEmbeddableNotAuditedTest.BookId bookId1;

    private RelationInsideEmbeddableNotAuditedTest.BookId bookId2;

    @Test
    @Priority(10)
    public void initData() {
        // Revision 1, persist author and book
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.ids.embeddedid.Author author = new org.hibernate.envers.test.integration.ids.embeddedid.Author();
            author.setName("Stephen King");
            entityManager.persist(author);
            authorId = author.getId();
            final org.hibernate.envers.test.integration.ids.embeddedid.Book book = new org.hibernate.envers.test.integration.ids.embeddedid.Book();
            book.setId(new org.hibernate.envers.test.integration.ids.embeddedid.BookId());
            book.getId().setId(1);
            book.getId().setAuthor(author);
            book.setName("Gunslinger");
            book.setEdition(1);
            entityManager.persist(book);
            this.bookId1 = book.getId();
        });
        // Revision 2, persist new book
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.ids.embeddedid.Author author = entityManager.find(.class, authorId);
            final org.hibernate.envers.test.integration.ids.embeddedid.Book book = new org.hibernate.envers.test.integration.ids.embeddedid.Book();
            book.setId(new org.hibernate.envers.test.integration.ids.embeddedid.BookId());
            book.getId().setId(2);
            book.getId().setAuthor(author);
            book.setName("Gunslinger");
            book.setEdition(2);
            entityManager.persist(book);
            this.bookId2 = book.getId();
        });
        // Modify books
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.ids.embeddedid.Book book1 = entityManager.find(.class, bookId1);
            book1.setName("Gunslinger: Dark Tower");
            entityManager.merge(book1);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.ids.embeddedid.Book book2 = entityManager.find(.class, bookId2);
            book2.setName("Gunslinger: Dark Tower");
            entityManager.merge(book2);
        });
        // ! Delete books
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.ids.embeddedid.Book book1 = entityManager.find(.class, bookId1);
            entityManager.remove(book1);
            final org.hibernate.envers.test.integration.ids.embeddedid.Book book2 = entityManager.find(.class, bookId2);
            entityManager.remove(book2);
        });
    }

    @Test
    public void tesRevisionCounts() {
        Assert.assertEquals(Arrays.asList(1, 3, 5), getAuditReader().getRevisions(RelationInsideEmbeddableNotAuditedTest.Book.class, bookId1));
        Assert.assertEquals(Arrays.asList(2, 4, 5), getAuditReader().getRevisions(RelationInsideEmbeddableNotAuditedTest.Book.class, bookId2));
    }

    @Test
    public void testRevisionHistoryBook1() {
        final RelationInsideEmbeddableNotAuditedTest.Book rev1 = getAuditReader().find(RelationInsideEmbeddableNotAuditedTest.Book.class, bookId1, 1);
        Assert.assertNotNull(rev1.getId().getAuthor());
        final RelationInsideEmbeddableNotAuditedTest.Book rev3 = getAuditReader().find(RelationInsideEmbeddableNotAuditedTest.Book.class, bookId1, 3);
        Assert.assertNotNull(rev3.getId().getAuthor());
        final RelationInsideEmbeddableNotAuditedTest.Book rev5 = getAuditReader().find(RelationInsideEmbeddableNotAuditedTest.Book.class, bookId1, 5);
        Assert.assertNull(rev5);
    }

    @Test
    public void testRevisionHistoryBook2() {
        final RelationInsideEmbeddableNotAuditedTest.Book rev2 = getAuditReader().find(RelationInsideEmbeddableNotAuditedTest.Book.class, bookId2, 2);
        Assert.assertNotNull(rev2.getId().getAuthor());
        final RelationInsideEmbeddableNotAuditedTest.Book rev4 = getAuditReader().find(RelationInsideEmbeddableNotAuditedTest.Book.class, bookId2, 4);
        Assert.assertNotNull(rev4.getId().getAuthor());
        final RelationInsideEmbeddableNotAuditedTest.Book rev5 = getAuditReader().find(RelationInsideEmbeddableNotAuditedTest.Book.class, bookId2, 5);
        Assert.assertNull(rev5);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectDeletedEntitiesBook1() {
        List<RelationInsideEmbeddableNotAuditedTest.Book> books = ((List<RelationInsideEmbeddableNotAuditedTest.Book>) (getAuditReader().createQuery().forRevisionsOfEntity(RelationInsideEmbeddableNotAuditedTest.Book.class, true, true).add(AuditEntity.id().eq(bookId1)).add(AuditEntity.revisionType().eq(DEL)).getResultList()));
        Assert.assertTrue((!(books.isEmpty())));
        final RelationInsideEmbeddableNotAuditedTest.Book book = books.get(0);
        Assert.assertNotNull(book.getId());
        Assert.assertNotNull(book.getId().getAuthor());
        Assert.assertEquals(authorId, book.getId().getAuthor().getId());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelectDeletedEntitiesBook2() {
        List<RelationInsideEmbeddableNotAuditedTest.Book> books = ((List<RelationInsideEmbeddableNotAuditedTest.Book>) (getAuditReader().createQuery().forRevisionsOfEntity(RelationInsideEmbeddableNotAuditedTest.Book.class, true, true).add(AuditEntity.id().eq(bookId2)).add(AuditEntity.revisionType().eq(DEL)).getResultList()));
        Assert.assertTrue((!(books.isEmpty())));
        final RelationInsideEmbeddableNotAuditedTest.Book book = books.get(0);
        Assert.assertNotNull(book.getId());
        Assert.assertNotNull(book.getId().getAuthor());
        Assert.assertEquals(authorId, book.getId().getAuthor().getId());
    }

    @Audited
    @Entity(name = "Book")
    public static class Book {
        @EmbeddedId
        private RelationInsideEmbeddableNotAuditedTest.BookId id;

        private String name;

        private Integer edition;

        public RelationInsideEmbeddableNotAuditedTest.BookId getId() {
            return id;
        }

        public void setId(RelationInsideEmbeddableNotAuditedTest.BookId id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getEdition() {
            return edition;
        }

        public void setEdition(Integer edition) {
            this.edition = edition;
        }
    }

    @Embeddable
    public static class BookId implements Serializable {
        private Integer id;

        @ManyToOne(fetch = FetchType.LAZY)
        private RelationInsideEmbeddableNotAuditedTest.Author author;

        BookId() {
        }

        BookId(Integer id, RelationInsideEmbeddableNotAuditedTest.Author author) {
            this.id = id;
            this.author = author;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public RelationInsideEmbeddableNotAuditedTest.Author getAuthor() {
            return author;
        }

        public void setAuthor(RelationInsideEmbeddableNotAuditedTest.Author author) {
            this.author = author;
        }
    }

    @Entity(name = "Author")
    public static class Author {
        @Id
        @GeneratedValue
        private Integer id;

        private String name;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
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

