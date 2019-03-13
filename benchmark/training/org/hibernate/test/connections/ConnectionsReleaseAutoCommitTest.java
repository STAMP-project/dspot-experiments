/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.connections;


import java.sql.Connection;
import java.sql.SQLException;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.sql.DataSource;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.engine.jdbc.connections.internal.UserSuppliedConnectionProviderImpl;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-12197")
@RequiresDialect(H2Dialect.class)
public class ConnectionsReleaseAutoCommitTest extends BaseEntityManagerFunctionalTestCase {
    private ConnectionsReleaseAutoCommitTest.ConnectionProviderDecorator connectionProvider;

    private Connection connection;

    @Test
    public void testConnectionAcquisitionCount() throws SQLException {
        connectionProvider.clear();
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            assertEquals(1, connectionProvider.getConnectionCount());
            org.hibernate.test.connections.Thing thing = new org.hibernate.test.connections.Thing();
            thing.setId(1);
            entityManager.persist(thing);
            assertEquals(1, connectionProvider.getConnectionCount());
        });
        Assert.assertEquals(1, connectionProvider.getConnectionCount());
        Mockito.verify(connectionProvider.connection, Mockito.times(1)).close();
    }

    @Entity(name = "Thing")
    @Table(name = "Thing")
    public static class Thing {
        @Id
        public Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    public static class ConnectionProviderDecorator extends UserSuppliedConnectionProviderImpl {
        private final DataSource dataSource;

        private int connectionCount;

        private Connection connection;

        public ConnectionProviderDecorator(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public Connection getConnection() throws SQLException {
            (connectionCount)++;
            connection = Mockito.spy(dataSource.getConnection());
            return connection;
        }

        @Override
        public void closeConnection(Connection connection) throws SQLException {
            connection.close();
        }

        public int getConnectionCount() {
            return this.connectionCount;
        }

        public void clear() {
            connectionCount = 0;
        }
    }
}

