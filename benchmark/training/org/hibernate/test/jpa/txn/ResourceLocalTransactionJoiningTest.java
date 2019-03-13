/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.jpa.txn;


import org.hibernate.Session;
import org.hibernate.resource.transaction.backend.jdbc.internal.JdbcResourceLocalTransactionCoordinatorImpl;
import org.hibernate.test.jpa.AbstractJPATest;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class ResourceLocalTransactionJoiningTest extends AbstractJPATest {
    @Test
    @TestForIssue(jiraKey = "HHH-9859")
    public void testExpectations() {
        // JPA spec is very vague on what should happen here.  It does vaguely
        // imply that javax.persistence.EntityManager.joinTransaction() should only be used
        // for JTA EMs, however it does not enforced that nor does the TCK check that.
        // And the TCK in fact does test calls to javax.persistence.EntityManager.isJoinedToTransaction()
        // from resource-local EMs, so lets make sure those work..
        Session session = sessionFactory().openSession();
        JdbcResourceLocalTransactionCoordinatorImpl tc = ExtraAssertions.assertTyping(JdbcResourceLocalTransactionCoordinatorImpl.class, getTransactionCoordinator());
        Assert.assertFalse(tc.isJoined());
        session.beginTransaction();
        tc = ExtraAssertions.assertTyping(JdbcResourceLocalTransactionCoordinatorImpl.class, getTransactionCoordinator());
        Assert.assertTrue(tc.isJoined());
        session.getTransaction().rollback();
        session.close();
    }
}

