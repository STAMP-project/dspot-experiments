/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.test.web;


import TestPropertySourceUtils.INLINED_PROPERTIES_PROPERTY_SOURCE_NAME;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.mock.env.MockEnvironment;


/**
 * Tests for {@link SpringBootTestRandomPortEnvironmentPostProcessor}.
 *
 * @author Madhura Bhave
 * @author Andy Wilkinson
 */
public class SpringBootTestRandomPortEnvironmentPostProcessorTests {
    private SpringBootTestRandomPortEnvironmentPostProcessor postProcessor = new SpringBootTestRandomPortEnvironmentPostProcessor();

    private MockEnvironment environment;

    private MutablePropertySources propertySources;

    @Test
    public void postProcessWhenServerAndManagementPortIsZeroInTestPropertySource() {
        addTestPropertySource("0", "0");
        this.postProcessor.postProcessEnvironment(this.environment, null);
        assertThat(this.environment.getProperty("server.port")).isEqualTo("0");
        assertThat(this.environment.getProperty("management.server.port")).isEqualTo("0");
    }

    @Test
    public void postProcessWhenTestServerAndTestManagementPortAreNonZero() {
        addTestPropertySource("8080", "8081");
        this.environment.setProperty("server.port", "8080");
        this.environment.setProperty("management.server.port", "8081");
        this.postProcessor.postProcessEnvironment(this.environment, null);
        assertThat(this.environment.getProperty("server.port")).isEqualTo("8080");
        assertThat(this.environment.getProperty("management.server.port")).isEqualTo("8081");
    }

    @Test
    public void postProcessWhenTestServerPortIsZeroAndTestManagementPortIsNotNull() {
        addTestPropertySource("0", "8080");
        this.postProcessor.postProcessEnvironment(this.environment, null);
        assertThat(this.environment.getProperty("server.port")).isEqualTo("0");
        assertThat(this.environment.getProperty("management.server.port")).isEqualTo("8080");
    }

    @Test
    public void postProcessWhenTestServerPortIsZeroAndManagementPortIsNull() {
        addTestPropertySource("0", null);
        this.postProcessor.postProcessEnvironment(this.environment, null);
        assertThat(this.environment.getProperty("server.port")).isEqualTo("0");
        assertThat(this.environment.getProperty("management.server.port")).isNull();
    }

    @Test
    public void postProcessWhenTestServerPortIsZeroAndManagementPortIsNotNullAndSameInProduction() {
        addTestPropertySource("0", null);
        Map<String, Object> other = new HashMap<>();
        other.put("server.port", "8081");
        other.put("management.server.port", "8081");
        MapPropertySource otherSource = new MapPropertySource("other", other);
        this.propertySources.addLast(otherSource);
        this.postProcessor.postProcessEnvironment(this.environment, null);
        assertThat(this.environment.getProperty("server.port")).isEqualTo("0");
        assertThat(this.environment.getProperty("management.server.port")).isEqualTo("");
    }

    @Test
    public void postProcessWhenTestServerPortIsZeroAndManagementPortIsNotNullAndDefaultSameInProduction() {
        // mgmt port is 8080 which means it's on the same port as main server since that
        // is null in app properties
        addTestPropertySource("0", null);
        this.propertySources.addLast(new MapPropertySource("other", Collections.singletonMap("management.server.port", "8080")));
        this.postProcessor.postProcessEnvironment(this.environment, null);
        assertThat(this.environment.getProperty("server.port")).isEqualTo("0");
        assertThat(this.environment.getProperty("management.server.port")).isEqualTo("");
    }

    @Test
    public void postProcessWhenTestServerPortIsZeroAndManagementPortIsNotNullAndDifferentInProduction() {
        addTestPropertySource("0", null);
        this.propertySources.addLast(new MapPropertySource("other", Collections.singletonMap("management.server.port", "8081")));
        this.postProcessor.postProcessEnvironment(this.environment, null);
        assertThat(this.environment.getProperty("server.port")).isEqualTo("0");
        assertThat(this.environment.getProperty("management.server.port")).isEqualTo("0");
    }

    @Test
    public void postProcessWhenTestServerPortIsZeroAndManagementPortMinusOne() {
        addTestPropertySource("0", null);
        this.propertySources.addLast(new MapPropertySource("other", Collections.singletonMap("management.server.port", "-1")));
        this.postProcessor.postProcessEnvironment(this.environment, null);
        assertThat(this.environment.getProperty("server.port")).isEqualTo("0");
        assertThat(this.environment.getProperty("management.server.port")).isEqualTo("-1");
    }

    @Test
    public void postProcessWhenTestServerPortIsZeroAndManagementPortIsAnInteger() {
        addTestPropertySource("0", null);
        this.propertySources.addLast(new MapPropertySource("other", Collections.singletonMap("management.server.port", 8081)));
        this.postProcessor.postProcessEnvironment(this.environment, null);
        assertThat(this.environment.getProperty("server.port")).isEqualTo("0");
        assertThat(this.environment.getProperty("management.server.port")).isEqualTo("0");
    }

    @Test
    public void postProcessWhenManagementServerPortPlaceholderPresentShouldResolvePlaceholder() {
        addTestPropertySource("0", null);
        MapPropertySource testPropertySource = ((MapPropertySource) (this.propertySources.get(INLINED_PROPERTIES_PROPERTY_SOURCE_NAME)));
        testPropertySource.getSource().put("port", "9090");
        this.propertySources.addLast(new MapPropertySource("other", Collections.singletonMap("management.server.port", "${port}")));
        this.postProcessor.postProcessEnvironment(this.environment, null);
        assertThat(this.environment.getProperty("server.port")).isEqualTo("0");
        assertThat(this.environment.getProperty("management.server.port")).isEqualTo("0");
    }

    @Test
    public void postProcessWhenManagementServerPortPlaceholderAbsentShouldFail() {
        addTestPropertySource("0", null);
        this.propertySources.addLast(new MapPropertySource("other", Collections.singletonMap("management.server.port", "${port}")));
        assertThatIllegalArgumentException().isThrownBy(() -> this.postProcessor.postProcessEnvironment(this.environment, null)).withMessage("Could not resolve placeholder \'port\' in value \"${port}\"");
    }

    @Test
    public void postProcessWhenServerPortPlaceholderPresentShouldResolvePlaceholder() {
        addTestPropertySource("0", null);
        MapPropertySource testPropertySource = ((MapPropertySource) (this.propertySources.get(INLINED_PROPERTIES_PROPERTY_SOURCE_NAME)));
        testPropertySource.getSource().put("port", "8080");
        Map<String, Object> source = new HashMap<>();
        source.put("server.port", "${port}");
        source.put("management.server.port", "9090");
        this.propertySources.addLast(new MapPropertySource("other", source));
        this.postProcessor.postProcessEnvironment(this.environment, null);
        assertThat(this.environment.getProperty("server.port")).isEqualTo("0");
        assertThat(this.environment.getProperty("management.server.port")).isEqualTo("0");
    }

    @Test
    public void postProcessWhenServerPortPlaceholderAbsentShouldFail() {
        addTestPropertySource("0", null);
        Map<String, Object> source = new HashMap<>();
        source.put("server.port", "${port}");
        source.put("management.server.port", "9090");
        this.propertySources.addLast(new MapPropertySource("other", source));
        assertThatIllegalArgumentException().isThrownBy(() -> this.postProcessor.postProcessEnvironment(this.environment, null)).withMessage("Could not resolve placeholder \'port\' in value \"${port}\"");
    }
}

