/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.lock;


import DialectChecks.SupportsJdbcDriverProxying;
import java.sql.PreparedStatement;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.test.util.jdbc.PreparedStatementSpyConnectionProvider;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.hibernate.testing.util.ExceptionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@RequiresDialectFeature(SupportsJdbcDriverProxying.class)
public class StatementIsClosedAfterALockExceptionTest extends BaseEntityManagerFunctionalTestCase {
    private static final PreparedStatementSpyConnectionProvider CONNECTION_PROVIDER = new PreparedStatementSpyConnectionProvider(false, false);

    private Integer lockId;

    // 30 seconds
    @Test(timeout = 1000 * 30)
    @TestForIssue(jiraKey = "HHH-11617")
    public void testStatementIsClosed() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em1) -> {
            Map<String, Object> properties = new HashMap<>();
            properties.put(org.hibernate.cfg.AvailableSettings.JPA_LOCK_TIMEOUT, 0L);
            Lock lock2 = em1.find(.class, lockId, LockModeType.PESSIMISTIC_WRITE, properties);
            assertEquals("lock mode should be PESSIMISTIC_WRITE ", LockModeType.PESSIMISTIC_WRITE, em1.getLockMode(lock2));
            TransactionUtil.doInJPA(this::entityManagerFactory, ( em2) -> {
                TransactionUtil.setJdbcTimeout(em2.unwrap(.class));
                try {
                    em2.find(.class, lockId, LockModeType.PESSIMISTIC_WRITE, properties);
                    fail("Exception should be thrown");
                } catch ( lte) {
                    if (!(ExceptionUtil.isSqlLockTimeout(lte))) {
                        fail("Should have thrown a Lock timeout exception");
                    }
                } finally {
                    try {
                        for (PreparedStatement statement : CONNECTION_PROVIDER.getPreparedStatements()) {
                            assertThat(("A SQL Statement was not closed : " + (statement.toString())), statement.isClosed(), is(true));
                        }
                    } catch ( e) {
                        fail(e.getMessage());
                    }
                }
            });
        });
    }
}

