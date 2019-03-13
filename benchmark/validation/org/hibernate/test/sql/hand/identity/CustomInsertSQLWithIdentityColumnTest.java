/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.sql.hand.identity;


import DialectChecks.SupportsIdentityColumns;
import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.hibernate.test.sql.hand.Organization;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Custom SQL tests for combined usage of custom insert SQL and identity columns
 *
 * @author Gail Badner
 */
@RequiresDialectFeature(SupportsIdentityColumns.class)
public class CustomInsertSQLWithIdentityColumnTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testBadInsertionFails() {
        Session session = openSession();
        session.beginTransaction();
        Organization org = new Organization("hola!");
        try {
            session.save(org);
            session.delete(org);
            Assert.fail("expecting bad custom insert statement to fail");
        } catch (JDBCException e) {
            // expected failure
        }
        session.getTransaction().rollback();
        session.close();
    }
}

