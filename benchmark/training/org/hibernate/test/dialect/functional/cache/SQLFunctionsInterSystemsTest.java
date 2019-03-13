/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.dialect.functional.cache;


import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.dialect.Cache71Dialect;
import org.hibernate.test.legacy.Simple;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for function support on CacheSQL...
 *
 * @author Jonathan Levinson
 */
@RequiresDialect(Cache71Dialect.class)
public class SQLFunctionsInterSystemsTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testDialectSQLFunctions() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Simple simple = new Simple(Long.valueOf(10));
        simple.setName("Simple Dialect Function Test");
        simple.setAddress("Simple Address");
        simple.setPay(new Float(45.8));
        simple.setCount(2);
        s.save(simple);
        // Test to make sure allocating a specified object operates correctly.
        Assert.assertTrue(((s.createQuery("select new org.hibernate.test.legacy.S(s.count, s.address) from Simple s").list().size()) == 1));
        // Quick check the base dialect functions operate correctly
        Assert.assertTrue(((s.createQuery("select max(s.count) from Simple s").list().size()) == 1));
        Assert.assertTrue(((s.createQuery("select count(*) from Simple s").list().size()) == 1));
        List rset = s.createQuery("select s.name, sysdate, floor(s.pay), round(s.pay,0) from Simple s").list();
        Assert.assertNotNull("Name string should have been returned", ((Object[]) (rset.get(0)))[0]);
        Assert.assertNotNull("Todays Date should have been returned", ((Object[]) (rset.get(0)))[1]);
        Assert.assertEquals("floor(45.8) result was incorrect ", new Integer(45), ((Object[]) (rset.get(0)))[2]);
        Assert.assertEquals("round(45.8) result was incorrect ", new Float(46), ((Object[]) (rset.get(0)))[3]);
        simple.setPay(new Float((-45.8)));
        s.update(simple);
        // Test type conversions while using nested functions (Float to Int).
        rset = s.createQuery("select abs(round(s.pay,0)) from Simple s").list();
        Assert.assertEquals("abs(round(-45.8)) result was incorrect ", new Float(46), rset.get(0));
        // Test a larger depth 3 function example - Not a useful combo other than for testing
        Assert.assertTrue(((s.createQuery("select floor(round(sysdate,1)) from Simple s").list().size()) == 1));
        // Test the oracle standard NVL funtion as a test of multi-param functions...
        simple.setPay(null);
        s.update(simple);
        Double value = ((Double) (s.createQuery("select mod( nvl(s.pay, 5000), 2 ) from Simple as s where s.id = 10").list().get(0)));
        Assert.assertTrue((0 == (value.intValue())));
        // Test the hsql standard MOD funtion as a test of multi-param functions...
        value = ((Double) (s.createQuery("select MOD(s.count, 2) from Simple as s where s.id = 10").list().get(0)));
        Assert.assertTrue((0 == (value.intValue())));
        s.delete(simple);
        t.commit();
        s.close();
    }
}

