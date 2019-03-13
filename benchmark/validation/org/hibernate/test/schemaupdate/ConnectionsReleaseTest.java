/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate;


import TargetType.DATABASE;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.EnumSet;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.hbm2ddl.SchemaValidator;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-10443")
public class ConnectionsReleaseTest extends BaseUnitTestCase {
    private StandardServiceRegistry ssr;

    private MetadataImplementor metadata;

    private ConnectionsReleaseTest.ConnectionProviderDecorator connectionProvider;

    @Test
    public void testSchemaUpdateReleasesAllConnections() throws SQLException {
        new SchemaUpdate().execute(EnumSet.of(DATABASE), metadata);
        MatcherAssert.assertThat(connectionProvider.getOpenConnection(), Is.is(0));
    }

    @Test
    public void testSchemaValidatorReleasesAllConnections() throws SQLException {
        new SchemaValidator().validate(metadata);
        MatcherAssert.assertThat(connectionProvider.getOpenConnection(), Is.is(0));
    }

    @Entity(name = "Thing")
    @Table(name = "Thing")
    public static class Thing {
        @Id
        public Integer id;
    }

    public static class ConnectionProviderDecorator extends DriverManagerConnectionProviderImpl {
        private int openConnection;

        @Override
        public Connection getConnection() throws SQLException {
            (openConnection)++;
            return super.getConnection();
        }

        @Override
        public void closeConnection(Connection conn) throws SQLException {
            super.closeConnection(conn);
            (openConnection)--;
        }

        public int getOpenConnection() {
            return this.openConnection;
        }
    }
}

