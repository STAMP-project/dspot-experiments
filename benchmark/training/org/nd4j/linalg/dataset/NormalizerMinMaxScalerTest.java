package org.nd4j.linalg.dataset;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.ops.transforms.Transforms;


/**
 * Created by susaneraly on 5/25/16.
 */
@RunWith(Parameterized.class)
public class NormalizerMinMaxScalerTest extends BaseNd4jTest {
    public NormalizerMinMaxScalerTest(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testBruteForce() {
        // X_std = (X - X.min(axis=0)) / (X.max(axis=0) - X.min(axis=0))
        // X_scaled = X_std * (max - min) + min
        // Dataset features are scaled consecutive natural numbers
        int nSamples = 500;
        int x = 4;
        int y = 2;
        int z = 3;
        INDArray featureX = Nd4j.linspace(1, nSamples, nSamples).reshape(nSamples, 1);
        INDArray featureY = featureX.mul(y);
        INDArray featureZ = featureX.mul(z);
        featureX.muli(x);
        INDArray featureSet = Nd4j.concat(1, featureX, featureY, featureZ);
        INDArray labelSet = Nd4j.zeros(nSamples, 1);
        DataSet sampleDataSet = new DataSet(featureSet, labelSet);
        // expected min and max
        INDArray theoreticalMin = Nd4j.create(new double[]{ x, y, z });
        INDArray theoreticalMax = Nd4j.create(new double[]{ nSamples * x, nSamples * y, nSamples * z });
        INDArray theoreticalRange = theoreticalMax.sub(theoreticalMin);
        NormalizerMinMaxScaler myNormalizer = new NormalizerMinMaxScaler();
        myNormalizer.fit(sampleDataSet);
        INDArray minDataSet = myNormalizer.getMin();
        INDArray maxDataSet = myNormalizer.getMax();
        INDArray minDiff = minDataSet.sub(theoreticalMin).max(1);
        INDArray maxDiff = maxDataSet.sub(theoreticalMax).max(1);
        Assert.assertEquals(minDiff.getDouble(0, 0), 0.0, 1.0E-9);
        Assert.assertEquals(maxDiff.max(1).getDouble(0, 0), 0.0, 1.0E-9);
        // SAME TEST WITH THE ITERATOR
        int bSize = 1;
        DataSetIterator sampleIter = new org.nd4j.linalg.dataset.api.iterator.TestDataSetIterator(sampleDataSet, bSize);
        myNormalizer.fit(sampleIter);
        minDataSet = myNormalizer.getMin();
        maxDataSet = myNormalizer.getMax();
        Assert.assertEquals(minDataSet.sub(theoreticalMin).max(1).getDouble(0, 0), 0.0, 1.0E-9);
        Assert.assertEquals(maxDataSet.sub(theoreticalMax).max(1).getDouble(0, 0), 0.0, 1.0E-9);
        sampleIter.setPreProcessor(myNormalizer);
        INDArray actual;
        INDArray expected;
        INDArray delta;
        int i = 1;
        while (sampleIter.hasNext()) {
            expected = theoreticalMin.mul((i - 1)).div(theoreticalRange);
            actual = sampleIter.next().getFeatures();
            delta = Transforms.abs(actual.sub(expected));
            Assert.assertTrue(((delta.max(1).getDouble(0, 0)) < 1.0E-4));
            i++;
        } 
    }

    @Test
    public void testRevert() {
        double tolerancePerc = 1;// 1% of correct value

        int nSamples = 500;
        int nFeatures = 3;
        Nd4j.getRandom().setSeed(12345);
        INDArray featureSet = Nd4j.rand(nSamples, nFeatures);
        INDArray labelSet = Nd4j.zeros(nSamples, 1);
        DataSet sampleDataSet = new DataSet(featureSet, labelSet);
        NormalizerMinMaxScaler myNormalizer = new NormalizerMinMaxScaler();
        DataSet transformed = sampleDataSet.copy();
        myNormalizer.fit(sampleDataSet);
        myNormalizer.transform(transformed);
        myNormalizer.revert(transformed);
        INDArray delta = Transforms.abs(transformed.getFeatures().sub(sampleDataSet.getFeatures())).div(sampleDataSet.getFeatures());
        double maxdeltaPerc = delta.max(0, 1).mul(100).getDouble(0, 0);
        System.out.println(("Delta: " + maxdeltaPerc));
        Assert.assertTrue((maxdeltaPerc < tolerancePerc));
    }

    @Test
    public void testGivenMaxMin() {
        double tolerancePerc = 1;// 1% of correct value

        int nSamples = 500;
        int nFeatures = 3;
        Nd4j.getRandom().setSeed(12345);
        INDArray featureSet = Nd4j.rand(nSamples, nFeatures);
        INDArray labelSet = Nd4j.zeros(nSamples, 1);
        DataSet sampleDataSet = new DataSet(featureSet, labelSet);
        double givenMin = -1;
        double givenMax = 1;
        NormalizerMinMaxScaler myNormalizer = new NormalizerMinMaxScaler(givenMin, givenMax);
        DataSet transformed = sampleDataSet.copy();
        myNormalizer.fit(sampleDataSet);
        myNormalizer.transform(transformed);
        myNormalizer.revert(transformed);
        INDArray delta = Transforms.abs(transformed.getFeatures().sub(sampleDataSet.getFeatures())).div(sampleDataSet.getFeatures());
        double maxdeltaPerc = delta.max(0, 1).mul(100).getDouble(0, 0);
        System.out.println(("Delta: " + maxdeltaPerc));
        Assert.assertTrue((maxdeltaPerc < tolerancePerc));
    }

    @Test
    public void testGivenMaxMinConstant() {
        double tolerancePerc = 1;// 1% of correct value

        int nSamples = 500;
        int nFeatures = 3;
        INDArray featureSet = Nd4j.rand(nSamples, nFeatures).mul(0.1).add(10);
        INDArray labelSet = Nd4j.zeros(nSamples, 1);
        DataSet sampleDataSet = new DataSet(featureSet, labelSet);
        double givenMin = -1000;
        double givenMax = 1000;
        DataNormalization myNormalizer = new NormalizerMinMaxScaler(givenMin, givenMax);
        DataSet transformed = sampleDataSet.copy();
        myNormalizer.fit(sampleDataSet);
        myNormalizer.transform(transformed);
        // feature set is basically all 10s -> should transform to the min
        INDArray expected = Nd4j.ones(nSamples, nFeatures).mul(givenMin);
        INDArray delta = Transforms.abs(transformed.getFeatures().sub(expected)).div(expected);
        double maxdeltaPerc = delta.max(0, 1).mul(100).getDouble(0, 0);
        Assert.assertTrue((maxdeltaPerc < tolerancePerc));
    }

    @Test
    public void testConstant() {
        double tolerancePerc = 0.01;// 0.01% of correct value

        int nSamples = 500;
        int nFeatures = 3;
        INDArray featureSet = Nd4j.zeros(nSamples, nFeatures).add(100);
        INDArray labelSet = Nd4j.zeros(nSamples, 1);
        DataSet sampleDataSet = new DataSet(featureSet, labelSet);
        NormalizerMinMaxScaler myNormalizer = new NormalizerMinMaxScaler();
        myNormalizer.fit(sampleDataSet);
        myNormalizer.transform(sampleDataSet);
        Assert.assertFalse(Double.isNaN(sampleDataSet.getFeatures().min(0, 1).getDouble(0)));
        Assert.assertEquals(sampleDataSet.getFeatures().sumNumber().doubleValue(), 0, 1.0E-5);
        myNormalizer.revert(sampleDataSet);
        Assert.assertFalse(Double.isNaN(sampleDataSet.getFeatures().min(0, 1).getDouble(0)));
        Assert.assertEquals(sampleDataSet.getFeatures().sumNumber().doubleValue(), ((100 * nFeatures) * nSamples), 1.0E-5);
    }
}

