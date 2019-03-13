/**
 * Copyright (c) 2019 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitoinline.bugs;


import org.junit.Test;
import org.mockito.Mockito;


public class CyclicMockMethodArgumentMemoryLeakTest {
    private static final int ARRAY_LENGTH = 1 << 20;// 4 MB


    @Test
    public void no_memory_leak_when_cyclically_calling_method_with_mocks() {
        for (int i = 0; i < 100; ++i) {
            final CyclicMockMethodArgumentMemoryLeakTest.A a = Mockito.mock(CyclicMockMethodArgumentMemoryLeakTest.A.class);
            a.largeArray = new int[CyclicMockMethodArgumentMemoryLeakTest.ARRAY_LENGTH];
            final CyclicMockMethodArgumentMemoryLeakTest.B b = Mockito.mock(CyclicMockMethodArgumentMemoryLeakTest.B.class);
            a.accept(b);
            b.accept(a);
            clearInlineMocks();
        }
    }

    private static class A {
        private int[] largeArray;

        void accept(CyclicMockMethodArgumentMemoryLeakTest.B b) {
        }
    }

    private static class B {
        void accept(CyclicMockMethodArgumentMemoryLeakTest.A a) {
        }
    }
}

