/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.cascade.circle;


import java.util.Arrays;
import java.util.HashSet;
import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * The test case uses the following model:
 *
 *                          <-    ->
 *                      -- (N : 0,1) -- Tour
 *                      |    <-   ->
 *                      | -- (1 : N) -- (pickup) ----
 *          <-   ->     | |                          |
 * Route -- (1 : N) -- Node                      Transport
 *                      |  <-   ->                |
 *                      -- (1 : N) -- (delivery) --
 *
 *  Arrows indicate the direction of cascade-merge, cascade-save, cascade-refresh and cascade-save-or-update
 *
 * It reproduces the following issues:
 * https://hibernate.atlassian.net/browse/HHH-9512
 * <p/>
 * This tests that cascades are done properly from each entity.
 *
 * @author Alex Belyaev (based on code by Pavol Zibrita and Gail Badner)
 */
public class CascadeManagedAndTransientTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testAttachedChildInMerge() {
        fillInitialData();
        Session s = openSession();
        s.beginTransaction();
        Route route = ((Route) (s.createQuery("FROM Route WHERE name = :name").setString("name", "Route 1").uniqueResult()));
        Node n2 = ((Node) (s.createQuery("FROM Node WHERE name = :name").setString("name", "Node 2").uniqueResult()));
        Node n3 = ((Node) (s.createQuery("FROM Node WHERE name = :name").setString("name", "Node 3").uniqueResult()));
        Vehicle vehicle = new Vehicle();
        vehicle.setName("Bus");
        vehicle.setRoute(route);
        Transport $2to3 = new Transport();
        $2to3.setName("Transport 2 -> 3");
        $2to3.setPickupNode(n2);
        n2.getPickupTransports().add($2to3);
        $2to3.setDeliveryNode(n3);
        n3.getDeliveryTransports().add($2to3);
        $2to3.setVehicle(vehicle);
        vehicle.setTransports(new HashSet<Transport>(Arrays.asList($2to3)));
        // Try to save graph of transient entities (vehicle, transport) which contains attached entities (node2, node3)
        Vehicle managedVehicle = ((Vehicle) (s.merge(vehicle)));
        checkNewVehicle(managedVehicle);
        s.flush();
        s.clear();
        Assert.assertEquals(3, s.createQuery("FROM Transport").list().size());
        Assert.assertEquals(2, s.createQuery("FROM Vehicle").list().size());
        Assert.assertEquals(4, s.createQuery("FROM Node").list().size());
        Vehicle newVehicle = ((Vehicle) (s.createQuery("FROM Vehicle WHERE name = :name").setParameter("name", "Bus").uniqueResult()));
        checkNewVehicle(newVehicle);
        s.getTransaction().commit();
        s.close();
    }
}

