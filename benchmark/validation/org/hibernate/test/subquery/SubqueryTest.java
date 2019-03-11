/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
/**
 * $Id$
 */
package org.hibernate.test.subquery;


import java.util.List;
import java.util.function.Consumer;
import javax.persistence.Query;
import org.hibernate.QueryException;
import org.hibernate.Session;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.type.Type;
import org.junit.Test;


/**
 * Test some subquery scenarios.
 *
 * @author Christian Beikov
 */
@RequiresDialect(H2Dialect.class)
public class SubqueryTest extends BaseCoreFunctionalTestCase {
    private static final SQLFunction LIMIT_FUNCTION = new SQLFunction() {
        public boolean hasArguments() {
            return true;
        }

        public boolean hasParenthesesIfNoArguments() {
            return true;
        }

        public Type getReturnType(Type type, Mapping mpng) throws QueryException {
            return type;
        }

        public String render(Type type, List list, SessionFactoryImplementor sfi) throws QueryException {
            String subquery = list.get(0).toString();
            return (((subquery.substring(0, ((subquery.length()) - 1))) + " limit ") + (list.get(1))) + ")";
        }
    };

    @Test
    public void testNestedOrderBySubqueryInFunction() {
        withLimit(( s) -> {
            Query q = s.createQuery(("SELECT a.id FROM EntityA a " + (((((((((((((("ORDER BY CASE WHEN (" + "SELECT 1 FROM EntityA s1 ") + "WHERE s1.id IN(") + "LIMIT(") + "(") + "SELECT 1 FROM EntityA sub ") + "ORDER BY ") + "CASE WHEN sub.name IS NULL THEN 1 ELSE 0 END, ") + "sub.name DESC, ") + "CASE WHEN sub.id IS NULL THEN 1 ELSE 0 END, ") + "sub.id DESC") + "),") + "1)") + ")") + ") = 1 THEN 1 ELSE 0 END")));
            q.getResultList();
        });
    }
}

