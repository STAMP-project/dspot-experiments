/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.client;


import CoordinationProtos.DrillbitEndpoint;
import ExecConstants.INITIAL_USER_PORT;
import java.util.List;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.exec.DrillSystemTestBase;
import org.apache.drill.exec.proto.CoordinationProtos;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The unit test case will read a physical plan in json format. The physical plan contains a "trace" operator,
 * which will produce a dump file.  The dump file will be input into DumpCat to test query mode and batch mode.
 */
public class DrillClientTest extends DrillSystemTestBase {
    private static final Logger logger = LoggerFactory.getLogger(DrillClientTest.class);

    private final DrillConfig config = DrillConfig.create();

    @Test
    public void testParseAndVerifyEndpointsSingleDrillbitIp() throws Exception {
        // Test with single drillbit ip
        final String drillBitConnection = "10.10.100.161";
        final List<CoordinationProtos.DrillbitEndpoint> endpointsList = DrillClient.parseAndVerifyEndpoints(drillBitConnection, config.getString(INITIAL_USER_PORT));
        final CoordinationProtos.DrillbitEndpoint endpoint = endpointsList.get(0);
        Assert.assertEquals(endpointsList.size(), 1);
        Assert.assertEquals(endpoint.getAddress(), drillBitConnection);
        Assert.assertEquals(endpoint.getUserPort(), config.getInt(INITIAL_USER_PORT));
    }

    @Test
    public void testParseAndVerifyEndpointsSingleDrillbitIpPort() throws Exception {
        // Test with single drillbit ip:port
        final String drillBitConnection = "10.10.100.161:5000";
        final String[] ipAndPort = drillBitConnection.split(":");
        final List<CoordinationProtos.DrillbitEndpoint> endpointsList = DrillClient.parseAndVerifyEndpoints(drillBitConnection, config.getString(INITIAL_USER_PORT));
        Assert.assertEquals(endpointsList.size(), 1);
        final CoordinationProtos.DrillbitEndpoint endpoint = endpointsList.get(0);
        Assert.assertEquals(endpoint.getAddress(), ipAndPort[0]);
        Assert.assertEquals(endpoint.getUserPort(), Integer.parseInt(ipAndPort[1]));
    }

    @Test
    public void testParseAndVerifyEndpointsMultipleDrillbitIp() throws Exception {
        // Test with multiple drillbit ip
        final String drillBitConnection = "10.10.100.161,10.10.100.162";
        final List<CoordinationProtos.DrillbitEndpoint> endpointsList = DrillClient.parseAndVerifyEndpoints(drillBitConnection, config.getString(INITIAL_USER_PORT));
        Assert.assertEquals(endpointsList.size(), 2);
        CoordinationProtos.DrillbitEndpoint endpoint = endpointsList.get(0);
        Assert.assertEquals(endpoint.getAddress(), "10.10.100.161");
        Assert.assertEquals(endpoint.getUserPort(), config.getInt(INITIAL_USER_PORT));
        endpoint = endpointsList.get(1);
        Assert.assertEquals(endpoint.getAddress(), "10.10.100.162");
        Assert.assertEquals(endpoint.getUserPort(), config.getInt(INITIAL_USER_PORT));
    }

    @Test
    public void testParseAndVerifyEndpointsMultipleDrillbitIpPort() throws Exception {
        // Test with multiple drillbit ip:port
        final String drillBitConnection = "10.10.100.161:5000,10.10.100.162:5000";
        final List<CoordinationProtos.DrillbitEndpoint> endpointsList = DrillClient.parseAndVerifyEndpoints(drillBitConnection, config.getString(INITIAL_USER_PORT));
        Assert.assertEquals(endpointsList.size(), 2);
        CoordinationProtos.DrillbitEndpoint endpoint = endpointsList.get(0);
        Assert.assertEquals(endpoint.getAddress(), "10.10.100.161");
        Assert.assertEquals(endpoint.getUserPort(), 5000);
        endpoint = endpointsList.get(1);
        Assert.assertEquals(endpoint.getAddress(), "10.10.100.162");
        Assert.assertEquals(endpoint.getUserPort(), 5000);
    }

    @Test
    public void testParseAndVerifyEndpointsMultipleDrillbitIpPortIp() throws Exception {
        // Test with multiple drillbit with mix of ip:port and ip
        final String drillBitConnection = "10.10.100.161:5000,10.10.100.162";
        final List<CoordinationProtos.DrillbitEndpoint> endpointsList = DrillClient.parseAndVerifyEndpoints(drillBitConnection, config.getString(INITIAL_USER_PORT));
        Assert.assertEquals(endpointsList.size(), 2);
        CoordinationProtos.DrillbitEndpoint endpoint = endpointsList.get(0);
        Assert.assertEquals(endpoint.getAddress(), "10.10.100.161");
        Assert.assertEquals(endpoint.getUserPort(), 5000);
        endpoint = endpointsList.get(1);
        Assert.assertEquals(endpoint.getAddress(), "10.10.100.162");
        Assert.assertEquals(endpoint.getUserPort(), config.getInt(INITIAL_USER_PORT));
    }

    @Test
    public void testParseAndVerifyEndpointsEmptyString() throws Exception {
        // Test with empty string
        final String drillBitConnection = "";
        try {
            final List<CoordinationProtos.DrillbitEndpoint> endpointsList = DrillClient.parseAndVerifyEndpoints(drillBitConnection, config.getString(INITIAL_USER_PORT));
            Assert.fail();
        } catch (InvalidConnectionInfoException e) {
            DrillClientTest.logger.error("Exception ", e);
        }
    }

    @Test
    public void testParseAndVerifyEndpointsOnlyPortDelim() throws Exception {
        // Test to check when connection string only has delimiter
        final String drillBitConnection = ":";
        try {
            final List<CoordinationProtos.DrillbitEndpoint> endpointsList = DrillClient.parseAndVerifyEndpoints(drillBitConnection, config.getString(INITIAL_USER_PORT));
            Assert.fail();
        } catch (InvalidConnectionInfoException e) {
            DrillClientTest.logger.error("Exception ", e);
        }
    }

    @Test
    public void testParseAndVerifyEndpointsWithOnlyPort() throws Exception {
        // Test to check when connection string has port with no ip
        final String drillBitConnection = ":5000";
        try {
            final List<CoordinationProtos.DrillbitEndpoint> endpointsList = DrillClient.parseAndVerifyEndpoints(drillBitConnection, config.getString(INITIAL_USER_PORT));
            Assert.fail();
        } catch (InvalidConnectionInfoException e) {
            DrillClientTest.logger.error("Exception ", e);
        }
    }

    @Test
    public void testParseAndVerifyEndpointsWithMultiplePort() throws Exception {
        // Test to check when connection string has multiple port with one ip
        final String drillBitConnection = "10.10.100.161:5000:6000";
        try {
            final List<CoordinationProtos.DrillbitEndpoint> endpointsList = DrillClient.parseAndVerifyEndpoints(drillBitConnection, config.getString(INITIAL_USER_PORT));
            Assert.fail();
        } catch (InvalidConnectionInfoException e) {
            DrillClientTest.logger.error("Exception ", e);
        }
    }

    @Test
    public void testParseAndVerifyEndpointsIpWithDelim() throws Exception {
        // Test to check when connection string has ip with delimiter
        final String drillBitConnection = "10.10.100.161:";
        final List<CoordinationProtos.DrillbitEndpoint> endpointsList = DrillClient.parseAndVerifyEndpoints(drillBitConnection, config.getString(INITIAL_USER_PORT));
        final CoordinationProtos.DrillbitEndpoint endpoint = endpointsList.get(0);
        Assert.assertEquals(endpointsList.size(), 1);
        Assert.assertEquals(endpoint.getAddress(), "10.10.100.161");
        Assert.assertEquals(endpoint.getUserPort(), config.getInt(INITIAL_USER_PORT));
    }

    @Test
    public void testParseAndVerifyEndpointsIpWithEmptyPort() throws Exception {
        // Test to check when connection string has ip with delimiter
        final String drillBitConnection = "10.10.100.161:    ";
        final List<CoordinationProtos.DrillbitEndpoint> endpointsList = DrillClient.parseAndVerifyEndpoints(drillBitConnection, config.getString(INITIAL_USER_PORT));
        final CoordinationProtos.DrillbitEndpoint endpoint = endpointsList.get(0);
        Assert.assertEquals(endpointsList.size(), 1);
        Assert.assertEquals(endpoint.getAddress(), "10.10.100.161");
        Assert.assertEquals(endpoint.getUserPort(), config.getInt(INITIAL_USER_PORT));
    }

    @Test
    public void testParseAndVerifyEndpointsIpWithSpaces() throws Exception {
        // Test to check when connection string has spaces in between
        final String drillBitConnection = "10.10.100.161 : 5000, 10.10.100.162:6000    ";
        final List<CoordinationProtos.DrillbitEndpoint> endpointsList = DrillClient.parseAndVerifyEndpoints(drillBitConnection, config.getString(INITIAL_USER_PORT));
        CoordinationProtos.DrillbitEndpoint endpoint = endpointsList.get(0);
        Assert.assertEquals(endpointsList.size(), 2);
        Assert.assertEquals(endpoint.getAddress(), "10.10.100.161");
        Assert.assertEquals(endpoint.getUserPort(), 5000);
        endpoint = endpointsList.get(1);
        Assert.assertEquals(endpoint.getAddress(), "10.10.100.162");
        Assert.assertEquals(endpoint.getUserPort(), 6000);
    }

    @Test
    public void testParseAndVerifyEndpointsStringWithSpaces() throws Exception {
        // Test to check when connection string has ip with delimiter
        final String drillBitConnection = "10.10.100.161 : 5000";
        final List<CoordinationProtos.DrillbitEndpoint> endpointsList = DrillClient.parseAndVerifyEndpoints(drillBitConnection, config.getString(INITIAL_USER_PORT));
        final CoordinationProtos.DrillbitEndpoint endpoint = endpointsList.get(0);
        Assert.assertEquals(endpointsList.size(), 1);
        Assert.assertEquals(endpoint.getAddress(), "10.10.100.161");
        Assert.assertEquals(endpoint.getUserPort(), 5000);
    }

    @Test
    public void testParseAndVerifyEndpointsNonNumericPort() throws Exception {
        // Test to check when connection string has non-numeric port
        final String drillBitConnection = "10.10.100.161:5ab0";
        try {
            final List<CoordinationProtos.DrillbitEndpoint> endpointsList = DrillClient.parseAndVerifyEndpoints(drillBitConnection, config.getString(INITIAL_USER_PORT));
            Assert.fail();
        } catch (InvalidConnectionInfoException e) {
            DrillClientTest.logger.error("Exception ", e);
        }
    }

    @Test
    public void testParseAndVerifyEndpointsOnlyDelim() throws Exception {
        // Test to check when connection string has only delimiter coma
        final String drillBitConnection = "  ,   ";
        try {
            final List<CoordinationProtos.DrillbitEndpoint> endpointsList = DrillClient.parseAndVerifyEndpoints(drillBitConnection, config.getString(INITIAL_USER_PORT));
            Assert.fail();
        } catch (InvalidConnectionInfoException e) {
            DrillClientTest.logger.error("Exception ", e);
        }
    }
}

