/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
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


import com.google.devtools.build.lib.concurrent.ThreadSafety.ConditionallyThreadCompatible;
import com.google.devtools.build.lib.concurrent.ThreadSafety.ConditionallyThreadSafe;
import com.google.devtools.build.lib.concurrent.ThreadSafety.Immutable;
import com.google.devtools.build.lib.concurrent.ThreadSafety.ThreadCompatible;
import com.google.devtools.build.lib.concurrent.ThreadSafety.ThreadHostile;
import com.google.devtools.build.lib.concurrent.ThreadSafety.ThreadSafe;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * This file just contains some examples of the use of
 * annotations for different categories of thread safety:
 *   ThreadSafe
 *   ThreadCompatible
 *   ThreadHostile
 *   Immutable ThreadSafe
 *   Immutable ThreadHostile
 *
 * It doesn't really test much -- just that this code
 * using those annotations compiles and runs.
 *
 * The main class here is annotated as being both ConditionallyThreadSafe
 * and ConditionallyThreadCompatible, and accordingly we document here the
 * conditions under which it is thread-safe and thread-compatible:
 *    - it is thread-safe if you only use the testThreadSafety() method,
 *      the ThreadSafeCounter class, and/or ImmutableThreadSafeCounter class;
 *    - it is thread-compatible if you use only those and/or the
 *      ThreadCompatibleCounter and/or ImmutableThreadCompatibleCounter class;
 *    - it is thread-hostile otherwise.
 */
@ConditionallyThreadSafe
@ConditionallyThreadCompatible
@RunWith(JUnit4.class)
public class ThreadSafetyTest {
    @ThreadSafe
    public static final class ThreadSafeCounter {
        // A ThreadSafe class can have public mutable fields,
        // provided they are atomic or volatile.
        public volatile boolean myBool;

        public AtomicInteger myInt;

        // A ThreadSafe class can have private mutable fields,
        // provided that access to them is synchronized.
        private int value;

        public ThreadSafeCounter(int value) {
            synchronized(this) {
                // is this needed?
                this.value = value;
            }
        }

        public synchronized int getValue() {
            return value;
        }

        public synchronized void increment() {
            (value)++;
        }

        // A ThreadSafe class can have private mutable members
        // provided that the methods of the class synchronize access
        // to them.
        // These members could be static...
        private static int numFoos = 0;

        public static synchronized void foo() {
            (ThreadSafetyTest.ThreadSafeCounter.numFoos)++;
        }

        public static synchronized int getNumFoos() {
            return ThreadSafetyTest.ThreadSafeCounter.numFoos;
        }

        // ... or non-static.
        private int numBars = 0;

        public synchronized void bar() {
            (numBars)++;
        }

        public synchronized int getNumBars() {
            return numBars;
        }
    }

    @ThreadCompatible
    public static final class ThreadCompatibleCounter {
        // A ThreadCompatible class can have public mutable fields.
        public int value;

        public ThreadCompatibleCounter(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void increment() {
            (value)++;
        }

        // A ThreadCompatible class can have mutable static members
        // provided that the methods of the class synchronize access
        // to them.
        private static int numFoos = 0;

        public static synchronized void foo() {
            (ThreadSafetyTest.ThreadCompatibleCounter.numFoos)++;
        }

        public static synchronized int getNumFoos() {
            return ThreadSafetyTest.ThreadCompatibleCounter.numFoos;
        }
    }

    @ThreadHostile
    public static final class ThreadHostileCounter {
        // A ThreadHostile class can have public mutable fields.
        public int value;

        public ThreadHostileCounter(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void increment() {
            (value)++;
        }

        // A ThreadHostile class can perform unsynchronized access
        // to mutable static data.
        private static int numFoos = 0;

        public static void foo() {
            (ThreadSafetyTest.ThreadHostileCounter.numFoos)++;
        }

        public static int getNumFoos() {
            return ThreadSafetyTest.ThreadHostileCounter.numFoos;
        }
    }

    @Immutable
    @ThreadSafe
    public static final class ImmutableThreadSafeCounter {
        // An Immutable ThreadSafe class can have public fields,
        // provided they are final and immutable.
        public final int value;

        public ImmutableThreadSafeCounter(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public ThreadSafetyTest.ImmutableThreadSafeCounter increment() {
            return new ThreadSafetyTest.ImmutableThreadSafeCounter(((value) + 1));
        }

        // An Immutable ThreadSafe class can have immutable static members.
        public static final int NUM_STATIC_CACHE_ENTRIES = 3;

        private static final ThreadSafetyTest.ImmutableThreadSafeCounter[] staticCache = new ThreadSafetyTest.ImmutableThreadSafeCounter[]{ new ThreadSafetyTest.ImmutableThreadSafeCounter(0), new ThreadSafetyTest.ImmutableThreadSafeCounter(1), new ThreadSafetyTest.ImmutableThreadSafeCounter(2) };

        public static ThreadSafetyTest.ImmutableThreadSafeCounter makeUsingStaticCache(int value) {
            if (value < (ThreadSafetyTest.ImmutableThreadSafeCounter.NUM_STATIC_CACHE_ENTRIES)) {
                return ThreadSafetyTest.ImmutableThreadSafeCounter.staticCache[value];
            } else {
                return new ThreadSafetyTest.ImmutableThreadSafeCounter(value);
            }
        }

        // An Immutable ThreadSafe class can have private mutable members
        // provided that the methods of the class synchronize access
        // to them.
        // These members could be static...
        private static int cachedValue = 0;

        private static ThreadSafetyTest.ImmutableThreadSafeCounter cachedCounter = new ThreadSafetyTest.ImmutableThreadSafeCounter(0);

        public static synchronized ThreadSafetyTest.ImmutableThreadSafeCounter makeUsingDynamicCache(int value) {
            if (value != (ThreadSafetyTest.ImmutableThreadSafeCounter.cachedValue)) {
                ThreadSafetyTest.ImmutableThreadSafeCounter.cachedValue = value;
                ThreadSafetyTest.ImmutableThreadSafeCounter.cachedCounter = new ThreadSafetyTest.ImmutableThreadSafeCounter(value);
            }
            return ThreadSafetyTest.ImmutableThreadSafeCounter.cachedCounter;
        }

        // ... or non-static.
        private ThreadSafetyTest.ImmutableThreadSafeCounter incrementCache = null;

        public synchronized ThreadSafetyTest.ImmutableThreadSafeCounter incrementUsingCache() {
            if ((incrementCache) == null) {
                incrementCache = new ThreadSafetyTest.ImmutableThreadSafeCounter(((value) + 1));
            }
            return incrementCache;
        }

        // Methods of an Immutable class need not be deterministic.
        private static Random random = new Random();

        public int choose() {
            return ThreadSafetyTest.ImmutableThreadSafeCounter.random.nextInt(value);
        }
    }

    @Immutable
    @ThreadHostile
    public static final class ImmutableThreadHostileCounter {
        // An Immutable ThreadHostile class can have public fields,
        // provided they are final and immutable.
        public final int value;

        public ImmutableThreadHostileCounter(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public ThreadSafetyTest.ImmutableThreadHostileCounter increment() {
            return new ThreadSafetyTest.ImmutableThreadHostileCounter(((value) + 1));
        }

        // An Immutable ThreadHostile class can have private mutable members,
        // and doesn't need to synchronize access to them.
        // These members could be static...
        private static int cachedValue = 0;

        private static ThreadSafetyTest.ImmutableThreadHostileCounter cachedCounter = new ThreadSafetyTest.ImmutableThreadHostileCounter(0);

        public static ThreadSafetyTest.ImmutableThreadHostileCounter makeUsingDynamicCache(int value) {
            if (value != (ThreadSafetyTest.ImmutableThreadHostileCounter.cachedValue)) {
                ThreadSafetyTest.ImmutableThreadHostileCounter.cachedValue = value;
                ThreadSafetyTest.ImmutableThreadHostileCounter.cachedCounter = new ThreadSafetyTest.ImmutableThreadHostileCounter(value);
            }
            return ThreadSafetyTest.ImmutableThreadHostileCounter.cachedCounter;
        }

        // ... or non-static.
        private ThreadSafetyTest.ImmutableThreadHostileCounter incrementCache = null;

        public ThreadSafetyTest.ImmutableThreadHostileCounter incrementUsingCache() {
            if ((incrementCache) == null) {
                incrementCache = new ThreadSafetyTest.ImmutableThreadHostileCounter(((value) + 1));
            }
            return incrementCache;
        }
    }

    @Test
    public void threadSafety() throws InterruptedException {
        final ThreadSafetyTest.ThreadSafeCounter[] threadSafeCounterArray = new ThreadSafetyTest.ThreadSafeCounter[]{ new ThreadSafetyTest.ThreadSafeCounter(1), new ThreadSafetyTest.ThreadSafeCounter(2), new ThreadSafetyTest.ThreadSafeCounter(3) };
        final ThreadSafetyTest.ThreadCompatibleCounter[] threadCompatibleCounterArray = new ThreadSafetyTest.ThreadCompatibleCounter[]{ new ThreadSafetyTest.ThreadCompatibleCounter(1), new ThreadSafetyTest.ThreadCompatibleCounter(2), new ThreadSafetyTest.ThreadCompatibleCounter(3) };
        final ThreadSafetyTest.ThreadHostileCounter threadHostileCounter = new ThreadSafetyTest.ThreadHostileCounter(1);
        class MyThread implements Runnable {
            ThreadSafetyTest.ThreadCompatibleCounter threadCompatibleCounter = new ThreadSafetyTest.ThreadCompatibleCounter(1);

            @Override
            public void run() {
                // ThreadSafe objects can be accessed with without synchronization
                for (ThreadSafetyTest.ThreadSafeCounter counter : threadSafeCounterArray) {
                    counter.increment();
                }
                // ThreadCompatible objects can be accessed with without
                // synchronization if they are thread-local
                threadCompatibleCounter.increment();
                // Access to ThreadCompatible objects must be synchronized
                // if they could be concurrently accessed by other threads
                for (ThreadSafetyTest.ThreadCompatibleCounter counter : threadCompatibleCounterArray) {
                    synchronized(counter) {
                        counter.increment();
                    }
                }
                // Access to ThreadHostile objects must be synchronized.
                synchronized(this.getClass()) {
                    threadHostileCounter.increment();
                }
            }
        }
        Thread thread1 = new Thread(new MyThread());
        Thread thread2 = new Thread(new MyThread());
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
    }
}

