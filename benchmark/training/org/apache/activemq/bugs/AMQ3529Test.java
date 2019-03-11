/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.bugs;


import Session.AUTO_ACKNOWLEDGE;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ3529Test {
    private static Logger LOG = LoggerFactory.getLogger(AMQ3529Test.class);

    private ConnectionFactory connectionFactory;

    private Connection connection;

    private Session session;

    private BrokerService broker;

    private String connectionUri;

    private MessageConsumer consumer;

    private Context ctx = null;

    @Test(timeout = 60000)
    public void testInterruptionAffects() throws Exception {
        ThreadGroup tg = new ThreadGroup("tg");
        Assert.assertEquals(0, tg.activeCount());
        Thread client = new Thread(tg, "client") {
            @Override
            public void run() {
                try {
                    connection = connectionFactory.createConnection();
                    session = connection.createSession(false, AUTO_ACKNOWLEDGE);
                    Assert.assertNotNull(session);
                    Properties props = new Properties();
                    props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
                    props.setProperty(Context.PROVIDER_URL, "tcp://0.0.0.0:0");
                    ctx = null;
                    try {
                        ctx = new InitialContext(props);
                    } catch (NoClassDefFoundError e) {
                        throw new NamingException(e.toString());
                    } catch (Exception e) {
                        throw new NamingException(e.toString());
                    }
                    Destination destination = ((Destination) (ctx.lookup("dynamicTopics/example.C")));
                    consumer = session.createConsumer(destination);
                    consumer.receive(10000);
                } catch (Exception e) {
                    // Expect an exception here from the interrupt.
                } finally {
                    // next line is the nature of the test, if I remove this
                    // line, everything works OK
                    try {
                        consumer.close();
                    } catch (JMSException e) {
                        Assert.fail(("Consumer Close failed with" + (e.getMessage())));
                    }
                    try {
                        session.close();
                    } catch (JMSException e) {
                        Assert.fail(("Session Close failed with" + (e.getMessage())));
                    }
                    try {
                        connection.close();
                    } catch (JMSException e) {
                        Assert.fail(("Connection Close failed with" + (e.getMessage())));
                    }
                    try {
                        ctx.close();
                    } catch (Exception e) {
                        Assert.fail(("Connection Close failed with" + (e.getMessage())));
                    }
                }
            }
        };
        client.start();
        Thread.sleep(5000);
        client.interrupt();
        client.join();
        Thread.sleep(2000);
        Thread[] remainThreads = new Thread[tg.activeCount()];
        tg.enumerate(remainThreads);
        for (Thread t : remainThreads) {
            if ((t.isAlive()) && (!(t.isDaemon())))
                Assert.fail(("Remaining thread: " + (t.toString())));

        }
        ThreadGroup root = Thread.currentThread().getThreadGroup().getParent();
        while ((root.getParent()) != null) {
            root = root.getParent();
        } 
        AMQ3529Test.visit(root, 0);
    }
}

