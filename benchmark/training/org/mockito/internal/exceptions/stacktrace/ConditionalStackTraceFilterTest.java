/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.exceptions.stacktrace;


import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.exceptions.base.TraceBuilder;
import org.mockito.internal.configuration.ConfigurationAccess;
import org.mockitoutil.Conditions;
import org.mockitoutil.TestBase;


public class ConditionalStackTraceFilterTest extends TestBase {
    private ConditionalStackTraceFilter filter = new ConditionalStackTraceFilter();

    @Test
    public void shouldNotFilterWhenConfigurationSaysNo() {
        ConfigurationAccess.getConfig().overrideCleansStackTrace(false);
        Throwable t = new TraceBuilder().classes("org.test.MockitoSampleTest", "org.mockito.Mockito").toThrowable();
        filter.filter(t);
        Assertions.assertThat(t).has(Conditions.onlyThoseClassesInStackTrace("org.mockito.Mockito", "org.test.MockitoSampleTest"));
    }

    @Test
    public void shouldFilterWhenConfigurationSaysYes() {
        ConfigurationAccess.getConfig().overrideCleansStackTrace(true);
        Throwable t = new TraceBuilder().classes("org.test.MockitoSampleTest", "org.mockito.Mockito").toThrowable();
        filter.filter(t);
        Assertions.assertThat(t).has(Conditions.onlyThoseClassesInStackTrace("org.test.MockitoSampleTest"));
    }
}

