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
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.hadoop.fs.Path;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.api.writable.Writable;
import org.datavec.hadoop.records.reader.mapfile.MapFileRecordReader;
import org.datavec.hadoop.records.reader.mapfile.MapFileSequenceRecordReader;
import org.datavec.hadoop.records.reader.mapfile.record.RecordWritable;
import org.datavec.hadoop.records.reader.mapfile.record.SequenceRecordWritable;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.util.MathUtils;


/**
 * Created by Alex on 29/05/2017.
 */
public class TestMapFileRecordReader {
    private static File tempDirSeq;

    private static File tempDir;

    private static Path seqMapFilePath;

    private static Path mapFilePath;

    private static Map<LongWritable, SequenceRecordWritable> seqMap;

    private static Map<LongWritable, RecordWritable> recordMap;

    @Test
    public void testSequenceRecordReader() throws Exception {
        SequenceRecordReader seqRR = new MapFileSequenceRecordReader();
        URI uri = TestMapFileRecordReader.seqMapFilePath.toUri();
        InputSplit is = new FileSplit(new File(uri));
        seqRR.initialize(is);
        Assert.assertTrue(seqRR.hasNext());
        int count = 0;
        while (seqRR.hasNext()) {
            List<List<Writable>> l = seqRR.sequenceRecord();
            Assert.assertEquals(TestMapFileRecordReader.seqMap.get(new LongWritable(count)).getSequenceRecord(), l);
            count++;
        } 
        Assert.assertEquals(TestMapFileRecordReader.seqMap.size(), count);
        seqRR.close();
        // Try the same thing, but with random order
        seqRR = new MapFileSequenceRecordReader(new Random(12345));
        seqRR.initialize(is);
        Field f = MapFileSequenceRecordReader.class.getDeclaredField("order");
        f.setAccessible(true);
        int[] order = ((int[]) (f.get(seqRR)));
        Assert.assertNotNull(order);
        int[] expOrder = new int[]{ 0, 1, 2 };
        MathUtils.shuffleArray(expOrder, new Random(12345));
        Assert.assertArrayEquals(expOrder, order);
        count = 0;
        while (seqRR.hasNext()) {
            List<List<Writable>> l = seqRR.sequenceRecord();
            Assert.assertEquals(TestMapFileRecordReader.seqMap.get(new LongWritable(expOrder[count])).getSequenceRecord(), l);
            count++;
        } 
    }

    @Test
    public void testRecordReader() throws Exception {
        RecordReader rr = new MapFileRecordReader();
        URI uri = TestMapFileRecordReader.mapFilePath.toUri();
        InputSplit is = new FileSplit(new File(uri));
        rr.initialize(is);
        Assert.assertTrue(rr.hasNext());
        int count = 0;
        while (rr.hasNext()) {
            List<Writable> l = rr.next();
            Assert.assertEquals(TestMapFileRecordReader.recordMap.get(new LongWritable(count)).getRecord(), l);
            count++;
        } 
        Assert.assertEquals(TestMapFileRecordReader.recordMap.size(), count);
        rr.close();
        // Try the same thing, but with random order
        rr = new MapFileRecordReader(new Random(12345));
        rr.initialize(is);
        Field f = MapFileRecordReader.class.getDeclaredField("order");
        f.setAccessible(true);
        int[] order = ((int[]) (f.get(rr)));
        Assert.assertNotNull(order);
        int[] expOrder = new int[]{ 0, 1, 2 };
        MathUtils.shuffleArray(expOrder, new Random(12345));
        Assert.assertArrayEquals(expOrder, order);
        count = 0;
        while (rr.hasNext()) {
            List<Writable> l = rr.next();
            Assert.assertEquals(TestMapFileRecordReader.recordMap.get(new LongWritable(expOrder[count])).getRecord(), l);
            count++;
        } 
    }
}

