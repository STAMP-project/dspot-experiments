/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.abstractembeddedcomponents.propertyref;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class AbstractComponentPropertyRefTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testPropertiesRefCascades() {
        Session session = openSession();
        Transaction trans = session.beginTransaction();
        ServerImpl server = new ServerImpl();
        session.save(server);
        AddressImpl address = new AddressImpl();
        server.setAddress(address);
        address.setServer(server);
        session.flush();
        session.createQuery("from Server s join fetch s.address").list();
        trans.commit();
        session.close();
        Assert.assertNotNull(server.getId());
        Assert.assertNotNull(address.getId());
        session = openSession();
        trans = session.beginTransaction();
        session.delete(address);
        session.delete(server);
        trans.commit();
        session.close();
    }
}

