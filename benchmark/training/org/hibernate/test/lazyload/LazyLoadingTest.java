/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.lazyload;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Oleksander Dukhno
 */
public class LazyLoadingTest extends BaseCoreFunctionalTestCase {
    private static final int CHILDREN_SIZE = 3;

    private Long parentID;

    private Long lastChildID;

    @Test
    @TestForIssue(jiraKey = "HHH-7971")
    public void testLazyCollectionLoadingAfterEndTransaction() {
        Session s = openSession();
        s.beginTransaction();
        Parent loadedParent = ((Parent) (s.load(Parent.class, parentID)));
        s.getTransaction().commit();
        s.close();
        Assert.assertFalse(Hibernate.isInitialized(loadedParent.getChildren()));
        int i = 0;
        for (Child child : loadedParent.getChildren()) {
            i++;
            Assert.assertNotNull(child);
        }
        Assert.assertEquals(LazyLoadingTest.CHILDREN_SIZE, i);
        s = openSession();
        s.beginTransaction();
        Child loadedChild = ((Child) (s.load(Child.class, lastChildID)));
        s.getTransaction().commit();
        s.close();
        Parent p = loadedChild.getParent();
        int j = 0;
        for (Child child : p.getChildren()) {
            j++;
            Assert.assertNotNull(child);
        }
        Assert.assertEquals(LazyLoadingTest.CHILDREN_SIZE, j);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11838")
    public void testGetIdOneToOne() {
        Serializable clientId = TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.lazyload.Address address = new org.hibernate.test.lazyload.Address();
            s.save(address);
            org.hibernate.test.lazyload.Client client = new org.hibernate.test.lazyload.Client(address);
            return s.save(client);
        });
        Serializable addressId = TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.lazyload.Client client = s.get(.class, clientId);
            org.hibernate.test.lazyload.Address address = client.getAddress();
            address.getId();
            assertThat(Hibernate.isInitialized(address), is(true));
            address.getStreet();
            assertThat(Hibernate.isInitialized(address), is(true));
            return address.getId();
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.lazyload.Address address = s.get(.class, addressId);
            org.hibernate.test.lazyload.Client client = address.getClient();
            client.getId();
            assertThat(Hibernate.isInitialized(client), is(false));
            client.getName();
            assertThat(Hibernate.isInitialized(client), is(true));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11838")
    public void testGetIdManyToOne() {
        Serializable accountId = TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.lazyload.Address address = new org.hibernate.test.lazyload.Address();
            s.save(address);
            org.hibernate.test.lazyload.Client client = new org.hibernate.test.lazyload.Client(address);
            org.hibernate.test.lazyload.Account account = new org.hibernate.test.lazyload.Account();
            client.addAccount(account);
            s.save(account);
            s.save(client);
            return account.getId();
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.lazyload.Account account = s.load(.class, accountId);
            org.hibernate.test.lazyload.Client client = account.getClient();
            client.getId();
            assertThat(Hibernate.isInitialized(client), is(false));
            client.getName();
            assertThat(Hibernate.isInitialized(client), is(true));
        });
    }

    @Entity(name = "Account")
    public static class Account {
        @Id
        @GeneratedValue
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "id_client")
        private LazyLoadingTest.Client client;

        public LazyLoadingTest.Client getClient() {
            return client;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setClient(LazyLoadingTest.Client client) {
            this.client = client;
        }
    }

    @Entity(name = "Address")
    public static class Address {
        @Id
        @GeneratedValue
        private Long id;

        @Column
        private String street;

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "id_client")
        private LazyLoadingTest.Client client;

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public LazyLoadingTest.Client getClient() {
            return client;
        }

        public void setClient(LazyLoadingTest.Client client) {
            this.client = client;
        }
    }

    @Entity(name = "Client")
    public static class Client {
        @Id
        @GeneratedValue
        private Long id;

        @Column
        private String name;

        @OneToMany(mappedBy = "client")
        private List<LazyLoadingTest.Account> accounts = new ArrayList<>();

        @OneToOne(mappedBy = "client", fetch = FetchType.LAZY)
        private LazyLoadingTest.Address address;

        public Client() {
        }

        public Client(LazyLoadingTest.Address address) {
            this.address = address;
            address.setClient(this);
        }

        public void addAccount(LazyLoadingTest.Account account) {
            accounts.add(account);
            account.setClient(this);
        }

        public String getName() {
            return name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public LazyLoadingTest.Address getAddress() {
            return address;
        }

        public void setAddress(LazyLoadingTest.Address address) {
            this.address = address;
        }
    }
}

