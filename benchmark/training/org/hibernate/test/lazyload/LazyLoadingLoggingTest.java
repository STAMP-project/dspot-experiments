/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.lazyload;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import org.hibernate.LazyInitializationException;
import org.hibernate.internal.AbstractSharedSessionContract;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class LazyLoadingLoggingTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12484")
    public void testNoSession() {
        LazyLoadingLoggingTest.Address address = TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            return s.load(.class, 1L);
        });
        try {
            address.getClient().getName();
            Assert.fail("Should throw LazyInitializationException");
        } catch (LazyInitializationException expected) {
            Assert.assertEquals(("could not initialize proxy " + ("[org.hibernate.test.lazyload.LazyLoadingLoggingTest$Address#1] " + "- no Session")), expected.getMessage());
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12484")
    public void testDisconnect() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.lazyload.Address address = session.load(.class, 1L);
            AbstractSharedSessionContract sessionContract = ((AbstractSharedSessionContract) (session));
            sessionContract.getJdbcCoordinator().close();
            try {
                address.getClient().getName();
                fail("Should throw LazyInitializationException");
            } catch ( expected) {
                assertEquals(("could not initialize proxy " + ("[org.hibernate.test.lazyload.LazyLoadingLoggingTest$Address#1] " + "- the owning Session is disconnected")), expected.getMessage());
            }
            session.getTransaction().markRollbackOnly();
        });
    }

    @Entity(name = "Address")
    public static class Address {
        @Id
        private Long id;

        @Column
        private String street;

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "id_client")
        private LazyLoadingLoggingTest.Client client;

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

        public LazyLoadingLoggingTest.Client getClient() {
            return client;
        }

        public void setClient(LazyLoadingLoggingTest.Client client) {
            this.client = client;
        }
    }

    @Entity(name = "Client")
    public static class Client {
        @Id
        private Long id;

        @Column
        private String name;

        @OneToOne(mappedBy = "client", fetch = FetchType.LAZY)
        private LazyLoadingLoggingTest.Address address;

        public Client() {
        }

        public void setName(String name) {
            this.name = name;
        }

        public Client(LazyLoadingLoggingTest.Address address) {
            this.address = address;
            address.setClient(this);
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

        public LazyLoadingLoggingTest.Address getAddress() {
            return address;
        }

        public void setAddress(LazyLoadingLoggingTest.Address address) {
            this.address = address;
        }
    }
}

