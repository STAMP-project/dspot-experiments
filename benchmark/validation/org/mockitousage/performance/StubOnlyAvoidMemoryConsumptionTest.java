/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.performance;


import org.junit.Test;
import org.mockito.Mockito;


public class StubOnlyAvoidMemoryConsumptionTest {
    @Test
    public void using_stub_only_wont_thrown_an_OutOfMemoryError() {
        Object obj = Mockito.mock(Object.class, Mockito.withSettings().stubOnly());
        Mockito.when(obj.toString()).thenReturn("asdf");
        for (int i = 0; i < 1000000; i++) {
            obj.toString();
        }
    }
}

