/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.stateless;


import FetchMode.SELECT;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.org.hibernate.query.NativeQuery;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author stliu
 */
public class StatelessSessionQueryTest extends BaseCoreFunctionalTestCase {
    @Test
    @SkipForDialect(value = AbstractHANADialect.class, comment = " HANA doesn't support tables consisting of only a single auto-generated column")
    public void testCriteria() {
        StatelessSessionQueryTest.TestData testData = new StatelessSessionQueryTest.TestData();
        testData.createData();
        StatelessSession s = sessionFactory().openStatelessSession();
        Assert.assertEquals(1, s.createCriteria(Contact.class).list().size());
        s.close();
        testData.cleanData();
    }

    @Test
    @SkipForDialect(value = AbstractHANADialect.class, comment = " HANA doesn't support tables consisting of only a single auto-generated column")
    public void testCriteriaWithSelectFetchMode() {
        StatelessSessionQueryTest.TestData testData = new StatelessSessionQueryTest.TestData();
        testData.createData();
        StatelessSession s = sessionFactory().openStatelessSession();
        Assert.assertEquals(1, s.createCriteria(Contact.class).setFetchMode("org", SELECT).list().size());
        s.close();
        testData.cleanData();
    }

    @Test
    @SkipForDialect(value = AbstractHANADialect.class, comment = " HANA doesn't support tables consisting of only a single auto-generated column")
    public void testHQL() {
        StatelessSessionQueryTest.TestData testData = new StatelessSessionQueryTest.TestData();
        testData.createData();
        StatelessSession s = sessionFactory().openStatelessSession();
        Assert.assertEquals(1, s.createQuery("from Contact c join fetch c.org join fetch c.org.country").list().size());
        s.close();
        testData.cleanData();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-13194")
    @SkipForDialect(value = AbstractHANADialect.class, comment = " HANA doesn't support tables consisting of only a single auto-generated column")
    public void testDeprecatedQueryApis() {
        StatelessSessionQueryTest.TestData testData = new StatelessSessionQueryTest.TestData();
        testData.createData();
        final String queryString = "from Contact c join fetch c.org join fetch c.org.country";
        StatelessSession s = sessionFactory().openStatelessSession();
        org.hibernate.Query query = s.createQuery(queryString);
        Assert.assertEquals(1, query.getResultList().size());
        query = s.getNamedQuery(((Contact.class.getName()) + ".contacts"));
        Assert.assertEquals(1, query.getResultList().size());
        org.hibernate.SQLQuery sqlQuery = s.createSQLQuery("select id from Contact");
        Assert.assertEquals(1, sqlQuery.getResultList().size());
        s.close();
        testData.cleanData();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-13194")
    @SkipForDialect(value = AbstractHANADialect.class, comment = " HANA doesn't support tables consisting of only a single auto-generated column")
    public void testNewQueryApis() {
        StatelessSessionQueryTest.TestData testData = new StatelessSessionQueryTest.TestData();
        testData.createData();
        final String queryString = "from Contact c join fetch c.org join fetch c.org.country";
        StatelessSession s = sessionFactory().openStatelessSession();
        org.hibernate.query.Query query = s.createQuery(queryString);
        Assert.assertEquals(1, query.getResultList().size());
        query = s.getNamedQuery(((Contact.class.getName()) + ".contacts"));
        Assert.assertEquals(1, query.getResultList().size());
        org.hibernate.query.NativeQuery sqlQuery = s.createSQLQuery("select id from Contact");
        Assert.assertEquals(1, sqlQuery.getResultList().size());
        s.close();
        testData.cleanData();
    }

    private class TestData {
        List list = new ArrayList();

        public void createData() {
            Session session = openSession();
            Transaction tx = session.beginTransaction();
            Country usa = new Country();
            session.save(usa);
            list.add(usa);
            Org disney = new Org();
            disney.setCountry(usa);
            session.save(disney);
            list.add(disney);
            Contact waltDisney = new Contact();
            waltDisney.setOrg(disney);
            session.save(waltDisney);
            list.add(waltDisney);
            tx.commit();
            session.close();
        }

        public void cleanData() {
            Session session = openSession();
            Transaction tx = session.beginTransaction();
            for (Object obj : list) {
                session.delete(obj);
            }
            tx.commit();
            session.close();
        }
    }
}

