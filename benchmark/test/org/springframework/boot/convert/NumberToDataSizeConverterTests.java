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


import DataUnit.KILOBYTES;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.unit.DataSize;


/**
 * Tests for {@link NumberToDataSizeConverter}.
 *
 * @author Stephane Nicoll
 */
@RunWith(Parameterized.class)
public class NumberToDataSizeConverterTests {
    private final ConversionService conversionService;

    public NumberToDataSizeConverterTests(String name, ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Test
    public void convertWhenSimpleWithoutSuffixShouldReturnDataSize() {
        assertThat(convert(10)).isEqualTo(DataSize.ofBytes(10));
        assertThat(convert((+10))).isEqualTo(DataSize.ofBytes(10));
        assertThat(convert((-10))).isEqualTo(DataSize.ofBytes((-10)));
    }

    @Test
    public void convertWhenSimpleWithoutSuffixButWithAnnotationShouldReturnDataSize() {
        assertThat(convert(10, KILOBYTES)).isEqualTo(DataSize.ofKilobytes(10));
        assertThat(convert((+10), KILOBYTES)).isEqualTo(DataSize.ofKilobytes(10));
        assertThat(convert((-10), KILOBYTES)).isEqualTo(DataSize.ofKilobytes((-10)));
    }
}

