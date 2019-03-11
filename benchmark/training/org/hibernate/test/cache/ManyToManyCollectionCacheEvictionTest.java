package org.hibernate.test.cache;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Janario Oliveira
 */
public class ManyToManyCollectionCacheEvictionTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testManyToManyPersist() {
        // if an error happen, it will propagate the exception failing the test case
        Session s = openSession();
        s.beginTransaction();
        ManyToManyCollectionCacheEvictionTest.Application application = new ManyToManyCollectionCacheEvictionTest.Application();
        s.save(application);
        ManyToManyCollectionCacheEvictionTest.Customer customer = new ManyToManyCollectionCacheEvictionTest.Customer();
        customer.applications.add(application);
        s.save(customer);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        Assert.assertEquals(1, s.get(ManyToManyCollectionCacheEvictionTest.Application.class, application.id).customers.size());
        Assert.assertEquals(1, s.get(ManyToManyCollectionCacheEvictionTest.Customer.class, customer.id).applications.size());
        s.close();
        s = openSession();
        s.beginTransaction();
        ManyToManyCollectionCacheEvictionTest.Customer customer2 = new ManyToManyCollectionCacheEvictionTest.Customer();
        customer2.applications.add(application);
        s.save(customer2);
        s.getTransaction().commit();
        s.close();
    }

    @Entity
    @Table(name = "customer")
    @Cacheable
    public static class Customer {
        @Id
        @GeneratedValue
        private Integer id;

        @ManyToMany
        @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
        private List<ManyToManyCollectionCacheEvictionTest.Application> applications = new ArrayList<ManyToManyCollectionCacheEvictionTest.Application>();
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
        private List<ManyToManyCollectionCacheEvictionTest.Customer> customers = new ArrayList<ManyToManyCollectionCacheEvictionTest.Customer>();
    }
}

