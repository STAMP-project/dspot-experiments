/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.ejb3configuration;


import DialectChecks.SupportsJdbcDriverProxying;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.jpa.test.Wallet;
import org.hibernate.test.util.jdbc.PreparedStatementSpyConnectionProvider;
import org.hibernate.testing.RequiresDialectFeature;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialectFeature(SupportsJdbcDriverProxying.class)
public class DisableDiscardPersistenceContextOnCloseTest extends BaseEntityManagerFunctionalTestCase {
    private PreparedStatementSpyConnectionProvider connectionProvider = new PreparedStatementSpyConnectionProvider(false, false);

    @Test
    public void testDiscardOnClose() throws SQLException {
        EntityManager em = entityManagerFactory().createEntityManager();
        Wallet wallet = new Wallet();
        wallet.setSerial("123");
        try {
            em.getTransaction().begin();
            em.persist(wallet);
            Assert.assertEquals(1, connectionProvider.getAcquiredConnections().size());
            em.close();
            Assert.assertEquals(1, connectionProvider.getAcquiredConnections().size());
            Assert.assertTrue(em.getTransaction().isActive());
        } finally {
            Assert.assertEquals(1, connectionProvider.getAcquiredConnections().size());
            em.getTransaction().rollback();
            Assert.assertEquals(0, connectionProvider.getAcquiredConnections().size());
            Assert.assertFalse(em.getTransaction().isActive());
        }
    }
}

