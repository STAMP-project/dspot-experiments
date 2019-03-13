/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria.subquery;


import Customer_.name;
import Customer_.orders;
import LineItem_.quantity;
import Order_.customer;
import Order_.lineItems;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.hibernate.dialect.SybaseASE15Dialect;
import org.hibernate.jpa.test.metamodel.AbstractMetamodelSpecificTest;
import org.hibernate.jpa.test.metamodel.Customer;
import org.hibernate.jpa.test.metamodel.LineItem;
import org.hibernate.jpa.test.metamodel.Order;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class CorrelatedSubqueryTest extends AbstractMetamodelSpecificTest {
    @Test
    public void testBasicCorrelation() {
        CriteriaBuilder builder = entityManagerFactory().getCriteriaBuilder();
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaQuery<Customer> criteria = builder.createQuery(Customer.class);
        Root<Customer> customer = criteria.from(Customer.class);
        criteria.select(customer);
        Subquery<Order> orderSubquery = criteria.subquery(Order.class);
        Root<Customer> customerCorrelationRoot = orderSubquery.correlate(customer);
        Join<Customer, Order> customerOrderCorrelationJoin = customerCorrelationRoot.join(orders);
        orderSubquery.select(customerOrderCorrelationJoin);
        criteria.where(builder.not(builder.exists(orderSubquery)));
        em.createQuery(criteria).getResultList();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testRestrictedCorrelation() {
        CriteriaBuilder builder = entityManagerFactory().getCriteriaBuilder();
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
        Root<Order> orderRoot = criteria.from(Order.class);
        criteria.select(orderRoot);
        // create correlated subquery
        Subquery<Customer> customerSubquery = criteria.subquery(Customer.class);
        Root<Order> orderRootCorrelation = customerSubquery.correlate(orderRoot);
        Join<Order, Customer> orderCustomerJoin = orderRootCorrelation.join(customer);
        customerSubquery.where(builder.like(orderCustomerJoin.get(name), "%Caruso")).select(orderCustomerJoin);
        criteria.where(builder.exists(customerSubquery));
        em.createQuery(criteria).getResultList();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @SkipForDialect(value = SybaseASE15Dialect.class, jiraKey = "HHH-3032")
    public void testCorrelationExplicitSelectionCorrelation() {
        CriteriaBuilder builder = entityManagerFactory().getCriteriaBuilder();
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaQuery<Customer> customerCriteria = builder.createQuery(Customer.class);
        Root<Customer> customer = customerCriteria.from(Customer.class);
        Join<Customer, Order> o = customer.join(orders);
        Subquery<Order> sq = customerCriteria.subquery(Order.class);
        Join<Customer, Order> sqo = sq.correlate(o);
        Join<Order, LineItem> sql = sqo.join(lineItems);
        sq.where(builder.gt(sql.get(quantity), 3));
        // use the correlation itself as the subquery selection (initially caused problems wrt aliases)
        sq.select(sqo);
        customerCriteria.select(customer).distinct(true);
        customerCriteria.where(builder.exists(sq));
        em.createQuery(customerCriteria).getResultList();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testRestrictedCorrelationNoExplicitSelection() {
        CriteriaBuilder builder = entityManagerFactory().getCriteriaBuilder();
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
        Root<Order> orderRoot = criteria.from(Order.class);
        criteria.select(orderRoot);
        // create correlated subquery
        Subquery<Customer> customerSubquery = criteria.subquery(Customer.class);
        Root<Order> orderRootCorrelation = customerSubquery.correlate(orderRoot);
        Join<Order, Customer> orderCustomerJoin = orderRootCorrelation.join("customer");
        customerSubquery.where(builder.like(orderCustomerJoin.<String>get("name"), "%Caruso"));
        criteria.where(builder.exists(customerSubquery));
        em.createQuery(criteria).getResultList();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-8556")
    public void testCorrelatedJoinsFromSubquery() {
        CriteriaBuilder builder = entityManagerFactory().getCriteriaBuilder();
        CriteriaQuery<Customer> cquery = builder.createQuery(Customer.class);
        Root<Customer> customer = cquery.from(Customer.class);
        cquery.select(customer);
        Subquery<Order> sq = cquery.subquery(Order.class);
        Join<Customer, Order> sqo = sq.correlate(customer.join(orders));
        sq.select(sqo);
        Set<Join<?, ?>> cJoins = sq.getCorrelatedJoins();
        // ensure the join is returned in #getCorrelatedJoins
        Assert.assertEquals(cJoins.size(), 1);
    }

    @Test
    public void testVariousSubqueryJoinSemantics() {
        // meant to assert semantics of #getJoins versus #getCorrelatedJoins
    }
}

