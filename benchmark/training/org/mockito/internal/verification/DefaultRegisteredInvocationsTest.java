/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.verification;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.invocation.InvocationBuilder;
import org.mockito.invocation.Invocation;
import org.mockitoutil.TestBase;


public class DefaultRegisteredInvocationsTest extends TestBase {
    private DefaultRegisteredInvocations invocations;

    @Test
    public void should_not_return_to_string_method() throws Exception {
        Invocation toString = new InvocationBuilder().method("toString").toInvocation();
        Invocation simpleMethod = new InvocationBuilder().simpleMethod().toInvocation();
        invocations.add(toString);
        invocations.add(simpleMethod);
        Assert.assertTrue(invocations.getAll().contains(simpleMethod));
        Assert.assertFalse(invocations.getAll().contains(toString));
    }
}

