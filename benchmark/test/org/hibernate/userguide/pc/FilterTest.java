/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.pc;


import java.util.ArrayList;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::pc-filter-Account-example[]
public class FilterTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::pc-filter-persistence-example[]
            org.hibernate.userguide.pc.Client client = new org.hibernate.userguide.pc.Client().setId(1L).setName("John Doe");
            client.addAccount(new org.hibernate.userguide.pc.Account().setId(1L).setType(AccountType.CREDIT).setAmount(5000.0).setRate((1.25 / 100)).setActive(true));
            client.addAccount(new org.hibernate.userguide.pc.Account().setId(2L).setType(AccountType.DEBIT).setAmount(0.0).setRate((1.05 / 100)).setActive(false));
            client.addAccount(new org.hibernate.userguide.pc.Account().setType(AccountType.DEBIT).setId(3L).setAmount(250.0).setRate((1.05 / 100)).setActive(true));
            entityManager.persist(client);
            // end::pc-filter-persistence-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            log.infof("Activate filter [%s]", "activeAccount");
            entityManager.unwrap(.class).enableFilter("activeAccount").setParameter("active", true);
            org.hibernate.userguide.pc.Account account1 = entityManager.find(.class, 1L);
            org.hibernate.userguide.pc.Account account2 = entityManager.find(.class, 2L);
            assertNotNull(account1);
            assertNotNull(account2);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            log.infof("Activate filter [%s]", "activeAccount");
            entityManager.unwrap(.class).enableFilter("activeAccount").setParameter("active", true);
            org.hibernate.userguide.pc.Account account1 = entityManager.createQuery("select a from Account a where a.id = :id", .class).setParameter("id", 1L).getSingleResult();
            assertNotNull(account1);
            try {
                org.hibernate.userguide.pc.Account account2 = entityManager.createQuery("select a from Account a where a.id = :id", .class).setParameter("id", 2L).getSingleResult();
            } catch ( expected) {
            }
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            log.infof("Activate filter [%s]", "activeAccount");
            // tag::pc-filter-entity-example[]
            entityManager.unwrap(.class).enableFilter("activeAccount").setParameter("active", true);
            org.hibernate.userguide.pc.Account account = entityManager.find(.class, 2L);
            assertFalse(account.isActive());
            // end::pc-filter-entity-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::pc-no-filter-entity-query-example[]
            List<org.hibernate.userguide.pc.Account> accounts = entityManager.createQuery("select a from Account a", .class).getResultList();
            assertEquals(3, accounts.size());
            // end::pc-no-filter-entity-query-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            log.infof("Activate filter [%s]", "activeAccount");
            // tag::pc-filter-entity-query-example[]
            entityManager.unwrap(.class).enableFilter("activeAccount").setParameter("active", true);
            List<org.hibernate.userguide.pc.Account> accounts = entityManager.createQuery("select a from Account a", .class).getResultList();
            assertEquals(2, accounts.size());
            // end::pc-filter-entity-query-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::pc-no-filter-collection-query-example[]
            org.hibernate.userguide.pc.Client client = entityManager.find(.class, 1L);
            assertEquals(3, client.getAccounts().size());
            // end::pc-no-filter-collection-query-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            log.infof("Activate filter [%s]", "activeAccount");
            // tag::pc-filter-collection-query-example[]
            entityManager.unwrap(.class).enableFilter("activeAccount").setParameter("active", true);
            org.hibernate.userguide.pc.Client client = entityManager.find(.class, 1L);
            assertEquals(2, client.getAccounts().size());
            // end::pc-filter-collection-query-example[]
        });
    }

    public enum AccountType {

        DEBIT,
        CREDIT;}

    // tag::pc-filter-Client-example[]
    @Entity(name = "Client")
    public static class Client {
        @Id
        private Long id;

        private String name;

        @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
        @Filter(name = "activeAccount", condition = "active_status = :active")
        private java.util.List<FilterTest.Account> accounts = new ArrayList<>();

        // Getters and setters omitted for brevity
        // end::pc-filter-Client-example[]
        public Long getId() {
            return id;
        }

        public FilterTest.Client setId(Long id) {
            this.id = id;
            return this;
        }

        public String getName() {
            return name;
        }

        public FilterTest.Client setName(String name) {
            this.name = name;
            return this;
        }

        public java.util.List<FilterTest.Account> getAccounts() {
            return accounts;
        }

        // tag::pc-filter-Client-example[]
        public void addAccount(FilterTest.Account account) {
            account.setClient(this);
            this.accounts.add(account);
        }
    }

    // end::pc-filter-Client-example[]
    // tag::pc-filter-Account-example[]
    // tag::pc-filter-Account-example[]
    @Entity(name = "Account")
    @FilterDef(name = "activeAccount", parameters = @ParamDef(name = "active", type = "boolean"))
    @Filter(name = "activeAccount", condition = "active_status = :active")
    public static class Account {
        @Id
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        private FilterTest.Client client;

        @Column(name = "account_type")
        @Enumerated(EnumType.STRING)
        private FilterTest.AccountType type;

        private Double amount;

        private Double rate;

        @Column(name = "active_status")
        private boolean active;

        // Getters and setters omitted for brevity
        // end::pc-filter-Account-example[]
        public Long getId() {
            return id;
        }

        public FilterTest.Account setId(Long id) {
            this.id = id;
            return this;
        }

        public FilterTest.Client getClient() {
            return client;
        }

        public FilterTest.Account setClient(FilterTest.Client client) {
            this.client = client;
            return this;
        }

        public FilterTest.AccountType getType() {
            return type;
        }

        public FilterTest.Account setType(FilterTest.AccountType type) {
            this.type = type;
            return this;
        }

        public Double getAmount() {
            return amount;
        }

        public FilterTest.Account setAmount(Double amount) {
            this.amount = amount;
            return this;
        }

        public Double getRate() {
            return rate;
        }

        public FilterTest.Account setRate(Double rate) {
            this.rate = rate;
            return this;
        }

        public boolean isActive() {
            return active;
        }

        public FilterTest.Account setActive(boolean active) {
            this.active = active;
            return this;
        }
    }
}

