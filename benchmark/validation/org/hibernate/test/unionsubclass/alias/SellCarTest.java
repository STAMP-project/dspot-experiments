/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.unionsubclass.alias;


import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Strong Liu <stliu@redhat.com>
 */
@TestForIssue(jiraKey = "HHH-4825")
public class SellCarTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testSellCar() throws Exception {
        prepareData();
        Session session = openSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery("from Seller");
        Seller seller = ((Seller) (query.uniqueResult()));
        Assert.assertNotNull(seller);
        Assert.assertEquals(1, seller.getBuyers().size());
        tx.commit();
        session.close();
    }
}

