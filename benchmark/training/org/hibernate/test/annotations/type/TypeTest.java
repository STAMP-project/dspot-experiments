/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.type;


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
public class TypeTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testIdWithMulticolumns() throws Exception {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        Dvd lesOiseaux = new Dvd();
        lesOiseaux.setTitle("Les oiseaux");
        s.persist(lesOiseaux);
        s.flush();
        Assert.assertNotNull(lesOiseaux.getId());
        tx.rollback();
        s.close();
    }
}

