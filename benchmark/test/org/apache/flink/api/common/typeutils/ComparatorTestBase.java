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
package org.apache.flink.api.common.typeutils;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputView;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Abstract test base for comparators.
 *
 * @param <T>
 * 		
 */
public abstract class ComparatorTestBase<T> extends TestLogger {
    // Same as in the NormalizedKeySorter
    private static final int DEFAULT_MAX_NORMALIZED_KEY_LEN = 8;

    // -------------------------------- test duplication ------------------------------------------
    @Test
    public void testDuplicate() {
        try {
            TypeComparator<T> comparator = getComparator(true);
            TypeComparator<T> clone = comparator.duplicate();
            T[] data = getSortedData();
            comparator.setReference(data[0]);
            clone.setReference(data[1]);
            Assert.assertTrue("Comparator duplication does not work: Altering the reference in a duplicated comparator alters the original comparator's reference.", ((comparator.equalToReference(data[0])) && (clone.equalToReference(data[1]))));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    // --------------------------------- equality tests -------------------------------------------
    @Test
    public void testEquality() {
        testEquals(true);
        testEquals(false);
    }

    @Test
    public void testEqualityWithReference() {
        try {
            TypeSerializer<T> serializer = createSerializer();
            TypeComparator<T> comparator = getComparator(true);
            TypeComparator<T> comparator2 = getComparator(true);
            T[] data = getSortedData();
            for (T d : data) {
                comparator.setReference(d);
                // Make a copy to compare
                T copy = serializer.copy(d, serializer.createInstance());
                // And then test equalTo and compareToReference method of comparator
                Assert.assertTrue(comparator.equalToReference(d));
                comparator2.setReference(copy);
                Assert.assertTrue(((comparator.compareToReference(comparator2)) == 0));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Exception in test: " + (e.getMessage())));
        }
    }

    // --------------------------------- inequality tests ----------------------------------------
    @Test
    public void testInequality() {
        testGreatSmallAscDesc(true, true);
        testGreatSmallAscDesc(false, true);
        testGreatSmallAscDesc(true, false);
        testGreatSmallAscDesc(false, false);
    }

    @Test
    public void testInequalityWithReference() {
        testGreatSmallAscDescWithReference(true, true);
        testGreatSmallAscDescWithReference(true, false);
        testGreatSmallAscDescWithReference(false, true);
        testGreatSmallAscDescWithReference(false, false);
    }

    @Test
    public void testNormalizedKeysEqualsFullLength() {
        // Ascending or descending does not matter in this case
        TypeComparator<T> comparator = getComparator(true);
        if (!(comparator.supportsNormalizedKey())) {
            return;
        }
        testNormalizedKeysEquals(false);
    }

    @Test
    public void testNormalizedKeysEqualsHalfLength() {
        TypeComparator<T> comparator = getComparator(true);
        if (!(comparator.supportsNormalizedKey())) {
            return;
        }
        testNormalizedKeysEquals(true);
    }

    @Test
    public void testNormalizedKeysGreatSmallFullLength() {
        // ascending/descending in comparator doesn't matter for normalized keys
        TypeComparator<T> comparator = getComparator(true);
        if (!(comparator.supportsNormalizedKey())) {
            return;
        }
        testNormalizedKeysGreatSmall(true, comparator, false);
        testNormalizedKeysGreatSmall(false, comparator, false);
    }

    @Test
    public void testNormalizedKeysGreatSmallAscDescHalfLength() {
        // ascending/descending in comparator doesn't matter for normalized keys
        TypeComparator<T> comparator = getComparator(true);
        if (!(comparator.supportsNormalizedKey())) {
            return;
        }
        testNormalizedKeysGreatSmall(true, comparator, true);
        testNormalizedKeysGreatSmall(false, comparator, true);
    }

    @Test
    public void testNormalizedKeyReadWriter() {
        try {
            T[] data = getSortedData();
            T reuse = getSortedData()[0];
            TypeComparator<T> comp1 = getComparator(true);
            if (!(comp1.supportsSerializationWithKeyNormalization())) {
                return;
            }
            TypeComparator<T> comp2 = comp1.duplicate();
            comp2.setReference(reuse);
            ComparatorTestBase.TestOutputView out = new ComparatorTestBase.TestOutputView();
            ComparatorTestBase.TestInputView in;
            for (T value : data) {
                comp1.setReference(value);
                comp1.writeWithKeyNormalization(value, out);
                in = out.getInputView();
                comp1.readWithKeyDenormalization(reuse, in);
                Assert.assertTrue(((comp1.compareToReference(comp2)) == 0));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Exception in test: " + (e.getMessage())));
        }
    }

    // -------------------------------- Key extraction tests --------------------------------------
    @Test
    @SuppressWarnings("unchecked")
    public void testKeyExtraction() {
        TypeComparator<T> comparator = getComparator(true);
        T[] data = getSortedData();
        for (T value : data) {
            TypeComparator[] comparators = comparator.getFlatComparators();
            Object[] extractedKeys = new Object[comparators.length];
            int insertedKeys = comparator.extractKeys(value, extractedKeys, 0);
            Assert.assertTrue((insertedKeys == (comparators.length)));
            for (int i = 0; i < insertedKeys; i++) {
                // check if some keys are null, although this is not supported
                if (!(supportsNullKeys())) {
                    Assert.assertNotNull(extractedKeys[i]);
                }
                // compare the extracted key with itself as a basic check
                // if the extracted key corresponds to the comparator
                Assert.assertTrue(((comparators[i].compare(extractedKeys[i], extractedKeys[i])) == 0));
            }
        }
    }

    // --------------------------------------------------------------------------------------------
    public static final class TestOutputView extends DataOutputStream implements DataOutputView {
        public TestOutputView() {
            super(new ByteArrayOutputStream(4096));
        }

        public ComparatorTestBase.TestInputView getInputView() {
            ByteArrayOutputStream baos = ((ByteArrayOutputStream) (out));
            return new ComparatorTestBase.TestInputView(baos.toByteArray());
        }

        @Override
        public void skipBytesToWrite(int numBytes) throws IOException {
            for (int i = 0; i < numBytes; i++) {
                write(0);
            }
        }

        @Override
        public void write(DataInputView source, int numBytes) throws IOException {
            byte[] buffer = new byte[numBytes];
            source.readFully(buffer);
            write(buffer);
        }
    }

    public static final class TestInputView extends DataInputStream implements DataInputView {
        public TestInputView(byte[] data) {
            super(new ByteArrayInputStream(data));
        }

        @Override
        public void skipBytesToRead(int numBytes) throws IOException {
            while (numBytes > 0) {
                int skipped = skipBytes(numBytes);
                numBytes -= skipped;
            } 
        }
    }
}

