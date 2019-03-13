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


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ReflectionUtils;

import static Delimiter.NONE;


/**
 * Tests for {@link ArrayToDelimitedStringConverter}.
 *
 * @author Phillip Webb
 */
@RunWith(Parameterized.class)
public class ArrayToDelimitedStringConverterTests {
    private final ConversionService conversionService;

    public ArrayToDelimitedStringConverterTests(String name, ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Test
    public void convertListToStringShouldConvert() {
        String[] list = new String[]{ "a", "b", "c" };
        String converted = this.conversionService.convert(list, String.class);
        assertThat(converted).isEqualTo("a,b,c");
    }

    @Test
    public void convertWhenHasDelimiterNoneShouldConvert() {
        ArrayToDelimitedStringConverterTests.Data data = new ArrayToDelimitedStringConverterTests.Data();
        data.none = new String[]{ "1", "2", "3" };
        String converted = ((String) (this.conversionService.convert(data.none, TypeDescriptor.nested(ReflectionUtils.findField(ArrayToDelimitedStringConverterTests.Data.class, "none"), 0), TypeDescriptor.valueOf(String.class))));
        assertThat(converted).isEqualTo("123");
    }

    @Test
    public void convertWhenHasDelimiterDashShouldConvert() {
        ArrayToDelimitedStringConverterTests.Data data = new ArrayToDelimitedStringConverterTests.Data();
        data.dash = new String[]{ "1", "2", "3" };
        String converted = ((String) (this.conversionService.convert(data.dash, TypeDescriptor.nested(ReflectionUtils.findField(ArrayToDelimitedStringConverterTests.Data.class, "dash"), 0), TypeDescriptor.valueOf(String.class))));
        assertThat(converted).isEqualTo("1-2-3");
    }

    @Test
    public void convertShouldConvertElements() {
        if ((this.conversionService) instanceof ApplicationConversionService) {
            ArrayToDelimitedStringConverterTests.Data data = new ArrayToDelimitedStringConverterTests.Data();
            data.type = new int[]{ 1, 2, 3 };
            String converted = ((String) (this.conversionService.convert(data.type, TypeDescriptor.nested(ReflectionUtils.findField(ArrayToDelimitedStringConverterTests.Data.class, "type"), 0), TypeDescriptor.valueOf(String.class))));
            assertThat(converted).isEqualTo("1.2.3");
        }
    }

    @Test
    public void convertShouldConvertNull() {
        String[] list = null;
        String converted = this.conversionService.convert(list, String.class);
        assertThat(converted).isNull();
    }

    static class Data {
        @Delimiter(NONE)
        String[] none;

        @Delimiter("-")
        String[] dash;

        @Delimiter(".")
        int[] type;
    }
}

