/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.listeners.InvocationListener;
import org.mockito.listeners.MethodInvocationReport;


public class ListenersLostOnResetMockTest {
    @Test
    public void listener() throws Exception {
        InvocationListener invocationListener = Mockito.mock(InvocationListener.class);
        List mockedList = Mockito.mock(List.class, Mockito.withSettings().invocationListeners(invocationListener));
        Mockito.reset(mockedList);
        mockedList.clear();
        Mockito.verify(invocationListener).reportInvocation(ArgumentMatchers.any(MethodInvocationReport.class));
    }
}

