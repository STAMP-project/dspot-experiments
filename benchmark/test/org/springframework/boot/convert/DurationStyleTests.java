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
package org.springframework.boot.convert;


import DurationStyle.ISO8601;
import DurationStyle.SIMPLE;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.junit.Test;


/**
 * Tests for {@link DurationStyle}.
 *
 * @author Phillip Webb
 */
public class DurationStyleTests {
    @Test
    public void detectAndParseWhenValueIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> DurationStyle.detectAndParse(null)).withMessageContaining("Value must not be null");
    }

    @Test
    public void detectAndParseWhenIso8601ShouldReturnDuration() {
        assertThat(DurationStyle.detectAndParse("PT20.345S")).isEqualTo(Duration.parse("PT20.345S"));
        assertThat(DurationStyle.detectAndParse("PT15M")).isEqualTo(Duration.parse("PT15M"));
        assertThat(DurationStyle.detectAndParse("+PT15M")).isEqualTo(Duration.parse("PT15M"));
        assertThat(DurationStyle.detectAndParse("PT10H")).isEqualTo(Duration.parse("PT10H"));
        assertThat(DurationStyle.detectAndParse("P2D")).isEqualTo(Duration.parse("P2D"));
        assertThat(DurationStyle.detectAndParse("P2DT3H4M")).isEqualTo(Duration.parse("P2DT3H4M"));
        assertThat(DurationStyle.detectAndParse("-PT6H3M")).isEqualTo(Duration.parse("-PT6H3M"));
        assertThat(DurationStyle.detectAndParse("-PT-6H+3M")).isEqualTo(Duration.parse("-PT-6H+3M"));
    }

    @Test
    public void detectAndParseWhenSimpleNanosShouldReturnDuration() {
        assertThat(DurationStyle.detectAndParse("10ns")).isEqualTo(Duration.ofNanos(10));
        assertThat(DurationStyle.detectAndParse("10NS")).isEqualTo(Duration.ofNanos(10));
        assertThat(DurationStyle.detectAndParse("+10ns")).isEqualTo(Duration.ofNanos(10));
        assertThat(DurationStyle.detectAndParse("-10ns")).isEqualTo(Duration.ofNanos((-10)));
    }

    @Test
    public void detectAndParseWhenSimpleMicrosShouldReturnDuration() {
        assertThat(DurationStyle.detectAndParse("10us")).isEqualTo(Duration.ofNanos(10000));
        assertThat(DurationStyle.detectAndParse("10US")).isEqualTo(Duration.ofNanos(10000));
        assertThat(DurationStyle.detectAndParse("+10us")).isEqualTo(Duration.ofNanos(10000));
        assertThat(DurationStyle.detectAndParse("-10us")).isEqualTo(Duration.ofNanos((-10000)));
    }

    @Test
    public void detectAndParseWhenSimpleMillisShouldReturnDuration() {
        assertThat(DurationStyle.detectAndParse("10ms")).isEqualTo(Duration.ofMillis(10));
        assertThat(DurationStyle.detectAndParse("10MS")).isEqualTo(Duration.ofMillis(10));
        assertThat(DurationStyle.detectAndParse("+10ms")).isEqualTo(Duration.ofMillis(10));
        assertThat(DurationStyle.detectAndParse("-10ms")).isEqualTo(Duration.ofMillis((-10)));
    }

    @Test
    public void detectAndParseWhenSimpleSecondsShouldReturnDuration() {
        assertThat(DurationStyle.detectAndParse("10s")).isEqualTo(Duration.ofSeconds(10));
        assertThat(DurationStyle.detectAndParse("10S")).isEqualTo(Duration.ofSeconds(10));
        assertThat(DurationStyle.detectAndParse("+10s")).isEqualTo(Duration.ofSeconds(10));
        assertThat(DurationStyle.detectAndParse("-10s")).isEqualTo(Duration.ofSeconds((-10)));
    }

    @Test
    public void detectAndParseWhenSimpleMinutesShouldReturnDuration() {
        assertThat(DurationStyle.detectAndParse("10m")).isEqualTo(Duration.ofMinutes(10));
        assertThat(DurationStyle.detectAndParse("10M")).isEqualTo(Duration.ofMinutes(10));
        assertThat(DurationStyle.detectAndParse("+10m")).isEqualTo(Duration.ofMinutes(10));
        assertThat(DurationStyle.detectAndParse("-10m")).isEqualTo(Duration.ofMinutes((-10)));
    }

    @Test
    public void detectAndParseWhenSimpleHoursShouldReturnDuration() {
        assertThat(DurationStyle.detectAndParse("10h")).isEqualTo(Duration.ofHours(10));
        assertThat(DurationStyle.detectAndParse("10H")).isEqualTo(Duration.ofHours(10));
        assertThat(DurationStyle.detectAndParse("+10h")).isEqualTo(Duration.ofHours(10));
        assertThat(DurationStyle.detectAndParse("-10h")).isEqualTo(Duration.ofHours((-10)));
    }

    @Test
    public void detectAndParseWhenSimpleDaysShouldReturnDuration() {
        assertThat(DurationStyle.detectAndParse("10d")).isEqualTo(Duration.ofDays(10));
        assertThat(DurationStyle.detectAndParse("10D")).isEqualTo(Duration.ofDays(10));
        assertThat(DurationStyle.detectAndParse("+10d")).isEqualTo(Duration.ofDays(10));
        assertThat(DurationStyle.detectAndParse("-10d")).isEqualTo(Duration.ofDays((-10)));
    }

    @Test
    public void detectAndParseWhenSimpleWithoutSuffixShouldReturnDuration() {
        assertThat(DurationStyle.detectAndParse("10")).isEqualTo(Duration.ofMillis(10));
        assertThat(DurationStyle.detectAndParse("+10")).isEqualTo(Duration.ofMillis(10));
        assertThat(DurationStyle.detectAndParse("-10")).isEqualTo(Duration.ofMillis((-10)));
    }

    @Test
    public void detectAndParseWhenSimpleWithoutSuffixButWithChronoUnitShouldReturnDuration() {
        assertThat(DurationStyle.detectAndParse("10", ChronoUnit.SECONDS)).isEqualTo(Duration.ofSeconds(10));
        assertThat(DurationStyle.detectAndParse("+10", ChronoUnit.SECONDS)).isEqualTo(Duration.ofSeconds(10));
        assertThat(DurationStyle.detectAndParse("-10", ChronoUnit.SECONDS)).isEqualTo(Duration.ofSeconds((-10)));
    }

    @Test
    public void detectAndParseWhenBadFormatShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> DurationStyle.detectAndParse("10foo")).withMessageContaining("'10foo' is not a valid duration");
    }

    @Test
    public void detectWhenSimpleShouldReturnSimple() {
        assertThat(DurationStyle.detect("10")).isEqualTo(SIMPLE);
        assertThat(DurationStyle.detect("+10")).isEqualTo(SIMPLE);
        assertThat(DurationStyle.detect("-10")).isEqualTo(SIMPLE);
        assertThat(DurationStyle.detect("10ns")).isEqualTo(SIMPLE);
        assertThat(DurationStyle.detect("10ms")).isEqualTo(SIMPLE);
        assertThat(DurationStyle.detect("10s")).isEqualTo(SIMPLE);
        assertThat(DurationStyle.detect("10m")).isEqualTo(SIMPLE);
        assertThat(DurationStyle.detect("10h")).isEqualTo(SIMPLE);
        assertThat(DurationStyle.detect("10d")).isEqualTo(SIMPLE);
        assertThat(DurationStyle.detect("-10ms")).isEqualTo(SIMPLE);
        assertThat(DurationStyle.detect("-10ms")).isEqualTo(SIMPLE);
        assertThat(DurationStyle.detect("10D")).isEqualTo(SIMPLE);
    }

    @Test
    public void detectWhenIso8601ShouldReturnIso8601() {
        assertThat(DurationStyle.detect("PT20.345S")).isEqualTo(ISO8601);
        assertThat(DurationStyle.detect("PT15M")).isEqualTo(ISO8601);
        assertThat(DurationStyle.detect("+PT15M")).isEqualTo(ISO8601);
        assertThat(DurationStyle.detect("PT10H")).isEqualTo(ISO8601);
        assertThat(DurationStyle.detect("P2D")).isEqualTo(ISO8601);
        assertThat(DurationStyle.detect("P2DT3H4M")).isEqualTo(ISO8601);
        assertThat(DurationStyle.detect("-PT6H3M")).isEqualTo(ISO8601);
        assertThat(DurationStyle.detect("-PT-6H+3M")).isEqualTo(ISO8601);
    }

    @Test
    public void detectWhenUnknownShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> DurationStyle.detect("bad")).withMessageContaining("'bad' is not a valid duration");
    }

    @Test
    public void parseIso8601ShouldParse() {
        assertThat(ISO8601.parse("PT20.345S")).isEqualTo(Duration.parse("PT20.345S"));
        assertThat(ISO8601.parse("PT15M")).isEqualTo(Duration.parse("PT15M"));
        assertThat(ISO8601.parse("+PT15M")).isEqualTo(Duration.parse("PT15M"));
        assertThat(ISO8601.parse("PT10H")).isEqualTo(Duration.parse("PT10H"));
        assertThat(ISO8601.parse("P2D")).isEqualTo(Duration.parse("P2D"));
        assertThat(ISO8601.parse("P2DT3H4M")).isEqualTo(Duration.parse("P2DT3H4M"));
        assertThat(ISO8601.parse("-PT6H3M")).isEqualTo(Duration.parse("-PT6H3M"));
        assertThat(ISO8601.parse("-PT-6H+3M")).isEqualTo(Duration.parse("-PT-6H+3M"));
    }

    @Test
    public void parseIso8601WithUnitShouldIgnoreUnit() {
        assertThat(ISO8601.parse("PT20.345S", ChronoUnit.SECONDS)).isEqualTo(Duration.parse("PT20.345S"));
        assertThat(ISO8601.parse("PT15M", ChronoUnit.SECONDS)).isEqualTo(Duration.parse("PT15M"));
        assertThat(ISO8601.parse("+PT15M", ChronoUnit.SECONDS)).isEqualTo(Duration.parse("PT15M"));
        assertThat(ISO8601.parse("PT10H", ChronoUnit.SECONDS)).isEqualTo(Duration.parse("PT10H"));
        assertThat(ISO8601.parse("P2D")).isEqualTo(Duration.parse("P2D"));
        assertThat(ISO8601.parse("P2DT3H4M", ChronoUnit.SECONDS)).isEqualTo(Duration.parse("P2DT3H4M"));
        assertThat(ISO8601.parse("-PT6H3M", ChronoUnit.SECONDS)).isEqualTo(Duration.parse("-PT6H3M"));
        assertThat(ISO8601.parse("-PT-6H+3M", ChronoUnit.SECONDS)).isEqualTo(Duration.parse("-PT-6H+3M"));
    }

    @Test
    public void parseIso8601WhenSimpleShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> DurationStyle.ISO8601.parse("10d")).withMessageContaining("'10d' is not a valid ISO-8601 duration");
    }

    @Test
    public void parseSimpleShouldParse() {
        assertThat(SIMPLE.parse("10m")).isEqualTo(Duration.ofMinutes(10));
    }

    @Test
    public void parseSimpleWithUnitShouldUseUnitAsFallback() {
        assertThat(SIMPLE.parse("10m", ChronoUnit.SECONDS)).isEqualTo(Duration.ofMinutes(10));
        assertThat(SIMPLE.parse("10", ChronoUnit.MINUTES)).isEqualTo(Duration.ofMinutes(10));
    }

    @Test
    public void parseSimpleWhenUnknownUnitShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> DurationStyle.SIMPLE.parse("10mb")).satisfies(( ex) -> assertThat(ex.getCause().getMessage()).isEqualTo("Unknown unit 'mb'"));
    }

    @Test
    public void parseSimpleWhenIso8601ShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> DurationStyle.SIMPLE.parse("PT10H")).withMessageContaining("'PT10H' is not a valid simple duration");
    }

    @Test
    public void printIso8601ShouldPrint() {
        Duration duration = Duration.parse("-PT-6H+3M");
        assertThat(ISO8601.print(duration)).isEqualTo("PT5H57M");
    }

    @Test
    public void printIso8601ShouldIgnoreUnit() {
        Duration duration = Duration.parse("-PT-6H+3M");
        assertThat(ISO8601.print(duration, ChronoUnit.DAYS)).isEqualTo("PT5H57M");
    }

    @Test
    public void printSimpleWithoutUnitShouldPrintInMs() {
        Duration duration = Duration.ofSeconds(1);
        assertThat(SIMPLE.print(duration)).isEqualTo("1000ms");
    }

    @Test
    public void printSimpleWithUnitShouldPrintInUnit() {
        Duration duration = Duration.ofMillis(1000);
        assertThat(SIMPLE.print(duration, ChronoUnit.SECONDS)).isEqualTo("1s");
    }
}

