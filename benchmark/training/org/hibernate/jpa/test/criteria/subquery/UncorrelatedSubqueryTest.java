/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria.subquery;


import Customer_.age;
import Customer_.orders;
import Order_.totalPrice;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.hibernate.jpa.test.metamodel.AbstractMetamodelSpecificTest;
import org.hibernate.jpa.test.metamodel.Customer;
import org.hibernate.jpa.test.metamodel.Order;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class UncorrelatedSubqueryTest extends AbstractMetamodelSpecificTest {
    @Test
    public void testGetCorrelatedParentIllegalStateException() {
        // test that attempting to call getCorrelatedParent on a uncorrelated query/subquery
        // throws ISE
        CriteriaBuilder builder = entityManagerFactory().getCriteriaBuilder();
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaQuery<Customer> criteria = builder.createQuery(Customer.class);
        Root<Customer> customerRoot = criteria.from(Customer.class);
        Join<Customer, Order> orderJoin = customerRoot.join(orders);
        criteria.select(customerRoot);
        Subquery<Double> subCriteria = criteria.subquery(Double.class);
        Root<Order> subqueryOrderRoot = subCriteria.from(Order.class);
        subCriteria.select(builder.min(subqueryOrderRoot.get(totalPrice)));
        criteria.where(builder.equal(orderJoin.get("totalPrice"), builder.all(subCriteria)));
        Assert.assertFalse(customerRoot.isCorrelated());
        Assert.assertFalse(subqueryOrderRoot.isCorrelated());
        try {
            customerRoot.getCorrelationParent();
            Assert.fail("Should have resulted in IllegalStateException");
        } catch (IllegalStateException expected) {
        }
        try {
            subqueryOrderRoot.getCorrelationParent();
            Assert.fail("Should have resulted in IllegalStateException");
        } catch (IllegalStateException expected) {
        }
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testEqualAll() {
        CriteriaBuilder builder = entityManagerFactory().getCriteriaBuilder();
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaQuery<Customer> criteria = builder.createQuery(Customer.class);
        Root<Customer> customerRoot = criteria.from(Customer.class);
        Join<Customer, Order> orderJoin = customerRoot.join(orders);
        criteria.select(customerRoot);
        Subquery<Double> subCriteria = criteria.subquery(Double.class);
        Root<Order> subqueryOrderRoot = subCriteria.from(Order.class);
        subCriteria.select(builder.min(subqueryOrderRoot.get(totalPrice)));
        criteria.where(builder.equal(orderJoin.get("totalPrice"), builder.all(subCriteria)));
        em.createQuery(criteria).getResultList();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testLessThan() {
        CriteriaBuilder builder = entityManagerFactory().getCriteriaBuilder();
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaQuery<Customer> criteria = builder.createQuery(Customer.class);
        Root<Customer> customerRoot = criteria.from(Customer.class);
        Subquery<Double> subCriteria = criteria.subquery(Double.class);
        Root<Customer> subQueryCustomerRoot = subCriteria.from(Customer.class);
        subCriteria.select(builder.avg(subQueryCustomerRoot.get(age)));
        criteria.where(builder.lessThan(customerRoot.get(age), subCriteria.getSelection().as(Integer.class)));
        em.createQuery(criteria).getResultList();
        em.getTransaction().commit();
        em.close();
    }
}

