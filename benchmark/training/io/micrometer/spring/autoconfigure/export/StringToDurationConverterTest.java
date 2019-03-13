/**
 * Copyright 2017 Pivotal Software, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.spring.autoconfigure.export;


import java.time.Duration;
import org.junit.Test;


public class StringToDurationConverterTest {
    private StringToDurationConverter converter = new StringToDurationConverter();

    @Test
    public void rfc() {
        assertThat(converter.convert("PT10M")).isEqualTo(Duration.ofMinutes(10));
    }

    @Test
    public void shorthand() {
        assertThat(converter.convert("10s")).isEqualTo(Duration.ofSeconds(10));
    }

    @Test
    public void invalidShorthand() {
        assertThatThrownBy(() -> converter.convert("10rs")).isInstanceOf(IllegalArgumentException.class);
    }
}

