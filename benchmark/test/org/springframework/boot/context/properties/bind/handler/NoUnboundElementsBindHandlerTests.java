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
package org.springframework.boot.context.properties.bind.handler;


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MockConfigurationPropertySource;


/**
 * Tests for {@link NoUnboundElementsBindHandler}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public class NoUnboundElementsBindHandlerTests {
    private List<ConfigurationPropertySource> sources = new ArrayList<>();

    private Binder binder;

    @Test
    public void bindWhenNotUsingNoUnboundElementsHandlerShouldBind() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("example.foo", "bar");
        source.put("example.baz", "bar");
        this.sources.add(source);
        this.binder = new Binder(this.sources);
        NoUnboundElementsBindHandlerTests.Example bound = this.binder.bind(ConfigurationPropertyName.of("example"), Bindable.of(NoUnboundElementsBindHandlerTests.Example.class)).get();
        assertThat(bound.getFoo()).isEqualTo("bar");
    }

    @Test
    public void bindWhenUsingNoUnboundElementsHandlerShouldBind() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("example.foo", "bar");
        this.sources.add(source);
        this.binder = new Binder(this.sources);
        NoUnboundElementsBindHandlerTests.Example bound = this.binder.bind("example", Bindable.of(NoUnboundElementsBindHandlerTests.Example.class), new NoUnboundElementsBindHandler()).get();
        assertThat(bound.getFoo()).isEqualTo("bar");
    }

    @Test
    public void bindWhenUsingNoUnboundElementsHandlerThrowException() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("example.foo", "bar");
        source.put("example.baz", "bar");
        this.sources.add(source);
        this.binder = new Binder(this.sources);
        assertThatExceptionOfType(BindException.class).isThrownBy(() -> this.binder.bind("example", Bindable.of(.class), new NoUnboundElementsBindHandler())).satisfies(( ex) -> assertThat(ex.getCause().getMessage()).contains("The elements [example.baz] were left unbound"));
    }

    @Test
    public void bindWhenUsingNoUnboundElementsHandlerShouldBindIfPrefixDifferent() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("example.foo", "bar");
        source.put("other.baz", "bar");
        this.sources.add(source);
        this.binder = new Binder(this.sources);
        NoUnboundElementsBindHandlerTests.Example bound = this.binder.bind("example", Bindable.of(NoUnboundElementsBindHandlerTests.Example.class), new NoUnboundElementsBindHandler()).get();
        assertThat(bound.getFoo()).isEqualTo("bar");
    }

    @Test
    public void bindWhenUsingNoUnboundElementsHandlerShouldBindIfUnboundSystemProperties() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("example.foo", "bar");
        source.put("example.other", "baz");
        this.sources.add(source);
        this.binder = new Binder(this.sources);
        NoUnboundElementsBindHandler handler = new NoUnboundElementsBindHandler(BindHandler.DEFAULT, ( configurationPropertySource) -> false);
        NoUnboundElementsBindHandlerTests.Example bound = this.binder.bind("example", Bindable.of(NoUnboundElementsBindHandlerTests.Example.class), handler).get();
        assertThat(bound.getFoo()).isEqualTo("bar");
    }

    public static class Example {
        private String foo;

        public String getFoo() {
            return this.foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }
    }
}

