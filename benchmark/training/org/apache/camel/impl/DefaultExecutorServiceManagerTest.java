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
package org.apache.camel.impl;


import ThreadPoolRejectedPolicy.Abort;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.apache.camel.spi.ThreadPoolProfile;
import org.apache.camel.util.concurrent.SizedScheduledExecutorService;
import org.junit.Assert;
import org.junit.Test;


public class DefaultExecutorServiceManagerTest extends ContextTestSupport {
    @Test
    public void testResolveThreadNameDefaultPattern() throws Exception {
        String foo = context.getExecutorServiceManager().resolveThreadName("foo");
        String bar = context.getExecutorServiceManager().resolveThreadName("bar");
        Assert.assertNotSame(foo, bar);
        Assert.assertTrue(foo.startsWith((("Camel (" + (context.getName())) + ") thread ")));
        Assert.assertTrue(foo.endsWith("foo"));
        Assert.assertTrue(bar.startsWith((("Camel (" + (context.getName())) + ") thread ")));
        Assert.assertTrue(bar.endsWith("bar"));
    }

    @Test
    public void testGetThreadNameCustomPattern() throws Exception {
        context.getExecutorServiceManager().setThreadNamePattern("##counter# - #name#");
        Assert.assertEquals("##counter# - #name#", context.getExecutorServiceManager().getThreadNamePattern());
        String foo = context.getExecutorServiceManager().resolveThreadName("foo");
        String bar = context.getExecutorServiceManager().resolveThreadName("bar");
        Assert.assertNotSame(foo, bar);
        Assert.assertTrue(foo.startsWith("#"));
        Assert.assertTrue(foo.endsWith(" - foo"));
        Assert.assertTrue(bar.startsWith("#"));
        Assert.assertTrue(bar.endsWith(" - bar"));
    }

    @Test
    public void testGetThreadNameCustomPatternCamelId() throws Exception {
        context.getExecutorServiceManager().setThreadNamePattern("##camelId# - ##counter# - #name#");
        String foo = context.getExecutorServiceManager().resolveThreadName("foo");
        String bar = context.getExecutorServiceManager().resolveThreadName("bar");
        Assert.assertNotSame(foo, bar);
        Assert.assertTrue(foo.startsWith((("#" + (context.getName())) + " - #")));
        Assert.assertTrue(foo.endsWith(" - foo"));
        Assert.assertTrue(bar.startsWith((("#" + (context.getName())) + " - #")));
        Assert.assertTrue(bar.endsWith(" - bar"));
    }

    @Test
    public void testGetThreadNameCustomPatternWithDollar() throws Exception {
        context.getExecutorServiceManager().setThreadNamePattern("Hello - #name#");
        String foo = context.getExecutorServiceManager().resolveThreadName("foo$bar");
        Assert.assertEquals("Hello - foo$bar", foo);
    }

    @Test
    public void testGetThreadNameCustomPatternLongName() throws Exception {
        context.getExecutorServiceManager().setThreadNamePattern("##counter# - #longName#");
        String foo = context.getExecutorServiceManager().resolveThreadName("foo?beer=Carlsberg");
        String bar = context.getExecutorServiceManager().resolveThreadName("bar");
        Assert.assertNotSame(foo, bar);
        Assert.assertTrue(foo.startsWith("#"));
        Assert.assertTrue(foo.endsWith(" - foo?beer=Carlsberg"));
        Assert.assertTrue(bar.startsWith("#"));
        Assert.assertTrue(bar.endsWith(" - bar"));
    }

    @Test
    public void testGetThreadNameCustomPatternWithParameters() throws Exception {
        context.getExecutorServiceManager().setThreadNamePattern("##counter# - #name#");
        String foo = context.getExecutorServiceManager().resolveThreadName("foo?beer=Carlsberg");
        String bar = context.getExecutorServiceManager().resolveThreadName("bar");
        Assert.assertNotSame(foo, bar);
        Assert.assertTrue(foo.startsWith("#"));
        Assert.assertTrue(foo.endsWith(" - foo"));
        Assert.assertTrue(bar.startsWith("#"));
        Assert.assertTrue(bar.endsWith(" - bar"));
    }

    @Test
    public void testGetThreadNameCustomPatternNoCounter() throws Exception {
        context.getExecutorServiceManager().setThreadNamePattern("Cool #name#");
        String foo = context.getExecutorServiceManager().resolveThreadName("foo");
        String bar = context.getExecutorServiceManager().resolveThreadName("bar");
        Assert.assertNotSame(foo, bar);
        Assert.assertEquals("Cool foo", foo);
        Assert.assertEquals("Cool bar", bar);
    }

    @Test
    public void testGetThreadNameCustomPatternInvalid() throws Exception {
        context.getExecutorServiceManager().setThreadNamePattern("Cool #xxx#");
        try {
            context.getExecutorServiceManager().resolveThreadName("foo");
            Assert.fail("Should thrown an exception");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Pattern is invalid: Cool #xxx#", e.getMessage());
        }
        // reset it so we can shutdown properly
        context.getExecutorServiceManager().setThreadNamePattern("Camel Thread #counter# - #name#");
    }

    @Test
    public void testDefaultThreadPool() throws Exception {
        ExecutorService myPool = context.getExecutorServiceManager().newDefaultThreadPool(this, "myPool");
        Assert.assertEquals(false, myPool.isShutdown());
        // should use default settings
        ThreadPoolExecutor executor = ((ThreadPoolExecutor) (myPool));
        Assert.assertEquals(10, executor.getCorePoolSize());
        Assert.assertEquals(20, executor.getMaximumPoolSize());
        Assert.assertEquals(60, executor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals(1000, executor.getQueue().remainingCapacity());
        context.stop();
        Assert.assertEquals(true, myPool.isShutdown());
    }

    @Test
    public void testDefaultUnboundedQueueThreadPool() throws Exception {
        ThreadPoolProfile custom = new ThreadPoolProfile("custom");
        custom.setPoolSize(10);
        custom.setMaxPoolSize(30);
        custom.setKeepAliveTime(50L);
        custom.setMaxQueueSize(Integer.MAX_VALUE);
        context.getExecutorServiceManager().setDefaultThreadPoolProfile(custom);
        Assert.assertEquals(true, custom.isDefaultProfile().booleanValue());
        ExecutorService myPool = context.getExecutorServiceManager().newDefaultThreadPool(this, "myPool");
        Assert.assertEquals(false, myPool.isShutdown());
        // should use default settings
        ThreadPoolExecutor executor = ((ThreadPoolExecutor) (myPool));
        Assert.assertEquals(10, executor.getCorePoolSize());
        Assert.assertEquals(30, executor.getMaximumPoolSize());
        Assert.assertEquals(50, executor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals(Integer.MAX_VALUE, executor.getQueue().remainingCapacity());
        context.stop();
        Assert.assertEquals(true, myPool.isShutdown());
    }

    @Test
    public void testDefaultNoMaxQueueThreadPool() throws Exception {
        ThreadPoolProfile custom = new ThreadPoolProfile("custom");
        custom.setPoolSize(10);
        custom.setMaxPoolSize(30);
        custom.setKeepAliveTime(50L);
        custom.setMaxQueueSize(0);
        context.getExecutorServiceManager().setDefaultThreadPoolProfile(custom);
        Assert.assertEquals(true, custom.isDefaultProfile().booleanValue());
        ExecutorService myPool = context.getExecutorServiceManager().newDefaultThreadPool(this, "myPool");
        Assert.assertEquals(false, myPool.isShutdown());
        // should use default settings
        ThreadPoolExecutor executor = ((ThreadPoolExecutor) (myPool));
        Assert.assertEquals(10, executor.getCorePoolSize());
        Assert.assertEquals(30, executor.getMaximumPoolSize());
        Assert.assertEquals(50, executor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals(0, executor.getQueue().remainingCapacity());
        context.stop();
        Assert.assertEquals(true, myPool.isShutdown());
    }

    @Test
    public void testCustomDefaultThreadPool() throws Exception {
        ThreadPoolProfile custom = new ThreadPoolProfile("custom");
        custom.setKeepAliveTime(20L);
        custom.setMaxPoolSize(40);
        custom.setPoolSize(5);
        custom.setMaxQueueSize(2000);
        context.getExecutorServiceManager().setDefaultThreadPoolProfile(custom);
        Assert.assertEquals(true, custom.isDefaultProfile().booleanValue());
        ExecutorService myPool = context.getExecutorServiceManager().newDefaultThreadPool(this, "myPool");
        Assert.assertEquals(false, myPool.isShutdown());
        // should use default settings
        ThreadPoolExecutor executor = ((ThreadPoolExecutor) (myPool));
        Assert.assertEquals(5, executor.getCorePoolSize());
        Assert.assertEquals(40, executor.getMaximumPoolSize());
        Assert.assertEquals(20, executor.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals(2000, executor.getQueue().remainingCapacity());
        context.stop();
        Assert.assertEquals(true, myPool.isShutdown());
    }

    @Test
    public void testGetThreadPoolProfile() throws Exception {
        Assert.assertNull(context.getExecutorServiceManager().getThreadPoolProfile("foo"));
        ThreadPoolProfile foo = new ThreadPoolProfile("foo");
        foo.setKeepAliveTime(20L);
        foo.setMaxPoolSize(40);
        foo.setPoolSize(5);
        foo.setMaxQueueSize(2000);
        context.getExecutorServiceManager().registerThreadPoolProfile(foo);
        Assert.assertSame(foo, context.getExecutorServiceManager().getThreadPoolProfile("foo"));
    }

    @Test
    public void testTwoGetThreadPoolProfile() throws Exception {
        Assert.assertNull(context.getExecutorServiceManager().getThreadPoolProfile("foo"));
        ThreadPoolProfile foo = new ThreadPoolProfile("foo");
        foo.setKeepAliveTime(20L);
        foo.setMaxPoolSize(40);
        foo.setPoolSize(5);
        foo.setMaxQueueSize(2000);
        context.getExecutorServiceManager().registerThreadPoolProfile(foo);
        ThreadPoolProfile bar = new ThreadPoolProfile("bar");
        bar.setKeepAliveTime(40L);
        bar.setMaxPoolSize(5);
        bar.setPoolSize(1);
        bar.setMaxQueueSize(100);
        context.getExecutorServiceManager().registerThreadPoolProfile(bar);
        Assert.assertSame(foo, context.getExecutorServiceManager().getThreadPoolProfile("foo"));
        Assert.assertSame(bar, context.getExecutorServiceManager().getThreadPoolProfile("bar"));
        Assert.assertNotSame(foo, bar);
        Assert.assertFalse(context.getExecutorServiceManager().getThreadPoolProfile("foo").isDefaultProfile());
        Assert.assertFalse(context.getExecutorServiceManager().getThreadPoolProfile("bar").isDefaultProfile());
    }

    @Test
    public void testGetThreadPoolProfileInheritDefaultValues() throws Exception {
        Assert.assertNull(context.getExecutorServiceManager().getThreadPoolProfile("foo"));
        ThreadPoolProfile foo = new ThreadPoolProfile("foo");
        foo.setMaxPoolSize(40);
        context.getExecutorServiceManager().registerThreadPoolProfile(foo);
        Assert.assertSame(foo, context.getExecutorServiceManager().getThreadPoolProfile("foo"));
        ExecutorService executor = context.getExecutorServiceManager().newThreadPool(this, "MyPool", "foo");
        ThreadPoolExecutor tp = TestSupport.assertIsInstanceOf(ThreadPoolExecutor.class, executor);
        Assert.assertEquals(40, tp.getMaximumPoolSize());
        // should inherit the default values
        Assert.assertEquals(10, tp.getCorePoolSize());
        Assert.assertEquals(60, tp.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals("CallerRuns", tp.getRejectedExecutionHandler().toString());
    }

    @Test
    public void testGetThreadPoolProfileInheritCustomDefaultValues() throws Exception {
        ThreadPoolProfile newDefault = new ThreadPoolProfile("newDefault");
        newDefault.setKeepAliveTime(30L);
        newDefault.setMaxPoolSize(50);
        newDefault.setPoolSize(5);
        newDefault.setMaxQueueSize(2000);
        newDefault.setRejectedPolicy(Abort);
        context.getExecutorServiceManager().setDefaultThreadPoolProfile(newDefault);
        Assert.assertNull(context.getExecutorServiceManager().getThreadPoolProfile("foo"));
        ThreadPoolProfile foo = new ThreadPoolProfile("foo");
        foo.setMaxPoolSize(25);
        foo.setPoolSize(1);
        context.getExecutorServiceManager().registerThreadPoolProfile(foo);
        Assert.assertSame(foo, context.getExecutorServiceManager().getThreadPoolProfile("foo"));
        ExecutorService executor = context.getExecutorServiceManager().newThreadPool(this, "MyPool", "foo");
        ThreadPoolExecutor tp = TestSupport.assertIsInstanceOf(ThreadPoolExecutor.class, executor);
        Assert.assertEquals(25, tp.getMaximumPoolSize());
        // should inherit the default values
        Assert.assertEquals(1, tp.getCorePoolSize());
        Assert.assertEquals(30, tp.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals("Abort", tp.getRejectedExecutionHandler().toString());
    }

    @Test
    public void testGetThreadPoolProfileInheritCustomDefaultValues2() throws Exception {
        ThreadPoolProfile newDefault = new ThreadPoolProfile("newDefault");
        // just change the max pool as the default profile should then inherit the old default profile
        newDefault.setMaxPoolSize(50);
        context.getExecutorServiceManager().setDefaultThreadPoolProfile(newDefault);
        Assert.assertNull(context.getExecutorServiceManager().getThreadPoolProfile("foo"));
        ThreadPoolProfile foo = new ThreadPoolProfile("foo");
        foo.setPoolSize(1);
        context.getExecutorServiceManager().registerThreadPoolProfile(foo);
        Assert.assertSame(foo, context.getExecutorServiceManager().getThreadPoolProfile("foo"));
        ExecutorService executor = context.getExecutorServiceManager().newThreadPool(this, "MyPool", "foo");
        ThreadPoolExecutor tp = TestSupport.assertIsInstanceOf(ThreadPoolExecutor.class, executor);
        Assert.assertEquals(1, tp.getCorePoolSize());
        // should inherit the default values
        Assert.assertEquals(50, tp.getMaximumPoolSize());
        Assert.assertEquals(60, tp.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals("CallerRuns", tp.getRejectedExecutionHandler().toString());
    }

    @Test
    public void testNewThreadPoolProfile() throws Exception {
        Assert.assertNull(context.getExecutorServiceManager().getThreadPoolProfile("foo"));
        ThreadPoolProfile foo = new ThreadPoolProfile("foo");
        foo.setKeepAliveTime(20L);
        foo.setMaxPoolSize(40);
        foo.setPoolSize(5);
        foo.setMaxQueueSize(2000);
        ExecutorService pool = context.getExecutorServiceManager().newThreadPool(this, "Cool", foo);
        Assert.assertNotNull(pool);
        ThreadPoolExecutor tp = TestSupport.assertIsInstanceOf(ThreadPoolExecutor.class, pool);
        Assert.assertEquals(20, tp.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals(40, tp.getMaximumPoolSize());
        Assert.assertEquals(5, tp.getCorePoolSize());
        Assert.assertFalse(tp.isShutdown());
        context.stop();
        Assert.assertTrue(tp.isShutdown());
    }

    @Test
    public void testNewThreadPoolProfileById() throws Exception {
        Assert.assertNull(context.getExecutorServiceManager().getThreadPoolProfile("foo"));
        ThreadPoolProfile foo = new ThreadPoolProfile("foo");
        foo.setKeepAliveTime(20L);
        foo.setMaxPoolSize(40);
        foo.setPoolSize(5);
        foo.setMaxQueueSize(2000);
        context.getExecutorServiceManager().registerThreadPoolProfile(foo);
        ExecutorService pool = context.getExecutorServiceManager().newThreadPool(this, "Cool", "foo");
        Assert.assertNotNull(pool);
        ThreadPoolExecutor tp = TestSupport.assertIsInstanceOf(ThreadPoolExecutor.class, pool);
        Assert.assertEquals(20, tp.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals(40, tp.getMaximumPoolSize());
        Assert.assertEquals(5, tp.getCorePoolSize());
        Assert.assertFalse(tp.isShutdown());
        context.stop();
        Assert.assertTrue(tp.isShutdown());
    }

    @Test
    public void testNewThreadPoolMinMax() throws Exception {
        ExecutorService pool = context.getExecutorServiceManager().newThreadPool(this, "Cool", 5, 10);
        Assert.assertNotNull(pool);
        ThreadPoolExecutor tp = TestSupport.assertIsInstanceOf(ThreadPoolExecutor.class, pool);
        Assert.assertEquals(60, tp.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals(10, tp.getMaximumPoolSize());
        Assert.assertEquals(5, tp.getCorePoolSize());
        Assert.assertFalse(tp.isShutdown());
        context.stop();
        Assert.assertTrue(tp.isShutdown());
    }

    @Test
    public void testNewFixedThreadPool() throws Exception {
        ExecutorService pool = context.getExecutorServiceManager().newFixedThreadPool(this, "Cool", 5);
        Assert.assertNotNull(pool);
        ThreadPoolExecutor tp = TestSupport.assertIsInstanceOf(ThreadPoolExecutor.class, pool);
        // a fixed dont use keep alive
        Assert.assertEquals("keepAliveTime", 0, tp.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals("maximumPoolSize", 5, tp.getMaximumPoolSize());
        Assert.assertEquals(5, tp.getCorePoolSize());
        Assert.assertFalse(tp.isShutdown());
        context.stop();
        Assert.assertTrue(tp.isShutdown());
    }

    @Test
    public void testNewSingleThreadExecutor() throws Exception {
        ExecutorService pool = context.getExecutorServiceManager().newSingleThreadExecutor(this, "Cool");
        Assert.assertNotNull(pool);
        ThreadPoolExecutor tp = TestSupport.assertIsInstanceOf(ThreadPoolExecutor.class, pool);
        // a single dont use keep alive
        Assert.assertEquals("keepAliveTime", 0, tp.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals("maximumPoolSize", 1, tp.getMaximumPoolSize());
        Assert.assertEquals(1, tp.getCorePoolSize());
        Assert.assertFalse(tp.isShutdown());
        context.stop();
        Assert.assertTrue(tp.isShutdown());
    }

    @Test
    public void testNewScheduledThreadPool() throws Exception {
        ExecutorService pool = context.getExecutorServiceManager().newScheduledThreadPool(this, "Cool", 5);
        Assert.assertNotNull(pool);
        SizedScheduledExecutorService tp = TestSupport.assertIsInstanceOf(SizedScheduledExecutorService.class, pool);
        // a scheduled dont use keep alive
        Assert.assertEquals(0, tp.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals(Integer.MAX_VALUE, tp.getMaximumPoolSize());
        Assert.assertEquals(5, tp.getCorePoolSize());
        Assert.assertFalse(tp.isShutdown());
        context.stop();
        Assert.assertTrue(tp.isShutdown());
    }

    @Test
    public void testNewSingleThreadScheduledExecutor() throws Exception {
        ExecutorService pool = context.getExecutorServiceManager().newSingleThreadScheduledExecutor(this, "Cool");
        Assert.assertNotNull(pool);
        SizedScheduledExecutorService tp = TestSupport.assertIsInstanceOf(SizedScheduledExecutorService.class, pool);
        // a scheduled dont use keep alive
        Assert.assertEquals(0, tp.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals(Integer.MAX_VALUE, tp.getMaximumPoolSize());
        Assert.assertEquals(1, tp.getCorePoolSize());
        Assert.assertFalse(tp.isShutdown());
        context.stop();
        Assert.assertTrue(tp.isShutdown());
    }

    @Test
    public void testNewCachedThreadPool() throws Exception {
        ExecutorService pool = context.getExecutorServiceManager().newCachedThreadPool(this, "Cool");
        Assert.assertNotNull(pool);
        ThreadPoolExecutor tp = TestSupport.assertIsInstanceOf(ThreadPoolExecutor.class, pool);
        Assert.assertEquals(60, tp.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals(Integer.MAX_VALUE, tp.getMaximumPoolSize());
        Assert.assertEquals(0, tp.getCorePoolSize());
        Assert.assertFalse(tp.isShutdown());
        context.stop();
        Assert.assertTrue(tp.isShutdown());
    }

    @Test
    public void testNewScheduledThreadPoolProfileById() throws Exception {
        Assert.assertNull(context.getExecutorServiceManager().getThreadPoolProfile("foo"));
        ThreadPoolProfile foo = new ThreadPoolProfile("foo");
        foo.setKeepAliveTime(20L);
        foo.setMaxPoolSize(40);
        foo.setPoolSize(5);
        foo.setMaxQueueSize(2000);
        context.getExecutorServiceManager().registerThreadPoolProfile(foo);
        ExecutorService pool = context.getExecutorServiceManager().newScheduledThreadPool(this, "Cool", "foo");
        Assert.assertNotNull(pool);
        SizedScheduledExecutorService tp = TestSupport.assertIsInstanceOf(SizedScheduledExecutorService.class, pool);
        // a scheduled dont use keep alive
        Assert.assertEquals(0, tp.getKeepAliveTime(TimeUnit.SECONDS));
        Assert.assertEquals(Integer.MAX_VALUE, tp.getMaximumPoolSize());
        Assert.assertEquals(5, tp.getCorePoolSize());
        Assert.assertFalse(tp.isShutdown());
        context.stop();
        Assert.assertTrue(tp.isShutdown());
    }

    @Test
    public void testNewThread() throws Exception {
        Thread thread = context.getExecutorServiceManager().newThread("Cool", new Runnable() {
            @Override
            public void run() {
                // noop
            }
        });
        Assert.assertNotNull(thread);
        Assert.assertTrue(thread.isDaemon());
        Assert.assertTrue(thread.getName().contains("Cool"));
    }
}

