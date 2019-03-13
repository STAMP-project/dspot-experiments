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
package org.deeplearning4j.nearestneighbor.server;


import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.deeplearning4j.clustering.sptree.DataPoint;
import org.deeplearning4j.clustering.vptree.VPTree;
import org.deeplearning4j.clustering.vptree.VPTreeFillSearch;
import org.deeplearning4j.nearestneighbor.client.NearestNeighborsClient;
import org.deeplearning4j.nearestneighbor.model.NearestNeighborRequest;
import org.deeplearning4j.nearestneighbor.model.NearestNeighborsResults;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.serde.binary.BinarySerde;


/**
 * Created by agibsonccc on 4/27/17.
 */
public class NearestNeighborTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testNearestNeighbor() {
        INDArray arr = Nd4j.create(new double[][]{ new double[]{ 1, 2, 3, 4 }, new double[]{ 1, 2, 3, 5 }, new double[]{ 3, 4, 5, 6 } });
        VPTree vpTree = new VPTree(arr, false);
        NearestNeighborRequest request = new NearestNeighborRequest();
        request.setK(2);
        request.setInputIndex(0);
        NearestNeighbor nearestNeighbor = NearestNeighbor.builder().tree(vpTree).points(arr).record(request).build();
        Assert.assertEquals(1, nearestNeighbor.search().get(0).getIndex());
    }

    @Test
    public void vpTreeTest() throws Exception {
        INDArray matrix = Nd4j.rand(new int[]{ 400, 10 });
        INDArray rowVector = matrix.getRow(70);
        INDArray resultArr = Nd4j.zeros(400, 1);
        Executor executor = Executors.newSingleThreadExecutor();
        VPTree vpTree = new VPTree(matrix);
        System.out.println("Ran!");
    }

    @Test
    public void testServer() throws Exception {
        int localPort = NearestNeighborTest.getAvailablePort();
        Nd4j.getRandom().setSeed(7);
        INDArray rand = Nd4j.randn(10, 5);
        File writeToTmp = testDir.newFile();
        writeToTmp.deleteOnExit();
        BinarySerde.writeArrayToDisk(rand, writeToTmp);
        NearestNeighborsServer server = new NearestNeighborsServer();
        server.runMain("--ndarrayPath", writeToTmp.getAbsolutePath(), "--nearestNeighborsPort", String.valueOf(localPort));
        NearestNeighborsClient client = new NearestNeighborsClient(("http://localhost:" + localPort));
        NearestNeighborsResults result = client.knnNew(5, rand.getRow(0));
        Assert.assertEquals(5, result.getResults().size());
        server.stop();
    }

    @Test
    public void testFullSearch() throws Exception {
        int numRows = 1000;
        int numCols = 100;
        int numNeighbors = 42;
        INDArray points = Nd4j.rand(numRows, numCols);
        VPTree tree = new VPTree(points);
        INDArray query = Nd4j.rand(new int[]{ 1, numCols });
        VPTreeFillSearch fillSearch = new VPTreeFillSearch(tree, numNeighbors, query);
        fillSearch.search();
        List<DataPoint> results = fillSearch.getResults();
        List<Double> distances = fillSearch.getDistances();
        Assert.assertEquals(numNeighbors, distances.size());
        Assert.assertEquals(numNeighbors, results.size());
    }
}

