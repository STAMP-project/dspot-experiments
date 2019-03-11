/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.java.util.emitter.core;


import BatchingStrategy.ONLY_EVENTS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.Ints;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class HttpEmitterTest {
    private final MockHttpClient httpClient = new MockHttpClient();

    private static final ObjectMapper objectMapper = new ObjectMapper() {
        @Override
        public byte[] writeValueAsBytes(Object value) {
            return Ints.toByteArray(((IntEvent) (value)).index);
        }
    };

    private final AtomicLong timeoutUsed = new AtomicLong();

    @Test
    public void timeoutEmptyQueue() throws IOException, InterruptedException {
        float timeoutAllowanceFactor = 2.0F;
        final HttpEmitterConfig config = new HttpEmitterConfig.Builder("http://foo.bar").setBatchingStrategy(ONLY_EVENTS).setHttpTimeoutAllowanceFactor(timeoutAllowanceFactor).build();
        final HttpPostEmitter emitter = new HttpPostEmitter(config, httpClient, HttpEmitterTest.objectMapper);
        long startMs = System.currentTimeMillis();
        emitter.start();
        emitter.emitAndReturnBatch(new IntEvent());
        emitter.flush();
        long fillTimeMs = (System.currentTimeMillis()) - startMs;
        Assert.assertThat(((double) (timeoutUsed.get())), Matchers.lessThan((fillTimeMs * (timeoutAllowanceFactor + 0.5))));
        startMs = System.currentTimeMillis();
        final Batch batch = emitter.emitAndReturnBatch(new IntEvent());
        Thread.sleep(1000);
        batch.seal();
        emitter.flush();
        fillTimeMs = (System.currentTimeMillis()) - startMs;
        Assert.assertThat(((double) (timeoutUsed.get())), Matchers.lessThan((fillTimeMs * (timeoutAllowanceFactor + 0.5))));
    }
}

