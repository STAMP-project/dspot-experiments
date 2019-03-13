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
package org.apache.camel.component.influxdb;


import InfluxDbConstants.MEASUREMENT_NAME;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class InfluxDbProducerTest extends AbstractInfluxDbTest {
    @EndpointInject(uri = "mock:test")
    MockEndpoint successEndpoint;

    @EndpointInject(uri = "mock:error")
    MockEndpoint errorEndpoint;

    @Test
    public void writePointFromMapAndStaticDbName() throws InterruptedException {
        errorEndpoint.expectedMessageCount(0);
        successEndpoint.expectedMessageCount(1);
        Map<String, Object> pointMap = createMapPoint();
        sendBody("direct:test", pointMap);
        errorEndpoint.assertIsSatisfied();
        successEndpoint.assertIsSatisfied();
    }

    @Test
    public void writePointFromMapAndDynamicDbName() throws InterruptedException {
        errorEndpoint.expectedMessageCount(0);
        successEndpoint.expectedMessageCount(1);
        Map<String, Object> pointMap = createMapPoint();
        Map<String, Object> header = createHeader();
        sendBody("direct:test", pointMap, header);
        errorEndpoint.assertIsSatisfied();
        successEndpoint.assertIsSatisfied();
    }

    @Test
    public void missingMeassurementNameFails() throws InterruptedException {
        errorEndpoint.expectedMessageCount(1);
        successEndpoint.expectedMessageCount(0);
        Map<String, Object> pointMap = new HashMap<>();
        pointMap.remove(MEASUREMENT_NAME);
        sendBody("direct:test", pointMap);
        errorEndpoint.assertIsSatisfied();
        successEndpoint.assertIsSatisfied();
    }
}

