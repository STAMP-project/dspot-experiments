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
package uk.gov.gchq.gaffer.sketches.datasketches.sampling.binaryoperator;


import com.yahoo.sketches.sampling.ReservoirItemsSketch;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.koryphe.binaryoperator.BinaryOperatorTest;


public class ReservoirItemsSketchAggregatorTest extends BinaryOperatorTest {
    private static final Random RANDOM = new Random();

    private ReservoirItemsSketch<String> union1;

    private ReservoirItemsSketch<String> union2;

    @Test
    public void testAggregate() {
        final ReservoirItemsSketchAggregator<String> sketchAggregator = new ReservoirItemsSketchAggregator();
        ReservoirItemsSketch<String> currentState = union1;
        Assert.assertEquals(3L, currentState.getN());
        Assert.assertEquals(3, currentState.getNumSamples());
        // As less items have been added than the capacity, the sample should exactly match what was added.
        Set<String> samples = new HashSet(Arrays.asList(currentState.getSamples()));
        Set<String> expectedSamples = new HashSet<>();
        expectedSamples.add("1");
        expectedSamples.add("2");
        expectedSamples.add("3");
        Assert.assertEquals(expectedSamples, samples);
        currentState = sketchAggregator.apply(currentState, union2);
        Assert.assertEquals(99L, currentState.getN());
        Assert.assertEquals(20L, currentState.getNumSamples());
        // As more items have been added than the capacity, we can't know exactly what items will be present
        // in the sample but we can check that they are all from the set of things we added.
        samples = new HashSet(Arrays.asList(currentState.getSamples()));
        for (long i = 4L; i < 100; i++) {
            expectedSamples.add(("" + i));
        }
        Assert.assertTrue(expectedSamples.containsAll(samples));
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(new ReservoirItemsSketchAggregator<String>(), new ReservoirItemsSketchAggregator<String>());
    }
}

