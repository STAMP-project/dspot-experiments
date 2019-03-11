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


public class InvalidTargetMockitoJUnitRuleTest {
    @Rule
    public MockitoRule mockitoJUnitRule = MockitoJUnit.rule();

    @Mock
    private InvalidTargetMockitoJUnitRuleTest.Injected injected;

    @InjectMocks
    private InvalidTargetMockitoJUnitRuleTest.InjectInto injectInto;

    @Test
    public void shouldInjectWithInvalidReference() throws Exception {
        Assert.assertNotNull("Mock created", injected);
        Assert.assertNotNull("Test object created", injectInto);
    }

    public static class Injected {}

    public static class InjectInto {
        private InvalidTargetMockitoJUnitRuleTest.Injected injected;

        public InvalidTargetMockitoJUnitRuleTest.Injected getInjected() {
            return injected;
        }
    }
}

