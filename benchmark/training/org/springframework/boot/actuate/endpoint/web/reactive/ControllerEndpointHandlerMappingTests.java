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
package org.springframework.boot.actuate.endpoint.web.reactive;


import HttpMethod.GET;
import HttpMethod.POST;
import org.junit.Test;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpoint;
import org.springframework.boot.actuate.endpoint.web.annotation.ExposableControllerEndpoint;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.MethodNotAllowedException;


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
        assertThat(getHandler(mapping, GET, "/first")).isEqualTo(handlerOf(first.getController(), "get"));
        assertThat(getHandler(mapping, POST, "/second")).isEqualTo(handlerOf(second.getController(), "save"));
        assertThat(getHandler(mapping, GET, "/third")).isNull();
    }

    @Test
    public void mappingWithPrefix() throws Exception {
        ExposableControllerEndpoint first = firstEndpoint();
        ExposableControllerEndpoint second = secondEndpoint();
        ControllerEndpointHandlerMapping mapping = createMapping("actuator", first, second);
        assertThat(getHandler(mapping, GET, "/actuator/first")).isEqualTo(handlerOf(first.getController(), "get"));
        assertThat(getHandler(mapping, POST, "/actuator/second")).isEqualTo(handlerOf(second.getController(), "save"));
        assertThat(getHandler(mapping, GET, "/first")).isNull();
        assertThat(getHandler(mapping, GET, "/second")).isNull();
    }

    @Test
    public void mappingWithNoPath() throws Exception {
        ExposableControllerEndpoint pathless = pathlessEndpoint();
        ControllerEndpointHandlerMapping mapping = createMapping("actuator", pathless);
        assertThat(getHandler(mapping, GET, "/actuator/pathless")).isEqualTo(handlerOf(pathless.getController(), "get"));
        assertThat(getHandler(mapping, GET, "/pathless")).isNull();
        assertThat(getHandler(mapping, GET, "/")).isNull();
    }

    @Test
    public void mappingNarrowedToMethod() throws Exception {
        ExposableControllerEndpoint first = firstEndpoint();
        ControllerEndpointHandlerMapping mapping = createMapping("actuator", first);
        assertThatExceptionOfType(MethodNotAllowedException.class).isThrownBy(() -> getHandler(mapping, HttpMethod.POST, "/actuator/first"));
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

