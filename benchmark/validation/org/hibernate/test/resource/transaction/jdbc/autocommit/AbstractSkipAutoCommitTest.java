/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.resource.transaction.jdbc.autocommit;


import DialectChecks.SupportsJdbcDriverProxying;
import java.sql.Connection;
import java.sql.SQLException;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.test.util.jdbc.PreparedStatementSpyConnectionProvider;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialectFeature(SupportsJdbcDriverProxying.class)
public abstract class AbstractSkipAutoCommitTest extends BaseEntityManagerFunctionalTestCase {
    private PreparedStatementSpyConnectionProvider connectionProvider = new PreparedStatementSpyConnectionProvider(false, true) {
        @Override
        protected Connection actualConnection() throws SQLException {
            Connection connection = super.actualConnection();
            connection.setAutoCommit(false);
            return connection;
        }
    };

    @Test
    public void test() {
        connectionProvider.clear();
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.resource.transaction.jdbc.autocommit.City city = new org.hibernate.test.resource.transaction.jdbc.autocommit.City();
            city.setId(1L);
            city.setName("Cluj-Napoca");
            entityManager.persist(city);
            assertTrue(connectionProvider.getAcquiredConnections().isEmpty());
            assertTrue(connectionProvider.getReleasedConnections().isEmpty());
        });
        verifyConnections();
        connectionProvider.clear();
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.resource.transaction.jdbc.autocommit.City city = entityManager.find(.class, 1L);
            assertEquals("Cluj-Napoca", city.getName());
        });
        verifyConnections();
    }

    @Entity(name = "City")
    public static class City {
        @Id
        private Long id;

        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

