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


import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.activemq.broker.BrokerService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ2171Test implements Thread.UncaughtExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ2171Test.class);

    private static final String BROKER_URL = "tcp://localhost:0";

    private static final int QUEUE_SIZE = 100;

    private static BrokerService brokerService;

    private static Queue destination;

    private String brokerUri;

    private String brokerUriNoPrefetch;

    private Collection<Throwable> exceptions = new CopyOnWriteArrayList<Throwable>();

    @Test(timeout = 10000)
    public void testBrowsePrefetch() throws Exception {
        runTest(brokerUri);
    }

    @Test(timeout = 10000)
    public void testBrowseNoPrefetch() throws Exception {
        runTest(brokerUriNoPrefetch);
    }
}

