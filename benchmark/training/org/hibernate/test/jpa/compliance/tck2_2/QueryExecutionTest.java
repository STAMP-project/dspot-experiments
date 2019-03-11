/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.jpa.compliance.tck2_2;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hamcrest.CoreMatchers;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class QueryExecutionTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testCollectionFetch() {
        inTransaction(( session) -> {
            final List nonDistinctResult = session.createQuery("select c from Customer c join fetch c.orders").list();
            assertThat(nonDistinctResult.size(), CoreMatchers.is(2));
            final List distinctResult = session.createQuery("select distinct c from Customer c join fetch c.orders").list();
            assertThat(distinctResult.size(), CoreMatchers.is(1));
            final List distinctViaTransformerResult = session.createQuery("select c from Customer c join fetch c.orders").setResultTransformer(DistinctRootEntityResultTransformer.INSTANCE).list();
            assertThat(distinctResult.size(), CoreMatchers.is(1));
        });
    }

    @Entity(name = "Customer")
    @Table(name = "customers")
    public static class Customer {
        @Id
        public Integer id;

        public String name;

        @OneToMany(mappedBy = "customer", cascade = { CascadeType.PERSIST, REMOVE })
        public List<QueryExecutionTest.Order> orders = new ArrayList<>();

        public Customer() {
        }

        public Customer(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Entity(name = "Order")
    @Table(name = "orders")
    public static class Order {
        @Id
        public Integer id;

        @JoinColumn
        @ManyToOne
        public QueryExecutionTest.Customer customer;

        public String receivableLocator;

        public Order() {
        }

        public Order(Integer id, QueryExecutionTest.Customer customer, String receivableLocator) {
            this.id = id;
            this.customer = customer;
            this.receivableLocator = receivableLocator;
            customer.orders.add(this);
        }
    }
}

