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


import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.core.convert.ConversionService;


/**
 * Tests for {@link NumberToDurationConverter}.
 *
 * @author Phillip Webb
 */
@RunWith(Parameterized.class)
public class NumberToDurationConverterTests {
    private final ConversionService conversionService;

    public NumberToDurationConverterTests(String name, ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Test
    public void convertWhenSimpleWithoutSuffixShouldReturnDuration() {
        assertThat(convert(10)).isEqualTo(Duration.ofMillis(10));
        assertThat(convert((+10))).isEqualTo(Duration.ofMillis(10));
        assertThat(convert((-10))).isEqualTo(Duration.ofMillis((-10)));
    }

    @Test
    public void convertWhenSimpleWithoutSuffixButWithAnnotationShouldReturnDuration() {
        assertThat(convert(10, ChronoUnit.SECONDS)).isEqualTo(Duration.ofSeconds(10));
        assertThat(convert((+10), ChronoUnit.SECONDS)).isEqualTo(Duration.ofSeconds(10));
        assertThat(convert((-10), ChronoUnit.SECONDS)).isEqualTo(Duration.ofSeconds((-10)));
    }
}

