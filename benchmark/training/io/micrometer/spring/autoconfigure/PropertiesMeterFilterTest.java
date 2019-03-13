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
package io.micrometer.spring.autoconfigure;


import DistributionStatisticConfig.DEFAULT;
import MeterFilterReply.DENY;
import MeterFilterReply.NEUTRAL;
import java.time.Duration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests for {@link PropertiesMeterFilter}.
 *
 * @author Phillip Webb
 * @author Jon Schneider
 * @author Artsiom Yudovin
 */
@SuppressWarnings("ConstantConditions")
public class PropertiesMeterFilterTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private MetricsProperties properties = new MetricsProperties();

    private PropertiesMeterFilter filter = new PropertiesMeterFilter(properties);

    @Test
    public void createWhenPropertiesIsNullShouldThrowException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Properties must not be null");
        new PropertiesMeterFilter(null);
    }

    @Test
    public void acceptWhenHasNoEnabledPropertiesShouldReturnNeutral() {
        assertThat(filter.accept(createSpringBootMeter())).isEqualTo(NEUTRAL);
    }

    @Test
    public void acceptWhenHasNoMatchingEnabledPropertyShouldReturnNeutral() {
        properties.getEnable().put("something.else", false);
        assertThat(filter.accept(createSpringBootMeter())).isEqualTo(NEUTRAL);
    }

    @Test
    public void acceptWhenHasEnableFalseShouldReturnDeny() {
        enable("spring.boot", false);
        assertThat(filter.accept(createSpringBootMeter())).isEqualTo(DENY);
    }

    @Test
    public void acceptWhenHasEnableTrueShouldReturnNeutral() {
        enable("spring.boot", true);
        assertThat(filter.accept(createSpringBootMeter())).isEqualTo(NEUTRAL);
    }

    @Test
    public void acceptWhenHasHigherEnableFalseShouldReturnDeny() {
        enable("spring", false);
        assertThat(filter.accept(createSpringBootMeter())).isEqualTo(DENY);
    }

    @Test
    public void acceptWhenHasHigherEnableTrueShouldReturnNeutral() {
        enable("spring", true);
        assertThat(filter.accept(createSpringBootMeter())).isEqualTo(NEUTRAL);
    }

    @Test
    public void acceptWhenHasHigherEnableFalseExactEnableTrueShouldReturnNeutral() {
        enable("spring", false);
        enable("spring.boot", true);
        assertThat(filter.accept(createSpringBootMeter())).isEqualTo(NEUTRAL);
    }

    @Test
    public void acceptWhenHasHigherEnableTrueExactEnableFalseShouldReturnDeny() {
        enable("spring", true);
        enable("spring.boot", false);
        assertThat(filter.accept(createSpringBootMeter())).isEqualTo(DENY);
    }

    @Test
    public void acceptWhenHasAllEnableFalseShouldReturnDeny() {
        enable("all", false);
        assertThat(filter.accept(createSpringBootMeter())).isEqualTo(DENY);
    }

    @Test
    public void acceptWhenHasAllEnableFalseButHigherEnableTrueShouldReturnNeutral() {
        enable("all", false);
        enable("spring", true);
        assertThat(filter.accept(createSpringBootMeter())).isEqualTo(NEUTRAL);
    }

    @Test
    public void configureWhenHasHistogramTrueShouldSetPercentilesHistogramToTrue() {
        percentilesHistogram("spring.boot", true);
        assertThat(filter.configure(createSpringBootMeter(), DEFAULT).isPercentileHistogram()).isTrue();
    }

    @Test
    public void configureWhenHasHistogramFalseShouldSetPercentilesHistogramToFalse() {
        percentilesHistogram("spring.boot", false);
        assertThat(filter.configure(createSpringBootMeter(), DEFAULT).isPercentileHistogram()).isFalse();
    }

    @Test
    public void configureWhenHasHigherHistogramTrueShouldSetPercentilesHistogramToTrue() {
        percentilesHistogram("spring", true);
        assertThat(filter.configure(createSpringBootMeter(), DEFAULT).isPercentileHistogram()).isTrue();
    }

    @Test
    public void configureWhenHasHigherHistogramFalseShouldSetPercentilesHistogramToFalse() {
        percentilesHistogram("spring", false);
        assertThat(filter.configure(createSpringBootMeter(), DEFAULT).isPercentileHistogram()).isFalse();
    }

    @Test
    public void configureWhenHasHigherHistogramTrueAndLowerFalseShouldSetPercentilesHistogramToFalse() {
        percentilesHistogram("spring", true);
        percentilesHistogram("spring.boot", false);
        assertThat(filter.configure(createSpringBootMeter(), DEFAULT).isPercentileHistogram()).isFalse();
    }

    @Test
    public void configureWhenHasHigherHistogramFalseAndLowerTrueShouldSetPercentilesHistogramToFalse() {
        percentilesHistogram("spring", false);
        percentilesHistogram("spring.boot", true);
        assertThat(filter.configure(createSpringBootMeter(), DEFAULT).isPercentileHistogram()).isTrue();
    }

    @Test
    public void configureWhenAllHistogramTrueSetPercentilesHistogramToTrue() {
        percentilesHistogram("all", true);
        assertThat(filter.configure(createSpringBootMeter(), DEFAULT).isPercentileHistogram()).isTrue();
    }

    @Test
    public void configureWhenHasPercentilesShouldSetPercentilesToValue() {
        percentiles("spring.boot", 0.5, 0.9);
        assertThat(filter.configure(createSpringBootMeter(), DEFAULT).getPercentiles()).containsExactly(0.5, 0.9);
    }

    @Test
    public void configureWhenHasHigherPercentilesShouldSetPercentilesToValue() {
        percentiles("spring", 0.5, 0.9);
        assertThat(filter.configure(createSpringBootMeter(), DEFAULT).getPercentiles()).containsExactly(0.5, 0.9);
    }

    @Test
    public void configureWhenHasHigherPercentilesAndLowerShouldSetPercentilesToHigher() {
        percentiles("spring", 0.5);
        percentiles("spring.boot", 0.9);
        assertThat(filter.configure(createSpringBootMeter(), DEFAULT).getPercentiles()).containsExactly(0.9);
    }

    @Test
    public void configureWhenAllPercentilesSetShouldSetPercentilesToValue() {
        percentiles("all", 0.5);
        assertThat(filter.configure(createSpringBootMeter(), DEFAULT).getPercentiles()).containsExactly(0.5);
    }

    @Test
    public void configureWhenHasSlaShouldSetSlaToValue() {
        slas("spring.boot", "1", "2", "3");
        assertThat(filter.configure(createSpringBootMeter(), DEFAULT).getSlaBoundaries()).containsExactly(1000000, 2000000, 3000000);
    }

    @Test
    public void configureWhenHasHigherSlaShouldSetPercentilesToValue() {
        slas("spring", "1", "2", "3");
        assertThat(filter.configure(createSpringBootMeter(), DEFAULT).getSlaBoundaries()).containsExactly(1000000, 2000000, 3000000);
    }

    @Test
    public void configureWhenHasHigherSlaAndLowerShouldSetSlaToHigher() {
        slas("spring", "1", "2", "3");
        slas("spring.boot", "4", "5", "6");
        assertThat(filter.configure(createSpringBootMeter(), DEFAULT).getSlaBoundaries()).containsExactly(4000000, 5000000, 6000000);
    }

    @Test
    public void configureWhenHasMinimumExpectedValueShouldSetMinimumExpectedToValue() {
        setMinimumExpectedValue("spring.boot", 10);
        assertThat(filter.configure(createSpringBootMeter(), DEFAULT).getMinimumExpectedValue()).isEqualTo(Duration.ofMillis(10).toNanos());
    }

    @Test
    public void configureWhenHasHigherMinimumExpectedValueShouldSetMinimumExpectedValueToValue() {
        setMinimumExpectedValue("spring", 10);
        assertThat(filter.configure(createSpringBootMeter(), DEFAULT).getMinimumExpectedValue()).isEqualTo(Duration.ofMillis(10).toNanos());
    }

    @Test
    public void configureWhenHasHigherMinimumExpectedValueAndLowerShouldSetMinimumExpectedValueToHigher() {
        setMinimumExpectedValue("spring", 10);
        setMinimumExpectedValue("spring.boot", 50);
        assertThat(filter.configure(createSpringBootMeter(), DEFAULT).getMinimumExpectedValue()).isEqualTo(Duration.ofMillis(50).toNanos());
    }

    @Test
    public void configureWhenHasMaximumExpectedValueShouldSetMaximumExpectedToValue() {
        setMaximumExpectedValue("spring.boot", 5000);
        assertThat(filter.configure(createSpringBootMeter(), DEFAULT).getMaximumExpectedValue()).isEqualTo(Duration.ofMillis(5000).toNanos());
    }

    @Test
    public void configureWhenHasHigherMaximumExpectedValueShouldSetMaximumExpectedValueToValue() {
        setMaximumExpectedValue("spring", 5000);
        assertThat(filter.configure(createSpringBootMeter(), DEFAULT).getMaximumExpectedValue()).isEqualTo(Duration.ofMillis(5000).toNanos());
    }

    @Test
    public void configureWhenHasHigherMaximumExpectedValueAndLowerShouldSetMaximumExpectedValueToHigher() {
        setMaximumExpectedValue("spring", 5000);
        setMaximumExpectedValue("spring.boot", 10000);
        assertThat(filter.configure(createSpringBootMeter(), DEFAULT).getMaximumExpectedValue()).isEqualTo(Duration.ofMillis(10000).toNanos());
    }
}

