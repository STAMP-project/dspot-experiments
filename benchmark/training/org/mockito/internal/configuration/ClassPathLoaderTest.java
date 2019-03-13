/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.configuration;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class ClassPathLoaderTest extends TestBase {
    @Test
    public void shouldReadConfigurationClassFromClassPath() {
        ConfigurationAccess.getConfig().overrideDefaultAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                return "foo";
            }
        });
        IMethods mock = Mockito.mock(IMethods.class);
        Assert.assertEquals("foo", mock.simpleMethod());
    }
}

