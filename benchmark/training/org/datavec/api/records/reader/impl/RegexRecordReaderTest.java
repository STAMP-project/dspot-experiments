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
package org.datavec.api.records.reader.impl;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.datavec.api.records.Record;
import org.datavec.api.records.SequenceRecord;
import org.datavec.api.records.metadata.RecordMetaData;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.records.reader.impl.regex.RegexLineRecordReader;
import org.datavec.api.records.reader.impl.regex.RegexSequenceRecordReader;
import org.datavec.api.split.InputSplit;
import org.datavec.api.split.NumberedFileInputSplit;
import org.datavec.api.writable.Text;
import org.datavec.api.writable.Writable;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.io.ClassPathResource;


/**
 * Created by Alex on 12/04/2016.
 */
public class RegexRecordReaderTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testRegexLineRecordReader() throws Exception {
        String regex = "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}) (\\d+) ([A-Z]+) (.*)";
        RecordReader rr = new RegexLineRecordReader(regex, 1);
        rr.initialize(new org.datavec.api.split.FileSplit(new ClassPathResource("datavec-api/logtestdata/logtestfile0.txt").getFile()));
        List<Writable> exp0 = Arrays.asList(((Writable) (new Text("2016-01-01 23:59:59.001"))), new Text("1"), new Text("DEBUG"), new Text("First entry message!"));
        List<Writable> exp1 = Arrays.asList(((Writable) (new Text("2016-01-01 23:59:59.002"))), new Text("2"), new Text("INFO"), new Text("Second entry message!"));
        List<Writable> exp2 = Arrays.asList(((Writable) (new Text("2016-01-01 23:59:59.003"))), new Text("3"), new Text("WARN"), new Text("Third entry message!"));
        Assert.assertEquals(exp0, rr.next());
        Assert.assertEquals(exp1, rr.next());
        Assert.assertEquals(exp2, rr.next());
        Assert.assertFalse(rr.hasNext());
        // Test reset:
        rr.reset();
        Assert.assertEquals(exp0, rr.next());
        Assert.assertEquals(exp1, rr.next());
        Assert.assertEquals(exp2, rr.next());
        Assert.assertFalse(rr.hasNext());
    }

    @Test
    public void testRegexLineRecordReaderMeta() throws Exception {
        String regex = "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}) (\\d+) ([A-Z]+) (.*)";
        RecordReader rr = new RegexLineRecordReader(regex, 1);
        rr.initialize(new org.datavec.api.split.FileSplit(new ClassPathResource("datavec-api/logtestdata/logtestfile0.txt").getFile()));
        List<List<Writable>> list = new ArrayList<>();
        while (rr.hasNext()) {
            list.add(rr.next());
        } 
        Assert.assertEquals(3, list.size());
        List<Record> list2 = new ArrayList<>();
        List<List<Writable>> list3 = new ArrayList<>();
        List<RecordMetaData> meta = new ArrayList<>();
        rr.reset();
        int count = 1;// Start by skipping 1 line

        while (rr.hasNext()) {
            Record r = rr.nextRecord();
            list2.add(r);
            list3.add(r.getRecord());
            meta.add(r.getMetaData());
            Assert.assertEquals((count++), getLineNumber());
        } 
        List<Record> fromMeta = rr.loadFromMetaData(meta);
        Assert.assertEquals(list, list3);
        Assert.assertEquals(list2, fromMeta);
    }

    @Test
    public void testRegexSequenceRecordReader() throws Exception {
        String regex = "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}) (\\d+) ([A-Z]+) (.*)";
        ClassPathResource cpr = new ClassPathResource("datavec-api/logtestdata/");
        File f = testDir.newFolder();
        cpr.copyDirectory(f);
        String path = new File(f, "logtestfile%d.txt").getAbsolutePath();
        InputSplit is = new NumberedFileInputSplit(path, 0, 1);
        SequenceRecordReader rr = new RegexSequenceRecordReader(regex, 1);
        rr.initialize(is);
        List<List<Writable>> exp0 = new ArrayList<>();
        exp0.add(Arrays.asList(((Writable) (new Text("2016-01-01 23:59:59.001"))), new Text("1"), new Text("DEBUG"), new Text("First entry message!")));
        exp0.add(Arrays.asList(((Writable) (new Text("2016-01-01 23:59:59.002"))), new Text("2"), new Text("INFO"), new Text("Second entry message!")));
        exp0.add(Arrays.asList(((Writable) (new Text("2016-01-01 23:59:59.003"))), new Text("3"), new Text("WARN"), new Text("Third entry message!")));
        List<List<Writable>> exp1 = new ArrayList<>();
        exp1.add(Arrays.asList(((Writable) (new Text("2016-01-01 23:59:59.011"))), new Text("11"), new Text("DEBUG"), new Text("First entry message!")));
        exp1.add(Arrays.asList(((Writable) (new Text("2016-01-01 23:59:59.012"))), new Text("12"), new Text("INFO"), new Text("Second entry message!")));
        exp1.add(Arrays.asList(((Writable) (new Text("2016-01-01 23:59:59.013"))), new Text("13"), new Text("WARN"), new Text("Third entry message!")));
        Assert.assertEquals(exp0, rr.sequenceRecord());
        Assert.assertEquals(exp1, rr.sequenceRecord());
        Assert.assertFalse(rr.hasNext());
        // Test resetting:
        rr.reset();
        Assert.assertEquals(exp0, rr.sequenceRecord());
        Assert.assertEquals(exp1, rr.sequenceRecord());
        Assert.assertFalse(rr.hasNext());
    }

    @Test
    public void testRegexSequenceRecordReaderMeta() throws Exception {
        String regex = "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}) (\\d+) ([A-Z]+) (.*)";
        ClassPathResource cpr = new ClassPathResource("datavec-api/logtestdata/");
        File f = testDir.newFolder();
        cpr.copyDirectory(f);
        String path = new File(f, "logtestfile%d.txt").getAbsolutePath();
        InputSplit is = new NumberedFileInputSplit(path, 0, 1);
        SequenceRecordReader rr = new RegexSequenceRecordReader(regex, 1);
        rr.initialize(is);
        List<List<List<Writable>>> out = new ArrayList<>();
        while (rr.hasNext()) {
            out.add(rr.sequenceRecord());
        } 
        Assert.assertEquals(2, out.size());
        List<List<List<Writable>>> out2 = new ArrayList<>();
        List<SequenceRecord> out3 = new ArrayList<>();
        List<RecordMetaData> meta = new ArrayList<>();
        rr.reset();
        while (rr.hasNext()) {
            SequenceRecord seqr = rr.nextSequence();
            out2.add(seqr.getSequenceRecord());
            out3.add(seqr);
            meta.add(seqr.getMetaData());
        } 
        List<SequenceRecord> fromMeta = rr.loadSequenceFromMetaData(meta);
        Assert.assertEquals(out, out2);
        Assert.assertEquals(out3, fromMeta);
    }
}

