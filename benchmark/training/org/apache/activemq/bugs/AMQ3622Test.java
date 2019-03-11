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


import Stomp.Headers.Subscribe.AckModeValues.CLIENT;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.transport.stomp.StompConnection;
import org.apache.activemq.util.DefaultTestAppender;
import org.apache.log4j.Appender;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Test;


public class AMQ3622Test {
    protected BrokerService broker;

    protected AtomicBoolean failed = new AtomicBoolean(false);

    protected String connectionUri;

    protected Appender appender = new DefaultTestAppender() {
        @Override
        public void doAppend(LoggingEvent event) {
            System.err.println(event.getMessage());
            if ((event.getThrowableInformation()) != null) {
                if ((event.getThrowableInformation().getThrowable()) instanceof NullPointerException) {
                    failed.set(true);
                }
            }
        }
    };

    @Test
    public void go() throws Exception {
        StompConnection connection = new StompConnection();
        Integer port = Integer.parseInt(connectionUri.split(":")[2]);
        connection.open("localhost", port);
        connection.connect("", "");
        connection.subscribe("/topic/foobar", CLIENT);
        connection.disconnect();
        Thread.sleep(1000);
        if (failed.get()) {
            Assert.fail("Received NullPointerException");
        }
    }
}

