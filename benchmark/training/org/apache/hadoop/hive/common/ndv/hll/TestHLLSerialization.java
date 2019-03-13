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
package org.apache.hadoop.hive.common.ndv.hll;


import EncodingType.DENSE;
import EncodingType.SPARSE;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.apache.hadoop.hive.metastore.annotation.MetastoreUnitTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Category(MetastoreUnitTest.class)
public class TestHLLSerialization {
    private int size;

    private File testFile;

    private static final String pathPrefix = ".";

    private static final int SEED = 100;

    // 5% tolerance for long range bias and 2.5% for short range bias
    private float longRangeTolerance = 5.0F;

    private float shortRangeTolerance = 2.5F;

    public TestHLLSerialization(int n) {
        this.size = n;
        this.testFile = new File((((((TestHLLSerialization.pathPrefix) + (testCaseName.getMethodName())) + "_") + (size)) + ".hll"));
    }

    @Rule
    public TestName testCaseName = new TestName();

    @Test
    public void testHLLSparseSerialization() throws IOException {
        HyperLogLog hll = HyperLogLog.builder().setEncoding(SPARSE).build();
        Random rand = new Random(TestHLLSerialization.SEED);
        for (int i = 0; i < (size); i++) {
            hll.addLong(rand.nextLong());
        }
        FileOutputStream fos = new FileOutputStream(testFile);
        DataOutputStream out = new DataOutputStream(fos);
        HyperLogLogUtils.serializeHLL(out, hll);
        FileInputStream fis = new FileInputStream(testFile);
        DataInputStream in = new DataInputStream(fis);
        HyperLogLog deserializedHLL = HyperLogLogUtils.deserializeHLL(in);
        Assert.assertEquals(hll, deserializedHLL);
        Assert.assertEquals(hll.toString(), deserializedHLL.toString());
        Assert.assertEquals(hll.toStringExtended(), deserializedHLL.toStringExtended());
        Assert.assertEquals(hll.hashCode(), deserializedHLL.hashCode());
        Assert.assertEquals(hll.count(), deserializedHLL.count());
    }

    @Test
    public void testHLLSparseSerializationHalfDistinct() throws IOException {
        HyperLogLog hll = HyperLogLog.builder().setEncoding(SPARSE).build();
        Random rand = new Random(TestHLLSerialization.SEED);
        Set<Integer> hashset = new HashSet<>();
        for (int i = 0; i < (size); i++) {
            int val = rand.nextInt(((size) / 2));
            hll.addLong(val);
            hashset.add(val);
        }
        FileOutputStream fos = new FileOutputStream(testFile);
        DataOutputStream out = new DataOutputStream(fos);
        HyperLogLogUtils.serializeHLL(out, hll);
        double threshold = ((size) > 40000) ? longRangeTolerance : shortRangeTolerance;
        double delta = (threshold * (hashset.size())) / 100;
        FileInputStream fis = new FileInputStream(testFile);
        DataInputStream in = new DataInputStream(fis);
        HyperLogLog deserializedHLL = HyperLogLogUtils.deserializeHLL(in);
        Assert.assertEquals(hll, deserializedHLL);
        Assert.assertEquals(hll.toString(), deserializedHLL.toString());
        Assert.assertEquals(hll.toStringExtended(), deserializedHLL.toStringExtended());
        Assert.assertEquals(hll.hashCode(), deserializedHLL.hashCode());
        Assert.assertEquals(hll.count(), deserializedHLL.count());
        Assert.assertEquals(hashset.size(), hll.count(), delta);
        Assert.assertEquals(hashset.size(), deserializedHLL.count(), delta);
    }

    @Test
    public void testHLLSparseNoBitPacking() throws IOException {
        HyperLogLog hll = HyperLogLog.builder().setEncoding(SPARSE).enableBitPacking(false).build();
        Random rand = new Random(TestHLLSerialization.SEED);
        for (int i = 0; i < (size); i++) {
            hll.addLong(rand.nextLong());
        }
        FileOutputStream fos = new FileOutputStream(testFile);
        DataOutputStream out = new DataOutputStream(fos);
        HyperLogLogUtils.serializeHLL(out, hll);
        FileInputStream fis = new FileInputStream(testFile);
        DataInputStream in = new DataInputStream(fis);
        HyperLogLog deserializedHLL = HyperLogLogUtils.deserializeHLL(in);
        Assert.assertEquals(hll, deserializedHLL);
        Assert.assertEquals(hll.toString(), deserializedHLL.toString());
        Assert.assertEquals(hll.toStringExtended(), deserializedHLL.toStringExtended());
        Assert.assertEquals(hll.hashCode(), deserializedHLL.hashCode());
        Assert.assertEquals(hll.count(), deserializedHLL.count());
    }

    @Test
    public void testHLLSparseNoBitPackingHalfDistinct() throws IOException {
        HyperLogLog hll = HyperLogLog.builder().setEncoding(SPARSE).enableBitPacking(false).build();
        Random rand = new Random(TestHLLSerialization.SEED);
        Set<Integer> hashset = new HashSet<>();
        for (int i = 0; i < (size); i++) {
            int val = rand.nextInt(((size) / 2));
            hll.addLong(val);
            hashset.add(val);
        }
        FileOutputStream fos = new FileOutputStream(testFile);
        DataOutputStream out = new DataOutputStream(fos);
        HyperLogLogUtils.serializeHLL(out, hll);
        double threshold = ((size) > 40000) ? longRangeTolerance : shortRangeTolerance;
        double delta = (threshold * (hashset.size())) / 100;
        FileInputStream fis = new FileInputStream(testFile);
        DataInputStream in = new DataInputStream(fis);
        HyperLogLog deserializedHLL = HyperLogLogUtils.deserializeHLL(in);
        Assert.assertEquals(hll, deserializedHLL);
        Assert.assertEquals(hll.toString(), deserializedHLL.toString());
        Assert.assertEquals(hll.toStringExtended(), deserializedHLL.toStringExtended());
        Assert.assertEquals(hll.hashCode(), deserializedHLL.hashCode());
        Assert.assertEquals(hll.count(), deserializedHLL.count());
        Assert.assertEquals(hashset.size(), hll.count(), delta);
        Assert.assertEquals(hashset.size(), deserializedHLL.count(), delta);
    }

    @Test
    public void testHLLDenseSerialization() throws IOException {
        HyperLogLog hll = HyperLogLog.builder().setEncoding(DENSE).build();
        Random rand = new Random(TestHLLSerialization.SEED);
        for (int i = 0; i < (size); i++) {
            hll.addLong(rand.nextLong());
        }
        FileOutputStream fos = new FileOutputStream(testFile);
        DataOutputStream out = new DataOutputStream(fos);
        HyperLogLogUtils.serializeHLL(out, hll);
        FileInputStream fis = new FileInputStream(testFile);
        DataInputStream in = new DataInputStream(fis);
        HyperLogLog deserializedHLL = HyperLogLogUtils.deserializeHLL(in);
        Assert.assertEquals(hll, deserializedHLL);
        Assert.assertEquals(hll.toString(), deserializedHLL.toString());
        Assert.assertEquals(hll.toStringExtended(), deserializedHLL.toStringExtended());
        Assert.assertEquals(hll.hashCode(), deserializedHLL.hashCode());
        Assert.assertEquals(hll.count(), deserializedHLL.count());
    }

    @Test
    public void testHLLDenseSerializationHalfDistinct() throws IOException {
        HyperLogLog hll = HyperLogLog.builder().setEncoding(DENSE).build();
        Random rand = new Random(TestHLLSerialization.SEED);
        Set<Integer> hashset = new HashSet<>();
        for (int i = 0; i < (size); i++) {
            int val = rand.nextInt(((size) / 2));
            hll.addLong(val);
            hashset.add(val);
        }
        FileOutputStream fos = new FileOutputStream(testFile);
        DataOutputStream out = new DataOutputStream(fos);
        HyperLogLogUtils.serializeHLL(out, hll);
        double threshold = ((size) > 40000) ? longRangeTolerance : shortRangeTolerance;
        double delta = (threshold * (hashset.size())) / 100;
        FileInputStream fis = new FileInputStream(testFile);
        DataInputStream in = new DataInputStream(fis);
        HyperLogLog deserializedHLL = HyperLogLogUtils.deserializeHLL(in);
        Assert.assertEquals(hll, deserializedHLL);
        Assert.assertEquals(hll.toString(), deserializedHLL.toString());
        Assert.assertEquals(hll.toStringExtended(), deserializedHLL.toStringExtended());
        Assert.assertEquals(hll.hashCode(), deserializedHLL.hashCode());
        Assert.assertEquals(hll.count(), deserializedHLL.count());
        Assert.assertEquals(hashset.size(), hll.count(), delta);
        Assert.assertEquals(hashset.size(), deserializedHLL.count(), delta);
    }

    @Test
    public void testHLLDenseNoBitPacking() throws IOException {
        HyperLogLog hll = HyperLogLog.builder().setEncoding(DENSE).enableBitPacking(false).build();
        Random rand = new Random(TestHLLSerialization.SEED);
        for (int i = 0; i < (size); i++) {
            hll.addLong(rand.nextLong());
        }
        FileOutputStream fos = new FileOutputStream(testFile);
        DataOutputStream out = new DataOutputStream(fos);
        HyperLogLogUtils.serializeHLL(out, hll);
        FileInputStream fis = new FileInputStream(testFile);
        DataInputStream in = new DataInputStream(fis);
        HyperLogLog deserializedHLL = HyperLogLogUtils.deserializeHLL(in);
        Assert.assertEquals(hll, deserializedHLL);
        Assert.assertEquals(hll.toString(), deserializedHLL.toString());
        Assert.assertEquals(hll.toStringExtended(), deserializedHLL.toStringExtended());
        Assert.assertEquals(hll.hashCode(), deserializedHLL.hashCode());
        Assert.assertEquals(hll.count(), deserializedHLL.count());
    }

    @Test
    public void testHLLDenseNoBitPackingHalfDistinct() throws IOException {
        HyperLogLog hll = HyperLogLog.builder().setEncoding(DENSE).enableBitPacking(false).build();
        Random rand = new Random(TestHLLSerialization.SEED);
        Set<Integer> hashset = new HashSet<>();
        for (int i = 0; i < (size); i++) {
            int val = rand.nextInt(((size) / 2));
            hll.addLong(val);
            hashset.add(val);
        }
        FileOutputStream fos = new FileOutputStream(testFile);
        DataOutputStream out = new DataOutputStream(fos);
        HyperLogLogUtils.serializeHLL(out, hll);
        double threshold = ((size) > 40000) ? longRangeTolerance : shortRangeTolerance;
        double delta = (threshold * (hashset.size())) / 100;
        FileInputStream fis = new FileInputStream(testFile);
        DataInputStream in = new DataInputStream(fis);
        HyperLogLog deserializedHLL = HyperLogLogUtils.deserializeHLL(in);
        Assert.assertEquals(hll, deserializedHLL);
        Assert.assertEquals(hll.toString(), deserializedHLL.toString());
        Assert.assertEquals(hll.toStringExtended(), deserializedHLL.toStringExtended());
        Assert.assertEquals(hll.hashCode(), deserializedHLL.hashCode());
        Assert.assertEquals(hll.count(), deserializedHLL.count());
        Assert.assertEquals(hashset.size(), hll.count(), delta);
        Assert.assertEquals(hashset.size(), deserializedHLL.count(), delta);
    }
}

