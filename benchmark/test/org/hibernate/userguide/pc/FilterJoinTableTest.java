/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.pc;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterJoinTable;
import org.hibernate.annotations.ParamDef;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::pc-filter-join-table-example[]
public class FilterJoinTableTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::pc-filter-join-table-persistence-example[]
            org.hibernate.userguide.pc.Client client = new org.hibernate.userguide.pc.Client().setId(1L).setName("John Doe");
            client.addAccount(new org.hibernate.userguide.pc.Account().setId(1L).setType(AccountType.CREDIT).setAmount(5000.0).setRate((1.25 / 100)));
            client.addAccount(new org.hibernate.userguide.pc.Account().setId(2L).setType(AccountType.DEBIT).setAmount(0.0).setRate((1.05 / 100)));
            client.addAccount(new org.hibernate.userguide.pc.Account().setType(AccountType.DEBIT).setId(3L).setAmount(250.0).setRate((1.05 / 100)));
            entityManager.persist(client);
            // end::pc-filter-join-table-persistence-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::pc-no-filter-join-table-collection-query-example[]
            org.hibernate.userguide.pc.Client client = entityManager.find(.class, 1L);
            assertEquals(3, client.getAccounts().size());
            // end::pc-no-filter-join-table-collection-query-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            log.infof("Activate filter [%s]", "firstAccounts");
            // tag::pc-filter-join-table-collection-query-example[]
            org.hibernate.userguide.pc.Client client = entityManager.find(.class, 1L);
            entityManager.unwrap(.class).enableFilter("firstAccounts").setParameter("maxOrderId", 1);
            assertEquals(2, client.getAccounts().size());
            // end::pc-filter-join-table-collection-query-example[]
        });
    }

    public enum AccountType {

        DEBIT,
        CREDIT;}

    // tag::pc-filter-join-table-example[]
    @Entity(name = "Client")
    @FilterDef(name = "firstAccounts", parameters = @ParamDef(name = "maxOrderId", type = "int"))
    @Filter(name = "firstAccounts", condition = "order_id <= :maxOrderId")
    public static class Client {
        @Id
        private Long id;

        private String name;

        @OneToMany(cascade = CascadeType.ALL)
        @OrderColumn(name = "order_id")
        @FilterJoinTable(name = "firstAccounts", condition = "order_id <= :maxOrderId")
        private List<FilterJoinTableTest.Account> accounts = new ArrayList<>();

        // Getters and setters omitted for brevity
        // end::pc-filter-join-table-example[]
        public Long getId() {
            return id;
        }

        public FilterJoinTableTest.Client setId(Long id) {
            this.id = id;
            return this;
        }

        public String getName() {
            return name;
        }

        public FilterJoinTableTest.Client setName(String name) {
            this.name = name;
            return this;
        }

        public List<FilterJoinTableTest.Account> getAccounts() {
            return accounts;
        }

        // tag::pc-filter-join-table-example[]
        public void addAccount(FilterJoinTableTest.Account account) {
            this.accounts.add(account);
        }
    }

    // tag::pc-filter-join-table-example[]
    @Entity(name = "Account")
    public static class Account {
        @Id
        private Long id;

        @Column(name = "account_type")
        @Enumerated(EnumType.STRING)
        private FilterJoinTableTest.AccountType type;

        private Double amount;

        private Double rate;

        // Getters and setters omitted for brevity
        // end::pc-filter-join-table-example[]
        public Long getId() {
            return id;
        }

        public FilterJoinTableTest.Account setId(Long id) {
            this.id = id;
            return this;
        }

        public FilterJoinTableTest.AccountType getType() {
            return type;
        }

        public FilterJoinTableTest.Account setType(FilterJoinTableTest.AccountType type) {
            this.type = type;
            return this;
        }

        public Double getAmount() {
            return amount;
        }

        public FilterJoinTableTest.Account setAmount(Double amount) {
            this.amount = amount;
            return this;
        }

        public Double getRate() {
            return rate;
        }

        public FilterJoinTableTest.Account setRate(Double rate) {
            this.rate = rate;
            return this;
        }
    }
}

