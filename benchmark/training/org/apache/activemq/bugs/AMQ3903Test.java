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


import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ3903Test {
    private static final transient Logger LOG = LoggerFactory.getLogger(AMQ3903Test.class);

    private static final String bindAddress = "tcp://0.0.0.0:0";

    private BrokerService broker;

    private ActiveMQConnectionFactory cf;

    private static final int MESSAGE_COUNT = 100;

    @Test
    public void testAdvisoryForFastGenericProducer() throws Exception {
        doTestAdvisoryForFastProducer(true);
    }

    @Test
    public void testAdvisoryForFastDedicatedProducer() throws Exception {
        doTestAdvisoryForFastProducer(false);
    }
}

