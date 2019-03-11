/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.derivedidentities.e1.c;


import org.hibernate.Session;
import org.hibernate.test.util.SchemaUtil;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class DerivedIdentitySimpleParentEmbeddedDepTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testManyToOne() throws Exception {
        Assert.assertTrue(SchemaUtil.isColumnPresent("Dependent", "emp_empId", metadata()));
        Assert.assertTrue((!(SchemaUtil.isColumnPresent("Dependent", "empPK", metadata()))));
        Employee e = new Employee();
        e.empId = 1;
        e.empName = "Emmanuel";
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(e);
        Dependent d = new Dependent();
        d.emp = e;
        d.name = "Doggy";
        s.persist(d);
        s.flush();
        s.clear();
        d = getDerivedClassById(e, s, Dependent.class, d.name);
        Assert.assertEquals(e.empId, d.emp.empId);
        s.getTransaction().rollback();
        s.close();
    }
}

