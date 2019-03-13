/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria.alias;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.hibernate.transform.Transformers;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class CriteriaMultiselectAliasTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @FailureExpected(jiraKey = "HHH-13140")
    @TestForIssue(jiraKey = "HHH-13140")
    public void testAlias() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            final CriteriaQuery<Object[]> query = cb.createQuery(.class);
            final Root<org.hibernate.jpa.test.criteria.alias.Book> entity = query.from(.class);
            query.multiselect(entity.get("id").alias("id"), entity.get("name").alias("title"));
            List<org.hibernate.jpa.test.criteria.alias.BookDto> dtos = entityManager.createQuery(query).unwrap(.class).setResultTransformer(Transformers.aliasToBean(.class)).getResultList();
            assertEquals(1, dtos.size());
            org.hibernate.jpa.test.criteria.alias.BookDto dto = dtos.get(0);
            assertEquals(1, ((int) (dto.getId())));
            assertEquals(bookName(), dto.getTitle());
        });
    }

    @Test
    @FailureExpected(jiraKey = "HHH-13140")
    @TestForIssue(jiraKey = "HHH-13192")
    public void testNoAliasInWhereClause() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            final CriteriaQuery<Object[]> query = cb.createQuery(.class);
            final Root<org.hibernate.jpa.test.criteria.alias.Book> entity = query.from(.class);
            query.multiselect(entity.get("id").alias("id"), entity.get("name").alias("title"));
            query.where(cb.equal(entity.get("name"), cb.parameter(.class, "name")));
            List<org.hibernate.jpa.test.criteria.alias.BookDto> dtos = entityManager.createQuery(query).setParameter("name", bookName()).unwrap(.class).setResultTransformer(Transformers.aliasToBean(.class)).getResultList();
            assertEquals(1, dtos.size());
            org.hibernate.jpa.test.criteria.alias.BookDto dto = dtos.get(0);
            assertEquals(1, ((int) (dto.getId())));
            assertEquals(bookName(), dto.getTitle());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-13192")
    public void testNoAliasInWhereClauseSimplified() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Object> criteriaQuery = cb.createQuery();
            Root<org.hibernate.jpa.test.criteria.alias.Book> root = criteriaQuery.from(.class);
            criteriaQuery.where(cb.equal(root.get("id"), cb.parameter(.class, "id")));
            criteriaQuery.select(root.get("id").alias("x"));
            List<Object> results = entityManager.createQuery(criteriaQuery).setParameter("id", 1).getResultList();
            assertEquals(1, ((int) (results.get(0))));
        });
    }

    @Entity(name = "Book")
    public static class Book {
        @Id
        private Integer id;

        private String name;
    }

    public static class BookDto {
        private Integer id;

        private String title;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}

