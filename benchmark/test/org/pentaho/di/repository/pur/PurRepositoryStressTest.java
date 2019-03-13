/**
 * !
 * Copyright 2010 - 2018 Hitachi Vantara.  All rights reserved.
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
package org.pentaho.di.repository.pur;


import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.di.core.DBCache;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.repository.IUser;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.RepositoryFile;
import org.pentaho.platform.repository2.ClientRepositoryPaths;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ ClientRepositoryPaths.class, Encr.class, AttributesMapUtil.class, DBCache.class })
public class PurRepositoryStressTest {
    private final int THREADS = 15;

    private final int SAMPLES = 250;

    private final int TIMEOUT = 30;

    private final int METHOD_CALLS_BY_SAMPLE = 30;

    private PurRepository purRepository;

    private IUnifiedRepository mockRepo;

    private RepositoryConnectResult result;

    private IRepositoryConnector connector;

    private PurRepositoryMeta mockMeta;

    private RepositoryFile mockRootFolder;

    private IUser user;

    private List<Method> testLockMethods = Arrays.asList(PurRepositoryStressTest.class.getMethods()).stream().filter(( line) -> line.getName().startsWith("testLock")).collect(Collectors.toList());

    private PurRepositoryStressTest obj;

    private class Flow implements Runnable {
        boolean success = false;

        Throwable t = null;

        int nr;

        public Flow(int nr) {
            this.nr = nr;
        }

        @Override
        public void run() {
            try {
                System.out.println(((("[PurRepositoryStressTest_" + (Thread.currentThread().getName())) + "]-> Starting Sample - ") + (this.nr)));
                Random randomGenerator = new Random();
                for (int i = 0; i < (METHOD_CALLS_BY_SAMPLE); i++) {
                    int index = randomGenerator.nextInt(testLockMethods.size());
                    Method item = testLockMethods.get(index);
                    item.invoke(obj);
                }
                System.out.println(((("[PurRepositoryStressTest_" + (Thread.currentThread().getName())) + "]-> Finishing Sample - ") + (this.nr)));
            } catch (Throwable e) {
                this.t = e;
                return;
            }
            success = true;
        }
    }

    @Test
    public void runLoadTest() throws Exception {
        obj = new PurRepositoryStressTest();
        obj.setUpTest();
        final ThreadFactory factory = new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(0);

            public Thread newThread(Runnable r) {
                final Thread t = Executors.defaultThreadFactory().newThread(r);
                t.setDaemon(true);
                t.setName((("THREAD" + '_') + (counter.incrementAndGet())));
                return t;
            }
        };
        final ScheduledExecutorService exec = Executors.newScheduledThreadPool(THREADS, factory);
        Map<Future, PurRepositoryStressTest.Flow> futures = new ConcurrentHashMap<Future, PurRepositoryStressTest.Flow>();
        for (int i = 0; i < (SAMPLES); i++) {
            PurRepositoryStressTest.Flow mr = new PurRepositoryStressTest.Flow(i);
            futures.put(exec.submit(mr), mr);
        }
        try {
            exec.shutdown();
            exec.awaitTermination(TIMEOUT, TimeUnit.SECONDS);
            AtomicBoolean success = new AtomicBoolean(true);
            futures.entrySet().forEach(( entry) -> {
                try {
                    entry.getKey().get();
                } catch (InterruptedException e) {
                    success.set(false);
                    Assert.fail("Interrupted future");
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    success.set(false);
                    Assert.fail("Execution exception");
                    e.printStackTrace();
                }
                if (!(entry.getValue().success)) {
                    success.set(false);
                    entry.getValue().t.printStackTrace();
                }
            });
            Assert.assertTrue(success.get());
        } catch (InterruptedException e) {
            Assert.fail("Did not complete.");
        }
    }
}

