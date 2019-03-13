/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.update;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.hibernate.envers.Audited;
import org.hibernate.envers.test.BaseEnversFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-11056")
public class SelectBeforeUpdateTest extends BaseEnversFunctionalTestCase {
    @Test
    @Priority(10)
    public void initDataUpdateDetachedUnchanged() {
        final SelectBeforeUpdateTest.Author author = new SelectBeforeUpdateTest.Author(1, "Author1");
        final SelectBeforeUpdateTest.Book book = new SelectBeforeUpdateTest.Book(1, "Book1", author);
        // Revision 1 - insert new entities.
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.save(author);
            session.save(book);
        });
        // Revision 2 - update detached with no changes.
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.update(book);
        });
    }

    @Test
    @Priority(9)
    public void initDataUpdateDetachedChanged() {
        final SelectBeforeUpdateTest.Author author = new SelectBeforeUpdateTest.Author(2, "Author2");
        final SelectBeforeUpdateTest.Book book = new SelectBeforeUpdateTest.Book(2, "Book2", author);
        // Revision 1 - insert new entities.
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.save(author);
            session.save(book);
        });
        // Revision 2 - update detached with changes.
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            book.setName("Book2Updated");
            session.update(book);
        });
    }

    @Test
    @Priority(8)
    public void initDataUpdateDetachedUnchangedAndChanged() {
        final SelectBeforeUpdateTest.Author author = new SelectBeforeUpdateTest.Author(3, "Author3");
        final SelectBeforeUpdateTest.Book book = new SelectBeforeUpdateTest.Book(3, "Book3", author);
        // Revision 1 - insert new entities.
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.save(author);
            session.save(book);
        });
        // Revision 2 - update detached with no changes.
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.update(book);
        });
        // Revision 3 - update detached with changes.
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            book.setName("Book3Updated");
            session.update(book);
        });
    }

    @Test
    @Priority(7)
    public void initDataUpdateDetachedChangedAndUnchanged() {
        final SelectBeforeUpdateTest.Author author = new SelectBeforeUpdateTest.Author(4, "Author4");
        final SelectBeforeUpdateTest.Book book = new SelectBeforeUpdateTest.Book(4, "Book4", author);
        // Revision 1 - insert new entities.
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.save(author);
            session.save(book);
        });
        // Revision 2 - update detached with changes.
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            book.setName("Book4Updated");
            session.update(book);
        });
        // Revision 3 - update detached with no changes.
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.update(book);
        });
    }

    @Test
    public void testRevisionCountsUpdateDetachedUnchanged() {
        Assert.assertEquals(1, getAuditReader().getRevisions(SelectBeforeUpdateTest.Author.class, 1).size());
        Assert.assertEquals(1, getAuditReader().getRevisions(SelectBeforeUpdateTest.Book.class, 1).size());
    }

    @Test
    public void testRevisionCountsUpdateDetachedChanged() {
        Assert.assertEquals(1, getAuditReader().getRevisions(SelectBeforeUpdateTest.Author.class, 2).size());
        Assert.assertEquals(2, getAuditReader().getRevisions(SelectBeforeUpdateTest.Book.class, 2).size());
    }

    @Test
    public void testRevisionCountsUpdateDetachedUnchangedAndChanged() {
        Assert.assertEquals(1, getAuditReader().getRevisions(SelectBeforeUpdateTest.Author.class, 3).size());
        Assert.assertEquals(2, getAuditReader().getRevisions(SelectBeforeUpdateTest.Book.class, 3).size());
    }

    @Test
    public void testRevisionCountsUpdateDetachedChangedAndUnchanged() {
        Assert.assertEquals(1, getAuditReader().getRevisions(SelectBeforeUpdateTest.Author.class, 4).size());
        Assert.assertEquals(2, getAuditReader().getRevisions(SelectBeforeUpdateTest.Book.class, 4).size());
    }

    @Entity(name = "Book")
    @SelectBeforeUpdate
    @Audited
    public static class Book {
        @Id
        private Integer id;

        private String name;

        @ManyToOne
        @JoinColumn(updatable = false)
        private SelectBeforeUpdateTest.Author author;

        Book() {
        }

        Book(Integer id, String name, SelectBeforeUpdateTest.Author author) {
            this.id = id;
            this.name = name;
            this.author = author;
        }

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

        public SelectBeforeUpdateTest.Author getAuthor() {
            return author;
        }

        public void setAuthor(SelectBeforeUpdateTest.Author author) {
            this.author = author;
        }
    }

    @Entity(name = "Author")
    @Audited
    public static class Author {
        @Id
        private Integer id;

        private String name;

        Author() {
        }

        Author(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

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

