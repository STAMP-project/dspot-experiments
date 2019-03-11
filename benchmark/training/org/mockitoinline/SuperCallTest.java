/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitoinline;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public final class SuperCallTest {
    @Test
    public void testSuperMethodCall() {
        SuperCallTest.Dummy d = Mockito.spy(new SuperCallTest.Dummy());
        d.foo();
        Mockito.verify(d).bar(ArgumentMatchers.eq("baz"));
    }

    static class Dummy {
        public void foo() {
            bar("baz");
        }

        // Also fails if public.
        void bar(String s) {
            return;
        }
    }
}

