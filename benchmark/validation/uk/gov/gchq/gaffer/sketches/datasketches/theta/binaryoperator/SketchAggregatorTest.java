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
package uk.gov.gchq.gaffer.sketches.datasketches.theta.binaryoperator;


import com.yahoo.sketches.theta.Sketch;
import com.yahoo.sketches.theta.UpdateSketch;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.koryphe.binaryoperator.BinaryOperatorTest;


public class SketchAggregatorTest extends BinaryOperatorTest {
    private static final double DELTA = 0.01;

    private UpdateSketch sketch1;

    private UpdateSketch sketch2;

    @Test
    public void testAggregate() {
        final SketchAggregator unionAggregator = new SketchAggregator();
        Sketch currentState = sketch1;
        Assert.assertEquals(2.0, currentState.getEstimate(), SketchAggregatorTest.DELTA);
        currentState = unionAggregator.apply(currentState, sketch2);
        Assert.assertEquals(4.0, currentState.getEstimate(), SketchAggregatorTest.DELTA);
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(new SketchAggregator(), new SketchAggregator());
    }
}

