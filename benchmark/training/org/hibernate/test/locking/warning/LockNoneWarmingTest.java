/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.locking.warning;


import LockMode.NONE;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.Session;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.loader.Loader;
import org.hibernate.query.Query;
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
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-10513")
@RunWith(BMUnitRunner.class)
public class LockNoneWarmingTest extends BaseCoreFunctionalTestCase {
    private Triggerable triggerable;

    @Rule
    public LoggerInspectionRule logInspection = new LoggerInspectionRule(Logger.getMessageLogger(CoreMessageLogger.class, Loader.class.getName()));

    @Test
    @BMRules(rules = { @BMRule(targetClass = "org.hibernate.dialect.Dialect", targetMethod = "useFollowOnLocking", action = "return true", name = "H2DialectUseFollowOnLocking") })
    public void testQuerySetLockModeNONEDoNotLogAWarnMessageWhenTheDialectUseFollowOnLockingIsTrue() {
        try (Session s = openSession()) {
            final Query query = s.createQuery("from Item i join i.bids b where name = :name");
            query.setParameter("name", "ZZZZ");
            query.setLockMode("i", NONE);
            query.setLockMode("b", NONE);
            query.list();
            Assert.assertFalse("Log message was not triggered", triggerable.wasTriggered());
        }
    }

    @Entity(name = "Item")
    @Table(name = "ITEM")
    public static class Item implements Serializable {
        @Id
        String name;

        @OneToMany(mappedBy = "item", fetch = FetchType.EAGER)
        Set<LockNoneWarmingTest.Bid> bids = new HashSet<LockNoneWarmingTest.Bid>();
    }

    @Entity(name = "Bid")
    @Table(name = "BID")
    public static class Bid implements Serializable {
        @Id
        float amount;

        @Id
        @ManyToOne
        LockNoneWarmingTest.Item item;
    }
}

