/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.agroal;


import DialectChecks.SupportsJdbcDriverProxying;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.test.agroal.util.PreparedStatementSpyConnectionProvider;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialectFeature(SupportsJdbcDriverProxying.class)
public class AgroalSkipAutoCommitTest extends BaseCoreFunctionalTestCase {
    private PreparedStatementSpyConnectionProvider connectionProvider = new PreparedStatementSpyConnectionProvider();

    @Test
    public void test() {
        connectionProvider.clear();
        doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.agroal.City city = new org.hibernate.test.agroal.City();
            city.setId(1L);
            city.setName("Cluj-Napoca");
            session.persist(city);
            assertTrue(connectionProvider.getAcquiredConnections().isEmpty());
            assertTrue(connectionProvider.getReleasedConnections().isEmpty());
        });
        verifyConnections();
        connectionProvider.clear();
        doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.agroal.City city = session.find(.class, 1L);
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

