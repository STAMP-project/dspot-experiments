/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.plugins.switcher;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockitousage.plugins.instantiator.MyInstantiatorProvider2;
import org.mockitousage.plugins.logger.MyMockitoLogger;
import org.mockitousage.plugins.stacktrace.MyStackTraceCleanerProvider;


public class PluginSwitchTest {
    @SuppressWarnings("CheckReturnValue")
    @Test
    public void plugin_switcher_is_used() {
        Mockito.mock(List.class);
        Assert.assertEquals(MyPluginSwitch.invokedFor, Arrays.asList(MyMockMaker.class.getName(), MyStackTraceCleanerProvider.class.getName(), MyMockitoLogger.class.getName(), MyInstantiatorProvider2.class.getName()));
    }

    @Test
    public void uses_custom_mock_maker() {
        // when
        MyMockMaker.explosive.set(new Object());
        // when
        try {
            Mockito.mock(List.class);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals(MyMockMaker.class.getName(), e.getMessage());
        } finally {
            MyMockMaker.explosive.remove();
        }
    }
}

