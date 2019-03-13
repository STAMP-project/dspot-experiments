/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.filter;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.stat.SessionStatistics;
import org.hibernate.stat.Statistics;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-7119")
public class FilterWitSubSelectFetchModeTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testFiltersAreApplied() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.enableFilter("ID").setParameter("id", 3L);
            List result = session.createQuery("from Customer order by id").list();
            assertFalse(result.isEmpty());
            org.hibernate.test.filter.Customer customer = ((org.hibernate.test.filter.Customer) (result.get(0)));
            assertSame(customer.getCustomerId(), 3L);
            assertSame(customer.getOrders().size(), 2);
            SessionStatistics statistics = session.getStatistics();
            assertSame(statistics.getEntityCount(), 9);
            Statistics sfStatistics = session.getSessionFactory().getStatistics();
            assertSame(sfStatistics.getCollectionFetchCount(), 1L);
            assertSame(sfStatistics.getQueries().length, 1);
        });
    }

    @Entity(name = "Customer")
    @FilterDef(defaultCondition = "customerId >= :id", name = "ID", parameters = { @ParamDef(type = "long", name = "id") })
    @Filter(name = "ID")
    public static class Customer {
        @Id
        private Long customerId;

        private String name;

        public Customer() {
        }

        public Customer(Long customerId, String name) {
            this.customerId = customerId;
            this.name = name;
        }

        @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
        @Fetch(FetchMode.SUBSELECT)
        private Set<FilterWitSubSelectFetchModeTest.CustomerOrder> orders = new HashSet<>();

        public Long getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Long customerId) {
            this.customerId = customerId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Set<FilterWitSubSelectFetchModeTest.CustomerOrder> getOrders() {
            return orders;
        }

        public void addOrder(FilterWitSubSelectFetchModeTest.CustomerOrder order) {
            order.setCustomer(this);
            this.orders.add(order);
        }
    }

    @Entity(name = "CustomerOrder")
    public static class CustomerOrder {
        @Id
        @GeneratedValue
        private Long orderId;

        private Long total;

        public CustomerOrder() {
        }

        public CustomerOrder(Long total) {
            this.total = total;
        }

        @ManyToOne
        private FilterWitSubSelectFetchModeTest.Customer customer;

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public FilterWitSubSelectFetchModeTest.Customer getCustomer() {
            return customer;
        }

        public void setCustomer(FilterWitSubSelectFetchModeTest.Customer customer) {
            this.customer = customer;
        }

        public Long getTotal() {
            return total;
        }

        public void setTotal(Long total) {
            this.total = total;
        }
    }
}

