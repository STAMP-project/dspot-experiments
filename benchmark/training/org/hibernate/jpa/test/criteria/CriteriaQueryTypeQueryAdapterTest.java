/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria;


import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


public class CriteriaQueryTypeQueryAdapterTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12685")
    public void testCriteriaQueryParameterIsBoundCheckNotFails() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.Item> query = builder.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.Item> root = query.from(.class);
            ParameterExpression<String> parameter = builder.parameter(.class, "name");
            Predicate predicate = builder.equal(root.get("name"), parameter);
            query.where(predicate);
            TypedQuery<org.hibernate.jpa.test.criteria.Item> criteriaQuery = entityManager.createQuery(query);
            Parameter<?> dynamicParameter = criteriaQuery.getParameter("name");
            boolean bound = criteriaQuery.isBound(dynamicParameter);
            assertFalse(bound);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12685")
    public void testCriteriaQueryGetParameters() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.Item> query = builder.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.Item> root = query.from(.class);
            ParameterExpression<String> parameter = builder.parameter(.class, "name");
            Predicate predicate = builder.equal(root.get("name"), parameter);
            query.where(predicate);
            TypedQuery<org.hibernate.jpa.test.criteria.Item> criteriaQuery = entityManager.createQuery(query);
            Set<Parameter<?>> parameters = criteriaQuery.getParameters();
            assertEquals(1, parameters.size());
            Parameter<?> dynamicParameter = parameters.iterator().next();
            assertEquals("name", dynamicParameter.getName());
        });
    }

    @TestForIssue(jiraKey = "HHH-12685")
    @Test(expected = IllegalArgumentException.class)
    public void testCriteriaQueryGetParameterOfWrongType() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.Item> query = builder.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.Item> root = query.from(.class);
            ParameterExpression<String> parameter = builder.parameter(.class, "name");
            Predicate predicate = builder.equal(root.get("name"), parameter);
            query.where(predicate);
            TypedQuery<org.hibernate.jpa.test.criteria.Item> criteriaQuery = entityManager.createQuery(query);
            criteriaQuery.getParameter("name", .class);
        });
    }

    @TestForIssue(jiraKey = "HHH-12685")
    @Test(expected = IllegalArgumentException.class)
    public void testCriteriaQueryGetNonExistingParameter() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.Item> query = builder.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.Item> root = query.from(.class);
            ParameterExpression<String> parameter = builder.parameter(.class, "name");
            Predicate predicate = builder.equal(root.get("name"), parameter);
            query.where(predicate);
            TypedQuery<org.hibernate.jpa.test.criteria.Item> criteriaQuery = entityManager.createQuery(query);
            criteriaQuery.getParameter("placedAt");
        });
    }

    @Test
    public void testSetParameterPassingTypeNotFails() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.Item> query = builder.createQuery(.class);
            Predicate predicate = builder.equal(query.from(.class).get("name"), builder.parameter(.class, "name"));
            query.where(predicate);
            QueryImplementor<?> criteriaQuery = ((QueryImplementor<?>) (entityManager.createQuery(query)));
            criteriaQuery.setParameter("name", "2", StringType.INSTANCE).list();
        });
    }

    @Test
    public void testSetParameterTypeInstantNotFails() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.Bid> query = builder.createQuery(.class);
            Predicate predicate = builder.equal(query.from(.class).get("placedAt"), builder.parameter(.class, "placedAt"));
            query.where(predicate);
            QueryImplementor<?> criteriaQuery = ((QueryImplementor<?>) (entityManager.createQuery(query)));
            criteriaQuery.setParameter("placedAt", Instant.now(), TemporalType.TIMESTAMP).list();
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetParameterOfTypeInstantToAFloatParameterType() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.Bid> query = builder.createQuery(.class);
            Predicate predicate = builder.equal(query.from(.class).get("amount"), builder.parameter(.class, "placedAt"));
            query.where(predicate);
            QueryImplementor<?> criteriaQuery = ((QueryImplementor<?>) (entityManager.createQuery(query)));
            criteriaQuery.setParameter("placedAt", Instant.now(), TemporalType.TIMESTAMP).list();
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetParameterOfTypeDateToAFloatParameterType() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.Bid> query = builder.createQuery(.class);
            Predicate predicate = builder.equal(query.from(.class).get("amount"), builder.parameter(.class, "placedAt"));
            query.where(predicate);
            QueryImplementor<?> criteriaQuery = ((QueryImplementor<?>) (entityManager.createQuery(query)));
            criteriaQuery.setParameter("placedAt", Date.from(Instant.now()), TemporalType.DATE).list();
        });
    }

    @Entity(name = "Bid")
    public static class Bid implements Serializable {
        @Id
        Long id;

        float amount;

        Instant placedAt;

        @Id
        @ManyToOne
        CriteriaQueryTypeQueryAdapterTest.Item item;
    }

    @Entity(name = "Item")
    public static class Item implements Serializable {
        @Id
        Long id;

        String name;

        @OneToMany(mappedBy = "item")
        java.util.Set<CriteriaQueryTypeQueryAdapterTest.Bid> bids = new HashSet<>();
    }
}

