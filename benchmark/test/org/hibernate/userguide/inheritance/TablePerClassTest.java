/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.inheritance;


import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::entity-inheritance-table-per-class-example[]
public class TablePerClassTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.inheritance.DebitAccount debitAccount = new org.hibernate.userguide.inheritance.DebitAccount();
            debitAccount.setId(1L);
            debitAccount.setOwner("John Doe");
            debitAccount.setBalance(BigDecimal.valueOf(100));
            debitAccount.setInterestRate(BigDecimal.valueOf(1.5));
            debitAccount.setOverdraftFee(BigDecimal.valueOf(25));
            org.hibernate.userguide.inheritance.CreditAccount creditAccount = new org.hibernate.userguide.inheritance.CreditAccount();
            creditAccount.setId(2L);
            creditAccount.setOwner("John Doe");
            creditAccount.setBalance(BigDecimal.valueOf(1000));
            creditAccount.setInterestRate(BigDecimal.valueOf(1.9));
            creditAccount.setCreditLimit(BigDecimal.valueOf(5000));
            entityManager.persist(debitAccount);
            entityManager.persist(creditAccount);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::entity-inheritance-table-per-class-query-example[]
            List<org.hibernate.userguide.inheritance.Account> accounts = entityManager.createQuery("select a from Account a").getResultList();
            // end::entity-inheritance-table-per-class-query-example[]
        });
    }

    // tag::entity-inheritance-table-per-class-example[]
    // tag::entity-inheritance-table-per-class-example[]
    @Entity(name = "Account")
    @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
    public static class Account {
        @Id
        private Long id;

        private String owner;

        private BigDecimal balance;

        private BigDecimal interestRate;

        // Getters and setters are omitted for brevity
        // end::entity-inheritance-table-per-class-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public BigDecimal getBalance() {
            return balance;
        }

        public void setBalance(BigDecimal balance) {
            this.balance = balance;
        }

        public BigDecimal getInterestRate() {
            return interestRate;
        }

        public void setInterestRate(BigDecimal interestRate) {
            this.interestRate = interestRate;
        }
    }

    // tag::entity-inheritance-table-per-class-example[]
    @Entity(name = "DebitAccount")
    public static class DebitAccount extends TablePerClassTest.Account {
        private BigDecimal overdraftFee;

        // Getters and setters are omitted for brevity
        // end::entity-inheritance-table-per-class-example[]
        public BigDecimal getOverdraftFee() {
            return overdraftFee;
        }

        public void setOverdraftFee(BigDecimal overdraftFee) {
            this.overdraftFee = overdraftFee;
        }
    }

    // tag::entity-inheritance-table-per-class-example[]
    @Entity(name = "CreditAccount")
    public static class CreditAccount extends TablePerClassTest.Account {
        private BigDecimal creditLimit;

        // Getters and setters are omitted for brevity
        // end::entity-inheritance-table-per-class-example[]
        public BigDecimal getCreditLimit() {
            return creditLimit;
        }

        public void setCreditLimit(BigDecimal creditLimit) {
            this.creditLimit = creditLimit;
        }
    }
}

