/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.inheritance;


import java.math.BigDecimal;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::entity-inheritance-single-table-discriminator-formula-example[]
public class SingleTableDiscriminatorFormulaTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @RequiresDialect({ PostgreSQL81Dialect.class })
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.inheritance.DebitAccount debitAccount = new org.hibernate.userguide.inheritance.DebitAccount("123-debit");
            debitAccount.setId(1L);
            debitAccount.setOwner("John Doe");
            debitAccount.setBalance(BigDecimal.valueOf(100));
            debitAccount.setInterestRate(BigDecimal.valueOf(1.5));
            debitAccount.setOverdraftFee(BigDecimal.valueOf(25));
            org.hibernate.userguide.inheritance.CreditAccount creditAccount = new org.hibernate.userguide.inheritance.CreditAccount("456-credit");
            creditAccount.setId(2L);
            creditAccount.setOwner("John Doe");
            creditAccount.setBalance(BigDecimal.valueOf(1000));
            creditAccount.setInterestRate(BigDecimal.valueOf(1.9));
            creditAccount.setCreditLimit(BigDecimal.valueOf(5000));
            entityManager.persist(debitAccount);
            entityManager.persist(creditAccount);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<org.hibernate.userguide.inheritance.Account> accounts = entityManager.createQuery("select a from Account a").getResultList();
            Assert.assertEquals(2, accounts.size());
        });
    }

    // tag::entity-inheritance-single-table-discriminator-formula-example[]
    // tag::entity-inheritance-single-table-discriminator-formula-example[]
    @Entity(name = "Account")
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    @DiscriminatorFormula("case when debitKey is not null " + (((((("then 'Debit' " + "else ( ") + "   case when creditKey is not null ") + "   then 'Credit' ") + "   else 'Unknown' ") + "   end ) ") + "end "))
    public static class Account {
        @Id
        private Long id;

        private String owner;

        private BigDecimal balance;

        private BigDecimal interestRate;

        // Getters and setters are omitted for brevity
        // end::entity-inheritance-single-table-discriminator-formula-example[]
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

    // tag::entity-inheritance-single-table-discriminator-formula-example[]
    @Entity(name = "DebitAccount")
    @DiscriminatorValue("Debit")
    public static class DebitAccount extends SingleTableDiscriminatorFormulaTest.Account {
        private String debitKey;

        private BigDecimal overdraftFee;

        // Getters and setters are omitted for brevity
        // end::entity-inheritance-single-table-discriminator-formula-example[]
        private DebitAccount() {
        }

        public DebitAccount(String debitKey) {
            this.debitKey = debitKey;
        }

        public String getDebitKey() {
            return debitKey;
        }

        public BigDecimal getOverdraftFee() {
            return overdraftFee;
        }

        public void setOverdraftFee(BigDecimal overdraftFee) {
            this.overdraftFee = overdraftFee;
        }
    }

    // tag::entity-inheritance-single-table-discriminator-formula-example[]
    @Entity(name = "CreditAccount")
    @DiscriminatorValue("Credit")
    public static class CreditAccount extends SingleTableDiscriminatorFormulaTest.Account {
        private String creditKey;

        private BigDecimal creditLimit;

        // Getters and setters are omitted for brevity
        // end::entity-inheritance-single-table-discriminator-formula-example[]
        private CreditAccount() {
        }

        public CreditAccount(String creditKey) {
            this.creditKey = creditKey;
        }

        public String getCreditKey() {
            return creditKey;
        }

        public BigDecimal getCreditLimit() {
            return creditLimit;
        }

        public void setCreditLimit(BigDecimal creditLimit) {
            this.creditLimit = creditLimit;
        }
    }
}

