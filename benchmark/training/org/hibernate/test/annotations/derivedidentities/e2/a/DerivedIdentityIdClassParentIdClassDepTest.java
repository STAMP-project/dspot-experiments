/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.derivedidentities.e2.a;


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
public class DerivedIdentityIdClassParentIdClassDepTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testManytoOne() {
        Assert.assertTrue(SchemaUtil.isColumnPresent("Dependent", "FK1", metadata()));
        Assert.assertTrue(SchemaUtil.isColumnPresent("Dependent", "FK2", metadata()));
        Assert.assertTrue(SchemaUtil.isColumnPresent("Dependent", "name", metadata()));
        Assert.assertTrue((!(SchemaUtil.isColumnPresent("Dependent", "firstName", metadata()))));
        Assert.assertTrue((!(SchemaUtil.isColumnPresent("Dependent", "lastName", metadata()))));
        Employee e = new Employee();
        e.firstName = "Emmanuel";
        e.lastName = "Bernard";
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(e);
        Dependent d = new Dependent();
        d.emp = e;
        d.name = "Doggy";
        s.persist(d);
        s.flush();
        s.clear();
        DependentId dId = new DependentId();
        EmployeeId eId = new EmployeeId();
        dId.name = d.name;
        dId.emp = eId;
        eId.firstName = e.firstName;
        eId.lastName = e.lastName;
        d = ((Dependent) (s.get(Dependent.class, dId)));
        Assert.assertNotNull(d.emp);
        Assert.assertEquals(e.firstName, d.emp.firstName);
        s.delete(d);
        s.delete(d.emp);
        s.getTransaction().commit();
        s.close();
    }
}

