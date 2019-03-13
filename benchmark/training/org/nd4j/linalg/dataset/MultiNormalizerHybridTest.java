package org.nd4j.linalg.dataset;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.MultiNormalizerHybrid;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 * In-depth testing of correctness of standardization and min-max scaling is covered by other tests, since the code for
 * doing that is reused in MultiNormalizerHybrid. These tests will just cover the configurability.
 */
@RunWith(Parameterized.class)
public class MultiNormalizerHybridTest extends BaseNd4jTest {
    private MultiNormalizerHybrid SUT;

    private MultiDataSet data;

    private MultiDataSet dataCopy;

    public MultiNormalizerHybridTest(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testNoNormalizationByDefault() {
        SUT.fit(data);
        SUT.preProcess(data);
        Assert.assertEquals(dataCopy, data);
        SUT.revert(data);
        Assert.assertEquals(dataCopy, data);
    }

    @Test
    public void testGlobalNormalization() {
        SUT.standardizeAllInputs().minMaxScaleAllOutputs((-10), 10).fit(data);
        SUT.preProcess(data);
        MultiDataSet expected = new MultiDataSet(new INDArray[]{ Nd4j.create(new float[][]{ new float[]{ -1, -1 }, new float[]{ 1, 1 } }), Nd4j.create(new float[][]{ new float[]{ -1, -1 }, new float[]{ 1, 1 } }) }, new INDArray[]{ Nd4j.create(new float[][]{ new float[]{ -10, -10 }, new float[]{ 10, 10 } }), Nd4j.create(new float[][]{ new float[]{ -10, -10 }, new float[]{ 10, 10 } }) });
        Assert.assertEquals(expected, data);
        SUT.revert(data);
        Assert.assertEquals(dataCopy, data);
    }

    @Test
    public void testSpecificInputOutputNormalization() {
        SUT.minMaxScaleAllInputs().standardizeInput(1).standardizeOutput(0).fit(data);
        SUT.preProcess(data);
        MultiDataSet expected = new MultiDataSet(new INDArray[]{ Nd4j.create(new float[][]{ new float[]{ 0, 0 }, new float[]{ 1, 1 } }), Nd4j.create(new float[][]{ new float[]{ -1, -1 }, new float[]{ 1, 1 } }) }, new INDArray[]{ Nd4j.create(new float[][]{ new float[]{ -1, -1 }, new float[]{ 1, 1 } }), Nd4j.create(new float[][]{ new float[]{ 14, 15 }, new float[]{ 16, 17 } }) });
        Assert.assertEquals(expected, data);
        SUT.revert(data);
        Assert.assertEquals(dataCopy, data);
    }

    @Test
    public void testMasking() {
        MultiDataSet timeSeries = new MultiDataSet(new INDArray[]{ Nd4j.create(new float[]{ 1, 2, 3, 4, 5, 0, 7, 0 }).reshape(2, 2, 2) }, new INDArray[]{ Nd4j.create(new float[]{ 0, 20, 0, 40, 50, 60, 70, 80 }).reshape(2, 2, 2) }, new INDArray[]{ Nd4j.create(new float[][]{ new float[]{ 1, 1 }, new float[]{ 1, 0 } }) }, new INDArray[]{ Nd4j.create(new float[][]{ new float[]{ 0, 1 }, new float[]{ 1, 1 } }) });
        MultiDataSet timeSeriesCopy = timeSeries.copy();
        SUT.minMaxScaleAllInputs((-10), 10).minMaxScaleAllOutputs((-10), 10).fit(timeSeries);
        SUT.preProcess(timeSeries);
        MultiDataSet expected = new MultiDataSet(new INDArray[]{ Nd4j.create(new float[]{ -10, -5, -10, -5, 10, 0, 10, 0 }).reshape(2, 2, 2) }, new INDArray[]{ Nd4j.create(new float[]{ 0, -10, 0, -10, 5, 10, 5, 10 }).reshape(2, 2, 2) }, new INDArray[]{ Nd4j.create(new float[][]{ new float[]{ 1, 1 }, new float[]{ 1, 0 } }) }, new INDArray[]{ Nd4j.create(new float[][]{ new float[]{ 0, 1 }, new float[]{ 1, 1 } }) });
        Assert.assertEquals(expected, timeSeries);
        SUT.revert(timeSeries);
        Assert.assertEquals(timeSeriesCopy, timeSeries);
    }

    @Test
    public void testDataSetWithoutLabels() {
        SUT.standardizeAllInputs().standardizeAllOutputs().fit(data);
        data.setLabels(null);
        data.setLabelsMaskArray(null);
        SUT.preProcess(data);
    }

    @Test
    public void testDataSetWithoutFeatures() {
        SUT.standardizeAllInputs().standardizeAllOutputs().fit(data);
        data.setFeatures(null);
        data.setFeaturesMaskArrays(null);
        SUT.preProcess(data);
    }
}

