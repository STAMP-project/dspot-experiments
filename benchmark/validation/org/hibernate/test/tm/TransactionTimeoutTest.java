/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.tm;


import javax.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.TransactionException;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.test.jdbc.Person;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
@TestForIssue(jiraKey = "HHH-6780")
@SkipForDialect(value = { PostgreSQL81Dialect.class, PostgreSQLDialect.class }, comment = "PostgreSQL jdbc driver doesn't impl timeout method")
public class TransactionTimeoutTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testJdbcCoordinatorTransactionTimeoutCheck() {
        Session session = openSession();
        Transaction transaction = session.getTransaction();
        transaction.setTimeout(2);
        Assert.assertEquals((-1), getJdbcCoordinator().determineRemainingTransactionTimeOutPeriod());
        transaction.begin();
        Assert.assertNotSame((-1), getJdbcCoordinator().determineRemainingTransactionTimeOutPeriod());
        transaction.commit();
        session.close();
    }

    @Test
    public void testTransactionTimeoutFailure() throws InterruptedException {
        Session session = openSession();
        try {
            Transaction transaction = session.getTransaction();
            transaction.setTimeout(1);
            Assert.assertEquals((-1), getJdbcCoordinator().determineRemainingTransactionTimeOutPeriod());
            transaction.begin();
            Thread.sleep(1000);
            session.persist(new Person("Lukasz", "Antoniak"));
            transaction.commit();
        } catch (TransactionException e) {
            // expected
        } catch (PersistenceException e) {
            ExtraAssertions.assertTyping(TransactionException.class, e.getCause());
        } finally {
            session.close();
        }
    }

    @Test
    public void testTransactionTimeoutSuccess() {
        Session session = openSession();
        Transaction transaction = session.getTransaction();
        transaction.setTimeout(60);
        transaction.begin();
        session.persist(new Person("Lukasz", "Antoniak"));
        transaction.commit();
        session.close();
    }
}

