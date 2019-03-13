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
package uk.gov.gchq.gaffer.sketches.clearspring.cardinality.binaryoperator;


import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.koryphe.binaryoperator.BinaryOperatorTest;


public class HyperLogLogPlusAggregatorTest extends BinaryOperatorTest {
    private HyperLogLogPlus hyperLogLogPlus1;

    private HyperLogLogPlus hyperLogLogPlus2;

    @Test
    public void shouldAggregateHyperLogLogPlusWithVariousPAndSpValues() {
        setupHllp(5, 5);
        shouldAggregateHyperLogLogPlus();
        setupHllp(5, 6);
        shouldAggregateHyperLogLogPlus();
        setupHllp(6, 6);
        shouldAggregateHyperLogLogPlus();
    }

    @Test
    public void testClone() {
        Assert.assertEquals(new HyperLogLogPlusAggregator(), new HyperLogLogPlusAggregator());
    }
}

