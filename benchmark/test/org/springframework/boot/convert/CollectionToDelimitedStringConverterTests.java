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


import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ReflectionUtils;

import static Delimiter.NONE;


/**
 * Tests for {@link CollectionToDelimitedStringConverter}.
 *
 * @author Phillip Webb
 */
@RunWith(Parameterized.class)
public class CollectionToDelimitedStringConverterTests {
    private final ConversionService conversionService;

    public CollectionToDelimitedStringConverterTests(String name, ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Test
    public void convertListToStringShouldConvert() {
        List<String> list = Arrays.asList("a", "b", "c");
        String converted = this.conversionService.convert(list, String.class);
        assertThat(converted).isEqualTo("a,b,c");
    }

    @Test
    public void convertWhenHasDelimiterNoneShouldConvert() {
        CollectionToDelimitedStringConverterTests.Data data = new CollectionToDelimitedStringConverterTests.Data();
        data.none = Arrays.asList("1", "2", "3");
        String converted = ((String) (this.conversionService.convert(data.none, TypeDescriptor.nested(ReflectionUtils.findField(CollectionToDelimitedStringConverterTests.Data.class, "none"), 0), TypeDescriptor.valueOf(String.class))));
        assertThat(converted).isEqualTo("123");
    }

    @Test
    public void convertWhenHasDelimiterDashShouldConvert() {
        CollectionToDelimitedStringConverterTests.Data data = new CollectionToDelimitedStringConverterTests.Data();
        data.dash = Arrays.asList("1", "2", "3");
        String converted = ((String) (this.conversionService.convert(data.dash, TypeDescriptor.nested(ReflectionUtils.findField(CollectionToDelimitedStringConverterTests.Data.class, "dash"), 0), TypeDescriptor.valueOf(String.class))));
        assertThat(converted).isEqualTo("1-2-3");
    }

    @Test
    public void convertShouldConvertElements() {
        if ((this.conversionService) instanceof ApplicationConversionService) {
            CollectionToDelimitedStringConverterTests.Data data = new CollectionToDelimitedStringConverterTests.Data();
            data.type = Arrays.asList(1, 2, 3);
            String converted = ((String) (this.conversionService.convert(data.type, TypeDescriptor.nested(ReflectionUtils.findField(CollectionToDelimitedStringConverterTests.Data.class, "type"), 0), TypeDescriptor.valueOf(String.class))));
            assertThat(converted).isEqualTo("1.2.3");
        }
    }

    @Test
    public void convertShouldConvertNull() {
        List<String> list = null;
        String converted = this.conversionService.convert(list, String.class);
        assertThat(converted).isNull();
    }

    static class Data {
        @Delimiter(NONE)
        List<String> none;

        @Delimiter("-")
        List<String> dash;

        @Delimiter(".")
        List<Integer> type;
    }
}

