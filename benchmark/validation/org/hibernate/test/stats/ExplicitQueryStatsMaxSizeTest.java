/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.stats;


import javax.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class ExplicitQueryStatsMaxSizeTest extends QueryStatsMaxSizeTest {
    public static final int QUERY_STATISTICS_MAX_SIZE = 100;

    @Test
    public void testMaxSize() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            EntityManagerFactory entityManagerFactory = entityManager.getEntityManagerFactory();
            SessionFactory sessionFactory = entityManagerFactory.unwrap(.class);
            assertEquals(expectedQueryStatisticsMaxSize(), sessionFactory.getSessionFactoryOptions().getQueryStatisticsMaxSize());
            StatisticsImplementor statistics = ((StatisticsImplementor) (sessionFactory.getStatistics()));
            for (int i = 0; i < 10; i++) {
                statistics.queryExecuted(String.valueOf(i), 100, (i * 1000));
            }
            assertEquals(1000, statistics.getQueryStatistics("1").getExecutionTotalTime());
            for (int i = 100; i < 300; i++) {
                statistics.queryExecuted(String.valueOf(i), 100, (i * 1000));
            }
            assertEquals(0, statistics.getQueryStatistics("1").getExecutionTotalTime());
        });
    }
}

