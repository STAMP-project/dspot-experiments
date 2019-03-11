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
package org.datavec.codec.reader;


import CodecRecordReader.COLUMNS;
import CodecRecordReader.RAVEL;
import CodecRecordReader.ROWS;
import CodecRecordReader.START_FRAME;
import CodecRecordReader.TOTAL_FRAMES;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;
import org.datavec.api.conf.Configuration;
import org.datavec.api.records.SequenceRecord;
import org.datavec.api.records.metadata.RecordMetaData;
import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.writable.Writable;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.io.ClassPathResource;


/**
 *
 *
 * @author Adam Gibson
 */
public class CodecReaderTest {
    @Test
    public void testCodecReader() throws Exception {
        File file = new ClassPathResource("datavec-data-codec/fire_lowres.mp4").getFile();
        SequenceRecordReader reader = new CodecRecordReader();
        Configuration conf = new Configuration();
        conf.set(RAVEL, "true");
        conf.set(START_FRAME, "160");
        conf.set(TOTAL_FRAMES, "500");
        conf.set(ROWS, "80");
        conf.set(COLUMNS, "46");
        reader.initialize(new FileSplit(file));
        reader.setConf(conf);
        Assert.assertTrue(reader.hasNext());
        List<List<Writable>> record = reader.sequenceRecord();
        // System.out.println(record.size());
        Iterator<List<Writable>> it = record.iterator();
        List<Writable> first = it.next();
        // System.out.println(first);
        // Expected size: 80x46x3
        Assert.assertEquals(1, first.size());
        Assert.assertEquals(((80 * 46) * 3), length());
    }

    @Test
    public void testCodecReaderMeta() throws Exception {
        File file = new ClassPathResource("datavec-data-codec/fire_lowres.mp4").getFile();
        SequenceRecordReader reader = new CodecRecordReader();
        Configuration conf = new Configuration();
        conf.set(RAVEL, "true");
        conf.set(START_FRAME, "160");
        conf.set(TOTAL_FRAMES, "500");
        conf.set(ROWS, "80");
        conf.set(COLUMNS, "46");
        reader.initialize(new FileSplit(file));
        reader.setConf(conf);
        Assert.assertTrue(reader.hasNext());
        List<List<Writable>> record = reader.sequenceRecord();
        Assert.assertEquals(500, record.size());// 500 frames

        reader.reset();
        SequenceRecord seqR = reader.nextSequence();
        Assert.assertEquals(record, seqR.getSequenceRecord());
        RecordMetaData meta = seqR.getMetaData();
        // System.out.println(meta);
        Assert.assertTrue(meta.getURI().toString().endsWith(file.getName()));
        SequenceRecord fromMeta = reader.loadSequenceFromMetaData(meta);
        Assert.assertEquals(seqR, fromMeta);
    }

    @Test
    public void testViaDataInputStream() throws Exception {
        File file = new ClassPathResource("datavec-data-codec/fire_lowres.mp4").getFile();
        SequenceRecordReader reader = new CodecRecordReader();
        Configuration conf = new Configuration();
        conf.set(RAVEL, "true");
        conf.set(START_FRAME, "160");
        conf.set(TOTAL_FRAMES, "500");
        conf.set(ROWS, "80");
        conf.set(COLUMNS, "46");
        Configuration conf2 = new Configuration(conf);
        reader.initialize(new FileSplit(file));
        reader.setConf(conf);
        Assert.assertTrue(reader.hasNext());
        List<List<Writable>> expected = reader.sequenceRecord();
        SequenceRecordReader reader2 = new CodecRecordReader();
        reader2.setConf(conf2);
        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
        List<List<Writable>> actual = reader2.sequenceRecord(null, dataInputStream);
        Assert.assertEquals(expected, actual);
    }
}

