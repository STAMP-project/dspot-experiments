/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.query;


import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class NamedQueryTest extends BaseCoreFunctionalTestCase {
    private static final String[] GAME_TITLES = new String[]{ "Halo", "Grand Theft Auto", "NetHack" };

    @Test
    public void testNamedQueriesOrdinalParametersAreOneBased() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Query query = session.getNamedQuery("NamedQuery");
            query.setParameter(1, GAME_TITLES[0]);
            List list = query.getResultList();
            assertEquals(1, list.size());
        });
    }

    @Test
    public void testNativeNamedQueriesOrdinalParametersAreOneBased() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Query query = session.getNamedNativeQuery("NamedNativeQuery");
            query.setParameter(1, GAME_TITLES[0]);
            List list = query.getResultList();
            assertEquals(1, list.size());
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

