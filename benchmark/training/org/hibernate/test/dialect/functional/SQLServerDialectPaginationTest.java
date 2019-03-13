/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.dialect.functional;


import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 * Test pagination on newer SQL Server Dialects where the application explicitly specifies
 * the legacy {@code SQLServerDialect} instead and will fail on pagination queries.
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-11642")
@RequiresDialect(SQLServerDialect.class)
public class SQLServerDialectPaginationTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testPaginationQuery() {
        // prepare some test data
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            for (int i = 1; i <= 20; ++i) {
                final org.hibernate.test.dialect.functional.SimpleEntity entity = new org.hibernate.test.dialect.functional.SimpleEntity(i, ("Entity" + i));
                entityManager.persist(entity);
            }
        });
        // This would fail with "index 2 out of range" within TopLimitHandler
        // The fix addresses this problem which only occurs when using SQLServerDialect explicitly.
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<org.hibernate.test.dialect.functional.SimpleEntity> results = entityManager.createQuery("SELECT o FROM SimpleEntity o WHERE o.id >= :firstId ORDER BY o.id", .class).setParameter("firstId", 10).setMaxResults(5).getResultList();
            // verify that the paginated query returned the right ids.
            final List<Integer> ids = results.stream().map(org.hibernate.test.dialect.functional.SimpleEntity::getId).collect(Collectors.toList());
            assertEquals(Arrays.asList(10, 11, 12, 13, 14), ids);
        });
    }

    @Entity(name = "SimpleEntity")
    public static class SimpleEntity implements Serializable {
        @Id
        private Integer id;

        private String name;

        SimpleEntity() {
        }

        SimpleEntity(Integer id, String name) {
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

