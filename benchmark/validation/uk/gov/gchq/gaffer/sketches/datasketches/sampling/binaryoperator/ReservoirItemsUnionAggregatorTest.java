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


import com.yahoo.sketches.sampling.ReservoirItemsUnion;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.koryphe.binaryoperator.BinaryOperatorTest;


public class ReservoirItemsUnionAggregatorTest extends BinaryOperatorTest {
    private static final Random RANDOM = new Random();

    private ReservoirItemsUnion<String> union1;

    private ReservoirItemsUnion<String> union2;

    @Test
    public void testAggregate() {
        final ReservoirItemsUnionAggregator<String> unionAggregator = new ReservoirItemsUnionAggregator();
        ReservoirItemsUnion<String> currentState = union1;
        Assert.assertEquals(3L, currentState.getResult().getN());
        Assert.assertEquals(3, currentState.getResult().getNumSamples());
        // As less items have been added than the capacity, the sample should exactly match what was added.
        Set<String> samples = new HashSet(Arrays.asList(currentState.getResult().getSamples()));
        Set<String> expectedSamples = new HashSet<>();
        expectedSamples.add("1");
        expectedSamples.add("2");
        expectedSamples.add("3");
        Assert.assertEquals(expectedSamples, samples);
        currentState = unionAggregator.apply(currentState, union2);
        Assert.assertEquals(99L, currentState.getResult().getN());
        Assert.assertEquals(20L, currentState.getResult().getNumSamples());
        // As more items have been added than the capacity, we can't know exactly what items will be present
        // in the sample but we can check that they are all from the set of things we added.
        samples = new HashSet(Arrays.asList(currentState.getResult().getSamples()));
        for (long i = 4L; i < 100; i++) {
            expectedSamples.add(("" + i));
        }
        Assert.assertTrue(expectedSamples.containsAll(samples));
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(new ReservoirItemsUnionAggregator(), new ReservoirItemsUnionAggregator());
    }
}

