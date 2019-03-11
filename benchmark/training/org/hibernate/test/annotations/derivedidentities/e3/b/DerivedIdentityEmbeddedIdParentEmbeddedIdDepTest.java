/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.derivedidentities.e3.b;


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
public class DerivedIdentityEmbeddedIdParentEmbeddedIdDepTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testManyToOne() throws Exception {
        Assert.assertTrue(SchemaUtil.isColumnPresent("Dependent", "FK1", metadata()));
        Assert.assertTrue(SchemaUtil.isColumnPresent("Dependent", "FK2", metadata()));
        Assert.assertTrue(SchemaUtil.isColumnPresent("Dependent", "dep_name", metadata()));
        Assert.assertTrue((!(SchemaUtil.isColumnPresent("Dependent", "firstName", metadata()))));
        Assert.assertTrue((!(SchemaUtil.isColumnPresent("Dependent", "lastName", metadata()))));
        Employee e = new Employee();
        e.empId = new EmployeeId();
        e.empId.firstName = "Emmanuel";
        e.empId.lastName = "Bernard";
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(e);
        Dependent d = new Dependent();
        d.emp = e;
        d.id = new DependentId();
        d.id.name = "Doggy";
        s.persist(d);
        s.flush();
        s.clear();
        d = ((Dependent) (s.get(Dependent.class, d.id)));
        Assert.assertNotNull(d.emp);
        Assert.assertEquals(e.empId.firstName, d.emp.empId.firstName);
        s.getTransaction().rollback();
        s.close();
    }
}

