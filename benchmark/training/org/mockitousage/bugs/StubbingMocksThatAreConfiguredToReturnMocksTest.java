/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import org.junit.Test;
import org.mockito.Mockito;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


// issue 151
public class StubbingMocksThatAreConfiguredToReturnMocksTest extends TestBase {
    @Test
    public void shouldAllowStubbingMocksConfiguredWithRETURNS_MOCKS() {
        IMethods mock = Mockito.mock(IMethods.class, Mockito.RETURNS_MOCKS);
        Mockito.when(mock.objectReturningMethodNoArgs()).thenReturn(null);
    }

    @Test
    public void shouldAllowStubbingMocksConfiguredWithRETURNS_MOCKSWithDoApi() {
        IMethods mock = Mockito.mock(IMethods.class, Mockito.RETURNS_MOCKS);
        Mockito.doReturn(null).when(mock).objectReturningMethodNoArgs();
    }
}

