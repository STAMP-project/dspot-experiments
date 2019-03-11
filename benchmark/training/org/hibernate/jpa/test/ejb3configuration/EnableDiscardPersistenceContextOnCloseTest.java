/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.ejb3configuration;


import DialectChecks.SupportsJdbcDriverProxying;
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
public class EnableDiscardPersistenceContextOnCloseTest extends BaseEntityManagerFunctionalTestCase {
    private PreparedStatementSpyConnectionProvider connectionProvider = new PreparedStatementSpyConnectionProvider(false, false);

    @Test
    public void testDiscardOnClose() {
        EntityManager em = entityManagerFactory().createEntityManager();
        Wallet wallet = new Wallet();
        wallet.setSerial("123");
        try {
            em.getTransaction().begin();
            em.persist(wallet);
            Assert.assertEquals(1, connectionProvider.getAcquiredConnections().size());
            em.close();
            Assert.assertEquals(0, connectionProvider.getAcquiredConnections().size());
            Assert.assertTrue(em.getTransaction().isActive());
        } finally {
            try {
                em.getTransaction().rollback();
                Assert.fail("Should throw IllegalStateException because the Connection is already closed!");
            } catch (IllegalStateException expected) {
            }
        }
    }
}

