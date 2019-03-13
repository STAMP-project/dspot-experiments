/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.entity;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class PropertyDefaultMappingsTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testSerializableObject() throws Exception {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        Country c = new Country();
        c.setName("France");
        Address a = new Address();
        a.setCity("Paris");
        a.setCountry(c);
        s.persist(a);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        Address reloadedAddress = ((Address) (s.get(Address.class, a.getId())));
        Assert.assertNotNull(reloadedAddress);
        Assert.assertNotNull(reloadedAddress.getCountry());
        Assert.assertEquals(a.getCountry().getName(), reloadedAddress.getCountry().getName());
        tx.rollback();
        s.close();
    }

    @Test
    public void testTransientField() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        WashingMachine wm = new WashingMachine();
        wm.setActive(true);
        s.persist(wm);
        tx.commit();
        s.clear();
        tx = s.beginTransaction();
        wm = ((WashingMachine) (s.get(WashingMachine.class, wm.getId())));
        Assert.assertFalse("transient should not be persistent", wm.isActive());
        s.delete(wm);
        tx.commit();
        s.close();
    }
}

