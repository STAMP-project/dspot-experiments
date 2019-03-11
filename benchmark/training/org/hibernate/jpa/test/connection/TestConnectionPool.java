/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.connection;


import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-11257")
public class TestConnectionPool extends BaseEntityManagerFunctionalTestCase {
    private static final int CONNECTION_POOL_SIZE = 2;

    @Test
    public void testConnectionPoolDoesNotConsumeAllConnections() {
        for (int i = 0; i < ((TestConnectionPool.CONNECTION_POOL_SIZE) + 1); ++i) {
            EntityManager entityManager = getOrCreateEntityManager();
            try {
                for (int j = 0; j < 2; j++) {
                    try {
                        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
                        final CriteriaQuery<TestConnectionPool.TestEntity> criteriaQuery = builder.createQuery(TestConnectionPool.TestEntity.class);
                        criteriaQuery.select(criteriaQuery.from(TestConnectionPool.TestEntity.class));
                        entityManager.createQuery(criteriaQuery).getResultList();
                    } catch (PersistenceException e) {
                        if ((e.getCause()) instanceof SQLGrammarException) {
                            // expected, the schema was not created
                        } else {
                            throw e;
                        }
                    }
                }
            } finally {
                entityManager.close();
            }
        }
    }

    @Entity(name = "Test_Entity")
    public static class TestEntity {
        @Id
        public long id;
    }
}

