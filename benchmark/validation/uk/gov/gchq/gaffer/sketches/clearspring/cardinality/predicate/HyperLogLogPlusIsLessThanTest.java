/**
 * Copyright 2016-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.sketches.clearspring.cardinality.predicate;


import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.koryphe.predicate.PredicateTest;


public class HyperLogLogPlusIsLessThanTest extends PredicateTest {
    private static HyperLogLogPlus hyperLogLogPlusWithCardinality5;

    private static HyperLogLogPlus hyperLogLogPlusWithCardinality15;

    private static HyperLogLogPlus hyperLogLogPlusWithCardinality31;

    @Test
    public void shouldAcceptWhenLessThan() {
        // Given
        final HyperLogLogPlusIsLessThan filter = new HyperLogLogPlusIsLessThan(15);
        // When
        boolean accepted = filter.test(HyperLogLogPlusIsLessThanTest.hyperLogLogPlusWithCardinality5);
        // Then
        Assert.assertTrue(accepted);
    }

    @Test
    public void shouldRejectWhenEqualToAndEqualToIsFalse() {
        // Given
        final HyperLogLogPlusIsLessThan filter = new HyperLogLogPlusIsLessThan(15);
        // When
        boolean accepted = filter.test(HyperLogLogPlusIsLessThanTest.hyperLogLogPlusWithCardinality15);
        // Then
        Assert.assertFalse(accepted);
    }

    @Test
    public void shouldAcceptWhenEqualToAndEqualToIsTrue() {
        // Given
        final HyperLogLogPlusIsLessThan filter = new HyperLogLogPlusIsLessThan(15, true);
        // When
        boolean accepted = filter.test(HyperLogLogPlusIsLessThanTest.hyperLogLogPlusWithCardinality15);
        // Then
        Assert.assertTrue(accepted);
    }

    @Test
    public void shouldRejectWhenMoreThan() {
        // Given
        final HyperLogLogPlusIsLessThan filter = new HyperLogLogPlusIsLessThan(15);
        // When
        boolean accepted = filter.test(HyperLogLogPlusIsLessThanTest.hyperLogLogPlusWithCardinality31);
        // Then
        Assert.assertFalse(accepted);
    }

    @Test
    public void shouldRejectWhenInputIsNull() {
        // Given
        final HyperLogLogPlusIsLessThan filter = new HyperLogLogPlusIsLessThan(15);
        // When
        boolean accepted = filter.test(null);
        // Then
        Assert.assertFalse(accepted);
    }
}

