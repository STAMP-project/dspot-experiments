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
package org.datavec.image.recordreader;


import DataType.FLOAT;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.io.labels.PathLabelGenerator;
import org.datavec.api.io.labels.PathMultiLabelGenerator;
import org.datavec.api.records.Record;
import org.datavec.api.records.listener.RecordListener;
import org.datavec.api.records.listener.impl.LogRecordListener;
import org.datavec.api.records.metadata.RecordMetaData;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.writer.RecordWriter;
import org.datavec.api.split.CollectionInputSplit;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.api.writable.NDArrayWritable;
import org.datavec.api.writable.Writable;
import org.datavec.api.writable.batch.NDArrayRecordBatch;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;


/**
 * Created by Alex on 27/09/2016.
 */
public class TestImageRecordReader {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test(expected = IllegalArgumentException.class)
    public void testEmptySplit() throws IOException {
        InputSplit data = new CollectionInputSplit(new ArrayList<URI>());
        new ImageRecordReader().initialize(data, null);
    }

    @Test
    public void testMetaData() throws IOException {
        File parentDir = testDir.newFolder();
        new ClassPathResource("datavec-data-image/testimages/").copyDirectory(parentDir);
        // System.out.println(f.getAbsolutePath());
        // System.out.println(f.getParentFile().getParentFile().getAbsolutePath());
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
        ImageRecordReader rr = new ImageRecordReader(32, 32, 3, labelMaker);
        rr.initialize(new FileSplit(parentDir));
        List<List<Writable>> out = new ArrayList<>();
        while (rr.hasNext()) {
            List<Writable> l = rr.next();
            out.add(l);
            Assert.assertEquals(2, l.size());
        } 
        Assert.assertEquals(6, out.size());
        rr.reset();
        List<List<Writable>> out2 = new ArrayList<>();
        List<Record> out3 = new ArrayList<>();
        List<RecordMetaData> meta = new ArrayList<>();
        while (rr.hasNext()) {
            Record r = rr.nextRecord();
            out2.add(r.getRecord());
            out3.add(r);
            meta.add(r.getMetaData());
            // System.out.println(r.getMetaData() + "\t" + r.getRecord().get(1));
        } 
        Assert.assertEquals(out, out2);
        List<Record> fromMeta = rr.loadFromMetaData(meta);
        Assert.assertEquals(out3, fromMeta);
    }

    @Test
    public void testImageRecordReaderLabelsOrder() throws Exception {
        // Labels order should be consistent, regardless of file iteration order
        // Idea: labels order should be consistent regardless of input file order
        File f = testDir.newFolder();
        new ClassPathResource("datavec-data-image/testimages/").copyDirectory(f);
        File f0 = new File(f, "/class0/0.jpg");
        File f1 = new File(f, "/class1/A.jpg");
        List<URI> order0 = Arrays.asList(f0.toURI(), f1.toURI());
        List<URI> order1 = Arrays.asList(f1.toURI(), f0.toURI());
        ParentPathLabelGenerator labelMaker0 = new ParentPathLabelGenerator();
        ImageRecordReader rr0 = new ImageRecordReader(32, 32, 3, labelMaker0);
        rr0.initialize(new CollectionInputSplit(order0));
        ParentPathLabelGenerator labelMaker1 = new ParentPathLabelGenerator();
        ImageRecordReader rr1 = new ImageRecordReader(32, 32, 3, labelMaker1);
        rr1.initialize(new CollectionInputSplit(order1));
        List<String> labels0 = rr0.getLabels();
        List<String> labels1 = rr1.getLabels();
        // System.out.println(labels0);
        // System.out.println(labels1);
        Assert.assertEquals(labels0, labels1);
    }

    @Test
    public void testImageRecordReaderRandomization() throws Exception {
        // Order of FileSplit+ImageRecordReader should be different after reset
        // Idea: labels order should be consistent regardless of input file order
        File f0 = testDir.newFolder();
        new ClassPathResource("datavec-data-image/testimages/").copyDirectory(f0);
        FileSplit fs = new FileSplit(f0, new Random(12345));
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
        ImageRecordReader rr = new ImageRecordReader(32, 32, 3, labelMaker);
        rr.initialize(fs);
        List<List<Writable>> out1 = new ArrayList<>();
        List<File> order1 = new ArrayList<>();
        while (rr.hasNext()) {
            out1.add(rr.next());
            order1.add(rr.getCurrentFile());
        } 
        Assert.assertEquals(6, out1.size());
        Assert.assertEquals(6, order1.size());
        rr.reset();
        List<List<Writable>> out2 = new ArrayList<>();
        List<File> order2 = new ArrayList<>();
        while (rr.hasNext()) {
            out2.add(rr.next());
            order2.add(rr.getCurrentFile());
        } 
        Assert.assertEquals(6, out2.size());
        Assert.assertEquals(6, order2.size());
        Assert.assertNotEquals(out1, out2);
        Assert.assertNotEquals(order1, order2);
        // Check that different seed gives different order for the initial iteration
        FileSplit fs2 = new FileSplit(f0, new Random(999999999));
        ParentPathLabelGenerator labelMaker2 = new ParentPathLabelGenerator();
        ImageRecordReader rr2 = new ImageRecordReader(32, 32, 3, labelMaker2);
        rr2.initialize(fs2);
        List<File> order3 = new ArrayList<>();
        while (rr2.hasNext()) {
            rr2.next();
            order3.add(rr2.getCurrentFile());
        } 
        Assert.assertEquals(6, order3.size());
        Assert.assertNotEquals(order1, order3);
    }

    @Test
    public void testImageRecordReaderRegression() throws Exception {
        PathLabelGenerator regressionLabelGen = new TestImageRecordReader.TestRegressionLabelGen();
        ImageRecordReader rr = new ImageRecordReader(28, 28, 3, regressionLabelGen);
        File rootDir = testDir.newFolder();
        new ClassPathResource("datavec-data-image/testimages/").copyDirectory(rootDir);
        FileSplit fs = new FileSplit(rootDir);
        rr.initialize(fs);
        URI[] arr = fs.locations();
        Assert.assertTrue((((rr.getLabels()) == null) || (rr.getLabels().isEmpty())));
        List<Writable> expLabels = new ArrayList<>();
        for (URI u : arr) {
            String path = u.getPath();
            expLabels.add(TestImageRecordReader.testLabel(path.substring(((path.length()) - 5), path.length())));
        }
        int count = 0;
        while (rr.hasNext()) {
            List<Writable> l = rr.next();
            Assert.assertEquals(2, l.size());
            Assert.assertEquals(expLabels.get(count), l.get(1));
            count++;
        } 
        Assert.assertEquals(6, count);
        // Test batch ops:
        rr.reset();
        List<List<Writable>> b1 = rr.next(3);
        List<List<Writable>> b2 = rr.next(3);
        Assert.assertFalse(rr.hasNext());
        NDArrayRecordBatch b1a = ((NDArrayRecordBatch) (b1));
        NDArrayRecordBatch b2a = ((NDArrayRecordBatch) (b2));
        Assert.assertEquals(2, b1a.getArrays().size());
        Assert.assertEquals(2, b2a.getArrays().size());
        NDArrayWritable l1 = new NDArrayWritable(Nd4j.create(new double[]{ expLabels.get(0).toDouble(), expLabels.get(1).toDouble(), expLabels.get(2).toDouble() }, new long[]{ 3, 1 }, FLOAT));
        NDArrayWritable l2 = new NDArrayWritable(Nd4j.create(new double[]{ expLabels.get(3).toDouble(), expLabels.get(4).toDouble(), expLabels.get(5).toDouble() }, new long[]{ 3, 1 }, FLOAT));
        INDArray act1 = b1a.getArrays().get(1);
        INDArray act2 = b2a.getArrays().get(1);
        Assert.assertEquals(l1.get(), act1);
        Assert.assertEquals(l2.get(), act2);
    }

    @Test
    public void testListenerInvocationBatch() throws IOException {
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
        ImageRecordReader rr = new ImageRecordReader(32, 32, 3, labelMaker);
        File f = testDir.newFolder();
        new ClassPathResource("datavec-data-image/testimages/").copyDirectory(f);
        File parent = f;
        int numFiles = 6;
        rr.initialize(new FileSplit(parent));
        TestImageRecordReader.CountingListener counting = new TestImageRecordReader.CountingListener(new LogRecordListener());
        rr.setListeners(counting);
        rr.next((numFiles + 1));
        Assert.assertEquals(numFiles, counting.getCount());
    }

    @Test
    public void testListenerInvocationSingle() throws IOException {
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
        ImageRecordReader rr = new ImageRecordReader(32, 32, 3, labelMaker);
        File parent = testDir.newFolder();
        new ClassPathResource("datavec-data-image/testimages/class0/").copyDirectory(parent);
        int numFiles = parent.list().length;
        rr.initialize(new FileSplit(parent));
        TestImageRecordReader.CountingListener counting = new TestImageRecordReader.CountingListener(new LogRecordListener());
        rr.setListeners(counting);
        while (rr.hasNext()) {
            rr.next();
        } 
        Assert.assertEquals(numFiles, counting.getCount());
    }

    private static class TestRegressionLabelGen implements PathLabelGenerator {
        @Override
        public Writable getLabelForPath(String path) {
            String filename = path.substring(((path.length()) - 5), path.length());
            return TestImageRecordReader.testLabel(filename);
        }

        @Override
        public Writable getLabelForPath(URI uri) {
            return getLabelForPath(uri.toString());
        }

        @Override
        public boolean inferLabelClasses() {
            return false;
        }
    }

    @Test
    public void testImageRecordReaderPathMultiLabelGenerator() throws Exception {
        Nd4j.setDataType(FLOAT);
        // Assumption: 2 multi-class (one hot) classification labels: 2 and 3 classes respectively
        // PLUS single value (Writable) regression label
        PathMultiLabelGenerator multiLabelGen = new TestImageRecordReader.TestPathMultiLabelGenerator();
        ImageRecordReader rr = new ImageRecordReader(28, 28, 3, multiLabelGen);
        File rootDir = testDir.newFolder();
        new ClassPathResource("datavec-data-image/testimages/").copyDirectory(rootDir);
        FileSplit fs = new FileSplit(rootDir);
        rr.initialize(fs);
        URI[] arr = fs.locations();
        Assert.assertTrue((((rr.getLabels()) == null) || (rr.getLabels().isEmpty())));
        List<List<Writable>> expLabels = new ArrayList<>();
        for (URI u : arr) {
            String path = u.getPath();
            expLabels.add(TestImageRecordReader.testMultiLabel(path.substring(((path.length()) - 5), path.length())));
        }
        int count = 0;
        while (rr.hasNext()) {
            List<Writable> l = rr.next();
            Assert.assertEquals(4, l.size());
            for (int i = 0; i < 3; i++) {
                Assert.assertEquals(expLabels.get(count).get(i), l.get((i + 1)));
            }
            count++;
        } 
        Assert.assertEquals(6, count);
        // Test batch ops:
        rr.reset();
        List<List<Writable>> b1 = rr.next(3);
        List<List<Writable>> b2 = rr.next(3);
        Assert.assertFalse(rr.hasNext());
        NDArrayRecordBatch b1a = ((NDArrayRecordBatch) (b1));
        NDArrayRecordBatch b2a = ((NDArrayRecordBatch) (b2));
        Assert.assertEquals(4, b1a.getArrays().size());
        Assert.assertEquals(4, b2a.getArrays().size());
        NDArrayWritable l1a = new NDArrayWritable(Nd4j.vstack(get(), get(), get()));
        NDArrayWritable l1b = new NDArrayWritable(Nd4j.vstack(get(), get(), get()));
        NDArrayWritable l1c = new NDArrayWritable(Nd4j.create(new double[]{ expLabels.get(0).get(2).toDouble(), expLabels.get(1).get(2).toDouble(), expLabels.get(2).get(2).toDouble() }, new long[]{ 1, 3 }, FLOAT));
        NDArrayWritable l2a = new NDArrayWritable(Nd4j.vstack(get(), get(), get()));
        NDArrayWritable l2b = new NDArrayWritable(Nd4j.vstack(get(), get(), get()));
        NDArrayWritable l2c = new NDArrayWritable(Nd4j.create(new double[]{ expLabels.get(3).get(2).toDouble(), expLabels.get(4).get(2).toDouble(), expLabels.get(5).get(2).toDouble() }, new long[]{ 1, 3 }, FLOAT));
        Assert.assertEquals(l1a.get(), b1a.getArrays().get(1));
        Assert.assertEquals(l1b.get(), b1a.getArrays().get(2));
        Assert.assertEquals(l1c.get(), b1a.getArrays().get(3));
        Assert.assertEquals(l2a.get(), b2a.getArrays().get(1));
        Assert.assertEquals(l2b.get(), b2a.getArrays().get(2));
        Assert.assertEquals(l2c.get(), b2a.getArrays().get(3));
    }

    private static class TestPathMultiLabelGenerator implements PathMultiLabelGenerator {
        @Override
        public List<Writable> getLabels(String uriPath) {
            String filename = uriPath.substring(((uriPath.length()) - 5));
            return TestImageRecordReader.testMultiLabel(filename);
        }
    }

    private static class CountingListener implements RecordListener {
        private RecordListener listener;

        private int count = 0;

        public CountingListener(RecordListener listener) {
            this.listener = listener;
        }

        @Override
        public boolean invoked() {
            return this.listener.invoked();
        }

        @Override
        public void invoke() {
            this.listener.invoke();
        }

        @Override
        public void recordRead(RecordReader reader, Object record) {
            this.listener.recordRead(reader, record);
            (this.count)++;
        }

        @Override
        public void recordWrite(RecordWriter writer, Object record) {
            this.listener.recordWrite(writer, record);
            (this.count)++;
        }

        public int getCount() {
            return count;
        }
    }
}

