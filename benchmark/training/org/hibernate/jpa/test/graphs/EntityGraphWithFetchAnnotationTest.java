/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.graphs;


import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.jdbc.SQLStatementInterceptor;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class EntityGraphWithFetchAnnotationTest extends BaseEntityManagerFunctionalTestCase {
    private SQLStatementInterceptor sqlStatementInterceptor;

    @Test
    @TestForIssue(jiraKey = "HHH-10485")
    public void testWithoutEntityGraph() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.graphs.Order> criteriaQuery = criteriaBuilder.createQuery(.class);
            criteriaQuery.from(.class);
            sqlStatementInterceptor.clear();
            entityManager.createQuery(criteriaQuery).setFirstResult(10).setMaxResults(20).getResultList();
            assertFalse(sqlStatementInterceptor.getSqlQueries().get(0).toLowerCase().contains("left outer join"));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10485")
    public void testWithEntityGraph() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.graphs.Order> criteriaQuery = criteriaBuilder.createQuery(.class);
            criteriaQuery.from(.class);
            EntityGraph<org.hibernate.jpa.test.graphs.Order> entityGraph = entityManager.createEntityGraph(.class);
            entityGraph.addAttributeNodes("products");
            sqlStatementInterceptor.clear();
            entityManager.createQuery(criteriaQuery).setFirstResult(10).setMaxResults(20).setHint(QueryHints.HINT_FETCHGRAPH, entityGraph).getResultList();
            assertTrue(sqlStatementInterceptor.getSqlQueries().get(0).toLowerCase().contains("left outer join"));
        });
    }

    @Entity(name = "Order")
    @Table(name = "orders")
    public static class Order {
        @Id
        @GeneratedValue
        private long id;

        @OneToMany
        @Fetch(FetchMode.SELECT)
        private List<EntityGraphWithFetchAnnotationTest.Product> products;

        @OneToMany
        @Fetch(FetchMode.SELECT)
        private List<EntityGraphWithFetchAnnotationTest.Tag> tags;

        public long getId() {
            return this.id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public List<EntityGraphWithFetchAnnotationTest.Product> getProducts() {
            return this.products;
        }

        public void setProducts(List<EntityGraphWithFetchAnnotationTest.Product> products) {
            this.products = products;
        }

        public List<EntityGraphWithFetchAnnotationTest.Tag> getTags() {
            return this.tags;
        }

        public void setTags(List<EntityGraphWithFetchAnnotationTest.Tag> tags) {
            this.tags = tags;
        }
    }

    @Entity(name = "Product")
    @Table(name = "products")
    public static class Product {
        @Id
        @GeneratedValue
        private long id;

        private String name;

        public long getId() {
            return this.id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Entity(name = "Tag")
    @Table(name = "tags")
    public static class Tag {
        @Id
        @GeneratedValue
        private long id;

        private String name;

        public long getId() {
            return this.id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

