/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.boot.database.qualfiedTableNaming;


import Namespace.Name;
import java.sql.Connection;
import java.sql.SQLException;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.testing.jdbc.JdbcMocks;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class QualifiedTableNamingTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testQualifiedNameSeparator() throws Exception {
        Namespace.Name namespaceName = new Namespace.Name(Identifier.toIdentifier("DB1"), Identifier.toIdentifier("PUBLIC"));
        String expectedName = null;
        for (Namespace namespace : metadata().getDatabase().getNamespaces()) {
            if (!(namespace.getName().equals(namespaceName))) {
                continue;
            }
            Assert.assertEquals(1, namespace.getTables().size());
            expectedName = metadata().getDatabase().getJdbcEnvironment().getQualifiedObjectNameFormatter().format(namespace.getTables().iterator().next().getQualifiedTableName(), getDialect());
        }
        Assert.assertNotNull(expectedName);
        SingleTableEntityPersister persister = ((SingleTableEntityPersister) (sessionFactory().getEntityPersister(QualifiedTableNamingTest.Box.class.getName())));
        Assert.assertEquals(expectedName, persister.getTableName());
    }

    @Entity(name = "Box")
    @Table(name = "Box", schema = "PUBLIC", catalog = "DB1")
    public static class Box {
        @Id
        public Integer id;

        public String value;
    }

    public static class TestDialect extends Dialect {
        @Override
        public NameQualifierSupport getNameQualifierSupport() {
            return NameQualifierSupport.BOTH;
        }
    }

    public static class MockedConnectionProvider implements ConnectionProvider {
        private Connection connection;

        @Override
        public Connection getConnection() throws SQLException {
            if ((connection) == null) {
                connection = JdbcMocks.createConnection("db1", 0);
            }
            return connection;
        }

        @Override
        public void closeConnection(Connection conn) throws SQLException {
        }

        @Override
        public boolean supportsAggressiveRelease() {
            return false;
        }

        @Override
        public boolean isUnwrappableAs(Class unwrapType) {
            return false;
        }

        @Override
        public <T> T unwrap(Class<T> unwrapType) {
            return null;
        }
    }
}

