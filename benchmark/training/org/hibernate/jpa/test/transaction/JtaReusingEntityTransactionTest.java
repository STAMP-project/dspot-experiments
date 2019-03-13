/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.transaction;


import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hamcrest.core.Is;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class JtaReusingEntityTransactionTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void entityTransactionShouldBeReusableTest() {
        EntityManager em = createEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = em.getTransaction();
            em.persist(new JtaReusingEntityTransactionTest.TestEntity());
            transaction.begin();
            transaction.commit();
            transaction.begin();
            em.persist(new JtaReusingEntityTransactionTest.TestEntity());
            transaction.commit();
        } finally {
            if ((transaction != null) && (transaction.isActive())) {
                transaction.rollback();
            }
            em.close();
        }
        em = createEntityManager();
        try {
            transaction = em.getTransaction();
            transaction.begin();
            List<JtaReusingEntityTransactionTest.TestEntity> results = em.createQuery("from TestEntity").getResultList();
            Assert.assertThat(results.size(), Is.is(2));
            transaction.commit();
        } finally {
            if ((transaction != null) && (transaction.isActive())) {
                transaction.rollback();
            }
            em.close();
        }
    }

    @Entity(name = "TestEntity")
    public static class TestEntity {
        @Id
        @GeneratedValue
        private Long id;
    }
}

