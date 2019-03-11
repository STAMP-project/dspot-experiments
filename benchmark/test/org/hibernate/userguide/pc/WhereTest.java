/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.pc;


import java.util.ArrayList;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.Where;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::pc-where-example[]
public class WhereTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        // tag::pc-where-persistence-example[]
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.pc.Client client = new org.hibernate.userguide.pc.Client();
            client.setId(1L);
            client.setName("John Doe");
            entityManager.persist(client);
            org.hibernate.userguide.pc.Account account1 = new org.hibernate.userguide.pc.Account();
            account1.setId(1L);
            account1.setType(AccountType.CREDIT);
            account1.setAmount(5000.0);
            account1.setRate((1.25 / 100));
            account1.setActive(true);
            account1.setClient(client);
            client.getCreditAccounts().add(account1);
            entityManager.persist(account1);
            org.hibernate.userguide.pc.Account account2 = new org.hibernate.userguide.pc.Account();
            account2.setId(2L);
            account2.setType(AccountType.DEBIT);
            account2.setAmount(0.0);
            account2.setRate((1.05 / 100));
            account2.setActive(false);
            account2.setClient(client);
            client.getDebitAccounts().add(account2);
            entityManager.persist(account2);
            org.hibernate.userguide.pc.Account account3 = new org.hibernate.userguide.pc.Account();
            account3.setType(AccountType.DEBIT);
            account3.setId(3L);
            account3.setAmount(250.0);
            account3.setRate((1.05 / 100));
            account3.setActive(true);
            account3.setClient(client);
            client.getDebitAccounts().add(account3);
            entityManager.persist(account3);
        });
        // end::pc-where-persistence-example[]
        // tag::pc-where-entity-query-example[]
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<org.hibernate.userguide.pc.Account> accounts = entityManager.createQuery("select a from Account a", .class).getResultList();
            assertEquals(2, accounts.size());
        });
        // end::pc-where-entity-query-example[]
        // tag::pc-where-collection-query-example[]
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.pc.Client client = entityManager.find(.class, 1L);
            assertEquals(1, client.getCreditAccounts().size());
            assertEquals(1, client.getDebitAccounts().size());
        });
        // end::pc-where-collection-query-example[]
    }

    // tag::pc-where-example[]
    public enum AccountType {

        DEBIT,
        CREDIT;}

    // tag::pc-where-example[]
    @Entity(name = "Client")
    public static class Client {
        @Id
        private Long id;

        private String name;

        @Where(clause = "account_type = 'DEBIT'")
        @OneToMany(mappedBy = "client")
        private java.util.List<WhereTest.Account> debitAccounts = new ArrayList<>();

        @Where(clause = "account_type = 'CREDIT'")
        @OneToMany(mappedBy = "client")
        private java.util.List<WhereTest.Account> creditAccounts = new ArrayList<>();

        // Getters and setters omitted for brevity
        // end::pc-where-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public java.util.List<WhereTest.Account> getDebitAccounts() {
            return debitAccounts;
        }

        public java.util.List<WhereTest.Account> getCreditAccounts() {
            return creditAccounts;
        }
    }

    // tag::pc-where-example[]
    @Entity(name = "Account")
    @Where(clause = "active = true")
    public static class Account {
        @Id
        private Long id;

        @ManyToOne
        private WhereTest.Client client;

        @Column(name = "account_type")
        @Enumerated(EnumType.STRING)
        private WhereTest.AccountType type;

        private Double amount;

        private Double rate;

        private boolean active;

        // Getters and setters omitted for brevity
        // end::pc-where-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public WhereTest.Client getClient() {
            return client;
        }

        public void setClient(WhereTest.Client client) {
            this.client = client;
        }

        public WhereTest.AccountType getType() {
            return type;
        }

        public void setType(WhereTest.AccountType type) {
            this.type = type;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public Double getRate() {
            return rate;
        }

        public void setRate(Double rate) {
            this.rate = rate;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
}

