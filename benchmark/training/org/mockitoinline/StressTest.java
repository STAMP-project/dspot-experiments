/**
 * Copyright (c) 2018 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitoinline;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class StressTest {
    public class TestClass {
        public String getStuff() {
            return "A";
        }
    }

    @Test
    public void call_a_lot_of_mocks() {
        // This requires smaller heap set for the test process, see "inline.gradle"
        for (int i = 0; i < 40000; i++) {
            StressTest.TestClass mock = Mockito.mock(StressTest.TestClass.class);
            Mockito.when(mock.getStuff()).thenReturn("B");
            Assert.assertEquals("B", mock.getStuff());
            StressTest.TestClass serializableMock = Mockito.mock(StressTest.TestClass.class, Mockito.withSettings().serializable());
            Mockito.when(serializableMock.getStuff()).thenReturn("C");
            Assert.assertEquals("C", serializableMock.getStuff());
            if ((i % 1024) == 0) {
                System.out.println((i + "/40000 mocks called"));
            }
        }
    }
}

