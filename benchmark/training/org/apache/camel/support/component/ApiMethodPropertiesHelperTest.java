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
package org.apache.camel.support.component;


import java.util.HashMap;
import org.apache.camel.CamelContext;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Assert;
import org.junit.Test;


public class ApiMethodPropertiesHelperTest {
    private static final String TEST_PREFIX = "CamelTest.";

    private static final String PROPERTY_1 = (ApiMethodPropertiesHelperTest.TEST_PREFIX) + "property1";

    private static final String PROPERTY_2 = (ApiMethodPropertiesHelperTest.TEST_PREFIX) + "property2";

    private static final String PROPERTY_3 = (ApiMethodPropertiesHelperTest.TEST_PREFIX) + "property3";

    private static final String PROPERTY_4 = (ApiMethodPropertiesHelperTest.TEST_PREFIX) + "property4";

    // test camel case property names
    private static final String PROPERTY_5 = (ApiMethodPropertiesHelperTest.TEST_PREFIX.substring(0, ((ApiMethodPropertiesHelperTest.TEST_PREFIX.length()) - 1))) + "Property5";

    private static final String VALUE_1 = "value1";

    private static final long VALUE_2 = 2;

    private static final String VALUE_3 = "value3";

    private static final String VALUE_4 = "true";

    private static final String VALUE_5 = "CamelCaseValue";

    private static ApiMethodPropertiesHelper<ApiMethodPropertiesHelperTest.TestComponentConfiguration> propertiesHelper = new ApiMethodPropertiesHelper<ApiMethodPropertiesHelperTest.TestComponentConfiguration>(ApiMethodPropertiesHelperTest.TestComponentConfiguration.class, ApiMethodPropertiesHelperTest.TEST_PREFIX) {};

    @Test
    public void testGetExchangeProperties() throws Exception {
        final CamelContext camelContext = new DefaultCamelContext();
        MockEndpoint mock = new MockEndpoint(null, new org.apache.camel.component.mock.MockComponent(camelContext));
        final HashMap<String, Object> properties = new HashMap<>();
        final DefaultExchange exchange = new DefaultExchange(mock);
        exchange.getIn().setHeader(ApiMethodPropertiesHelperTest.PROPERTY_1, ApiMethodPropertiesHelperTest.VALUE_1);
        exchange.getIn().setHeader(ApiMethodPropertiesHelperTest.PROPERTY_2, ApiMethodPropertiesHelperTest.VALUE_2);
        exchange.getIn().setHeader(ApiMethodPropertiesHelperTest.PROPERTY_3, ApiMethodPropertiesHelperTest.VALUE_3);
        exchange.getIn().setHeader(ApiMethodPropertiesHelperTest.PROPERTY_4, ApiMethodPropertiesHelperTest.VALUE_4);
        exchange.getIn().setHeader(ApiMethodPropertiesHelperTest.PROPERTY_5, ApiMethodPropertiesHelperTest.VALUE_5);
        ApiMethodPropertiesHelperTest.propertiesHelper.getExchangeProperties(exchange, properties);
        Assert.assertEquals(5, properties.size());
    }

    @Test
    public void testGetEndpointProperties() throws Exception {
        final HashMap<String, Object> properties = new HashMap<>();
        final ApiMethodPropertiesHelperTest.TestEndpointConfiguration endpointConfiguration = new ApiMethodPropertiesHelperTest.TestEndpointConfiguration();
        endpointConfiguration.setProperty1(ApiMethodPropertiesHelperTest.VALUE_1);
        endpointConfiguration.setProperty2(ApiMethodPropertiesHelperTest.VALUE_2);
        endpointConfiguration.setProperty3(ApiMethodPropertiesHelperTest.VALUE_3);
        endpointConfiguration.setProperty4(Boolean.valueOf(ApiMethodPropertiesHelperTest.VALUE_4));
        ApiMethodPropertiesHelperTest.propertiesHelper.getEndpointProperties(endpointConfiguration, properties);
        Assert.assertEquals(2, properties.size());
    }

    @Test
    public void testGetEndpointPropertyNames() throws Exception {
        final ApiMethodPropertiesHelperTest.TestEndpointConfiguration endpointConfiguration = new ApiMethodPropertiesHelperTest.TestEndpointConfiguration();
        endpointConfiguration.setProperty1(ApiMethodPropertiesHelperTest.VALUE_1);
        endpointConfiguration.setProperty4(Boolean.valueOf(ApiMethodPropertiesHelperTest.VALUE_4));
        Assert.assertEquals(1, ApiMethodPropertiesHelperTest.propertiesHelper.getEndpointPropertyNames(endpointConfiguration).size());
    }

    @Test
    public void testGetValidEndpointProperties() throws Exception {
        Assert.assertEquals(2, ApiMethodPropertiesHelperTest.propertiesHelper.getValidEndpointProperties(new ApiMethodPropertiesHelperTest.TestEndpointConfiguration()).size());
    }

    @SuppressWarnings("unused")
    private static class TestComponentConfiguration {
        private String property1;

        private Long property2;

        public String getProperty1() {
            return property1;
        }

        public void setProperty1(String property1) {
            this.property1 = property1;
        }

        public long getProperty2() {
            return property2;
        }

        public void setProperty2(Long property2) {
            this.property2 = property2;
        }
    }

    @SuppressWarnings("unused")
    private static class TestEndpointConfiguration extends ApiMethodPropertiesHelperTest.TestComponentConfiguration {
        private String property3;

        private Boolean property4;

        public String getProperty3() {
            return property3;
        }

        public void setProperty3(String property3) {
            this.property3 = property3;
        }

        public Boolean getProperty4() {
            return property4;
        }

        public void setProperty4(Boolean property4) {
            this.property4 = property4;
        }
    }
}

