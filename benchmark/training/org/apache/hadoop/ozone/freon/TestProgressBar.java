/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.ozone.freon;


import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;
import java.util.function.Supplier;
import java.util.stream.LongStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Using Mockito runner.
 */
/**
 * Tests for the Progressbar class for Freon.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestProgressBar {
    private PrintStream stream;

    private AtomicLong numberOfKeysAdded;

    private Supplier<Long> currentValue;

    @Test
    public void testWithRunnable() {
        Long maxValue = 10L;
        ProgressBar progressbar = new ProgressBar(stream, maxValue, currentValue);
        Runnable task = () -> {
            LongStream.range(0, maxValue).forEach(( counter) -> {
                numberOfKeysAdded.getAndIncrement();
            });
        };
        progressbar.start();
        task.run();
        progressbar.shutdown();
        Mockito.verify(stream, Mockito.atLeastOnce()).print(ArgumentMatchers.anyChar());
        Mockito.verify(stream, Mockito.atLeastOnce()).print(ArgumentMatchers.anyString());
    }
}

