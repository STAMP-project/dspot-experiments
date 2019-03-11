/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.store.jdbc;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.jms.MessageProducer;
import javax.jms.XASession;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQXAConnection;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.TestUtils;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.Test;


public class JDBCCleanupLimitedPoolTest {
    BrokerService broker;

    JDBCPersistenceAdapter jdbcPersistenceAdapter;

    BasicDataSource pool;

    EmbeddedDataSource derby;

    @Test
    public void testNoDeadlockOnXaPoolExhaustion() throws Exception {
        final CountDownLatch done = new CountDownLatch(1);
        final CountDownLatch doneCommit = new CountDownLatch(1000);
        final ActiveMQXAConnectionFactory factory = new ActiveMQXAConnectionFactory(broker.getTransportConnectorByScheme("tcp").getPublishableConnectString());
        ExecutorService executorService = Executors.newCachedThreadPool();
        // some contention over pool of 2
        for (int i = 0; i < 3; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        ActiveMQXAConnection conn = ((ActiveMQXAConnection) (factory.createXAConnection()));
                        conn.start();
                        XASession sess = conn.createXASession();
                        while (((done.getCount()) > 0) && ((doneCommit.getCount()) > 0)) {
                            Xid xid = TestUtils.createXid();
                            sess.getXAResource().start(xid, XAResource.TMNOFLAGS);
                            MessageProducer producer = sess.createProducer(sess.createQueue("test"));
                            producer.send(sess.createTextMessage("test"));
                            sess.getXAResource().end(xid, XAResource.TMSUCCESS);
                            sess.getXAResource().prepare(xid);
                            sess.getXAResource().commit(xid, false);
                            doneCommit.countDown();
                        } 
                        conn.close();
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
            });
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while ((!(done.await(10, TimeUnit.MILLISECONDS))) && ((doneCommit.getCount()) > 0)) {
                        jdbcPersistenceAdapter.cleanup();
                    } 
                } catch (Exception ignored) {
                }
            }
        });
        executorService.shutdown();
        boolean allComplete = executorService.awaitTermination(40, TimeUnit.SECONDS);
        if (!allComplete) {
            dumpAllThreads((("Why-at-count-" + (doneCommit.getCount())) + "-"));
        }
        done.countDown();
        TestCase.assertTrue("all complete", allComplete);
        executorService.shutdownNow();
        TestCase.assertTrue("xa tx done", doneCommit.await(10, TimeUnit.SECONDS));
    }
}

