/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.query.hhh12076;


import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-12076")
public class HbmMappingJoinClassTest extends BaseCoreFunctionalTestCase {
    @Test
    @FailureExpected(jiraKey = "HHH-12076")
    public void testClassExpressionInOnClause() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List<SettlementTask> results = session.createQuery(("select " + ((((((("	rootAlias.id, " + "	linked.id, ") + "	extensions.id ") + "from SettlementTask as rootAlias ") + "join rootAlias.linked as linked ") + "left join linked.extensions as extensions ") + "	on extensions.class = org.hibernate.query.hhh12076.EwtAssessmentExtension ") + "where linked.id = :claimId"))).setParameter("claimId", 1L).getResultList();
            assertNotNull(results);
        });
    }
}

