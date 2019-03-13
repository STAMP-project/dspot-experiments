/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.derivedidentities.e1.b;


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
public class DerivedIdentitySimpleParentEmbeddedIdDepTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testManyToOne() throws Exception {
        Assert.assertTrue(SchemaUtil.isColumnPresent("Dependent", "emp_empId", metadata()));
        Assert.assertTrue((!(SchemaUtil.isColumnPresent("Dependent", "empPK", metadata()))));
        Employee e = new Employee();
        e.empId = 1;
        e.empName = "Emmanuel";
        Session s = openSession();
        s.getTransaction().begin();
        Dependent d = new Dependent();
        d.emp = e;
        d.id = new DependentId();
        d.id.name = "Doggy";
        s.persist(d);
        s.persist(e);
        s.flush();
        s.clear();
        d = ((Dependent) (s.get(Dependent.class, d.id)));
        Assert.assertEquals(d.id.empPK, d.emp.empId);
        s.getTransaction().rollback();
        s.close();
    }

    @Test
    public void testOneToOne() throws Exception {
        Assert.assertTrue(SchemaUtil.isColumnPresent("ExclusiveDependent", "FK", metadata()));
        Assert.assertTrue((!(SchemaUtil.isColumnPresent("ExclusiveDependent", "empPK", metadata()))));
        Employee e = new Employee();
        e.empId = 1;
        e.empName = "Emmanuel";
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(e);
        ExclusiveDependent d = new ExclusiveDependent();
        d.emp = e;
        d.id = new DependentId();
        d.id.name = "Doggy";
        // d.id.empPK = e.empId; //FIXME not needed when foreign is enabled
        s.persist(d);
        s.flush();
        s.clear();
        d = ((ExclusiveDependent) (s.get(ExclusiveDependent.class, d.id)));
        Assert.assertEquals(d.id.empPK, d.emp.empId);
        s.getTransaction().rollback();
        s.close();
    }
}

