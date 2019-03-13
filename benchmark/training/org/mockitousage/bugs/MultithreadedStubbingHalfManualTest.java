/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;


@Ignore
public class MultithreadedStubbingHalfManualTest {
    /**
     * Class with two methods, one of them is repeatedly mocked while another is repeatedly called.
     */
    public interface ToMock {
        Integer getValue(Integer param);

        List<Integer> getValues(Integer param);
    }

    /**
     * Thread pool for concurrent invocations.
     */
    private Executor executor;

    private List<Exception> exceptions = Collections.synchronizedList(new LinkedList<Exception>());

    // this problem shows at 4 out of 5 executions
    // it is not strictly a bug because Mockito does not support simultanous stubbing (see FAQ)
    // however I decided to synchronize some calls in order to make the exceptions nicer
    @Test
    public void tryToRevealTheProblem() {
        MultithreadedStubbingHalfManualTest.ToMock toMock = Mockito.mock(MultithreadedStubbingHalfManualTest.ToMock.class);
        for (int i = 0; i < 100; i++) {
            int j = i % 11;
            // Repeated mocking
            Mockito.when(toMock.getValue(i)).thenReturn(j);
            // TODO make it also showing errors for doReturn()
            // doReturn(j).when(toMock).getValue(i);
            while (true) {
                try {
                    // Scheduling invocation
                    this.executor.execute(getConflictingRunnable(toMock));
                    break;
                } catch (RejectedExecutionException ex) {
                    Assert.fail();
                }
            } 
            try {
                Thread.sleep((10 / ((i % 10) + 1)));// NOPMD

            } catch (InterruptedException e) {
            }
        }
    }
}

