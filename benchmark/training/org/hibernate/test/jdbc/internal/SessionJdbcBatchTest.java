/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.jdbc.internal;


import DialectChecks.SupportsJdbcDriverProxying;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.Session;
import org.hibernate.test.util.jdbc.PreparedStatementSpyConnectionProvider;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialectFeature(SupportsJdbcDriverProxying.class)
public class SessionJdbcBatchTest extends BaseNonConfigCoreFunctionalTestCase {
    private PreparedStatementSpyConnectionProvider connectionProvider = new PreparedStatementSpyConnectionProvider(true, false);

    private long id;

    @Test
    public void testSessionFactorySetting() throws SQLException {
        Session session = sessionFactory().openSession();
        session.beginTransaction();
        try {
            addEvents(session);
        } finally {
            connectionProvider.clear();
            session.getTransaction().commit();
            session.close();
        }
        PreparedStatement preparedStatement = connectionProvider.getPreparedStatement("insert into Event (name, id) values (?, ?)");
        Mockito.verify(preparedStatement, Mockito.times(5)).addBatch();
        Mockito.verify(preparedStatement, Mockito.times(3)).executeBatch();
    }

    @Test
    public void testSessionSettingOverridesSessionFactorySetting() throws SQLException {
        Session session = sessionFactory().openSession();
        session.setJdbcBatchSize(3);
        session.beginTransaction();
        try {
            addEvents(session);
        } finally {
            connectionProvider.clear();
            session.getTransaction().commit();
            session.close();
        }
        PreparedStatement preparedStatement = connectionProvider.getPreparedStatement("insert into Event (name, id) values (?, ?)");
        Mockito.verify(preparedStatement, Mockito.times(5)).addBatch();
        Mockito.verify(preparedStatement, Mockito.times(2)).executeBatch();
        session = sessionFactory().openSession();
        session.setJdbcBatchSize(null);
        session.beginTransaction();
        try {
            addEvents(session);
        } finally {
            connectionProvider.clear();
            session.getTransaction().commit();
            session.close();
        }
        List<PreparedStatement> preparedStatements = connectionProvider.getPreparedStatements();
        Assert.assertEquals(1, preparedStatements.size());
        preparedStatement = preparedStatements.get(0);
        Mockito.verify(preparedStatement, Mockito.times(5)).addBatch();
        Mockito.verify(preparedStatement, Mockito.times(3)).executeBatch();
    }

    @Entity(name = "Event")
    public static class Event {
        @Id
        private Long id;

        private String name;
    }
}

