/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.querytimeout;


import DialectChecks.SupportsJdbcDriverProxying;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.test.util.jdbc.PreparedStatementSpyConnectionProvider;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
@RequiresDialectFeature(SupportsJdbcDriverProxying.class)
public class QueryTimeOutTest extends BaseNonConfigCoreFunctionalTestCase {
    private static final PreparedStatementSpyConnectionProvider CONNECTION_PROVIDER = new PreparedStatementSpyConnectionProvider(true, false);

    private static final String QUERY = "update AnEntity set name='abc'";

    @Test
    @TestForIssue(jiraKey = "HHH-12075")
    public void testCreateQuerySetTimeout() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Query query = session.createQuery(QUERY);
            query.setTimeout(123);
            query.executeUpdate();
            try {
                verify(CONNECTION_PROVIDER.getPreparedStatement(QUERY), times(1)).setQueryTimeout(123);
            } catch ( ex) {
                fail("should not have thrown exception");
            }
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12075")
    public void testCreateQuerySetTimeoutHint() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            Query query = session.createQuery(QUERY);
            query.setHint(QueryHints.SPEC_HINT_TIMEOUT, 123000);
            query.executeUpdate();
            try {
                verify(CONNECTION_PROVIDER.getPreparedStatement(QUERY), times(1)).setQueryTimeout(123);
            } catch ( ex) {
                fail("should not have thrown exception");
            }
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12075")
    public void testCreateNativeQuerySetTimeout() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            NativeQuery query = session.createNativeQuery(QUERY);
            query.setTimeout(123);
            query.executeUpdate();
            try {
                verify(CONNECTION_PROVIDER.getPreparedStatement(QUERY), times(1)).setQueryTimeout(123);
            } catch ( ex) {
                fail("should not have thrown exception");
            }
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12075")
    public void testCreateNativeQuerySetTimeoutHint() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            NativeQuery query = session.createNativeQuery(QUERY);
            query.setHint(QueryHints.SPEC_HINT_TIMEOUT, 123000);
            query.executeUpdate();
            try {
                verify(CONNECTION_PROVIDER.getPreparedStatement(QUERY), times(1)).setQueryTimeout(123);
            } catch ( ex) {
                fail("should not have thrown exception");
            }
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12075")
    public void testCreateSQLQuerySetTimeout() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            NativeQuery query = session.createSQLQuery(QUERY);
            query.setTimeout(123);
            query.executeUpdate();
            try {
                verify(CONNECTION_PROVIDER.getPreparedStatement(QUERY), times(1)).setQueryTimeout(123);
            } catch ( ex) {
                fail("should not have thrown exception");
            }
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12075")
    public void testCreateSQLQuerySetTimeoutHint() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            NativeQuery query = session.createSQLQuery(QUERY);
            query.setHint(QueryHints.SPEC_HINT_TIMEOUT, 123000);
            query.executeUpdate();
            try {
                verify(CONNECTION_PROVIDER.getPreparedStatement(QUERY), times(1)).setQueryTimeout(123);
            } catch ( ex) {
                fail("should not have thrown exception");
            }
        });
    }

    @Entity(name = "AnEntity")
    @Table(name = "AnEntity")
    public static class AnEntity {
        @Id
        private int id;

        private String name;
    }
}

