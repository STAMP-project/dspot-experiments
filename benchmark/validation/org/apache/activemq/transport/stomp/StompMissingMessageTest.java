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
package org.apache.activemq.transport.stomp;


import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StompMissingMessageTest extends StompTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(StompMissingMessageTest.class);

    protected String destination;

    @Test(timeout = 60000)
    public void testProducerConsumerLoop() throws Exception {
        final int ITERATIONS = 500;
        int received = 0;
        for (int i = 1; i <= (ITERATIONS * 2); i += 2) {
            if ((doTestProducerConsumer(i)) != null) {
                received++;
            }
        }
        Assert.assertEquals(ITERATIONS, received);
    }

    @Test(timeout = 60000)
    public void testProducerDurableConsumerLoop() throws Exception {
        final int ITERATIONS = 500;
        int received = 0;
        for (int i = 1; i <= (ITERATIONS * 2); i += 2) {
            if ((doTestProducerDurableConsumer(i)) != null) {
                received++;
            }
        }
        Assert.assertEquals(ITERATIONS, received);
    }
}

