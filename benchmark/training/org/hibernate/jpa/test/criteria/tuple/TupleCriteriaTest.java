/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria.tuple;


import Customer_.age;
import Customer_.name;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CompoundSelection;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import org.hibernate.jpa.test.metamodel.AbstractMetamodelSpecificTest;
import org.hibernate.jpa.test.metamodel.Customer;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class TupleCriteriaTest extends AbstractMetamodelSpecificTest {
    @Test
    public void testArray() {
        EntityManager em = entityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        Customer c1 = new Customer();
        c1.setId("c1");
        c1.setAge(18);
        c1.setName("Bob");
        em.persist(c1);
        em.getTransaction().commit();
        em.close();
        em = entityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> q = cb.createQuery(Object[].class);
        Root<Customer> c = q.from(Customer.class);
        q.select(cb.array(c.get(name), c.get(age)));
        List<Object[]> result = em.createQuery(q).getResultList();
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(c1.getName(), result.get(0)[0]);
        Assert.assertEquals(c1.getAge(), result.get(0)[1]);
        em.getTransaction().commit();
        em.close();
        em = entityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete Customer").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testTuple() {
        EntityManager em = entityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        Customer c1 = new Customer();
        c1.setId("c1");
        c1.setAge(18);
        c1.setName("Bob");
        em.persist(c1);
        em.getTransaction().commit();
        em.close();
        em = entityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteria = builder.createTupleQuery();
        Root<Customer> customerRoot = criteria.from(Customer.class);
        Path<String> namePath = customerRoot.get(name);
        Path<Integer> agePath = customerRoot.get(age);
        agePath.alias("age");
        criteria.multiselect(namePath, agePath);
        List<Tuple> results = em.createQuery(criteria).getResultList();
        Assert.assertEquals(1, results.size());
        Object resultElement = results.get(0);
        Assert.assertTrue("Check  result 'row' as Tuple", Tuple.class.isInstance(resultElement));
        Tuple resultElementTuple = ((Tuple) (resultElement));
        Object[] tupleArray = resultElementTuple.toArray();
        Assert.assertEquals(2, tupleArray.length);
        Assert.assertEquals(tupleArray[0], resultElementTuple.get(0));
        Assert.assertEquals(resultElementTuple.get(namePath), resultElementTuple.get(0));
        Assert.assertEquals(tupleArray[1], resultElementTuple.get(1));
        Assert.assertEquals(resultElementTuple.get(agePath), resultElementTuple.get(1));
        Assert.assertEquals(resultElementTuple.get(agePath), resultElementTuple.get("age"));
        em.getTransaction().commit();
        em.close();
        em = entityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete Customer").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testIllegalArgumentExceptionBuildingTupleWithSameAliases() {
        EntityManager em = entityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteria = builder.createTupleQuery();
        Root<Customer> customerRoot = criteria.from(Customer.class);
        Path<String> namePath = customerRoot.get(name);
        namePath.alias("age");
        Path<Integer> agePath = customerRoot.get(age);
        agePath.alias("age");
        try {
            criteria.multiselect(namePath, agePath);
            Assert.fail("Attempt to define multi-select with same aliases should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testVariousTupleAccessMethods() {
        EntityManager em = entityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        Customer c1 = new Customer();
        c1.setId("c1");
        c1.setAge(18);
        c1.setName("Bob");
        em.persist(c1);
        em.getTransaction().commit();
        em.close();
        em = entityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteria = builder.createTupleQuery();
        Root<Customer> customerRoot = criteria.from(Customer.class);
        Path<String> namePath = customerRoot.get(name);
        namePath.alias("NAME");
        Path<Integer> agePath = customerRoot.get(age);
        agePath.alias("AGE");
        criteria.multiselect(namePath, agePath);
        List<Tuple> results = em.createQuery(criteria).getResultList();
        Tuple tuple = results.get(0);
        Assert.assertNotNull(tuple);
        Assert.assertNotNull(tuple.get("NAME"));
        Assert.assertNotNull(tuple.get("NAME", String.class));
        try {
            tuple.get("NAME", Date.class);
            Assert.fail("Accessing Customer#name tuple as Date should have thrown exception");
        } catch (IllegalArgumentException expected) {
        }
        em.getTransaction().commit();
        em.close();
        em = entityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete Customer").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testIllegalArgumentExceptionBuildingSelectArrayWithSameAliases() {
        EntityManager em = entityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery criteria = builder.createQuery();
        Root<Customer> customerRoot = criteria.from(Customer.class);
        Path<String> namePath = customerRoot.get(name);
        Path<Integer> agePath = customerRoot.get(age);
        try {
            CompoundSelection<Object[]> c = builder.array(namePath.alias("SAME"), agePath.alias("SAME"));
            criteria.select(c);
            Assert.fail("Attempt to define multi-select with same aliases should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testInvalidTupleIndexAccess() {
        EntityManager em = entityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        Customer c1 = new Customer();
        c1.setId("c1");
        c1.setAge(18);
        c1.setName("Bob");
        em.persist(c1);
        em.getTransaction().commit();
        em.close();
        // the actual assertion block
        em = entityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteria = builder.createTupleQuery();
        Root<Customer> customerRoot = criteria.from(Customer.class);
        criteria.multiselect(customerRoot.get(name), customerRoot.get(age));
        List<Tuple> results = em.createQuery(criteria).getResultList();
        Assert.assertEquals(1, results.size());
        Tuple tuple = results.get(0);
        try {
            tuple.get(99);
            Assert.fail("99 is invalid index");
        } catch (IllegalArgumentException expected) {
        }
        try {
            tuple.get(99, String.class);
            Assert.fail("99 is invalid index");
        } catch (IllegalArgumentException expected) {
        }
        tuple.get(0, String.class);
        tuple.get(1, Integer.class);
        try {
            tuple.get(0, Date.class);
            Assert.fail("Date is invalid type");
        } catch (IllegalArgumentException expected) {
        }
        em.getTransaction().commit();
        em.close();
        em = entityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete Customer").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }
}

