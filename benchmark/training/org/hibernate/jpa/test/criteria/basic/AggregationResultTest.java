/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria.basic;


import Product_.partNumber;
import Product_.price;
import Product_.quantity;
import Product_.rating;
import Product_.someBigDecimal;
import Product_.someBigInteger;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.jpa.test.metamodel.AbstractMetamodelSpecificTest;
import org.hibernate.jpa.test.metamodel.Product;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class AggregationResultTest extends AbstractMetamodelSpecificTest {
    private CriteriaBuilder builder;

    /**
     * Sum of Longs should return a Long
     */
    @Test
    public void testSumOfLongs() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Product> productRoot = criteria.from(Product.class);
        criteria.select(builder.sum(productRoot.get(partNumber)));
        Object sumResult = em.createQuery(criteria).getSingleResult();
        assertReturnType(Long.class, sumResult);
        em.getTransaction().commit();
        em.close();
    }

    /**
     * Sum of Integers should return an Integer; note that this is distinctly different than JPAQL
     */
    @Test
    public void testSumOfIntegers() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaQuery<Integer> criteria = builder.createQuery(Integer.class);
        Root<Product> productRoot = criteria.from(Product.class);
        criteria.select(builder.sum(productRoot.get(quantity)));
        Object sumResult = em.createQuery(criteria).getSingleResult();
        assertReturnType(Integer.class, sumResult);
        em.getTransaction().commit();
        em.close();
    }

    /**
     * Sum of Doubles should return a Double
     */
    @Test
    public void testSumOfDoubles() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaQuery<Double> criteria = builder.createQuery(Double.class);
        Root<Product> productRoot = criteria.from(Product.class);
        criteria.select(builder.sum(productRoot.get(price)));
        Object sumResult = em.createQuery(criteria).getSingleResult();
        assertReturnType(Double.class, sumResult);
        em.getTransaction().commit();
        em.close();
    }

    /**
     * Sum of Floats should return a Float; note that this is distinctly different than JPAQL
     */
    @Test
    public void testSumOfFloats() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaQuery<Float> criteria = builder.createQuery(Float.class);
        Root<Product> productRoot = criteria.from(Product.class);
        criteria.select(builder.sum(productRoot.get(rating)));
        Object sumResult = em.createQuery(criteria).getSingleResult();
        assertReturnType(Float.class, sumResult);
        em.getTransaction().commit();
        em.close();
    }

    /**
     * Sum of BigInteger should return a BigInteger
     */
    @Test
    public void testSumOfBigIntegers() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaQuery<BigInteger> criteria = builder.createQuery(BigInteger.class);
        Root<Product> productRoot = criteria.from(Product.class);
        criteria.select(builder.sum(productRoot.get(someBigInteger)));
        Object sumResult = em.createQuery(criteria).getSingleResult();
        assertReturnType(BigInteger.class, sumResult);
        em.getTransaction().commit();
        em.close();
    }

    /**
     * Sum of BigDecimal should return a BigDecimal
     */
    @Test
    public void testSumOfBigDecimals() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaQuery<BigDecimal> criteria = builder.createQuery(BigDecimal.class);
        Root<Product> productRoot = criteria.from(Product.class);
        criteria.select(builder.sum(productRoot.get(someBigDecimal)));
        Object sumResult = em.createQuery(criteria).getSingleResult();
        assertReturnType(BigDecimal.class, sumResult);
        em.getTransaction().commit();
        em.close();
    }
}

