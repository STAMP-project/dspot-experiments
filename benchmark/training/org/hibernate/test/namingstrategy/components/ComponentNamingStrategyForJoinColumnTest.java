/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.namingstrategy.components;


import ImplicitNamingStrategyComponentPathImpl.INSTANCE;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Cai Chun
 */
@TestForIssue(jiraKey = "HHH-11826")
public class ComponentNamingStrategyForJoinColumnTest extends BaseUnitTestCase {
    @Test
    public void testNamingComponentPath() {
        final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
        try {
            final MetadataSources ms = addAnnotatedClass(ComponentNamingStrategyForJoinColumnTest.BankAccount.class).addAnnotatedClass(ComponentNamingStrategyForJoinColumnTest.WebUser.class);
            final Metadata metadata = ms.getMetadataBuilder().applyImplicitNamingStrategy(INSTANCE).build();
            checkDefaultJoinTableAndAllColumnNames(metadata, ComponentNamingStrategyForJoinColumnTest.Employee.class, "bankAccounts.accounts", "ComponentNamingStrategyForJoinColumnTest$Employee_bankAccounts_accounts", "ComponentNamingStrategyForJoinColumnTest$Employee_id", new String[]{ "ComponentNamingStrategyForJoinColumnTest$Employee_id", "bankAccounts_accounts_accountNumber", "bankAccounts_accounts_bankName", "bankAccounts_accounts_verificationUser_id" });
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }

    @Entity
    public static class Employee {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        private String name;

        @Embedded
        private ComponentNamingStrategyForJoinColumnTest.BankAccounts bankAccounts = new ComponentNamingStrategyForJoinColumnTest.BankAccounts();

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

        public ComponentNamingStrategyForJoinColumnTest.BankAccounts getBankAccounts() {
            return bankAccounts;
        }

        public void setBankAccounts(ComponentNamingStrategyForJoinColumnTest.BankAccounts bankAccounts) {
            this.bankAccounts = bankAccounts;
        }
    }

    @Embeddable
    public static class BankAccounts {
        @ElementCollection(fetch = FetchType.LAZY)
        private List<ComponentNamingStrategyForJoinColumnTest.BankAccount> accounts = new ArrayList<>();

        public List<ComponentNamingStrategyForJoinColumnTest.BankAccount> getAccounts() {
            return this.accounts;
        }
    }

    @Embeddable
    public static class BankAccount {
        private String bankName;

        private String accountNumber;

        @ManyToOne(fetch = FetchType.LAZY)
        private ComponentNamingStrategyForJoinColumnTest.WebUser verificationUser;

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public ComponentNamingStrategyForJoinColumnTest.WebUser getVerificationUser() {
            return verificationUser;
        }

        public void setVerificationUser(ComponentNamingStrategyForJoinColumnTest.WebUser verificationUser) {
            this.verificationUser = verificationUser;
        }
    }

    @Entity
    public static class WebUser {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        private String name;

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
    }
}

