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
package org.apache.activemq.jms.pool;


import javax.jms.Connection;
import javax.jms.JMSException;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Assert;
import org.junit.Test;


public class ConnectionPoolTest extends JmsPoolTestSupport {
    private class PooledConnectionFactoryTest extends PooledConnectionFactory {
        ConnectionPool pool = null;

        @Override
        protected Connection newPooledConnection(ConnectionPool connection) {
            connection.setIdleTimeout(Integer.MAX_VALUE);
            this.pool = connection;
            Connection ret = super.newPooledConnection(connection);
            ConnectionPool cp = ((PooledConnection) (ret)).pool;
            cp.decrementReferenceCount();
            // will fail if timeout does overflow
            Assert.assertFalse(cp.expiredCheck());
            return ret;
        }

        public ConnectionPool getPool() {
            return pool;
        }
    }

    @Test(timeout = 120000)
    public void demo() throws InterruptedException, JMSException {
        final ConnectionPoolTest.PooledConnectionFactoryTest pooled = new ConnectionPoolTest.PooledConnectionFactoryTest();
        pooled.setConnectionFactory(new ActiveMQConnectionFactory("vm://localhost?create=false"));
        setMaxConnections(2);
        setExpiryTimeout(Long.MAX_VALUE);
        start();
    }
}

