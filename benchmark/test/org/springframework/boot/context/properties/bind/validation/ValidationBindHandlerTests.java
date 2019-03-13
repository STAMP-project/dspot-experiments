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
package org.springframework.boot.context.properties.bind.validation;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.junit.Test;
import org.springframework.boot.context.properties.bind.AbstractBindHandler;
import org.springframework.boot.context.properties.bind.BindContext;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MockConfigurationPropertySource;
import org.springframework.boot.origin.Origin;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;


/**
 * Tests for {@link ValidationBindHandler}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public class ValidationBindHandlerTests {
    private List<ConfigurationPropertySource> sources = new ArrayList<>();

    private ValidationBindHandler handler;

    private Binder binder;

    private LocalValidatorFactoryBean validator;

    @Test
    public void bindShouldBindWithoutHandler() {
        this.sources.add(new MockConfigurationPropertySource("foo.age", 4));
        ValidationBindHandlerTests.ExampleValidatedBean bean = this.binder.bind("foo", Bindable.of(ValidationBindHandlerTests.ExampleValidatedBean.class)).get();
        assertThat(bean.getAge()).isEqualTo(4);
    }

    @Test
    public void bindShouldFailWithHandler() {
        this.sources.add(new MockConfigurationPropertySource("foo.age", 4));
        assertThatExceptionOfType(BindException.class).isThrownBy(() -> this.binder.bind("foo", Bindable.of(.class), this.handler)).withCauseInstanceOf(BindValidationException.class);
    }

    @Test
    public void bindShouldValidateNestedProperties() {
        this.sources.add(new MockConfigurationPropertySource("foo.nested.age", 4));
        assertThatExceptionOfType(BindException.class).isThrownBy(() -> this.binder.bind("foo", Bindable.of(.class), this.handler)).withCauseInstanceOf(BindValidationException.class);
    }

    @Test
    public void bindShouldFailWithAccessToOrigin() {
        this.sources.add(new MockConfigurationPropertySource("foo.age", 4, "file"));
        BindValidationException cause = bindAndExpectValidationError(() -> this.binder.bind(ConfigurationPropertyName.of("foo"), Bindable.of(ValidationBindHandlerTests.ExampleValidatedBean.class), this.handler));
        ObjectError objectError = cause.getValidationErrors().getAllErrors().get(0);
        assertThat(Origin.from(objectError).toString()).isEqualTo("file");
    }

    @Test
    public void bindShouldFailWithAccessToBoundProperties() {
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.nested.name", "baz");
        source.put("foo.nested.age", "4");
        source.put("faf.bar", "baz");
        this.sources.add(source);
        BindValidationException cause = bindAndExpectValidationError(() -> this.binder.bind(ConfigurationPropertyName.of("foo"), Bindable.of(ValidationBindHandlerTests.ExampleValidatedWithNestedBean.class), this.handler));
        Set<ConfigurationProperty> boundProperties = cause.getValidationErrors().getBoundProperties();
        assertThat(boundProperties).extracting(( p) -> p.getName().toString()).contains("foo.nested.age", "foo.nested.name");
    }

    @Test
    public void bindShouldFailWithAccessToName() {
        this.sources.add(new MockConfigurationPropertySource("foo.nested.age", "4"));
        BindValidationException cause = bindAndExpectValidationError(() -> this.binder.bind(ConfigurationPropertyName.of("foo"), Bindable.of(ValidationBindHandlerTests.ExampleValidatedWithNestedBean.class), this.handler));
        assertThat(cause.getValidationErrors().getName().toString()).isEqualTo("foo");
        assertThat(cause.getMessage()).contains("nested.age");
    }

    @Test
    public void bindShouldFailIfExistingValueIsInvalid() {
        ValidationBindHandlerTests.ExampleValidatedBean existingValue = new ValidationBindHandlerTests.ExampleValidatedBean();
        BindValidationException cause = bindAndExpectValidationError(() -> this.binder.bind(ConfigurationPropertyName.of("foo"), Bindable.of(ValidationBindHandlerTests.ExampleValidatedBean.class).withExistingValue(existingValue), this.handler));
        FieldError fieldError = ((FieldError) (cause.getValidationErrors().getAllErrors().get(0)));
        assertThat(fieldError.getField()).isEqualTo("age");
    }

    @Test
    public void bindShouldValidateWithoutAnnotation() {
        ValidationBindHandlerTests.ExampleNonValidatedBean existingValue = new ValidationBindHandlerTests.ExampleNonValidatedBean();
        bindAndExpectValidationError(() -> this.binder.bind(ConfigurationPropertyName.of("foo"), Bindable.of(ValidationBindHandlerTests.ExampleNonValidatedBean.class).withExistingValue(existingValue), this.handler));
    }

    @Test
    public void bindShouldNotValidateDepthGreaterThanZero() {
        // gh-12227
        MockConfigurationPropertySource source = new MockConfigurationPropertySource();
        source.put("foo.bar", "baz");
        this.sources.add(source);
        ValidationBindHandlerTests.ExampleValidatedBeanWithGetterException existingValue = new ValidationBindHandlerTests.ExampleValidatedBeanWithGetterException();
        this.binder.bind(ConfigurationPropertyName.of("foo"), Bindable.of(ValidationBindHandlerTests.ExampleValidatedBeanWithGetterException.class).withExistingValue(existingValue), this.handler);
    }

    @Test
    public void bindShouldNotValidateIfOtherHandlersInChainThrowError() {
        this.sources.add(new MockConfigurationPropertySource("foo", "hello"));
        ValidationBindHandlerTests.ExampleValidatedBean bean = new ValidationBindHandlerTests.ExampleValidatedBean();
        assertThatExceptionOfType(BindException.class).isThrownBy(() -> this.binder.bind("foo", Bindable.of(.class).withExistingValue(bean), this.handler)).withCauseInstanceOf(ConverterNotFoundException.class);
    }

    @Test
    public void bindShouldValidateIfOtherHandlersInChainIgnoreError() {
        ValidationBindHandlerTests.TestHandler testHandler = new ValidationBindHandlerTests.TestHandler();
        this.handler = new ValidationBindHandler(testHandler, this.validator);
        this.sources.add(new MockConfigurationPropertySource("foo", "hello"));
        ValidationBindHandlerTests.ExampleValidatedBean bean = new ValidationBindHandlerTests.ExampleValidatedBean();
        assertThatExceptionOfType(BindException.class).isThrownBy(() -> this.binder.bind("foo", Bindable.of(.class).withExistingValue(bean), this.handler)).withCauseInstanceOf(BindValidationException.class);
    }

    public static class ExampleNonValidatedBean {
        @Min(5)
        private int age;

        public int getAge() {
            return this.age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    @Validated
    public static class ExampleValidatedBean {
        @Min(5)
        private int age;

        public int getAge() {
            return this.age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    @Validated
    public static class ExampleValidatedWithNestedBean {
        @Valid
        private ValidationBindHandlerTests.ExampleNested nested = new ValidationBindHandlerTests.ExampleNested();

        public ValidationBindHandlerTests.ExampleNested getNested() {
            return this.nested;
        }

        public void setNested(ValidationBindHandlerTests.ExampleNested nested) {
            this.nested = nested;
        }
    }

    public static class ExampleNested {
        private String name;

        @Min(5)
        private int age;

        @NotNull
        private String address;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return this.age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getAddress() {
            return this.address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    @Validated
    public static class ExampleValidatedBeanWithGetterException {
        public int getAge() {
            throw new RuntimeException();
        }
    }

    static class TestHandler extends AbstractBindHandler {
        @Override
        public Object onFailure(ConfigurationPropertyName name, Bindable<?> target, BindContext context, Exception error) throws Exception {
            return null;
        }
    }
}

