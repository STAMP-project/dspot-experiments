/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.twin;


import java.time.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Date: 12/30/15 - 18:55 PM
 *
 * @author Jeroen Meulemeester
 */
public class BallThreadTest {
    /**
     * Verify if the {@link BallThread} can be resumed
     */
    @Test
    public void testSuspend() throws Exception {
        Assertions.assertTimeout(Duration.ofMillis(5000), () -> {
            final BallThread ballThread = new BallThread();
            final BallItem ballItem = Mockito.mock(BallItem.class);
            ballThread.setTwin(ballItem);
            ballThread.start();
            Mockito.verify(ballItem, Mockito.timeout(2000).atLeastOnce()).draw();
            Mockito.verify(ballItem, Mockito.timeout(2000).atLeastOnce()).move();
            ballThread.suspendMe();
            Thread.sleep(1000);
            ballThread.stopMe();
            ballThread.join();
            Mockito.verifyNoMoreInteractions(ballItem);
        });
    }

    /**
     * Verify if the {@link BallThread} can be resumed
     */
    @Test
    public void testResume() {
        Assertions.assertTimeout(Duration.ofMillis(5000), () -> {
            final BallThread ballThread = new BallThread();
            final BallItem ballItem = Mockito.mock(BallItem.class);
            ballThread.setTwin(ballItem);
            ballThread.suspendMe();
            ballThread.start();
            Thread.sleep(1000);
            Mockito.verifyZeroInteractions(ballItem);
            ballThread.resumeMe();
            Mockito.verify(ballItem, Mockito.timeout(2000).atLeastOnce()).draw();
            Mockito.verify(ballItem, Mockito.timeout(2000).atLeastOnce()).move();
            ballThread.stopMe();
            ballThread.join();
            Mockito.verifyNoMoreInteractions(ballItem);
        });
    }

    /**
     * Verify if the {@link BallThread} is interruptible
     */
    @Test
    public void testInterrupt() {
        Assertions.assertTimeout(Duration.ofMillis(5000), () -> {
            final BallThread ballThread = new BallThread();
            final Thread.UncaughtExceptionHandler exceptionHandler = Mockito.mock(Thread.UncaughtExceptionHandler.class);
            ballThread.setUncaughtExceptionHandler(exceptionHandler);
            ballThread.setTwin(Mockito.mock(BallItem.class));
            ballThread.start();
            ballThread.interrupt();
            ballThread.join();
            Mockito.verify(exceptionHandler).uncaughtException(ArgumentMatchers.eq(ballThread), ArgumentMatchers.any(RuntimeException.class));
            Mockito.verifyNoMoreInteractions(exceptionHandler);
        });
    }
}

