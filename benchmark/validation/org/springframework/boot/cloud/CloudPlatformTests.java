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
package org.springframework.boot.cloud;


import CloudPlatform.CLOUD_FOUNDRY;
import CloudPlatform.HEROKU;
import CloudPlatform.KUBERNETES;
import CloudPlatform.SAP;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.mock.env.MockEnvironment;


/**
 * Tests for {@link CloudPlatform}.
 *
 * @author Phillip Webb
 */
public class CloudPlatformTests {
    @Test
    public void getActiveWhenEnvironmentIsNullShouldReturnNull() {
        CloudPlatform platform = CloudPlatform.getActive(null);
        assertThat(platform).isNull();
    }

    @Test
    public void getActiveWhenNotInCloudShouldReturnNull() {
        Environment environment = new MockEnvironment();
        CloudPlatform platform = CloudPlatform.getActive(environment);
        assertThat(platform).isNull();
    }

    @Test
    public void getActiveWhenHasVcapApplicationShouldReturnCloudFoundry() {
        Environment environment = new MockEnvironment().withProperty("VCAP_APPLICATION", "---");
        CloudPlatform platform = CloudPlatform.getActive(environment);
        assertThat(platform).isEqualTo(CLOUD_FOUNDRY);
        assertThat(platform.isActive(environment)).isTrue();
    }

    @Test
    public void getActiveWhenHasVcapServicesShouldReturnCloudFoundry() {
        Environment environment = new MockEnvironment().withProperty("VCAP_SERVICES", "---");
        CloudPlatform platform = CloudPlatform.getActive(environment);
        assertThat(platform).isEqualTo(CLOUD_FOUNDRY);
        assertThat(platform.isActive(environment)).isTrue();
    }

    @Test
    public void getActiveWhenHasDynoShouldReturnHeroku() {
        Environment environment = new MockEnvironment().withProperty("DYNO", "---");
        CloudPlatform platform = CloudPlatform.getActive(environment);
        assertThat(platform).isEqualTo(HEROKU);
        assertThat(platform.isActive(environment)).isTrue();
    }

    @Test
    public void getActiveWhenHasHcLandscapeShouldReturnSap() {
        Environment environment = new MockEnvironment().withProperty("HC_LANDSCAPE", "---");
        CloudPlatform platform = CloudPlatform.getActive(environment);
        assertThat(platform).isEqualTo(SAP);
        assertThat(platform.isActive(environment)).isTrue();
    }

    @Test
    public void getActiveWhenHasServiceHostAndServicePortShouldReturnKubernetes() {
        MockEnvironment environment = new MockEnvironment();
        Map<String, Object> source = new HashMap<>();
        source.put("EXAMPLE_SERVICE_HOST", "---");
        source.put("EXAMPLE_SERVICE_PORT", "8080");
        PropertySource<?> propertySource = new org.springframework.core.env.SystemEnvironmentPropertySource(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, source);
        environment.getPropertySources().addFirst(propertySource);
        CloudPlatform platform = CloudPlatform.getActive(environment);
        assertThat(platform).isEqualTo(KUBERNETES);
        assertThat(platform.isActive(environment)).isTrue();
    }

    @Test
    public void getActiveWhenHasServiceHostAndNoServicePortShouldNotReturnKubernetes() {
        MockEnvironment environment = new MockEnvironment();
        PropertySource<?> propertySource = new org.springframework.core.env.SystemEnvironmentPropertySource(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, Collections.singletonMap("EXAMPLE_SERVICE_HOST", "---"));
        environment.getPropertySources().addFirst(propertySource);
        CloudPlatform platform = CloudPlatform.getActive(environment);
        assertThat(platform).isNull();
    }
}

