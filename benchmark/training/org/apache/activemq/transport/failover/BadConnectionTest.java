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
package org.apache.activemq.transport.failover;


import java.io.IOException;
import junit.framework.TestCase;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.transport.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class BadConnectionTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(BadConnectionTest.class);

    protected Transport transport;

    public void testConnectingToUnavailableServer() throws Exception {
        try {
            transport.asyncRequest(new ActiveMQMessage(), null);
            TestCase.fail("This should never succeed");
        } catch (IOException e) {
            BadConnectionTest.LOG.info(("Caught expected exception: " + e), e);
        }
    }
}

