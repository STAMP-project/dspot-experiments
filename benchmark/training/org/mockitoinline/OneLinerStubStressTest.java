/**
 * Copyright (c) 2018 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitoinline;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class OneLinerStubStressTest {
    public class OneLinerStubTestClass {
        public String getStuff() {
            return "A";
        }
    }

    @Test
    public void call_a_lot_of_mocks_using_one_line_stubbing() {
        // This requires smaller heap set for the test process, see "inline.gradle"
        final String returnValue = OneLinerStubStressTest.generateLargeString();
        for (int i = 0; i < 50000; i++) {
            // make sure that mock object does not get cleaned up prematurely
            final OneLinerStubStressTest.OneLinerStubTestClass mock = Mockito.when(Mockito.mock(OneLinerStubStressTest.OneLinerStubTestClass.class).getStuff()).thenReturn(returnValue).getMock();
            Assert.assertEquals(returnValue, mock.getStuff());
        }
    }
}

