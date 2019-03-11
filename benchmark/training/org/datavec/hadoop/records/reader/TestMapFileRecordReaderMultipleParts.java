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
package org.datavec.hadoop.records.reader;


import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.hadoop.fs.Path;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.api.writable.Writable;
import org.datavec.hadoop.records.reader.mapfile.IndexToKey;
import org.datavec.hadoop.records.reader.mapfile.MapFileRecordReader;
import org.datavec.hadoop.records.reader.mapfile.MapFileSequenceRecordReader;
import org.datavec.hadoop.records.reader.mapfile.index.LongIndexToKey;
import org.datavec.hadoop.records.reader.mapfile.record.RecordWritable;
import org.datavec.hadoop.records.reader.mapfile.record.SequenceRecordWritable;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.primitives.Pair;
import org.nd4j.linalg.util.MathUtils;


/**
 * Basically the same as TestMapfileRecordReader, but we have multiple parts as per say a Spark save operation
 * Paths are like
 * /part-r-00000/data
 * /part-r-00000/index
 * /part-r-00001/data
 * /part-r-00001/index
 * /part-r-00002/data
 * /part-r-00002/index
 */
public class TestMapFileRecordReaderMultipleParts {
    private static File tempDirSeq;

    private static File tempDir;

    private static Path seqMapFilePath;

    private static Path mapFilePath;

    private static Map<LongWritable, SequenceRecordWritable> seqMap;

    private static Map<LongWritable, RecordWritable> recordMap;

    @Test
    public void testSequenceRecordReader() throws Exception {
        SequenceRecordReader seqRR = new MapFileSequenceRecordReader();
        URI uri = TestMapFileRecordReaderMultipleParts.seqMapFilePath.toUri();
        InputSplit is = new FileSplit(new File(uri));
        seqRR.initialize(is);
        // Check number of records calculation
        Field f = MapFileSequenceRecordReader.class.getDeclaredField("indexToKey");
        f.setAccessible(true);
        IndexToKey itk = ((IndexToKey) (f.get(seqRR)));
        Assert.assertEquals(TestMapFileRecordReaderMultipleParts.seqMap.size(), itk.getNumRecords());
        // Check indices for each map file
        List<Pair<Long, Long>> expReaderExampleIdxs = new ArrayList<>();
        expReaderExampleIdxs.add(new Pair(0L, 2L));
        expReaderExampleIdxs.add(new Pair(3L, 5L));
        expReaderExampleIdxs.add(new Pair(6L, 8L));
        f = LongIndexToKey.class.getDeclaredField("readerIndices");
        f.setAccessible(true);
        Assert.assertEquals(expReaderExampleIdxs, f.get(itk));
        // System.out.println(f.get(itk));
        // Check standard iteration order (no randomization)
        Assert.assertTrue(seqRR.hasNext());
        int count = 0;
        while (seqRR.hasNext()) {
            List<List<Writable>> l = seqRR.sequenceRecord();
            Assert.assertEquals(TestMapFileRecordReaderMultipleParts.seqMap.get(new LongWritable(count)).getSequenceRecord(), l);
            count++;
        } 
        Assert.assertFalse(seqRR.hasNext());
        Assert.assertEquals(TestMapFileRecordReaderMultipleParts.seqMap.size(), count);
        seqRR.close();
        // Try the same thing, but with random order
        seqRR = new MapFileSequenceRecordReader(new Random(12345));
        seqRR.initialize(is);
        // Check order is defined and as expected
        f = MapFileSequenceRecordReader.class.getDeclaredField("order");
        f.setAccessible(true);
        int[] order = ((int[]) (f.get(seqRR)));
        Assert.assertNotNull(order);
        int[] expOrder = new int[9];
        for (int i = 0; i < (expOrder.length); i++) {
            expOrder[i] = i;
        }
        MathUtils.shuffleArray(expOrder, new Random(12345));
        Assert.assertArrayEquals(expOrder, order);
        // System.out.println(Arrays.toString(expOrder));
        count = 0;
        while (seqRR.hasNext()) {
            List<List<Writable>> l = seqRR.sequenceRecord();
            Assert.assertEquals(TestMapFileRecordReaderMultipleParts.seqMap.get(new LongWritable(expOrder[count])).getSequenceRecord(), l);
            count++;
        } 
    }

    @Test
    public void testRecordReaderMultipleParts() throws Exception {
        RecordReader rr = new MapFileRecordReader();
        URI uri = TestMapFileRecordReaderMultipleParts.mapFilePath.toUri();
        InputSplit is = new FileSplit(new File(uri));
        rr.initialize(is);
        // Check number of records calculation
        Field f = MapFileRecordReader.class.getDeclaredField("indexToKey");
        f.setAccessible(true);
        IndexToKey itk = ((IndexToKey) (f.get(rr)));
        Assert.assertEquals(TestMapFileRecordReaderMultipleParts.seqMap.size(), itk.getNumRecords());
        // Check indices for each map file
        List<Pair<Long, Long>> expReaderExampleIdxs = new ArrayList<>();
        expReaderExampleIdxs.add(new Pair(0L, 2L));
        expReaderExampleIdxs.add(new Pair(3L, 5L));
        expReaderExampleIdxs.add(new Pair(6L, 8L));
        f = LongIndexToKey.class.getDeclaredField("readerIndices");
        f.setAccessible(true);
        Assert.assertEquals(expReaderExampleIdxs, f.get(itk));
        Assert.assertTrue(rr.hasNext());
        int count = 0;
        while (rr.hasNext()) {
            List<Writable> l = rr.next();
            Assert.assertEquals(TestMapFileRecordReaderMultipleParts.recordMap.get(new LongWritable(count)).getRecord(), l);
            count++;
        } 
        Assert.assertEquals(TestMapFileRecordReaderMultipleParts.recordMap.size(), count);
        rr.close();
        // Try the same thing, but with random order
        rr = new MapFileRecordReader(new Random(12345));
        rr.initialize(is);
        f = MapFileRecordReader.class.getDeclaredField("order");
        f.setAccessible(true);
        int[] order = ((int[]) (f.get(rr)));
        Assert.assertNotNull(order);
        int[] expOrder = new int[9];
        for (int i = 0; i < (expOrder.length); i++) {
            expOrder[i] = i;
        }
        MathUtils.shuffleArray(expOrder, new Random(12345));
        Assert.assertArrayEquals(expOrder, order);
        count = 0;
        while (rr.hasNext()) {
            List<Writable> l = rr.next();
            Assert.assertEquals(TestMapFileRecordReaderMultipleParts.recordMap.get(new LongWritable(expOrder[count])).getRecord(), l);
            count++;
        } 
    }
}

