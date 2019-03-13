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
package org.deeplearning4j.datasets.iterator;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.datasets.iterator.file.FileDataSetIterator;
import org.deeplearning4j.datasets.iterator.file.FileMultiDataSetIterator;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;
import org.nd4j.linalg.factory.Nd4j;


public class TestFileIterators extends BaseDL4JTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Rule
    public TemporaryFolder folder2 = new TemporaryFolder();

    @Test
    public void testFileDataSetIterator() throws Exception {
        folder.create();
        File f = folder.newFolder();
        DataSet d1 = new DataSet(Nd4j.linspace(1, 10, 10).reshape(10, 1), Nd4j.linspace(101, 110, 10).reshape(10, 1));
        DataSet d2 = new DataSet(Nd4j.linspace(11, 20, 10).reshape(10, 1), Nd4j.linspace(111, 120, 10).reshape(10, 1));
        DataSet d3 = new DataSet(Nd4j.linspace(21, 30, 10).reshape(10, 1), Nd4j.linspace(121, 130, 10).reshape(10, 1));
        d1.save(new File(f, "d1.bin"));
        File f2 = new File(f, "subdir/d2.bin");
        f2.getParentFile().mkdir();
        d2.save(f2);
        d3.save(new File(f, "d3.otherExt"));
        Map<Double, DataSet> exp = new HashMap<>();
        exp.put(d1.getFeatures().getDouble(0), d1);
        exp.put(d2.getFeatures().getDouble(0), d2);
        exp.put(d3.getFeatures().getDouble(0), d3);
        DataSetIterator iter = new FileDataSetIterator(f, true, null, (-1), ((String[]) (null)));
        Map<Double, DataSet> act = new HashMap<>();
        while (iter.hasNext()) {
            DataSet d = iter.next();
            act.put(d.getFeatures().getDouble(0), d);
        } 
        Assert.assertEquals(exp, act);
        // Test multiple directories
        folder2.create();
        File f2a = folder2.newFolder();
        File f2b = folder2.newFolder();
        File f2c = folder2.newFolder();
        d1.save(new File(f2a, "d1.bin"));
        d2.save(new File(f2a, "d2.bin"));
        d3.save(new File(f2b, "d3.bin"));
        d1.save(new File(f2c, "d1.bin"));
        d2.save(new File(f2c, "d2.bin"));
        d3.save(new File(f2c, "d3.bin"));
        iter = new FileDataSetIterator(f2c, true, null, (-1), ((String[]) (null)));
        DataSetIterator iterMultiDir = new FileDataSetIterator(new File[]{ f2a, f2b }, true, null, (-1), ((String[]) (null)));
        iter.reset();
        int count = 0;
        Map<Double, DataSet> iter1Out = new HashMap<>();
        Map<Double, DataSet> iter2Out = new HashMap<>();
        while (iter.hasNext()) {
            DataSet ds1 = iter.next();
            DataSet ds2 = iterMultiDir.next();
            // assertEquals(ds1, ds2);   //Iteration order may not be consistent across all platforms due to file listing order differences
            iter1Out.put(ds1.getFeatures().getDouble(0), ds1);
            iter2Out.put(ds2.getFeatures().getDouble(0), ds2);
            count++;
        } 
        Assert.assertEquals(3, count);
        Assert.assertEquals(iter1Out, iter2Out);
        // Test with extension filtering:
        exp.clear();
        exp.put(d1.getFeatures().getDouble(0), d1);
        exp.put(d2.getFeatures().getDouble(0), d2);
        iter = new FileDataSetIterator(f, true, null, (-1), "bin");
        act.clear();
        while (iter.hasNext()) {
            DataSet d = iter.next();
            act.put(d.getFeatures().getDouble(0), d);
        } 
        Assert.assertEquals(exp, act);
        // Test non-recursive
        exp.clear();
        exp.put(d1.getFeatures().getDouble(0), d1);
        exp.put(d3.getFeatures().getDouble(0), d3);
        iter = new FileDataSetIterator(f, false, null, (-1), ((String[]) (null)));
        act.clear();
        while (iter.hasNext()) {
            DataSet d = iter.next();
            act.put(d.getFeatures().getDouble(0), d);
        } 
        Assert.assertEquals(exp, act);
        // Test batch size != saved size
        f = folder.newFolder();
        d1.save(new File(f, "d1.bin"));
        d2.save(new File(f, "d2.bin"));
        d3.save(new File(f, "d3.bin"));
        /* //TODO different file iteration orders make the batch recombining hard to test...
        exp = Arrays.asList(
        new DataSet(Nd4j.linspace(1, 15, 15).reshape(10,1),
        Nd4j.linspace(101, 115, 15).reshape(10,1)),
        new DataSet(Nd4j.linspace(16, 30, 15).reshape(10,1),
        Nd4j.linspace(116, 130, 15).reshape(10,1)));
        act = new ArrayList<>();
         */
        iter = new FileDataSetIterator(f, true, null, 15, ((String[]) (null)));
        count = 0;
        while (iter.hasNext()) {
            DataSet next = iter.next();
            Assert.assertArrayEquals(new long[]{ 15, 1 }, next.getFeatures().shape());
            Assert.assertArrayEquals(new long[]{ 15, 1 }, next.getLabels().shape());
            count++;
        } 
        Assert.assertEquals(2, count);// 2x15 = 30 examples

    }

    @Test
    public void testFileMultiDataSetIterator() throws Exception {
        folder.create();
        File f = folder.newFolder();
        MultiDataSet d1 = new org.nd4j.linalg.dataset.MultiDataSet(Nd4j.linspace(1, 10, 10).reshape(10, 1), Nd4j.linspace(101, 110, 10).reshape(10, 1));
        MultiDataSet d2 = new org.nd4j.linalg.dataset.MultiDataSet(Nd4j.linspace(11, 20, 10).reshape(10, 1), Nd4j.linspace(111, 120, 10).reshape(10, 1));
        MultiDataSet d3 = new org.nd4j.linalg.dataset.MultiDataSet(Nd4j.linspace(21, 30, 10).reshape(10, 1), Nd4j.linspace(121, 130, 10).reshape(10, 1));
        d1.save(new File(f, "d1.bin"));
        File f2 = new File(f, "subdir/d2.bin");
        f2.getParentFile().mkdir();
        d2.save(f2);
        d3.save(new File(f, "d3.otherExt"));
        Map<Double, MultiDataSet> exp = new HashMap<>();
        exp.put(d1.getFeatures(0).getDouble(0), d1);
        exp.put(d2.getFeatures(0).getDouble(0), d2);
        exp.put(d3.getFeatures(0).getDouble(0), d3);
        MultiDataSetIterator iter = new FileMultiDataSetIterator(f, true, null, (-1), ((String[]) (null)));
        Map<Double, MultiDataSet> act = new HashMap<>();
        while (iter.hasNext()) {
            MultiDataSet next = iter.next();
            act.put(next.getFeatures(0).getDouble(0), next);
        } 
        Assert.assertEquals(exp, act);
        // Test multiple directories
        folder2.create();
        File f2a = folder2.newFolder();
        File f2b = folder2.newFolder();
        File f2c = folder2.newFolder();
        d1.save(new File(f2a, "d1.bin"));
        d2.save(new File(f2a, "d2.bin"));
        d3.save(new File(f2b, "d3.bin"));
        d1.save(new File(f2c, "d1.bin"));
        d2.save(new File(f2c, "d2.bin"));
        d3.save(new File(f2c, "d3.bin"));
        iter = new FileMultiDataSetIterator(f2c, true, null, (-1), ((String[]) (null)));
        MultiDataSetIterator iterMultiDir = new FileMultiDataSetIterator(new File[]{ f2a, f2b }, true, null, (-1), ((String[]) (null)));
        iter.reset();
        int count = 0;
        Map<Double, MultiDataSet> m1 = new HashMap<>();// Use maps due to possibility of file iteration order differing on some platforms

        Map<Double, MultiDataSet> m2 = new HashMap<>();
        while (iter.hasNext()) {
            MultiDataSet ds1 = iter.next();
            MultiDataSet ds2 = iterMultiDir.next();
            m1.put(ds1.getFeatures(0).getDouble(0), ds1);
            m2.put(ds2.getFeatures(0).getDouble(0), ds2);
            count++;
        } 
        Assert.assertEquals(3, count);
        Assert.assertEquals(m1, m2);
        // Test with extension filtering:
        exp = new HashMap();
        exp.put(d1.getFeatures(0).getDouble(0), d1);
        exp.put(d2.getFeatures(0).getDouble(0), d2);
        iter = new FileMultiDataSetIterator(f, true, null, (-1), "bin");
        act = new HashMap();
        while (iter.hasNext()) {
            MultiDataSet next = iter.next();
            act.put(next.getFeatures(0).getDouble(0), next);
        } 
        Assert.assertEquals(exp, act);
        // Test non-recursive
        exp = new HashMap();
        exp.put(d1.getFeatures(0).getDouble(0), d1);
        exp.put(d3.getFeatures(0).getDouble(0), d3);
        iter = new FileMultiDataSetIterator(f, false, null, (-1), ((String[]) (null)));
        act = new HashMap();
        while (iter.hasNext()) {
            MultiDataSet next = iter.next();
            act.put(next.getFeatures(0).getDouble(0), next);
        } 
        Assert.assertEquals(exp, act);
        // Test batch size != saved size
        f = folder.newFolder();
        d1.save(new File(f, "d1.bin"));
        d2.save(new File(f, "d2.bin"));
        d3.save(new File(f, "d3.bin"));
        /* //TODO different file iteration orders make the batch recombining hard to test...
        exp = Arrays.<MultiDataSet>asList(
        new org.nd4j.linalg.dataset.MultiDataSet(Nd4j.linspace(1, 15, 15).reshape(10,1),
        Nd4j.linspace(101, 115, 15).reshape(10,1)),
        new org.nd4j.linalg.dataset.MultiDataSet(Nd4j.linspace(16, 30, 15).reshape(10,1),
        Nd4j.linspace(116, 130, 15).reshape(10,1)));
        act = new ArrayList<>();
        while (iter.hasNext()) {
        act.add(iter.next());
        }
        assertEquals(exp, act);
         */
        iter = new FileMultiDataSetIterator(f, true, null, 15, ((String[]) (null)));
        count = 0;
        while (iter.hasNext()) {
            MultiDataSet next = iter.next();
            Assert.assertArrayEquals(new long[]{ 15, 1 }, next.getFeatures(0).shape());
            Assert.assertArrayEquals(new long[]{ 15, 1 }, next.getLabels(0).shape());
            count++;
        } 
        Assert.assertEquals(2, count);// 2x15 = 30 examples

    }
}

