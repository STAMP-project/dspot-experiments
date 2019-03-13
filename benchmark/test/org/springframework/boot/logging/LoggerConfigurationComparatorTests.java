/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.logging;


import org.junit.Test;

import static LogLevel.OFF;


/**
 * Tests for {@link LoggerConfigurationComparator}.
 *
 * @author Ben Hale
 */
public class LoggerConfigurationComparatorTests {
    private final LoggerConfigurationComparator comparator = new LoggerConfigurationComparator("ROOT");

    @Test
    public void rootLoggerFirst() {
        LoggerConfiguration first = new LoggerConfiguration("ROOT", null, OFF);
        LoggerConfiguration second = new LoggerConfiguration("alpha", null, OFF);
        assertThat(this.comparator.compare(first, second)).isLessThan(0);
    }

    @Test
    public void rootLoggerSecond() {
        LoggerConfiguration first = new LoggerConfiguration("alpha", null, OFF);
        LoggerConfiguration second = new LoggerConfiguration("ROOT", null, OFF);
        assertThat(this.comparator.compare(first, second)).isGreaterThan(0);
    }

    @Test
    public void rootLoggerFirstEmpty() {
        LoggerConfiguration first = new LoggerConfiguration("ROOT", null, OFF);
        LoggerConfiguration second = new LoggerConfiguration("", null, OFF);
        assertThat(this.comparator.compare(first, second)).isLessThan(0);
    }

    @Test
    public void rootLoggerSecondEmpty() {
        LoggerConfiguration first = new LoggerConfiguration("", null, OFF);
        LoggerConfiguration second = new LoggerConfiguration("ROOT", null, OFF);
        assertThat(this.comparator.compare(first, second)).isGreaterThan(0);
    }

    @Test
    public void lexicalFirst() {
        LoggerConfiguration first = new LoggerConfiguration("alpha", null, OFF);
        LoggerConfiguration second = new LoggerConfiguration("bravo", null, OFF);
        assertThat(this.comparator.compare(first, second)).isLessThan(0);
    }

    @Test
    public void lexicalSecond() {
        LoggerConfiguration first = new LoggerConfiguration("bravo", null, OFF);
        LoggerConfiguration second = new LoggerConfiguration("alpha", null, OFF);
        assertThat(this.comparator.compare(first, second)).isGreaterThan(0);
    }

    @Test
    public void lexicalEqual() {
        LoggerConfiguration first = new LoggerConfiguration("alpha", null, OFF);
        LoggerConfiguration second = new LoggerConfiguration("alpha", null, OFF);
        assertThat(this.comparator.compare(first, second)).isEqualTo(0);
    }
}

