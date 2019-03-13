/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.query;


import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.spi.NativeQueryImplementor;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class NativeQueryOrdinalParametersTest extends BaseEntityManagerFunctionalTestCase {
    private static final String[] GAME_TITLES = new String[]{ "Super Mario Brothers", "Mario Kart", "F-Zero" };

    @Test
    @TestForIssue(jiraKey = "HHH-10885")
    public void testNativeQueryIndexedOrdinalParameter() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Query query = entityManager.createNativeQuery("SELECT * FROM GAME g WHERE title = ?1");
            query.setParameter(1, "Super Mario Brothers");
            List list = query.getResultList();
            assertEquals(1, list.size());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10885")
    public void testNativeQueryOrdinalParameter() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Query query = entityManager.createNativeQuery("SELECT * FROM GAME g WHERE title = ?");
            query.setParameter(1, "Super Mario Brothers");
            List list = query.getResultList();
            assertEquals(1, list.size());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11121")
    public void testConflictWithSessionNativeQuery() {
        final String sqlString = "SELECT * FROM GAME g WHERE title = ?";
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            NativeQuery sqlQuery = entityManager.unwrap(.class).createSQLQuery(sqlString);
            sqlQuery.setString(1, "Super Mario Brothers").setCacheable(true);
            List results = sqlQuery.list();
            assertEquals(1, results.size());
            NativeQueryImplementor query = ((NativeQueryImplementor) (entityManager.createNativeQuery(sqlString)));
            query.setString(1, "Super Mario Brothers");
            List list = query.list();
            assertEquals(1, list.size());
            sqlQuery = entityManager.unwrap(.class).createSQLQuery(sqlString);
            sqlQuery.setString(1, "Super Mario Brothers").setCacheable(true);
            results = sqlQuery.list();
            assertEquals(1, results.size());
            query.setString(1, "Super Mario Brothers");
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12532")
    @RequiresDialect(PostgreSQL82Dialect.class)
    public void testCteNativeQueryOrdinalParameter() {
        NativeQueryOrdinalParametersTest.Node root1 = new NativeQueryOrdinalParametersTest.Node();
        root1.setCode("ABC");
        NativeQueryOrdinalParametersTest.Node root2 = new NativeQueryOrdinalParametersTest.Node();
        root2.setCode("DEF");
        NativeQueryOrdinalParametersTest.Node node11 = new NativeQueryOrdinalParametersTest.Node();
        node11.setParent(root1);
        NativeQueryOrdinalParametersTest.Node node21 = new NativeQueryOrdinalParametersTest.Node();
        node21.setParent(root2);
        NativeQueryOrdinalParametersTest.Node node211 = new NativeQueryOrdinalParametersTest.Node();
        node211.setParent(node21);
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.persist(root1);
            entityManager.persist(root2);
            entityManager.persist(node11);
            entityManager.persist(node21);
            entityManager.persist(node211);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Query cte = entityManager.createNativeQuery(("WITH RECURSIVE CTE(id, parent_id) AS ( " + ((((((((("  SELECT id, parent_id " + "  FROM Node ") + "  WHERE code like ?1 and parent_id is null") + "  UNION ALL ") + "  SELECT child.id, child.parent_id ") + "  FROM Node child  ") + "  JOIN CTE cte ") + "  ON cte.id = child.parent_id ") + ") SELECT DISTINCT id as integer ") + "  FROM CTE cte")));
            List<Long> root1Ids = cte.setParameter(1, "AB%").getResultList();
            assertEquals(2, root1Ids.size());
            assertTrue(root1Ids.contains(root1.getId()));
            assertTrue(root1Ids.contains(node11.getId()));
            List<Long> root2Ids = cte.setParameter(1, "DE%").getResultList();
            assertEquals(3, root2Ids.size());
            assertTrue(root2Ids.contains(root2.getId()));
            assertTrue(root2Ids.contains(node21.getId()));
            assertTrue(root2Ids.contains(node211.getId()));
        });
    }

    @Entity(name = "Game")
    @Table(name = "GAME")
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

        @NotNull
        @Size(min = 3, max = 50)
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    @Entity(name = "Node")
    @Table(name = "Node")
    public static class Node {
        @Id
        @GeneratedValue
        private Integer id;

        private String code;

        @ManyToOne(fetch = FetchType.LAZY)
        private NativeQueryOrdinalParametersTest.Node parent;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public NativeQueryOrdinalParametersTest.Node getParent() {
            return parent;
        }

        public void setParent(NativeQueryOrdinalParametersTest.Node parent) {
            this.parent = parent;
        }
    }
}

