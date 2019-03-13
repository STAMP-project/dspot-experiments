/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.xml.ejb3;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.xml.ErrorLogger;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.logger.LoggerInspectionRule;
import org.hibernate.testing.logger.Triggerable;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-6271")
public class OrmVersion1SupportedTest extends BaseCoreFunctionalTestCase {
    @Rule
    public LoggerInspectionRule logInspection = new LoggerInspectionRule(Logger.getMessageLogger(CoreMessageLogger.class, ErrorLogger.class.getName()));

    @Test
    public void testOrm1Support() {
        Triggerable triggerable = logInspection.watchForLogMessages("HHH00196");
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Light light = new Light();
        light.name = "the light at the end of the tunnel";
        s.persist(light);
        s.flush();
        s.clear();
        Assert.assertEquals(1, s.getNamedQuery("find.the.light").list().size());
        tx.rollback();
        s.close();
        Assert.assertFalse(triggerable.wasTriggered());
    }
}

