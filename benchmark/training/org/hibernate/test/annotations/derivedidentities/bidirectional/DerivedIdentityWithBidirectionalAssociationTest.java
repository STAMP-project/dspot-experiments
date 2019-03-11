/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.derivedidentities.bidirectional;


import Skip.AlwaysSkip;
import org.hibernate.Session;
import org.hibernate.test.util.SchemaUtil;
import org.hibernate.testing.Skip;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Hardy Ferentschik
 */
@Skip(condition = AlwaysSkip.class, message = "sdf")
public class DerivedIdentityWithBidirectionalAssociationTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testBidirectionalAssociation() throws Exception {
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
        s.persist(d);
        s.flush();
        s.clear();
        d = getDerivedClassById(e, s, Dependent.class);
        Assert.assertEquals(e.empId, d.emp.empId);
        s.getTransaction().rollback();
        s.close();
    }
}

