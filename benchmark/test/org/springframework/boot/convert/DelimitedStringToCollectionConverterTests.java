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
package org.springframework.boot.convert;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ReflectionUtils;

import static Delimiter.NONE;


/**
 * Tests for {@link DelimitedStringToCollectionConverter}.
 *
 * @author Phillip Webb
 */
@RunWith(Parameterized.class)
public class DelimitedStringToCollectionConverterTests {
    private final ConversionService conversionService;

    public DelimitedStringToCollectionConverterTests(String name, ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Test
    public void canConvertFromStringToCollectionShouldReturnTrue() {
        assertThat(this.conversionService.canConvert(String.class, Collection.class)).isTrue();
    }

    @Test
    public void matchesWhenTargetIsNotAnnotatedShouldReturnTrue() {
        TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);
        TypeDescriptor targetType = TypeDescriptor.nested(ReflectionUtils.findField(DelimitedStringToCollectionConverterTests.Values.class, "noAnnotation"), 0);
        assertThat(new DelimitedStringToCollectionConverter(this.conversionService).matches(sourceType, targetType)).isTrue();
    }

    @Test
    public void matchesWhenHasAnnotationAndNoElementTypeShouldReturnTrue() {
        TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);
        TypeDescriptor targetType = TypeDescriptor.nested(ReflectionUtils.findField(DelimitedStringToCollectionConverterTests.Values.class, "noElementType"), 0);
        assertThat(new DelimitedStringToCollectionConverter(this.conversionService).matches(sourceType, targetType)).isTrue();
    }

    @Test
    public void matchesWhenHasAnnotationAndConvertibleElementTypeShouldReturnTrue() {
        if ((this.conversionService) instanceof ApplicationConversionService) {
            TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);
            TypeDescriptor targetType = TypeDescriptor.nested(ReflectionUtils.findField(DelimitedStringToCollectionConverterTests.Values.class, "convertibleElementType"), 0);
            assertThat(new DelimitedStringToCollectionConverter(this.conversionService).matches(sourceType, targetType)).isTrue();
        }
    }

    @Test
    public void matchesWhenHasAnnotationAndNonConvertibleElementTypeShouldReturnFalse() {
        TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);
        TypeDescriptor targetType = TypeDescriptor.nested(ReflectionUtils.findField(DelimitedStringToCollectionConverterTests.Values.class, "nonConvertibleElementType"), 0);
        assertThat(new DelimitedStringToCollectionConverter(this.conversionService).matches(sourceType, targetType)).isFalse();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void convertWhenHasNoElementTypeShouldReturnTrimmedString() {
        TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);
        TypeDescriptor targetType = TypeDescriptor.nested(ReflectionUtils.findField(DelimitedStringToCollectionConverterTests.Values.class, "noElementType"), 0);
        Collection<String> converted = ((Collection<String>) (this.conversionService.convert(" a |  b| c  ", sourceType, targetType)));
        assertThat(converted).containsExactly("a", "b", "c");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void convertWhenHasConvertibleElementTypeShouldReturnConvertedType() {
        if ((this.conversionService) instanceof ApplicationConversionService) {
            TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);
            TypeDescriptor targetType = TypeDescriptor.nested(ReflectionUtils.findField(DelimitedStringToCollectionConverterTests.Values.class, "convertibleElementType"), 0);
            List<Integer> converted = ((List<Integer>) (this.conversionService.convert(" 1 |  2| 3  ", sourceType, targetType)));
            assertThat(converted).containsExactly(1, 2, 3);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void convertWhenHasDelimiterOfNoneShouldReturnWholeString() {
        TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);
        TypeDescriptor targetType = TypeDescriptor.nested(ReflectionUtils.findField(DelimitedStringToCollectionConverterTests.Values.class, "delimiterNone"), 0);
        List<String> converted = ((List<String>) (this.conversionService.convert("a,b,c", sourceType, targetType)));
        assertThat(converted).containsExactly("a,b,c");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void convertWhenHasCollectionObjectTypeShouldUseCollectionObjectType() {
        TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);
        TypeDescriptor targetType = TypeDescriptor.nested(ReflectionUtils.findField(DelimitedStringToCollectionConverterTests.Values.class, "specificType"), 0);
        DelimitedStringToCollectionConverterTests.MyCustomList<String> converted = ((DelimitedStringToCollectionConverterTests.MyCustomList<String>) (this.conversionService.convert("a*b", sourceType, targetType)));
        assertThat(converted).containsExactly("a", "b");
    }

    static class Values {
        List<String> noAnnotation;

        @SuppressWarnings("rawtypes")
        @Delimiter("|")
        List noElementType;

        @Delimiter("|")
        List<Integer> convertibleElementType;

        @Delimiter("|")
        List<DelimitedStringToCollectionConverterTests.NonConvertible> nonConvertibleElementType;

        @Delimiter(NONE)
        List<String> delimiterNone;

        @Delimiter("*")
        DelimitedStringToCollectionConverterTests.MyCustomList<String> specificType;
    }

    static class NonConvertible {}

    static class MyCustomList<E> extends LinkedList<E> {}
}

