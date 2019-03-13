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


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.springframework.boot.context.properties.ConfigurationPropertyDefaultValue;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MockConfigurationPropertySource;


/**
 * Tests for {@link ConstructorParametersBinder}.
 *
 * @author Madhura Bhave
 */
public class ConstructorParametersBinderTests {
    private List<ConfigurationPropertySource> sources = new ArrayList<>();

    private Binder binder;

    @Test
    public void bindToClassShouldCreateBoundBean() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.int-value", "12");
        source.put("foo.long-value", "34");
        source.put("foo.boolean-value", "true");
        source.put("foo.string-value", "foo");
        source.put("foo.enum-value", "foo-bar");
        this.sources.add(source);
        ConstructorParametersBinderTests.ExampleValueBean bean = this.binder.bind("foo", Bindable.of(ConstructorParametersBinderTests.ExampleValueBean.class)).get();
        assertThat(bean.getIntValue()).isEqualTo(12);
        assertThat(bean.getLongValue()).isEqualTo(34);
        assertThat(bean.isBooleanValue()).isTrue();
        assertThat(bean.getStringValue()).isEqualTo("foo");
        assertThat(bean.getEnumValue()).isEqualTo(ConstructorParametersBinderTests.ExampleEnum.FOO_BAR);
    }

    @Test
    public void bindToClassWhenHasNoPrefixShouldCreateBoundBean() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("int-value", "12");
        source.put("long-value", "34");
        source.put("boolean-value", "true");
        source.put("string-value", "foo");
        source.put("enum-value", "foo-bar");
        this.sources.add(source);
        ConstructorParametersBinderTests.ExampleValueBean bean = this.binder.bind(ConfigurationPropertyName.of(""), Bindable.of(ConstructorParametersBinderTests.ExampleValueBean.class)).get();
        assertThat(bean.getIntValue()).isEqualTo(12);
        assertThat(bean.getLongValue()).isEqualTo(34);
        assertThat(bean.isBooleanValue()).isTrue();
        assertThat(bean.getStringValue()).isEqualTo("foo");
        assertThat(bean.getEnumValue()).isEqualTo(ConstructorParametersBinderTests.ExampleEnum.FOO_BAR);
    }

    @Test
    public void bindToAbstractClassWithShouldNotBind() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.name", "test");
        this.sources.add(source);
        boolean bound = this.binder.bind("foo", Bindable.of(ConstructorParametersBinderTests.ExampleAbstractBean.class)).isBound();
        assertThat(bound).isFalse();
    }

    @Test
    public void bindToClassWithMultipleConstructorsShouldNotBind() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.int-value", "12");
        this.sources.add(source);
        boolean bound = this.binder.bind("foo", Bindable.of(ConstructorParametersBinderTests.MultipleConstructorsBean.class)).isBound();
        assertThat(bound).isFalse();
    }

    @Test
    public void bindToClassWithOnlyDefaultConstructorShouldNotBind() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.int-value", "12");
        this.sources.add(source);
        boolean bound = this.binder.bind("foo", Bindable.of(ConstructorParametersBinderTests.DefaultConstructorBean.class)).isBound();
        assertThat(bound).isFalse();
    }

    @Test
    public void bindToClassShouldBindNested() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.value-bean.int-value", "123");
        source.put("foo.value-bean.long-value", "34");
        source.put("foo.value-bean.string-value", "foo");
        this.sources.add(source);
        ConstructorParametersBinderTests.ExampleNestedBean bean = this.binder.bind("foo", Bindable.of(ConstructorParametersBinderTests.ExampleNestedBean.class)).get();
        assertThat(bean.getValueBean().getIntValue()).isEqualTo(123);
        assertThat(bean.getValueBean().getLongValue()).isEqualTo(34);
        assertThat(bean.getValueBean().isBooleanValue()).isFalse();
        assertThat(bean.getValueBean().getStringValue()).isEqualTo("foo");
        assertThat(bean.getValueBean().getEnumValue()).isNull();
    }

    @Test
    public void bindToClassWithNoValueForPrimitiveShouldUseDefault() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.string-value", "foo");
        this.sources.add(source);
        ConstructorParametersBinderTests.ExampleValueBean bean = this.binder.bind("foo", Bindable.of(ConstructorParametersBinderTests.ExampleValueBean.class)).get();
        assertThat(bean.getIntValue()).isEqualTo(0);
        assertThat(bean.getLongValue()).isEqualTo(0);
        assertThat(bean.isBooleanValue()).isEqualTo(false);
        assertThat(bean.getStringValue()).isEqualTo("foo");
    }

    @Test
    public void bindToClassWithNoValueAndDefaultValueShouldUseDefault() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.string-value", "foo");
        this.sources.add(source);
        ConstructorParametersBinderTests.ExampleDefaultValueBean bean = this.binder.bind("foo", Bindable.of(ConstructorParametersBinderTests.ExampleDefaultValueBean.class)).get();
        assertThat(bean.getIntValue()).isEqualTo(5);
        assertThat(bean.getStringsList()).containsOnly("a", "b", "c");
        assertThat(bean.getCustomList()).containsOnly("x,y,z");
    }

    public static class ExampleValueBean {
        private final int intValue;

        private final long longValue;

        private final boolean booleanValue;

        private final String stringValue;

        private final ConstructorParametersBinderTests.ExampleEnum enumValue;

        public ExampleValueBean(int intValue, long longValue, boolean booleanValue, String stringValue, ConstructorParametersBinderTests.ExampleEnum enumValue) {
            this.intValue = intValue;
            this.longValue = longValue;
            this.booleanValue = booleanValue;
            this.stringValue = stringValue;
            this.enumValue = enumValue;
        }

        public int getIntValue() {
            return this.intValue;
        }

        public long getLongValue() {
            return this.longValue;
        }

        public boolean isBooleanValue() {
            return this.booleanValue;
        }

        public String getStringValue() {
            return this.stringValue;
        }

        public ConstructorParametersBinderTests.ExampleEnum getEnumValue() {
            return this.enumValue;
        }
    }

    public enum ExampleEnum {

        FOO_BAR,
        BAR_BAZ;}

    @SuppressWarnings("unused")
    public static class MultipleConstructorsBean {
        public MultipleConstructorsBean(int intValue) {
            this(intValue, 23L, "hello");
        }

        public MultipleConstructorsBean(int intValue, long longValue, String stringValue) {
        }
    }

    public abstract static class ExampleAbstractBean {
        private final String name;

        public ExampleAbstractBean(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    public static class DefaultConstructorBean {
        public DefaultConstructorBean() {
        }
    }

    public static class ExampleNestedBean {
        private final ConstructorParametersBinderTests.ExampleValueBean valueBean;

        public ExampleNestedBean(ConstructorParametersBinderTests.ExampleValueBean valueBean) {
            this.valueBean = valueBean;
        }

        public ConstructorParametersBinderTests.ExampleValueBean getValueBean() {
            return this.valueBean;
        }
    }

    public static class ExampleDefaultValueBean {
        private final int intValue;

        private final List<String> stringsList;

        private final List<String> customList;

        public ExampleDefaultValueBean(@ConfigurationPropertyDefaultValue("5")
        int intValue, @ConfigurationPropertyDefaultValue({ "a", "b", "c" })
        List<String> stringsList, @ConfigurationPropertyDefaultValue("x,y,z")
        List<String> customList) {
            this.intValue = intValue;
            this.stringsList = stringsList;
            this.customList = customList;
        }

        public int getIntValue() {
            return this.intValue;
        }

        public List<String> getStringsList() {
            return this.stringsList;
        }

        public List<String> getCustomList() {
            return this.customList;
        }
    }
}

