/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.lazy.cache;


import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.stat.CacheRegionStatistics;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Aaron Schmischke
 * @author Gail Badner
 */
@RunWith(BytecodeEnhancerRunner.class)
public class UninitializedLazyBasicCacheTest extends BaseCoreFunctionalTestCase {
    private Long personId;

    @Test
    @TestForIssue(jiraKey = "HHH-11766")
    public void test() {
        sessionFactory().getStatistics().clear();
        sessionFactory().getCache().evictAll();
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            final org.hibernate.test.bytecode.enhancement.lazy.cache.Person person = s.get(.class, personId);
            assertFalse(Hibernate.isPropertyInitialized(person, "lazyAttribute"));
        });
        CacheRegionStatistics regionStatistics = sessionFactory().getStatistics().getCacheRegionStatistics("Person");
        Assert.assertEquals(0, regionStatistics.getHitCount());
        Assert.assertEquals(1, regionStatistics.getMissCount());
        Assert.assertEquals(1, regionStatistics.getPutCount());
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            final org.hibernate.test.bytecode.enhancement.lazy.cache.Person person = s.get(.class, personId);
            assertFalse(Hibernate.isPropertyInitialized(person, "lazyAttribute"));
            person.getLazyAttribute();
            assertTrue(Hibernate.isPropertyInitialized(person, "lazyAttribute"));
        });
        Assert.assertEquals(1, regionStatistics.getHitCount());
        Assert.assertEquals(1, regionStatistics.getMissCount());
        Assert.assertEquals(1, regionStatistics.getPutCount());
    }

    @Cacheable
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, include = "all", region = "Person")
    @Entity(name = "Person")
    public static class Person {
        @Id
        @GeneratedValue
        @Column(name = "id")
        private Long id;

        @Column(name = "lazyAttribute")
        @Basic(fetch = FetchType.LAZY)
        private String lazyAttribute;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getLazyAttribute() {
            return lazyAttribute;
        }

        public void setLazyAttribute(String lazyAttribute) {
            this.lazyAttribute = lazyAttribute;
        }
    }
}

