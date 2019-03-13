/**
 * (c) ????? ??, 2016. ??? ????? ????????.
 */
package org.baeldung.querydsl.intro;


import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.baeldung.querydsl.intro.entities.QBlogPost;
import org.baeldung.querydsl.intro.entities.QUser;
import org.baeldung.querydsl.intro.entities.User;
import org.junit.Assert;
import org.junit.Test;


public class QueryDSLIntegrationTest {
    private static EntityManagerFactory emf;

    private EntityManager em;

    private JPAQueryFactory queryFactory;

    @Test
    public void whenFindByLogin_thenShouldReturnUser() {
        QUser user = QUser.user;
        User aUser = queryFactory.selectFrom(user).where(user.login.eq("David")).fetchOne();
        Assert.assertNotNull(aUser);
        Assert.assertEquals(aUser.getLogin(), "David");
    }

    @Test
    public void whenUsingOrderBy_thenResultsShouldBeOrdered() {
        QUser user = QUser.user;
        List<User> users = queryFactory.selectFrom(user).orderBy(user.login.asc()).fetch();
        Assert.assertEquals(users.size(), 4);
        Assert.assertEquals(users.get(0).getLogin(), "Ash");
        Assert.assertEquals(users.get(1).getLogin(), "Bishop");
        Assert.assertEquals(users.get(2).getLogin(), "Call");
        Assert.assertEquals(users.get(3).getLogin(), "David");
    }

    @Test
    public void whenGroupingByTitle_thenReturnsTuples() {
        QBlogPost blogPost = QBlogPost.blogPost;
        NumberPath<Long> count = Expressions.numberPath(Long.class, "c");
        List<Tuple> userTitleCounts = queryFactory.select(blogPost.title, blogPost.id.count().as(count)).from(blogPost).groupBy(blogPost.title).orderBy(count.desc()).fetch();
        Assert.assertEquals("Hello World!", userTitleCounts.get(0).get(blogPost.title));
        Assert.assertEquals(new Long(2), userTitleCounts.get(0).get(count));
        Assert.assertEquals("My Second Post", userTitleCounts.get(1).get(blogPost.title));
        Assert.assertEquals(new Long(1), userTitleCounts.get(1).get(count));
    }

    @Test
    public void whenJoiningWithCondition_thenResultCountShouldMatch() {
        QUser user = QUser.user;
        QBlogPost blogPost = QBlogPost.blogPost;
        List<User> users = queryFactory.selectFrom(user).innerJoin(user.blogPosts, blogPost).on(blogPost.title.eq("Hello World!")).fetch();
        Assert.assertEquals(2, users.size());
    }

    @Test
    public void whenRefiningWithSubquery_thenResultCountShouldMatch() {
        QUser user = QUser.user;
        QBlogPost blogPost = QBlogPost.blogPost;
        List<User> users = queryFactory.selectFrom(user).where(user.id.in(JPAExpressions.select(blogPost.user.id).from(blogPost).where(blogPost.title.eq("Hello World!")))).fetch();
        Assert.assertEquals(2, users.size());
    }

    @Test
    public void whenUpdating_thenTheRecordShouldChange() {
        QUser user = QUser.user;
        queryFactory.update(user).where(user.login.eq("Ash")).set(user.login, "Ash2").set(user.disabled, true).execute();
        em.getTransaction().commit();
        em.getTransaction().begin();
        Assert.assertEquals(Boolean.TRUE, queryFactory.select(user.disabled).from(user).where(user.login.eq("Ash2")).fetchOne());
    }

    @Test
    public void whenDeleting_thenTheRecordShouldBeAbsent() {
        QUser user = QUser.user;
        queryFactory.delete(user).where(user.login.eq("Bishop")).execute();
        em.getTransaction().commit();
        em.getTransaction().begin();
        Assert.assertNull(queryFactory.selectFrom(user).where(user.login.eq("Bishop")).fetchOne());
    }
}

