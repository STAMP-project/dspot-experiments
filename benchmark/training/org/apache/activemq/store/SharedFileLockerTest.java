/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.store;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.activemq.broker.LockableServiceSupport;
import org.apache.activemq.broker.Locker;
import org.apache.activemq.util.DefaultTestAppender;
import org.apache.activemq.util.IOHelper;
import org.apache.activemq.util.LockFile;
import org.apache.activemq.util.ServiceStopper;
import org.apache.activemq.util.Wait;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.log4j.Logger.getLogger;


public class SharedFileLockerTest {
    private static final Logger LOG = LoggerFactory.getLogger(SharedFileLockerTest.class);

    @Rule
    public TemporaryFolder testFolder;

    public SharedFileLockerTest() {
        File file = new File(IOHelper.getDefaultDataDirectory());
        file.mkdir();
        // TemporaryFolder will make sure the files are removed after the test is done
        testFolder = new TemporaryFolder(file);
    }

    @Test
    public void testStopNoStart() throws Exception {
        SharedFileLocker locker1 = new SharedFileLocker();
        locker1.setDirectory(testFolder.getRoot());
        locker1.stop();
    }

    @Test
    public void testLoop() throws Exception {
        // Increase the number of iterations if you are debugging races
        for (int i = 0; i < 100; i++) {
            internalLoop(5);
        }
    }

    @Test
    public void testLogging() throws Exception {
        // using a bigger wait here
        // to make sure we won't log any extra info
        internalLoop(100);
    }

    @Test
    public void verifyLockAcquireWaitsForLockDrop() throws Exception {
        final AtomicInteger logCounts = new AtomicInteger(0);
        DefaultTestAppender appender = new DefaultTestAppender() {
            @Override
            public void doAppend(LoggingEvent event) {
                logCounts.incrementAndGet();
            }
        };
        org.apache.log4j.Logger sharedFileLogger = getLogger(SharedFileLocker.class);
        sharedFileLogger.addAppender(appender);
        LockableServiceSupport config = new LockableServiceSupport() {
            @Override
            public long getLockKeepAlivePeriod() {
                return 500;
            }

            @Override
            public Locker createDefaultLocker() throws IOException {
                return null;
            }

            public void init() throws Exception {
            }

            protected void doStop(ServiceStopper stopper) throws Exception {
            }

            protected void doStart() throws Exception {
            }
        };
        final SharedFileLocker underTest = new SharedFileLocker();
        underTest.setDirectory(testFolder.getRoot());
        underTest.setLockAcquireSleepInterval(5);
        underTest.setLockable(config);
        // get the in jvm lock
        File lockFile = new File(testFolder.getRoot(), "lock");
        String jvmProp = ((LockFile.class.getName()) + ".lock.") + (lockFile.getCanonicalPath());
        System.getProperties().put(jvmProp, jvmProp);
        final CountDownLatch locked = new CountDownLatch(1);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            final AtomicLong acquireTime = new AtomicLong(0L);
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        underTest.start();
                        acquireTime.set(System.currentTimeMillis());
                        locked.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            Assert.assertTrue("locker failed to obtain lock", Wait.waitFor(new Wait.Condition() {
                @Override
                public boolean isSatisified() throws Exception {
                    return (logCounts.get()) > 0;
                }
            }, 5000, 10));
            // release vm lock
            long releaseTime = System.currentTimeMillis();
            System.getProperties().remove(jvmProp);
            Assert.assertTrue("locker got lock", locked.await(5, TimeUnit.SECONDS));
            // verify delay in start
            SharedFileLockerTest.LOG.info(((("ReleaseTime: " + releaseTime) + ", AcquireTime:") + (acquireTime.get())));
            Assert.assertTrue(("acquire delayed for keepAlive: " + (config.getLockKeepAlivePeriod())), ((acquireTime.get()) >= (releaseTime + (config.getLockKeepAlivePeriod()))));
        } finally {
            executorService.shutdownNow();
            underTest.stop();
            lockFile.delete();
            sharedFileLogger.removeAppender(appender);
        }
    }
}

