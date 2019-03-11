/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.insertordering;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-12380")
public class InsertOrderingHasParentTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testInsert() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.insertordering.Book book = new org.hibernate.test.insertordering.Book();
            book.setComment(new org.hibernate.test.insertordering.Comment("first comment"));
            book.setComments(Arrays.asList(new org.hibernate.test.insertordering.Comment("second comment")));
            org.hibernate.test.insertordering.Author author = new org.hibernate.test.insertordering.Author();
            author.setBook(book);
            session.persist(author);
        });
    }

    @Entity(name = "Author")
    public static class Author {
        @Id
        @GeneratedValue
        Long id;

        @OneToOne(cascade = CascadeType.ALL)
        InsertOrderingHasParentTest.Book book;

        public InsertOrderingHasParentTest.Book getBook() {
            return book;
        }

        public void setBook(InsertOrderingHasParentTest.Book book) {
            this.book = book;
        }
    }

    @Entity(name = "Book")
    public static class Book {
        @Id
        @GeneratedValue
        Long id;

        @OneToOne(cascade = CascadeType.ALL)
        InsertOrderingHasParentTest.Comment comment;

        @ManyToMany(cascade = CascadeType.ALL)
        List<InsertOrderingHasParentTest.Comment> comments = new ArrayList<>();

        public InsertOrderingHasParentTest.Comment getComment() {
            return comment;
        }

        public void setComment(InsertOrderingHasParentTest.Comment comment) {
            this.comment = comment;
        }

        public List<InsertOrderingHasParentTest.Comment> getComments() {
            return comments;
        }

        public void setComments(List<InsertOrderingHasParentTest.Comment> comments) {
            this.comments = comments;
        }
    }

    @Entity(name = "Comment")
    @Table(name = "book_comment")
    public static class Comment {
        @Id
        @GeneratedValue
        Long id;

        @Column(name = "book_comment")
        String comment;

        public Comment() {
        }

        public Comment(String comment) {
            this.comment = comment;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}

