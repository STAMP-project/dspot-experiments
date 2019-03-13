/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.compositeusertype.nested;


import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


public class NestedCompositeUserTypeTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12166")
    public void testIt() {
        Line line = new Line(new Point(0, 0), new Point(42, 84));
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            LineEntity le = new LineEntity();
            le.setLine(line);
            session.save(le);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            LineEntity lineEntry = session.createQuery(("from " + (.class.getName())), .class).uniqueResult();
            Assert.assertEquals(line, lineEntry.getLine());
        });
    }
}

