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
package org.apache.activemq.config;


import java.io.File;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.store.jdbc.JDBCPersistenceAdapter;
import org.apache.activemq.wireformat.ObjectStreamWireFormat;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;


public class JDBCConfigTest {
    protected static final String JOURNAL_ROOT = "target/test-data/";

    protected static final String DERBY_ROOT = "target/test-data/";

    protected static final String CONF_ROOT = "src/test/resources/org/apache/activemq/config/sample-conf/";

    private static final Logger LOG = LoggerFactory.getLogger(JDBCConfigTest.class);

    /* This tests creating a jdbc persistence adapter using xbeans-spring */
    @Test
    public void testJdbcConfig() throws Exception {
        File journalFile = new File(((JDBCConfigTest.JOURNAL_ROOT) + "testJDBCConfig/journal"));
        JDBCConfigTest.recursiveDelete(journalFile);
        File derbyFile = new File(((JDBCConfigTest.DERBY_ROOT) + "testJDBCConfig/derbydb"));// Default

        JDBCConfigTest.recursiveDelete(derbyFile);
        BrokerService broker;
        broker = createBroker(new FileSystemResource(((JDBCConfigTest.CONF_ROOT) + "jdbc-example.xml")));
        try {
            Assert.assertEquals("Broker Config Error (brokerName)", "brokerJdbcConfigTest", broker.getBrokerName());
            PersistenceAdapter adapter = broker.getPersistenceAdapter();
            Assert.assertTrue("Should have created a jdbc persistence adapter", (adapter instanceof JDBCPersistenceAdapter));
            Assert.assertEquals("JDBC Adapter Config Error (cleanupPeriod)", 60000, getCleanupPeriod());
            Assert.assertTrue("Should have created an EmbeddedDataSource", ((getDataSource()) instanceof EmbeddedDataSource));
            Assert.assertTrue("Should have created a DefaultWireFormat", ((getWireFormat()) instanceof ObjectStreamWireFormat));
            JDBCConfigTest.LOG.info("Success");
        } finally {
            if (broker != null) {
                broker.stop();
            }
        }
    }
}

