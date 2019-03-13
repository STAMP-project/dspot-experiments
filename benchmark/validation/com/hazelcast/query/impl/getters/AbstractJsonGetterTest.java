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
package com.hazelcast.query.impl.getters;


import com.fasterxml.jackson.core.JsonFactory;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.internal.json.Json;
import com.hazelcast.json.HazelcastJson;
import com.hazelcast.json.internal.JsonSchemaHelper;
import com.hazelcast.json.internal.JsonSchemaNode;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * AbstractJsonGetter uses a lot of caching and guessing based on previously
 * encountered queries. Therefore, the first query and the subsequent
 * queries are likely to follow different execution path. This test is
 * to ensure that AbstractJsonGetter generates the same results for repeat
 * executions of the same query.
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AbstractJsonGetterTest {
    private AbstractJsonGetter getter = new JsonGetter();

    private JsonFactory factory = new JsonFactory();

    @Test
    public void testRepeatQueriesUseTheCachedContext() throws Exception {
        String jsonText = Json.object().add("at1", "val1").add("at2", "val2").toString();
        HazelcastJsonValue jsonValue = HazelcastJson.fromString(jsonText);
        JsonSchemaNode node = JsonSchemaHelper.createSchema(factory.createParser(jsonText));
        TestCase.assertEquals("val1", getter.getValue(jsonValue, "at1", node));
        TestCase.assertEquals("val1", getter.getValue(jsonValue, "at1", node));
        TestCase.assertEquals("val1", getter.getValue(jsonValue, "at1", node));
        TestCase.assertEquals("val1", getter.getValue(jsonValue, "at1", node));
        TestCase.assertEquals(1, getter.getContextCacheSize());
    }

    @Test
    public void testDifferentQueriesCreateNewContexts() throws Exception {
        String jsonText = Json.object().add("at1", "val1").add("at2", "val2").toString();
        HazelcastJsonValue jsonValue = HazelcastJson.fromString(jsonText);
        JsonSchemaNode node = JsonSchemaHelper.createSchema(factory.createParser(jsonText));
        TestCase.assertEquals("val1", getter.getValue(jsonValue, "at1", node));
        TestCase.assertEquals("val1", getter.getValue(jsonValue, "at1", node));
        TestCase.assertEquals("val1", getter.getValue(jsonValue, "at1", node));
        TestCase.assertEquals("val1", getter.getValue(jsonValue, "at1", node));
        TestCase.assertEquals("val2", getter.getValue(jsonValue, "at2", node));
        TestCase.assertEquals("val2", getter.getValue(jsonValue, "at2", node));
        TestCase.assertEquals("val2", getter.getValue(jsonValue, "at2", node));
        TestCase.assertEquals("val2", getter.getValue(jsonValue, "at2", node));
        TestCase.assertEquals(2, getter.getContextCacheSize());
    }

    @Test
    public void testQueryObjectsWithDifferentPatterns() throws Exception {
        testRandomOrderObjectRepetitiveQuerying(100);
    }

    @Test
    public void testMultithreadedGetter() throws InterruptedException {
        int numberOfThreads = 5;
        Thread[] threads = new Thread[numberOfThreads];
        AbstractJsonGetterTest.GetterRunner[] getterRunners = new AbstractJsonGetterTest.GetterRunner[numberOfThreads];
        AtomicBoolean running = new AtomicBoolean();
        for (int i = 0; i < numberOfThreads; i++) {
            getterRunners[i] = new AbstractJsonGetterTest.GetterRunner(running);
            threads[i] = new Thread(getterRunners[i]);
            threads[i].start();
        }
        running.set(true);
        HazelcastTestSupport.sleepAtLeastSeconds(5);
        running.set(false);
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i].join();
        }
        for (int i = 0; i < numberOfThreads; i++) {
            AbstractJsonGetterTest.GetterRunner current = getterRunners[i];
            TestCase.assertFalse(current.getStackTrace(), getterRunners[i].isFailed);
        }
    }

    private class GetterRunner implements Runnable {
        private AtomicBoolean running;

        private boolean isFailed;

        private Throwable exception;

        public GetterRunner(AtomicBoolean running) {
            this.running = running;
        }

        public boolean isFailed() {
            return isFailed;
        }

        public Throwable getThrowable() {
            return exception;
        }

        public String getStackTrace() {
            if ((exception) != null) {
                return Arrays.toString(exception.getStackTrace());
            } else {
                return null;
            }
        }

        @Override
        public void run() {
            while (running.get()) {
                try {
                    testRandomOrderObjectRepetitiveQuerying(10);
                } catch (Throwable e) {
                    exception = e;
                    isFailed = true;
                    running.set(false);
                }
            } 
        }
    }
}

