/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.spi.impl;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CancellationException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AbstractInvocationFuture_CancelTest extends AbstractInvocationFuture_AbstractTest {
    @Rule
    public final ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void whenCancelCalled_thenFutureCancelled() {
        // When
        final boolean result = cancel(true);
        // Then
        Assert.assertTrue(result);
        Assert.assertTrue(isCancelled());
        Assert.assertTrue(isDone());
    }

    @Test
    public void whenCancelled_thenCantCancelAgain() {
        // When
        future.cancel(true);
        // Then
        Assert.assertFalse(future.cancel(true));
    }

    @Test
    public void whenCancelled_thenGetThrowsCancelled() throws Exception {
        // Given
        future.cancel(true);
        // Then
        exceptionRule.expect(CancellationException.class);
        // When
        get();
    }

    @Test
    public void whenCancelled_thenJoinThrowsCancelled() {
        // Given
        future.cancel(true);
        // Then
        exceptionRule.expect(CancellationException.class);
        // When
        join();
    }

    @Test
    public void whenCancelled_thenCompleteNoEffect() {
        // Given
        future.cancel(true);
        // When
        complete(value);
        // Then
        exceptionRule.expect(CancellationException.class);
        join();
    }
}

