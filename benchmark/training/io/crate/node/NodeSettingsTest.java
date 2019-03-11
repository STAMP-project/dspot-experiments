/**
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.node;


import SQLTransportExecutor.REQUEST_TIMEOUT;
import io.crate.action.sql.SQLOperations;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.SQLResponse;
import io.crate.testing.SQLTransportExecutor;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class NodeSettingsTest extends CrateUnitTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private CrateNode node;

    private boolean loggingConfigured = false;

    private SQLOperations sqlOperations;

    /**
     * The default cluster name is "crate" if not set differently in crate settings
     */
    @Test
    public void testClusterName() {
        SQLResponse response = SQLTransportExecutor.execute("select name from sys.cluster", new Object[0], sqlOperations.newSystemSession()).actionGet(REQUEST_TIMEOUT);
        assertThat(response.rows()[0][0], Matchers.is("crate"));
    }

    @Test
    public void testDefaultPaths() {
        assertThat(PATH_DATA_SETTING.get(node.settings()), Matchers.contains(Matchers.endsWith("data")));
        assertTrue(node.settings().get(PATH_LOGS_SETTING.getKey()).endsWith("logs"));
    }

    @Test
    public void testDefaultPorts() {
        assertEquals(Constants.HTTP_PORT_RANGE, node.settings().get("http.port"));
        assertEquals(Constants.TRANSPORT_PORT_RANGE, node.settings().get("transport.tcp.port"));
    }
}

