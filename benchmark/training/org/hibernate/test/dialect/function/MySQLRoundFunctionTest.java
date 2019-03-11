/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.dialect.function;


import java.math.BigDecimal;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Strong Liu <stliu@redhat.com>
 */
@RequiresDialect(MySQLDialect.class)
public class MySQLRoundFunctionTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testRoundFunction() {
        Product product = new Product();
        product.setLength(100);
        product.setPrice(new BigDecimal(1.298));
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        s.save(product);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        Query q = s.createQuery("select round(p.price,1) from Product p");
        Object o = q.uniqueResult();
        Assert.assertEquals(BigDecimal.class, o.getClass());
        Assert.assertEquals(BigDecimal.valueOf(1.3), o);
        tx.commit();
        s.close();
    }
}

