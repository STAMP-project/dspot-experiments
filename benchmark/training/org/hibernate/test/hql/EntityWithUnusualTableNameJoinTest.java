/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.NaturalId;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Jonathan Bregler
 */
public class EntityWithUnusualTableNameJoinTest extends EntityJoinTest {
    @Test
    @TestForIssue(jiraKey = "HHH-11816")
    public void testInnerEntityJoinsWithVariable() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            // this should get financial records which have a lastUpdateBy user set
            List<Object[]> result = session.createQuery(("select r.id, c.name, u.id, u.username " + (("from FinancialRecord r " + "   inner join r.customer c ") + "	inner join User u on r.lastUpdateBy = u.username and u.username=:username"))).setParameter("username", "steve").list();
            assertThat(Integer.valueOf(result.size()), is(Integer.valueOf(1)));
            Object[] steveAndAcme = result.get(0);
            assertThat(steveAndAcme[0], is(Integer.valueOf(1)));
            assertThat(steveAndAcme[1], is("Acme"));
            assertThat(steveAndAcme[3], is("steve"));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11816")
    public void testInnerEntityJoinsWithVariableSingleQuoted() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            // this should get financial records which have a lastUpdateBy user set
            List<Object[]> result = session.createQuery(("select r.id, c.name, a.id, a.accountname, r.lastUpdateBy " + (("from FinancialRecord r " + "   inner join r.customer c ") + "	inner join Account a on a.customer = c and a.accountname!='test:account' and a.accountname=:accountname and r.lastUpdateBy != null"))).setParameter("accountname", "DEBIT").list();
            assertThat(Integer.valueOf(result.size()), is(Integer.valueOf(1)));
            Object[] steveAndAcmeAndDebit = result.get(0);
            assertThat(steveAndAcmeAndDebit[0], is(Integer.valueOf(1)));
            assertThat(steveAndAcmeAndDebit[1], is("Acme"));
            assertThat(steveAndAcmeAndDebit[3], is("DEBIT"));
            assertThat(steveAndAcmeAndDebit[4], is("steve"));
        });
    }

    @Entity(name = "Customer")
    @Table(name = "`my::customer`")
    public static class Customer {
        private Integer id;

        private String name;

        public Customer() {
        }

        public Customer(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        @Id
        public Integer getId() {
            return this.id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Entity(name = "FinancialRecord")
    @Table(name = "`financial?record`")
    public static class FinancialRecord {
        private Integer id;

        private EntityWithUnusualTableNameJoinTest.Customer customer;

        private String lastUpdateBy;

        public FinancialRecord() {
        }

        public FinancialRecord(Integer id, EntityWithUnusualTableNameJoinTest.Customer customer, String lastUpdateBy) {
            this.id = id;
            this.customer = customer;
            this.lastUpdateBy = lastUpdateBy;
        }

        @Id
        public Integer getId() {
            return this.id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @ManyToOne
        @JoinColumn
        public EntityWithUnusualTableNameJoinTest.Customer getCustomer() {
            return this.customer;
        }

        public void setCustomer(EntityWithUnusualTableNameJoinTest.Customer customer) {
            this.customer = customer;
        }

        public String getLastUpdateBy() {
            return this.lastUpdateBy;
        }

        public void setLastUpdateBy(String lastUpdateBy) {
            this.lastUpdateBy = lastUpdateBy;
        }
    }

    @Entity(name = "User")
    @Table(name = "`my::user`")
    public static class User {
        private Integer id;

        private String username;

        private EntityWithUnusualTableNameJoinTest.Customer customer;

        public User() {
        }

        public User(Integer id, String username) {
            this.id = id;
            this.username = username;
        }

        public User(Integer id, String username, EntityWithUnusualTableNameJoinTest.Customer customer) {
            this.id = id;
            this.username = username;
            this.customer = customer;
        }

        @Id
        public Integer getId() {
            return this.id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @NaturalId
        public String getUsername() {
            return this.username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        @ManyToOne(fetch = FetchType.LAZY)
        public EntityWithUnusualTableNameJoinTest.Customer getCustomer() {
            return this.customer;
        }

        public void setCustomer(EntityWithUnusualTableNameJoinTest.Customer customer) {
            this.customer = customer;
        }
    }

    @Entity(name = "Account")
    @Table(name = "`account`")
    public static class Account {
        private Integer id;

        private String accountname;

        private EntityWithUnusualTableNameJoinTest.Customer customer;

        public Account() {
        }

        public Account(Integer id, String accountname) {
            this.id = id;
            this.accountname = accountname;
        }

        public Account(Integer id, String accountname, EntityWithUnusualTableNameJoinTest.Customer customer) {
            this.id = id;
            this.accountname = accountname;
            this.customer = customer;
        }

        @Id
        public Integer getId() {
            return this.id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @NaturalId
        public String getAccountname() {
            return this.accountname;
        }

        public void setAccountname(String accountname) {
            this.accountname = accountname;
        }

        @ManyToOne(fetch = FetchType.LAZY)
        public EntityWithUnusualTableNameJoinTest.Customer getCustomer() {
            return this.customer;
        }

        public void setCustomer(EntityWithUnusualTableNameJoinTest.Customer customer) {
            this.customer = customer;
        }
    }
}

