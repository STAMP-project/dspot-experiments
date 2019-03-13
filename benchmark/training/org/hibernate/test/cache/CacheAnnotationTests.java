/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.cache;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
public class CacheAnnotationTests extends BaseCoreFunctionalTestCase {
    private Integer entityId;

    @Test
    @TestForIssue(jiraKey = "HHH-12587")
    public void testCacheWriteConcurrencyStrategyNone() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.cache.NoCacheConcurrencyStrategyEntity entity = new org.hibernate.test.cache.NoCacheConcurrencyStrategyEntity();
            session.save(entity);
            session.flush();
            session.clear();
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12868")
    public void testCacheReadConcurrencyStrategyNone() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.cache.NoCacheConcurrencyStrategyEntity entity = new org.hibernate.test.cache.NoCacheConcurrencyStrategyEntity();
            entity.setName("name");
            session.save(entity);
            session.flush();
            this.entityId = entity.getId();
            session.clear();
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.cache.NoCacheConcurrencyStrategyEntity entity = session.load(.class, this.entityId);
            assertEquals("name", entity.getName());
        });
    }

    @Entity(name = "NoCacheConcurrencyStrategy")
    @Cache(usage = CacheConcurrencyStrategy.NONE)
    public static class NoCacheConcurrencyStrategyEntity {
        @Id
        @GeneratedValue
        private Integer id;

        private String name;

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

