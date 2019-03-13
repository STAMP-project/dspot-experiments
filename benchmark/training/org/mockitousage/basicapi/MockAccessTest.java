/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.basicapi;


import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class MockAccessTest {
    @Test
    public void shouldAllowStubbedMockReferenceAccess() throws Exception {
        Set<?> expectedMock = Mockito.mock(Set.class);
        Set<?> returnedMock = Mockito.when(expectedMock.isEmpty()).thenReturn(false).getMock();
        Assert.assertEquals(expectedMock, returnedMock);
    }

    @Test
    public void stubbedMockShouldWorkAsUsual() throws Exception {
        Set<?> returnedMock = Mockito.when(Mockito.mock(Set.class).isEmpty()).thenReturn(false, true).getMock();
        Assert.assertEquals(false, returnedMock.isEmpty());
        Assert.assertEquals(true, returnedMock.isEmpty());
    }
}

