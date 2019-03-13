/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.ondemandload.cache;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Janario Oliveira
 */
public class CacheLazyLoadNoTransTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void hibernateInitialize() {
        CacheLazyLoadNoTransTest.Customer customer = new CacheLazyLoadNoTransTest.Customer();
        CacheLazyLoadNoTransTest.Item item1 = new CacheLazyLoadNoTransTest.Item(customer);
        CacheLazyLoadNoTransTest.Item item2 = new CacheLazyLoadNoTransTest.Item(customer);
        customer.boughtItems.add(item1);
        customer.boughtItems.add(item2);
        persist(customer);
        customer = find(CacheLazyLoadNoTransTest.Customer.class, customer.id);
        Assert.assertFalse(Hibernate.isInitialized(customer.boughtItems));
        Hibernate.initialize(customer.boughtItems);
        Assert.assertTrue(Hibernate.isInitialized(customer.boughtItems));
    }

    @Test
    public void testOneToMany() {
        CacheLazyLoadNoTransTest.Customer customer = new CacheLazyLoadNoTransTest.Customer();
        CacheLazyLoadNoTransTest.Item item1 = new CacheLazyLoadNoTransTest.Item(customer);
        CacheLazyLoadNoTransTest.Item item2 = new CacheLazyLoadNoTransTest.Item(customer);
        customer.boughtItems.add(item1);
        customer.boughtItems.add(item2);
        persist(customer);
        // init cache
        Assert.assertFalse(isCached(customer.id, CacheLazyLoadNoTransTest.Customer.class, "boughtItems"));
        customer = find(CacheLazyLoadNoTransTest.Customer.class, customer.id);
        Assert.assertEquals(2, customer.boughtItems.size());
        // read from cache
        Assert.assertTrue(isCached(customer.id, CacheLazyLoadNoTransTest.Customer.class, "boughtItems"));
        customer = find(CacheLazyLoadNoTransTest.Customer.class, customer.id);
        Assert.assertEquals(2, customer.boughtItems.size());
    }

    @Test
    public void testManyToMany() {
        CacheLazyLoadNoTransTest.Application application = new CacheLazyLoadNoTransTest.Application();
        persist(application);
        CacheLazyLoadNoTransTest.Customer customer = new CacheLazyLoadNoTransTest.Customer();
        customer.applications.add(application);
        application.customers.add(customer);
        persist(customer);
        // init cache
        Assert.assertFalse(isCached(customer.id, CacheLazyLoadNoTransTest.Customer.class, "applications"));
        Assert.assertFalse(isCached(application.id, CacheLazyLoadNoTransTest.Application.class, "customers"));
        customer = find(CacheLazyLoadNoTransTest.Customer.class, customer.id);
        Assert.assertEquals(1, customer.applications.size());
        application = find(CacheLazyLoadNoTransTest.Application.class, application.id);
        Assert.assertEquals(1, application.customers.size());
        Assert.assertTrue(isCached(customer.id, CacheLazyLoadNoTransTest.Customer.class, "applications"));
        Assert.assertTrue(isCached(application.id, CacheLazyLoadNoTransTest.Application.class, "customers"));
        // read from cache
        customer = find(CacheLazyLoadNoTransTest.Customer.class, customer.id);
        Assert.assertEquals(1, customer.applications.size());
        application = find(CacheLazyLoadNoTransTest.Application.class, application.id);
        Assert.assertEquals(1, application.customers.size());
    }

    @Entity
    @Table(name = "application")
    @Cacheable
    public static class Application {
        @Id
        @GeneratedValue
        private Integer id;

        @ManyToMany(mappedBy = "applications")
        @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
        private List<CacheLazyLoadNoTransTest.Customer> customers = new ArrayList<>();
    }

    @Entity
    @Table(name = "customer")
    @Cacheable
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public static class Customer {
        @Id
        @GeneratedValue
        private Integer id;

        @ManyToMany
        @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
        private List<CacheLazyLoadNoTransTest.Application> applications = new ArrayList<>();

        @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
        @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
        private List<CacheLazyLoadNoTransTest.Item> boughtItems = new ArrayList<>();
    }

    @Entity
    @Table(name = "item")
    @Cacheable
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public static class Item {
        @Id
        @GeneratedValue
        private Integer id;

        @ManyToOne
        @JoinColumn(name = "customer_id")
        private CacheLazyLoadNoTransTest.Customer customer;

        protected Item() {
        }

        public Item(CacheLazyLoadNoTransTest.Customer customer) {
            this.customer = customer;
        }
    }
}

