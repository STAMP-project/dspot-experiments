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
package org.apache.camel.component.file.strategy;


import Exchange.FILE_NAME;
import java.util.concurrent.TimeUnit;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.processor.idempotent.MemoryIdempotentRepository;
import org.junit.Assert;
import org.junit.Test;


public class FileIdempotentReadLockDelayedAsyncTest extends ContextTestSupport {
    MemoryIdempotentRepository myRepo = new MemoryIdempotentRepository();

    @Test
    public void testIdempotentReadLock() throws Exception {
        Assert.assertEquals(0, myRepo.getCacheSize());
        NotifyBuilder notify = whenDone(2).create();
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(2);
        // both of them should be done without 2 sequential delays of 1 sec
        mock.message(0).arrives().between(0, 1400).millis();
        mock.message(1).arrives().between(0, 1400).millis();
        template.sendBodyAndHeader("file:target/data/changed/in", "Hello World", FILE_NAME, "hello.txt");
        template.sendBodyAndHeader("file:target/data/changed/in", "Bye World", FILE_NAME, "bye.txt");
        assertMockEndpointsSatisfied();
        Assert.assertTrue(notify.matches(10, TimeUnit.SECONDS));
        // the files are kept on commit
        // if you want to remove them then the idempotent repo need some way to evict idle keys
        Assert.assertEquals(2, myRepo.getCacheSize());
    }
}

