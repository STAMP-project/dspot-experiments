/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.tool.schema;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.engine.transaction.jta.platform.internal.JtaPlatformInitiator;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.logger.LoggerInspectionRule;
import org.hibernate.testing.logger.Triggerable;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-12763")
public class JtaPlatformLoggingTest extends BaseNonConfigCoreFunctionalTestCase {
    @Rule
    public LoggerInspectionRule logInspection = new LoggerInspectionRule(Logger.getMessageLogger(CoreMessageLogger.class, JtaPlatformInitiator.class.getName()));

    private Triggerable triggerable = logInspection.watchForLogMessages("HHH000490");

    @Test
    public void test() {
        Assert.assertEquals("HHH000490: Using JtaPlatform implementation: [org.hibernate.testing.jta.TestingJtaPlatformImpl]", triggerable.triggerMessage());
    }

    @Entity(name = "TestEntity")
    @Table(name = "TestEntity")
    public static class TestEntity {
        @Id
        public Integer id;

        String name;
    }
}

