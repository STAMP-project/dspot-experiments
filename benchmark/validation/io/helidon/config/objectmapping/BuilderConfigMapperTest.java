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


import ConfigNode.ListNode;
import ConfigNode.ObjectNode;
import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import java.net.URI;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static io.helidon.config.objectmapping.FactoryMethodConfigMapperTest.CustomType.from;


/**
 * Tests {@link ConfigMappers} with focus on builder pattern.
 */
public class BuilderConfigMapperTest {
    @Test
    public void testBuilderNoBuildMethod() {
        Config config = Config.empty();
        BuilderConfigMapperTest.BuilderNoBuildMethodBean bean = config.as(BuilderConfigMapperTest.BuilderNoBuildMethodBean.class).get();
        MatcherAssert.assertThat(bean.isOk(), Matchers.is(true));
    }

    @Test
    public void testBuilderBadTypeBuildMethod() {
        Config config = Config.empty();
        BuilderConfigMapperTest.BadTypeBuilderBean bean = config.as(BuilderConfigMapperTest.BadTypeBuilderBean.class).get();
        MatcherAssert.assertThat(bean.isOk(), Matchers.is(true));
    }

    @Test
    public void testTransientBuilder() {
        Config config = Config.empty();
        BuilderConfigMapperTest.TransientBuilderBean bean = config.as(BuilderConfigMapperTest.TransientBuilderBean.class).get();
        MatcherAssert.assertThat(bean.isOk(), Matchers.is(true));
    }

    @Test
    public void testTransientBuildMethod() {
        Config config = Config.empty();
        BuilderConfigMapperTest.TransientBuildMethodBean bean = config.as(BuilderConfigMapperTest.TransientBuildMethodBean.class).get();
        MatcherAssert.assertThat(bean.isOk(), Matchers.is(true));
    }

    @Test
    public void testSettersBuilder() {
        Config config = Config.create(ConfigSources.create(ObjectNode.builder().addValue("app.number", "1").addValue("app.uri", "this:is/my?uri").addValue("app.path", "/this/is/my.path").addList("app.numbers", ListNode.builder().addValue("1").addValue("2").build()).addList("app.uris", ListNode.builder().addValue("this:is/my?uri").addValue("http://another/uri").build()).addList("app.paths", ListNode.builder().addValue("/this/is/my.path").addValue("/and/another.path").build()).build()));
        BuilderConfigMapperTest.SettersBuilderBean bean = config.get("app").as(BuilderConfigMapperTest.SettersBuilderBean.class).get();
        MatcherAssert.assertThat(bean.getNumber(), Matchers.is(1));
        MatcherAssert.assertThat(bean.getUri(), Matchers.is(URI.create("this:is/my?uri")));
        MatcherAssert.assertThat(bean.getCustom(), Matchers.is(from("/this/is/my.path")));
        MatcherAssert.assertThat(bean.getNumbers(), Matchers.contains(1, 2));
        MatcherAssert.assertThat(bean.getUris(), Matchers.contains(URI.create("this:is/my?uri"), URI.create("http://another/uri")));
        MatcherAssert.assertThat(bean.getCustoms(), Matchers.contains(from("/this/is/my.path"), from("/and/another.path")));
    }

    @Test
    public void testFieldsBuilder() {
        Config config = Config.create(ConfigSources.create(ObjectNode.builder().addValue("app.number", "1").addValue("app.uri", "this:is/my?uri").addValue("app.path", "/this/is/my.path").addList("app.numbers", ListNode.builder().addValue("1").addValue("2").build()).addList("app.uris", ListNode.builder().addValue("this:is/my?uri").addValue("http://another/uri").build()).addList("app.paths", ListNode.builder().addValue("/this/is/my.path").addValue("/and/another.path").build()).build()));
        BuilderConfigMapperTest.FieldsBuilderBean bean = config.get("app").as(BuilderConfigMapperTest.FieldsBuilderBean.class).get();
        MatcherAssert.assertThat(bean.getNumber(), Matchers.is(1));
        MatcherAssert.assertThat(bean.getUri(), Matchers.is(URI.create("this:is/my?uri")));
        MatcherAssert.assertThat(bean.getCustom(), Matchers.is(from("/this/is/my.path")));
        MatcherAssert.assertThat(bean.getNumbers(), Matchers.contains(1, 2));
        MatcherAssert.assertThat(bean.getUris(), Matchers.contains(URI.create("this:is/my?uri"), URI.create("http://another/uri")));
        MatcherAssert.assertThat(bean.getCustoms(), Matchers.contains(from("/this/is/my.path"), from("/and/another.path")));
    }

    @Test
    public void testInterfaceBuilder() {
        Config config = Config.create(ConfigSources.create(ObjectNode.builder().addValue("app.number", "1").addValue("app.uri", "this:is/my?uri").addValue("app.path", "/this/is/my.path").addList("app.numbers", ListNode.builder().addValue("1").addValue("2").build()).addList("app.uris", ListNode.builder().addValue("this:is/my?uri").addValue("http://another/uri").build()).addList("app.paths", ListNode.builder().addValue("/this/is/my.path").addValue("/and/another.path").build()).build()));
        BuilderConfigMapperTest.InterfaceBuilderBean bean = config.get("app").as(BuilderConfigMapperTest.InterfaceBuilderBean.class).get();
        MatcherAssert.assertThat(bean.getNumber(), Matchers.is(1));
        MatcherAssert.assertThat(bean.getUri(), Matchers.is(URI.create("this:is/my?uri")));
        MatcherAssert.assertThat(bean.getCustom(), Matchers.is(from("/this/is/my.path")));
        MatcherAssert.assertThat(bean.getNumbers(), Matchers.contains(1, 2));
        MatcherAssert.assertThat(bean.getUris(), Matchers.contains(URI.create("this:is/my?uri"), URI.create("http://another/uri")));
        MatcherAssert.assertThat(bean.getCustoms(), Matchers.contains(from("/this/is/my.path"), from("/and/another.path")));
    }

    @Test
    public void testDefaultsSettersBuilder() {
        Config config = Config.empty();
        BuilderConfigMapperTest.DefaultsSettersBuilderBean bean = config.as(BuilderConfigMapperTest.DefaultsSettersBuilderBean.class).get();
        MatcherAssert.assertThat(bean.getNumber(), Matchers.is(42));
        MatcherAssert.assertThat(bean.getUri(), Matchers.is(URI.create("default:uri")));
        MatcherAssert.assertThat(bean.getCustom(), Matchers.is(from("/tmp/default")));
        MatcherAssert.assertThat(bean.getNumbers(), Matchers.contains(23, 42));
        MatcherAssert.assertThat(bean.getUris(), Matchers.contains(URI.create("default:uri"), URI.create("default:another:uri")));
        MatcherAssert.assertThat(bean.getCustoms(), Matchers.contains(from("/tmp/default"), from("/tmp/another/default")));
    }

    @Test
    public void testDefaultsFieldsBuilder() {
        Config config = Config.empty();
        BuilderConfigMapperTest.DefaultsFieldsBuilderBean bean = config.as(BuilderConfigMapperTest.DefaultsFieldsBuilderBean.class).get();
        MatcherAssert.assertThat(bean.getNumber(), Matchers.is(42));
        MatcherAssert.assertThat(bean.getUri(), Matchers.is(URI.create("default:uri")));
        MatcherAssert.assertThat(bean.getCustom(), Matchers.is(from("/tmp/default")));
        MatcherAssert.assertThat(bean.getNumbers(), Matchers.contains(23, 42));
        MatcherAssert.assertThat(bean.getUris(), Matchers.contains(URI.create("default:uri"), URI.create("default:another:uri")));
        MatcherAssert.assertThat(bean.getCustoms(), Matchers.contains(from("/tmp/default"), from("/tmp/another/default")));
    }

    @Test
    public void testDefaultsInterfaceBuilder() {
        Config config = Config.empty();
        BuilderConfigMapperTest.DefaultsInterfaceBuilderBean bean = config.as(BuilderConfigMapperTest.DefaultsInterfaceBuilderBean.class).get();
        MatcherAssert.assertThat(bean.getNumber(), Matchers.is(42));
        MatcherAssert.assertThat(bean.getUri(), Matchers.is(URI.create("default:uri")));
        MatcherAssert.assertThat(bean.getCustom(), Matchers.is(from("/tmp/default")));
        MatcherAssert.assertThat(bean.getNumbers(), Matchers.contains(23, 42));
        MatcherAssert.assertThat(bean.getUris(), Matchers.contains(URI.create("default:uri"), URI.create("default:another:uri")));
        MatcherAssert.assertThat(bean.getCustoms(), Matchers.contains(from("/tmp/default"), from("/tmp/another/default")));
    }

    // 
    // test beans
    // 
    public static class SettersBuilderBean extends FactoryMethodConfigMapperTest.JavaBean {
        private SettersBuilderBean(int number, URI uri, FactoryMethodConfigMapperTest.CustomType custom, List<Integer> numbers, List<URI> uris, List<FactoryMethodConfigMapperTest.CustomType> customs) {
            super(number, uri, custom, numbers, uris, customs);
        }

        public static BuilderConfigMapperTest.SettersBuilderBean.Builder builder() {
            return new BuilderConfigMapperTest.SettersBuilderBean.Builder();
        }

        public static class Builder {
            private int number;

            private URI uri;

            private FactoryMethodConfigMapperTest.CustomType custom;

            private List<Integer> numbers;

            private List<URI> uris;

            private List<FactoryMethodConfigMapperTest.CustomType> customs;

            private Builder() {
            }

            public void setNumber(int number) {
                this.number = number;
            }

            public void setUri(URI uri) {
                this.uri = uri;
            }

            @Value(key = "path")
            public void setCustom(FactoryMethodConfigMapperTest.CustomType custom) {
                this.custom = custom;
            }

            public void setNumbers(List<Integer> numbers) {
                this.numbers = numbers;
            }

            public void setUris(List<URI> uris) {
                this.uris = uris;
            }

            @Value(key = "paths")
            public void setCustoms(List<FactoryMethodConfigMapperTest.CustomType> customs) {
                this.customs = customs;
            }

            public BuilderConfigMapperTest.SettersBuilderBean build() {
                return new BuilderConfigMapperTest.SettersBuilderBean(number, uri, custom, numbers, uris, customs);
            }
        }
    }

    public static class InterfaceBuilderBean extends FactoryMethodConfigMapperTest.JavaBean {
        private InterfaceBuilderBean(int number, URI uri, FactoryMethodConfigMapperTest.CustomType custom, List<Integer> numbers, List<URI> uris, List<FactoryMethodConfigMapperTest.CustomType> customs) {
            super(number, uri, custom, numbers, uris, customs);
        }

        public static BuilderConfigMapperTest.InterfaceBuilderBean.Builder builder() {
            return new BuilderConfigMapperTest.InterfaceBuilderBean.BuilderImpl();
        }

        public interface Builder {
            void setNumber(int number);

            void setUri(URI uri);

            @Value(key = "path")
            void setCustom(FactoryMethodConfigMapperTest.CustomType custom);

            void setNumbers(List<Integer> numbers);

            void setUris(List<URI> uris);

            @Value(key = "paths")
            void setCustoms(List<FactoryMethodConfigMapperTest.CustomType> customs);

            BuilderConfigMapperTest.InterfaceBuilderBean build();
        }

        private static class BuilderImpl implements BuilderConfigMapperTest.InterfaceBuilderBean.Builder {
            private int number;

            private URI uri;

            private FactoryMethodConfigMapperTest.CustomType custom;

            private List<Integer> numbers;

            private List<URI> uris;

            private List<FactoryMethodConfigMapperTest.CustomType> customs;

            private BuilderImpl() {
            }

            public void setNumber(int number) {
                this.number = number;
            }

            public void setUri(URI uri) {
                this.uri = uri;
            }

            public void setCustom(FactoryMethodConfigMapperTest.CustomType custom) {
                this.custom = custom;
            }

            public void setNumbers(List<Integer> numbers) {
                this.numbers = numbers;
            }

            public void setUris(List<URI> uris) {
                this.uris = uris;
            }

            public void setCustoms(List<FactoryMethodConfigMapperTest.CustomType> customs) {
                this.customs = customs;
            }

            public BuilderConfigMapperTest.InterfaceBuilderBean build() {
                return new BuilderConfigMapperTest.InterfaceBuilderBean(number, uri, custom, numbers, uris, customs);
            }
        }
    }

    public static class FieldsBuilderBean extends FactoryMethodConfigMapperTest.JavaBean {
        private FieldsBuilderBean(int number, URI uri, FactoryMethodConfigMapperTest.CustomType custom, List<Integer> numbers, List<URI> uris, List<FactoryMethodConfigMapperTest.CustomType> customs) {
            super(number, uri, custom, numbers, uris, customs);
        }

        public static BuilderConfigMapperTest.FieldsBuilderBean.Builder builder() {
            return new BuilderConfigMapperTest.FieldsBuilderBean.Builder();
        }

        public static class Builder {
            public int number;

            public URI uri;

            @Value(key = "path")
            public FactoryMethodConfigMapperTest.CustomType custom;

            public List<Integer> numbers;

            public List<URI> uris;

            @Value(key = "paths")
            public List<FactoryMethodConfigMapperTest.CustomType> customs;

            private Builder() {
            }

            public BuilderConfigMapperTest.FieldsBuilderBean build() {
                return new BuilderConfigMapperTest.FieldsBuilderBean(number, uri, custom, numbers, uris, customs);
            }
        }
    }

    public static class DefaultsSettersBuilderBean extends FactoryMethodConfigMapperTest.JavaBean {
        private DefaultsSettersBuilderBean(int number, URI uri, FactoryMethodConfigMapperTest.CustomType custom, List<Integer> numbers, List<URI> uris, List<FactoryMethodConfigMapperTest.CustomType> customs) {
            super(number, uri, custom, numbers, uris, customs);
        }

        public static BuilderConfigMapperTest.DefaultsSettersBuilderBean.Builder builder() {
            return new BuilderConfigMapperTest.DefaultsSettersBuilderBean.Builder();
        }

        public static class Builder {
            private int number;

            private URI uri;

            private FactoryMethodConfigMapperTest.CustomType custom;

            private List<Integer> numbers;

            private List<URI> uris;

            private List<FactoryMethodConfigMapperTest.CustomType> customs;

            private Builder() {
            }

            @Value(withDefault = "42")
            public void setNumber(int number) {
                this.number = number;
            }

            @Value(withDefault = "default:uri")
            public void setUri(URI uri) {
                this.uri = uri;
            }

            @Value(key = "path", withDefaultSupplier = FactoryMethodConfigMapperTest.DefaultCustomTypeSupplier.class)
            public void setCustom(FactoryMethodConfigMapperTest.CustomType custom) {
                this.custom = custom;
            }

            @Value(withDefaultSupplier = FactoryMethodConfigMapperTest.DefaultNumbersSupplier.class)
            public void setNumbers(List<Integer> numbers) {
                this.numbers = numbers;
            }

            @Value(withDefaultSupplier = FactoryMethodConfigMapperTest.DefaultUrisSupplier.class)
            public void setUris(List<URI> uris) {
                this.uris = uris;
            }

            @Value(withDefaultSupplier = FactoryMethodConfigMapperTest.DefaultCustomTypesSupplier.class)
            public void setCustoms(List<FactoryMethodConfigMapperTest.CustomType> customs) {
                this.customs = customs;
            }

            public BuilderConfigMapperTest.DefaultsSettersBuilderBean build() {
                return new BuilderConfigMapperTest.DefaultsSettersBuilderBean(number, uri, custom, numbers, uris, customs);
            }
        }
    }

    public static class DefaultsInterfaceBuilderBean extends FactoryMethodConfigMapperTest.JavaBean {
        private DefaultsInterfaceBuilderBean(int number, URI uri, FactoryMethodConfigMapperTest.CustomType custom, List<Integer> numbers, List<URI> uris, List<FactoryMethodConfigMapperTest.CustomType> customs) {
            super(number, uri, custom, numbers, uris, customs);
        }

        public static BuilderConfigMapperTest.DefaultsInterfaceBuilderBean.Builder builder() {
            return new BuilderConfigMapperTest.DefaultsInterfaceBuilderBean.BuilderImpl();
        }

        public interface Builder {
            @Value(withDefault = "42")
            void setNumber(int number);

            @Value(withDefault = "default:uri")
            void setUri(URI uri);

            @Value(key = "path", withDefaultSupplier = FactoryMethodConfigMapperTest.DefaultCustomTypeSupplier.class)
            void setCustom(FactoryMethodConfigMapperTest.CustomType custom);

            @Value(withDefaultSupplier = FactoryMethodConfigMapperTest.DefaultNumbersSupplier.class)
            void setNumbers(List<Integer> numbers);

            @Value(withDefaultSupplier = FactoryMethodConfigMapperTest.DefaultUrisSupplier.class)
            void setUris(List<URI> uris);

            @Value(withDefaultSupplier = FactoryMethodConfigMapperTest.DefaultCustomTypesSupplier.class)
            void setCustoms(List<FactoryMethodConfigMapperTest.CustomType> customs);

            BuilderConfigMapperTest.DefaultsInterfaceBuilderBean build();
        }

        private static class BuilderImpl implements BuilderConfigMapperTest.DefaultsInterfaceBuilderBean.Builder {
            private int number;

            private URI uri;

            private FactoryMethodConfigMapperTest.CustomType custom;

            private List<Integer> numbers;

            private List<URI> uris;

            private List<FactoryMethodConfigMapperTest.CustomType> customs;

            private BuilderImpl() {
            }

            public void setNumber(int number) {
                this.number = number;
            }

            public void setUri(URI uri) {
                this.uri = uri;
            }

            public void setCustom(FactoryMethodConfigMapperTest.CustomType custom) {
                this.custom = custom;
            }

            public void setNumbers(List<Integer> numbers) {
                this.numbers = numbers;
            }

            public void setUris(List<URI> uris) {
                this.uris = uris;
            }

            public void setCustoms(List<FactoryMethodConfigMapperTest.CustomType> customs) {
                this.customs = customs;
            }

            public BuilderConfigMapperTest.DefaultsInterfaceBuilderBean build() {
                return new BuilderConfigMapperTest.DefaultsInterfaceBuilderBean(number, uri, custom, numbers, uris, customs);
            }
        }
    }

    public static class DefaultsFieldsBuilderBean extends FactoryMethodConfigMapperTest.JavaBean {
        private DefaultsFieldsBuilderBean(int number, URI uri, FactoryMethodConfigMapperTest.CustomType custom, List<Integer> numbers, List<URI> uris, List<FactoryMethodConfigMapperTest.CustomType> customs) {
            super(number, uri, custom, numbers, uris, customs);
        }

        public static BuilderConfigMapperTest.DefaultsFieldsBuilderBean.Builder builder() {
            return new BuilderConfigMapperTest.DefaultsFieldsBuilderBean.Builder();
        }

        public static class Builder {
            @Value(withDefault = "42")
            public int number;

            @Value(withDefault = "default:uri")
            public URI uri;

            @Value(withDefaultSupplier = FactoryMethodConfigMapperTest.DefaultCustomTypeSupplier.class)
            public FactoryMethodConfigMapperTest.CustomType custom;

            @Value(withDefaultSupplier = FactoryMethodConfigMapperTest.DefaultNumbersSupplier.class)
            public List<Integer> numbers;

            @Value(withDefaultSupplier = FactoryMethodConfigMapperTest.DefaultUrisSupplier.class)
            public List<URI> uris;

            @Value(withDefaultSupplier = FactoryMethodConfigMapperTest.DefaultCustomTypesSupplier.class)
            public List<FactoryMethodConfigMapperTest.CustomType> customs;

            private Builder() {
            }

            public BuilderConfigMapperTest.DefaultsFieldsBuilderBean build() {
                return new BuilderConfigMapperTest.DefaultsFieldsBuilderBean(number, uri, custom, numbers, uris, customs);
            }
        }
    }

    public abstract static class TestingBean {
        private final boolean ok;

        protected TestingBean(boolean ok) {
            this.ok = ok;
        }

        public boolean isOk() {
            return ok;
        }
    }

    public static class BuilderNoBuildMethodBean extends BuilderConfigMapperTest.TestingBean {
        private BuilderNoBuildMethodBean(boolean ok) {
            super(ok);
        }

        public BuilderNoBuildMethodBean() {
            this(true);
        }

        public static BuilderConfigMapperTest.BuilderNoBuildMethodBean.NotBuilder builder() {
            return new BuilderConfigMapperTest.BuilderNoBuildMethodBean.NotBuilder();
        }

        public static class NotBuilder {
            private NotBuilder() {
            }

            private BuilderConfigMapperTest.BuilderNoBuildMethodBean build() {
                // ignored
                return new BuilderConfigMapperTest.BuilderNoBuildMethodBean(false);
            }
        }
    }

    public static class BadTypeBuilderBean extends BuilderConfigMapperTest.TestingBean {
        public BadTypeBuilderBean() {
            super(true);
        }

        public static BuilderConfigMapperTest.BadTypeBuilderBean.Builder builder() {
            return new BuilderConfigMapperTest.BadTypeBuilderBean.Builder();
        }

        public static class Builder {
            private Builder() {
            }

            public String build() {
                return "bad-return-type";
            }
        }
    }

    public static class TransientBuilderBean extends BuilderConfigMapperTest.TestingBean {
        private TransientBuilderBean(boolean ok) {
            super(ok);
        }

        public TransientBuilderBean() {
            this(true);
        }

        @Transient
        public static BuilderConfigMapperTest.TransientBuilderBean.Builder builder() {
            return new BuilderConfigMapperTest.TransientBuilderBean.Builder();
        }

        public static class Builder {
            private Builder() {
            }

            public BuilderConfigMapperTest.TransientBuilderBean build() {
                return new BuilderConfigMapperTest.TransientBuilderBean(false);
            }
        }
    }

    public static class TransientBuildMethodBean extends BuilderConfigMapperTest.TestingBean {
        private TransientBuildMethodBean(boolean ok) {
            super(ok);
        }

        public TransientBuildMethodBean() {
            this(true);
        }

        public static BuilderConfigMapperTest.TransientBuildMethodBean.Builder builder() {
            return new BuilderConfigMapperTest.TransientBuildMethodBean.Builder();
        }

        public static class Builder {
            private Builder() {
            }

            @Transient
            public BuilderConfigMapperTest.TransientBuildMethodBean build() {
                return new BuilderConfigMapperTest.TransientBuildMethodBean(false);
            }
        }
    }
}

