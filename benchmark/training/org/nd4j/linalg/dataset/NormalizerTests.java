package org.nd4j.linalg.dataset;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.indexing.NDArrayIndex;


/**
 * Created by susaneraly on 11/13/16.
 */
@RunWith(Parameterized.class)
public class NormalizerTests extends BaseNd4jTest {
    public NormalizerTests(Nd4jBackend backend) {
        super(backend);
    }

    private NormalizerStandardize stdScaler;

    private NormalizerMinMaxScaler minMaxScaler;

    private DataSet data;

    private int batchSize;

    private int batchCount;

    private int lastBatch;

    private final float thresholdPerc = 2.0F;// this is the difference in percentage!


    @Test
    public void testPreProcessors() {
        System.out.println("Running iterator vs non-iterator std scaler..");
        double d1 = testItervsDataset(stdScaler);
        Assert.assertTrue(((d1 + " < ") + (thresholdPerc)), (d1 < (thresholdPerc)));
        System.out.println("Running iterator vs non-iterator min max scaler..");
        double d2 = testItervsDataset(minMaxScaler);
        Assert.assertTrue(((d2 + " < ") + (thresholdPerc)), (d2 < (thresholdPerc)));
    }

    @Test
    public void testMasking() {
        Nd4j.getRandom().setSeed(235);
        DataNormalization[] normalizers = new DataNormalization[]{ new NormalizerMinMaxScaler(), new NormalizerStandardize() };
        DataNormalization[] normalizersNoMask = new DataNormalization[]{ new NormalizerMinMaxScaler(), new NormalizerStandardize() };
        DataNormalization[] normalizersByRow = new DataNormalization[]{ new NormalizerMinMaxScaler(), new NormalizerStandardize() };
        for (int i = 0; i < (normalizers.length); i++) {
            // First: check that normalization is the same with/without masking arrays
            DataNormalization norm = normalizers[i];
            DataNormalization normFitSubset = normalizersNoMask[i];
            DataNormalization normByRow = normalizersByRow[i];
            System.out.println(norm.getClass());
            INDArray arr = Nd4j.rand('c', new int[]{ 2, 3, 5 }).muli(100).addi(100);
            arr.get(NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.interval(3, 5)).assign(0);
            INDArray arrCopy = arr.dup();
            INDArray arrPt1 = arr.get(NDArrayIndex.interval(0, 0, true), NDArrayIndex.all(), NDArrayIndex.all()).dup();
            INDArray arrPt2 = arr.get(NDArrayIndex.interval(1, 1, true), NDArrayIndex.all(), NDArrayIndex.interval(0, 3)).dup();
            INDArray mask = Nd4j.create(new double[][]{ new double[]{ 1, 1, 1, 1, 1 }, new double[]{ 1, 1, 1, 0, 0 } });
            DataSet ds = new DataSet(arr, null, mask, null);
            DataSet dsCopy1 = new DataSet(arr.dup(), null, mask, null);
            DataSet dsCopy2 = new DataSet(arr.dup(), null, mask, null);
            norm.fit(ds);
            // Check that values aren't modified by fit op
            Assert.assertEquals(arrCopy, arr);
            List<DataSet> toFitTimeSeries1Ex = new ArrayList<>();
            toFitTimeSeries1Ex.add(new DataSet(arrPt1, arrPt1));
            toFitTimeSeries1Ex.add(new DataSet(arrPt2, arrPt2));
            normFitSubset.fit(new org.nd4j.linalg.dataset.api.iterator.TestDataSetIterator(toFitTimeSeries1Ex, 1));
            List<DataSet> toFitRows = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                INDArray row = arr.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.interval(j, j, true)).transpose();
                Assert.assertTrue(row.isRowVector());
                toFitRows.add(new DataSet(row, row));
            }
            for (int j = 0; j < 3; j++) {
                INDArray row = arr.get(NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.interval(j, j, true)).transpose();
                Assert.assertTrue(row.isRowVector());
                toFitRows.add(new DataSet(row, row));
            }
            normByRow.fit(new org.nd4j.linalg.dataset.api.iterator.TestDataSetIterator(toFitRows, 1));
            norm.transform(ds);
            normFitSubset.transform(dsCopy1);
            normByRow.transform(dsCopy2);
            Assert.assertEquals(ds.getFeatures(), dsCopy1.getFeatures());
            Assert.assertEquals(ds.getLabels(), dsCopy1.getLabels());
            Assert.assertEquals(ds.getFeaturesMaskArray(), dsCopy1.getFeaturesMaskArray());
            Assert.assertEquals(ds.getLabelsMaskArray(), dsCopy1.getLabelsMaskArray());
            Assert.assertEquals(ds, dsCopy1);
            Assert.assertEquals(ds, dsCopy2);
            // Second: ensure time steps post normalization (and post revert) are 0.0
            INDArray shouldBe0_1 = ds.getFeatureMatrix().get(NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.interval(3, 5));
            INDArray shouldBe0_2 = dsCopy1.getFeatureMatrix().get(NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.interval(3, 5));
            INDArray shouldBe0_3 = dsCopy2.getFeatureMatrix().get(NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.interval(3, 5));
            INDArray zeros = Nd4j.zeros(shouldBe0_1.shape());
            for (int j = 0; j < 2; j++) {
                System.out.println(ds.getFeatureMatrix().get(NDArrayIndex.point(j), NDArrayIndex.all(), NDArrayIndex.all()));
                System.out.println();
            }
            Assert.assertEquals(zeros, shouldBe0_1);
            Assert.assertEquals(zeros, shouldBe0_2);
            Assert.assertEquals(zeros, shouldBe0_3);
            // Check same thing after reverting:
            norm.revert(ds);
            normFitSubset.revert(dsCopy1);
            normByRow.revert(dsCopy2);
            shouldBe0_1 = ds.getFeatureMatrix().get(NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.interval(3, 5));
            shouldBe0_2 = dsCopy1.getFeatureMatrix().get(NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.interval(3, 5));
            shouldBe0_3 = dsCopy2.getFeatureMatrix().get(NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.interval(3, 5));
            Assert.assertEquals(zeros, shouldBe0_1);
            Assert.assertEquals(zeros, shouldBe0_2);
            Assert.assertEquals(zeros, shouldBe0_3);
        }
    }
}

