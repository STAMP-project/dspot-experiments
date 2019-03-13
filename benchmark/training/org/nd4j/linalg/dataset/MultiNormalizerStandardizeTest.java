package org.nd4j.linalg.dataset;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.TestMultiDataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.MultiNormalizerStandardize;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 * Most of the normalizer functionality is shared with {@link MultiNormalizerStandardize}
 * and is covered in {@link NormalizerStandardizeTest}. This test suite just verifies if it deals properly with
 * multiple inputs and multiple outputs
 *
 * @author Ede Meijer
 */
@RunWith(Parameterized.class)
public class MultiNormalizerStandardizeTest extends BaseNd4jTest {
    private static final double TOLERANCE_PERC = 0.01;// 0.01% of correct value


    private static final int INPUT1_SCALE = 1;

    private static final int INPUT2_SCALE = 2;

    private static final int OUTPUT1_SCALE = 3;

    private static final int OUTPUT2_SCALE = 4;

    private MultiNormalizerStandardize SUT;

    private MultiDataSet data;

    private double meanNaturalNums;

    private double stdNaturalNums;

    public MultiNormalizerStandardizeTest(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testMultipleInputsAndOutputsWithDataSet() {
        SUT.fit(data);
        assertExpectedMeanStd();
    }

    @Test
    public void testMultipleInputsAndOutputsWithIterator() {
        MultiDataSetIterator iter = new TestMultiDataSetIterator(1, data);
        SUT.fit(iter);
        assertExpectedMeanStd();
    }

    @Test
    public void testRevertFeaturesINDArray() {
        SUT.fit(data);
        MultiDataSet transformed = data.copy();
        SUT.preProcess(transformed);
        INDArray reverted = transformed.getFeatures(0).dup();
        SUT.revertFeatures(reverted, null, 0);
        Assert.assertNotEquals(reverted, transformed.getFeatures(0));
        SUT.revert(transformed);
        Assert.assertEquals(reverted, transformed.getFeatures(0));
    }

    @Test
    public void testRevertLabelsINDArray() {
        SUT.fit(data);
        MultiDataSet transformed = data.copy();
        SUT.preProcess(transformed);
        INDArray reverted = transformed.getLabels(0).dup();
        SUT.revertLabels(reverted, null, 0);
        Assert.assertNotEquals(reverted, transformed.getLabels(0));
        SUT.revert(transformed);
        Assert.assertEquals(reverted, transformed.getLabels(0));
    }

    @Test
    public void testRevertMultiDataSet() {
        SUT.fit(data);
        MultiDataSet transformed = data.copy();
        SUT.preProcess(transformed);
        double diffBeforeRevert = getMaxRelativeDifference(data, transformed);
        Assert.assertTrue((diffBeforeRevert > (MultiNormalizerStandardizeTest.TOLERANCE_PERC)));
        SUT.revert(transformed);
        double diffAfterRevert = getMaxRelativeDifference(data, transformed);
        Assert.assertTrue((diffAfterRevert < (MultiNormalizerStandardizeTest.TOLERANCE_PERC)));
    }

    @Test
    public void testFullyMaskedData() {
        MultiDataSetIterator iter = new TestMultiDataSetIterator(1, new MultiDataSet(new INDArray[]{ Nd4j.create(new float[]{ 1 }).reshape(1, 1, 1) }, new INDArray[]{ Nd4j.create(new float[]{ 2 }).reshape(1, 1, 1) }), new MultiDataSet(new INDArray[]{ Nd4j.create(new float[]{ 2 }).reshape(1, 1, 1) }, new INDArray[]{ Nd4j.create(new float[]{ 4 }).reshape(1, 1, 1) }, null, new INDArray[]{ Nd4j.create(new float[]{ 0 }).reshape(1, 1) }));
        SUT.fit(iter);
        // The label mean should be 2, as the second row with 4 is masked.
        Assert.assertEquals(2.0F, SUT.getLabelMean(0).getFloat(0), 1.0E-6);
    }
}

