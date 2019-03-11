/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.query;


import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
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
@TestForIssue(jiraKey = "HHH-12469")
@RequiresDialect(H2Dialect.class)
public class MaxInExpressionParameterPaddingTest extends BaseEntityManagerFunctionalTestCase {
    public static final int MAX_COUNT = 15;

    private SQLStatementInterceptor sqlStatementInterceptor;

    @Test
    public void testInClauseParameterPadding() {
        sqlStatementInterceptor.clear();
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            return entityManager.createQuery(("select p " + ("from Person p " + "where p.id in :ids"))).setParameter("ids", IntStream.range(0, MAX_COUNT).boxed().collect(Collectors.toList())).getResultList();
        });
        StringBuilder expectedInClause = new StringBuilder();
        expectedInClause.append("in (?");
        for (int i = 1; i < (MaxInExpressionParameterPaddingTest.MAX_COUNT); i++) {
            expectedInClause.append(" , ?");
        }
        expectedInClause.append(")");
        Assert.assertTrue(sqlStatementInterceptor.getSqlQueries().get(0).endsWith(expectedInClause.toString()));
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
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

    public static class MaxCountInExpressionH2Dialect extends H2Dialect {
        @Override
        public int getInExpressionCountLimit() {
            return MaxInExpressionParameterPaddingTest.MAX_COUNT;
        }
    }
}

