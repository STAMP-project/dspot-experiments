/**
 * Copyright 2018-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.sketches.datasketches.cardinality.predicate;


import com.yahoo.sketches.hll.HllSketch;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.koryphe.predicate.PredicateTest;


public class HllSketchIsLessThanTest extends PredicateTest {
    private static final double DELTA = 1.0E-5;

    private static HllSketch hllSketchWithCardinality5;

    private static HllSketch hllSketchWithCardinality18;

    private static HllSketch hllSketchWithCardinality32;

    @Test
    public void shouldAcceptWhenLessThan() {
        // Given
        final HllSketchIsLessThan filter = new HllSketchIsLessThan(15);
        // When
        boolean accepted = filter.test(HllSketchIsLessThanTest.hllSketchWithCardinality5);
        // Then
        Assert.assertTrue(accepted);
    }

    @Test
    public void shouldRejectWhenEqualToAndEqualToIsFalse() {
        // Given
        final HllSketchIsLessThan filter = new HllSketchIsLessThan(18);
        // When
        boolean accepted = filter.test(HllSketchIsLessThanTest.hllSketchWithCardinality18);
        // Then
        Assert.assertFalse(accepted);
    }

    @Test
    public void shouldAcceptWhenEqualToAndEqualToIsTrue() {
        // Given
        final HllSketchIsLessThan filter = new HllSketchIsLessThan(18, true);
        // When
        boolean accepted = filter.test(HllSketchIsLessThanTest.hllSketchWithCardinality18);
        // Then
        Assert.assertTrue(accepted);
    }

    @Test
    public void shouldRejectWhenMoreThan() {
        // Given
        final HllSketchIsLessThan filter = new HllSketchIsLessThan(15);
        // When
        boolean accepted = filter.test(HllSketchIsLessThanTest.hllSketchWithCardinality32);
        // Then
        Assert.assertFalse(accepted);
    }

    @Test
    public void shouldRejectWhenInputIsNull() {
        // Given
        final HllSketchIsLessThan filter = new HllSketchIsLessThan(15);
        // When
        boolean accepted = filter.test(null);
        // Then
        Assert.assertFalse(accepted);
    }
}

