/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.concurrent;


import java.util.concurrent.ThreadFactory;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Test class for {@code BasicThreadFactory}.
 */
public class BasicThreadFactoryTest {
    /**
     * Constant for the test naming pattern.
     */
    private static final String PATTERN = "testThread-%d";

    /**
     * The builder for creating a thread factory.
     */
    private BasicThreadFactory.Builder builder;

    /**
     * Tests the default values used by the builder.
     */
    @Test
    public void testBuildDefaults() {
        final BasicThreadFactory factory = builder.build();
        checkFactoryDefaults(factory);
    }

    /**
     * Tries to set a null naming pattern.
     */
    @Test
    public void testBuildNamingPatternNull() {
        Assertions.assertThrows(NullPointerException.class, () -> builder.namingPattern(null));
    }

    /**
     * Tries to set a null wrapped factory.
     */
    @Test
    public void testBuildWrappedFactoryNull() {
        Assertions.assertThrows(NullPointerException.class, () -> builder.wrappedFactory(null));
    }

    /**
     * Tries to set a null exception handler.
     */
    @Test
    public void testBuildUncaughtExceptionHandlerNull() {
        Assertions.assertThrows(NullPointerException.class, () -> builder.uncaughtExceptionHandler(null));
    }

    /**
     * Tests the reset() method of the builder.
     */
    @Test
    public void testBuilderReset() {
        final ThreadFactory wrappedFactory = EasyMock.createMock(ThreadFactory.class);
        final Thread.UncaughtExceptionHandler exHandler = EasyMock.createMock(Thread.UncaughtExceptionHandler.class);
        EasyMock.replay(wrappedFactory, exHandler);
        builder.namingPattern(BasicThreadFactoryTest.PATTERN).daemon(true).priority(Thread.MAX_PRIORITY).uncaughtExceptionHandler(exHandler).wrappedFactory(wrappedFactory);
        builder.reset();
        final BasicThreadFactory factory = builder.build();
        checkFactoryDefaults(factory);
        Assertions.assertNotSame(wrappedFactory, factory.getWrappedFactory(), "Wrapped factory not reset");
        EasyMock.verify(wrappedFactory, exHandler);
    }

    /**
     * Tests whether reset() is automatically called after build().
     */
    @Test
    public void testBuilderResetAfterBuild() {
        builder.wrappedFactory(EasyMock.createNiceMock(ThreadFactory.class)).namingPattern(BasicThreadFactoryTest.PATTERN).daemon(true).build();
        checkFactoryDefaults(builder.build());
    }

    /**
     * Tests whether the naming pattern is applied to new threads.
     */
    @Test
    public void testNewThreadNamingPattern() {
        final ThreadFactory wrapped = EasyMock.createMock(ThreadFactory.class);
        final Runnable r = EasyMock.createMock(Runnable.class);
        final int count = 12;
        for (int i = 0; i < count; i++) {
            EasyMock.expect(wrapped.newThread(r)).andReturn(new Thread());
        }
        EasyMock.replay(wrapped, r);
        final BasicThreadFactory factory = builder.wrappedFactory(wrapped).namingPattern(BasicThreadFactoryTest.PATTERN).build();
        for (int i = 0; i < count; i++) {
            final Thread t = factory.newThread(r);
            Assertions.assertEquals(String.format(BasicThreadFactoryTest.PATTERN, Long.valueOf((i + 1))), t.getName(), "Wrong thread name");
            Assertions.assertEquals((i + 1), factory.getThreadCount(), "Wrong thread count");
        }
        EasyMock.verify(wrapped, r);
    }

    /**
     * Tests whether the thread name is not modified if no naming pattern is
     * set.
     */
    @Test
    public void testNewThreadNoNamingPattern() {
        final ThreadFactory wrapped = EasyMock.createMock(ThreadFactory.class);
        final Runnable r = EasyMock.createMock(Runnable.class);
        final String name = "unchangedThreadName";
        final Thread t = new Thread(name);
        EasyMock.expect(wrapped.newThread(r)).andReturn(t);
        EasyMock.replay(wrapped, r);
        final BasicThreadFactory factory = builder.wrappedFactory(wrapped).build();
        Assertions.assertSame(t, factory.newThread(r), "Wrong thread");
        Assertions.assertEquals(name, t.getName(), "Name was changed");
        EasyMock.verify(wrapped, r);
    }

    /**
     * Tests whether daemon threads can be created.
     */
    @Test
    public void testNewThreadDaemonTrue() {
        checkDaemonFlag(true);
    }

    /**
     * Tests whether the daemon status of new threads can be turned off.
     */
    @Test
    public void testNewThreadDaemonFalse() {
        checkDaemonFlag(false);
    }

    /**
     * Tests whether the daemon flag is not touched on newly created threads if
     * it is not specified.
     */
    @Test
    public void testNewThreadNoDaemonFlag() {
        final ThreadFactory wrapped = EasyMock.createMock(ThreadFactory.class);
        final Runnable r1 = EasyMock.createMock(Runnable.class);
        final Runnable r2 = EasyMock.createMock(Runnable.class);
        final Thread t1 = new Thread();
        final Thread t2 = new Thread();
        t1.setDaemon(true);
        EasyMock.expect(wrapped.newThread(r1)).andReturn(t1);
        EasyMock.expect(wrapped.newThread(r2)).andReturn(t2);
        EasyMock.replay(wrapped, r1, r2);
        final BasicThreadFactory factory = builder.wrappedFactory(wrapped).build();
        Assertions.assertSame(t1, factory.newThread(r1), "Wrong thread 1");
        Assertions.assertTrue(t1.isDaemon(), "No daemon thread");
        Assertions.assertSame(t2, factory.newThread(r2), "Wrong thread 2");
        Assertions.assertFalse(t2.isDaemon(), "A daemon thread");
        EasyMock.verify(wrapped, r1, r2);
    }

    /**
     * Tests whether the priority is set on newly created threads.
     */
    @Test
    public void testNewThreadPriority() {
        final ThreadFactory wrapped = EasyMock.createMock(ThreadFactory.class);
        final Runnable r = EasyMock.createMock(Runnable.class);
        final Thread t = new Thread();
        EasyMock.expect(wrapped.newThread(r)).andReturn(t);
        EasyMock.replay(wrapped, r);
        final int priority = (Thread.NORM_PRIORITY) + 1;
        final BasicThreadFactory factory = builder.wrappedFactory(wrapped).priority(priority).build();
        Assertions.assertSame(t, factory.newThread(r), "Wrong thread");
        Assertions.assertEquals(priority, t.getPriority(), "Wrong priority");
        EasyMock.verify(wrapped, r);
    }

    /**
     * Tests whether the original priority is not changed if no priority is
     * specified.
     */
    @Test
    public void testNewThreadNoPriority() {
        final ThreadFactory wrapped = EasyMock.createMock(ThreadFactory.class);
        final Runnable r = EasyMock.createMock(Runnable.class);
        final int orgPriority = (Thread.NORM_PRIORITY) + 1;
        final Thread t = new Thread();
        t.setPriority(orgPriority);
        EasyMock.expect(wrapped.newThread(r)).andReturn(t);
        EasyMock.replay(wrapped, r);
        final BasicThreadFactory factory = builder.wrappedFactory(wrapped).build();
        Assertions.assertSame(t, factory.newThread(r), "Wrong thread");
        Assertions.assertEquals(orgPriority, t.getPriority(), "Wrong priority");
        EasyMock.verify(wrapped, r);
    }

    /**
     * Tests whether the exception handler is set if one is provided.
     */
    @Test
    public void testNewThreadExHandler() {
        final ThreadFactory wrapped = EasyMock.createMock(ThreadFactory.class);
        final Runnable r = EasyMock.createMock(Runnable.class);
        final Thread.UncaughtExceptionHandler handler = EasyMock.createMock(Thread.UncaughtExceptionHandler.class);
        final Thread t = new Thread();
        EasyMock.expect(wrapped.newThread(r)).andReturn(t);
        EasyMock.replay(wrapped, r, handler);
        final BasicThreadFactory factory = builder.wrappedFactory(wrapped).uncaughtExceptionHandler(handler).build();
        Assertions.assertSame(t, factory.newThread(r), "Wrong thread");
        Assertions.assertEquals(handler, t.getUncaughtExceptionHandler(), "Wrong exception handler");
        EasyMock.verify(wrapped, r, handler);
    }

    /**
     * Tests whether the original exception handler is not touched if none is
     * specified.
     */
    @Test
    public void testNewThreadNoExHandler() {
        final ThreadFactory wrapped = EasyMock.createMock(ThreadFactory.class);
        final Runnable r = EasyMock.createMock(Runnable.class);
        final Thread.UncaughtExceptionHandler handler = EasyMock.createMock(Thread.UncaughtExceptionHandler.class);
        final Thread t = new Thread();
        t.setUncaughtExceptionHandler(handler);
        EasyMock.expect(wrapped.newThread(r)).andReturn(t);
        EasyMock.replay(wrapped, r, handler);
        final BasicThreadFactory factory = builder.wrappedFactory(wrapped).build();
        Assertions.assertSame(t, factory.newThread(r), "Wrong thread");
        Assertions.assertEquals(handler, t.getUncaughtExceptionHandler(), "Wrong exception handler");
        EasyMock.verify(wrapped, r, handler);
    }
}

