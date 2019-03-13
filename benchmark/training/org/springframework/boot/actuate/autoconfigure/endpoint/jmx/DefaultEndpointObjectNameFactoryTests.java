/**
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.actuate.autoconfigure.endpoint.jmx;


import java.util.Collections;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.endpoint.EndpointId;
import org.springframework.mock.env.MockEnvironment;


/**
 * Tests for {@link DefaultEndpointObjectNameFactory}.
 *
 * @author Stephane Nicoll
 */
public class DefaultEndpointObjectNameFactoryTests {
    private final MockEnvironment environment = new MockEnvironment();

    private final JmxEndpointProperties properties = new JmxEndpointProperties(this.environment);

    private final MBeanServer mBeanServer = Mockito.mock(MBeanServer.class);

    private String contextId;

    @Test
    public void generateObjectName() {
        ObjectName objectName = generateObjectName(endpoint(EndpointId.of("test")));
        assertThat(objectName.toString()).isEqualTo("org.springframework.boot:type=Endpoint,name=Test");
    }

    @Test
    public void generateObjectNameWithCapitalizedId() {
        ObjectName objectName = generateObjectName(endpoint(EndpointId.of("testEndpoint")));
        assertThat(objectName.toString()).isEqualTo("org.springframework.boot:type=Endpoint,name=TestEndpoint");
    }

    @Test
    public void generateObjectNameWithCustomDomain() {
        this.properties.setDomain("com.example.acme");
        ObjectName objectName = generateObjectName(endpoint(EndpointId.of("test")));
        assertThat(objectName.toString()).isEqualTo("com.example.acme:type=Endpoint,name=Test");
    }

    @Test
    public void generateObjectNameWithUniqueNames() {
        this.environment.setProperty("spring.jmx.unique-names", "true");
        assertUniqueObjectName();
    }

    @Test
    public void generateObjectNameWithStaticNames() {
        this.properties.getStaticNames().setProperty("counter", "42");
        this.properties.getStaticNames().setProperty("foo", "bar");
        ObjectName objectName = generateObjectName(endpoint(EndpointId.of("test")));
        assertThat(objectName.getKeyProperty("counter")).isEqualTo("42");
        assertThat(objectName.getKeyProperty("foo")).isEqualTo("bar");
        assertThat(objectName.toString()).startsWith("org.springframework.boot:type=Endpoint,name=Test,");
    }

    @Test
    public void generateObjectNameWithDuplicate() throws MalformedObjectNameException {
        this.contextId = "testContext";
        BDDMockito.given(this.mBeanServer.queryNames(new ObjectName("org.springframework.boot:type=Endpoint,name=Test,*"), null)).willReturn(Collections.singleton(new ObjectName("org.springframework.boot:type=Endpoint,name=Test")));
        ObjectName objectName = generateObjectName(endpoint(EndpointId.of("test")));
        assertThat(objectName.toString()).isEqualTo("org.springframework.boot:type=Endpoint,name=Test,context=testContext");
    }
}

