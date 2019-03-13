/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import bolts.AggregateException;
import bolts.Task;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Test;


public class ParseTaskUtilsTest {
    /**
     * Verifies {@link bolts.AggregateException} gets wrapped with {@link ParseException} when thrown from
     * {@link com.parse.ParseTaskUtils#wait(bolts.Task)}.
     */
    @Test
    public void testWaitForTaskWrapsAggregateExceptionAsParseException() {
        final Exception error = new RuntimeException("This task failed.");
        final ArrayList<Task<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            final int number = i;
            Task<Void> task = Task.callInBackground(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    Thread.sleep(((long) ((Math.random()) * 100)));
                    if ((number == 10) || (number == 11)) {
                        throw error;
                    }
                    return null;
                }
            });
            tasks.add(task);
        }
        try {
            ParseTaskUtils.wait(Task.whenAll(tasks));
        } catch (ParseException e) {
            Assert.assertTrue(((e.getCause()) instanceof AggregateException));
        }
    }
}

