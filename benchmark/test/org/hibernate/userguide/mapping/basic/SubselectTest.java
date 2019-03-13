/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.mapping.basic;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::mapping-Subselect-example[]
public class SubselectTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        // tag::mapping-Subselect-entity-find-example[]
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.basic.Client client = new org.hibernate.userguide.mapping.basic.Client();
            client.setId(1L);
            client.setFirstName("John");
            client.setLastName("Doe");
            entityManager.persist(client);
            org.hibernate.userguide.mapping.basic.Account account = new org.hibernate.userguide.mapping.basic.Account();
            account.setId(1L);
            account.setClient(client);
            account.setDescription("Checking account");
            entityManager.persist(account);
            org.hibernate.userguide.mapping.basic.AccountTransaction transaction = new org.hibernate.userguide.mapping.basic.AccountTransaction();
            transaction.setAccount(account);
            transaction.setDescription("Salary");
            transaction.setCents((100 * 7000));
            entityManager.persist(transaction);
            org.hibernate.userguide.mapping.basic.AccountSummary summary = entityManager.createQuery(("select s " + ("from AccountSummary s " + "where s.id = :id")), .class).setParameter("id", account.getId()).getSingleResult();
            assertEquals("John Doe", summary.getClientName());
            assertEquals((100 * 7000), summary.getBalance());
        });
        // end::mapping-Subselect-entity-find-example[]
        // tag::mapping-Subselect-entity-refresh-example[]
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.mapping.basic.AccountSummary summary = entityManager.find(.class, 1L);
            assertEquals("John Doe", summary.getClientName());
            assertEquals((100 * 7000), summary.getBalance());
            org.hibernate.userguide.mapping.basic.AccountTransaction transaction = new org.hibernate.userguide.mapping.basic.AccountTransaction();
            transaction.setAccount(entityManager.getReference(.class, 1L));
            transaction.setDescription("Shopping");
            transaction.setCents(((-100) * 2200));
            entityManager.persist(transaction);
            entityManager.flush();
            entityManager.refresh(summary);
            assertEquals((100 * 4800), summary.getBalance());
        });
        // end::mapping-Subselect-entity-refresh-example[]
    }

    // tag::mapping-Subselect-example[]
    // tag::mapping-Subselect-example[]
    @Entity(name = "Client")
    @Table(name = "client")
    public static class Client {
        @Id
        private Long id;

        @Column(name = "first_name")
        private String firstName;

        @Column(name = "last_name")
        private String lastName;

        // Getters and setters omitted for brevity
        // end::mapping-Subselect-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }

    // tag::mapping-Subselect-example[]
    @Entity(name = "Account")
    @Table(name = "account")
    public static class Account {
        @Id
        private Long id;

        @ManyToOne
        private SubselectTest.Client client;

        private String description;

        // Getters and setters omitted for brevity
        // end::mapping-Subselect-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public SubselectTest.Client getClient() {
            return client;
        }

        public void setClient(SubselectTest.Client client) {
            this.client = client;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    // tag::mapping-Subselect-example[]
    @Entity(name = "AccountTransaction")
    @Table(name = "account_transaction")
    public static class AccountTransaction {
        @Id
        @GeneratedValue
        private Long id;

        @ManyToOne
        private SubselectTest.Account account;

        private Integer cents;

        private String description;

        // Getters and setters omitted for brevity
        // end::mapping-Subselect-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public SubselectTest.Account getAccount() {
            return account;
        }

        public void setAccount(SubselectTest.Account account) {
            this.account = account;
        }

        public Integer getCents() {
            return cents;
        }

        public void setCents(Integer cents) {
            this.cents = cents;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    // tag::mapping-Subselect-example[]
    @Entity(name = "AccountSummary")
    @Subselect("select " + (((((("	a.id as id, " + "	concat(concat(c.first_name, ' '), c.last_name) as clientName, ") + "	sum(at.cents) as balance ") + "from account a ") + "join client c on c.id = a.client_id ") + "join account_transaction at on a.id = at.account_id ") + "group by a.id, concat(concat(c.first_name, ' '), c.last_name)"))
    @Synchronize({ "client", "account", "account_transaction" })
    public static class AccountSummary {
        @Id
        private Long id;

        private String clientName;

        private int balance;

        // Getters and setters omitted for brevity
        // end::mapping-Subselect-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getClientName() {
            return clientName;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public int getBalance() {
            return balance;
        }

        public void setBalance(int balance) {
            this.balance = balance;
        }
    }
}

