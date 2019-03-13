/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.query;


import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import org.hibernate.Session;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-11092")
public class NamedQueryTest extends BaseEntityManagerFunctionalTestCase {
    private static final String[] GAME_TITLES = new String[]{ "Halo", "Grand Theft Auto", "NetHack" };

    @Test
    public void testNamedQueriesOrdinalParametersAreOneBased() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Query query = entityManager.createNamedQuery("NamedQuery");
            query.setParameter(1, GAME_TITLES[0]);
            List list = query.getResultList();
            assertEquals(1, list.size());
        });
    }

    @Test
    public void testNamedQueryOrdinalParametersConflict() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Query query = entityManager.createNamedQuery("NamedQuery");
            query.setParameter(1, GAME_TITLES[0]);
            List list = query.getResultList();
            assertEquals(1, list.size());
            final Session session = entityManager.unwrap(.class);
            final org.hibernate.query.Query sessionQuery = session.createQuery("select g from Game g where title = ?1");
            sessionQuery.setParameter(1, GAME_TITLES[0]);
            list = sessionQuery.getResultList();
            query.setParameter(1, GAME_TITLES[0]);
            assertEquals(1, list.size());
        });
    }

    @Test
    public void testNamedQueryOrdinalParametersConflict2() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Query query = entityManager.createNamedQuery("NamedQuery");
            query.setParameter(1, GAME_TITLES[0]);
            List list = query.getResultList();
            assertEquals(1, list.size());
            final Session session = entityManager.unwrap(.class);
            final org.hibernate.query.Query sessionQuery = session.getNamedQuery("NamedQuery");
            sessionQuery.setParameter(1, GAME_TITLES[0]);
            list = sessionQuery.getResultList();
            query.setParameter(1, GAME_TITLES[0]);
            assertEquals(1, list.size());
        });
    }

    @Test
    public void testNativeNamedQueriesOrdinalParametersAreOneBased() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Query query = entityManager.createNamedQuery("NamedNativeQuery");
            query.setParameter(1, GAME_TITLES[0]);
            List list = query.getResultList();
            assertEquals(1, list.size());
        });
    }

    @Test
    public void testNativeNamedQueriesOrdinalParametersConflict() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Query query = entityManager.createNamedQuery("NamedNativeQuery");
            query.setParameter(1, GAME_TITLES[0]);
            List list = query.getResultList();
            assertEquals(1, list.size());
            final Session session = entityManager.unwrap(.class);
            final org.hibernate.query.Query sessionQuery = session.createSQLQuery("select * from Game g where title = ?");
            sessionQuery.setParameter(1, GAME_TITLES[0]);
            list = sessionQuery.getResultList();
            query.setParameter(1, GAME_TITLES[0]);
            assertEquals(1, list.size());
        });
    }

    @Test
    public void testNativeNamedQueriesOrdinalParametersConflict2() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Query query = entityManager.createNamedQuery("NamedNativeQuery");
            query.setParameter(1, GAME_TITLES[0]);
            List list = query.getResultList();
            assertEquals(1, list.size());
            final Session session = entityManager.unwrap(.class);
            final org.hibernate.query.Query sessionQuery = session.getNamedNativeQuery("NamedNativeQuery");
            sessionQuery.setParameter(1, GAME_TITLES[0]);
            list = sessionQuery.getResultList();
            query.setParameter(1, GAME_TITLES[0]);
            assertEquals(1, list.size());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12621")
    public void testNativeQueriesFromNamedQueriesDoNotShareQuerySpaces() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Query originalQuery = entityManager.createNativeQuery("select g from Game g where title = ?1");
            entityManager.getEntityManagerFactory().addNamedQuery("myQuery", originalQuery);
            NativeQuery<?> query1 = entityManager.createNamedQuery("myQuery").unwrap(.class);
            query1.addSynchronizedQuerySpace("newQuerySpace");
            assertEquals(1, query1.getSynchronizedQuerySpaces().size());
            assertEquals("newQuerySpace", query1.getSynchronizedQuerySpaces().iterator().next());
            NativeQuery<?> query2 = entityManager.createNamedQuery("myQuery").unwrap(.class);
            assertEquals(0, query2.getSynchronizedQuerySpaces().size());
        });
    }

    @Entity(name = "Game")
    @NamedQueries(@NamedQuery(name = "NamedQuery", query = "select g from Game g where title = ?1"))
    @NamedNativeQueries(@NamedNativeQuery(name = "NamedNativeQuery", query = "select * from Game g where title = ?"))
    public static class Game {
        private Long id;

        private String title;

        public Game() {
        }

        public Game(String title) {
            this.title = title;
        }

        @Id
        @GeneratedValue
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

