/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.propertyref.basic;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Brett Meyer
 */
public class BasicPropertiesTest extends BaseCoreFunctionalTestCase {
    /**
     * Really simple regression test for HHH-8689.
     */
    @Test
    @TestForIssue(jiraKey = "HHH-8689")
    public void testProperties() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        EntityClass ec = new EntityClass();
        ec.setKey(1L);
        ec.setField1("foo1");
        ec.setField2("foo2");
        s.persist(ec);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        ec = ((EntityClass) (s.get(EntityClass.class, 1L)));
        t.commit();
        s.close();
        Assert.assertNotNull(ec);
        Assert.assertEquals(ec.getField1(), "foo1");
        Assert.assertEquals(ec.getField2(), "foo2");
    }
}

