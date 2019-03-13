/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
/**
 * $Id: CustomSQLTest.java 10977 2006-12-12 23:28:04Z steve.ebersole@jboss.com $
 */
package org.hibernate.test.legacy;


import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.testing.DialectCheck;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.SkipForDialect;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author MAX
 */
public class CustomSQLTest extends LegacyTestCase {
    public static class NonIdentityGeneratorChecker implements DialectCheck {
        @Override
        public boolean isMatch(Dialect dialect) {
            return !("identity".equals(getDialect().getNativeIdentifierGeneratorStrategy()));
        }
    }

    @Test
    @RequiresDialectFeature(CustomSQLTest.NonIdentityGeneratorChecker.class)
    @SkipForDialect(value = { PostgreSQL81Dialect.class, PostgreSQLDialect.class }, jiraKey = "HHH-6704")
    public void testInsert() throws SQLException, HibernateException {
        Session s = openSession();
        s.beginTransaction();
        Role p = new Role();
        p.setName("Patient");
        s.save(p);
        s.getTransaction().commit();
        s.close();
        sessionFactory().getCache().evictEntityRegion(Role.class);
        s = openSession();
        s.beginTransaction();
        Role p2 = ((Role) (s.get(Role.class, Long.valueOf(p.getId()))));
        Assert.assertNotSame(p, p2);
        Assert.assertEquals(p2.getId(), p.getId());
        Assert.assertTrue(p2.getName().equalsIgnoreCase(p.getName()));
        s.delete(p2);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testJoinedSubclass() throws SQLException, HibernateException {
        Session s = openSession();
        s.beginTransaction();
        Medication m = new Medication();
        m.setPrescribedDrug(new Drug());
        m.getPrescribedDrug().setName("Morphine");
        s.save(m.getPrescribedDrug());
        s.save(m);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Medication m2 = ((Medication) (s.get(Medication.class, m.getId())));
        Assert.assertNotSame(m, m2);
        s.getTransaction().commit();
        s.close();
    }
}

