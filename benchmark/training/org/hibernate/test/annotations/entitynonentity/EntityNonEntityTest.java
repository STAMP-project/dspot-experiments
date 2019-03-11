/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.entitynonentity;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.UnknownEntityTypeException;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class EntityNonEntityTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testMix() throws Exception {
        GSM gsm = new GSM();
        gsm.brand = "Sony";
        gsm.frequency = 900;
        gsm.isNumeric = true;
        gsm.number = 2;
        gsm.species = "human";
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        s.persist(gsm);
        tx.commit();
        s.clear();
        tx = s.beginTransaction();
        gsm = ((GSM) (s.get(GSM.class, gsm.id)));
        Assert.assertEquals("top mapped superclass", 2, gsm.number);
        Assert.assertNull("non entity between mapped superclass and entity", gsm.species);
        Assert.assertTrue("mapped superclass under entity", gsm.isNumeric);
        Assert.assertNull("non entity under entity", gsm.brand);
        Assert.assertEquals("leaf entity", 900, gsm.frequency);
        s.delete(gsm);
        tx.commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9856")
    public void testGetAndFindNonEntityThrowsIllegalArgumentException() {
        try {
            sessionFactory().locateEntityPersister(Cellular.class);
        } catch (UnknownEntityTypeException ignore) {
            // expected
        }
        try {
            sessionFactory().locateEntityPersister(Cellular.class.getName());
        } catch (UnknownEntityTypeException ignore) {
            // expected
        }
        Session s = openSession();
        s.beginTransaction();
        try {
            s.get(Cellular.class, 1);
            Assert.fail("Expecting a failure");
        } catch (UnknownEntityTypeException ignore) {
            // expected
        } finally {
            s.getTransaction().commit();
            s.close();
        }
        s = openSession();
        s.beginTransaction();
        try {
            s.get(Cellular.class.getName(), 1);
            Assert.fail("Expecting a failure");
        } catch (UnknownEntityTypeException ignore) {
            // expected
        } finally {
            s.getTransaction().commit();
            s.close();
        }
    }
}

