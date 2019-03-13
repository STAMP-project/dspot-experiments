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
package org.apache.camel.component.docker;


import DockerConstants.DOCKER_HOST;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.junit.Assert;
import org.junit.Test;


public class DockerConfigurationTest {
    private DockerConfiguration configuration;

    private CamelContext camelContext;

    @Test
    public void testPropertyFromHeader() {
        String host = "camelhost";
        Message message = new org.apache.camel.support.DefaultMessage(camelContext);
        message.setHeader(DOCKER_HOST, host);
        String configurationProp = DockerHelper.getProperty(DOCKER_HOST, configuration, message, String.class);
        Assert.assertEquals(host, configurationProp);
    }

    @Test
    public void testPropertyfromEndpointProperties() {
        String host = "camelhost";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DockerHelper.transformFromHeaderName(DOCKER_HOST), host);
        configuration.setParameters(parameters);
        Message message = new org.apache.camel.support.DefaultMessage(camelContext);
        String configurationProp = DockerHelper.getProperty(DOCKER_HOST, configuration, message, String.class);
        Assert.assertEquals(host, configurationProp);
    }
}

