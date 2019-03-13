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


import java.util.Arrays;
import java.util.Random;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tagir Valeev
 */
public class CharSpliteratorTest {
    @Test
    public void testBasics() {
        CharSpliterator spliterator = new CharSpliterator("abcd,efgh", ',', false);
        Assert.assertTrue(spliterator.hasCharacteristics(Spliterator.ORDERED));
        Assert.assertTrue(spliterator.hasCharacteristics(Spliterator.NONNULL));
        Assert.assertFalse(spliterator.hasCharacteristics(Spliterator.SORTED));
        Assert.assertFalse(spliterator.hasCharacteristics(Spliterator.SIZED));
        Assert.assertEquals(9, spliterator.estimateSize());
        Assert.assertTrue(spliterator.tryAdvance(( a) -> {
        }));
        Assert.assertEquals(4, spliterator.estimateSize());
        Assert.assertTrue(spliterator.tryAdvance(( a) -> {
        }));
        Assert.assertEquals(0, spliterator.estimateSize());
    }

    @Test
    public void testSpliterator() {
        // Empty string is processed differently by CharSpliterator, but this is
        // fixed in StreamEx.split
        TestHelpers.checkSpliterator("split", Arrays.asList(), () -> new CharSpliterator("", ',', true));
        TestHelpers.checkSpliterator("split", Arrays.asList(""), () -> new CharSpliterator("", ',', false));
        TestHelpers.withRandom(( r) -> {
            String[] inputs = new String[]{ ",", "abcd,e,f,gh,,,i,j,kl,,,,,,", ",", "abcdasdfgsdfgsdfgsdfgsdfgsdgdfsgs", "abcdasdfgsdfgsdfgsdfgsdfgsdgdfsgs,", "abcdasdfgs,dfgsdfgsdfgsdfgsdgdfsgs", "abcd,e,f,gh,,,i,j,kl,,,,,,x", "abcd,e,f,gh,,,i,j,kl,,,,,,x,", IntStreamEx.of(r, 0, 3).limit(((r.nextInt(1000)) + 1)).elements(new int[]{ ',', 'a', 'b' }).charsToString() };
            for (String input : inputs) {
                TestHelpers.checkSpliterator(input, Arrays.asList(input.split(",")), () -> new CharSpliterator(input, ',', true));
                TestHelpers.checkSpliterator(input, Arrays.asList(input.split(",", (-1))), () -> new CharSpliterator(input, ',', false));
            }
        });
    }
}

