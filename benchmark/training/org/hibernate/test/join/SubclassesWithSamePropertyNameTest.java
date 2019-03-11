/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.join;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Two subclasses of Reportable each have a property with the same name:
 * Bug#detail and BlogEntry#detail. BlogEntry#detail is stored on a
 * join (secondary) table. Bug#detail is actually a collection, so its
 * values should be stored in a collection table.
 *
 * @author Gail Badner
 */
@TestForIssue(jiraKey = "HHH-11241")
public class SubclassesWithSamePropertyNameTest extends BaseCoreFunctionalTestCase {
    private Long blogEntryId;

    @Test
    @TestForIssue(jiraKey = "HHH-11241")
    public void testGetSuperclass() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Reportable reportable = s.get(Reportable.class, blogEntryId);
        Assert.assertEquals("John Doe", reportable.getReportedBy());
        Assert.assertEquals("detail", ((BlogEntry) (reportable)).getDetail());
        tx.commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11241")
    public void testQuerySuperclass() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Reportable reportable = ((Reportable) (s.createQuery("from Reportable where reportedBy='John Doe'").uniqueResult()));
        Assert.assertEquals("John Doe", reportable.getReportedBy());
        Assert.assertEquals("detail", ((BlogEntry) (reportable)).getDetail());
        tx.commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11241")
    public void testCriteriaSuperclass() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Reportable reportable = ((Reportable) (s.createCriteria(Reportable.class, "r").add(Restrictions.eq("r.reportedBy", "John Doe")).uniqueResult()));
        Assert.assertEquals("John Doe", reportable.getReportedBy());
        Assert.assertEquals("detail", ((BlogEntry) (reportable)).getDetail());
        tx.commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11241")
    public void testQuerySubclass() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        BlogEntry blogEntry = ((BlogEntry) (s.createQuery("from BlogEntry where reportedBy='John Doe'").uniqueResult()));
        Assert.assertEquals("John Doe", blogEntry.getReportedBy());
        Assert.assertEquals("detail", blogEntry.getDetail());
        tx.commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11241")
    public void testCriteriaSubclass() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        BlogEntry blogEntry = ((BlogEntry) (s.createCriteria(BlogEntry.class, "r").add(Restrictions.eq("r.reportedBy", "John Doe")).uniqueResult()));
        Assert.assertEquals("John Doe", blogEntry.getReportedBy());
        Assert.assertEquals("detail", blogEntry.getDetail());
        tx.commit();
        s.close();
    }
}

