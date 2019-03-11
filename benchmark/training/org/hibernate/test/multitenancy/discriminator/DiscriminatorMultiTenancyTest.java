/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.multitenancy.discriminator;


import java.util.function.Consumer;
import org.hibernate.Session;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.test.multitenancy.schema.Customer;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.env.ConnectionProviderBuilder;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author M?rten Svantesson
 */
@TestForIssue(jiraKey = "HHH-11980")
@RequiresDialectFeature(ConnectionProviderBuilder.class)
public class DiscriminatorMultiTenancyTest extends BaseUnitTestCase {
    private SessionFactoryImplementor sessionFactory;

    private DriverManagerConnectionProviderImpl connectionProvider;

    private final DiscriminatorMultiTenancyTest.TestCurrentTenantIdentifierResolver currentTenantResolver = new DiscriminatorMultiTenancyTest.TestCurrentTenantIdentifierResolver();

    /**
     * This tests for current tenant being used for second level cache, but not selecting connection provider.
     * Discrimination on connection level will for now need to be implemented in the supplied connection provider.
     */
    @Test
    public void testDiscriminator() {
        doInHibernate("jboss", ( session) -> {
            Customer steve = new Customer(1L, "steve");
            session.save(steve);
        });
        sessionFactory.getStatistics().clear();
        // make sure we get the steve back, from cache if same tenant (jboss)
        doInHibernate("jboss", ( session) -> {
            Customer customer = session.load(Customer.class, 1L);
            Assert.assertEquals("steve", customer.getName());
            // also, make sure this came from second level
            Assert.assertEquals(1, sessionFactory.getStatistics().getSecondLevelCacheHitCount());
        });
        sessionFactory.getStatistics().clear();
        // then make sure we get the steve back, from db if other tenant (acme)
        doInHibernate("acme", ( session) -> {
            Customer customer = session.load(Customer.class, 1L);
            Assert.assertEquals("steve", customer.getName());
            // also, make sure this doesn't came from second level
            Assert.assertEquals(0, sessionFactory.getStatistics().getSecondLevelCacheHitCount());
        });
        // make sure the same works from data store too
        sessionFactory.getStatistics().clear();
        sessionFactory.getCache().evictEntityRegions();
        // first jboss
        doInHibernate("jboss", ( session) -> {
            Customer customer = session.load(Customer.class, 1L);
            Assert.assertEquals("steve", customer.getName());
            // also, make sure this doesn't came from second level
            Assert.assertEquals(0, sessionFactory.getStatistics().getSecondLevelCacheHitCount());
        });
        sessionFactory.getStatistics().clear();
        // then, acme
        doInHibernate("acme", ( session) -> {
            Customer customer = session.load(Customer.class, 1L);
            Assert.assertEquals("steve", customer.getName());
            // also, make sure this doesn't came from second level
            Assert.assertEquals(0, sessionFactory.getStatistics().getSecondLevelCacheHitCount());
        });
        doInHibernate("jboss", ( session) -> {
            Customer customer = session.load(Customer.class, 1L);
            session.delete(customer);
        });
    }

    private static class TestCurrentTenantIdentifierResolver implements CurrentTenantIdentifierResolver {
        private String currentTenantIdentifier;

        @Override
        public String resolveCurrentTenantIdentifier() {
            return currentTenantIdentifier;
        }

        @Override
        public boolean validateExistingCurrentSessions() {
            return false;
        }
    }
}

