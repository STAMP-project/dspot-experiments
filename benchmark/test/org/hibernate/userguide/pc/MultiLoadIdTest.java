/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.pc;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.stat.Statistics;
import org.hibernate.testing.jdbc.SQLStatementInterceptor;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class MultiLoadIdTest extends BaseEntityManagerFunctionalTestCase {
    private SQLStatementInterceptor sqlStatementInterceptor;

    @Test
    public void testSessionCheck() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::pc-by-multiple-ids-example[]
            Session session = entityManager.unwrap(.class);
            List<org.hibernate.userguide.pc.Person> persons = session.byMultipleIds(.class).multiLoad(1L, 2L, 3L);
            assertEquals(3, persons.size());
            List<org.hibernate.userguide.pc.Person> samePersons = session.byMultipleIds(.class).enableSessionCheck(true).multiLoad(1L, 2L, 3L);
            assertEquals(persons, samePersons);
            // end::pc-by-multiple-ids-example[]
        });
    }

    @Test
    public void testSecondLevelCacheCheck() {
        // tag::pc-by-multiple-ids-second-level-cache-example[]
        SessionFactory sessionFactory = entityManagerFactory().unwrap(SessionFactory.class);
        Statistics statistics = sessionFactory.getStatistics();
        sessionFactory.getCache().evictAll();
        statistics.clear();
        sqlStatementInterceptor.clear();
        Assert.assertEquals(0, statistics.getQueryExecutionCount());
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            List<org.hibernate.userguide.pc.Person> persons = session.byMultipleIds(.class).multiLoad(1L, 2L, 3L);
            assertEquals(3, persons.size());
        });
        Assert.assertEquals(0, statistics.getSecondLevelCacheHitCount());
        Assert.assertEquals(3, statistics.getSecondLevelCachePutCount());
        Assert.assertEquals(1, sqlStatementInterceptor.getSqlQueries().size());
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            sqlStatementInterceptor.clear();
            List<org.hibernate.userguide.pc.Person> persons = session.byMultipleIds(.class).with(CacheMode.NORMAL).multiLoad(1L, 2L, 3L);
            assertEquals(3, persons.size());
        });
        Assert.assertEquals(3, statistics.getSecondLevelCacheHitCount());
        Assert.assertEquals(0, sqlStatementInterceptor.getSqlQueries().size());
        // end::pc-by-multiple-ids-second-level-cache-example[]
    }

    @Entity(name = "Person")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public static class Person {
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

