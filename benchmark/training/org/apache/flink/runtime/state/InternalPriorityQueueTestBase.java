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
package org.apache.flink.runtime.state;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.TypeSerializerSchemaCompatibility;
import org.apache.flink.api.common.typeutils.TypeSerializerSnapshot;
import org.apache.flink.core.memory.ByteArrayOutputStreamWithPos;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputView;
import org.apache.flink.core.memory.DataOutputViewStreamWrapper;
import org.apache.flink.runtime.state.heap.HeapPriorityQueueElement;
import org.apache.flink.util.CloseableIterator;
import org.apache.flink.util.MathUtils;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Testbase for implementations of {@link InternalPriorityQueue}.
 */
public abstract class InternalPriorityQueueTestBase extends TestLogger {
    protected static final KeyGroupRange KEY_GROUP_RANGE = new KeyGroupRange(0, 2);

    protected static final KeyExtractorFunction<InternalPriorityQueueTestBase.TestElement> KEY_EXTRACTOR_FUNCTION = InternalPriorityQueueTestBase.TestElement::getKey;

    protected static final PriorityComparator<InternalPriorityQueueTestBase.TestElement> TEST_ELEMENT_PRIORITY_COMPARATOR = ( left, right) -> Long.compare(left.getPriority(), right.getPriority());

    protected static final Comparator<InternalPriorityQueueTestBase.TestElement> TEST_ELEMENT_COMPARATOR = new InternalPriorityQueueTestBase.TestElementComparator();

    @Test
    public void testPeekPollOrder() {
        final int initialCapacity = 4;
        final int testSize = 1000;
        final Comparator<Long> comparator = getTestElementPriorityComparator();
        InternalPriorityQueue<InternalPriorityQueueTestBase.TestElement> priorityQueue = newPriorityQueue(initialCapacity);
        HashSet<InternalPriorityQueueTestBase.TestElement> checkSet = new HashSet<>(testSize);
        InternalPriorityQueueTestBase.insertRandomElements(priorityQueue, checkSet, testSize);
        long lastPriorityValue = getHighestPriorityValueForComparator();
        int lastSize = priorityQueue.size();
        Assert.assertEquals(testSize, lastSize);
        InternalPriorityQueueTestBase.TestElement testElement;
        while ((testElement = priorityQueue.peek()) != null) {
            Assert.assertFalse(priorityQueue.isEmpty());
            Assert.assertEquals(lastSize, priorityQueue.size());
            Assert.assertEquals(testElement, priorityQueue.poll());
            Assert.assertTrue(checkSet.remove(testElement));
            Assert.assertTrue(((comparator.compare(testElement.getPriority(), lastPriorityValue)) >= 0));
            lastPriorityValue = testElement.getPriority();
            --lastSize;
        } 
        Assert.assertTrue(priorityQueue.isEmpty());
        Assert.assertEquals(0, priorityQueue.size());
        Assert.assertEquals(0, checkSet.size());
    }

    @Test
    public void testRemoveInsertMixKeepsOrder() {
        InternalPriorityQueue<InternalPriorityQueueTestBase.TestElement> priorityQueue = newPriorityQueue(3);
        final Comparator<Long> comparator = getTestElementPriorityComparator();
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        final int testSize = 300;
        final int addCounterMax = testSize / 4;
        int iterationsTillNextAdds = random.nextInt(addCounterMax);
        HashSet<InternalPriorityQueueTestBase.TestElement> checkSet = new HashSet<>(testSize);
        InternalPriorityQueueTestBase.insertRandomElements(priorityQueue, checkSet, testSize);
        // check that the whole set is still in order
        while (!(checkSet.isEmpty())) {
            final long highestPrioValue = getHighestPriorityValueForComparator();
            Iterator<InternalPriorityQueueTestBase.TestElement> iterator = checkSet.iterator();
            InternalPriorityQueueTestBase.TestElement element = iterator.next();
            iterator.remove();
            final boolean removesHead = element.equals(priorityQueue.peek());
            if (removesHead) {
                Assert.assertTrue(priorityQueue.remove(element));
            } else {
                priorityQueue.remove(element);
            }
            long currentPriorityWatermark;
            // test some bulk polling from time to time
            if (removesHead) {
                currentPriorityWatermark = element.getPriority();
            } else {
                currentPriorityWatermark = highestPrioValue;
            }
            while ((element = priorityQueue.poll()) != null) {
                Assert.assertTrue(((comparator.compare(element.getPriority(), currentPriorityWatermark)) >= 0));
                currentPriorityWatermark = element.getPriority();
                if ((--iterationsTillNextAdds) == 0) {
                    // some random adds
                    iterationsTillNextAdds = random.nextInt(addCounterMax);
                    InternalPriorityQueueTestBase.insertRandomElements(priorityQueue, new HashSet(checkSet), (1 + (random.nextInt(3))));
                    currentPriorityWatermark = priorityQueue.peek().getPriority();
                }
            } 
            Assert.assertTrue(priorityQueue.isEmpty());
            priorityQueue.addAll(checkSet);
        } 
    }

    @Test
    public void testPoll() {
        InternalPriorityQueue<InternalPriorityQueueTestBase.TestElement> priorityQueue = newPriorityQueue(3);
        final Comparator<Long> comparator = getTestElementPriorityComparator();
        Assert.assertNull(priorityQueue.poll());
        final int testSize = 345;
        HashSet<InternalPriorityQueueTestBase.TestElement> checkSet = new HashSet<>(testSize);
        InternalPriorityQueueTestBase.insertRandomElements(priorityQueue, checkSet, testSize);
        long lastPriorityValue = getHighestPriorityValueForComparator();
        while (!(priorityQueue.isEmpty())) {
            InternalPriorityQueueTestBase.TestElement removed = priorityQueue.poll();
            Assert.assertNotNull(removed);
            Assert.assertTrue(checkSet.remove(removed));
            Assert.assertTrue(((comparator.compare(removed.getPriority(), lastPriorityValue)) >= 0));
            lastPriorityValue = removed.getPriority();
        } 
        Assert.assertTrue(checkSet.isEmpty());
        Assert.assertNull(priorityQueue.poll());
    }

    @Test
    public void testIsEmpty() {
        InternalPriorityQueue<InternalPriorityQueueTestBase.TestElement> priorityQueue = newPriorityQueue(1);
        Assert.assertTrue(priorityQueue.isEmpty());
        Assert.assertTrue(priorityQueue.add(new InternalPriorityQueueTestBase.TestElement(4711L, 42L)));
        Assert.assertFalse(priorityQueue.isEmpty());
        priorityQueue.poll();
        Assert.assertTrue(priorityQueue.isEmpty());
    }

    @Test
    public void testBulkAddRestoredElements() throws Exception {
        final int testSize = 10;
        HashSet<InternalPriorityQueueTestBase.TestElement> elementSet = new HashSet<>(testSize);
        for (int i = 0; i < testSize; ++i) {
            elementSet.add(new InternalPriorityQueueTestBase.TestElement(i, i));
        }
        List<InternalPriorityQueueTestBase.TestElement> twoTimesElementSet = new ArrayList<>(((elementSet.size()) * 2));
        for (InternalPriorityQueueTestBase.TestElement testElement : elementSet) {
            twoTimesElementSet.add(testElement.deepCopy());
            twoTimesElementSet.add(testElement.deepCopy());
        }
        InternalPriorityQueue<InternalPriorityQueueTestBase.TestElement> priorityQueue = newPriorityQueue(1);
        priorityQueue.addAll(twoTimesElementSet);
        priorityQueue.addAll(elementSet);
        final int expectedSize = (testSetSemanticsAgainstDuplicateElements()) ? elementSet.size() : 3 * (elementSet.size());
        Assert.assertEquals(expectedSize, priorityQueue.size());
        try (final CloseableIterator<InternalPriorityQueueTestBase.TestElement> iterator = priorityQueue.iterator()) {
            while (iterator.hasNext()) {
                if (testSetSemanticsAgainstDuplicateElements()) {
                    Assert.assertTrue(elementSet.remove(iterator.next()));
                } else {
                    Assert.assertTrue(elementSet.contains(iterator.next()));
                }
            } 
        }
        if (testSetSemanticsAgainstDuplicateElements()) {
            Assert.assertTrue(elementSet.isEmpty());
        }
    }

    @Test
    public void testIterator() throws Exception {
        InternalPriorityQueue<InternalPriorityQueueTestBase.TestElement> priorityQueue = newPriorityQueue(1);
        // test empty iterator
        try (CloseableIterator<InternalPriorityQueueTestBase.TestElement> iterator = priorityQueue.iterator()) {
            Assert.assertFalse(iterator.hasNext());
            try {
                iterator.next();
                Assert.fail();
            } catch (NoSuchElementException ignore) {
            }
        }
        // iterate some data
        final int testSize = 10;
        HashSet<InternalPriorityQueueTestBase.TestElement> checkSet = new HashSet<>(testSize);
        InternalPriorityQueueTestBase.insertRandomElements(priorityQueue, checkSet, testSize);
        try (CloseableIterator<InternalPriorityQueueTestBase.TestElement> iterator = priorityQueue.iterator()) {
            while (iterator.hasNext()) {
                Assert.assertTrue(checkSet.remove(iterator.next()));
            } 
            Assert.assertTrue(checkSet.isEmpty());
        }
    }

    @Test
    public void testAdd() {
        InternalPriorityQueue<InternalPriorityQueueTestBase.TestElement> priorityQueue = newPriorityQueue(1);
        final List<InternalPriorityQueueTestBase.TestElement> testElements = Arrays.asList(new InternalPriorityQueueTestBase.TestElement(4711L, 42L), new InternalPriorityQueueTestBase.TestElement(815L, 23L));
        testElements.sort(( l, r) -> getTestElementPriorityComparator().compare(r.priority, l.priority));
        Assert.assertTrue(priorityQueue.add(testElements.get(0)));
        if (testSetSemanticsAgainstDuplicateElements()) {
            priorityQueue.add(testElements.get(0).deepCopy());
        }
        Assert.assertEquals(1, priorityQueue.size());
        Assert.assertTrue(priorityQueue.add(testElements.get(1)));
        Assert.assertEquals(2, priorityQueue.size());
        Assert.assertEquals(testElements.get(1), priorityQueue.poll());
        Assert.assertEquals(1, priorityQueue.size());
        Assert.assertEquals(testElements.get(0), priorityQueue.poll());
        Assert.assertEquals(0, priorityQueue.size());
    }

    @Test
    public void testRemove() {
        InternalPriorityQueue<InternalPriorityQueueTestBase.TestElement> priorityQueue = newPriorityQueue(1);
        final long key = 4711L;
        final long priorityValue = 42L;
        final InternalPriorityQueueTestBase.TestElement testElement = new InternalPriorityQueueTestBase.TestElement(key, priorityValue);
        if (testSetSemanticsAgainstDuplicateElements()) {
            Assert.assertFalse(priorityQueue.remove(testElement));
        }
        Assert.assertTrue(priorityQueue.add(testElement));
        Assert.assertTrue(priorityQueue.remove(testElement));
        if (testSetSemanticsAgainstDuplicateElements()) {
            Assert.assertFalse(priorityQueue.remove(testElement));
        }
        Assert.assertTrue(priorityQueue.isEmpty());
    }

    /**
     * Payload for usage in the test.
     */
    protected static class TestElement implements Keyed<Long> , PriorityComparable<InternalPriorityQueueTestBase.TestElement> , HeapPriorityQueueElement {
        private final long key;

        private final long priority;

        private int internalIndex;

        public TestElement(long key, long priority) {
            this.key = key;
            this.priority = priority;
            this.internalIndex = NOT_CONTAINED;
        }

        @Override
        public int comparePriorityTo(@Nonnull
        InternalPriorityQueueTestBase.TestElement other) {
            return Long.compare(priority, other.priority);
        }

        public Long getKey() {
            return key;
        }

        public long getPriority() {
            return priority;
        }

        @Override
        public int getInternalIndex() {
            return internalIndex;
        }

        @Override
        public void setInternalIndex(int newIndex) {
            internalIndex = newIndex;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            InternalPriorityQueueTestBase.TestElement that = ((InternalPriorityQueueTestBase.TestElement) (o));
            return ((key) == (that.key)) && ((priority) == (that.priority));
        }

        @Override
        public int hashCode() {
            return Objects.hash(getKey(), getPriority());
        }

        public InternalPriorityQueueTestBase.TestElement deepCopy() {
            return new InternalPriorityQueueTestBase.TestElement(key, priority);
        }

        @Override
        public String toString() {
            return (((("TestElement{" + "key=") + (key)) + ", priority=") + (priority)) + '}';
        }
    }

    /**
     * Serializer for {@link TestElement}. The serialization format produced by this serializer allows lexicographic
     * ordering by {@link TestElement#getPriority}.
     */
    protected static class TestElementSerializer extends TypeSerializer<InternalPriorityQueueTestBase.TestElement> {
        private static final int REVISION = 1;

        public static final InternalPriorityQueueTestBase.TestElementSerializer INSTANCE = new InternalPriorityQueueTestBase.TestElementSerializer();

        protected TestElementSerializer() {
        }

        @Override
        public boolean isImmutableType() {
            return true;
        }

        @Override
        public TypeSerializer<InternalPriorityQueueTestBase.TestElement> duplicate() {
            return this;
        }

        @Override
        public InternalPriorityQueueTestBase.TestElement createInstance() {
            throw new UnsupportedOperationException();
        }

        @Override
        public InternalPriorityQueueTestBase.TestElement copy(InternalPriorityQueueTestBase.TestElement from) {
            return new InternalPriorityQueueTestBase.TestElement(from.key, from.priority);
        }

        @Override
        public InternalPriorityQueueTestBase.TestElement copy(InternalPriorityQueueTestBase.TestElement from, InternalPriorityQueueTestBase.TestElement reuse) {
            return copy(from);
        }

        @Override
        public int getLength() {
            return 2 * (Long.BYTES);
        }

        @Override
        public void serialize(InternalPriorityQueueTestBase.TestElement record, DataOutputView target) throws IOException {
            // serialize priority first, so that we have correct order in RocksDB. We flip the sign bit for correct
            // lexicographic order.
            target.writeLong(MathUtils.flipSignBit(record.getPriority()));
            target.writeLong(record.getKey());
        }

        @Override
        public InternalPriorityQueueTestBase.TestElement deserialize(DataInputView source) throws IOException {
            long prio = MathUtils.flipSignBit(source.readLong());
            long key = source.readLong();
            return new InternalPriorityQueueTestBase.TestElement(key, prio);
        }

        @Override
        public InternalPriorityQueueTestBase.TestElement deserialize(InternalPriorityQueueTestBase.TestElement reuse, DataInputView source) throws IOException {
            return deserialize(source);
        }

        @Override
        public void copy(DataInputView source, DataOutputView target) throws IOException {
            serialize(deserialize(source), target);
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }

        @Override
        public int hashCode() {
            return 4711;
        }

        protected int getRevision() {
            return InternalPriorityQueueTestBase.TestElementSerializer.REVISION;
        }

        @Override
        public InternalPriorityQueueTestBase.TestElementSerializer.Snapshot snapshotConfiguration() {
            return new InternalPriorityQueueTestBase.TestElementSerializer.Snapshot(getRevision());
        }

        public static class Snapshot implements TypeSerializerSnapshot<InternalPriorityQueueTestBase.TestElement> {
            private int revision;

            public Snapshot() {
            }

            public Snapshot(int revision) {
                this.revision = revision;
            }

            @Override
            public boolean equals(Object obj) {
                return (obj instanceof InternalPriorityQueueTestBase.TestElementSerializer.Snapshot) && ((revision) == (((InternalPriorityQueueTestBase.TestElementSerializer.Snapshot) (obj)).revision));
            }

            @Override
            public int hashCode() {
                return revision;
            }

            @Override
            public int getCurrentVersion() {
                return 0;
            }

            public int getRevision() {
                return revision;
            }

            @Override
            public void writeSnapshot(DataOutputView out) throws IOException {
                out.writeInt(revision);
            }

            @Override
            public void readSnapshot(int readVersion, DataInputView in, ClassLoader userCodeClassLoader) throws IOException {
                this.revision = in.readInt();
            }

            @Override
            public TypeSerializer<InternalPriorityQueueTestBase.TestElement> restoreSerializer() {
                return new InternalPriorityQueueTestBase.TestElementSerializer();
            }

            @Override
            public TypeSerializerSchemaCompatibility<InternalPriorityQueueTestBase.TestElement> resolveSchemaCompatibility(TypeSerializer<InternalPriorityQueueTestBase.TestElement> newSerializer) {
                if (!(newSerializer instanceof InternalPriorityQueueTestBase.TestElementSerializer)) {
                    return TypeSerializerSchemaCompatibility.incompatible();
                }
                InternalPriorityQueueTestBase.TestElementSerializer testElementSerializer = ((InternalPriorityQueueTestBase.TestElementSerializer) (newSerializer));
                return (revision) <= (testElementSerializer.getRevision()) ? TypeSerializerSchemaCompatibility.compatibleAsIs() : TypeSerializerSchemaCompatibility.incompatible();
            }
        }
    }

    /**
     * Comparator for test elements, operating on the serialized bytes of the elements.
     */
    protected static class TestElementComparator implements Comparator<InternalPriorityQueueTestBase.TestElement> {
        @Override
        public int compare(InternalPriorityQueueTestBase.TestElement o1, InternalPriorityQueueTestBase.TestElement o2) {
            ByteArrayOutputStreamWithPos os = new ByteArrayOutputStreamWithPos();
            DataOutputViewStreamWrapper ow = new DataOutputViewStreamWrapper(os);
            try {
                InternalPriorityQueueTestBase.TestElementSerializer.INSTANCE.serialize(o1, ow);
                byte[] a1 = os.toByteArray();
                os.reset();
                InternalPriorityQueueTestBase.TestElementSerializer.INSTANCE.serialize(o2, ow);
                byte[] a2 = os.toByteArray();
                return org.apache.flink.shaded.guava18.com.google.common.primitives.UnsignedBytes.lexicographicalComparator().compare(a1, a2);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

