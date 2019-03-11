/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.state;


import java.io.IOException;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import org.apache.flink.core.memory.ByteArrayOutputStreamWithPos;
import org.apache.flink.core.memory.DataOutputView;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Abstract test base for implementations of {@link KeyGroupPartitioner}.
 */
public abstract class KeyGroupPartitionerTestBase<T> extends TestLogger {
    private static final DataOutputView DUMMY_OUT_VIEW = new org.apache.flink.core.memory.DataOutputViewStreamWrapper(new ByteArrayOutputStreamWithPos(0));

    @Nonnull
    protected final KeyExtractorFunction<T> keyExtractorFunction;

    @Nonnull
    protected final Function<Random, T> elementGenerator;

    protected KeyGroupPartitionerTestBase(@Nonnull
    Function<Random, T> elementGenerator, @Nonnull
    KeyExtractorFunction<T> keyExtractorFunction) {
        this.elementGenerator = elementGenerator;
        this.keyExtractorFunction = keyExtractorFunction;
    }

    @Test
    public void testPartitionByKeyGroup() throws IOException {
        final Random random = new Random(66);
        testPartitionByKeyGroupForSize(0, random);
        testPartitionByKeyGroupForSize(1, random);
        testPartitionByKeyGroupForSize(2, random);
        testPartitionByKeyGroupForSize(10, random);
    }

    /**
     * Simple test implementation with validation .
     */
    static final class ValidatingElementWriterDummy<T> implements KeyGroupPartitioner.ElementWriterFunction<T> {
        @Nonnull
        private final KeyExtractorFunction<T> keyExtractorFunction;

        @Nonnegative
        private final int numberOfKeyGroups;

        @Nonnull
        private final Set<T> allElementsSet;

        @Nonnegative
        private int currentKeyGroup;

        ValidatingElementWriterDummy(@Nonnull
        KeyExtractorFunction<T> keyExtractorFunction, @Nonnegative
        int numberOfKeyGroups, @Nonnull
        Set<T> allElementsSet) {
            this.keyExtractorFunction = keyExtractorFunction;
            this.numberOfKeyGroups = numberOfKeyGroups;
            this.allElementsSet = allElementsSet;
        }

        @Override
        public void writeElement(@Nonnull
        T element, @Nonnull
        DataOutputView dov) {
            Assert.assertTrue(allElementsSet.remove(element));
            Assert.assertEquals(currentKeyGroup, KeyGroupRangeAssignment.assignToKeyGroup(keyExtractorFunction.extractKeyFromElement(element), numberOfKeyGroups));
        }

        void validateAllElementsSeen() {
            Assert.assertTrue(allElementsSet.isEmpty());
        }

        void setCurrentKeyGroup(int currentKeyGroup) {
            this.currentKeyGroup = currentKeyGroup;
        }
    }
}

