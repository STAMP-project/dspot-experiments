/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.multitenancy.schema;


import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.Stoppable;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public abstract class AbstractSchemaBasedMultiTenancyTest<T extends MultiTenantConnectionProvider, C extends ConnectionProvider & Stoppable> extends BaseUnitTestCase {
    protected C acmeProvider;

    protected C jbossProvider;

    protected ServiceRegistryImplementor serviceRegistry;

    protected SessionFactoryImplementor sessionFactory;

    @Test
    public void testBasicExpectedBehavior() {
        Customer steve = AbstractSchemaBasedMultiTenancyTest.doInHibernateSessionBuilder(this::jboss, ( session) -> {
            Customer _steve = new Customer(1L, "steve");
            session.save(_steve);
            return _steve;
        });
        AbstractSchemaBasedMultiTenancyTest.doInHibernateSessionBuilder(this::acme, ( session) -> {
            Customer check = session.get(.class, steve.getId());
            Assert.assertNull("tenancy not properly isolated", check);
        });
        AbstractSchemaBasedMultiTenancyTest.doInHibernateSessionBuilder(this::jboss, ( session) -> {
            session.delete(steve);
        });
    }

    @Test
    public void testSameIdentifiers() {
        // create a customer 'steve' in jboss
        Customer steve = AbstractSchemaBasedMultiTenancyTest.doInHibernateSessionBuilder(this::jboss, ( session) -> {
            Customer _steve = new Customer(1L, "steve");
            session.save(_steve);
            return _steve;
        });
        // now, create a customer 'john' in acme
        Customer john = AbstractSchemaBasedMultiTenancyTest.doInHibernateSessionBuilder(this::acme, ( session) -> {
            Customer _john = new Customer(1L, "john");
            session.save(_john);
            return _john;
        });
        sessionFactory.getStatistics().clear();
        // make sure we get the correct people back, from cache
        // first, jboss
        AbstractSchemaBasedMultiTenancyTest.doInHibernateSessionBuilder(this::jboss, ( session) -> {
            Customer customer = ((Customer) (session.load(.class, 1L)));
            Assert.assertEquals("steve", customer.getName());
            // also, make sure this came from second level
            Assert.assertEquals(1, sessionFactory.getStatistics().getSecondLevelCacheHitCount());
        });
        sessionFactory.getStatistics().clear();
        // then, acme
        AbstractSchemaBasedMultiTenancyTest.doInHibernateSessionBuilder(this::acme, ( session) -> {
            Customer customer = ((Customer) (session.load(.class, 1L)));
            Assert.assertEquals("john", customer.getName());
            // also, make sure this came from second level
            Assert.assertEquals(1, sessionFactory.getStatistics().getSecondLevelCacheHitCount());
        });
        // make sure the same works from datastore too
        sessionFactory.getStatistics().clear();
        sessionFactory.getCache().evictEntityRegions();
        // first jboss
        AbstractSchemaBasedMultiTenancyTest.doInHibernateSessionBuilder(this::jboss, ( session) -> {
            Customer customer = ((Customer) (session.load(.class, 1L)));
            Assert.assertEquals("steve", customer.getName());
            // also, make sure this came from second level
            Assert.assertEquals(0, sessionFactory.getStatistics().getSecondLevelCacheHitCount());
        });
        sessionFactory.getStatistics().clear();
        // then, acme
        AbstractSchemaBasedMultiTenancyTest.doInHibernateSessionBuilder(this::acme, ( session) -> {
            Customer customer = ((Customer) (session.load(.class, 1L)));
            Assert.assertEquals("john", customer.getName());
            // also, make sure this came from second level
            Assert.assertEquals(0, sessionFactory.getStatistics().getSecondLevelCacheHitCount());
        });
        AbstractSchemaBasedMultiTenancyTest.doInHibernateSessionBuilder(this::jboss, ( session) -> {
            session.delete(steve);
        });
        AbstractSchemaBasedMultiTenancyTest.doInHibernateSessionBuilder(this::acme, ( session) -> {
            session.delete(john);
        });
    }

    @Test
    public void testTableIdentifiers() {
        Invoice orderJboss = AbstractSchemaBasedMultiTenancyTest.doInHibernateSessionBuilder(this::jboss, ( session) -> {
            Invoice _orderJboss = new Invoice();
            session.save(_orderJboss);
            Assert.assertEquals(Long.valueOf(1), _orderJboss.getId());
            return _orderJboss;
        });
        Invoice orderAcme = AbstractSchemaBasedMultiTenancyTest.doInHibernateSessionBuilder(this::acme, ( session) -> {
            Invoice _orderAcme = new Invoice();
            session.save(_orderAcme);
            Assert.assertEquals(Long.valueOf(1), _orderAcme.getId());
            return _orderAcme;
        });
        AbstractSchemaBasedMultiTenancyTest.doInHibernateSessionBuilder(this::jboss, ( session) -> {
            session.delete(orderJboss);
        });
        AbstractSchemaBasedMultiTenancyTest.doInHibernateSessionBuilder(this::acme, ( session) -> {
            session.delete(orderAcme);
        });
        sessionFactory.getStatistics().clear();
    }
}

