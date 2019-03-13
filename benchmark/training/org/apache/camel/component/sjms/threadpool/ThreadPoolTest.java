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
package org.apache.camel.component.sjms.threadpool;


import org.apache.camel.component.sjms.support.JmsTestSupport;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit test for CAMEL-7715.
 */
@Ignore("TODO: investigate for Camel 3.0")
public class ThreadPoolTest extends JmsTestSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolTest.class);

    private static final String FROM_ROUTE = "from";

    private static final String TO_ROUTE = "to";

    /**
     * Test that only 2 thread pools are created on start
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testContextStart() throws Exception {
        assertProducerThreadPoolCount(1);
        assertConsumerThreadPoolCount(1);
    }

    /**
     * Test that ThreadPool is removed when producer is removed
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testProducerThreadThreadPoolRemoved() throws Exception {
        context.getRouteController().stopRoute(ThreadPoolTest.FROM_ROUTE);
        assertProducerThreadPoolCount(0);
    }

    /**
     * Test that ThreadPool is removed when consumer is removed
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConsumerThreadThreadPoolRemoved() throws Exception {
        context.getRouteController().stopRoute(ThreadPoolTest.TO_ROUTE);
        assertConsumerThreadPoolCount(0);
    }
}

