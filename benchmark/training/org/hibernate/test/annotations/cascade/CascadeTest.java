/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.cascade;


import java.util.ArrayList;
import java.util.Collection;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Check some of the individual cascade styles
 *
 * @unknown do something for refresh
 * @author Emmanuel Bernard
 */
public class CascadeTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testPersist() {
        Session s;
        Transaction tx;
        s = openSession();
        Tooth tooth = new Tooth();
        Tooth leftTooth = new Tooth();
        tooth.leftNeighbour = leftTooth;
        s.persist(tooth);
        tx = s.beginTransaction();
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        leftTooth = ((Tooth) (s.get(Tooth.class, leftTooth.id)));
        Assert.assertNotNull(leftTooth);
        tx.commit();
        s.close();
    }

    @Test
    public void testMerge() {
        Session s;
        Transaction tx;
        s = openSession();
        Tooth tooth = new Tooth();
        Tooth rightTooth = new Tooth();
        tooth.type = "canine";
        tooth.rightNeighbour = rightTooth;
        rightTooth.type = "incisive";
        s.persist(rightTooth);
        s.persist(tooth);
        tx = s.beginTransaction();
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        tooth = ((Tooth) (s.get(Tooth.class, tooth.id)));
        Assert.assertEquals("incisive", tooth.rightNeighbour.type);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        tooth.rightNeighbour.type = "premolars";
        s.merge(tooth);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        rightTooth = ((Tooth) (s.get(Tooth.class, rightTooth.id)));
        Assert.assertEquals("premolars", rightTooth.type);
        tx.commit();
        s.close();
    }

    @Test
    public void testRemove() {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        Tooth tooth = new Tooth();
        Mouth mouth = new Mouth();
        s.persist(mouth);
        s.persist(tooth);
        tooth.mouth = mouth;
        mouth.teeth = new ArrayList<Tooth>();
        mouth.teeth.add(tooth);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        tooth = ((Tooth) (s.get(Tooth.class, tooth.id)));
        Assert.assertNotNull(tooth);
        s.delete(tooth.mouth);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        tooth = ((Tooth) (s.get(Tooth.class, tooth.id)));
        Assert.assertNull(tooth);
        tx.commit();
        s.close();
    }

    @Test
    public void testDetach() {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        Tooth tooth = new Tooth();
        Mouth mouth = new Mouth();
        s.persist(mouth);
        s.persist(tooth);
        tooth.mouth = mouth;
        mouth.teeth = new ArrayList<Tooth>();
        mouth.teeth.add(tooth);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        mouth = ((Mouth) (s.get(Mouth.class, mouth.id)));
        Assert.assertNotNull(mouth);
        Assert.assertEquals(1, mouth.teeth.size());
        tooth = mouth.teeth.iterator().next();
        s.evict(mouth);
        Assert.assertFalse(s.contains(tooth));
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        s.delete(s.get(Mouth.class, mouth.id));
        tx.commit();
        s.close();
    }
}

