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
package org.apache.camel.issues;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.TestSupport;
import org.apache.camel.support.processor.idempotent.FileIdempotentRepository;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class SedaFileIdempotentIssueTest extends ContextTestSupport {
    private final CountDownLatch latch = new CountDownLatch(1);

    private FileIdempotentRepository repository = new FileIdempotentRepository();

    @Test
    public void testRepo() throws Exception {
        boolean done = latch.await(10, TimeUnit.SECONDS);
        Assert.assertTrue("Should stop Camel", done);
        Assert.assertEquals("No file should be reported consumed", 0, repository.getCache().keySet().size());
    }

    protected class ShutDown implements Processor {
        @Override
        public void process(final Exchange exchange) throws Exception {
            // shutdown route
            Thread thread = new Thread() {
                @Override
                public void run() {
                    // shutdown camel
                    try {
                        log.info("Stopping Camel");
                        exchange.getContext().stop();
                        log.info("Stopped Camel complete");
                        latch.countDown();
                    } catch (Exception e) {
                        // ignore
                        e.printStackTrace();
                    }
                }
            };
            // start shutdown in a separate thread
            thread.start();
        }
    }
}

