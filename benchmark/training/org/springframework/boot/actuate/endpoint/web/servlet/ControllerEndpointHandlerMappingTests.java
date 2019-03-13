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
package org.springframework.boot.actuate.endpoint.web.servlet;


import org.junit.Test;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpoint;
import org.springframework.boot.actuate.endpoint.web.annotation.ExposableControllerEndpoint;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


/**
 * Tests for {@link ControllerEndpointHandlerMapping}.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 */
public class ControllerEndpointHandlerMappingTests {
    private final StaticApplicationContext context = new StaticApplicationContext();

    @Test
    public void mappingWithNoPrefix() throws Exception {
        ExposableControllerEndpoint first = firstEndpoint();
        ExposableControllerEndpoint second = secondEndpoint();
        ControllerEndpointHandlerMapping mapping = createMapping("", first, second);
        assertThat(mapping.getHandler(request("GET", "/first")).getHandler()).isEqualTo(handlerOf(first.getController(), "get"));
        assertThat(mapping.getHandler(request("POST", "/second")).getHandler()).isEqualTo(handlerOf(second.getController(), "save"));
        assertThat(mapping.getHandler(request("GET", "/third"))).isNull();
    }

    @Test
    public void mappingWithPrefix() throws Exception {
        ExposableControllerEndpoint first = firstEndpoint();
        ExposableControllerEndpoint second = secondEndpoint();
        ControllerEndpointHandlerMapping mapping = createMapping("actuator", first, second);
        assertThat(mapping.getHandler(request("GET", "/actuator/first")).getHandler()).isEqualTo(handlerOf(first.getController(), "get"));
        assertThat(mapping.getHandler(request("POST", "/actuator/second")).getHandler()).isEqualTo(handlerOf(second.getController(), "save"));
        assertThat(mapping.getHandler(request("GET", "/first"))).isNull();
        assertThat(mapping.getHandler(request("GET", "/second"))).isNull();
    }

    @Test
    public void mappingNarrowedToMethod() throws Exception {
        ExposableControllerEndpoint first = firstEndpoint();
        ControllerEndpointHandlerMapping mapping = createMapping("actuator", first);
        assertThatExceptionOfType(HttpRequestMethodNotSupportedException.class).isThrownBy(() -> mapping.getHandler(request("POST", "/actuator/first")));
    }

    @Test
    public void mappingWithNoPath() throws Exception {
        ExposableControllerEndpoint pathless = pathlessEndpoint();
        ControllerEndpointHandlerMapping mapping = createMapping("actuator", pathless);
        assertThat(mapping.getHandler(request("GET", "/actuator/pathless")).getHandler()).isEqualTo(handlerOf(pathless.getController(), "get"));
        assertThat(mapping.getHandler(request("GET", "/pathless"))).isNull();
        assertThat(mapping.getHandler(request("GET", "/"))).isNull();
    }

    @ControllerEndpoint(id = "first")
    private static class FirstTestMvcEndpoint {
        @GetMapping("/")
        public String get() {
            return "test";
        }
    }

    @ControllerEndpoint(id = "second")
    private static class SecondTestMvcEndpoint {
        @PostMapping("/")
        public void save() {
        }
    }

    @ControllerEndpoint(id = "pathless")
    private static class PathlessControllerEndpoint {
        @GetMapping
        public String get() {
            return "test";
        }
    }
}

