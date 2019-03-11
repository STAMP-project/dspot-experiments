/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.plugins.stacktrace;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class PluginStackTraceFilteringTest extends TestBase {
    @Mock
    private IMethods mock;

    @Test
    public void pluginFiltersOutStackTraceElement() {
        try {
            MyStackTraceCleanerProvider.ENABLED = true;
            verifyMock_x();
            Assert.fail();
        } catch (WantedButNotInvoked e) {
            String trace = getStackTrace(e);
            assertThat(trace).contains("verifyMock_x").doesNotContain("verify_excludeMe_x");
        }
    }

    @Test
    public void pluginDoesNotFilterOutStackTraceElement() {
        try {
            MyStackTraceCleanerProvider.ENABLED = false;
            verifyMock_x();
            Assert.fail();
        } catch (WantedButNotInvoked e) {
            String trace = getStackTrace(e);
            assertThat(trace).contains("verifyMock_x").contains("verify_excludeMe_x");
        }
    }
}

