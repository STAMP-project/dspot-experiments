/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.indexcoll.eager;


import java.util.Date;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.test.annotations.indexcoll.Gas;
import org.hibernate.test.annotations.indexcoll.GasKey;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;

import static org.hibernate.test.annotations.indexcoll.eager.Atmosphere.Level.HIGH;


/**
 * Test index collections
 *
 * @author Emmanuel Bernard
 */
public class EagerIndexedCollectionTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testJPA2DefaultMapColumns() throws Exception {
        isDefaultKeyColumnPresent(Atmosphere.class.getName(), "gasesDef", "_KEY");
        isDefaultKeyColumnPresent(Atmosphere.class.getName(), "gasesPerKeyDef", "_KEY");
        isDefaultKeyColumnPresent(Atmosphere.class.getName(), "gasesDefLeg", "_KEY");
    }

    @Test
    public void testRealMap() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Atmosphere atm = new Atmosphere();
        Atmosphere atm2 = new Atmosphere();
        GasKey key = new GasKey();
        key.setName("O2");
        Gas o2 = new Gas();
        o2.name = "oxygen";
        atm.gases.put("100%", o2);
        atm.gasesPerKey.put(key, o2);
        atm2.gases.put("100%", o2);
        atm2.gasesPerKey.put(key, o2);
        s.persist(key);
        s.persist(atm);
        s.persist(atm2);
        s.flush();
        s.clear();
        atm = ((Atmosphere) (s.get(Atmosphere.class, atm.id)));
        key = ((GasKey) (s.get(GasKey.class, key.getName())));
        Assert.assertEquals(1, atm.gases.size());
        Assert.assertEquals(o2.name, atm.gases.get("100%").name);
        Assert.assertEquals(o2.name, atm.gasesPerKey.get(key).name);
        tx.rollback();
        s.close();
    }

    @Test
    public void testTemporalKeyMap() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Atmosphere atm = new Atmosphere();
        atm.colorPerDate.put(new Date(1234567000), "red");
        s.persist(atm);
        s.flush();
        s.clear();
        atm = ((Atmosphere) (s.get(Atmosphere.class, atm.id)));
        Assert.assertEquals(1, atm.colorPerDate.size());
        final Date date = atm.colorPerDate.keySet().iterator().next();
        final long diff = (new Date(1234567000).getTime()) - (date.getTime());
        Assert.assertTrue("24h diff max", ((diff >= 0) && (diff < (((24 * 60) * 60) * 1000))));
        tx.rollback();
        s.close();
    }

    @Test
    public void testEnumKeyType() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Atmosphere atm = new Atmosphere();
        atm.colorPerLevel.put(HIGH, "red");
        s.persist(atm);
        s.flush();
        s.clear();
        atm = ((Atmosphere) (s.get(Atmosphere.class, atm.id)));
        Assert.assertEquals(1, atm.colorPerLevel.size());
        Assert.assertEquals("red", atm.colorPerLevel.get(HIGH));
        tx.rollback();
        s.close();
    }

    @Test
    public void testEntityKeyElementTarget() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Atmosphere atm = new Atmosphere();
        Gas o2 = new Gas();
        o2.name = "oxygen";
        atm.composition.put(o2, 94.3);
        s.persist(o2);
        s.persist(atm);
        s.flush();
        s.clear();
        atm = ((Atmosphere) (s.get(Atmosphere.class, atm.id)));
        Assert.assertTrue(Hibernate.isInitialized(atm.composition));
        Assert.assertEquals(1, atm.composition.size());
        Assert.assertEquals(o2.name, atm.composition.keySet().iterator().next().name);
        tx.rollback();
        s.close();
    }
}

