/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.criteria.many_to_many;


import Criteria.DISTINCT_ROOT_ENTITY;
import JoinType.INNER_JOIN;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Janario Oliveira
 */
public class CriteriaManyToManyTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testJoinTable() {
        Seller[] sellers = persist("join-table");
        Seller seller1 = sellers[0];
        Seller seller2 = sellers[1];
        Session session = openSession();
        Criteria criteria = session.createCriteria(Seller.class, "s");
        criteria.createCriteria("s.soldTo", "c", INNER_JOIN, Restrictions.eq("name", "join-table-customer1"));
        criteria.setResultTransformer(DISTINCT_ROOT_ENTITY);
        @SuppressWarnings("unchecked")
        List<Seller> results = criteria.list();
        Assert.assertTrue(((results.size()) == 1));
        Assert.assertTrue(results.contains(seller1));
        Assert.assertFalse(results.contains(seller2));
        criteria = session.createCriteria(Seller.class, "s");
        criteria.createCriteria("s.soldTo", "c", INNER_JOIN, Restrictions.eq("name", "join-table-customer2"));
        criteria.setResultTransformer(DISTINCT_ROOT_ENTITY);
        @SuppressWarnings("unchecked")
        List<Seller> results2 = criteria.list();
        Assert.assertTrue(((results2.size()) == 2));
        Assert.assertTrue(results2.contains(seller1));
        Assert.assertTrue(results2.contains(seller2));
        session.close();
    }

    @Test
    public void testMappedBy() {
        Set<Customer> customersAll = new LinkedHashSet<Customer>();
        Seller[] sellers = persist("mappedby");
        customersAll.addAll(sellers[0].getSoldTo());
        customersAll.addAll(sellers[1].getSoldTo());
        Customer[] customers = customersAll.toArray(new Customer[customersAll.size()]);
        Customer customer1 = customers[0];
        Customer customer2 = customers[1];
        Customer customer3 = customers[2];
        Session session = openSession();
        Criteria criteria = session.createCriteria(Customer.class, "c");
        criteria.createCriteria("c.boughtFrom", "s", INNER_JOIN, Restrictions.eq("name", "mappedby-seller1"));
        criteria.setResultTransformer(DISTINCT_ROOT_ENTITY);
        @SuppressWarnings("unchecked")
        List<Customer> results = criteria.list();
        Assert.assertTrue(((results.size()) == 2));
        Assert.assertTrue(results.contains(customer1));
        Assert.assertTrue(results.contains(customer2));
        Assert.assertFalse(results.contains(customer3));
        criteria = session.createCriteria(Customer.class, "c");
        criteria.createCriteria("c.boughtFrom", "s", INNER_JOIN, Restrictions.eq("name", "mappedby-seller2"));
        criteria.setResultTransformer(DISTINCT_ROOT_ENTITY);
        @SuppressWarnings("unchecked")
        List<Customer> results2 = criteria.list();
        Assert.assertTrue(((results2.size()) == 2));
        Assert.assertFalse(results2.contains(customer1));
        Assert.assertTrue(results2.contains(customer2));
        Assert.assertTrue(results2.contains(customer3));
        session.close();
    }
}

