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
package org.apache.activemq.bugs;


import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.kahadb.MessageDatabase;
import org.apache.activemq.util.DefaultTestAppender;
import org.apache.activemq.util.Wait;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.log4j.Logger.getLogger;


public class AMQ6432Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ6432Test.class);

    private static final String QUEUE_NAME = "test.queue";

    private BrokerService broker;

    @Test
    public void testTransactedStoreUsageSuspendResume() throws Exception {
        org.apache.log4j.Logger log4jLogger = getLogger(MessageDatabase.class);
        final AtomicBoolean failed = new AtomicBoolean(false);
        final File journalDataDir = getStore().getJournal().getDirectory();
        Appender appender = new DefaultTestAppender() {
            @Override
            public void doAppend(LoggingEvent event) {
                if ((event.getLevel().equals(Level.WARN)) && (event.getMessage().toString().startsWith("Failed to load next journal"))) {
                    AMQ6432Test.LOG.info(("received unexpected log message: " + (event.getMessage())));
                    failed.set(true);
                }
            }
        };
        log4jLogger.addAppender(appender);
        try {
            ExecutorService sendExecutor = Executors.newSingleThreadExecutor();
            sendExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        sendReceive(10000);
                    } catch (Exception ignored) {
                    }
                }
            });
            sendExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        sendLargeAndPurge(5000);
                    } catch (Exception ignored) {
                    }
                }
            });
            sendExecutor.shutdown();
            sendExecutor.awaitTermination(10, TimeUnit.MINUTES);
            // need to let a few gc cycles to complete then there will be 2 files in the mix and acks will move
            TimeUnit.SECONDS.sleep(2);
            Assert.assertTrue("gc worked ok", Wait.waitFor(new Wait.Condition() {
                @Override
                public boolean isSatisified() throws Exception {
                    return (getStore().getJournal().getFileMap().size()) < 3;
                }
            }));
        } finally {
            log4jLogger.removeAppender(appender);
        }
        Assert.assertFalse("failed on unexpected log event", failed.get());
        sendReceive(500);
        Assert.assertTrue("gc worked ok", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getStore().getJournal().getFileMap().size()) < 2;
            }
        }));
        // file actually gone!
        AMQ6432Test.LOG.info(("Files: " + (Arrays.asList(journalDataDir.listFiles()))));
        Assert.assertTrue("Minimum data files in the mix", ((journalDataDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("db-");
            }
        }).length) == 1));
    }
}

