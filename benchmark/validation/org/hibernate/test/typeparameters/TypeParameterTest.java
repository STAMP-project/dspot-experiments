/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.typeparameters;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for parameterizable types.
 *
 * @author Michael Gloegl
 */
public class TypeParameterTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testSave() throws Exception {
        deleteData();
        Session s = openSession();
        s.beginTransaction();
        Widget obj = new Widget();
        obj.setValueThree(5);
        final Integer id = ((Integer) (s.save(obj)));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        doWork(id, s);
        s.getTransaction().commit();
        s.close();
        deleteData();
    }

    @Test
    public void testLoading() throws Exception {
        initData();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Widget obj = ((Widget) (s.createQuery("from Widget o where o.string = :string").setString("string", "all-normal").uniqueResult()));
        Assert.assertEquals("Non-Default value incorrectly loaded", obj.getValueOne(), 7);
        Assert.assertEquals("Non-Default value incorrectly loaded", obj.getValueTwo(), 8);
        Assert.assertEquals("Non-Default value incorrectly loaded", obj.getValueThree(), 9);
        Assert.assertEquals("Non-Default value incorrectly loaded", obj.getValueFour(), 10);
        obj = ((Widget) (s.createQuery("from Widget o where o.string = :string").setString("string", "all-default").uniqueResult()));
        Assert.assertEquals("Default value incorrectly loaded", obj.getValueOne(), 1);
        Assert.assertEquals("Default value incorrectly loaded", obj.getValueTwo(), 2);
        Assert.assertEquals("Default value incorrectly loaded", obj.getValueThree(), (-1));
        Assert.assertEquals("Default value incorrectly loaded", obj.getValueFour(), (-5));
        t.commit();
        s.close();
        deleteData();
    }
}

