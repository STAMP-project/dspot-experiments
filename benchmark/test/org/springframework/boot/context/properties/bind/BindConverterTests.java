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


import java.beans.PropertyEditorSupport;
import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;


/**
 * Tests for {@link BindConverter}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class BindConverterTests {
    @Mock
    private Consumer<PropertyEditorRegistry> propertyEditorInitializer;

    @Test
    public void createWhenConversionServiceIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> BindConverter.get(null, null)).withMessageContaining("ConversionService must not be null");
    }

    @Test
    public void createWhenPropertyEditorInitializerIsNullShouldCreate() {
        BindConverter.get(ApplicationConversionService.getSharedInstance(), null);
    }

    @Test
    public void createWhenPropertyEditorInitializerIsNotNullShouldUseToInitialize() {
        BindConverter.get(ApplicationConversionService.getSharedInstance(), this.propertyEditorInitializer);
        Mockito.verify(this.propertyEditorInitializer).accept(ArgumentMatchers.any(PropertyEditorRegistry.class));
    }

    @Test
    public void canConvertWhenHasDefaultEditorShouldReturnTrue() {
        BindConverter bindConverter = getPropertyEditorOnlyBindConverter(null);
        assertThat(bindConverter.canConvert("java.lang.RuntimeException", ResolvableType.forClass(Class.class))).isTrue();
    }

    @Test
    public void canConvertWhenHasCustomEditorShouldReturnTrue() {
        BindConverter bindConverter = getPropertyEditorOnlyBindConverter(this::registerSampleTypeEditor);
        assertThat(bindConverter.canConvert("test", ResolvableType.forClass(BindConverterTests.SampleType.class))).isTrue();
    }

    @Test
    public void canConvertWhenHasEditorByConventionShouldReturnTrue() {
        BindConverter bindConverter = getPropertyEditorOnlyBindConverter(null);
        assertThat(bindConverter.canConvert("test", ResolvableType.forClass(BindConverterTests.ConventionType.class))).isTrue();
    }

    @Test
    public void canConvertWhenHasEditorForCollectionElementShouldReturnTrue() {
        BindConverter bindConverter = getPropertyEditorOnlyBindConverter(this::registerSampleTypeEditor);
        assertThat(bindConverter.canConvert("test", ResolvableType.forClassWithGenerics(List.class, BindConverterTests.SampleType.class))).isTrue();
    }

    @Test
    public void canConvertWhenHasEditorForArrayElementShouldReturnTrue() {
        BindConverter bindConverter = getPropertyEditorOnlyBindConverter(this::registerSampleTypeEditor);
        assertThat(bindConverter.canConvert("test", ResolvableType.forClass(BindConverterTests.SampleType[].class))).isTrue();
    }

    @Test
    public void canConvertWhenConversionServiceCanConvertShouldReturnTrue() {
        BindConverter bindConverter = getBindConverter(new BindConverterTests.SampleTypeConverter());
        assertThat(bindConverter.canConvert("test", ResolvableType.forClass(BindConverterTests.SampleType.class))).isTrue();
    }

    @Test
    public void canConvertWhenNotPropertyEditorAndConversionServiceCannotConvertShouldReturnFalse() {
        BindConverter bindConverter = BindConverter.get(ApplicationConversionService.getSharedInstance(), null);
        assertThat(bindConverter.canConvert("test", ResolvableType.forClass(BindConverterTests.SampleType.class))).isFalse();
    }

    @Test
    public void convertWhenHasDefaultEditorShouldConvert() {
        BindConverter bindConverter = getPropertyEditorOnlyBindConverter(null);
        Class<?> converted = bindConverter.convert("java.lang.RuntimeException", ResolvableType.forClass(Class.class));
        assertThat(converted).isEqualTo(RuntimeException.class);
    }

    @Test
    public void convertWhenHasCustomEditorShouldConvert() {
        BindConverter bindConverter = getPropertyEditorOnlyBindConverter(this::registerSampleTypeEditor);
        BindConverterTests.SampleType converted = bindConverter.convert("test", ResolvableType.forClass(BindConverterTests.SampleType.class));
        assertThat(converted.getText()).isEqualTo("test");
    }

    @Test
    public void convertWhenHasEditorByConventionShouldConvert() {
        BindConverter bindConverter = getPropertyEditorOnlyBindConverter(null);
        BindConverterTests.ConventionType converted = bindConverter.convert("test", ResolvableType.forClass(BindConverterTests.ConventionType.class));
        assertThat(converted.getText()).isEqualTo("test");
    }

    @Test
    public void convertWhenHasEditorForCollectionElementShouldConvert() {
        BindConverter bindConverter = getPropertyEditorOnlyBindConverter(this::registerSampleTypeEditor);
        List<BindConverterTests.SampleType> converted = bindConverter.convert("test", ResolvableType.forClassWithGenerics(List.class, BindConverterTests.SampleType.class));
        assertThat(converted).hasSize(1);
        assertThat(converted.get(0).getText()).isEqualTo("test");
    }

    @Test
    public void convertWhenHasEditorForArrayElementShouldConvert() {
        BindConverter bindConverter = getPropertyEditorOnlyBindConverter(this::registerSampleTypeEditor);
        BindConverterTests.SampleType[] converted = bindConverter.convert("test", ResolvableType.forClass(BindConverterTests.SampleType[].class));
        assertThat(converted).isNotEmpty();
        assertThat(converted[0].getText()).isEqualTo("test");
    }

    @Test
    public void convertWhenConversionServiceCanConvertShouldConvert() {
        BindConverter bindConverter = getBindConverter(new BindConverterTests.SampleTypeConverter());
        BindConverterTests.SampleType converted = bindConverter.convert("test", ResolvableType.forClass(BindConverterTests.SampleType.class));
        assertThat(converted.getText()).isEqualTo("test");
    }

    @Test
    public void convertWhenNotPropertyEditorAndConversionServiceCannotConvertShouldThrowException() {
        BindConverter bindConverter = BindConverter.get(ApplicationConversionService.getSharedInstance(), null);
        assertThatExceptionOfType(ConverterNotFoundException.class).isThrownBy(() -> bindConverter.convert("test", ResolvableType.forClass(.class)));
    }

    @Test
    public void convertWhenConvertingToFileShouldExcludeFileEditor() {
        // For back compatibility we want true file conversion and not an accidental
        // classpath resource reference. See gh-12163
        BindConverter bindConverter = BindConverter.get(new GenericConversionService(), null);
        File result = bindConverter.convert(".", ResolvableType.forClass(File.class));
        assertThat(result.getPath()).isEqualTo(".");
    }

    @Test
    public void fallsBackToApplicationConversionService() {
        BindConverter bindConverter = BindConverter.get(new GenericConversionService(), null);
        Duration result = bindConverter.convert("10s", ResolvableType.forClass(Duration.class));
        assertThat(result.getSeconds()).isEqualTo(10);
    }

    static class SampleType {
        private String text;

        public String getText() {
            return this.text;
        }
    }

    static class SampleTypePropertyEditor extends PropertyEditorSupport {
        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            BindConverterTests.SampleType value = new BindConverterTests.SampleType();
            value.text = text;
            setValue(value);
        }
    }

    static class SampleTypeConverter implements Converter<String, BindConverterTests.SampleType> {
        @Override
        public BindConverterTests.SampleType convert(String source) {
            BindConverterTests.SampleType result = new BindConverterTests.SampleType();
            result.text = source;
            return result;
        }
    }

    static class ConventionType {
        private String text;

        public String getText() {
            return this.text;
        }
    }

    static class ConventionTypeEditor extends PropertyEditorSupport {
        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            BindConverterTests.ConventionType value = new BindConverterTests.ConventionType();
            value.text = text;
            setValue(value);
        }
    }

    /**
     * {@link ConversionService} that always throws an {@link AssertionError}.
     */
    private static class ThrowingConversionService implements ConversionService {
        @Override
        public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
            throw new AssertionError("Should not call conversion service");
        }

        @Override
        public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
            throw new AssertionError("Should not call conversion service");
        }

        @Override
        public <T> T convert(Object source, Class<T> targetType) {
            throw new AssertionError("Should not call conversion service");
        }

        @Override
        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            throw new AssertionError("Should not call conversion service");
        }
    }
}

