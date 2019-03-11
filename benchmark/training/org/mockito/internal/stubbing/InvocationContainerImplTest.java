/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.stubbing;


import java.util.LinkedList;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.internal.invocation.InvocationBuilder;
import org.mockito.internal.invocation.InvocationMatcher;
import org.mockito.internal.stubbing.defaultanswers.ReturnsEmptyValues;
import org.mockito.invocation.Invocation;
import org.mockito.mock.MockCreationSettings;


/**
 * Author: Szczepan Faber
 */
public class InvocationContainerImplTest {
    InvocationContainerImpl container = new InvocationContainerImpl(new MockSettingsImpl());

    InvocationContainerImpl containerStubOnly = new InvocationContainerImpl(((MockCreationSettings) (new MockSettingsImpl().stubOnly())));

    Invocation invocation = new InvocationBuilder().toInvocation();

    LinkedList<Throwable> exceptions = new LinkedList<Throwable>();

    @Test
    public void should_be_thread_safe() throws Throwable {
        doShouldBeThreadSafe(container);
    }

    @Test
    public void should_be_thread_safe_stub_only() throws Throwable {
        doShouldBeThreadSafe(containerStubOnly);
    }

    @Test
    public void should_return_invoked_mock() throws Exception {
        container.setInvocationForPotentialStubbing(new InvocationMatcher(invocation));
        Assert.assertEquals(invocation.getMock(), container.invokedMock());
    }

    @Test
    public void should_return_invoked_mock_stub_only() throws Exception {
        containerStubOnly.setInvocationForPotentialStubbing(new InvocationMatcher(invocation));
        Assert.assertEquals(invocation.getMock(), containerStubOnly.invokedMock());
    }

    @Test
    public void should_tell_if_has_invocation_for_potential_stubbing() throws Exception {
        container.setInvocationForPotentialStubbing(new InvocationBuilder().toInvocationMatcher());
        Assert.assertTrue(container.hasInvocationForPotentialStubbing());
        container.addAnswer(new ReturnsEmptyValues(), null);
        Assert.assertFalse(container.hasInvocationForPotentialStubbing());
    }

    @Test
    public void should_tell_if_has_invocation_for_potential_stubbing_stub_only() throws Exception {
        containerStubOnly.setInvocationForPotentialStubbing(new InvocationBuilder().toInvocationMatcher());
        Assert.assertTrue(containerStubOnly.hasInvocationForPotentialStubbing());
        containerStubOnly.addAnswer(new ReturnsEmptyValues(), null);
        Assert.assertFalse(containerStubOnly.hasInvocationForPotentialStubbing());
    }
}

