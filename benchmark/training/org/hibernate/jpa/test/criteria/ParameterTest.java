/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria;


import MultiTypedBasicAttributesEntity_.id;
import MultiTypedBasicAttributesEntity_.someInts;
import MultiTypedBasicAttributesEntity_.someWrappedIntegers;
import java.util.Arrays;
import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import org.hamcrest.CoreMatchers;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class ParameterTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testPrimitiveArrayParameterBinding() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaQuery<MultiTypedBasicAttributesEntity> criteria = em.getCriteriaBuilder().createQuery(MultiTypedBasicAttributesEntity.class);
        Root<MultiTypedBasicAttributesEntity> rootEntity = criteria.from(MultiTypedBasicAttributesEntity.class);
        Path<int[]> someIntsPath = rootEntity.get(someInts);
        ParameterExpression<int[]> param = em.getCriteriaBuilder().parameter(int[].class, "theInts");
        criteria.where(em.getCriteriaBuilder().equal(someIntsPath, param));
        TypedQuery<MultiTypedBasicAttributesEntity> query = em.createQuery(criteria);
        query.setParameter(param, new int[]{ 1, 1, 1 });
        Assert.assertThat(query.getParameterValue(param.getName()), CoreMatchers.instanceOf(int[].class));
        query.getResultList();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testNonPrimitiveArrayParameterBinding() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaQuery<MultiTypedBasicAttributesEntity> criteria = em.getCriteriaBuilder().createQuery(MultiTypedBasicAttributesEntity.class);
        Root<MultiTypedBasicAttributesEntity> rootEntity = criteria.from(MultiTypedBasicAttributesEntity.class);
        Path<Integer[]> thePath = rootEntity.get(someWrappedIntegers);
        ParameterExpression<Integer[]> param = em.getCriteriaBuilder().parameter(Integer[].class, "theIntegers");
        criteria.where(em.getCriteriaBuilder().equal(thePath, param));
        TypedQuery<MultiTypedBasicAttributesEntity> query = em.createQuery(criteria);
        query.setParameter(param, new Integer[]{ Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1) });
        Assert.assertThat(query.getParameterValue(param.getName()), CoreMatchers.instanceOf(Integer[].class));
        query.getResultList();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testNamedParameterMetadata() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaQuery<MultiTypedBasicAttributesEntity> criteria = em.getCriteriaBuilder().createQuery(MultiTypedBasicAttributesEntity.class);
        Root<MultiTypedBasicAttributesEntity> rootEntity = criteria.from(MultiTypedBasicAttributesEntity.class);
        criteria.where(em.getCriteriaBuilder().equal(rootEntity.get(id), em.getCriteriaBuilder().parameter(Long.class, "id")));
        TypedQuery<MultiTypedBasicAttributesEntity> query = em.createQuery(criteria);
        Parameter parameter = query.getParameter("id");
        Assert.assertEquals("id", parameter.getName());
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testParameterInParameterList() {
        // Yes, this test makes no semantic sense.  But the JPA TCK does it...
        // it causes a problem on Derby, which does not like the form "... where ? in (?,?)"
        // Derby wants one side or the other to be CAST (I assume so it can check typing).
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaQuery<MultiTypedBasicAttributesEntity> criteria = em.getCriteriaBuilder().createQuery(MultiTypedBasicAttributesEntity.class);
        criteria.from(MultiTypedBasicAttributesEntity.class);
        criteria.where(em.getCriteriaBuilder().in(em.getCriteriaBuilder().parameter(Long.class, "p1")).value(em.getCriteriaBuilder().parameter(Long.class, "p2")).value(em.getCriteriaBuilder().parameter(Long.class, "p3")));
        TypedQuery<MultiTypedBasicAttributesEntity> query = em.createQuery(criteria);
        query.setParameter("p1", 1L);
        query.setParameter("p2", 2L);
        query.setParameter("p3", 3L);
        query.getResultList();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10870")
    public void testParameterInParameterList2() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            final CriteriaQuery<MultiTypedBasicAttributesEntity> criteria = criteriaBuilder.createQuery(.class);
            final ParameterExpression<Iterable> parameter = criteriaBuilder.parameter(.class);
            final Root<MultiTypedBasicAttributesEntity> root = criteria.from(.class);
            criteria.select(root).where(root.get("id").in(parameter));
            final TypedQuery<MultiTypedBasicAttributesEntity> query1 = em.createQuery(criteria);
            query1.setParameter(parameter, Arrays.asList(1L, 2L, 3L));
            query1.getResultList();
        });
    }
}

