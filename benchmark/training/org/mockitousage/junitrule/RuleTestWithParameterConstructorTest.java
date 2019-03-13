/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.junitrule;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class RuleTestWithParameterConstructorTest {
    @Rule
    public MockitoRule mockitoJUnitRule = MockitoJUnit.rule();

    @Mock
    private RuleTestWithParameterConstructorTest.Injected injected;

    @InjectMocks
    private RuleTestWithParameterConstructorTest.InjectInto injectInto;

    @Test
    public void testInjectMocks() throws Exception {
        Assert.assertNotNull("Mock created", injected);
        Assert.assertNotNull("Object created", injectInto);
        Assert.assertEquals("A injected into B", injected, injectInto.getInjected());
    }

    public static class Injected {}

    public static class InjectInto {
        private RuleTestWithParameterConstructorTest.Injected injected;

        public RuleTestWithParameterConstructorTest.Injected getInjected() {
            return injected;
        }
    }
}

