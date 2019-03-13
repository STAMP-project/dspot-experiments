/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.collection.map.hhh7557;


import java.util.HashMap;
import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Elizabeth Chatman
 * @author Steve Ebersole
 */
public class EntityMapTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testInsertIntoMap() throws Exception {
        {
            // Session 1: Insert 3 values into the map
            Session session = openSession();
            session.beginTransaction();
            MapHolder mapHolder = new MapHolder();
            mapHolder.setMap(new HashMap<MapKey, MapValue>());
            addMapEntry(session, mapHolder, "A", "1");
            addMapEntry(session, mapHolder, "B", "2");
            addMapEntry(session, mapHolder, "C", "3");
            session.save(mapHolder);
            // Verify there are 3 entries in the map
            Assert.assertEquals(3, mapHolder.getMap().size());
            session.getTransaction().commit();
            session.close();
        }
        {
            // Session 2: Add a 4th value to the map
            Session session = openSession();
            session.beginTransaction();
            MapHolder mapHolder = getMapHolder(session);
            System.out.println("Got MapHolder; checking map size -----");
            Assert.assertEquals(3, mapHolder.getMap().size());
            System.out.println("Got MapHolder; checked map size -----");
            addMapEntry(session, mapHolder, "D", "4");
            // Verify there are 4 entries in the map
            Assert.assertEquals(4, mapHolder.getMap().size());
            session.getTransaction().commit();
            session.close();
        }
        {
            // Session 3: Count the entries in the map
            Session session = openSession();
            session.beginTransaction();
            MapHolder mapHolder = getMapHolder(session);
            // Fails here (expected:<4> but was:<1>)
            Assert.assertEquals(4, mapHolder.getMap().size());
            session.getTransaction().commit();
            session.close();
        }
    }
}

