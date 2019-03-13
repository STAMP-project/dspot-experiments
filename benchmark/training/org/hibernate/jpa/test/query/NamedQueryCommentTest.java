/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.query;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Query;
import javax.persistence.Table;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.jdbc.SQLStatementInterceptor;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-11640")
public class NamedQueryCommentTest extends BaseEntityManagerFunctionalTestCase {
    private SQLStatementInterceptor sqlStatementInterceptor;

    private static final String[] GAME_TITLES = new String[]{ "Halo", "Grand Theft Auto", "NetHack" };

    @Test
    public void testSelectNamedQueryWithSqlComment() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            sqlStatementInterceptor.clear();
            TypedQuery<org.hibernate.jpa.test.query.Game> query = entityManager.createNamedQuery("SelectNamedQuery", .class);
            query.setParameter("title", GAME_TITLES[0]);
            List<org.hibernate.jpa.test.query.Game> list = query.getResultList();
            assertEquals(1, list.size());
            sqlStatementInterceptor.assertExecutedCount(1);
            sqlStatementInterceptor.assertExecuted("/* COMMENT_SELECT_INDEX_game_title */ select namedquery0_.id as id1_0_, namedquery0_.title as title2_0_ from game namedquery0_ where namedquery0_.title=?");
        });
    }

    @Test
    public void testSelectNamedNativeQueryWithSqlComment() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            sqlStatementInterceptor.clear();
            TypedQuery<org.hibernate.jpa.test.query.Game> query = entityManager.createNamedQuery("SelectNamedNativeQuery", .class);
            query.setParameter("title", GAME_TITLES[0]);
            List<org.hibernate.jpa.test.query.Game> list = query.getResultList();
            assertEquals(1, list.size());
            sqlStatementInterceptor.assertExecutedCount(1);
            sqlStatementInterceptor.assertExecuted("/* + INDEX (game idx_game_title)  */ select * from game g where title = ?");
        });
    }

    @Test
    public void testUpdateNamedQueryWithSqlComment() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            sqlStatementInterceptor.clear();
            Query query = entityManager.createNamedQuery("UpdateNamedNativeQuery");
            query.setParameter("title", GAME_TITLES[0]);
            query.setParameter("id", 1L);
            int updateCount = query.executeUpdate();
            assertEquals(1, updateCount);
            sqlStatementInterceptor.assertExecutedCount(1);
            sqlStatementInterceptor.assertExecuted("/* COMMENT_INDEX_game_title */ update game set title = ? where id = ?");
        });
    }

    @Test
    public void testUpdateNamedNativeQueryWithSqlComment() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            sqlStatementInterceptor.clear();
            Query query = entityManager.createNamedQuery("UpdateNamedNativeQuery");
            query.setParameter("title", GAME_TITLES[0]);
            query.setParameter("id", 1L);
            int updateCount = query.executeUpdate();
            assertEquals(1, updateCount);
            sqlStatementInterceptor.assertExecutedCount(1);
            sqlStatementInterceptor.assertExecuted("/* COMMENT_INDEX_game_title */ update game set title = ? where id = ?");
        });
    }

    @Test
    @RequiresDialect(Oracle8iDialect.class)
    public void testUpdateNamedNativeQueryWithQueryHintUsingOracle() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            sqlStatementInterceptor.clear();
            Query query = entityManager.createNamedQuery("UpdateNamedNativeQuery");
            query.setParameter("title", GAME_TITLES[0]);
            query.setParameter("id", 1L);
            query.unwrap(.class).addQueryHint("INDEX (game idx_game_id)");
            int updateCount = query.executeUpdate();
            assertEquals(1, updateCount);
            sqlStatementInterceptor.assertExecutedCount(1);
            sqlStatementInterceptor.assertExecuted("/* COMMENT_INDEX_game_title */ update /*+ INDEX (game idx_game_id) */ game set title = ? where id = ?");
        });
    }

    @Test
    @RequiresDialect(H2Dialect.class)
    public void testUpdateNamedNativeQueryWithQueryHintUsingIndex() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            sqlStatementInterceptor.clear();
            Query query = entityManager.createNamedQuery("UpdateNamedNativeQuery");
            query.setParameter("title", GAME_TITLES[0]);
            query.setParameter("id", 1L);
            query.unwrap(.class).addQueryHint("INDEX (game idx_game_id)");
            int updateCount = query.executeUpdate();
            assertEquals(1, updateCount);
            sqlStatementInterceptor.assertExecutedCount(1);
            sqlStatementInterceptor.assertExecuted("/* COMMENT_INDEX_game_title */ update game set title = ? where id = ?");
        });
    }

    @Test
    @RequiresDialect(MySQLDialect.class)
    @RequiresDialect(H2Dialect.class)
    public void testSelectNamedNativeQueryWithQueryHintUsingIndex() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            sqlStatementInterceptor.clear();
            Query query = entityManager.createNamedQuery("SelectNamedQuery");
            query.setParameter("title", GAME_TITLES[0]);
            query.unwrap(.class).addQueryHint("idx_game_id");
            List<org.hibernate.jpa.test.query.Game> list = query.getResultList();
            assertEquals(1, list.size());
            sqlStatementInterceptor.assertExecutedCount(1);
            sqlStatementInterceptor.assertExecuted("/* COMMENT_SELECT_INDEX_game_title */ select namedquery0_.id as id1_0_, namedquery0_.title as title2_0_ from game namedquery0_  USE INDEX (idx_game_id) where namedquery0_.title=?");
        });
    }

    @Entity(name = "Game")
    @Table(name = "game", indexes = { @Index(name = "idx_game_title", columnList = "title"), @Index(name = "idx_game_id", columnList = "id") })
    @NamedQuery(name = "SelectNamedQuery", query = "select g from Game g where title = :title", comment = "COMMENT_SELECT_INDEX_game_title")
    @NamedQuery(name = "UpdateNamedQuery", query = "update Game set title = :title where id = :id", comment = "INDEX (game idx_game_title) ")
    @NamedNativeQuery(name = "SelectNamedNativeQuery", query = "select * from game g where title = :title", comment = "+ INDEX (game idx_game_title) ", resultClass = NamedQueryCommentTest.Game.class)
    @NamedNativeQuery(name = "UpdateNamedNativeQuery", query = "update game set title = :title where id = :id", comment = "COMMENT_INDEX_game_title", resultClass = NamedQueryCommentTest.Game.class)
    public static class Game {
        @Id
        @GeneratedValue
        private Long id;

        private String title;

        public Game() {
        }

        public Game(String title) {
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
    }
}

