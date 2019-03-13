/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.legacy;


import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.SybaseASE15Dialect;
import org.hibernate.testing.SkipForDialect;
import org.junit.Assert;
import org.junit.Test;


/**
 * Query by example test to allow nested components
 *
 * @author Emmanuel Bernard
 */
public class QueryByExampleTest extends LegacyTestCase {
    @Test
    public void testSimpleQBE() throws Exception {
        deleteData();
        initData();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Componentizable master = getMaster("hibernate", "open sourc%", "open source1");
        Criteria crit = s.createCriteria(Componentizable.class);
        Example ex = Example.create(master).enableLike();
        crit.add(ex);
        List result = crit.list();
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        t.commit();
        s.close();
    }

    @Test
    @SkipForDialect(value = SybaseASE15Dialect.class, jiraKey = "HHH-4580")
    public void testJunctionNotExpressionQBE() throws Exception {
        deleteData();
        initData();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Componentizable master = getMaster("hibernate", null, "ope%");
        Criteria crit = s.createCriteria(Componentizable.class);
        Example ex = Example.create(master).enableLike();
        crit.add(Restrictions.or(Restrictions.not(ex), ex));
        List result = crit.list();
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        t.commit();
        s.close();
    }

    @Test
    public void testExcludingQBE() throws Exception {
        deleteData();
        initData();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Componentizable master = getMaster("hibernate", null, "ope%");
        Criteria crit = s.createCriteria(Componentizable.class);
        Example ex = Example.create(master).enableLike().excludeProperty("component.subComponent");
        crit.add(ex);
        List result = crit.list();
        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.size());
        master = getMaster("hibernate", "ORM tool", "fake stuff");
        crit = s.createCriteria(Componentizable.class);
        ex = Example.create(master).enableLike().excludeProperty("component.subComponent.subName1");
        crit.add(ex);
        result = crit.list();
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        t.commit();
        s.close();
    }
}

