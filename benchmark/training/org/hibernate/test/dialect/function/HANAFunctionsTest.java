/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.dialect.function;


import java.math.BigDecimal;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.query.Query;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


@RequiresDialect(AbstractHANADialect.class)
public class HANAFunctionsTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12546")
    public void testLocateFunction() {
        try (Session s = openSession()) {
            Transaction tx = s.beginTransaction();
            Query<Product> q = s.createQuery("select p from Product p where locate('.', cast(p.price as string)) > 0", Product.class);
            Product p = q.uniqueResult();
            Assert.assertNotNull(p);
            Assert.assertEquals(100, p.getLength());
            Assert.assertEquals(BigDecimal.valueOf(1.29), p.getPrice());
            tx.commit();
        }
        try (Session s = openSession()) {
            Transaction tx = s.beginTransaction();
            Query<Product> q = s.createQuery("select p from Product p where locate('.', cast(p.price as string)) = 0", Product.class);
            Product p = q.uniqueResult();
            Assert.assertNull(p);
            tx.commit();
        }
        try (Session s = openSession()) {
            Transaction tx = s.beginTransaction();
            Query<Product> q = s.createQuery("select p from Product p where locate('.', cast(p.price as string), 3) > 0", Product.class);
            Product p = q.uniqueResult();
            Assert.assertNull(p);
            tx.commit();
        }
    }

    @Test
    public void testSubstringFunction() {
        try (Session s = openSession()) {
            Transaction tx = s.beginTransaction();
            Query<Product> q = s.createQuery("select p from Product p where substring(cast(p.price as string), 1, 2) = '1.'", Product.class);
            Product p = q.uniqueResult();
            Assert.assertNotNull(p);
            Assert.assertEquals(100, p.getLength());
            Assert.assertEquals(BigDecimal.valueOf(1.29), p.getPrice());
            tx.commit();
        }
        try (Session s = openSession()) {
            Transaction tx = s.beginTransaction();
            Query<Product> q = s.createQuery("select p from Product p where substring(cast(p.price as string), 1, 2) = '.1'", Product.class);
            Product p = q.uniqueResult();
            Assert.assertNull(p);
            tx.commit();
        }
        try (Session s = openSession()) {
            Transaction tx = s.beginTransaction();
            Query<Product> q = s.createQuery("select p from Product p where substring(cast(p.price as string), 1) = '1.29'", Product.class);
            Product p = q.uniqueResult();
            Assert.assertNotNull(p);
            Assert.assertEquals(100, p.getLength());
            Assert.assertEquals(BigDecimal.valueOf(1.29), p.getPrice());
            tx.commit();
        }
        try (Session s = openSession()) {
            Transaction tx = s.beginTransaction();
            Query<Product> q = s.createQuery("select p from Product p where substring(cast(p.price as string), 1) = '1.'", Product.class);
            Product p = q.uniqueResult();
            Assert.assertNull(p);
            tx.commit();
        }
    }
}

