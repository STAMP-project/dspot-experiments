/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.collections;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.OrderBy;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::collections-customizing-ordered-by-sql-clause-mapping-example[]
@RequiresDialect(H2Dialect.class)
public class OrderedBySQLTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.collections.Person person = new org.hibernate.userguide.collections.Person();
            person.setId(1L);
            person.setName("Vlad Mihalcea");
            person.addArticle(new org.hibernate.userguide.collections.Article("High-Performance JDBC", "Connection Management, Statement Caching, Batch Updates"));
            person.addArticle(new org.hibernate.userguide.collections.Article("High-Performance Hibernate", "Associations, Lazy fetching, Concurrency Control, Second-level Caching"));
            entityManager.persist(person);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::collections-customizing-ordered-by-sql-clause-fetching-example[]
            org.hibernate.userguide.collections.Person person = entityManager.find(.class, 1L);
            assertEquals("High-Performance Hibernate", person.getArticles().get(0).getName());
            // end::collections-customizing-ordered-by-sql-clause-fetching-example[]
        });
    }

    // tag::collections-customizing-ordered-by-sql-clause-mapping-example[]
    // tag::collections-customizing-ordered-by-sql-clause-mapping-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        private String name;

        @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
        @OrderBy(clause = "CHAR_LENGTH(name) DESC")
        private List<OrderedBySQLTest.Article> articles = new ArrayList<>();

        // Getters and setters are omitted for brevity
        // end::collections-customizing-ordered-by-sql-clause-mapping-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<OrderedBySQLTest.Article> getArticles() {
            return articles;
        }

        public void addArticle(OrderedBySQLTest.Article article) {
            article.setPerson(this);
            articles.add(article);
        }
    }

    // tag::collections-customizing-ordered-by-sql-clause-mapping-example[]
    @Entity(name = "Article")
    public static class Article {
        @Id
        @GeneratedValue
        private Long id;

        private String name;

        private String content;

        @ManyToOne(fetch = FetchType.LAZY)
        private OrderedBySQLTest.Person person;

        // Getters and setters are omitted for brevity
        // end::collections-customizing-ordered-by-sql-clause-mapping-example[]
        private Article() {
        }

        public Article(String name, String content) {
            this.name = name;
            this.content = content;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public OrderedBySQLTest.Person getPerson() {
            return person;
        }

        public void setPerson(OrderedBySQLTest.Person person) {
            this.person = person;
        }
    }
}

