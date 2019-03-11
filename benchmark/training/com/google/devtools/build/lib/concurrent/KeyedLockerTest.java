/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.concurrent;


import java.util.concurrent.ExecutorService;
import org.junit.Test;


/**
 * Base class for tests for {@link KeyedLocker} implementations.
 */
public abstract class KeyedLockerTest {
    private static final int NUM_EXECUTOR_THREADS = 1000;

    private KeyedLocker<String> locker;

    protected ExecutorService executorService;

    protected ThrowableRecordingRunnableWrapper wrapper;

    @Test
    public void simpleSingleThreaded_NoUnlocks() {
        runSimpleSingleThreaded_NoUnlocks(makeLockFn1(), makeLockFn2());
    }

    @Test
    public void simpleSingleThreaded_WithUnlocks() {
        runSimpleSingleThreaded_WithUnlocks(makeLockFn1(), makeLockFn2());
    }

    @Test
    public void doubleUnlockOnSameAutoUnlockerNotAllowed() {
        runDoubleUnlockOnSameAutoUnlockerNotAllowed(makeLockFn1());
    }

    @Test
    public void unlockOnDifferentAutoUnlockersAllowed() {
        runUnlockOnDifferentAutoUnlockersAllowed(makeLockFn1());
    }

    @Test
    public void threadLocksMultipleTimesBeforeUnlocking() throws Exception {
        runThreadLocksMultipleTimesBeforeUnlocking(makeLockFn1());
    }

    @Test
    public void unlockOnOtherThreadNotAllowed() throws Exception {
        runUnlockOnOtherThreadNotAllowed(makeLockFn1());
    }

    @Test
    public void refCountingSanity() {
        runRefCountingSanity(makeLockFn1());
    }

    @Test
    public void simpleMultiThreaded_MutualExclusion() throws Exception {
        runSimpleMultiThreaded_MutualExclusion(makeLockFn1());
    }
}

