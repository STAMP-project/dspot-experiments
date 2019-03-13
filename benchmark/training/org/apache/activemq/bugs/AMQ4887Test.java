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


import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ4887Test {
    private static final transient Logger LOG = LoggerFactory.getLogger(AMQ4887Test.class);

    private static final Integer ITERATIONS = 10;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testBytesMessageSetPropertyBeforeCopy() throws Exception {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
        ActiveMQConnection connection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        connection.start();
        doTestBytesMessageSetPropertyBeforeCopy(connection);
    }

    @Test
    public void testBytesMessageSetPropertyBeforeCopyCompressed() throws Exception {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
        connectionFactory.setUseCompression(true);
        ActiveMQConnection connection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        connection.start();
        doTestBytesMessageSetPropertyBeforeCopy(connection);
    }

    @Test
    public void testStreamMessageSetPropertyBeforeCopy() throws Exception {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
        ActiveMQConnection connection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        connection.start();
        doTestStreamMessageSetPropertyBeforeCopy(connection);
    }

    @Test
    public void testStreamMessageSetPropertyBeforeCopyCompressed() throws Exception {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
        connectionFactory.setUseCompression(true);
        ActiveMQConnection connection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        connection.start();
        doTestStreamMessageSetPropertyBeforeCopy(connection);
    }
}

