/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.sql.refcursor;


import java.util.Arrays;
import org.hibernate.Session;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
@RequiresDialect(Oracle8iDialect.class)
public class CursorFromCallableTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-8022")
    public void testReadResultSetFromRefCursor() {
        Session session = openSession();
        session.getTransaction().begin();
        Assert.assertEquals(Arrays.asList(new NumValue(1, "Line 1"), new NumValue(2, "Line 2")), session.getNamedQuery("NumValue.getSomeValues").list());
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-7984")
    public void testStatementClosing() {
        Session session = openSession();
        session.getTransaction().begin();
        // Reading maximum number of opened cursors requires SYS privileges.
        // Verify statement closing with JdbcCoordinator#hasRegisteredResources() instead.
        // BigDecimal maxCursors = (BigDecimal) session.createSQLQuery( "SELECT value FROM v$parameter WHERE name = 'open_cursors'" ).uniqueResult();
        // for ( int i = 0; i < maxCursors + 10; ++i ) { named_query_execution }
        Assert.assertEquals(Arrays.asList(new NumValue(1, "Line 1"), new NumValue(2, "Line 2")), session.getNamedQuery("NumValue.getSomeValues").list());
        JdbcCoordinator jdbcCoordinator = getJdbcCoordinator();
        Assert.assertFalse("Prepared statement and result set should be released after query execution.", jdbcCoordinator.getResourceRegistry().hasRegisteredResources());
        session.getTransaction().commit();
        session.close();
    }
}

