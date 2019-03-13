/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria.basic;


import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hamcrest.CoreMatchers;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-10843")
public class ConcatTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testSelectCaseWithConcat() throws Exception {
        EntityManager entityManager = getOrCreateEntityManager();
        entityManager.getTransaction().begin();
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
            Root<ConcatTest.TestEntity> testEntity = query.from(ConcatTest.TestEntity.class);
            query.multiselect(cb.selectCase().when(cb.isNotNull(testEntity.get("id")), cb.concat("test", cb.literal("_1"))).otherwise(cb.literal("Empty")), cb.trim(cb.concat(".", cb.literal("Test   "))));
            final List<Object[]> results = entityManager.createQuery(query).getResultList();
            Assert.assertThat(results.size(), CoreMatchers.is(1));
            Assert.assertThat(results.get(0)[0], CoreMatchers.is("test_1"));
            Assert.assertThat(results.get(0)[1], CoreMatchers.is(".Test"));
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }

    @Test
    public void testConcat() throws Exception {
        EntityManager entityManager = getOrCreateEntityManager();
        entityManager.getTransaction().begin();
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery query = cb.createQuery();
            Root<ConcatTest.TestEntity> testEntity = query.from(ConcatTest.TestEntity.class);
            query.select(testEntity).where(cb.equal(testEntity.get("name"), cb.concat("test", cb.literal("_1"))));
            final List results = entityManager.createQuery(query).getResultList();
            entityManager.getTransaction().commit();
            Assert.assertThat(results.size(), CoreMatchers.is(1));
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }

    @Entity(name = "TestEntity")
    @Table(name = "TEST_ENTITY")
    public static class TestEntity implements Serializable {
        @Id
        @GeneratedValue
        private Integer id;

        private String name;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

