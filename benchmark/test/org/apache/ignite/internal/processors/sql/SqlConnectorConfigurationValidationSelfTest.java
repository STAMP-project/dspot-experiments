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
package org.apache.ignite.internal.processors.sql;


import SqlConnectorConfiguration.DFLT_PORT;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.configuration.SqlConnectorConfiguration;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.junit.Test;


/**
 * SQL connector configuration validation tests.
 */
@SuppressWarnings("deprecation")
public class SqlConnectorConfigurationValidationSelfTest extends AbstractIndexingCommonTest {
    /**
     * Node index generator.
     */
    private static final AtomicInteger NODE_IDX_GEN = new AtomicInteger();

    /**
     * Cache name.
     */
    private static final String CACHE_NAME = "CACHE";

    /**
     * Test host.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDefault() throws Exception {
        check(new SqlConnectorConfiguration(), true);
        assertJdbc(null, DFLT_PORT);
    }

    /**
     * Test host.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testHost() throws Exception {
        check(new SqlConnectorConfiguration().setHost("126.0.0.1"), false);
        check(new SqlConnectorConfiguration().setHost("127.0.0.1"), true);
        assertJdbc("127.0.0.1", DFLT_PORT);
        check(new SqlConnectorConfiguration().setHost("0.0.0.0"), true);
        assertJdbc("0.0.0.0", ((SqlConnectorConfiguration.DFLT_PORT) + 1));
    }

    /**
     * Test port.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPort() throws Exception {
        check(new SqlConnectorConfiguration().setPort((-1)), false);
        check(new SqlConnectorConfiguration().setPort(0), false);
        check(new SqlConnectorConfiguration().setPort(512), false);
        check(new SqlConnectorConfiguration().setPort(65536), false);
        check(new SqlConnectorConfiguration().setPort(DFLT_PORT), true);
        assertJdbc(null, DFLT_PORT);
        check(new SqlConnectorConfiguration().setPort(((SqlConnectorConfiguration.DFLT_PORT) + 200)), true);
        assertJdbc(null, ((SqlConnectorConfiguration.DFLT_PORT) + 200));
    }

    /**
     * Test port.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPortRange() throws Exception {
        check(new SqlConnectorConfiguration().setPortRange((-1)), false);
        check(new SqlConnectorConfiguration().setPortRange(0), true);
        assertJdbc(null, DFLT_PORT);
        check(new SqlConnectorConfiguration().setPortRange(10), true);
        assertJdbc(null, ((SqlConnectorConfiguration.DFLT_PORT) + 1));
    }

    /**
     * Test socket buffers.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSocketBuffers() throws Exception {
        check(new SqlConnectorConfiguration().setSocketSendBufferSize(((-4) * 1024)), false);
        check(new SqlConnectorConfiguration().setSocketReceiveBufferSize(((-4) * 1024)), false);
        check(new SqlConnectorConfiguration().setSocketSendBufferSize((4 * 1024)), true);
        assertJdbc(null, DFLT_PORT);
        check(new SqlConnectorConfiguration().setSocketReceiveBufferSize((4 * 1024)), true);
        assertJdbc(null, ((SqlConnectorConfiguration.DFLT_PORT) + 1));
    }

    /**
     * Test max open cursors per connection.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMaxOpenCusrorsPerConnection() throws Exception {
        check(new SqlConnectorConfiguration().setMaxOpenCursorsPerConnection((-1)), false);
        check(new SqlConnectorConfiguration().setMaxOpenCursorsPerConnection(0), true);
        assertJdbc(null, DFLT_PORT);
        check(new SqlConnectorConfiguration().setMaxOpenCursorsPerConnection(100), true);
        assertJdbc(null, ((SqlConnectorConfiguration.DFLT_PORT) + 1));
    }

    /**
     * Test thread pool size.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testThreadPoolSize() throws Exception {
        check(new SqlConnectorConfiguration().setThreadPoolSize(0), false);
        check(new SqlConnectorConfiguration().setThreadPoolSize((-1)), false);
        check(new SqlConnectorConfiguration().setThreadPoolSize(4), true);
        assertJdbc(null, DFLT_PORT);
    }

    /**
     * Key class.
     */
    private static class SqlConnectorKey {
        @QuerySqlField
        public int key;
    }

    /**
     * Value class.
     */
    private static class SqlConnectorValue {
        @QuerySqlField
        public int val;
    }
}

