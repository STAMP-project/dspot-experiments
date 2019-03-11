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
package org.nd4j.linalg.dataset;


import DataType.FLOAT;
import Nd4j.EPS_THRESHOLD;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.ops.transforms.Transforms;


/**
 * Created by susaneraly on 7/15/16.
 */
@Slf4j
@RunWith(Parameterized.class)
public class PreProcessor3D4DTest extends BaseNd4jTest {
    public PreProcessor3D4DTest(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testBruteForce3d() {
        NormalizerStandardize myNormalizer = new NormalizerStandardize();
        NormalizerMinMaxScaler myMinMaxScaler = new NormalizerMinMaxScaler();
        int timeSteps = 15;
        int samples = 100;
        // multiplier for the features
        INDArray featureScaleA = Nd4j.create(new double[]{ 1, -2, 3 }).reshape(3, 1);
        INDArray featureScaleB = Nd4j.create(new double[]{ 2, 2, 3 }).reshape(3, 1);
        PreProcessor3D4DTest.Construct3dDataSet caseA = new PreProcessor3D4DTest.Construct3dDataSet(featureScaleA, timeSteps, samples, 1);
        PreProcessor3D4DTest.Construct3dDataSet caseB = new PreProcessor3D4DTest.Construct3dDataSet(featureScaleB, timeSteps, samples, 1);
        myNormalizer.fit(caseA.sampleDataSet);
        Assert.assertEquals(caseA.expectedMean.castTo(FLOAT), myNormalizer.getMean().castTo(FLOAT));
        Assert.assertTrue(((Transforms.abs(myNormalizer.getStd().div(caseA.expectedStd).sub(1)).maxNumber().floatValue()) < 0.01));
        myMinMaxScaler.fit(caseB.sampleDataSet);
        Assert.assertEquals(caseB.expectedMin.castTo(FLOAT), myMinMaxScaler.getMin().castTo(FLOAT));
        Assert.assertEquals(caseB.expectedMax.castTo(FLOAT), myMinMaxScaler.getMax().castTo(FLOAT));
        // Same Test with an Iterator, values should be close for std, exact for everything else
        DataSetIterator sampleIterA = new org.nd4j.linalg.dataset.api.iterator.TestDataSetIterator(caseA.sampleDataSet, 5);
        DataSetIterator sampleIterB = new org.nd4j.linalg.dataset.api.iterator.TestDataSetIterator(caseB.sampleDataSet, 5);
        myNormalizer.fit(sampleIterA);
        Assert.assertEquals(myNormalizer.getMean().castTo(FLOAT), caseA.expectedMean.castTo(FLOAT));
        Assert.assertTrue(((Transforms.abs(myNormalizer.getStd().div(caseA.expectedStd).sub(1)).maxNumber().floatValue()) < 0.01));
        myMinMaxScaler.fit(sampleIterB);
        Assert.assertEquals(myMinMaxScaler.getMin().castTo(FLOAT), caseB.expectedMin.castTo(FLOAT));
        Assert.assertEquals(myMinMaxScaler.getMax().castTo(FLOAT), caseB.expectedMax.castTo(FLOAT));
    }

    @Test
    public void testBruteForce3dMaskLabels() {
        NormalizerStandardize myNormalizer = new NormalizerStandardize();
        myNormalizer.fitLabel(true);
        NormalizerMinMaxScaler myMinMaxScaler = new NormalizerMinMaxScaler();
        myMinMaxScaler.fitLabel(true);
        // generating a dataset with consecutive numbers as feature values. Dataset also has masks
        int samples = 100;
        INDArray featureScale = Nd4j.create(new double[]{ 1, 2, 10 }).reshape(3, 1);
        int timeStepsU = 5;
        PreProcessor3D4DTest.Construct3dDataSet sampleU = new PreProcessor3D4DTest.Construct3dDataSet(featureScale, timeStepsU, samples, 1);
        int timeStepsV = 3;
        PreProcessor3D4DTest.Construct3dDataSet sampleV = new PreProcessor3D4DTest.Construct3dDataSet(featureScale, timeStepsV, samples, sampleU.newOrigin);
        List<DataSet> dataSetList = new ArrayList<DataSet>();
        dataSetList.add(sampleU.sampleDataSet);
        dataSetList.add(sampleV.sampleDataSet);
        DataSet fullDataSetA = DataSet.merge(dataSetList);
        DataSet fullDataSetAA = fullDataSetA.copy();
        // This should be the same datasets as above without a mask
        PreProcessor3D4DTest.Construct3dDataSet fullDataSetNoMask = new PreProcessor3D4DTest.Construct3dDataSet(featureScale, (timeStepsU + timeStepsV), samples, 1);
        // preprocessors - label and feature values are the same
        myNormalizer.fit(fullDataSetA);
        Assert.assertEquals(myNormalizer.getMean().castTo(FLOAT), fullDataSetNoMask.expectedMean.castTo(FLOAT));
        Assert.assertEquals(myNormalizer.getStd().castTo(FLOAT), fullDataSetNoMask.expectedStd.castTo(FLOAT));
        Assert.assertEquals(myNormalizer.getLabelMean().castTo(FLOAT), fullDataSetNoMask.expectedMean.castTo(FLOAT));
        Assert.assertEquals(myNormalizer.getLabelStd().castTo(FLOAT), fullDataSetNoMask.expectedStd.castTo(FLOAT));
        myMinMaxScaler.fit(fullDataSetAA);
        Assert.assertEquals(myMinMaxScaler.getMin().castTo(FLOAT), fullDataSetNoMask.expectedMin.castTo(FLOAT));
        Assert.assertEquals(myMinMaxScaler.getMax().castTo(FLOAT), fullDataSetNoMask.expectedMax.castTo(FLOAT));
        Assert.assertEquals(myMinMaxScaler.getLabelMin().castTo(FLOAT), fullDataSetNoMask.expectedMin.castTo(FLOAT));
        Assert.assertEquals(myMinMaxScaler.getLabelMax().castTo(FLOAT), fullDataSetNoMask.expectedMax.castTo(FLOAT));
        // Same Test with an Iterator, values should be close for std, exact for everything else
        DataSetIterator sampleIterA = new org.nd4j.linalg.dataset.api.iterator.TestDataSetIterator(fullDataSetA, 5);
        DataSetIterator sampleIterB = new org.nd4j.linalg.dataset.api.iterator.TestDataSetIterator(fullDataSetAA, 5);
        myNormalizer.fit(sampleIterA);
        Assert.assertEquals(myNormalizer.getMean().castTo(FLOAT), fullDataSetNoMask.expectedMean.castTo(FLOAT));
        Assert.assertEquals(myNormalizer.getLabelMean().castTo(FLOAT), fullDataSetNoMask.expectedMean.castTo(FLOAT));
        Assert.assertTrue(((Transforms.abs(myNormalizer.getStd().div(fullDataSetNoMask.expectedStd).sub(1)).maxNumber().floatValue()) < 0.01));
        Assert.assertTrue(((Transforms.abs(myNormalizer.getLabelStd().div(fullDataSetNoMask.expectedStd).sub(1)).maxNumber().floatValue()) < 0.01));
        myMinMaxScaler.fit(sampleIterB);
        Assert.assertEquals(myMinMaxScaler.getMin().castTo(FLOAT), fullDataSetNoMask.expectedMin.castTo(FLOAT));
        Assert.assertEquals(myMinMaxScaler.getMax().castTo(FLOAT), fullDataSetNoMask.expectedMax.castTo(FLOAT));
        Assert.assertEquals(myMinMaxScaler.getLabelMin().castTo(FLOAT), fullDataSetNoMask.expectedMin.castTo(FLOAT));
        Assert.assertEquals(myMinMaxScaler.getLabelMax().castTo(FLOAT), fullDataSetNoMask.expectedMax.castTo(FLOAT));
    }

    @Test
    public void testStdX() {
        INDArray array = Nd4j.create(new double[]{ 11.1, 22.2, 33.3, 44.4, 55.5, 66.6, 77.7, 88.8, 99.9, 111.0, 122.1, 133.2, 144.3, 155.4, 166.5, 177.6, 188.7, 199.8, 210.9, 222.0, 233.1, 244.2, 255.3, 266.4, 277.5, 288.6, 299.7, 310.8, 321.9, 333.0, 344.1, 355.2, 366.3, 377.4, 388.5, 399.6, 410.7, 421.8, 432.9, 444.0, 455.1, 466.2, 477.3, 488.4, 499.5, 510.6, 521.7, 532.8, 543.9, 555.0, 566.1, 577.2, 588.3, 599.4, 610.5, 621.6, 632.7, 643.8, 654.9, 666.0, 677.1, 688.2, 699.3, 710.4, 721.5, 732.6, 743.7, 754.8, 765.9, 777.0, 788.1, 799.2, 810.3, 821.4, 832.5, 843.6, 854.7, 865.8, 876.9, 888.0, 899.1, 910.2, 921.3, 932.4, 943.5, 954.6, 965.7, 976.8, 987.9, 999.0, 1, 10.1, 1, 21.2, 1, 32.3, 1, 43.4, 1, 54.5, 1, 65.6, 1, 76.7, 1, 87.8, 1, 98.9, 1, 110.0, 1, 121.1, 1, 132.2, 1, 143.3, 1, 154.4, 1, 165.5, 1, 176.6, 1, 187.7, 1, 198.8, 1, 209.9, 1, 221.0, 1, 232.1, 1, 243.2, 1, 254.3, 1, 265.4, 1, 276.5, 1, 287.6, 1, 298.7, 1, 309.8, 1, 320.9, 1, 332.0, 1, 343.1, 1, 354.2, 1, 365.3, 1, 376.4, 1, 387.5, 1, 398.6, 1, 409.7, 1, 420.8, 1, 431.9, 1, 443.0, 1, 454.1, 1, 465.2, 1, 476.3, 1, 487.4, 1, 498.5, 1, 509.6, 1, 520.7, 1, 531.8, 1, 542.9, 1, 554.0, 1, 565.1, 1, 576.2, 1, 587.3, 1, 598.4, 1, 609.5, 1, 620.6, 1, 631.7, 1, 642.8, 1, 653.9, 1, 665.0, 2.1, 4.2, 6.3, 8.4, 10.5, 12.6, 14.7, 16.8, 18.9, 21.0, 23.1, 25.2, 27.3, 29.4, 31.5, 33.6, 35.7, 37.8, 39.9, 42.0, 44.1, 46.2, 48.3, 50.4, 52.5, 54.6, 56.7, 58.8, 60.9, 63.0, 65.1, 67.2, 69.3, 71.4, 73.5, 75.6, 77.7, 79.8, 81.9, 84.0, 86.1, 88.2, 90.3, 92.4, 94.5, 96.6, 98.7, 100.8, 102.9, 105.0, 107.1, 109.2, 111.3, 113.4, 115.5, 117.6, 119.7, 121.8, 123.9, 126.0, 128.1, 130.2, 132.3, 134.4, 136.5, 138.6, 140.7, 142.8, 144.9, 147.0, 149.1, 151.2, 153.3, 155.4, 157.5, 159.6, 161.7, 163.8, 165.9, 168.0, 170.1, 172.2, 174.3, 176.4, 178.5, 180.6, 182.7, 184.8, 186.9, 189.0, 191.1, 193.2, 195.3, 197.4, 199.5, 201.6, 203.7, 205.8, 207.9, 210.0, 212.1, 214.2, 216.3, 218.4, 220.5, 222.6, 224.7, 226.8, 228.9, 231.0, 233.1, 235.2, 237.3, 239.4, 241.5, 243.6, 245.7, 247.8, 249.9, 252.0, 254.1, 256.2, 258.3, 260.4, 262.5, 264.6, 266.7, 268.8, 270.9, 273.0, 275.1, 277.2, 279.3, 281.4, 283.5, 285.6, 287.7, 289.8, 291.9, 294.0, 296.1, 298.2, 300.3, 302.4, 304.5, 306.6, 308.7, 310.8, 312.9, 315.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0, 110.0, 120.0, 130.0, 140.0, 150.0, 160.0, 170.0, 180.0, 190.0, 200.0, 210.0, 220.0, 230.0, 240.0, 250.0, 260.0, 270.0, 280.0, 290.0, 300.0, 310.0, 320.0, 330.0, 340.0, 350.0, 360.0, 370.0, 380.0, 390.0, 400.0, 410.0, 420.0, 430.0, 440.0, 450.0, 460.0, 470.0, 480.0, 490.0, 500.0, 510.0, 520.0, 530.0, 540.0, 550.0, 560.0, 570.0, 580.0, 590.0, 600.0, 610.0, 620.0, 630.0, 640.0, 650.0, 660.0, 670.0, 680.0, 690.0, 700.0, 710.0, 720.0, 730.0, 740.0, 750.0, 760.0, 770.0, 780.0, 790.0, 800.0, 810.0, 820.0, 830.0, 840.0, 850.0, 860.0, 870.0, 880.0, 890.0, 900.0, 910.0, 920.0, 930.0, 940.0, 950.0, 960.0, 970.0, 980.0, 990.0, 1, 0.0, 1, 10.0, 1, 20.0, 1, 30.0, 1, 40.0, 1, 50.0, 1, 60.0, 1, 70.0, 1, 80.0, 1, 90.0, 1, 100.0, 1, 110.0, 1, 120.0, 1, 130.0, 1, 140.0, 1, 150.0, 1, 160.0, 1, 170.0, 1, 180.0, 1, 190.0, 1, 200.0, 1, 210.0, 1, 220.0, 1, 230.0, 1, 240.0, 1, 250.0, 1, 260.0, 1, 270.0, 1, 280.0, 1, 290.0, 1, 300.0, 1, 310.0, 1, 320.0, 1, 330.0, 1, 340.0, 1, 350.0, 1, 360.0, 1, 370.0, 1, 380.0, 1, 390.0, 1, 400.0, 1, 410.0, 1, 420.0, 1, 430.0, 1, 440.0, 1, 450.0, 1, 460.0, 1, 470.0, 1, 480.0, 1, 490.0, 1, 500.0, 99.0, 198.0, 297.0, 396.0, 495.0, 594.0, 693.0, 792.0, 891.0, 990.0, 1, 89.0, 1, 188.0, 1, 287.0, 1, 386.0, 1, 485.0, 1, 584.0, 1, 683.0, 1, 782.0, 1, 881.0, 1, 980.0, 2, 79.0, 2, 178.0, 2, 277.0, 2, 376.0, 2, 475.0, 2, 574.0, 2, 673.0, 2, 772.0, 2, 871.0, 2, 970.0, 3, 69.0, 3, 168.0, 3, 267.0, 3, 366.0, 3, 465.0, 3, 564.0, 3, 663.0, 3, 762.0, 3, 861.0, 3, 960.0, 4, 59.0, 4, 158.0, 4, 257.0, 4, 356.0, 4, 455.0, 4, 554.0, 4, 653.0, 4, 752.0, 4, 851.0, 4, 950.0, 5, 49.0, 5, 148.0, 5, 247.0, 5, 346.0, 5, 445.0, 5, 544.0, 5, 643.0, 5, 742.0, 5, 841.0, 5, 940.0, 6, 39.0, 6, 138.0, 6, 237.0, 6, 336.0, 6, 435.0, 6, 534.0, 6, 633.0, 6, 732.0, 6, 831.0, 6, 930.0, 7, 29.0, 7, 128.0, 7, 227.0, 7, 326.0, 7, 425.0, 7, 524.0, 7, 623.0, 7, 722.0, 7, 821.0, 7, 920.0, 8, 19.0, 8, 118.0, 8, 217.0, 8, 316.0, 8, 415.0, 8, 514.0, 8, 613.0, 8, 712.0, 8, 811.0, 8, 910.0, 9, 9.0, 9, 108.0, 9, 207.0, 9, 306.0, 9, 405.0, 9, 504.0, 9, 603.0, 9, 702.0, 9, 801.0, 9, 900.0, 9, 999.0, 10, 98.0, 10, 197.0, 10, 296.0, 10, 395.0, 10, 494.0, 10, 593.0, 10, 692.0, 10, 791.0, 10, 890.0, 10, 989.0, 11, 88.0, 11, 187.0, 11, 286.0, 11, 385.0, 11, 484.0, 11, 583.0, 11, 682.0, 11, 781.0, 11, 880.0, 11, 979.0, 12, 78.0, 12, 177.0, 12, 276.0, 12, 375.0, 12, 474.0, 12, 573.0, 12, 672.0, 12, 771.0, 12, 870.0, 12, 969.0, 13, 68.0, 13, 167.0, 13, 266.0, 13, 365.0, 13, 464.0, 13, 563.0, 13, 662.0, 13, 761.0, 13, 860.0, 13, 959.0, 14, 58.0, 14, 157.0, 14, 256.0, 14, 355.0, 14, 454.0, 14, 553.0, 14, 652.0, 14, 751.0, 14, 850.0, 7.16, 14.31, 21.47, 28.62, 35.78, 42.94, 50.09, 57.25, 64.4, 71.56, 78.72, 85.87, 93.03, 100.18, 107.34, 114.5, 121.65, 128.81, 135.96, 143.12, 150.28, 157.43, 164.59, 171.74, 178.9, 186.06, 193.21, 200.37, 207.52, 214.68, 221.84, 228.99, 236.15, 243.3, 250.46, 257.62, 264.77, 271.93, 279.08, 286.24, 293.4, 300.55, 307.71, 314.86, 322.02, 329.18, 336.33, 343.49, 350.64, 357.8, 364.96, 372.11, 379.27, 386.42, 393.58, 400.74, 407.89, 415.05, 422.2, 429.36, 436.52, 443.67, 450.83, 457.98, 465.14, 472.3, 479.45, 486.61, 493.76, 500.92, 508.08, 515.23, 522.39, 529.54, 536.7, 543.86, 551.01, 558.17, 565.32, 572.48, 579.64, 586.79, 593.95, 601.1, 608.26, 615.42, 622.57, 629.73, 636.88, 644.04, 651.2, 658.35, 665.51, 672.66, 679.82, 686.98, 694.13, 701.29, 708.44, 715.6, 722.76, 729.91, 737.07, 744.22, 751.38, 758.54, 765.69, 772.85, 780.0, 787.16, 794.32, 801.47, 808.63, 815.78, 822.94, 830.1, 837.25, 844.41, 851.56, 858.72, 865.88, 873.03, 880.19, 887.34, 894.5, 901.66, 908.81, 915.97, 923.12, 930.28, 937.44, 944.59, 951.75, 958.9, 966.06, 973.22, 980.37, 987.53, 994.68, 1, 1.84, 1, 9.0, 1, 16.15, 1, 23.31, 1, 30.46, 1, 37.62, 1, 44.78, 1, 51.93, 1, 59.09, 1, 66.24, 1, 73.4, 9.0, 18.0, 27.0, 36.0, 45.0, 54.0, 63.0, 72.0, 81.0, 90.0, 99.0, 108.0, 117.0, 126.0, 135.0, 144.0, 153.0, 162.0, 171.0, 180.0, 189.0, 198.0, 207.0, 216.0, 225.0, 234.0, 243.0, 252.0, 261.0, 270.0, 279.0, 288.0, 297.0, 306.0, 315.0, 324.0, 333.0, 342.0, 351.0, 360.0, 369.0, 378.0, 387.0, 396.0, 405.0, 414.0, 423.0, 432.0, 441.0, 450.0, 459.0, 468.0, 477.0, 486.0, 495.0, 504.0, 513.0, 522.0, 531.0, 540.0, 549.0, 558.0, 567.0, 576.0, 585.0, 594.0, 603.0, 612.0, 621.0, 630.0, 639.0, 648.0, 657.0, 666.0, 675.0, 684.0, 693.0, 702.0, 711.0, 720.0, 729.0, 738.0, 747.0, 756.0, 765.0, 774.0, 783.0, 792.0, 801.0, 810.0, 819.0, 828.0, 837.0, 846.0, 855.0, 864.0, 873.0, 882.0, 891.0, 900.0, 909.0, 918.0, 927.0, 936.0, 945.0, 954.0, 963.0, 972.0, 981.0, 990.0, 999.0, 1, 8.0, 1, 17.0, 1, 26.0, 1, 35.0, 1, 44.0, 1, 53.0, 1, 62.0, 1, 71.0, 1, 80.0, 1, 89.0, 1, 98.0, 1, 107.0, 1, 116.0, 1, 125.0, 1, 134.0, 1, 143.0, 1, 152.0, 1, 161.0, 1, 170.0, 1, 179.0, 1, 188.0, 1, 197.0, 1, 206.0, 1, 215.0, 1, 224.0, 1, 233.0, 1, 242.0, 1, 251.0, 1, 260.0, 1, 269.0, 1, 278.0, 1, 287.0, 1, 296.0, 1, 305.0, 1, 314.0, 1, 323.0, 1, 332.0, 1, 341.0, 1, 350.0 }).reshape(1, (-1));
        float templateStd = array.std(1).getFloat(0);
        Assert.assertEquals(301.22601, templateStd, 0.01);
    }

    @Test
    public void testBruteForce4d() {
        PreProcessor3D4DTest.Construct4dDataSet imageDataSet = new PreProcessor3D4DTest.Construct4dDataSet(10, 5, 10, 15);
        NormalizerStandardize myNormalizer = new NormalizerStandardize();
        myNormalizer.fit(imageDataSet.sampleDataSet);
        Assert.assertEquals(imageDataSet.expectedMean, myNormalizer.getMean());
        float aat = Transforms.abs(myNormalizer.getStd().div(imageDataSet.expectedStd).sub(1)).maxNumber().floatValue();
        float abt = myNormalizer.getStd().maxNumber().floatValue();
        float act = imageDataSet.expectedStd.maxNumber().floatValue();
        System.out.println(("ValA: " + aat));
        System.out.println(("ValB: " + abt));
        System.out.println(("ValC: " + act));
        Assert.assertTrue((aat < 0.05));
        NormalizerMinMaxScaler myMinMaxScaler = new NormalizerMinMaxScaler();
        myMinMaxScaler.fit(imageDataSet.sampleDataSet);
        Assert.assertEquals(imageDataSet.expectedMin, myMinMaxScaler.getMin());
        Assert.assertEquals(imageDataSet.expectedMax, myMinMaxScaler.getMax());
        DataSet copyDataSet = imageDataSet.sampleDataSet.copy();
        myNormalizer.transform(copyDataSet);
    }

    @Test
    public void test3dRevertStandardize() {
        test3dRevert(new NormalizerStandardize());
    }

    @Test
    public void test3dRevertNormalize() {
        test3dRevert(new NormalizerMinMaxScaler());
    }

    @Test
    public void test3dNinMaxScaling() {
        INDArray values = Nd4j.linspace((-10), 10, 100).reshape(5, 2, 10);
        DataSet data = new DataSet(values, values);
        NormalizerMinMaxScaler SUT = new NormalizerMinMaxScaler();
        SUT.fit(data);
        SUT.preProcess(data);
        // Data should now be in a 0-1 range
        float min = data.getFeatures().minNumber().floatValue();
        float max = data.getFeatures().maxNumber().floatValue();
        Assert.assertEquals(0, min, EPS_THRESHOLD);
        Assert.assertEquals(1, max, EPS_THRESHOLD);
    }

    public class Construct3dDataSet {
        /* This will return a dataset where the features are consecutive numbers scaled by featureScaler (a column vector)
        If more than one sample is specified it will continue the series from the last sample
        If origin is not 1, the series will start from the value given
         */
        DataSet sampleDataSet;

        INDArray featureScale;

        int numFeatures;

        int maxN;

        int timeSteps;

        int samples;

        int origin;

        int newOrigin;

        INDArray expectedMean;

        INDArray expectedStd;

        INDArray expectedMin;

        INDArray expectedMax;

        public Construct3dDataSet(INDArray featureScale, int timeSteps, int samples, int origin) {
            this.featureScale = featureScale;
            this.timeSteps = timeSteps;
            this.samples = samples;
            this.origin = origin;
            // FIXME: int cast
            numFeatures = ((int) (featureScale.size(0)));
            maxN = samples * timeSteps;
            INDArray template = Nd4j.linspace(origin, ((origin + timeSteps) - 1), timeSteps).reshape(1, (-1));
            template = Nd4j.concat(0, Nd4j.linspace(origin, ((origin + timeSteps) - 1), timeSteps).reshape(1, (-1)), template);
            template = Nd4j.concat(0, Nd4j.linspace(origin, ((origin + timeSteps) - 1), timeSteps).reshape(1, (-1)), template);
            template.muliColumnVector(featureScale);
            template = template.reshape(1, numFeatures, timeSteps);
            INDArray featureMatrix = template.dup();
            int newStart = origin + timeSteps;
            int newEnd;
            for (int i = 1; i < samples; i++) {
                newEnd = (newStart + timeSteps) - 1;
                template = Nd4j.linspace(newStart, newEnd, timeSteps).reshape(1, (-1));
                template = Nd4j.concat(0, Nd4j.linspace(newStart, newEnd, timeSteps).reshape(1, (-1)), template);
                template = Nd4j.concat(0, Nd4j.linspace(newStart, newEnd, timeSteps).reshape(1, (-1)), template);
                template.muliColumnVector(featureScale);
                template = template.reshape(1, numFeatures, timeSteps);
                newStart = newEnd + 1;
                featureMatrix = Nd4j.concat(0, featureMatrix, template);
            }
            INDArray labelSet = featureMatrix.dup();
            this.newOrigin = newStart;
            sampleDataSet = new DataSet(featureMatrix, labelSet);
            // calculating stats
            // The theoretical mean should be the mean of 1,..samples*timesteps
            float theoreticalMean = (origin - 1) + (((samples * timeSteps) + 1) / 2.0F);
            expectedMean = Nd4j.create(new double[]{ theoreticalMean, theoreticalMean, theoreticalMean }).reshape(3, 1).castTo(featureScale.dataType());
            expectedMean.muliColumnVector(featureScale);
            float stdNaturalNums = ((float) (Math.sqrt((((((samples * samples) * timeSteps) * timeSteps) - 1) / 12))));
            expectedStd = Nd4j.create(new double[]{ stdNaturalNums, stdNaturalNums, stdNaturalNums }).reshape(3, 1).castTo(Nd4j.defaultFloatingPointType());
            expectedStd.muliColumnVector(Transforms.abs(featureScale, true));
            // preprocessors use the population std so divides by n not (n-1)
            expectedStd = expectedStd.dup().muli(Math.sqrt(maxN)).divi(Math.sqrt(maxN)).transpose();
            // min max assumes all scaling values are +ve
            expectedMin = Nd4j.ones(Nd4j.defaultFloatingPointType(), 3, 1).muliColumnVector(featureScale);
            expectedMax = Nd4j.ones(Nd4j.defaultFloatingPointType(), 3, 1).muli((samples * timeSteps)).muliColumnVector(featureScale);
        }
    }

    public class Construct4dDataSet {
        DataSet sampleDataSet;

        INDArray expectedMean;

        INDArray expectedStd;

        INDArray expectedMin;

        INDArray expectedMax;

        INDArray expectedLabelMean;

        INDArray expectedLabelStd;

        INDArray expectedLabelMin;

        INDArray expectedLabelMax;

        public Construct4dDataSet(int nExamples, int nChannels, int height, int width) {
            Nd4j.getRandom().setSeed(12345);
            INDArray allImages = Nd4j.rand(new int[]{ nExamples, nChannels, height, width });
            allImages.get(NDArrayIndex.all(), NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all()).muli(100).addi(200);
            allImages.get(NDArrayIndex.all(), NDArrayIndex.point(2), NDArrayIndex.all(), NDArrayIndex.all()).muli(0.01).subi(10);
            INDArray labels = Nd4j.linspace(1, nChannels, nChannels).reshape('c', nChannels, 1);
            sampleDataSet = new DataSet(allImages, labels);
            expectedMean = allImages.mean(0, 2, 3);
            expectedStd = allImages.std(0, 2, 3);
            expectedLabelMean = labels.mean(0);
            expectedLabelStd = labels.std(0);
            expectedMin = allImages.min(0, 2, 3);
            expectedMax = allImages.max(0, 2, 3);
        }
    }
}

