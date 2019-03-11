/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.io.file.tfile;


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.file.tfile.TFile.Reader;
import org.apache.hadoop.io.file.tfile.TFile.Writer;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Test;


/**
 * test tfile features.
 */
public class TestTFile {
    private static String ROOT = GenericTestUtils.getTempPath("tfile-test");

    private FileSystem fs;

    private Configuration conf;

    private static final int minBlockSize = 512;

    private static final int largeVal = (3 * 1024) * 1024;

    private static final String localFormatter = "%010d";

    @Test
    public void testTFileFeatures() throws IOException {
        basicWithSomeCodec("none");
        basicWithSomeCodec("gz");
    }

    // test unsorted t files.
    @Test
    public void testUnsortedTFileFeatures() throws IOException {
        unsortedWithSomeCodec("none");
        unsortedWithSomeCodec("gz");
    }

    // test meta blocks for tfiles
    @Test
    public void testMetaBlocks() throws IOException {
        Path mFile = new Path(TestTFile.ROOT, "meta.tfile");
        FSDataOutputStream fout = createFSOutput(mFile);
        Writer writer = new Writer(fout, TestTFile.minBlockSize, "none", null, conf);
        someTestingWithMetaBlock(writer, "none");
        writer.close();
        fout.close();
        FSDataInputStream fin = fs.open(mFile);
        Reader reader = new Reader(fin, fs.getFileStatus(mFile).getLen(), conf);
        someReadingWithMetaBlock(reader);
        fs.delete(mFile, true);
        reader.close();
        fin.close();
    }
}

