/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.pagination;


import DialectChecks.SupportLimitAndOffsetCheck;
import DialectChecks.SupportLimitCheck;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hibernate.Session;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class PaginationTest extends BaseNonConfigCoreFunctionalTestCase {
    public static final int NUMBER_OF_TEST_ROWS = 100;

    @Test
    @RequiresDialectFeature(value = SupportLimitCheck.class, comment = "Dialect does not support limit")
    public void testLimit() {
        prepareTestData();
        Session session = openSession();
        session.beginTransaction();
        int count;
        count = generateBaseHQLQuery(session).setMaxResults(5).list().size();
        Assert.assertEquals(5, count);
        count = generateBaseCriteria(session).setMaxResults(18).list().size();
        Assert.assertEquals(18, count);
        count = generateBaseSQLQuery(session).setMaxResults(13).list().size();
        Assert.assertEquals(13, count);
        session.getTransaction().commit();
        session.close();
        cleanupTestData();
    }

    @Test
    public void testOffset() {
        prepareTestData();
        Session session = openSession();
        session.beginTransaction();
        List result;
        result = generateBaseHQLQuery(session).setFirstResult(3).list();
        DataPoint firstDataPointHQL = ((DataPoint) (result.get(0)));
        result = generateBaseCriteria(session).setFirstResult(3).list();
        DataPoint firstDataPointCriteria = ((DataPoint) (result.get(0)));
        Assert.assertEquals("The first entry should be the same in HQL and Criteria", firstDataPointHQL, firstDataPointHQL);
        Assert.assertEquals("Wrong first result", 3, firstDataPointCriteria.getSequence());
        session.getTransaction().commit();
        session.close();
        cleanupTestData();
    }

    /**
     *
     *
     * @author Piotr Findeisen <piotr.findeisen@gmail.com>
     */
    @Test
    @TestForIssue(jiraKey = "HHH-951")
    @RequiresDialectFeature(value = SupportLimitCheck.class, comment = "Dialect does not support limit")
    public void testLimitWithExpreesionAndFetchJoin() {
        Session session = openSession();
        session.beginTransaction();
        String hql = "SELECT b, 1 FROM DataMetaPoint b inner join fetch b.dataPoint dp";
        // This should not fail
        session.createQuery(hql).setMaxResults(3).list();
        HQLQueryPlan queryPlan = new HQLQueryPlan(hql, false, Collections.EMPTY_MAP, sessionFactory());
        String sqlQuery = queryPlan.getTranslators()[0].collectSqlStrings().get(0);
        session.getTransaction().commit();
        session.close();
        Matcher matcher = Pattern.compile("(?is)\\b(?<column>\\w+\\.\\w+)\\s+as\\s+(?<alias>\\w+)\\b.*\\k<column>\\s+as\\s+\\k<alias>").matcher(sqlQuery);
        if (matcher.find()) {
            Assert.fail(String.format("Column %s mapped to alias %s twice in generated SQL: %s", matcher.group("column"), matcher.group("alias"), sqlQuery));
        }
    }

    @Test
    @RequiresDialectFeature(value = SupportLimitAndOffsetCheck.class, comment = "Dialect does not support limit+offset")
    public void testLimitOffset() {
        prepareTestData();
        Session session = openSession();
        session.beginTransaction();
        List result;
        result = generateBaseHQLQuery(session).setFirstResult(0).setMaxResults(20).list();
        Assert.assertEquals(20, result.size());
        Assert.assertEquals(0, ((DataPoint) (result.get(0))).getSequence());
        Assert.assertEquals(1, ((DataPoint) (result.get(1))).getSequence());
        result = generateBaseCriteria(session).setFirstResult(1).setMaxResults(20).list();
        Assert.assertEquals(20, result.size());
        Assert.assertEquals(1, ((DataPoint) (result.get(0))).getSequence());
        Assert.assertEquals(2, ((DataPoint) (result.get(1))).getSequence());
        result = generateBaseCriteria(session).setFirstResult(99).setMaxResults(((Integer.MAX_VALUE) - 200)).list();
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(99, ((DataPoint) (result.get(0))).getSequence());
        result = session.createQuery("select distinct description from DataPoint order by description").setFirstResult(2).setMaxResults(3).list();
        Assert.assertEquals(3, result.size());
        Assert.assertEquals("Description: 2", result.get(0));
        Assert.assertEquals("Description: 3", result.get(1));
        Assert.assertEquals("Description: 4", result.get(2));
        result = session.createSQLQuery("select description, xval, yval from DataPoint order by xval, yval").setFirstResult(2).setMaxResults(5).list();
        Assert.assertEquals(5, result.size());
        Object[] row = ((Object[]) (result.get(0)));
        Assert.assertTrue(((row[0]) instanceof String));
        result = session.createSQLQuery("select * from DataPoint order by xval, yval").setFirstResult(2).setMaxResults(5).list();
        Assert.assertEquals(5, result.size());
        session.getTransaction().commit();
        session.close();
        cleanupTestData();
    }
}

