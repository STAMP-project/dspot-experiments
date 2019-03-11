/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.math;


import org.hibernate.Session;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Brett Meyer
 */
@RequiresDialect({ Oracle8iDialect.class, H2Dialect.class })
public class MathTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testBitAnd() {
        MathEntity me = new MathEntity();
        me.setValue(5);
        Session s = openSession();
        s.beginTransaction();
        Long id = ((Long) (s.save(me)));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        int value1 = ((Integer) (s.createQuery(("select bitand(m.value,0) from MathEntity m where m.id=" + id)).uniqueResult())).intValue();
        int value2 = ((Integer) (s.createQuery(("select bitand(m.value,2) from MathEntity m where m.id=" + id)).uniqueResult())).intValue();
        int value3 = ((Integer) (s.createQuery(("select bitand(m.value,3) from MathEntity m where m.id=" + id)).uniqueResult())).intValue();
        s.getTransaction().commit();
        s.close();
        Assert.assertEquals(value1, 0);
        Assert.assertEquals(value2, 0);
        Assert.assertEquals(value3, 1);
    }
}

