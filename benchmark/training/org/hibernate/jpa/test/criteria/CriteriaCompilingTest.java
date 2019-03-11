/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria;


import java.util.Arrays;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.jpa.test.metamodel.Customer;
import org.hibernate.jpa.test.metamodel.Order;
import org.hibernate.query.Query;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class CriteriaCompilingTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testTrim() {
        final String expectedResult = "David R. Vincent";
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<String> cquery = cb.createQuery(.class);
            Root<Customer> cust = cquery.from(.class);
            // Get Metamodel from Root
            EntityType<Customer> Customer_ = cust.getModel();
            cquery.where(cb.equal(cust.get(Customer_.getSingularAttribute("name", .class)), cb.literal(" David R. Vincent ")));
            cquery.select(cb.trim(CriteriaBuilder.Trimspec.BOTH, cust.get(Customer_.getSingularAttribute("name", .class))));
            TypedQuery<String> tq = entityManager.createQuery(cquery);
            String result = tq.getSingleResult();
            Assert.assertEquals("Mismatch in received results", expectedResult, result);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11393")
    public void testTrimAChar() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<Customer> query = criteriaBuilder.createQuery(.class);
            final Root<Customer> from = query.from(.class);
            query.select(from);
            query.where(criteriaBuilder.equal(criteriaBuilder.trim(CriteriaBuilder.Trimspec.LEADING, criteriaBuilder.literal('R'), from.get("name")), " Vincent"));
            List<Customer> resultList = entityManager.createQuery(query).getResultList();
            assertThat(resultList.size(), is(1));
        });
    }

    @Test
    public void testJustSimpleRootCriteria() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // First w/o explicit selection...
            CriteriaQuery<Customer> criteria = entityManager.getCriteriaBuilder().createQuery(.class);
            criteria.from(.class);
            entityManager.createQuery(criteria).getResultList();
            // Now with...
            criteria = entityManager.getCriteriaBuilder().createQuery(.class);
            Root<Customer> root = criteria.from(.class);
            criteria.select(root);
            entityManager.createQuery(criteria).getResultList();
        });
    }

    @Test
    public void testSimpleJoinCriteria() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // String based...
            CriteriaQuery<Order> criteria = entityManager.getCriteriaBuilder().createQuery(.class);
            Root<Order> root = criteria.from(.class);
            root.join("lineItems");
            criteria.select(root);
            entityManager.createQuery(criteria).getResultList();
        });
    }

    @Test
    public void testSimpleFetchCriteria() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // String based...
            CriteriaQuery<Order> criteria = entityManager.getCriteriaBuilder().createQuery(.class);
            Root<Order> root = criteria.from(.class);
            root.fetch("lineItems");
            criteria.select(root);
            entityManager.createQuery(criteria).getResultList();
        });
    }

    @Test
    public void testSerialization() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaQuery<Order> criteria = entityManager.getCriteriaBuilder().createQuery(.class);
            Root<Order> root = criteria.from(.class);
            root.fetch("lineItems");
            criteria.select(root);
            criteria = serializeDeserialize(criteria);
            entityManager.createQuery(criteria).getResultList();
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10960")
    public void testDeprecation() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Session session = entityManager.unwrap(.class);
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Order> query = builder.createQuery(.class);
            Root<Order> from = query.from(.class);
            query.orderBy(builder.desc(from.get("totalPrice")));
            TypedQuery<Order> jpaQuery = session.createQuery(query);
            Query<?> hibQuery = jpaQuery.unwrap(.class);
            ScrollableResults sr = hibQuery.scroll(ScrollMode.FORWARD_ONLY);
            hibQuery.setCacheMode(CacheMode.IGNORE).scroll(ScrollMode.FORWARD_ONLY);
            Query<Order> anotherQuery = session.createQuery("select o from Order o where totalPrice in :totalPrices", .class);
            anotherQuery.setParameterList("totalPrices", Arrays.asList(12.5, 14.6));
        });
    }
}

