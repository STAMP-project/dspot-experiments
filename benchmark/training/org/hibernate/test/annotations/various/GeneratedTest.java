/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.various;


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
public class GeneratedTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testGenerated() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Antenna antenna = new Antenna();
        antenna.id = new Integer(1);
        s.persist(antenna);
        Assert.assertNull(antenna.latitude);
        Assert.assertNull(antenna.longitude);
        tx.commit();
        s.close();
    }
}

