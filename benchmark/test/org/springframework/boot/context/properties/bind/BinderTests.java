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
package org.springframework.boot.context.properties.bind;


import DateTimeFormat.ISO.DATE_TIME;
import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Validation;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.context.properties.source.MockConfigurationPropertySource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;


/**
 * Tests for {@link Binder}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public class BinderTests {
    private List<ConfigurationPropertySource> sources = new ArrayList<>();

    private Binder binder;

    @Test
    public void createWhenSourcesIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Binder(((Iterable<ConfigurationPropertySource>) (null)))).withMessageContaining("Sources must not be null");
    }

    @Test
    public void bindWhenNameIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.binder.bind(((ConfigurationPropertyName) (null)), Bindable.of(.class), BindHandler.DEFAULT)).withMessageContaining("Name must not be null");
    }

    @Test
    public void bindWhenTargetIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.binder.bind(ConfigurationPropertyName.of("foo"), null, BindHandler.DEFAULT)).withMessageContaining("Target must not be null");
    }

    @Test
    public void bindToValueWhenPropertyIsMissingShouldReturnUnbound() {
        this.sources.add(new MockConfigurationPropertySource());
        BindResult<String> result = this.binder.bind("foo", Bindable.of(String.class));
        assertThat(result.isBound()).isFalse();
    }

    @Test
    public void bindToValueShouldReturnPropertyValue() {
        this.sources.add(new MockConfigurationPropertySource("foo", 123));
        BindResult<Integer> result = this.binder.bind("foo", Bindable.of(Integer.class));
        assertThat(result.get()).isEqualTo(123);
    }

    @Test
    public void bindToValueShouldReturnPropertyValueFromSecondSource() {
        this.sources.add(new MockConfigurationPropertySource("foo", 123));
        this.sources.add(new MockConfigurationPropertySource("bar", 234));
        BindResult<Integer> result = this.binder.bind("bar", Bindable.of(Integer.class));
        assertThat(result.get()).isEqualTo(234);
    }

    @Test
    public void bindToValueShouldReturnConvertedPropertyValue() {
        this.sources.add(new MockConfigurationPropertySource("foo", "123"));
        BindResult<Integer> result = this.binder.bind("foo", Bindable.of(Integer.class));
        assertThat(result.get()).isEqualTo(123);
    }

    @Test
    public void bindToValueWhenMultipleCandidatesShouldReturnFirst() {
        this.sources.add(new MockConfigurationPropertySource("foo", 123));
        this.sources.add(new MockConfigurationPropertySource("foo", 234));
        BindResult<Integer> result = this.binder.bind("foo", Bindable.of(Integer.class));
        assertThat(result.get()).isEqualTo(123);
    }

    @Test
    public void bindToValueWithPlaceholdersShouldResolve() {
        StandardEnvironment environment = new StandardEnvironment();
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(environment, "bar=23");
        this.sources.add(new MockConfigurationPropertySource("foo", "1${bar}"));
        this.binder = new Binder(this.sources, new PropertySourcesPlaceholdersResolver(environment));
        BindResult<Integer> result = this.binder.bind("foo", Bindable.of(Integer.class));
        assertThat(result.get()).isEqualTo(123);
    }

    @Test
    public void bindToValueWithMissingPlaceholderShouldResolveToValueWithPlaceholder() {
        StandardEnvironment environment = new StandardEnvironment();
        this.sources.add(new MockConfigurationPropertySource("foo", "${bar}"));
        this.binder = new Binder(this.sources, new PropertySourcesPlaceholdersResolver(environment));
        BindResult<String> result = this.binder.bind("foo", Bindable.of(String.class));
        assertThat(result.get()).isEqualTo("${bar}");
    }

    @Test
    public void bindToValueWithCustomPropertyEditorShouldReturnConvertedValue() {
        this.binder = new Binder(this.sources, null, null, ( registry) -> registry.registerCustomEditor(.class, new org.springframework.boot.context.properties.bind.JavaBeanPropertyEditor()));
        this.sources.add(new MockConfigurationPropertySource("foo", "123"));
        BindResult<BinderTests.JavaBean> result = this.binder.bind("foo", Bindable.of(BinderTests.JavaBean.class));
        assertThat(result.get().getValue()).isEqualTo("123");
    }

    @Test
    public void bindToValueShouldTriggerOnSuccess() {
        this.sources.add(new MockConfigurationPropertySource("foo", "1", "line1"));
        BindHandler handler = Mockito.mock(BindHandler.class, Answers.CALLS_REAL_METHODS);
        Bindable<Integer> target = Bindable.of(Integer.class);
        this.binder.bind("foo", target, handler);
        InOrder ordered = Mockito.inOrder(handler);
        ordered.verify(handler).onSuccess(ArgumentMatchers.eq(ConfigurationPropertyName.of("foo")), ArgumentMatchers.eq(target), ArgumentMatchers.any(), ArgumentMatchers.eq(1));
    }

    @Test
    public void bindToJavaBeanShouldReturnPopulatedBean() {
        this.sources.add(new MockConfigurationPropertySource("foo.value", "bar"));
        BinderTests.JavaBean result = this.binder.bind("foo", Bindable.of(BinderTests.JavaBean.class)).get();
        assertThat(result.getValue()).isEqualTo("bar");
    }

    @Test
    public void bindToJavaBeanWhenNonIterableShouldReturnPopulatedBean() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource("foo.value", "bar");
        this.sources.add(source.nonIterable());
        BinderTests.JavaBean result = this.binder.bind("foo", Bindable.of(BinderTests.JavaBean.class)).get();
        assertThat(result.getValue()).isEqualTo("bar");
    }

    @Test
    public void bindToJavaBeanWhenHasPropertyWithSameNameShouldStillBind() {
        // gh-10945
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo", "boom");
        source.put("foo.value", "bar");
        this.sources.add(source);
        BinderTests.JavaBean result = this.binder.bind("foo", Bindable.of(BinderTests.JavaBean.class)).get();
        assertThat(result.getValue()).isEqualTo("bar");
    }

    @Test
    public void bindToJavaBeanShouldTriggerOnSuccess() {
        this.sources.add(new MockConfigurationPropertySource("foo.value", "bar", "line1"));
        BindHandler handler = Mockito.mock(BindHandler.class, Answers.CALLS_REAL_METHODS);
        Bindable<BinderTests.JavaBean> target = Bindable.of(BinderTests.JavaBean.class);
        this.binder.bind("foo", target, handler);
        InOrder inOrder = Mockito.inOrder(handler);
        inOrder.verify(handler).onSuccess(ArgumentMatchers.eq(ConfigurationPropertyName.of("foo.value")), ArgumentMatchers.eq(Bindable.of(String.class)), ArgumentMatchers.any(), ArgumentMatchers.eq("bar"));
        inOrder.verify(handler).onSuccess(ArgumentMatchers.eq(ConfigurationPropertyName.of("foo")), ArgumentMatchers.eq(target), ArgumentMatchers.any(), ArgumentMatchers.isA(BinderTests.JavaBean.class));
    }

    @Test
    public void bindWhenHasMalformedDateShouldThrowException() {
        this.sources.add(new MockConfigurationPropertySource("foo", "2014-04-01T01:30:00.000-05:00"));
        assertThatExceptionOfType(BindException.class).isThrownBy(() -> this.binder.bind("foo", Bindable.of(.class))).withCauseInstanceOf(ConversionFailedException.class);
    }

    @Test
    public void bindWhenHasAnnotationsShouldChangeConvertedValue() {
        this.sources.add(new MockConfigurationPropertySource("foo", "2014-04-01T01:30:00.000-05:00"));
        DateTimeFormat annotation = AnnotationUtils.synthesizeAnnotation(Collections.singletonMap("iso", DATE_TIME), DateTimeFormat.class, null);
        LocalDate result = this.binder.bind("foo", Bindable.of(LocalDate.class).withAnnotations(annotation)).get();
        assertThat(result.toString()).isEqualTo("2014-04-01");
    }

    @Test
    public void bindExceptionWhenBeanBindingFailsShouldHaveNullConfigurationProperty() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.value", "hello");
        source.put("foo.items", "bar,baz");
        this.sources.add(source);
        Bindable<BinderTests.JavaBean> target = Bindable.of(BinderTests.JavaBean.class);
        assertThatExceptionOfType(BindException.class).isThrownBy(() -> this.binder.bind("foo", target)).satisfies(this::noItemsSetterRequirements);
    }

    @Test
    public void bindToValidatedBeanWithResourceAndNonEnumerablePropertySource() {
        ConfigurationPropertySources.from(new org.springframework.core.env.PropertySource<String>("test") {
            @Override
            public Object getProperty(String name) {
                return null;
            }
        }).forEach(this.sources::add);
        Validator validator = new org.springframework.validation.beanvalidation.SpringValidatorAdapter(Validation.byDefaultProvider().configure().buildValidatorFactory().getValidator());
        this.binder.bind("foo", Bindable.of(BinderTests.ResourceBean.class), new org.springframework.boot.context.properties.bind.validation.ValidationBindHandler(validator));
    }

    @Test
    public void bindToBeanWithCycle() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        this.sources.add(source.nonIterable());
        Bindable<BinderTests.CycleBean1> target = Bindable.of(BinderTests.CycleBean1.class);
        this.binder.bind("foo", target);
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void bindToBeanWithUnresolvableGenerics() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.bar", "hello");
        this.sources.add(source);
        Bindable<BinderTests.GenericBean> target = Bindable.of(BinderTests.GenericBean.class);
        this.binder.bind("foo", target);
    }

    @Test
    public void bindWithEmptyPrefixShouldIgnorePropertiesWithEmptyName() {
        Map<String, Object> source = new HashMap<>();
        source.put("value", "hello");
        source.put("", "bar");
        Iterable<ConfigurationPropertySource> propertySources = ConfigurationPropertySources.from(new MapPropertySource("test", source));
        propertySources.forEach(this.sources::add);
        Bindable<BinderTests.JavaBean> target = Bindable.of(BinderTests.JavaBean.class);
        BinderTests.JavaBean result = this.binder.bind("", target).get();
        assertThat(result.getValue()).isEqualTo("hello");
    }

    public static class JavaBean {
        private String value;

        private List<String> items = Collections.emptyList();

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public List<String> getItems() {
            return this.items;
        }
    }

    public enum ExampleEnum {

        FOO_BAR,
        BAR_BAZ,
        BAZ_BOO;}

    @Validated
    public static class ResourceBean {
        private Resource resource;

        public Resource getResource() {
            return this.resource;
        }

        public void setResource(Resource resource) {
            this.resource = resource;
        }
    }

    public static class CycleBean1 {
        private BinderTests.CycleBean2 two;

        public BinderTests.CycleBean2 getTwo() {
            return this.two;
        }

        public void setTwo(BinderTests.CycleBean2 two) {
            this.two = two;
        }
    }

    public static class CycleBean2 {
        private BinderTests.CycleBean1 one;

        public BinderTests.CycleBean1 getOne() {
            return this.one;
        }

        public void setOne(BinderTests.CycleBean1 one) {
            this.one = one;
        }
    }

    public static class GenericBean<T> {
        private T bar;

        public T getBar() {
            return this.bar;
        }

        public void setBar(T bar) {
            this.bar = bar;
        }
    }

    public static class JavaBeanPropertyEditor extends PropertyEditorSupport {
        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            BinderTests.JavaBean value = new BinderTests.JavaBean();
            value.setValue(text);
            setValue(value);
        }
    }
}

