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
package org.apache.activemq.jaas;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;


public class PropertiesLoginModuleRaceConditionTest {
    private static final String GROUPS_FILE = "groups.properties";

    private static final String USERS_FILE = "users.properties";

    private static final String USERNAME = "first";

    private static final String PASSWORD = "secret";

    @Rule
    public final ErrorCollector e = new ErrorCollector();

    @Rule
    public final TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public final TestName name = new TestName();

    private Map<String, String> options;

    private BlockingQueue<Exception> errors;

    private ExecutorService pool;

    private CallbackHandler callback;

    private static class LoginTester implements Runnable {
        private final CountDownLatch finished;

        private final BlockingQueue<Exception> errors;

        private final Map<String, String> options;

        private final CountDownLatch start;

        private final CallbackHandler callback;

        LoginTester(CountDownLatch start, CountDownLatch finished, BlockingQueue<Exception> errors, Map<String, String> options, CallbackHandler callbackHandler) {
            this.finished = finished;
            this.errors = errors;
            this.options = options;
            this.start = start;
            this.callback = callbackHandler;
        }

        @Override
        public void run() {
            try {
                start.await();
                Subject subject = new Subject();
                PropertiesLoginModule module = new PropertiesLoginModule();
                module.initialize(subject, callback, new HashMap<Object, Object>(), options);
                module.login();
                module.commit();
            } catch (Exception e) {
                errors.offer(e);
            } finally {
                finished.countDown();
            }
        }
    }

    @Test
    public void raceConditionInUsersAndGroupsLoading() throws FileNotFoundException, IOException, InterruptedException {
        // Brute force approach to increase the likelihood of the race condition occurring
        for (int i = 0; i < 25000; i++) {
            final CountDownLatch start = new CountDownLatch(1);
            final CountDownLatch finished = new CountDownLatch(processorCount());
            prepareLoginThreads(start, finished);
            // Releases every login thread simultaneously to increase our chances of
            // encountering the race condition
            start.countDown();
            finished.await();
            if (isRaceConditionDetected()) {
                e.addError(new AssertionError(("At least one race condition in PropertiesLoginModule " + ("has been encountered. Please examine the " + "following stack traces for more details:"))));
                for (Exception exception : errors) {
                    e.addError(exception);
                }
                return;
            }
        }
    }
}

