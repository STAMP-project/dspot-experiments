/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria.literal;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.jdbc.SQLStatementInterceptor;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public abstract class AbstractCriteriaLiteralHandlingModeTest extends BaseEntityManagerFunctionalTestCase {
    private SQLStatementInterceptor sqlStatementInterceptor;

    @Test
    public void testLiteralHandlingMode() throws Exception {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            final CriteriaQuery<Tuple> query = cb.createQuery(.class);
            final Root<org.hibernate.jpa.test.criteria.literal.Book> entity = query.from(.class);
            query.where(cb.and(cb.equal(entity.get("id"), cb.literal(1)), cb.equal(entity.get("name"), cb.literal(bookName()))));
            query.multiselect(cb.literal("abc"), entity.get("name"));
            sqlStatementInterceptor.clear();
            List<Tuple> tuples = entityManager.createQuery(query).getResultList();
            assertEquals(1, tuples.size());
            sqlStatementInterceptor.assertExecuted(expectedSQL());
        });
    }

    @Entity(name = "Book")
    public static class Book {
        @Id
        private Integer id;

        private String name;
    }
}

