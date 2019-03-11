/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.testing.logger;


import Level.DEBUG;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.core.IsNull;
import org.hibernate.testing.TestForIssue;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the TestHelper ..
 * Verifies the Logger interception capabilities which we might use in other tests
 * are working as expected.
 *
 * @author Sanne Grinovero <sanne@hibernate.org> (C) 2015 Red Hat Inc.
 */
@TestForIssue(jiraKey = "HHH-9658")
public class LogDelegationTest {
    private static final Logger LOG = Logger.getLogger(LogDelegationTest.class.getName());

    @Test
    public void testLogDelegationIsActivated() {
        Assert.assertThat(LogDelegationTest.LOG, IsInstanceOf.instanceOf(Log4DelegatingLogger.class));
    }

    @Test
    public void testRecording() {
        LogDelegationTest.TestListener listener = new LogDelegationTest.TestListener();
        LogInspectionHelper.registerListener(listener, LogDelegationTest.LOG);
        LogDelegationTest.LOG.debug("Hey coffee is ready!");
        Assert.assertThat(listener.isCAlled, Is.is(true));
        Assert.assertThat(listener.level, Is.is(DEBUG));
        Assert.assertThat(((String) (listener.renderedMessage)), Is.is("Hey coffee is ready!"));
        Assert.assertThat(listener.thrown, IsNull.nullValue());
        LogInspectionHelper.clearAllListeners(LogDelegationTest.LOG);
    }

    @Test
    public void testClearListeners() {
        LogDelegationTest.TestListener listener = new LogDelegationTest.TestListener();
        LogInspectionHelper.registerListener(listener, LogDelegationTest.LOG);
        LogInspectionHelper.clearAllListeners(LogDelegationTest.LOG);
        LogDelegationTest.LOG.debug("Hey coffee is ready!");
        Assert.assertThat(listener.isCAlled, Is.is(false));
    }

    private static class TestListener implements LogListener {
        boolean isCAlled = false;

        Level level;

        String renderedMessage;

        Throwable thrown;

        @Override
        public void loggedEvent(Level level, String renderedMessage, Throwable thrown) {
            this.level = level;
            this.renderedMessage = renderedMessage;
            this.thrown = thrown;
            this.isCAlled = true;
        }
    }
}

