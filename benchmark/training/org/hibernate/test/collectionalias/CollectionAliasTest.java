/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.collectionalias;


import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dave Stephan
 * @author Gail Badner
 */
public class CollectionAliasTest extends BaseCoreFunctionalTestCase {
    @TestForIssue(jiraKey = "HHH-7545")
    @Test
    public void test() {
        Session s = openSession();
        s.getTransaction().begin();
        ATable aTable = new ATable(1);
        TableB tableB = new TableB(new TableBId(1, "a", "b"));
        aTable.getTablebs().add(tableB);
        tableB.setTablea(aTable);
        s.save(aTable);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        aTable = ((ATable) (s.createQuery("select distinct	tablea from ATable tablea LEFT JOIN FETCH tablea.tablebs ").uniqueResult()));
        Assert.assertEquals(new Integer(1), aTable.getFirstId());
        Assert.assertEquals(1, aTable.getTablebs().size());
        tableB = aTable.getTablebs().get(0);
        Assert.assertSame(aTable, tableB.getTablea());
        Assert.assertEquals(new Integer(1), tableB.getId().getFirstId());
        Assert.assertEquals("a", tableB.getId().getSecondId());
        Assert.assertEquals("b", tableB.getId().getThirdId());
        s.close();
    }
}

