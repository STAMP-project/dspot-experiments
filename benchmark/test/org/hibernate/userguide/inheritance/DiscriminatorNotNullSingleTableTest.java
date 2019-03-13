/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.inheritance;


import java.math.BigDecimal;
import java.sql.Statement;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::entity-inheritance-single-table-discriminator-value-example[]
@RequiresDialect(H2Dialect.class)
public class DiscriminatorNotNullSingleTableTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.unwrap(.class).doWork(( connection) -> {
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("ALTER TABLE Account ALTER COLUMN DTYPE SET NULL");
                }
            });
            // tag::entity-inheritance-single-table-discriminator-value-persist-example[]
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
            org.hibernate.userguide.inheritance.Account account = new org.hibernate.userguide.inheritance.Account();
            account.setId(3L);
            account.setOwner("John Doe");
            account.setBalance(BigDecimal.valueOf(1000));
            account.setInterestRate(BigDecimal.valueOf(1.9));
            entityManager.persist(debitAccount);
            entityManager.persist(creditAccount);
            entityManager.persist(account);
            entityManager.unwrap(.class).doWork(( connection) -> {
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(("insert into Account (DTYPE, active, balance, interestRate, owner, id) " + "values ('Other', true, 25, 0.5, 'Vlad', 4)"));
                }
            });
            // end::entity-inheritance-single-table-discriminator-value-persist-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::entity-inheritance-single-table-discriminator-value-persist-example[]
            Map<Long, org.hibernate.userguide.inheritance.Account> accounts = entityManager.createQuery("select a from Account a", .class).getResultList().stream().collect(Collectors.toMap(org.hibernate.userguide.inheritance.Account::getId, Function.identity()));
            assertEquals(4, accounts.size());
            assertEquals(.class, accounts.get(1L).getClass());
            assertEquals(.class, accounts.get(2L).getClass());
            assertEquals(.class, accounts.get(3L).getClass());
            assertEquals(.class, accounts.get(4L).getClass());
            // end::entity-inheritance-single-table-discriminator-value-persist-example[]
        });
    }

    // tag::entity-inheritance-single-table-discriminator-value-example[]
    // tag::entity-inheritance-single-table-discriminator-value-example[]
    @Entity(name = "Account")
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    @DiscriminatorValue("null")
    public static class Account {
        @Id
        private Long id;

        private String owner;

        private BigDecimal balance;

        private BigDecimal interestRate;

        // Getters and setters are omitted for brevity
        // end::entity-inheritance-single-table-discriminator-value-example[]
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

    // tag::entity-inheritance-single-table-discriminator-value-example[]
    @Entity(name = "DebitAccount")
    @DiscriminatorValue("Debit")
    public static class DebitAccount extends DiscriminatorNotNullSingleTableTest.Account {
        private BigDecimal overdraftFee;

        // Getters and setters are omitted for brevity
        // end::entity-inheritance-single-table-discriminator-value-example[]
        public BigDecimal getOverdraftFee() {
            return overdraftFee;
        }

        public void setOverdraftFee(BigDecimal overdraftFee) {
            this.overdraftFee = overdraftFee;
        }
    }

    // tag::entity-inheritance-single-table-discriminator-value-example[]
    @Entity(name = "CreditAccount")
    @DiscriminatorValue("Credit")
    public static class CreditAccount extends DiscriminatorNotNullSingleTableTest.Account {
        private BigDecimal creditLimit;

        // Getters and setters are omitted for brevity
        // end::entity-inheritance-single-table-discriminator-value-example[]
        public BigDecimal getCreditLimit() {
            return creditLimit;
        }

        public void setCreditLimit(BigDecimal creditLimit) {
            this.creditLimit = creditLimit;
        }
    }

    // tag::entity-inheritance-single-table-discriminator-value-example[]
    @Entity(name = "OtherAccount")
    @DiscriminatorValue("not null")
    public static class OtherAccount extends DiscriminatorNotNullSingleTableTest.Account {
        private boolean active;

        // Getters and setters are omitted for brevity
        // end::entity-inheritance-single-table-discriminator-value-example[]
        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
}

