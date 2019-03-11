/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.pc;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SqlFragmentAlias;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::pc-filter-sql-fragment-alias-example[]
@RequiresDialect(H2Dialect.class)
public class FilterSqlFragementAliasTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.pc.Account account1 = new org.hibernate.userguide.pc.Account();
            account1.setId(1L);
            account1.setAmount(5000.0);
            account1.setRate((1.25 / 100));
            account1.setActive(true);
            entityManager.persist(account1);
            org.hibernate.userguide.pc.Account account2 = new org.hibernate.userguide.pc.Account();
            account2.setId(2L);
            account2.setAmount(0.0);
            account2.setRate((1.05 / 100));
            account2.setActive(false);
            entityManager.persist(account2);
            org.hibernate.userguide.pc.Account account3 = new org.hibernate.userguide.pc.Account();
            account3.setId(3L);
            account3.setAmount(250.0);
            account3.setRate((1.05 / 100));
            account3.setActive(true);
            entityManager.persist(account3);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            log.infof("Activate filter [%s]", "activeAccount");
            // tag::pc-filter-sql-fragment-alias-query-example[]
            entityManager.unwrap(.class).enableFilter("activeAccount").setParameter("active", true);
            List<org.hibernate.userguide.pc.Account> accounts = entityManager.createQuery("select a from Account a", .class).getResultList();
            // end::pc-filter-sql-fragment-alias-query-example[]
            assertEquals(2, accounts.size());
        });
    }

    // tag::pc-filter-sql-fragment-alias-example[]
    // tag::pc-filter-sql-fragment-alias-example[]
    @Entity(name = "Account")
    @Table(name = "account")
    @SecondaryTable(name = "account_details")
    @SQLDelete(sql = "UPDATE account_details SET deleted = true WHERE id = ? ")
    @FilterDef(name = "activeAccount", parameters = @ParamDef(name = "active", type = "boolean"))
    @Filter(name = "activeAccount", condition = "{a}.active = :active and {ad}.deleted = false", aliases = { @SqlFragmentAlias(alias = "a", table = "account"), @SqlFragmentAlias(alias = "ad", table = "account_details") })
    public static class Account {
        @Id
        private Long id;

        private Double amount;

        private Double rate;

        private boolean active;

        @Column(table = "account_details")
        private boolean deleted;

        // Getters and setters omitted for brevity
        // end::pc-filter-sql-fragment-alias-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
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

