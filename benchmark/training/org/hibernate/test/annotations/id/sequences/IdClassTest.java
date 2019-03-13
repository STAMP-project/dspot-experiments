/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.id.sequences;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.test.annotations.id.sequences.entities.Location;
import org.hibernate.test.annotations.id.sequences.entities.MilitaryBuilding;
import org.hibernate.test.annotations.id.sequences.entities.Tower;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
@SuppressWarnings("unchecked")
public class IdClassTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testIdClassInSuperclass() throws Exception {
        Tower tower = new Tower();
        tower.latitude = 10.3;
        tower.longitude = 45.4;
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        s.persist(tower);
        s.flush();
        s.clear();
        Location loc = new Location();
        loc.latitude = tower.latitude;
        loc.longitude = tower.longitude;
        Assert.assertNotNull(s.get(Tower.class, loc));
        tx.rollback();
        s.close();
    }
}

