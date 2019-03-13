/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.sql.hand.custom;


import LockMode.UPGRADE;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.test.sql.hand.Employment;
import org.hibernate.test.sql.hand.ImageHolder;
import org.hibernate.test.sql.hand.Organization;
import org.hibernate.test.sql.hand.Person;
import org.hibernate.test.sql.hand.TextHolder;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Abstract test case defining tests for the support for user-supplied (aka
 * custom) insert, update, delete SQL.
 *
 * @author Steve Ebersole
 */
@SuppressWarnings({ "UnusedDeclaration" })
public abstract class CustomSQLTestSupport extends BaseCoreFunctionalTestCase {
    @Test
    public void testHandSQL() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Organization ifa = new Organization("IFA");
        Organization jboss = new Organization("JBoss");
        Person gavin = new Person("Gavin");
        Employment emp = new Employment(gavin, jboss, "AU");
        Serializable orgId = s.save(jboss);
        s.save(ifa);
        s.save(gavin);
        s.save(emp);
        t.commit();
        t = s.beginTransaction();
        Person christian = new Person("Christian");
        s.save(christian);
        Employment emp2 = new Employment(christian, jboss, "EU");
        s.save(emp2);
        t.commit();
        s.close();
        sessionFactory().getCache().evictEntityRegion(Organization.class);
        sessionFactory().getCache().evictEntityRegion(Person.class);
        sessionFactory().getCache().evictEntityRegion(Employment.class);
        s = openSession();
        t = s.beginTransaction();
        jboss = ((Organization) (s.get(Organization.class, orgId)));
        Assert.assertEquals(jboss.getEmployments().size(), 2);
        Assert.assertEquals(jboss.getName(), "JBOSS");
        emp = ((Employment) (jboss.getEmployments().iterator().next()));
        gavin = emp.getEmployee();
        Assert.assertEquals("GAVIN", gavin.getName());
        Assert.assertEquals(UPGRADE, s.getCurrentLockMode(gavin));
        emp.setEndDate(new Date());
        Employment emp3 = new Employment(gavin, jboss, "US");
        s.save(emp3);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Iterator itr = s.getNamedQuery("allOrganizationsWithEmployees").list().iterator();
        Assert.assertTrue(itr.hasNext());
        Organization o = ((Organization) (itr.next()));
        Assert.assertEquals(o.getEmployments().size(), 3);
        Iterator itr2 = o.getEmployments().iterator();
        while (itr2.hasNext()) {
            Employment e = ((Employment) (itr2.next()));
            s.delete(e);
        } 
        itr2 = o.getEmployments().iterator();
        while (itr2.hasNext()) {
            Employment e = ((Employment) (itr2.next()));
            s.delete(e.getEmployee());
        } 
        s.delete(o);
        Assert.assertFalse(itr.hasNext());
        s.delete(ifa);
        t.commit();
        s.close();
    }

    @Test
    public void testTextProperty() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        String description = buildLongString(15000, 'a');
        TextHolder holder = new TextHolder(description);
        s.save(holder);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        holder = ((TextHolder) (s.get(TextHolder.class, holder.getId())));
        Assert.assertEquals(description, holder.getDescription());
        description = buildLongString(15000, 'b');
        holder.setDescription(description);
        s.save(holder);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        holder = ((TextHolder) (s.get(TextHolder.class, holder.getId())));
        Assert.assertEquals(description, holder.getDescription());
        s.delete(holder);
        t.commit();
        s.close();
    }

    @Test
    public void testImageProperty() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        byte[] photo = buildLongByteArray(15000, true);
        ImageHolder holder = new ImageHolder(photo);
        s.save(holder);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        holder = ((ImageHolder) (s.get(ImageHolder.class, holder.getId())));
        Assert.assertTrue(Arrays.equals(photo, holder.getPhoto()));
        photo = buildLongByteArray(15000, false);
        holder.setPhoto(photo);
        s.save(holder);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        holder = ((ImageHolder) (s.get(ImageHolder.class, holder.getId())));
        Assert.assertTrue(Arrays.equals(photo, holder.getPhoto()));
        s.delete(holder);
        t.commit();
        s.close();
    }
}

