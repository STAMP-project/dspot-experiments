/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.stat.internal;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.stat.Statistics;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
@TestForIssue(jiraKey = "HHH-12855")
public class QueryPlanCacheStatisticsTest extends BaseEntityManagerFunctionalTestCase {
    private Statistics statistics;

    @Test
    public void test() {
        Assert.assertEquals(0, statistics.getQueryPlanCacheHitCount());
        Assert.assertEquals(0, statistics.getQueryPlanCacheMissCount());
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final String FIRST_QUERY = "select e from Employee e";
            entityManager.createQuery(FIRST_QUERY);
            assertEquals(0, statistics.getQueryPlanCacheHitCount());
            assertEquals(1, statistics.getQueryPlanCacheMissCount());
            assertQueryStatistics(FIRST_QUERY, 0);
            entityManager.createQuery(FIRST_QUERY);
            assertEquals(1, statistics.getQueryPlanCacheHitCount());
            assertEquals(1, statistics.getQueryPlanCacheMissCount());
            assertQueryStatistics(FIRST_QUERY, 1);
            entityManager.createQuery(FIRST_QUERY);
            assertEquals(2, statistics.getQueryPlanCacheHitCount());
            assertEquals(1, statistics.getQueryPlanCacheMissCount());
            assertQueryStatistics(FIRST_QUERY, 2);
            final String SECOND_QUERY = "select count(e) from Employee e";
            entityManager.createQuery(SECOND_QUERY);
            assertEquals(2, statistics.getQueryPlanCacheHitCount());
            assertEquals(2, statistics.getQueryPlanCacheMissCount());
            assertQueryStatistics(SECOND_QUERY, 0);
            entityManager.createQuery(SECOND_QUERY);
            assertEquals(3, statistics.getQueryPlanCacheHitCount());
            assertEquals(2, statistics.getQueryPlanCacheMissCount());
            assertQueryStatistics(SECOND_QUERY, 1);
            entityManager.createQuery(SECOND_QUERY);
            assertEquals(4, statistics.getQueryPlanCacheHitCount());
            assertEquals(2, statistics.getQueryPlanCacheMissCount());
            assertQueryStatistics(SECOND_QUERY, 2);
        });
    }

    @Entity(name = "Employee")
    public static class Employee {
        @Id
        @GeneratedValue
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

