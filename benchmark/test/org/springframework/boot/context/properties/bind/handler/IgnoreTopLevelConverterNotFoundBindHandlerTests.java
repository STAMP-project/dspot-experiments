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
package org.springframework.boot.context.properties.bind.handler;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MockConfigurationPropertySource;
import org.springframework.core.convert.ConverterNotFoundException;


/**
 * Tests for {@link IgnoreTopLevelConverterNotFoundBindHandler}.
 *
 * @author Madhura Bhave
 */
public class IgnoreTopLevelConverterNotFoundBindHandlerTests {
    private List<ConfigurationPropertySource> sources = new ArrayList<>();

    private Binder binder;

    @Test
    public void bindWhenHandlerNotPresentShouldFail() {
        assertThatExceptionOfType(BindException.class).isThrownBy(() -> this.binder.bind("example", Bindable.of(.class))).withCauseInstanceOf(ConverterNotFoundException.class);
    }

    @Test
    public void bindWhenTopLevelContextAndExceptionIgnorableShouldNotFail() {
        this.binder.bind("example", Bindable.of(IgnoreTopLevelConverterNotFoundBindHandlerTests.Example.class), new IgnoreTopLevelConverterNotFoundBindHandler());
    }

    @Test
    public void bindWhenExceptionNotIgnorableShouldFail() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("example.foo", "1");
        this.sources.add(source);
        assertThatExceptionOfType(BindException.class).isThrownBy(() -> this.binder.bind("example", Bindable.of(.class), new IgnoreTopLevelConverterNotFoundBindHandler())).withCauseInstanceOf(IllegalStateException.class);
    }

    @Test
    public void bindWhenExceptionInNestedContextShouldFail() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("example.map", "hello");
        this.sources.add(source);
        assertThatExceptionOfType(BindException.class).isThrownBy(() -> this.binder.bind("example", Bindable.of(.class), new IgnoreTopLevelConverterNotFoundBindHandler())).withCauseInstanceOf(ConverterNotFoundException.class);
    }

    public static class Example {
        private int foo;

        private Map<String, String> map;

        public int getFoo() {
            return this.foo;
        }

        public void setFoo(int foo) {
            throw new IllegalStateException();
        }

        public Map<String, String> getMap() {
            return this.map;
        }

        public void setMap(Map<String, String> map) {
            this.map = map;
        }
    }
}

