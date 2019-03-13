/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.orphan.one2one.fk.composite;


import java.util.List;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class DeleteOneToOneOrphansTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testOrphanedWhileManaged() {
        createData();
        Session session = openSession();
        session.beginTransaction();
        List results = session.createQuery("from EmployeeInfo").list();
        Assert.assertEquals(1, results.size());
        results = session.createQuery("from Employee").list();
        Assert.assertEquals(1, results.size());
        Employee emp = ((Employee) (results.get(0)));
        Assert.assertNotNull(emp.getInfo());
        emp.setInfo(null);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        emp = ((Employee) (session.get(Employee.class, emp.getId())));
        Assert.assertNull(emp.getInfo());
        results = session.createQuery("from EmployeeInfo").list();
        Assert.assertEquals(0, results.size());
        results = session.createQuery("from Employee").list();
        Assert.assertEquals(1, results.size());
        session.getTransaction().commit();
        session.close();
        cleanupData();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-6484")
    public void testReplacedWhileManaged() {
        createData();
        Session session = openSession();
        session.beginTransaction();
        List results = session.createQuery("from EmployeeInfo").list();
        Assert.assertEquals(1, results.size());
        results = session.createQuery("from Employee").list();
        Assert.assertEquals(1, results.size());
        Employee emp = ((Employee) (results.get(0)));
        Assert.assertNotNull(emp.getInfo());
        // Replace with a new EmployeeInfo instance
        emp.setInfo(new EmployeeInfo(2L, 2L));
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        emp = ((Employee) (session.get(Employee.class, emp.getId())));
        Assert.assertNotNull(emp.getInfo());
        results = session.createQuery("from EmployeeInfo").list();
        Assert.assertEquals(1, results.size());
        results = session.createQuery("from Employee").list();
        Assert.assertEquals(1, results.size());
        session.getTransaction().commit();
        session.close();
        cleanupData();
    }
}

