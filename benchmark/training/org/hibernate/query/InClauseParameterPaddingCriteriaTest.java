/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.query;


import java.util.Arrays;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.jdbc.SQLStatementInterceptor;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-13108")
public class InClauseParameterPaddingCriteriaTest extends BaseEntityManagerFunctionalTestCase {
    private SQLStatementInterceptor sqlStatementInterceptor;

    @Test
    public void testInClauseParameterPadding() {
        sqlStatementInterceptor.clear();
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> query = cb.createQuery(.class);
            Root<org.hibernate.query.Document> document = query.from(.class);
            ParameterExpression<List> inClauseParams = cb.parameter(.class, "ids");
            query.select(document.get("id")).where(document.get("id").in(inClauseParams));
            List<Long> ids = entityManager.createQuery(query).setParameter("ids", Arrays.asList(1, 2, 3, 4, 5)).getResultList();
            assertEquals(1, ids.size());
        });
        Assert.assertTrue(sqlStatementInterceptor.getSqlQueries().get(0).endsWith("in (? , ? , ? , ? , ? , ? , ? , ?)"));
    }

    @Entity(name = "Document")
    public static class Document {
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

