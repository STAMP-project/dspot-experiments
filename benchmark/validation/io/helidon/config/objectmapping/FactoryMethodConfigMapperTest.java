/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.config.objectmapping;


import io.helidon.common.CollectionsHelper;
import io.helidon.config.Config;
import io.helidon.config.ConfigMappingException;
import io.helidon.config.ConfigSources;
import io.helidon.config.spi.ConfigNode.ListNode;
import io.helidon.config.spi.ConfigNode.ObjectNode;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Tests {@link ConfigMappers} with focus on factory method and constructor initialization.
 */
public class FactoryMethodConfigMapperTest {
    // 
    // constructor
    // 
    @Test
    public void testAmbiguousConstructors() {
        Config config = Config.empty();
        ConfigMappingException ex = Assertions.assertThrows(ConfigMappingException.class, () -> {
            config.as(FactoryMethodConfigMapperTest.AmbiguousConstructorsBean.class).get();
        });
        MatcherAssert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(CollectionsHelper.listOf(FactoryMethodConfigMapperTest.AmbiguousConstructorsBean.class.getName(), "No mapper configured")));
    }

    @Test
    public void testTransientConstructor() {
        Config config = Config.create(ConfigSources.create(ObjectNode.builder().addValue("app.number", "1").addValue("app.uri", "this:is/my?uri").addValue("app.path", "/this/is/my.path").addValue("app.unused", "true").addList("app.numbers", ListNode.builder().addValue("1").addValue("2").build()).addList("app.uris", ListNode.builder().addValue("this:is/my?uri").addValue("http://another/uri").build()).addList("app.paths", ListNode.builder().addValue("/this/is/my.path").addValue("/and/another.path").build()).build()));
        FactoryMethodConfigMapperTest.ConstructorBean bean = config.get("app").as(FactoryMethodConfigMapperTest.ConstructorBean.class).get();
        MatcherAssert.assertThat(bean.getNumber(), Matchers.is(1));
        MatcherAssert.assertThat(bean.getUri(), Matchers.is(URI.create("this:is/my?uri")));
        MatcherAssert.assertThat(bean.getCustom(), Matchers.is(FactoryMethodConfigMapperTest.CustomType.from("/this/is/my.path")));
        MatcherAssert.assertThat(bean.getNumbers(), Matchers.contains(1, 2));
        MatcherAssert.assertThat(bean.getUris(), Matchers.contains(URI.create("this:is/my?uri"), URI.create("http://another/uri")));
        MatcherAssert.assertThat(bean.getCustoms(), Matchers.contains(FactoryMethodConfigMapperTest.CustomType.from("/this/is/my.path"), FactoryMethodConfigMapperTest.CustomType.from("/and/another.path")));
    }

    @Test
    public void testNoConfigValueConstructor() {
        Config config = Config.create(ConfigSources.create(ObjectNode.builder().addValue("app.arg0", "1").addValue("app.arg1", "this:is/my?uri").addValue("app.arg2", "/this/is/my.path").addList("app.arg3", ListNode.builder().addValue("1").addValue("2").build()).addList("app.arg4", ListNode.builder().addValue("this:is/my?uri").addValue("http://another/uri").build()).addList("app.arg5", ListNode.builder().addValue("/this/is/my.path").addValue("/and/another.path").build()).build()));
        FactoryMethodConfigMapperTest.NoConfigValueConstructorBean bean = config.get("app").as(FactoryMethodConfigMapperTest.NoConfigValueConstructorBean.class).get();
        MatcherAssert.assertThat(bean.getNumber(), Matchers.is(1));
        MatcherAssert.assertThat(bean.getUri(), Matchers.is(URI.create("this:is/my?uri")));
        MatcherAssert.assertThat(bean.getCustom(), Matchers.is(FactoryMethodConfigMapperTest.CustomType.from("/this/is/my.path")));
        MatcherAssert.assertThat(bean.getNumbers(), Matchers.contains(1, 2));
        MatcherAssert.assertThat(bean.getUris(), Matchers.contains(URI.create("this:is/my?uri"), URI.create("http://another/uri")));
        MatcherAssert.assertThat(bean.getCustoms(), Matchers.contains(FactoryMethodConfigMapperTest.CustomType.from("/this/is/my.path"), FactoryMethodConfigMapperTest.CustomType.from("/and/another.path")));
    }

    @Test
    public void testMissingParamsConstructor() {
        Config config = Config.create(ConfigSources.create(CollectionsHelper.mapOf("app.number", "1")));
        ConfigMappingException ex = Assertions.assertThrows(ConfigMappingException.class, () -> {
            config.get("app").as(FactoryMethodConfigMapperTest.ConstructorBean.class).get();
        });
        MatcherAssert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(CollectionsHelper.listOf("'app'", "ConstructorBean", "Missing value for parameter 'uri'.")));
    }

    @Test
    public void testDefaultsConstructor() {
        Config config = Config.create(ConfigSources.create(CollectionsHelper.mapOf("app.number", "1")));
        FactoryMethodConfigMapperTest.DefaultsConstructorBean bean = config.get("app").as(FactoryMethodConfigMapperTest.DefaultsConstructorBean.class).get();
        MatcherAssert.assertThat(bean.getNumber(), Matchers.is(1));
        MatcherAssert.assertThat(bean.getUri(), Matchers.is(URI.create("default:uri")));
        MatcherAssert.assertThat(bean.getCustom(), Matchers.is(FactoryMethodConfigMapperTest.CustomType.from("/tmp/default")));
        MatcherAssert.assertThat(bean.getNumbers(), Matchers.contains(23, 42));
        MatcherAssert.assertThat(bean.getUris(), Matchers.contains(URI.create("default:uri"), URI.create("default:another:uri")));
        MatcherAssert.assertThat(bean.getCustoms(), Matchers.contains(FactoryMethodConfigMapperTest.CustomType.from("/tmp/default"), FactoryMethodConfigMapperTest.CustomType.from("/tmp/another/default")));
    }

    // 
    // static from method
    // 
    @Test
    public void testAmbiguousFromMethods() {
        Config config = Config.empty();
        ConfigMappingException ex = Assertions.assertThrows(ConfigMappingException.class, () -> {
            config.as(FactoryMethodConfigMapperTest.AmbiguousFromMethodsBean.class).get();
        });
        MatcherAssert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(CollectionsHelper.listOf(FactoryMethodConfigMapperTest.AmbiguousFromMethodsBean.class.getName(), "No mapper configured")));
    }

    @Test
    public void testTransientFromMethod() {
        Config config = Config.create(ConfigSources.create(ObjectNode.builder().addValue("app.number", "1").addValue("app.uri", "this:is/my?uri").addValue("app.path", "/this/is/my.path").addValue("app.unused", "true").addList("app.numbers", ListNode.builder().addValue("1").addValue("2").build()).addList("app.uris", ListNode.builder().addValue("this:is/my?uri").addValue("http://another/uri").build()).addList("app.paths", ListNode.builder().addValue("/this/is/my.path").addValue("/and/another.path").build()).build()));
        FactoryMethodConfigMapperTest.FromMethodBean bean = config.get("app").as(FactoryMethodConfigMapperTest.FromMethodBean.class).get();
        MatcherAssert.assertThat(bean.getNumber(), Matchers.is(1));
        MatcherAssert.assertThat(bean.getUri(), Matchers.is(URI.create("this:is/my?uri")));
        MatcherAssert.assertThat(bean.getCustom(), Matchers.is(FactoryMethodConfigMapperTest.CustomType.from("/this/is/my.path")));
        MatcherAssert.assertThat(bean.getNumbers(), Matchers.contains(1, 2));
        MatcherAssert.assertThat(bean.getUris(), Matchers.contains(URI.create("this:is/my?uri"), URI.create("http://another/uri")));
        MatcherAssert.assertThat(bean.getCustoms(), Matchers.contains(FactoryMethodConfigMapperTest.CustomType.from("/this/is/my.path"), FactoryMethodConfigMapperTest.CustomType.from("/and/another.path")));
    }

    @Test
    public void testNoConfigValueFromMethod() {
        Config config = Config.create(ConfigSources.create(ObjectNode.builder().addValue("app.arg0", "1").addValue("app.arg1", "this:is/my?uri").addValue("app.arg2", "/this/is/my.path").addList("app.arg3", ListNode.builder().addValue("1").addValue("2").build()).addList("app.arg4", ListNode.builder().addValue("this:is/my?uri").addValue("http://another/uri").build()).addList("app.arg5", ListNode.builder().addValue("/this/is/my.path").addValue("/and/another.path").build()).build()));
        FactoryMethodConfigMapperTest.NoConfigValueFromMethodBean bean = config.get("app").as(FactoryMethodConfigMapperTest.NoConfigValueFromMethodBean.class).get();
        MatcherAssert.assertThat(bean.getNumber(), Matchers.is(1));
        MatcherAssert.assertThat(bean.getUri(), Matchers.is(URI.create("this:is/my?uri")));
        MatcherAssert.assertThat(bean.getCustom(), Matchers.is(FactoryMethodConfigMapperTest.CustomType.from("/this/is/my.path")));
        MatcherAssert.assertThat(bean.getNumbers(), Matchers.contains(1, 2));
        MatcherAssert.assertThat(bean.getUris(), Matchers.contains(URI.create("this:is/my?uri"), URI.create("http://another/uri")));
        MatcherAssert.assertThat(bean.getCustoms(), Matchers.contains(FactoryMethodConfigMapperTest.CustomType.from("/this/is/my.path"), FactoryMethodConfigMapperTest.CustomType.from("/and/another.path")));
    }

    @Test
    public void testMissingParamsFromMethod() {
        Config config = Config.create(ConfigSources.create(CollectionsHelper.mapOf("app.number", "1")));
        ConfigMappingException ex = Assertions.assertThrows(ConfigMappingException.class, () -> {
            config.get("app").as(FactoryMethodConfigMapperTest.FromMethodBean.class).get();
        });
        MatcherAssert.assertThat(ex.getMessage(), Matchers.stringContainsInOrder(CollectionsHelper.listOf("'app'", "FromMethodBean", "Missing value for parameter 'uri'.")));
    }

    @Test
    public void testDefaultsFromMethod() {
        Config config = Config.create(ConfigSources.create(CollectionsHelper.mapOf("app.number", "1")));
        FactoryMethodConfigMapperTest.DefaultsFromMethodBean bean = config.get("app").as(FactoryMethodConfigMapperTest.DefaultsFromMethodBean.class).get();
        MatcherAssert.assertThat(bean.getNumber(), Matchers.is(1));
        MatcherAssert.assertThat(bean.getUri(), Matchers.is(URI.create("default:uri")));
        MatcherAssert.assertThat(bean.getCustom(), Matchers.is(FactoryMethodConfigMapperTest.CustomType.from("/tmp/default")));
        MatcherAssert.assertThat(bean.getNumbers(), Matchers.contains(23, 42));
        MatcherAssert.assertThat(bean.getUris(), Matchers.contains(URI.create("default:uri"), URI.create("default:another:uri")));
        MatcherAssert.assertThat(bean.getCustoms(), Matchers.contains(FactoryMethodConfigMapperTest.CustomType.from("/tmp/default"), FactoryMethodConfigMapperTest.CustomType.from("/tmp/another/default")));
    }

    // 
    // test beans
    // 
    public abstract static class JavaBean {
        private final int number;

        private final URI uri;

        private final FactoryMethodConfigMapperTest.CustomType custom;

        private final List<Integer> numbers;

        private final List<URI> uris;

        private final List<FactoryMethodConfigMapperTest.CustomType> customs;

        protected JavaBean(int number, URI uri, FactoryMethodConfigMapperTest.CustomType custom, List<Integer> numbers, List<URI> uris, List<FactoryMethodConfigMapperTest.CustomType> customs) {
            this.number = number;
            this.uri = uri;
            this.custom = custom;
            this.numbers = numbers;
            this.uris = uris;
            this.customs = customs;
        }

        public int getNumber() {
            return number;
        }

        public URI getUri() {
            return uri;
        }

        public FactoryMethodConfigMapperTest.CustomType getCustom() {
            return custom;
        }

        public List<Integer> getNumbers() {
            return numbers;
        }

        public List<URI> getUris() {
            return uris;
        }

        public List<FactoryMethodConfigMapperTest.CustomType> getCustoms() {
            return customs;
        }
    }

    public static class CustomType {
        private final Path path;

        private CustomType(Path path) {
            this.path = path;
        }

        public Path getPath() {
            return path;
        }

        public static FactoryMethodConfigMapperTest.CustomType from(String value) {
            return new FactoryMethodConfigMapperTest.CustomType(Paths.get(value));
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            FactoryMethodConfigMapperTest.CustomType that = ((FactoryMethodConfigMapperTest.CustomType) (o));
            return Objects.equals(path, that.path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path);
        }
    }

    public static class AmbiguousFromMethodsBean extends FactoryMethodConfigMapperTest.JavaBean {
        private AmbiguousFromMethodsBean(int number, URI uri, FactoryMethodConfigMapperTest.CustomType custom, List<Integer> numbers, List<URI> uris, List<FactoryMethodConfigMapperTest.CustomType> customs) {
            super(number, uri, custom, numbers, uris, customs);
        }

        public static FactoryMethodConfigMapperTest.AmbiguousFromMethodsBean from(int number, URI uri, FactoryMethodConfigMapperTest.CustomType custom) {
            return new FactoryMethodConfigMapperTest.AmbiguousFromMethodsBean(number, uri, custom, null, null, null);
        }

        public static FactoryMethodConfigMapperTest.AmbiguousFromMethodsBean from(int number, URI uri, FactoryMethodConfigMapperTest.CustomType custom, List<Integer> numbers, List<URI> uris, List<FactoryMethodConfigMapperTest.CustomType> customs) {
            return new FactoryMethodConfigMapperTest.AmbiguousFromMethodsBean(number, uri, custom, numbers, uris, customs);
        }
    }

    public static class AmbiguousConstructorsBean extends FactoryMethodConfigMapperTest.JavaBean {
        public AmbiguousConstructorsBean(int number, URI uri, FactoryMethodConfigMapperTest.CustomType custom) {
            super(number, uri, custom, null, null, null);
        }

        public AmbiguousConstructorsBean(int number, URI uri, FactoryMethodConfigMapperTest.CustomType custom, List<Integer> numbers, List<URI> uris, List<FactoryMethodConfigMapperTest.CustomType> customs) {
            super(number, uri, custom, numbers, uris, customs);
        }
    }

    public static class FromMethodBean extends FactoryMethodConfigMapperTest.JavaBean {
        private FromMethodBean(int number, URI uri, FactoryMethodConfigMapperTest.CustomType custom, List<Integer> numbers, List<URI> uris, List<FactoryMethodConfigMapperTest.CustomType> customs) {
            super(number, uri, custom, numbers, uris, customs);
        }

        @Transient
        public static FactoryMethodConfigMapperTest.FromMethodBean from(int number, URI uri, FactoryMethodConfigMapperTest.CustomType custom) {
            return new FactoryMethodConfigMapperTest.FromMethodBean(number, uri, custom, null, null, null);
        }

        public static FactoryMethodConfigMapperTest.FromMethodBean from(@Value(key = "number")
        int number, @Value(key = "uri")
        URI uri, @Value(key = "path")
        FactoryMethodConfigMapperTest.CustomType custom, @Value(key = "numbers")
        List<Integer> numbers, @Value(key = "uris")
        List<URI> uris, @Value(key = "paths")
        List<FactoryMethodConfigMapperTest.CustomType> customs) {
            return new FactoryMethodConfigMapperTest.FromMethodBean(number, uri, custom, numbers, uris, customs);
        }
    }

    public static class NoConfigValueFromMethodBean extends FactoryMethodConfigMapperTest.JavaBean {
        private NoConfigValueFromMethodBean(int number, URI uri, FactoryMethodConfigMapperTest.CustomType custom, List<Integer> numbers, List<URI> uris, List<FactoryMethodConfigMapperTest.CustomType> customs) {
            super(number, uri, custom, numbers, uris, customs);
        }

        public static FactoryMethodConfigMapperTest.NoConfigValueFromMethodBean from(int number, URI uri, FactoryMethodConfigMapperTest.CustomType custom, List<Integer> numbers, List<URI> uris, List<FactoryMethodConfigMapperTest.CustomType> customs) {
            return new FactoryMethodConfigMapperTest.NoConfigValueFromMethodBean(number, uri, custom, numbers, uris, customs);
        }
    }

    public static class DefaultsFromMethodBean extends FactoryMethodConfigMapperTest.JavaBean {
        public DefaultsFromMethodBean() {
            super((-1), null, null, null, null, null);
            throw new IllegalStateException("The constructor should be ignored.");
        }

        @Transient
        public DefaultsFromMethodBean(Config config) {
            super((-1), null, null, null, null, null);
            throw new IllegalStateException("The constructor should be ignored.");
        }

        private DefaultsFromMethodBean(int number, URI uri, FactoryMethodConfigMapperTest.CustomType custom, List<Integer> numbers, List<URI> uris, List<FactoryMethodConfigMapperTest.CustomType> customs) {
            super(number, uri, custom, numbers, uris, customs);
        }

        public static FactoryMethodConfigMapperTest.DefaultsFromMethodBean from(@Value(key = "number", withDefault = "42")
        int number, @Value(key = "uri", withDefault = "default:uri")
        URI uri, @Value(key = "path", withDefaultSupplier = FactoryMethodConfigMapperTest.DefaultCustomTypeSupplier.class)
        FactoryMethodConfigMapperTest.CustomType custom, @Value(key = "numbers", withDefaultSupplier = FactoryMethodConfigMapperTest.DefaultNumbersSupplier.class)
        List<Integer> numbers, @Value(key = "uris", withDefaultSupplier = FactoryMethodConfigMapperTest.DefaultUrisSupplier.class)
        List<URI> uris, @Value(key = "paths", withDefaultSupplier = FactoryMethodConfigMapperTest.DefaultCustomTypesSupplier.class)
        List<FactoryMethodConfigMapperTest.CustomType> customs) {
            return new FactoryMethodConfigMapperTest.DefaultsFromMethodBean(number, uri, custom, numbers, uris, customs);
        }

        @Transient
        public FactoryMethodConfigMapperTest.DefaultsFromMethodBean from(Config config) {
            throw new IllegalStateException("The method should be ignored.");
        }
    }

    public static class ConstructorBean extends FactoryMethodConfigMapperTest.JavaBean {
        @Transient
        public ConstructorBean(int number, URI uri, FactoryMethodConfigMapperTest.CustomType custom) {
            super(number, uri, custom, null, null, null);
        }

        public ConstructorBean(@Value(key = "number")
        int number, @Value(key = "uri")
        URI uri, @Value(key = "path")
        FactoryMethodConfigMapperTest.CustomType custom, @Value(key = "numbers")
        List<Integer> numbers, @Value(key = "uris")
        List<URI> uris, @Value(key = "paths")
        List<FactoryMethodConfigMapperTest.CustomType> customs) {
            super(number, uri, custom, numbers, uris, customs);
        }
    }

    public static class NoConfigValueConstructorBean extends FactoryMethodConfigMapperTest.JavaBean {
        public NoConfigValueConstructorBean(int number, URI uri, FactoryMethodConfigMapperTest.CustomType custom, List<Integer> numbers, List<URI> uris, List<FactoryMethodConfigMapperTest.CustomType> customs) {
            super(number, uri, custom, numbers, uris, customs);
        }
    }

    public static class DefaultsConstructorBean extends FactoryMethodConfigMapperTest.JavaBean {
        @Transient
        public DefaultsConstructorBean(Config config) {
            super((-1), null, null, null, null, null);
            throw new IllegalStateException("The constructor should be ignored.");
        }

        public DefaultsConstructorBean(@Value(key = "number", withDefault = "42")
        int number, @Value(key = "uri", withDefault = "default:uri")
        URI uri, @Value(key = "path", withDefaultSupplier = FactoryMethodConfigMapperTest.DefaultCustomTypeSupplier.class)
        FactoryMethodConfigMapperTest.CustomType custom, @Value(key = "numbers", withDefaultSupplier = FactoryMethodConfigMapperTest.DefaultNumbersSupplier.class)
        List<Integer> numbers, @Value(key = "uris", withDefaultSupplier = FactoryMethodConfigMapperTest.DefaultUrisSupplier.class)
        List<URI> uris, @Value(key = "paths", withDefaultSupplier = FactoryMethodConfigMapperTest.DefaultCustomTypesSupplier.class)
        List<FactoryMethodConfigMapperTest.CustomType> customs) {
            super(number, uri, custom, numbers, uris, customs);
        }

        public DefaultsConstructorBean() {
            super((-1), null, null, null, null, null);
            throw new IllegalStateException("The constructor should be ignored.");
        }

        @Transient
        public FactoryMethodConfigMapperTest.DefaultsConstructorBean from(Config config) {
            throw new IllegalStateException("The method should be ignored.");
        }
    }

    public static class DefaultCustomTypeSupplier implements Supplier<FactoryMethodConfigMapperTest.CustomType> {
        @Override
        public FactoryMethodConfigMapperTest.CustomType get() {
            return FactoryMethodConfigMapperTest.CustomType.from("/tmp/default");
        }
    }

    public static class DefaultCustomTypesSupplier implements Supplier<List<FactoryMethodConfigMapperTest.CustomType>> {
        @Override
        public List<FactoryMethodConfigMapperTest.CustomType> get() {
            return CollectionsHelper.listOf(FactoryMethodConfigMapperTest.CustomType.from("/tmp/default"), FactoryMethodConfigMapperTest.CustomType.from("/tmp/another/default"));
        }
    }

    public static class DefaultUrisSupplier implements Supplier<List<URI>> {
        @Override
        public List<URI> get() {
            return CollectionsHelper.listOf(URI.create("default:uri"), URI.create("default:another:uri"));
        }
    }

    public static class DefaultNumbersSupplier implements Supplier<List<Integer>> {
        @Override
        public List<Integer> get() {
            return CollectionsHelper.listOf(23, 42);
        }
    }
}

