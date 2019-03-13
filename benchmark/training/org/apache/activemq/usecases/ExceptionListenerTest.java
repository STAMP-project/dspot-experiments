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
package org.apache.activemq.usecases;


import java.net.URI;
import java.util.LinkedList;
import javax.jms.ExceptionListener;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Oliver Belikan
 */
public class ExceptionListenerTest implements ExceptionListener {
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionListenerTest.class);

    BrokerService brokerService;

    URI brokerUri;

    LinkedList<Throwable> exceptionsViaListener = new LinkedList<Throwable>();

    @Test
    public void fireOnSecurityException() throws Exception {
        doFireOnSecurityException(new ActiveMQConnectionFactory(brokerUri));
    }

    @Test
    public void fireOnSecurityExceptionFailover() throws Exception {
        doFireOnSecurityException(new ActiveMQConnectionFactory(("failover://" + (brokerUri))));
    }
}

