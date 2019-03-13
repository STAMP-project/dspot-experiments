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
package org.springframework.boot.autoconfigure.web;


import java.util.function.Consumer;
import org.junit.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;


/**
 * Binding tests for {@link ResourceProperties}.
 *
 * @author Stephane Nicoll
 */
public class ResourcePropertiesBindingTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(ResourcePropertiesBindingTests.TestConfiguration.class);

    @Test
    public void staticLocationsExpandArray() {
        this.contextRunner.withPropertyValues("spring.resources.static-locations[0]=classpath:/one/", "spring.resources.static-locations[1]=classpath:/two", "spring.resources.static-locations[2]=classpath:/three/", "spring.resources.static-locations[3]=classpath:/four", "spring.resources.static-locations[4]=classpath:/five/", "spring.resources.static-locations[5]=classpath:/six").run(assertResourceProperties(( properties) -> assertThat(properties.getStaticLocations()).contains("classpath:/one/", "classpath:/two/", "classpath:/three/", "classpath:/four/", "classpath:/five/", "classpath:/six/")));
    }

    @Configuration
    @EnableConfigurationProperties(ResourceProperties.class)
    static class TestConfiguration {}
}

