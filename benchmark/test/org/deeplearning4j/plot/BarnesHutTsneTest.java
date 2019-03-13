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
package org.deeplearning4j.plot;


import DataType.DOUBLE;
import java.io.File;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.deeplearning4j.BaseDL4JTest;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.buffer.util.DataTypeUtil;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.io.ClassPathResource;


// import org.nd4j.jita.conf.CudaEnvironment;
/**
 * Created by agibsonccc on 10/1/14.
 */
public class BarnesHutTsneTest extends BaseDL4JTest {
    @Test
    public void testTsne() throws Exception {
        DataTypeUtil.setDTypeForContext(DOUBLE);
        Nd4j.getRandom().setSeed(123);
        BarnesHutTsne b = new BarnesHutTsne.Builder().stopLyingIteration(10).setMaxIter(10).theta(0.5).learningRate(500).useAdaGrad(false).build();
        ClassPathResource resource = new ClassPathResource("/mnist2500_X.txt");
        File f = resource.getTempFileFromArchive();
        INDArray data = Nd4j.readNumpy(f.getAbsolutePath(), "   ").get(NDArrayIndex.interval(0, 100), NDArrayIndex.interval(0, 784));
        ClassPathResource labels = new ClassPathResource("mnist2500_labels.txt");
        List<String> labelsList = IOUtils.readLines(labels.getInputStream()).subList(0, 100);
        b.fit(data);
    }

    @Test
    public void testBuilderFields() throws Exception {
        final double theta = 0;
        final boolean invert = false;
        final String similarityFunctions = "euclidean";
        final int maxIter = 1;
        final double realMin = 1.0;
        final double initialMomentum = 2.0;
        final double finalMomentum = 3.0;
        final double momentum = 4.0;
        final int switchMomentumIteration = 1;
        final boolean normalize = false;
        final int stopLyingIteration = 100;
        final double tolerance = 0.1;
        final double learningRate = 100;
        final boolean useAdaGrad = false;
        final double perplexity = 1.0;
        final double minGain = 1.0;
        BarnesHutTsne b = new BarnesHutTsne.Builder().theta(theta).invertDistanceMetric(invert).similarityFunction(similarityFunctions).setMaxIter(maxIter).setRealMin(realMin).setInitialMomentum(initialMomentum).setFinalMomentum(finalMomentum).setMomentum(momentum).setSwitchMomentumIteration(switchMomentumIteration).normalize(normalize).stopLyingIteration(stopLyingIteration).tolerance(tolerance).learningRate(learningRate).perplexity(perplexity).minGain(minGain).build();
        final double DELTA = 1.0E-15;
        Assert.assertEquals(theta, b.getTheta(), DELTA);
        Assert.assertEquals("invert", invert, b.isInvert());
        Assert.assertEquals("similarityFunctions", similarityFunctions, b.getSimiarlityFunction());
        Assert.assertEquals("maxIter", maxIter, b.maxIter);
        Assert.assertEquals(realMin, b.realMin, DELTA);
        Assert.assertEquals(initialMomentum, b.initialMomentum, DELTA);
        Assert.assertEquals(finalMomentum, b.finalMomentum, DELTA);
        Assert.assertEquals(momentum, b.momentum, DELTA);
        Assert.assertEquals("switchMomentumnIteration", switchMomentumIteration, b.switchMomentumIteration);
        Assert.assertEquals("normalize", normalize, b.normalize);
        Assert.assertEquals("stopLyingInMemoryLookupTable.javaIteration", stopLyingIteration, b.stopLyingIteration);
        Assert.assertEquals(tolerance, b.tolerance, DELTA);
        Assert.assertEquals(learningRate, b.learningRate, DELTA);
        Assert.assertEquals("useAdaGrad", useAdaGrad, b.useAdaGrad);
        Assert.assertEquals(perplexity, b.getPerplexity(), DELTA);
        Assert.assertEquals(minGain, b.minGain, DELTA);
    }
}

