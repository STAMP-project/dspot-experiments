/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.queryplan;


import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
import org.hibernate.engine.query.spi.QueryPlanCache;
import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests equals() for NativeSQLQueryReturn implementations.
 *
 * @author Michael Stevens
 */
public class NativeSQLQueryPlanEqualsTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testNativeSQLQuerySpecEquals() {
        QueryPlanCache cache = new QueryPlanCache(sessionFactory());
        NativeSQLQuerySpecification firstSpec = createSpec();
        NativeSQLQuerySpecification secondSpec = createSpec();
        NativeSQLQueryPlan firstPlan = cache.getNativeSQLQueryPlan(firstSpec);
        NativeSQLQueryPlan secondPlan = cache.getNativeSQLQueryPlan(secondSpec);
        Assert.assertEquals(firstPlan, secondPlan);
    }
}

