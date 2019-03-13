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
package org.datavec.nlp.reader;


import RecordReader.APPEND_LABEL;
import TfidfVectorizer.MIN_WORD_FREQUENCY;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import org.datavec.api.conf.Configuration;
import org.datavec.api.records.Record;
import org.datavec.api.split.FileSplit;
import org.datavec.api.writable.NDArrayWritable;
import org.datavec.api.writable.Writable;
import org.datavec.nlp.vectorizer.TfidfVectorizer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.io.ClassPathResource;


/**
 *
 *
 * @author Adam Gibson
 */
public class TfidfRecordReaderTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testReader() throws Exception {
        TfidfVectorizer vectorizer = new TfidfVectorizer();
        Configuration conf = new Configuration();
        conf.setInt(MIN_WORD_FREQUENCY, 1);
        conf.setBoolean(APPEND_LABEL, true);
        vectorizer.initialize(conf);
        TfidfRecordReader reader = new TfidfRecordReader();
        File f = testDir.newFolder();
        new ClassPathResource("datavec-data-nlp/labeled/").copyDirectory(f);
        reader.initialize(conf, new FileSplit(f));
        int count = 0;
        int[] labelAssertions = new int[3];
        while (reader.hasNext()) {
            Collection<Writable> record = reader.next();
            Iterator<Writable> recordIter = record.iterator();
            NDArrayWritable writable = ((NDArrayWritable) (recordIter.next()));
            labelAssertions[count] = recordIter.next().toInt();
            count++;
        } 
        Assert.assertArrayEquals(new int[]{ 0, 1, 2 }, labelAssertions);
        Assert.assertEquals(3, reader.getLabels().size());
        Assert.assertEquals(3, count);
    }

    @Test
    public void testRecordMetaData() throws Exception {
        TfidfVectorizer vectorizer = new TfidfVectorizer();
        Configuration conf = new Configuration();
        conf.setInt(MIN_WORD_FREQUENCY, 1);
        conf.setBoolean(APPEND_LABEL, true);
        vectorizer.initialize(conf);
        TfidfRecordReader reader = new TfidfRecordReader();
        File f = testDir.newFolder();
        new ClassPathResource("datavec-data-nlp/labeled/").copyDirectory(f);
        reader.initialize(conf, new FileSplit(f));
        while (reader.hasNext()) {
            Record record = reader.nextRecord();
            Assert.assertNotNull(record.getMetaData().getURI());
            Assert.assertEquals(record.getMetaData().getReaderClass(), TfidfRecordReader.class);
        } 
    }

    @Test
    public void testReadRecordFromMetaData() throws Exception {
        TfidfVectorizer vectorizer = new TfidfVectorizer();
        Configuration conf = new Configuration();
        conf.setInt(MIN_WORD_FREQUENCY, 1);
        conf.setBoolean(APPEND_LABEL, true);
        vectorizer.initialize(conf);
        TfidfRecordReader reader = new TfidfRecordReader();
        File f = testDir.newFolder();
        new ClassPathResource("datavec-data-nlp/labeled/").copyDirectory(f);
        reader.initialize(conf, new FileSplit(f));
        Record record = reader.nextRecord();
        Record reread = reader.loadFromMetaData(record.getMetaData());
        Assert.assertEquals(record.getRecord().size(), 2);
        Assert.assertEquals(reread.getRecord().size(), 2);
        Assert.assertEquals(record.getRecord().get(0), reread.getRecord().get(0));
        Assert.assertEquals(record.getRecord().get(1), reread.getRecord().get(1));
        Assert.assertEquals(record.getMetaData(), reread.getMetaData());
    }
}

