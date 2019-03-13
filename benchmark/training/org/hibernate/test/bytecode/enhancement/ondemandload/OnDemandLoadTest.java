/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.ondemandload;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Luis Barreiro
 */
@TestForIssue(jiraKey = "HHH-10055")
@RunWith(BytecodeEnhancerRunner.class)
public class OnDemandLoadTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testClosedSession() {
        sessionFactory().getStatistics().clear();
        OnDemandLoadTest.Store[] store = new OnDemandLoadTest.Store[1];
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            // first load the store, making sure it is not initialized
            store[0] = s.load(.class, 1L);
            assertNotNull(store[0]);
            assertFalse(isPropertyInitialized(store[0], "inventories"));
            assertEquals(1, sessionFactory().getStatistics().getSessionOpenCount());
            assertEquals(0, sessionFactory().getStatistics().getSessionCloseCount());
        });
        Assert.assertEquals(1, sessionFactory().getStatistics().getSessionOpenCount());
        Assert.assertEquals(1, sessionFactory().getStatistics().getSessionCloseCount());
        store[0].getInventories();
        Assert.assertTrue(Hibernate.isPropertyInitialized(store[0], "inventories"));
        Assert.assertEquals(2, sessionFactory().getStatistics().getSessionOpenCount());
        Assert.assertEquals(2, sessionFactory().getStatistics().getSessionCloseCount());
    }

    @Test
    public void testClearedSession() {
        sessionFactory().getStatistics().clear();
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            // first load the store, making sure collection is not initialized
            org.hibernate.test.bytecode.enhancement.ondemandload.Store store = s.get(.class, 1L);
            assertNotNull(store);
            assertFalse(isPropertyInitialized(store, "inventories"));
            assertEquals(1, sessionFactory().getStatistics().getSessionOpenCount());
            assertEquals(0, sessionFactory().getStatistics().getSessionCloseCount());
            // then clear session and try to initialize collection
            s.clear();
            assertNotNull(store);
            assertFalse(isPropertyInitialized(store, "inventories"));
            store.getInventories().size();
            assertTrue(isPropertyInitialized(store, "inventories"));
            // the extra Session is the temp Session needed to perform the init
            assertEquals(2, sessionFactory().getStatistics().getSessionOpenCount());
            assertEquals(1, sessionFactory().getStatistics().getSessionCloseCount());
            // clear Session again.  The collection should still be recognized as initialized from above
            s.clear();
            assertNotNull(store);
            assertTrue(isPropertyInitialized(store, "inventories"));
            assertEquals(2, sessionFactory().getStatistics().getSessionOpenCount());
            assertEquals(1, sessionFactory().getStatistics().getSessionCloseCount());
            // lets clear the Session again and this time reload the Store
            s.clear();
            store = s.get(.class, 1L);
            s.clear();
            assertNotNull(store);
            // collection should be back to uninitialized since we have a new entity instance
            assertFalse(isPropertyInitialized(store, "inventories"));
            assertEquals(2, sessionFactory().getStatistics().getSessionOpenCount());
            assertEquals(1, sessionFactory().getStatistics().getSessionCloseCount());
            store.getInventories().size();
            assertTrue(isPropertyInitialized(store, "inventories"));
            // the extra Session is the temp Session needed to perform the init
            assertEquals(3, sessionFactory().getStatistics().getSessionOpenCount());
            assertEquals(2, sessionFactory().getStatistics().getSessionCloseCount());
            // clear Session again.  The collection should still be recognized as initialized from above
            s.clear();
            assertNotNull(store);
            assertTrue(isPropertyInitialized(store, "inventories"));
            assertEquals(3, sessionFactory().getStatistics().getSessionOpenCount());
            assertEquals(2, sessionFactory().getStatistics().getSessionCloseCount());
        });
    }

    // --- //
    @Entity
    @Table(name = "STORE")
    private static class Store {
        @Id
        Long id;

        String name;

        @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        List<OnDemandLoadTest.Inventory> inventories = new ArrayList<>();

        @Version
        Integer version;

        Store() {
        }

        Store(long id) {
            this.id = id;
        }

        OnDemandLoadTest.Store setName(String name) {
            this.name = name;
            return this;
        }

        OnDemandLoadTest.Inventory addInventoryProduct(OnDemandLoadTest.Product product) {
            OnDemandLoadTest.Inventory inventory = new OnDemandLoadTest.Inventory(this, product);
            inventories.add(inventory);
            return inventory;
        }

        public List<OnDemandLoadTest.Inventory> getInventories() {
            return Collections.unmodifiableList(inventories);
        }
    }

    @Entity
    @Table(name = "INVENTORY")
    private static class Inventory {
        @Id
        @GeneratedValue
        @GenericGenerator(name = "increment", strategy = "increment")
        Long id = -1L;

        @ManyToOne
        @JoinColumn(name = "STORE_ID")
        OnDemandLoadTest.Store store;

        @ManyToOne
        @JoinColumn(name = "PRODUCT_ID")
        OnDemandLoadTest.Product product;

        Long quantity;

        BigDecimal storePrice;

        public Inventory() {
        }

        public Inventory(OnDemandLoadTest.Store store, OnDemandLoadTest.Product product) {
            this.store = store;
            this.product = product;
        }

        OnDemandLoadTest.Inventory setStore(OnDemandLoadTest.Store store) {
            this.store = store;
            return this;
        }

        OnDemandLoadTest.Inventory setProduct(OnDemandLoadTest.Product product) {
            this.product = product;
            return this;
        }

        OnDemandLoadTest.Inventory setQuantity(Long quantity) {
            this.quantity = quantity;
            return this;
        }

        OnDemandLoadTest.Inventory setStorePrice(BigDecimal storePrice) {
            this.storePrice = storePrice;
            return this;
        }
    }

    @Entity
    @Table(name = "PRODUCT")
    private static class Product {
        @Id
        String id;

        String name;

        String description;

        BigDecimal msrp;

        @Version
        Long version;

        Product() {
        }

        Product(String id) {
            this.id = id;
        }

        OnDemandLoadTest.Product setName(String name) {
            this.name = name;
            return this;
        }

        OnDemandLoadTest.Product setDescription(String description) {
            this.description = description;
            return this;
        }

        OnDemandLoadTest.Product setMsrp(BigDecimal msrp) {
            this.msrp = msrp;
            return this;
        }
    }
}

