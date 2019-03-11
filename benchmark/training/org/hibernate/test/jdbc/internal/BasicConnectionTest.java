/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.jdbc.internal;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 * @author Brett Meyer
 */
public class BasicConnectionTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testExceptionHandling() {
        Session session = openSession();
        SessionImplementor sessionImpl = ((SessionImplementor) (session));
        boolean caught = false;
        try {
            PreparedStatement ps = sessionImpl.getJdbcCoordinator().getStatementPreparer().prepareStatement("select count(*) from NON_EXISTENT");
            sessionImpl.getJdbcCoordinator().getResultSetReturn().execute(ps);
        } catch (JDBCException ok) {
            caught = true;
        } finally {
            session.close();
        }
        Assert.assertTrue("The connection did not throw a JDBCException as expected", caught);
    }

    @Test
    public void testBasicJdbcUsage() throws JDBCException {
        Session session = openSession();
        SessionImplementor sessionImpl = ((SessionImplementor) (session));
        JdbcCoordinator jdbcCoord = sessionImpl.getJdbcCoordinator();
        try {
            Statement statement = jdbcCoord.getStatementPreparer().createStatement();
            String dropSql = getDialect().getDropTableString("SANDBOX_JDBC_TST");
            try {
                jdbcCoord.getResultSetReturn().execute(statement, dropSql);
            } catch (Exception e) {
                // ignore if the DB doesn't support "if exists" and the table doesn't exist
            }
            jdbcCoord.getResultSetReturn().execute(statement, "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )");
            Assert.assertTrue(getResourceRegistry(jdbcCoord).hasRegisteredResources());
            Assert.assertTrue(jdbcCoord.getLogicalConnection().isPhysicallyConnected());
            getResourceRegistry(jdbcCoord).release(statement);
            Assert.assertFalse(getResourceRegistry(jdbcCoord).hasRegisteredResources());
            Assert.assertTrue(jdbcCoord.getLogicalConnection().isPhysicallyConnected());// after_transaction specified

            PreparedStatement ps = jdbcCoord.getStatementPreparer().prepareStatement("insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )");
            ps.setLong(1, 1);
            ps.setString(2, "name");
            jdbcCoord.getResultSetReturn().execute(ps);
            ps = jdbcCoord.getStatementPreparer().prepareStatement("select * from SANDBOX_JDBC_TST");
            jdbcCoord.getResultSetReturn().extract(ps);
            Assert.assertTrue(getResourceRegistry(jdbcCoord).hasRegisteredResources());
        } catch (SQLException e) {
            Assert.fail("incorrect exception type : sqlexception");
        } finally {
            try {
                session.doWork(( connection) -> {
                    final Statement stmnt = connection.createStatement();
                    stmnt.execute(getDialect().getDropTableString("SANDBOX_JDBC_TST"));
                });
            } finally {
                session.close();
            }
        }
        Assert.assertFalse(getResourceRegistry(jdbcCoord).hasRegisteredResources());
    }
}

