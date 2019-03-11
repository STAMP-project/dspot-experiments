/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.quote;


import java.util.Iterator;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 * @author Brett Meyer
 */
public class QuoteGlobalTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-7890")
    public void testQuotedUniqueConstraint() {
        Iterator<UniqueKey> itr = metadata().getEntityBinding(Person.class.getName()).getTable().getUniqueKeyIterator();
        while (itr.hasNext()) {
            UniqueKey uk = itr.next();
            Assert.assertEquals(uk.getColumns().size(), 1);
            Assert.assertEquals(uk.getColumn(0).getName(), "name");
            return;
        } 
        Assert.fail("GLOBALLY_QUOTED_IDENTIFIERS caused the unique key creation to fail.");
    }

    @Test
    public void testQuoteManytoMany() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        User u = new User();
        s.persist(u);
        Role r = new Role();
        s.persist(r);
        u.getRoles().add(r);
        s.flush();
        s.clear();
        u = ((User) (s.get(User.class, u.getId())));
        Assert.assertEquals(1, u.getRoles().size());
        tx.rollback();
        String role = (User.class.getName()) + ".roles";
        Assert.assertEquals("User_Role", metadata().getCollectionBinding(role).getCollectionTable().getName());
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-8520")
    public void testHbmQuoting() {
        doTestHbmQuoting(DataPoint.class);
        doTestHbmQuoting(AssociatedDataPoint.class);
    }
}

