/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.exception;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import org.hibernate.Session;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.dialect.MySQLMyISAMDialect;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.jdbc.spi.ResultSetReturn;
import org.hibernate.engine.jdbc.spi.StatementPreparer;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.jdbc.Work;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Implementation of SQLExceptionConversionTest.
 *
 * @author Steve Ebersole
 */
public class SQLExceptionConversionTest extends BaseCoreFunctionalTestCase {
    @Test
    @SkipForDialect(value = { MySQLMyISAMDialect.class, AbstractHANADialect.class }, comment = "MySQL (MyISAM) / Hana do not support FK violation checking")
    public void testIntegrityViolation() throws Exception {
        final Session session = openSession();
        session.beginTransaction();
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                // Attempt to insert some bad values into the T_MEMBERSHIP table that should
                // result in a constraint violation
                PreparedStatement ps = null;
                try {
                    ps = getJdbcCoordinator().getStatementPreparer().prepareStatement("INSERT INTO T_MEMBERSHIP (user_id, group_id) VALUES (?, ?)");
                    ps.setLong(1, 52134241);// Non-existent user_id

                    ps.setLong(2, 5342);// Non-existent group_id

                    getJdbcCoordinator().getResultSetReturn().executeUpdate(ps);
                    Assert.fail("INSERT should have failed");
                } catch (ConstraintViolationException ignore) {
                    // expected outcome
                } finally {
                    releaseStatement(session, ps);
                }
            }
        });
        session.getTransaction().rollback();
        session.close();
    }

    @Test
    public void testBadGrammar() throws Exception {
        final Session session = openSession();
        session.beginTransaction();
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                // prepare/execute a query against a non-existent table
                PreparedStatement ps = null;
                try {
                    ps = getJdbcCoordinator().getStatementPreparer().prepareStatement("SELECT user_id, user_name FROM tbl_no_there");
                    getJdbcCoordinator().getResultSetReturn().extract(ps);
                    Assert.fail("SQL compilation should have failed");
                } catch (SQLGrammarException ignored) {
                    // expected outcome
                } finally {
                    releaseStatement(session, ps);
                }
            }
        });
        session.getTransaction().rollback();
        session.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-7357")
    public void testNotNullConstraint() {
        final Session session = openSession();
        session.beginTransaction();
        final User user = new User();
        user.setUsername("Lukasz");
        session.save(user);
        session.flush();
        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                final JdbcCoordinator jdbcCoordinator = ((org.hibernate.engine.spi.SessionImplementor) (session)).getJdbcCoordinator();
                final StatementPreparer statementPreparer = jdbcCoordinator.getStatementPreparer();
                final ResultSetReturn resultSetReturn = jdbcCoordinator.getResultSetReturn();
                PreparedStatement ps = null;
                try {
                    ps = statementPreparer.prepareStatement("UPDATE T_USER SET user_name = ? WHERE user_id = ?");
                    ps.setNull(1, Types.VARCHAR);// Attempt to update user name to NULL (NOT NULL constraint defined).

                    ps.setLong(2, user.getId());
                    resultSetReturn.executeUpdate(ps);
                    Assert.fail("UPDATE should have failed because of not NULL constraint.");
                } catch (ConstraintViolationException ignore) {
                    // expected outcome
                } finally {
                    releaseStatement(session, ps);
                }
            }
        });
        session.getTransaction().rollback();
        session.close();
    }
}

