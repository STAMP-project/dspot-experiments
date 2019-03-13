/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.criteria;


import LockMode.NONE;
import LockMode.OPTIMISTIC;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.loader.Loader;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.logger.LoggerInspectionRule;
import org.hibernate.testing.logger.Triggerable;
import org.jboss.byteman.contrib.bmunit.BMRule;
import org.jboss.byteman.contrib.bmunit.BMRules;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Sanne Grinovero
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-8788")
@RunWith(BMUnitRunner.class)
public class CriteriaLockingTest extends BaseCoreFunctionalTestCase {
    @Rule
    public LoggerInspectionRule logInspection = new LoggerInspectionRule(Logger.getMessageLogger(CoreMessageLogger.class, Loader.class.getName()));

    @Test
    @BMRules(rules = { @BMRule(targetClass = "org.hibernate.dialect.Dialect", targetMethod = "useFollowOnLocking", action = "return true", name = "H2DialectUseFollowOnLocking") })
    public void testSetLockModeNONEDoNotLogAWarnMessageWhenTheDialectUseFollowOnLockingIsTrue() {
        buildSessionFactory();
        Triggerable triggerable = logInspection.watchForLogMessages("HHH000444");
        final Session s = openSession();
        final Transaction tx = s.beginTransaction();
        Item item = new Item();
        item.name = "ZZZZ";
        s.persist(item);
        s.flush();
        Criteria criteria = s.createCriteria(Item.class).setLockMode(NONE);
        criteria.list();
        tx.rollback();
        s.close();
        releaseSessionFactory();
        Assert.assertFalse(triggerable.wasTriggered());
    }

    @Test
    @BMRules(rules = { @BMRule(targetClass = "org.hibernate.dialect.Dialect", targetMethod = "useFollowOnLocking", action = "return true", name = "H2DialectUseFollowOnLocking") })
    public void testSetLockModeDifferentFromNONELogAWarnMessageWhenTheDialectUseFollowOnLockingIsTrue() {
        buildSessionFactory();
        Triggerable triggerable = logInspection.watchForLogMessages("HHH000444");
        final Session s = openSession();
        final Transaction tx = s.beginTransaction();
        Item item = new Item();
        item.name = "ZZZZ";
        s.persist(item);
        s.flush();
        Criteria criteria = s.createCriteria(Item.class).setLockMode(OPTIMISTIC);
        criteria.list();
        tx.rollback();
        s.close();
        releaseSessionFactory();
        Assert.assertTrue(triggerable.wasTriggered());
    }
}

