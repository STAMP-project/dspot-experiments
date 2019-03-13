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
package org.springframework.boot.actuate.autoconfigure.metrics;


import DistributionStatisticConfig.DEFAULT;
import MeterFilterReply.DENY;
import MeterFilterReply.NEUTRAL;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Duration;
import org.junit.Test;


/**
 * Tests for {@link PropertiesMeterFilter}.
 *
 * @author Phillip Webb
 * @author Jon Schneider
 * @author Artsiom Yudovin
 */
public class PropertiesMeterFilterTests {
    @Test
    public void createWhenPropertiesIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new PropertiesMeterFilter(null)).withMessageContaining("Properties must not be null");
    }

    @Test
    public void acceptWhenHasNoEnabledPropertiesShouldReturnNeutral() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties());
        assertThat(filter.accept(createMeterId("spring.boot"))).isEqualTo(NEUTRAL);
    }

    @Test
    public void acceptWhenHasNoMatchingEnabledPropertyShouldReturnNeutral() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("enable.something.else=false"));
        assertThat(filter.accept(createMeterId("spring.boot"))).isEqualTo(NEUTRAL);
    }

    @Test
    public void acceptWhenHasEnableFalseShouldReturnDeny() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("enable.spring.boot=false"));
        assertThat(filter.accept(createMeterId("spring.boot"))).isEqualTo(DENY);
    }

    @Test
    public void acceptWhenHasEnableTrueShouldReturnNeutral() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("enable.spring.boot=true"));
        assertThat(filter.accept(createMeterId("spring.boot"))).isEqualTo(NEUTRAL);
    }

    @Test
    public void acceptWhenHasHigherEnableFalseShouldReturnDeny() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("enable.spring=false"));
        assertThat(filter.accept(createMeterId("spring.boot"))).isEqualTo(DENY);
    }

    @Test
    public void acceptWhenHasHigherEnableTrueShouldReturnNeutral() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("enable.spring=true"));
        assertThat(filter.accept(createMeterId("spring.boot"))).isEqualTo(NEUTRAL);
    }

    @Test
    public void acceptWhenHasHigherEnableFalseExactEnableTrueShouldReturnNeutral() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("enable.spring=false", "enable.spring.boot=true"));
        assertThat(filter.accept(createMeterId("spring.boot"))).isEqualTo(NEUTRAL);
    }

    @Test
    public void acceptWhenHasHigherEnableTrueExactEnableFalseShouldReturnDeny() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("enable.spring=true", "enable.spring.boot=false"));
        assertThat(filter.accept(createMeterId("spring.boot"))).isEqualTo(DENY);
    }

    @Test
    public void acceptWhenHasAllEnableFalseShouldReturnDeny() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("enable.all=false"));
        assertThat(filter.accept(createMeterId("spring.boot"))).isEqualTo(DENY);
    }

    @Test
    public void acceptWhenHasAllEnableFalseButHigherEnableTrueShouldReturnNeutral() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("enable.all=false", "enable.spring=true"));
        assertThat(filter.accept(createMeterId("spring.boot"))).isEqualTo(NEUTRAL);
    }

    @Test
    public void configureWhenHasHistogramTrueShouldSetPercentilesHistogramToTrue() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("distribution.percentiles-histogram.spring.boot=true"));
        assertThat(filter.configure(createMeterId("spring.boot"), DEFAULT).isPercentileHistogram()).isTrue();
    }

    @Test
    public void configureWhenHasHistogramFalseShouldSetPercentilesHistogramToFalse() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("distribution.percentiles-histogram.spring.boot=false"));
        assertThat(filter.configure(createMeterId("spring.boot"), DEFAULT).isPercentileHistogram()).isFalse();
    }

    @Test
    public void configureWhenHasHigherHistogramTrueShouldSetPercentilesHistogramToTrue() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("distribution.percentiles-histogram.spring=true"));
        assertThat(filter.configure(createMeterId("spring.boot"), DEFAULT).isPercentileHistogram()).isTrue();
    }

    @Test
    public void configureWhenHasHigherHistogramFalseShouldSetPercentilesHistogramToFalse() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("distribution.percentiles-histogram.spring=false"));
        assertThat(filter.configure(createMeterId("spring.boot"), DEFAULT).isPercentileHistogram()).isFalse();
    }

    @Test
    public void configureWhenHasHigherHistogramTrueAndLowerFalseShouldSetPercentilesHistogramToFalse() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("distribution.percentiles-histogram.spring=true", "distribution.percentiles-histogram.spring.boot=false"));
        assertThat(filter.configure(createMeterId("spring.boot"), DEFAULT).isPercentileHistogram()).isFalse();
    }

    @Test
    public void configureWhenHasHigherHistogramFalseAndLowerTrueShouldSetPercentilesHistogramToFalse() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("distribution.percentiles-histogram.spring=false", "distribution.percentiles-histogram.spring.boot=true"));
        assertThat(filter.configure(createMeterId("spring.boot"), DEFAULT).isPercentileHistogram()).isTrue();
    }

    @Test
    public void configureWhenAllHistogramTrueSetPercentilesHistogramToTrue() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("distribution.percentiles-histogram.all=true"));
        assertThat(filter.configure(createMeterId("spring.boot"), DEFAULT).isPercentileHistogram()).isTrue();
    }

    @Test
    public void configureWhenHasPercentilesShouldSetPercentilesToValue() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("distribution.percentiles.spring.boot=1,1.5,2"));
        assertThat(filter.configure(createMeterId("spring.boot"), DEFAULT).getPercentiles()).containsExactly(1, 1.5, 2);
    }

    @Test
    public void configureWhenHasHigherPercentilesShouldSetPercentilesToValue() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("distribution.percentiles.spring=1,1.5,2"));
        assertThat(filter.configure(createMeterId("spring.boot"), DEFAULT).getPercentiles()).containsExactly(1, 1.5, 2);
    }

    @Test
    public void configureWhenHasHigherPercentilesAndLowerShouldSetPercentilesToHigher() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("distribution.percentiles.spring=1,1.5,2", "distribution.percentiles.spring.boot=3,3.5,4"));
        assertThat(filter.configure(createMeterId("spring.boot"), DEFAULT).getPercentiles()).containsExactly(3, 3.5, 4);
    }

    @Test
    public void configureWhenAllPercentilesSetShouldSetPercentilesToValue() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("distribution.percentiles.all=1,1.5,2"));
        assertThat(filter.configure(createMeterId("spring.boot"), DEFAULT).getPercentiles()).containsExactly(1, 1.5, 2);
    }

    @Test
    public void configureWhenHasSlaShouldSetSlaToValue() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("distribution.sla.spring.boot=1,2,3"));
        assertThat(filter.configure(createMeterId("spring.boot"), DEFAULT).getSlaBoundaries()).containsExactly(1000000, 2000000, 3000000);
    }

    @Test
    public void configureWhenHasHigherSlaShouldSetPercentilesToValue() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("distribution.sla.spring=1,2,3"));
        assertThat(filter.configure(createMeterId("spring.boot"), DEFAULT).getSlaBoundaries()).containsExactly(1000000, 2000000, 3000000);
    }

    @Test
    public void configureWhenHasHigherSlaAndLowerShouldSetSlaToHigher() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("distribution.sla.spring=1,2,3", "distribution.sla.spring.boot=4,5,6"));
        assertThat(filter.configure(createMeterId("spring.boot"), DEFAULT).getSlaBoundaries()).containsExactly(4000000, 5000000, 6000000);
    }

    @Test
    public void configureWhenHasMinimumExpectedValueShouldSetMinimumExpectedToValue() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("distribution.minimum-expected-value.spring.boot=10"));
        assertThat(filter.configure(createMeterId("spring.boot"), DEFAULT).getMinimumExpectedValue()).isEqualTo(Duration.ofMillis(10).toNanos());
    }

    @Test
    public void configureWhenHasHigherMinimumExpectedValueShouldSetMinimumExpectedValueToValue() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("distribution.minimum-expected-value.spring=10"));
        assertThat(filter.configure(createMeterId("spring.boot"), DEFAULT).getMinimumExpectedValue()).isEqualTo(Duration.ofMillis(10).toNanos());
    }

    @Test
    public void configureWhenHasHigherMinimumExpectedValueAndLowerShouldSetMinimumExpectedValueToHigher() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("distribution.minimum-expected-value.spring=10", "distribution.minimum-expected-value.spring.boot=50"));
        assertThat(filter.configure(createMeterId("spring.boot"), DEFAULT).getMinimumExpectedValue()).isEqualTo(Duration.ofMillis(50).toNanos());
    }

    @Test
    public void configureWhenHasMaximumExpectedValueShouldSetMaximumExpectedToValue() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("distribution.maximum-expected-value.spring.boot=5000"));
        assertThat(filter.configure(createMeterId("spring.boot"), DEFAULT).getMaximumExpectedValue()).isEqualTo(Duration.ofMillis(5000).toNanos());
    }

    @Test
    public void configureWhenHasHigherMaximumExpectedValueShouldSetMaximumExpectedValueToValue() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("distribution.maximum-expected-value.spring=5000"));
        assertThat(filter.configure(createMeterId("spring.boot"), DEFAULT).getMaximumExpectedValue()).isEqualTo(Duration.ofMillis(5000).toNanos());
    }

    @Test
    public void configureWhenHasHigherMaximumExpectedValueAndLowerShouldSetMaximumExpectedValueToHigher() {
        PropertiesMeterFilter filter = new PropertiesMeterFilter(createProperties("distribution.maximum-expected-value.spring=5000", "distribution.maximum-expected-value.spring.boot=10000"));
        assertThat(filter.configure(createMeterId("spring.boot"), DEFAULT).getMaximumExpectedValue()).isEqualTo(Duration.ofMillis(10000).toNanos());
    }

    private static class TestMeterRegistry extends SimpleMeterRegistry {}
}

