/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;


import java.util.Collections;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.Filter;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@TestForIssue(jiraKey = "HHH-7757")
public class TupleSupportTest extends BaseUnitTestCase {
    @Entity(name = "TheEntity")
    public static class TheEntity {
        @Id
        private Long id;

        @Embedded
        private TupleSupportTest.TheComposite compositeValue;
    }

    @Embeddable
    public static class TheComposite {
        private String thing1;

        private String thing2;

        public TheComposite() {
        }

        public TheComposite(String thing1, String thing2) {
            this.thing1 = thing1;
            this.thing2 = thing2;
        }
    }

    private SessionFactory sessionFactory;

    @Test
    public void testImplicitTupleNotEquals() {
        final String hql = "from TheEntity e where e.compositeValue <> :p1";
        HQLQueryPlan queryPlan = getQueryPlanCache().getHQLQueryPlan(hql, false, Collections.<String, Filter>emptyMap());
        Assert.assertEquals(1, queryPlan.getSqlStrings().length);
        System.out.println((" SQL : " + (queryPlan.getSqlStrings()[0])));
        Assert.assertTrue(queryPlan.getSqlStrings()[0].contains("<>"));
    }

    @Test
    public void testImplicitTupleNotInList() {
        final String hql = "from TheEntity e where e.compositeValue not in (:p1,:p2)";
        HQLQueryPlan queryPlan = getQueryPlanCache().getHQLQueryPlan(hql, false, Collections.<String, Filter>emptyMap());
        Assert.assertEquals(1, queryPlan.getSqlStrings().length);
        System.out.println((" SQL : " + (queryPlan.getSqlStrings()[0])));
        Assert.assertTrue(queryPlan.getSqlStrings()[0].contains("<>"));
    }

    public static class NoTupleSupportDialect extends H2Dialect {
        @Override
        public boolean supportsRowValueConstructorSyntax() {
            return false;
        }

        @Override
        public boolean supportsRowValueConstructorSyntaxInInList() {
            return false;
        }
    }
}

