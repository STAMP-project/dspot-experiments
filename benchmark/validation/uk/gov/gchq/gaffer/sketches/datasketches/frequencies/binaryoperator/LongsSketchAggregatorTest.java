/**
 * Copyright 2017-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.sketches.datasketches.frequencies.binaryoperator;


import com.yahoo.sketches.frequencies.LongsSketch;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.koryphe.binaryoperator.BinaryOperatorTest;


public class LongsSketchAggregatorTest extends BinaryOperatorTest {
    private LongsSketch sketch1;

    private LongsSketch sketch2;

    @Test
    public void testAggregate() {
        final LongsSketchAggregator sketchAggregator = new LongsSketchAggregator();
        LongsSketch currentState = sketch1;
        Assert.assertEquals(1L, currentState.getEstimate(1L));
        currentState = sketchAggregator.apply(currentState, sketch2);
        Assert.assertEquals(1L, currentState.getEstimate(1L));
        Assert.assertEquals(2L, currentState.getEstimate(3L));
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(new LongsSketchAggregator(), new LongsSketchAggregator());
    }
}

