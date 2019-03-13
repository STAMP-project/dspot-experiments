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


import java.util.concurrent.atomic.AtomicBoolean;
import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.DefaultTestAppender;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.log4j.Logger.getLogger;


/**
 *
 *
 * @author Claudio Corsi
 */
public class AMQ3567Test {
    private static Logger logger = LoggerFactory.getLogger(AMQ3567Test.class);

    private ActiveMQConnectionFactory factory;

    private Connection connection;

    private Session sessionWithListener;

    private Session session;

    private Queue destination;

    private MessageConsumer consumer;

    private Thread thread;

    private BrokerService broker;

    private String connectionUri;

    @Test
    public void runTest() throws Exception {
        produceSingleMessage();
        org.apache.log4j.Logger log4jLogger = getLogger("org.apache.activemq.util.ServiceSupport");
        final AtomicBoolean failed = new AtomicBoolean(false);
        Appender appender = new DefaultTestAppender() {
            @Override
            public void doAppend(LoggingEvent event) {
                if ((event.getThrowableInformation()) != null) {
                    if ((event.getThrowableInformation().getThrowable()) instanceof InterruptedException) {
                        InterruptedException ie = ((InterruptedException) (event.getThrowableInformation().getThrowable()));
                        if (ie.getMessage().startsWith("Could not stop service:")) {
                            AMQ3567Test.logger.info("Received an interrupted exception : ", ie);
                            failed.set(true);
                        }
                    }
                }
            }
        };
        log4jLogger.addAppender(appender);
        Level level = log4jLogger.getLevel();
        log4jLogger.setLevel(Level.DEBUG);
        try {
            stopConsumer();
            stopBroker();
            if (failed.get()) {
                Assert.fail("An Interrupt exception was generated");
            }
        } finally {
            log4jLogger.setLevel(level);
            log4jLogger.removeAppender(appender);
        }
    }
}

