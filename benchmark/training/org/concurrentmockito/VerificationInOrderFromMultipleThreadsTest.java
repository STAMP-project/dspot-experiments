/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.concurrentmockito;


import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;


public class VerificationInOrderFromMultipleThreadsTest extends TestBase {
    @Test
    public void shouldVerifyInOrderWhenMultipleThreadsInteractWithMock() throws Exception {
        final VerificationInOrderFromMultipleThreadsTest.Foo testInf = Mockito.mock(VerificationInOrderFromMultipleThreadsTest.Foo.class);
        Thread threadOne = new Thread(new Runnable() {
            public void run() {
                testInf.methodOne();
            }
        });
        threadOne.start();
        threadOne.join();
        Thread threadTwo = new Thread(new Runnable() {
            public void run() {
                testInf.methodTwo();
            }
        });
        threadTwo.start();
        threadTwo.join();
        InOrder inOrder = Mockito.inOrder(testInf);
        inOrder.verify(testInf).methodOne();
        inOrder.verify(testInf).methodTwo();
    }

    public interface Foo {
        void methodOne();

        void methodTwo();
    }
}

