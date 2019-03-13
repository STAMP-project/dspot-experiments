/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.ml.knn;


import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.apache.ignite.ml.math.ExternalizableTest;
import org.apache.ignite.ml.math.exceptions.CardinalityException;
import org.apache.ignite.ml.math.exceptions.NoDataException;
import org.apache.ignite.ml.math.exceptions.knn.EmptyFileException;
import org.apache.ignite.ml.math.exceptions.knn.FileParsingException;
import org.apache.ignite.ml.math.exceptions.knn.NoLabelVectorException;
import org.apache.ignite.ml.structures.LabeledVector;
import org.apache.ignite.ml.structures.LabeledVectorSet;
import org.apache.ignite.ml.structures.LabeledVectorSetTestTrainPair;
import org.apache.ignite.ml.structures.preprocessing.LabeledDatasetLoader;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests behaviour of KNNClassificationTest.
 */
public class LabeledVectorSetTest implements ExternalizableTest<LabeledVectorSet> {
    /**
     *
     */
    private static final String KNN_IRIS_TXT = "datasets/knn/iris.txt";

    /**
     *
     */
    private static final String NO_DATA_TXT = "datasets/knn/no_data.txt";

    /**
     *
     */
    private static final String EMPTY_TXT = "datasets/knn/empty.txt";

    /**
     *
     */
    private static final String IRIS_INCORRECT_TXT = "datasets/knn/iris_incorrect.txt";

    /**
     *
     */
    private static final String IRIS_MISSED_DATA = "datasets/knn/missed_data.txt";

    /**
     *
     */
    @Test
    public void testFeatureNames() {
        double[][] mtx = new double[][]{ new double[]{ 1.0, 1.0 }, new double[]{ 1.0, 2.0 }, new double[]{ 2.0, 1.0 }, new double[]{ -1.0, -1.0 }, new double[]{ -1.0, -2.0 }, new double[]{ -2.0, -1.0 } };
        double[] lbs = new double[]{ 1.0, 1.0, 1.0, 2.0, 2.0, 2.0 };
        String[] featureNames = new String[]{ "x", "y" };
        final LabeledVectorSet dataset = new LabeledVectorSet(mtx, lbs, featureNames, false);
        Assert.assertEquals(dataset.getFeatureName(0), "x");
    }

    /**
     *
     */
    @Test
    public void testAccessMethods() {
        double[][] mtx = new double[][]{ new double[]{ 1.0, 1.0 }, new double[]{ 1.0, 2.0 }, new double[]{ 2.0, 1.0 }, new double[]{ -1.0, -1.0 }, new double[]{ -1.0, -2.0 }, new double[]{ -2.0, -1.0 } };
        double[] lbs = new double[]{ 1.0, 1.0, 1.0, 2.0, 2.0, 2.0 };
        final LabeledVectorSet dataset = new LabeledVectorSet(mtx, lbs, null, false);
        Assert.assertEquals(dataset.colSize(), 2);
        Assert.assertEquals(dataset.rowSize(), 6);
        Assert.assertEquals(dataset.label(0), lbs[0], 0);
        Assert.assertEquals(dataset.copy().colSize(), 2);
        final LabeledVector<Double> row = ((LabeledVector<Double>) (dataset.getRow(0)));
        Assert.assertEquals(1.0, row.features().get(0), 0);
        Assert.assertEquals(1.0, row.label(), 0);
        dataset.setLabel(0, 2.0);
        Assert.assertEquals(2.0, row.label(), 0);
        Assert.assertEquals(0, new LabeledVectorSet().rowSize());
        Assert.assertEquals(1, new LabeledVectorSet(1, 2).rowSize());
        Assert.assertEquals(1, new LabeledVectorSet(1, 2, true).rowSize());
        Assert.assertEquals(1, new LabeledVectorSet(1, 2, null, true).rowSize());
    }

    /**
     *
     */
    @Test
    public void testFailOnYNull() {
        double[][] mtx = new double[][]{ new double[]{ 1.0, 1.0 }, new double[]{ 1.0, 2.0 }, new double[]{ 2.0, 1.0 }, new double[]{ -1.0, -1.0 }, new double[]{ -1.0, -2.0 }, new double[]{ -2.0, -1.0 } };
        double[] lbs = new double[]{  };
        try {
            new LabeledVectorSet(mtx, lbs);
            Assert.fail("CardinalityException");
        } catch (CardinalityException e) {
            return;
        }
        Assert.fail("CardinalityException");
    }

    /**
     *
     */
    @Test
    public void testFailOnXNull() {
        double[][] mtx = new double[][]{  };
        double[] lbs = new double[]{ 1.0, 1.0, 1.0, 2.0, 2.0, 2.0 };
        try {
            new LabeledVectorSet(mtx, lbs);
            Assert.fail("CardinalityException");
        } catch (CardinalityException e) {
            return;
        }
        Assert.fail("CardinalityException");
    }

    /**
     *
     */
    @Test
    public void testLoadingCorrectTxtFile() {
        LabeledVectorSet training = LabeledDatasetHelper.loadDatasetFromTxt(LabeledVectorSetTest.KNN_IRIS_TXT, false);
        Assert.assertEquals(training.rowSize(), 150);
    }

    /**
     *
     */
    @Test
    public void testLoadingEmptyFile() {
        try {
            LabeledDatasetHelper.loadDatasetFromTxt(LabeledVectorSetTest.EMPTY_TXT, false);
            Assert.fail("EmptyFileException");
        } catch (EmptyFileException e) {
            return;
        }
        Assert.fail("EmptyFileException");
    }

    /**
     *
     */
    @Test
    public void testLoadingFileWithFirstEmptyRow() {
        try {
            LabeledDatasetHelper.loadDatasetFromTxt(LabeledVectorSetTest.NO_DATA_TXT, false);
            Assert.fail("NoDataException");
        } catch (NoDataException e) {
            return;
        }
        Assert.fail("NoDataException");
    }

    /**
     *
     */
    @Test
    public void testLoadingFileWithIncorrectData() {
        LabeledVectorSet training = LabeledDatasetHelper.loadDatasetFromTxt(LabeledVectorSetTest.IRIS_INCORRECT_TXT, false);
        Assert.assertEquals(149, training.rowSize());
    }

    /**
     *
     */
    @Test
    public void testFailOnLoadingFileWithIncorrectData() {
        try {
            LabeledDatasetHelper.loadDatasetFromTxt(LabeledVectorSetTest.IRIS_INCORRECT_TXT, true);
            Assert.fail("FileParsingException");
        } catch (FileParsingException e) {
            return;
        }
        Assert.fail("FileParsingException");
    }

    /**
     *
     */
    @Test
    public void testLoadingFileWithMissedData() throws IOException, URISyntaxException {
        Path path = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource(LabeledVectorSetTest.IRIS_MISSED_DATA)).toURI());
        LabeledVectorSet training = LabeledDatasetLoader.loadFromTxtFile(path, ",", false, false);
        Assert.assertEquals(training.features(2).get(1), 0.0, 0);
    }

    /**
     *
     */
    @Test
    public void testSplitting() {
        double[][] mtx = new double[][]{ new double[]{ 1.0, 1.0 }, new double[]{ 1.0, 2.0 }, new double[]{ 2.0, 1.0 }, new double[]{ -1.0, -1.0 }, new double[]{ -1.0, -2.0 }, new double[]{ -2.0, -1.0 } };
        double[] lbs = new double[]{ 1.0, 1.0, 1.0, 2.0, 2.0, 2.0 };
        LabeledVectorSet training = new LabeledVectorSet(mtx, lbs);
        LabeledVectorSetTestTrainPair split1 = new LabeledVectorSetTestTrainPair(training, 0.67);
        Assert.assertEquals(4, split1.test().rowSize());
        Assert.assertEquals(2, split1.train().rowSize());
        LabeledVectorSetTestTrainPair split2 = new LabeledVectorSetTestTrainPair(training, 0.65);
        Assert.assertEquals(3, split2.test().rowSize());
        Assert.assertEquals(3, split2.train().rowSize());
        LabeledVectorSetTestTrainPair split3 = new LabeledVectorSetTestTrainPair(training, 0.4);
        Assert.assertEquals(2, split3.test().rowSize());
        Assert.assertEquals(4, split3.train().rowSize());
        LabeledVectorSetTestTrainPair split4 = new LabeledVectorSetTestTrainPair(training, 0.3);
        Assert.assertEquals(1, split4.test().rowSize());
        Assert.assertEquals(5, split4.train().rowSize());
    }

    /**
     *
     */
    @Test
    public void testLabels() {
        double[][] mtx = new double[][]{ new double[]{ 1.0, 1.0 }, new double[]{ 1.0, 2.0 }, new double[]{ 2.0, 1.0 }, new double[]{ -1.0, -1.0 }, new double[]{ -1.0, -2.0 }, new double[]{ -2.0, -1.0 } };
        double[] lbs = new double[]{ 1.0, 1.0, 1.0, 2.0, 2.0, 2.0 };
        LabeledVectorSet dataset = new LabeledVectorSet(mtx, lbs);
        final double[] labels = dataset.labels();
        for (int i = 0; i < (lbs.length); i++)
            Assert.assertEquals(lbs[i], labels[i], 0);

    }

    /**
     *
     */
    @Test(expected = NoLabelVectorException.class)
    @SuppressWarnings("unchecked")
    public void testSetLabelInvalid() {
        setLabel(0, 2.0);
    }

    /**
     *
     */
    @Test
    @Override
    public void testExternalization() {
        double[][] mtx = new double[][]{ new double[]{ 1.0, 1.0 }, new double[]{ 1.0, 2.0 }, new double[]{ 2.0, 1.0 }, new double[]{ -1.0, -1.0 }, new double[]{ -1.0, -2.0 }, new double[]{ -2.0, -1.0 } };
        double[] lbs = new double[]{ 1.0, 1.0, 1.0, 2.0, 2.0, 2.0 };
        LabeledVectorSet dataset = new LabeledVectorSet(mtx, lbs);
        externalizeTest(dataset);
    }
}

