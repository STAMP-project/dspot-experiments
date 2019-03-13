/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
/**
 * $Id: DynamicMapOneToOneTest.java 10977 2006-12-12 23:28:04Z steve.ebersole@jboss.com $
 */
package org.hibernate.test.onetoone.nopojo;


import java.util.HashMap;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class DynamicMapOneToOneTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testOneToOneOnSubclass() {
        Map person = new HashMap();
        person.put("name", "Steve");
        Map address = new HashMap();
        address.put("zip", "12345");
        address.put("state", "TX");
        address.put("street", "123 Main St");
        person.put("address", address);
        address.put("owner", person);
        Session s = openSession();
        s.beginTransaction();
        s.persist("Person", person);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        EntityStatistics addressStats = sessionFactory().getStatistics().getEntityStatistics("Address");
        person = ((Map) (s.createQuery("from Person p join fetch p.address").uniqueResult()));
        Assert.assertNotNull("could not locate person", person);
        Assert.assertNotNull("could not locate persons address", person.get("address"));
        s.clear();
        Object[] tuple = ((Object[]) (s.createQuery("select p.name, p from Person p join fetch p.address").uniqueResult()));
        Assert.assertEquals(tuple.length, 2);
        person = ((Map) (tuple[1]));
        Assert.assertNotNull("could not locate person", person);
        Assert.assertNotNull("could not locate persons address", person.get("address"));
        s.delete("Person", person);
        s.getTransaction().commit();
        s.close();
        Assert.assertEquals(addressStats.getFetchCount(), 0);
    }
}

