/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.criteria.limitexpression;


import DialectChecks.SupportLimitCheck;
import java.util.Arrays;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@RequiresDialectFeature(value = SupportLimitCheck.class, comment = "Dialect does not support limit")
public class LimitExpressionTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-915")
    public void testWithFetchJoin() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final List<String> stateCodes = Arrays.asList("DC", "CT");
            final Criteria crit = session.createCriteria(.class);
            crit.createCriteria("states").add(Restrictions.in("code", stateCodes));
            crit.setMaxResults(10);
            crit.list();
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11278")
    public void testAnEmptyListIsReturnedWhenSetMaxResultsToZero() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final Criteria crit = session.createCriteria(.class);
            crit.setMaxResults(0);
            final java.util.List list = crit.list();
            assertTrue("The list should be empty with setMaxResults 0", list.isEmpty());
        });
    }
}

