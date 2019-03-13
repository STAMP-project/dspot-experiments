/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.rowid;


import java.math.BigDecimal;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.dialect.Oracle9iDialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
@RequiresDialect(Oracle9iDialect.class)
public class RowIdTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testRowId() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Point p = new Point(new BigDecimal(1.0), new BigDecimal(1.0));
        s.persist(p);
        t.commit();
        s.clear();
        t = s.beginTransaction();
        p = ((Point) (s.createCriteria(Point.class).uniqueResult()));
        p.setDescription("new desc");
        t.commit();
        s.clear();
        t = s.beginTransaction();
        p = ((Point) (s.createQuery("from Point").uniqueResult()));
        p.setDescription("new new desc");
        t.commit();
        s.clear();
        t = s.beginTransaction();
        p = ((Point) (s.get(Point.class, p)));
        p.setDescription("new new new desc");
        t.commit();
        s.close();
    }
}

