/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.collection.bag;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.loader.MultipleBagFetchException;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


public class MultipleBagFetchHqlTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testMultipleBagFetchHql() throws Exception {
        Session session = openSession();
        Transaction transaction = session.beginTransaction();
        MultipleBagFetchHqlTest.Post post = new MultipleBagFetchHqlTest.Post();
        post.setId(1L);
        post.setTitle(String.format("Post nr. %d", 1));
        MultipleBagFetchHqlTest.PostComment comment = new MultipleBagFetchHqlTest.PostComment();
        comment.setId(1L);
        comment.setReview("Excellent!");
        session.persist(post);
        session.persist(comment);
        post.comments.add(comment);
        transaction.commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        try {
            post = ((MultipleBagFetchHqlTest.Post) (session.createQuery(("select p " + ((("from Post p " + "join fetch p.tags ") + "join fetch p.comments ") + "where p.id = :id"))).setParameter("id", 1L).uniqueResult()));
            Assert.fail("Should throw org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags");
        } catch (IllegalArgumentException expected) {
            session.getTransaction().rollback();
            // MultipleBagFetchException was converted to IllegalArgumentException
            Assert.assertTrue(MultipleBagFetchException.class.isInstance(expected.getCause()));
        } finally {
            session.close();
        }
    }

    @Entity(name = "Post")
    @Table(name = "post")
    public static class Post {
        @Id
        private Long id;

        private String title;

        @OneToMany(fetch = FetchType.LAZY)
        private List<MultipleBagFetchHqlTest.PostComment> comments = new ArrayList<MultipleBagFetchHqlTest.PostComment>();

        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "post_tag", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
        private List<MultipleBagFetchHqlTest.Tag> tags = new ArrayList<MultipleBagFetchHqlTest.Tag>();

        public Post() {
        }

        public Post(Long id) {
            this.id = id;
        }

        public Post(String title) {
            this.title = title;
        }

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

        public List<MultipleBagFetchHqlTest.Tag> getTags() {
            return tags;
        }
    }

    @Entity(name = "PostComment")
    @Table(name = "post_comment")
    public static class PostComment {
        @Id
        private Long id;

        private String review;

        public PostComment() {
        }

        public PostComment(String review) {
            this.review = review;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getReview() {
            return review;
        }

        public void setReview(String review) {
            this.review = review;
        }
    }

    @Entity(name = "Tag")
    @Table(name = "tag")
    public static class Tag {
        @Id
        private Long id;

        private String name;

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

