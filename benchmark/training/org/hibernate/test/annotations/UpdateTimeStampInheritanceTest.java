/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations;


import java.util.Date;
import java.util.HashSet;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-11867")
public class UpdateTimeStampInheritanceTest extends BaseEntityManagerFunctionalTestCase {
    private static final long SLEEP_MILLIS = 25;

    private static final String customerId = "1";

    @Test
    public void updateParentClassProperty() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.annotations.Customer customer = entityManager.find(.class, customerId);
            assertThat(customer.getCreatedAt(), is(not(nullValue())));
            assertThat(customer.getModifiedAt(), is(not(nullValue())));
            assertModifiedAtWasNotUpdated(customer);
            customer.setName("xyz");
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.annotations.Customer customer = entityManager.find(.class, customerId);
            assertThat(customer.getCreatedAt(), is(not(nullValue())));
            assertThat(customer.getModifiedAt(), is(not(nullValue())));
            assertModifiedAtWasUpdated(customer);
        });
    }

    @Test
    public void updateSubClassProperty() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.annotations.Customer customer = entityManager.find(.class, customerId);
            assertThat(customer.getCreatedAt(), is(not(nullValue())));
            assertThat(customer.getModifiedAt(), is(not(nullValue())));
            assertModifiedAtWasNotUpdated(customer);
            customer.setEmail("xyz@");
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.annotations.Customer customer = entityManager.find(.class, customerId);
            assertThat(customer.getCreatedAt(), is(not(nullValue())));
            assertThat(customer.getModifiedAt(), is(not(nullValue())));
            assertModifiedAtWasUpdated(customer);
        });
    }

    @Test
    public void updateParentClassOneToOneAssociation() throws Exception {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.annotations.Customer customer = entityManager.find(.class, customerId);
            assertThat(customer.getCreatedAt(), is(not(nullValue())));
            assertThat(customer.getModifiedAt(), is(not(nullValue())));
            assertModifiedAtWasNotUpdated(customer);
            org.hibernate.test.annotations.Address a = new org.hibernate.test.annotations.Address();
            a.setStreet("Lollard street");
            customer.setWorkAddress(a);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.annotations.Customer customer = entityManager.find(.class, customerId);
            assertThat(customer.getCreatedAt(), is(not(nullValue())));
            assertThat(customer.getModifiedAt(), is(not(nullValue())));
            assertModifiedAtWasUpdated(customer);
        });
    }

    @Test
    public void updateSubClassOnrToOneAssociation() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.annotations.Customer customer = entityManager.find(.class, customerId);
            assertThat(customer.getCreatedAt(), is(not(nullValue())));
            assertThat(customer.getModifiedAt(), is(not(nullValue())));
            assertModifiedAtWasNotUpdated(customer);
            org.hibernate.test.annotations.Address a = new org.hibernate.test.annotations.Address();
            a.setStreet("Lollard Street");
            customer.setHomeAddress(a);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.annotations.Customer customer = entityManager.find(.class, customerId);
            assertThat(customer.getCreatedAt(), is(not(nullValue())));
            assertThat(customer.getModifiedAt(), is(not(nullValue())));
            assertModifiedAtWasUpdated(customer);
        });
    }

    @Test
    public void replaceParentClassElementCollection() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.annotations.Customer customer = entityManager.find(.class, customerId);
            assertThat(customer.getCreatedAt(), is(not(nullValue())));
            assertThat(customer.getModifiedAt(), is(not(nullValue())));
            assertModifiedAtWasNotUpdated(customer);
            Set<String> adresses = new HashSet<>();
            adresses.add("another address");
            customer.setAdresses(adresses);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.annotations.Customer customer = entityManager.find(.class, customerId);
            assertThat(customer.getCreatedAt(), is(not(nullValue())));
            assertThat(customer.getModifiedAt(), is(not(nullValue())));
            assertModifiedAtWasUpdated(customer);
        });
    }

    @Test
    public void replaceSubClassElementCollection() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.annotations.Customer customer = entityManager.find(.class, customerId);
            assertThat(customer.getCreatedAt(), is(not(nullValue())));
            assertThat(customer.getModifiedAt(), is(not(nullValue())));
            assertModifiedAtWasNotUpdated(customer);
            Set<String> books = new HashSet<>();
            customer.setBooks(books);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.annotations.Customer customer = entityManager.find(.class, customerId);
            assertThat(customer.getCreatedAt(), is(not(nullValue())));
            assertThat(customer.getModifiedAt(), is(not(nullValue())));
            assertModifiedAtWasUpdated(customer);
        });
    }

    @Test
    public void updateDetachedEntity() {
        UpdateTimeStampInheritanceTest.Customer customer = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            return entityManager.find(.class, customerId);
        });
        assertModifiedAtWasNotUpdated(customer);
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.unwrap(.class).update(customer);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            assertModifiedAtWasUpdated(entityManager.find(.class, customerId));
        });
    }

    @Entity(name = "person")
    @Inheritance(strategy = InheritanceType.JOINED)
    public abstract static class AbstractPerson {
        @Id
        @Column(name = "id")
        private String id;

        private String name;

        @CreationTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "created_at", updatable = false)
        private Date createdAt;

        @UpdateTimestamp
        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "modified_at")
        private Date modifiedAt;

        @ElementCollection
        private java.util.Set<String> adresses = new HashSet<>();

        @OneToOne(cascade = CascadeType.ALL)
        private UpdateTimeStampInheritanceTest.Address workAddress;

        public void setId(String id) {
            this.id = id;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public Date getModifiedAt() {
            return modifiedAt;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void addAddress(String address) {
            this.adresses.add(address);
        }

        public void setWorkAddress(UpdateTimeStampInheritanceTest.Address workAddress) {
            this.workAddress = workAddress;
        }

        public void setAdresses(java.util.Set<String> adresses) {
            this.adresses = adresses;
        }
    }

    @Entity(name = "Address")
    @Table(name = "address")
    public static class Address {
        @Id
        @GeneratedValue
        private Long id;

        private String street;

        public void setStreet(String street) {
            this.street = street;
        }
    }

    @Entity(name = "Customer")
    @Table(name = "customer")
    public static class Customer extends UpdateTimeStampInheritanceTest.AbstractPerson {
        private String email;

        @ElementCollection
        private java.util.Set<String> books = new HashSet<>();

        @OneToOne(cascade = CascadeType.ALL)
        private UpdateTimeStampInheritanceTest.Address homeAddress;

        public void setEmail(String email) {
            this.email = email;
        }

        public void addBook(String book) {
            this.books.add(book);
        }

        public void setHomeAddress(UpdateTimeStampInheritanceTest.Address homeAddress) {
            this.homeAddress = homeAddress;
        }

        public void setBooks(java.util.Set<String> books) {
            this.books = books;
        }
    }
}

