/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria.nulliteral;


import java.util.ArrayList;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.jdbc.SQLStatementInterceptor;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(H2Dialect.class)
public class CriteriaLiteralsTest extends BaseEntityManagerFunctionalTestCase {
    private SQLStatementInterceptor sqlStatementInterceptor;

    @Test
    public void testLiteralsInWhereClause() throws Exception {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            final CriteriaQuery<Tuple> query = cb.createQuery(.class);
            final Root<org.hibernate.jpa.test.criteria.nulliteral.Book> entity = query.from(.class);
            query.where(cb.equal(entity.get("name"), cb.literal("( SELECT REPEAT('abc' || ' ', 10000000000 FROM MY_ENTITY )")));
            query.multiselect(cb.literal("abc"), entity.get("name"));
            sqlStatementInterceptor.clear();
            List<Tuple> tuples = entityManager.createQuery(query).getResultList();
            assertEquals(1, sqlStatementInterceptor.getSqlQueries().size());
            sqlStatementInterceptor.assertExecuted("select 'abc' as col_0_0_, criteriali0_.name as col_1_0_ from Book criteriali0_ where criteriali0_.name=?");
            assertTrue(tuples.isEmpty());
        });
    }

    @Test
    public void testNumericLiteralsInWhereClause() throws Exception {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            testNumericLiterals(entityManager, "select 'abc' as col_0_0_, criteriali0_.name as col_1_0_ from Book criteriali0_ where criteriali0_.id=1");
        });
    }

    @Test
    public void testNumericLiteralsInWhereClauseUsingBindParameters() throws Exception {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            testNumericLiterals(entityManager, "select 'abc' as col_0_0_, criteriali0_.name as col_1_0_ from Book criteriali0_ where criteriali0_.id=1");
        });
    }

    @Test
    public void testCriteriaParameters() throws Exception {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<String> query = cb.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.nulliteral.Book> root = query.from(.class);
            ListJoin<org.hibernate.jpa.test.criteria.nulliteral.Book, org.hibernate.jpa.test.criteria.nulliteral.Author> authors = root.joinList("authors");
            query.where(cb.equal(root.get("name"), "( SELECT REPEAT('abc' || ' ', 10000000000 FROM MY_ENTITY )"), cb.equal(authors.index(), 0)).select(authors.get("name"));
            sqlStatementInterceptor.clear();
            entityManager.createQuery(query).getResultList();
            assertEquals(1, sqlStatementInterceptor.getSqlQueries().size());
            sqlStatementInterceptor.assertExecuted("select authors1_.name as col_0_0_ from Book criteriali0_ inner join Author authors1_ on criteriali0_.id=authors1_.book_id where criteriali0_.name=? and authors1_.index_id=0");
        });
    }

    @Test
    public void testLiteralsInSelectClause() throws Exception {
        try {
            TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
                final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                CriteriaQuery<String> query = cb.createQuery(.class);
                Root<org.hibernate.jpa.test.criteria.nulliteral.Book> root = query.from(.class);
                ListJoin<org.hibernate.jpa.test.criteria.nulliteral.Book, org.hibernate.jpa.test.criteria.nulliteral.Author> authors = root.joinList("authors");
                query.where(cb.equal(root.get("name"), "Java Persistence with Hibernate")).select(cb.literal("( SELECT REPEAT('abc' || ' ', 10000000000 FROM MY_ENTITY )"));
                entityManager.createQuery(query).getResultList();
            });
        } catch (Exception expected) {
            Assert.assertEquals(QuerySyntaxException.class, expected.getCause().getClass());
        }
    }

    @Entity(name = "Book")
    public static class Book {
        @Id
        @GeneratedValue
        private Integer id;

        private String name;

        @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
        @OrderColumn(name = "index_id")
        private java.util.List<CriteriaLiteralsTest.Author> authors = new ArrayList<>();
    }

    @Entity(name = "Author")
    public static class Author {
        @Id
        @GeneratedValue
        private Integer id;

        private String name;

        @ManyToOne
        private CriteriaLiteralsTest.Book book;
    }
}

