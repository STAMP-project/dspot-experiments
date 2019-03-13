/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.deeplearning4j.clustering.lsh;


import DataType.BOOL;
import DataType.FLOAT;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


public class RandomProjectionLSHTest {
    int hashLength = 31;

    int numTables = 2;

    int intDimensions = 13;

    RandomProjectionLSH rpLSH;

    INDArray e1 = Nd4j.ones(1, intDimensions);

    INDArray inputs;

    @Test
    public void testEntropyDims() {
        Assert.assertArrayEquals(new long[]{ numTables, intDimensions }, rpLSH.entropy(e1).shape());
    }

    @Test
    public void testHashDims() {
        Assert.assertArrayEquals(new long[]{ 1, hashLength }, rpLSH.hash(e1).shape());
    }

    @Test
    public void testHashDimsMultiple() {
        INDArray data = Nd4j.ones(1, intDimensions);
        Assert.assertArrayEquals(new long[]{ 1, hashLength }, rpLSH.hash(data).shape());
        data = Nd4j.ones(100, intDimensions);
        Assert.assertArrayEquals(new long[]{ 100, hashLength }, rpLSH.hash(data).shape());
    }

    @Test
    public void testSigNums() {
        Assert.assertEquals(1.0F, rpLSH.hash(e1).aminNumber().floatValue(), 0.001F);
    }

    @Test
    public void testIndexDims() {
        rpLSH.makeIndex(Nd4j.rand(100, intDimensions));
        Assert.assertArrayEquals(new long[]{ 100, hashLength }, rpLSH.index.shape());
    }

    @Test
    public void testGetRawBucketOfDims() {
        rpLSH.makeIndex(inputs);
        Assert.assertArrayEquals(new long[]{ 100, 1 }, rpLSH.rawBucketOf(e1).shape());
    }

    @Test
    public void testRawBucketOfReflexive() {
        rpLSH.makeIndex(inputs);
        int idx = new Random(12345).nextInt(100);
        INDArray row = inputs.getRow(idx);
        Assert.assertEquals(1.0F, rpLSH.rawBucketOf(row).maxNumber().floatValue(), 0.001F);
    }

    @Test
    public void testBucketDims() {
        rpLSH.makeIndex(inputs);
        Assert.assertArrayEquals(new long[]{ 100, 1 }, rpLSH.bucket(e1).shape());
    }

    @Test
    public void testBucketReflexive() {
        rpLSH.makeIndex(inputs);
        int idx = new Random(12345).nextInt(100);
        INDArray row = inputs.getRow(idx);
        Assert.assertEquals(1.0F, rpLSH.bucket(row).maxNumber().floatValue(), 0.001F);
    }

    @Test
    public void testBucketDataReflexiveDimensions() {
        rpLSH.makeIndex(inputs);
        int idx = new Random(12345).nextInt(100);
        INDArray row = inputs.getRow(idx);
        INDArray bucketData = rpLSH.bucketData(row);
        Assert.assertEquals(intDimensions, bucketData.shape()[1]);
        Assert.assertTrue((1 <= (bucketData.shape()[0])));
    }

    @Test
    public void testBucketDataReflexive() {
        rpLSH.makeIndex(inputs);
        int idx = new Random(12345).nextInt(100);
        INDArray row = inputs.getRow(idx);
        INDArray bucketData = rpLSH.bucketData(row);
        INDArray res = Nd4j.zeros(BOOL, bucketData.shape());
        Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.broadcast.bool.BroadcastEqualTo(bucketData, row, res, (-1)));
        res = res.castTo(FLOAT);
        Assert.assertEquals(String.format("Expected one bucket content to be the query %s, but found %s", row, rpLSH.bucket(row)), 1.0F, res.min((-1)).maxNumber().floatValue(), 0.001F);
    }

    @Test
    public void testSearchReflexiveDimensions() {
        rpLSH.makeIndex(inputs);
        int idx = new Random(12345).nextInt(100);
        INDArray row = inputs.getRow(idx);
        INDArray searchResults = rpLSH.search(row, 10.0F);
        Assert.assertTrue(String.format("Expected the search to return at least one result, the query %s but found %s yielding %d results", row, searchResults, searchResults.shape()[0]), ((searchResults.shape()[0]) >= 1));
    }

    @Test
    public void testSearchReflexive() {
        rpLSH.makeIndex(inputs);
        int idx = new Random(12345).nextInt(100);
        INDArray row = inputs.getRow(idx);
        INDArray searchResults = rpLSH.search(row, 10.0F);
        INDArray res = Nd4j.zeros(BOOL, searchResults.shape());
        Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.broadcast.bool.BroadcastEqualTo(searchResults, row, res, (-1)));
        res = res.castTo(FLOAT);
        Assert.assertEquals(String.format("Expected one search result to be the query %s, but found %s", row, searchResults), 1.0F, res.min((-1)).maxNumber().floatValue(), 0.001F);
    }

    @Test
    public void testANNSearchReflexiveDimensions() {
        rpLSH.makeIndex(inputs);
        int idx = new Random(12345).nextInt(100);
        INDArray row = inputs.getRow(idx);
        INDArray searchResults = rpLSH.search(row, 100);
        Assert.assertTrue(String.format("Expected the search to return at least one result, the query %s but found %s yielding %d results", row, searchResults, searchResults.shape()[0]), ((searchResults.shape()[0]) >= 1));
    }

    @Test
    public void testANNSearchReflexive() {
        rpLSH.makeIndex(inputs);
        int idx = new Random(12345).nextInt(100);
        INDArray row = inputs.getRow(idx);
        INDArray searchResults = rpLSH.search(row, 100);
        INDArray res = Nd4j.zeros(BOOL, searchResults.shape());
        Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.broadcast.bool.BroadcastEqualTo(searchResults, row, res, (-1)));
        res = res.castTo(FLOAT);
        Assert.assertEquals(String.format("Expected one search result to be the query %s, but found %s", row, searchResults), 1.0F, res.min((-1)).maxNumber().floatValue(), 0.001F);
    }
}

