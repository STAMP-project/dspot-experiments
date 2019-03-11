/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.xml.hbm;


import DialectChecks.SupportsIdentityColumns;
import org.hibernate.Session;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
@RequiresDialectFeature(SupportsIdentityColumns.class)
public class HbmWithIdentityTest extends BaseCoreFunctionalTestCase {
    @Test
    @SkipForDialect(value = AbstractHANADialect.class, comment = " HANA doesn't support tables consisting of only a single auto-generated column")
    public void testManyToOneAndInterface() throws Exception {
        Session s = openSession();
        s.getTransaction().begin();
        B b = new BImpl();
        b.setBId(1);
        s.persist(b);
        Z z = new ZImpl();
        z.setB(b);
        s.persist(z);
        s.flush();
        s.getTransaction().rollback();
        s.close();
    }
}

