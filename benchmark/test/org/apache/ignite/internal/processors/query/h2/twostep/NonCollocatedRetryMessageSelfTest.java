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
package org.apache.ignite.internal.processors.query.h2.twostep;


import Cache.Entry;
import java.util.List;
import javax.cache.CacheException;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.internal.IgniteInterruptedCheckedException;
import org.apache.ignite.internal.managers.communication.GridIoMessage;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.apache.ignite.internal.processors.query.h2.twostep.msg.GridH2IndexRangeRequest;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.lang.IgniteInClosure;
import org.apache.ignite.plugin.extensions.communication.Message;
import org.apache.ignite.spi.IgniteSpiException;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.junit.Test;


/**
 * Failed to execute non-collocated query root cause message test
 */
public class NonCollocatedRetryMessageSelfTest extends AbstractIndexingCommonTest {
    /**
     *
     */
    private static final int NODES_COUNT = 2;

    /**
     *
     */
    private static final String ORG = "org";

    /**
     *
     */
    private static final int TEST_SQL_RETRY_TIMEOUT = 500;

    /**
     *
     */
    private String sqlRetryTimeoutBackup;

    /**
     *
     */
    private IgniteCache<String, JoinSqlTestHelper.Person> personCache;

    /**
     *
     */
    @Test
    public void testNonCollocatedRetryMessage() {
        SqlQuery<String, JoinSqlTestHelper.Person> qry = new SqlQuery<String, JoinSqlTestHelper.Person>(JoinSqlTestHelper.Person.class, JoinSqlTestHelper.JOIN_SQL).setArgs("Organization #0");
        qry.setDistributedJoins(true);
        try {
            List<Entry<String, JoinSqlTestHelper.Person>> prsns = personCache.query(qry).getAll();
            fail(("No CacheException emitted. Collection size=" + (prsns.size())));
        } catch (CacheException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("Failed to execute non-collocated query"));
        }
    }

    /**
     * TcpCommunicationSpi with additional features needed for tests.
     */
    private class TestTcpCommunication extends TcpCommunicationSpi {
        /**
         * {@inheritDoc }
         */
        @Override
        public void sendMessage(ClusterNode node, Message msg, IgniteInClosure<IgniteException> ackC) {
            assert msg != null;
            if ((igniteInstanceName.equals(getTestIgniteInstanceName(1))) && (GridIoMessage.class.isAssignableFrom(msg.getClass()))) {
                GridIoMessage gridMsg = ((GridIoMessage) (msg));
                if (GridH2IndexRangeRequest.class.isAssignableFrom(gridMsg.message().getClass())) {
                    try {
                        U.sleep(NonCollocatedRetryMessageSelfTest.TEST_SQL_RETRY_TIMEOUT);
                    } catch (IgniteInterruptedCheckedException e) {
                        fail("Test was interrupted.");
                    }
                    throw new IgniteSpiException("Test exception.");
                }
            }
            super.sendMessage(node, msg, ackC);
        }
    }
}

