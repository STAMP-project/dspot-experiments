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
package org.springframework.boot.context.properties.bind;


import DateTimeFormat.ISO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.springframework.boot.context.properties.bind.handler.IgnoreErrorsBindHandler;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MockConfigurationPropertySource;
import org.springframework.boot.convert.Delimiter;
import org.springframework.format.annotation.DateTimeFormat;


/**
 * Tests for {@link JavaBeanBinder}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public class JavaBeanBinderTests {
    private List<ConfigurationPropertySource> sources = new ArrayList<>();

    private Binder binder;

    @Test
    public void bindToClassShouldCreateBoundBean() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.int-value", "12");
        source.put("foo.long-value", "34");
        source.put("foo.string-value", "foo");
        source.put("foo.enum-value", "foo-bar");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleValueBean bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleValueBean.class)).get();
        assertThat(bean.getIntValue()).isEqualTo(12);
        assertThat(bean.getLongValue()).isEqualTo(34);
        assertThat(bean.getStringValue()).isEqualTo("foo");
        assertThat(bean.getEnumValue()).isEqualTo(JavaBeanBinderTests.ExampleEnum.FOO_BAR);
    }

    @Test
    public void bindToClassWhenHasNoPrefixShouldCreateBoundBean() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("int-value", "12");
        source.put("long-value", "34");
        source.put("string-value", "foo");
        source.put("enum-value", "foo-bar");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleValueBean bean = this.binder.bind(ConfigurationPropertyName.of(""), Bindable.of(JavaBeanBinderTests.ExampleValueBean.class)).get();
        assertThat(bean.getIntValue()).isEqualTo(12);
        assertThat(bean.getLongValue()).isEqualTo(34);
        assertThat(bean.getStringValue()).isEqualTo("foo");
        assertThat(bean.getEnumValue()).isEqualTo(JavaBeanBinderTests.ExampleEnum.FOO_BAR);
    }

    @Test
    public void bindToInstanceShouldBindToInstance() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.int-value", "12");
        source.put("foo.long-value", "34");
        source.put("foo.string-value", "foo");
        source.put("foo.enum-value", "foo-bar");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleValueBean bean = new JavaBeanBinderTests.ExampleValueBean();
        JavaBeanBinderTests.ExampleValueBean boundBean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleValueBean.class).withExistingValue(bean)).get();
        assertThat(boundBean).isSameAs(bean);
        assertThat(bean.getIntValue()).isEqualTo(12);
        assertThat(bean.getLongValue()).isEqualTo(34);
        assertThat(bean.getStringValue()).isEqualTo("foo");
        assertThat(bean.getEnumValue()).isEqualTo(JavaBeanBinderTests.ExampleEnum.FOO_BAR);
    }

    @Test
    public void bindToInstanceWithNoPropertiesShouldReturnUnbound() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        this.sources.add(source);
        JavaBeanBinderTests.ExampleDefaultsBean bean = new JavaBeanBinderTests.ExampleDefaultsBean();
        BindResult<JavaBeanBinderTests.ExampleDefaultsBean> boundBean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleDefaultsBean.class).withExistingValue(bean));
        assertThat(boundBean.isBound()).isFalse();
        assertThat(bean.getFoo()).isEqualTo(123);
        assertThat(bean.getBar()).isEqualTo(456);
    }

    @Test
    public void bindToClassShouldLeaveDefaults() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.bar", "999");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleDefaultsBean bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleDefaultsBean.class)).get();
        assertThat(bean.getFoo()).isEqualTo(123);
        assertThat(bean.getBar()).isEqualTo(999);
    }

    @Test
    public void bindToExistingInstanceShouldLeaveDefaults() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.bar", "999");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleDefaultsBean bean = new JavaBeanBinderTests.ExampleDefaultsBean();
        bean.setFoo(888);
        JavaBeanBinderTests.ExampleDefaultsBean boundBean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleDefaultsBean.class).withExistingValue(bean)).get();
        assertThat(boundBean).isSameAs(bean);
        assertThat(bean.getFoo()).isEqualTo(888);
        assertThat(bean.getBar()).isEqualTo(999);
    }

    @Test
    public void bindToClassShouldBindToMap() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.map.foo-bar", "1");
        source.put("foo.map.bar-baz", "2");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleMapBean bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleMapBean.class)).get();
        assertThat(bean.getMap()).containsExactly(entry(JavaBeanBinderTests.ExampleEnum.FOO_BAR, 1), entry(JavaBeanBinderTests.ExampleEnum.BAR_BAZ, 2));
    }

    @Test
    public void bindToClassShouldBindToList() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.list[0]", "foo-bar");
        source.put("foo.list[1]", "bar-baz");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleListBean bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleListBean.class)).get();
        assertThat(bean.getList()).containsExactly(JavaBeanBinderTests.ExampleEnum.FOO_BAR, JavaBeanBinderTests.ExampleEnum.BAR_BAZ);
    }

    @Test
    public void bindToListIfUnboundElementsPresentShouldThrowException() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.list[0]", "foo-bar");
        source.put("foo.list[2]", "bar-baz");
        this.sources.add(source);
        assertThatExceptionOfType(BindException.class).isThrownBy(() -> this.binder.bind("foo", Bindable.of(.class))).withCauseInstanceOf(UnboundConfigurationPropertiesException.class);
    }

    @Test
    public void bindToClassShouldBindToSet() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.set[0]", "foo-bar");
        source.put("foo.set[1]", "bar-baz");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleSetBean bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleSetBean.class)).get();
        assertThat(bean.getSet()).containsExactly(JavaBeanBinderTests.ExampleEnum.FOO_BAR, JavaBeanBinderTests.ExampleEnum.BAR_BAZ);
    }

    @Test
    public void bindToClassShouldBindToCollection() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.collection[0]", "foo-bar");
        source.put("foo.collection[1]", "bar-baz");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleCollectionBean bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleCollectionBean.class)).get();
        assertThat(bean.getCollection()).containsExactly(JavaBeanBinderTests.ExampleEnum.FOO_BAR, JavaBeanBinderTests.ExampleEnum.BAR_BAZ);
    }

    @Test
    public void bindToClassShouldBindToCollectionWithDelimiter() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.collection", "foo-bar|bar-baz");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleCollectionBeanWithDelimiter bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleCollectionBeanWithDelimiter.class)).get();
        assertThat(bean.getCollection()).containsExactly(JavaBeanBinderTests.ExampleEnum.FOO_BAR, JavaBeanBinderTests.ExampleEnum.BAR_BAZ);
    }

    @Test
    public void bindToClassWhenHasNoSetterShouldBindToMap() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.map.foo-bar", "1");
        source.put("foo.map.bar-baz", "2");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleMapBeanWithoutSetter bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleMapBeanWithoutSetter.class)).get();
        assertThat(bean.getMap()).containsExactly(entry(JavaBeanBinderTests.ExampleEnum.FOO_BAR, 1), entry(JavaBeanBinderTests.ExampleEnum.BAR_BAZ, 2));
    }

    @Test
    public void bindToClassWhenHasNoSetterShouldBindToList() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.list[0]", "foo-bar");
        source.put("foo.list[1]", "bar-baz");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleListBeanWithoutSetter bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleListBeanWithoutSetter.class)).get();
        assertThat(bean.getList()).containsExactly(JavaBeanBinderTests.ExampleEnum.FOO_BAR, JavaBeanBinderTests.ExampleEnum.BAR_BAZ);
    }

    @Test
    public void bindToClassWhenHasNoSetterShouldBindToSet() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.set[0]", "foo-bar");
        source.put("foo.set[1]", "bar-baz");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleSetBeanWithoutSetter bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleSetBeanWithoutSetter.class)).get();
        assertThat(bean.getSet()).containsExactly(JavaBeanBinderTests.ExampleEnum.FOO_BAR, JavaBeanBinderTests.ExampleEnum.BAR_BAZ);
    }

    @Test
    public void bindToClassWhenHasNoSetterShouldBindToCollection() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.collection[0]", "foo-bar");
        source.put("foo.collection[1]", "bar-baz");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleCollectionBeanWithoutSetter bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleCollectionBeanWithoutSetter.class)).get();
        assertThat(bean.getCollection()).containsExactly(JavaBeanBinderTests.ExampleEnum.FOO_BAR, JavaBeanBinderTests.ExampleEnum.BAR_BAZ);
    }

    @Test
    public void bindToClassShouldBindNested() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.value-bean.int-value", "123");
        source.put("foo.value-bean.string-value", "foo");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleNestedBean bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleNestedBean.class)).get();
        assertThat(bean.getValueBean().getIntValue()).isEqualTo(123);
        assertThat(bean.getValueBean().getStringValue()).isEqualTo("foo");
    }

    @Test
    public void bindToClassWhenIterableShouldBindNestedBasedOnInstance() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.value-bean.int-value", "123");
        source.put("foo.value-bean.string-value", "foo");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleNestedBeanWithoutSetterOrType bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleNestedBeanWithoutSetterOrType.class)).get();
        JavaBeanBinderTests.ExampleValueBean valueBean = ((JavaBeanBinderTests.ExampleValueBean) (bean.getValueBean()));
        assertThat(valueBean.getIntValue()).isEqualTo(123);
        assertThat(valueBean.getStringValue()).isEqualTo("foo");
    }

    @Test
    public void bindToClassWhenNotIterableShouldNotBindNestedBasedOnInstance() {
        // If we can't tell that binding will happen, we don't want to randomly invoke
        // getters on the class and cause side effects
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.value-bean.int-value", "123");
        source.put("foo.value-bean.string-value", "foo");
        this.sources.add(source.nonIterable());
        BindResult<JavaBeanBinderTests.ExampleNestedBeanWithoutSetterOrType> bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleNestedBeanWithoutSetterOrType.class));
        assertThat(bean.isBound()).isFalse();
    }

    @Test
    public void bindToClassWhenHasNoSetterShouldBindNested() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.value-bean.int-value", "123");
        source.put("foo.value-bean.string-value", "foo");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleNestedBeanWithoutSetter bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleNestedBeanWithoutSetter.class)).get();
        assertThat(bean.getValueBean().getIntValue()).isEqualTo(123);
        assertThat(bean.getValueBean().getStringValue()).isEqualTo("foo");
    }

    @Test
    public void bindToClassWhenHasNoSetterAndImmutableShouldThrowException() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.nested.foo", "bar");
        this.sources.add(source);
        assertThatExceptionOfType(BindException.class).isThrownBy(() -> this.binder.bind("foo", Bindable.of(.class)));
    }

    @Test
    public void bindToInstanceWhenNoNestedShouldLeaveNestedAsNull() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("faf.value-bean.int-value", "123");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleNestedBean bean = new JavaBeanBinderTests.ExampleNestedBean();
        BindResult<JavaBeanBinderTests.ExampleNestedBean> boundBean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleNestedBean.class).withExistingValue(bean));
        assertThat(boundBean.isBound()).isFalse();
        assertThat(bean.getValueBean()).isNull();
    }

    @Test
    public void bindToClassWhenPropertiesMissingShouldReturnUnbound() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("faf.int-value", "12");
        this.sources.add(source);
        BindResult<JavaBeanBinderTests.ExampleValueBean> bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleValueBean.class));
        assertThat(bean.isBound()).isFalse();
    }

    @Test
    public void bindToClassWhenNoDefaultConstructorShouldBind() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.value", "bar");
        this.sources.add(source);
        BindResult<JavaBeanBinderTests.ExampleWithNonDefaultConstructor> bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleWithNonDefaultConstructor.class));
        assertThat(bean.isBound()).isTrue();
        JavaBeanBinderTests.ExampleWithNonDefaultConstructor boundBean = bean.get();
        assertThat(boundBean.getValue()).isEqualTo("bar");
    }

    @Test
    public void bindToInstanceWhenNoDefaultConstructorShouldBind() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.value", "bar");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleWithNonDefaultConstructor bean = new JavaBeanBinderTests.ExampleWithNonDefaultConstructor("faf");
        JavaBeanBinderTests.ExampleWithNonDefaultConstructor boundBean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleWithNonDefaultConstructor.class).withExistingValue(bean)).get();
        assertThat(boundBean).isSameAs(bean);
        assertThat(bean.getValue()).isEqualTo("bar");
    }

    @Test
    public void bindToClassShouldBindHierarchy() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.int-value", "123");
        source.put("foo.long-value", "456");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleSubclassBean bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleSubclassBean.class)).get();
        assertThat(bean.getIntValue()).isEqualTo(123);
        assertThat(bean.getLongValue()).isEqualTo(456);
    }

    @Test
    public void bindToClassWhenPropertyCannotBeConvertedShouldThrowException() {
        this.sources.add(new MockConfigurationPropertySource("foo.int-value", "foo"));
        assertThatExceptionOfType(BindException.class).isThrownBy(() -> this.binder.bind("foo", Bindable.of(.class)));
    }

    @Test
    public void bindToClassWhenPropertyCannotBeConvertedAndIgnoreErrorsShouldNotSetValue() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.int-value", "12");
        source.put("foo.long-value", "bang");
        source.put("foo.string-value", "foo");
        source.put("foo.enum-value", "foo-bar");
        this.sources.add(source);
        IgnoreErrorsBindHandler handler = new IgnoreErrorsBindHandler();
        JavaBeanBinderTests.ExampleValueBean bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleValueBean.class), handler).get();
        assertThat(bean.getIntValue()).isEqualTo(12);
        assertThat(bean.getLongValue()).isEqualTo(0);
        assertThat(bean.getStringValue()).isEqualTo("foo");
        assertThat(bean.getEnumValue()).isEqualTo(JavaBeanBinderTests.ExampleEnum.FOO_BAR);
    }

    @Test
    public void bindToClassWhenMismatchedGetSetShouldBind() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.value", "123");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleMismatchBean bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleMismatchBean.class)).get();
        assertThat(bean.getValue()).isEqualTo("123");
    }

    @Test
    public void bindToClassShouldNotInvokeExtraMethods() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource("foo.value", "123");
        this.sources.add(source.nonIterable());
        JavaBeanBinderTests.ExampleWithThrowingGetters bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleWithThrowingGetters.class)).get();
        assertThat(bean.getValue()).isEqualTo(123);
    }

    @Test
    public void bindToClassWithSelfReferenceShouldBind() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.value", "123");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleWithSelfReference bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleWithSelfReference.class)).get();
        assertThat(bean.getValue()).isEqualTo(123);
    }

    @Test
    public void bindToInstanceWithExistingValueShouldReturnUnbound() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        this.sources.add(source);
        JavaBeanBinderTests.ExampleNestedBean existingValue = new JavaBeanBinderTests.ExampleNestedBean();
        JavaBeanBinderTests.ExampleValueBean valueBean = new JavaBeanBinderTests.ExampleValueBean();
        existingValue.setValueBean(valueBean);
        BindResult<JavaBeanBinderTests.ExampleNestedBean> result = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleNestedBean.class).withExistingValue(existingValue));
        assertThat(result.isBound()).isFalse();
    }

    @Test
    public void bindWithAnnotations() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.date", "2014-04-01");
        this.sources.add(source);
        JavaBeanBinderTests.ConverterAnnotatedExampleBean bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ConverterAnnotatedExampleBean.class)).get();
        assertThat(bean.getDate().toString()).isEqualTo("2014-04-01");
    }

    @Test
    public void bindWhenValueIsConvertedWithPropertyEditorShouldBind() {
        // gh-12166
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.value", "java.lang.RuntimeException");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleWithPropertyEditorType bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleWithPropertyEditorType.class)).get();
        assertThat(bean.getValue()).isEqualTo(RuntimeException.class);
    }

    @Test
    public void bindToClassShouldIgnoreInvalidAccessors() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.name", "something");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleWithInvalidAccessors bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleWithInvalidAccessors.class)).get();
        assertThat(bean.getName()).isEqualTo("something");
    }

    @Test
    public void bindToClassShouldIgnoreStaticAccessors() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.name", "invalid");
        source.put("foo.counter", "42");
        this.sources.add(source);
        JavaBeanBinderTests.ExampleWithStaticAccessors bean = this.binder.bind("foo", Bindable.of(JavaBeanBinderTests.ExampleWithStaticAccessors.class)).get();
        assertThat(JavaBeanBinderTests.ExampleWithStaticAccessors.name).isNull();
        assertThat(bean.getCounter()).isEqualTo(42);
    }

    public static class ExampleValueBean {
        private int intValue;

        private long longValue;

        private String stringValue;

        private JavaBeanBinderTests.ExampleEnum enumValue;

        public int getIntValue() {
            return this.intValue;
        }

        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }

        public long getLongValue() {
            return this.longValue;
        }

        public void setLongValue(long longValue) {
            this.longValue = longValue;
        }

        public String getStringValue() {
            return this.stringValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        public JavaBeanBinderTests.ExampleEnum getEnumValue() {
            return this.enumValue;
        }

        public void setEnumValue(JavaBeanBinderTests.ExampleEnum enumValue) {
            this.enumValue = enumValue;
        }
    }

    public static class ExampleDefaultsBean {
        private int foo = 123;

        private int bar = 456;

        public int getFoo() {
            return this.foo;
        }

        public void setFoo(int foo) {
            this.foo = foo;
        }

        public int getBar() {
            return this.bar;
        }

        public void setBar(int bar) {
            this.bar = bar;
        }
    }

    public static class ExampleMapBean {
        private Map<JavaBeanBinderTests.ExampleEnum, Integer> map;

        public Map<JavaBeanBinderTests.ExampleEnum, Integer> getMap() {
            return this.map;
        }

        public void setMap(Map<JavaBeanBinderTests.ExampleEnum, Integer> map) {
            this.map = map;
        }
    }

    public static class ExampleListBean {
        private List<JavaBeanBinderTests.ExampleEnum> list;

        public List<JavaBeanBinderTests.ExampleEnum> getList() {
            return this.list;
        }

        public void setList(List<JavaBeanBinderTests.ExampleEnum> list) {
            this.list = list;
        }
    }

    public static class ExampleSetBean {
        private Set<JavaBeanBinderTests.ExampleEnum> set;

        public Set<JavaBeanBinderTests.ExampleEnum> getSet() {
            return this.set;
        }

        public void setSet(Set<JavaBeanBinderTests.ExampleEnum> set) {
            this.set = set;
        }
    }

    public static class ExampleCollectionBean {
        private Collection<JavaBeanBinderTests.ExampleEnum> collection;

        public Collection<JavaBeanBinderTests.ExampleEnum> getCollection() {
            return this.collection;
        }

        public void setCollection(Collection<JavaBeanBinderTests.ExampleEnum> collection) {
            this.collection = collection;
        }
    }

    public static class ExampleMapBeanWithoutSetter {
        private Map<JavaBeanBinderTests.ExampleEnum, Integer> map = new LinkedHashMap<>();

        public Map<JavaBeanBinderTests.ExampleEnum, Integer> getMap() {
            return this.map;
        }
    }

    public static class ExampleListBeanWithoutSetter {
        private List<JavaBeanBinderTests.ExampleEnum> list = new ArrayList<>();

        public List<JavaBeanBinderTests.ExampleEnum> getList() {
            return this.list;
        }
    }

    public static class ExampleSetBeanWithoutSetter {
        private Set<JavaBeanBinderTests.ExampleEnum> set = new LinkedHashSet<>();

        public Set<JavaBeanBinderTests.ExampleEnum> getSet() {
            return this.set;
        }
    }

    public static class ExampleCollectionBeanWithoutSetter {
        private Collection<JavaBeanBinderTests.ExampleEnum> collection = new ArrayList<>();

        public Collection<JavaBeanBinderTests.ExampleEnum> getCollection() {
            return this.collection;
        }
    }

    public static class ExampleCollectionBeanWithDelimiter {
        @Delimiter("|")
        private Collection<JavaBeanBinderTests.ExampleEnum> collection;

        public Collection<JavaBeanBinderTests.ExampleEnum> getCollection() {
            return this.collection;
        }

        public void setCollection(Collection<JavaBeanBinderTests.ExampleEnum> collection) {
            this.collection = collection;
        }
    }

    public static class ExampleNestedBean {
        private JavaBeanBinderTests.ExampleValueBean valueBean;

        public JavaBeanBinderTests.ExampleValueBean getValueBean() {
            return this.valueBean;
        }

        public void setValueBean(JavaBeanBinderTests.ExampleValueBean valueBean) {
            this.valueBean = valueBean;
        }
    }

    public static class ExampleNestedBeanWithoutSetter {
        private JavaBeanBinderTests.ExampleValueBean valueBean = new JavaBeanBinderTests.ExampleValueBean();

        public JavaBeanBinderTests.ExampleValueBean getValueBean() {
            return this.valueBean;
        }
    }

    public static class ExampleNestedBeanWithoutSetterOrType {
        private JavaBeanBinderTests.ExampleValueBean valueBean = new JavaBeanBinderTests.ExampleValueBean();

        public Object getValueBean() {
            return this.valueBean;
        }
    }

    public static class ExampleImmutableNestedBeanWithoutSetter {
        private JavaBeanBinderTests.ExampleImmutableNestedBeanWithoutSetter.NestedImmutable nested = new JavaBeanBinderTests.ExampleImmutableNestedBeanWithoutSetter.NestedImmutable();

        public JavaBeanBinderTests.ExampleImmutableNestedBeanWithoutSetter.NestedImmutable getNested() {
            return this.nested;
        }

        public static class NestedImmutable {
            public String getFoo() {
                return "foo";
            }
        }
    }

    public static class ExampleWithNonDefaultConstructor {
        private String value;

        public ExampleWithNonDefaultConstructor(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public abstract static class ExampleSuperClassBean {
        private int intValue;

        public int getIntValue() {
            return this.intValue;
        }

        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }
    }

    public static class ExampleSubclassBean extends JavaBeanBinderTests.ExampleSuperClassBean {
        private long longValue;

        public long getLongValue() {
            return this.longValue;
        }

        public void setLongValue(long longValue) {
            this.longValue = longValue;
        }
    }

    public static class ExampleMismatchBean {
        private int value;

        public String getValue() {
            return String.valueOf(this.value);
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class ExampleWithThrowingGetters {
        private int value;

        public int getValue() {
            return this.value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public List<String> getNames() {
            throw new RuntimeException();
        }

        public JavaBeanBinderTests.ExampleValueBean getNested() {
            throw new RuntimeException();
        }
    }

    public static class ExampleWithSelfReference {
        private int value;

        private JavaBeanBinderTests.ExampleWithSelfReference self;

        public int getValue() {
            return this.value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public JavaBeanBinderTests.ExampleWithSelfReference getSelf() {
            return this.self;
        }

        public void setSelf(JavaBeanBinderTests.ExampleWithSelfReference self) {
            this.self = self;
        }
    }

    public static class ExampleWithInvalidAccessors {
        private String name;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String get() {
            throw new IllegalArgumentException("should not be invoked");
        }

        public boolean is() {
            throw new IllegalArgumentException("should not be invoked");
        }
    }

    public static class ExampleWithStaticAccessors {
        private static String name;

        private int counter;

        public static String getName() {
            return JavaBeanBinderTests.ExampleWithStaticAccessors.name;
        }

        public static void setName(String name) {
            JavaBeanBinderTests.ExampleWithStaticAccessors.name = name;
        }

        public int getCounter() {
            return this.counter;
        }

        public void setCounter(int counter) {
            this.counter = counter;
        }
    }

    public enum ExampleEnum {

        FOO_BAR,
        BAR_BAZ;}

    public static class ConverterAnnotatedExampleBean {
        @DateTimeFormat(iso = ISO.DATE)
        private LocalDate date;

        public LocalDate getDate() {
            return this.date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }
    }

    public static class ExampleWithPropertyEditorType {
        private Class<? extends Throwable> value;

        public Class<? extends Throwable> getValue() {
            return this.value;
        }

        public void setValue(Class<? extends Throwable> value) {
            this.value = value;
        }
    }
}

