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
package org.deeplearning4j.spark.util;


import java.io.File;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;
import org.deeplearning4j.spark.BaseSparkTest;
import org.deeplearning4j.spark.util.data.SparkDataValidation;
import org.deeplearning4j.spark.util.data.ValidationResult;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.MultiDataSet;
import org.nd4j.linalg.factory.Nd4j;


public class TestValidation extends BaseSparkTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testDataSetValidation() throws Exception {
        File f = folder.newFolder();
        for (int i = 0; i < 3; i++) {
            DataSet ds = new DataSet(Nd4j.create(1, 10), Nd4j.create(1, 10));
            ds.save(new File(f, (i + ".bin")));
        }
        ValidationResult r = SparkDataValidation.validateDataSets(sc, f.toURI().toString());
        ValidationResult exp = ValidationResult.builder().countTotal(3).countTotalValid(3).build();
        Assert.assertEquals(exp, r);
        // Add a DataSet that is corrupt (can't be loaded)
        File f3 = new File(f, "3.bin");
        FileUtils.writeStringToFile(f3, "This isn't a DataSet!");
        r = SparkDataValidation.validateDataSets(sc, f.toURI().toString());
        exp = ValidationResult.builder().countTotal(4).countTotalValid(3).countTotalInvalid(1).countLoadingFailure(1).build();
        Assert.assertEquals(exp, r);
        f3.delete();
        // Add a DataSet with missing features:
        save(f3);
        r = SparkDataValidation.validateDataSets(sc, f.toURI().toString());
        exp = ValidationResult.builder().countTotal(4).countTotalValid(3).countTotalInvalid(1).countMissingFeatures(1).build();
        Assert.assertEquals(exp, r);
        r = SparkDataValidation.deleteInvalidDataSets(sc, f.toURI().toString());
        exp.setCountInvalidDeleted(1);
        Assert.assertEquals(exp, r);
        Assert.assertFalse(f3.exists());
        for (int i = 0; i < 3; i++) {
            Assert.assertTrue(new File(f, (i + ".bin")).exists());
        }
        // Add DataSet with incorrect labels shape:
        save(f3);
        r = SparkDataValidation.validateDataSets(sc, f.toURI().toString(), new int[]{ -1, 10 }, new int[]{ -1, 10 });
        exp = ValidationResult.builder().countTotal(4).countTotalValid(3).countTotalInvalid(1).countInvalidLabels(1).build();
        Assert.assertEquals(exp, r);
    }

    @Test
    public void testMultiDataSetValidation() throws Exception {
        File f = folder.newFolder();
        for (int i = 0; i < 3; i++) {
            MultiDataSet ds = new MultiDataSet(Nd4j.create(1, 10), Nd4j.create(1, 10));
            ds.save(new File(f, (i + ".bin")));
        }
        ValidationResult r = SparkDataValidation.validateMultiDataSets(sc, f.toURI().toString());
        ValidationResult exp = ValidationResult.builder().countTotal(3).countTotalValid(3).build();
        Assert.assertEquals(exp, r);
        // Add a MultiDataSet that is corrupt (can't be loaded)
        File f3 = new File(f, "3.bin");
        FileUtils.writeStringToFile(f3, "This isn't a MultiDataSet!");
        r = SparkDataValidation.validateMultiDataSets(sc, f.toURI().toString());
        exp = ValidationResult.builder().countTotal(4).countTotalValid(3).countTotalInvalid(1).countLoadingFailure(1).build();
        Assert.assertEquals(exp, r);
        f3.delete();
        // Add a MultiDataSet with missing features:
        save(f3);
        r = SparkDataValidation.validateMultiDataSets(sc, f.toURI().toString());
        exp = ValidationResult.builder().countTotal(4).countTotalValid(3).countTotalInvalid(1).countMissingFeatures(1).build();
        Assert.assertEquals(exp, r);
        r = SparkDataValidation.deleteInvalidMultiDataSets(sc, f.toURI().toString());
        exp.setCountInvalidDeleted(1);
        Assert.assertEquals(exp, r);
        Assert.assertFalse(f3.exists());
        for (int i = 0; i < 3; i++) {
            Assert.assertTrue(new File(f, (i + ".bin")).exists());
        }
        // Add MultiDataSet with incorrect labels shape:
        save(f3);
        r = SparkDataValidation.validateMultiDataSets(sc, f.toURI().toString(), Arrays.asList(new int[]{ -1, 10 }), Arrays.asList(new int[]{ -1, 10 }));
        exp = ValidationResult.builder().countTotal(4).countTotalValid(3).countTotalInvalid(1).countInvalidLabels(1).build();
        f3.delete();
        Assert.assertEquals(exp, r);
        // Add a MultiDataSet with incorrect number of feature arrays:
        save(f3);
        r = SparkDataValidation.validateMultiDataSets(sc, f.toURI().toString(), Arrays.asList(new int[]{ -1, 10 }), Arrays.asList(new int[]{ -1, 10 }));
        exp = ValidationResult.builder().countTotal(4).countTotalValid(3).countTotalInvalid(1).countInvalidFeatures(1).build();
        Assert.assertEquals(exp, r);
        r = SparkDataValidation.deleteInvalidMultiDataSets(sc, f.toURI().toString(), Arrays.asList(new int[]{ -1, 10 }), Arrays.asList(new int[]{ -1, 10 }));
        exp.setCountInvalidDeleted(1);
        Assert.assertEquals(exp, r);
        Assert.assertFalse(f3.exists());
    }
}

