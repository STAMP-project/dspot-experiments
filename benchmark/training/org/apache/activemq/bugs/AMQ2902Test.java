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
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.TransportDisposedIOException;
import org.apache.activemq.util.DefaultTestAppender;
import org.apache.log4j.Appender;
import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ2902Test extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ2580Test.class);

    final AtomicBoolean gotExceptionInLog = new AtomicBoolean(Boolean.FALSE);

    final AtomicBoolean failedToFindMDC = new AtomicBoolean(Boolean.FALSE);

    Appender appender = new DefaultTestAppender() {
        @Override
        public void doAppend(LoggingEvent event) {
            if (((event.getThrowableInformation()) != null) && ((event.getThrowableInformation().getThrowable()) instanceof TransportDisposedIOException)) {
                // Prevent StackOverflowException so we can see a sane stack trace.
                if (gotExceptionInLog.get()) {
                    return;
                }
                gotExceptionInLog.set(Boolean.TRUE);
                AMQ2902Test.LOG.error(((("got event: " + event) + ", ex:") + (event.getThrowableInformation().getThrowable())), event.getThrowableInformation().getThrowable());
                AMQ2902Test.LOG.error("Event source: ", new Throwable("Here"));
            }
            if (!("Loaded the Bouncy Castle security provider.".equals(event.getMessage()))) {
                if ((event.getMDC("activemq.broker")) == null) {
                    failedToFindMDC.set(Boolean.TRUE);
                }
            }
            return;
        }
    };

    public void testNoExceptionOnClosewithStartStop() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        Connection connection = connectionFactory.createConnection();
        connection.start();
        connection.stop();
        connection.close();
    }

    public void testNoExceptionOnClose() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        Connection connection = connectionFactory.createConnection();
        connection.close();
    }
}

