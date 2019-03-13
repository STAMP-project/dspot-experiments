/**
 * Copyright 2015, 2017 StreamEx contributors
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
package one.util.streamex;


import java.util.Spliterator;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tagir Valeev
 */
public class PrependSpliteratorTest {
    @Test
    public void testSpliterator() {
        TestHelpers.checkSpliterator("prepend", IntStreamEx.range(100).boxed().toList(), () -> new PrependSpliterator<>(IntStream.range(1, 100).spliterator(), 0));
        Assert.assertTrue(new PrependSpliterator(IntStream.range(1, 100).spliterator(), 0).hasCharacteristics(Spliterator.SIZED));
        Assert.assertTrue(new PrependSpliterator(LongStream.range(0, ((Long.MAX_VALUE) - 2)).spliterator(), 0L).hasCharacteristics(Spliterator.SIZED));
        Assert.assertFalse(new PrependSpliterator(LongStream.range(0, ((Long.MAX_VALUE) - 1)).spliterator(), 0L).hasCharacteristics(Spliterator.SIZED));
        Assert.assertFalse(new PrependSpliterator(LongStream.range(0, Long.MAX_VALUE).spliterator(), 0L).hasCharacteristics(Spliterator.SIZED));
        Assert.assertEquals(((Long.MAX_VALUE) - 1), new PrependSpliterator(LongStream.range(0, ((Long.MAX_VALUE) - 2)).spliterator(), 0L).estimateSize());
        Assert.assertEquals(Long.MAX_VALUE, new PrependSpliterator(LongStream.range(0, ((Long.MAX_VALUE) - 1)).spliterator(), 0L).estimateSize());
        Assert.assertEquals(Long.MAX_VALUE, new PrependSpliterator(LongStream.range(0, Long.MAX_VALUE).spliterator(), 0L).estimateSize());
        PrependSpliterator<Integer> spltr = new PrependSpliterator(IntStream.range(1, 100).spliterator(), 0);
        spltr.tryAdvance(( x) -> assertEquals(0, ((int) (x))));
        Assert.assertTrue(spltr.hasCharacteristics(Spliterator.SORTED));
    }
}

