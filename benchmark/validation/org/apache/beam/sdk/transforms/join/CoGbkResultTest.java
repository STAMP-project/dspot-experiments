/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.transforms.join;


import org.apache.beam.sdk.util.common.Reiterable;
import org.apache.beam.sdk.util.common.Reiterator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests the CoGbkResult.
 */
@RunWith(JUnit4.class)
public class CoGbkResultTest {
    @Test
    public void testLazyResults() {
        runLazyResult(0);
        runLazyResult(1);
        runLazyResult(3);
        runLazyResult(10);
    }

    private static class TestUnionValues implements Reiterable<RawUnionValue> {
        final int[] tags;

        int maxPos = 0;

        /**
         * This will create a list of RawUnionValues whose tags are as given and values are increasing
         * starting at 0 (i.e. the index in the constructor).
         */
        public TestUnionValues(int... tags) {
            this.tags = tags;
        }

        /**
         * Returns the highest position iterated to so far, useful for ensuring laziness.
         */
        public int maxPos() {
            return maxPos;
        }

        @Override
        public Reiterator<RawUnionValue> iterator() {
            return iterator(0);
        }

        public Reiterator<RawUnionValue> iterator(final int start) {
            return new Reiterator<RawUnionValue>() {
                int pos = start;

                @Override
                public boolean hasNext() {
                    return (pos) < (tags.length);
                }

                @Override
                public RawUnionValue next() {
                    maxPos = Math.max(((pos) + 1), maxPos);
                    return new RawUnionValue(tags[pos], ((pos)++));
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Reiterator<RawUnionValue> copy() {
                    return iterator(pos);
                }
            };
        }
    }
}

