/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.derivedidentities.e1.a;


import java.util.List;
import org.hibernate.Query;
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
public class DerivedIdentitySimpleParentIdClassDepTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testManyToOne() throws Exception {
        Assert.assertTrue(SchemaUtil.isColumnPresent("Dependent", "emp_empId", metadata()));
        Assert.assertTrue((!(SchemaUtil.isColumnPresent("Dependent", "emp", metadata()))));
        Session s = openSession();
        s.getTransaction().begin();
        Employee e = new Employee(1L, "Emmanuel", "Manu");
        Dependent d = new Dependent("Doggy", e);
        s.persist(d);
        s.persist(e);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.getTransaction().begin();
        DependentId dId = new DependentId(d.getName(), d.getEmp().empId);
        d = ((Dependent) (s.get(Dependent.class, dId)));
        Assert.assertEquals(e.empId, d.getEmp().empId);
        Assert.assertEquals(e.empName, d.getEmp().empName);
        Assert.assertEquals(e.nickname, d.getEmp().nickname);
        s.delete(d);
        s.delete(d.getEmp());
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testQueryNewEntityInPC() throws Exception {
        Session s = openSession();
        s.getTransaction().begin();
        Employee e = new Employee(1L, "Paula", "P");
        Dependent d = new Dependent("LittleP", e);
        d.setEmp(e);
        s.persist(d);
        s.persist(e);
        // find the entity added above
        Query query = s.createQuery("Select d from Dependent d where d.name='LittleP' and d.emp.empName='Paula'");
        List depList = query.list();
        Assert.assertEquals(1, depList.size());
        Object newDependent = depList.get(0);
        Assert.assertSame(d, newDependent);
        s.getTransaction().rollback();
        s.close();
    }
}

