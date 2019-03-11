/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.common.blockaliasmap.impl;


import TextWriter.Options;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.server.common.FileRegion;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.compress.GzipCodec;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for the text based block format for provided block maps.
 */
public class TestTextBlockAliasMap {
    static final String OUTFILE_PATH = "hdfs://dummyServer:0000/";

    static final String OUTFILE_BASENAME = "dummyFile";

    static final Path OUTFILE = new Path(TestTextBlockAliasMap.OUTFILE_PATH, ((TestTextBlockAliasMap.OUTFILE_BASENAME) + "txt"));

    static final String BPID = "BPID-0";

    @Test
    public void testWriterOptions() throws Exception {
        TextWriter.Options opts = TextWriter.defaults();
        Assert.assertTrue((opts instanceof WriterOptions));
        WriterOptions wopts = ((WriterOptions) (opts));
        Path def = new Path(DFSConfigKeys.DFS_PROVIDED_ALIASMAP_TEXT_WRITE_DIR_DEFAULT);
        Assert.assertEquals(def, wopts.getDir());
        Assert.assertNull(wopts.getCodec());
        Path cp = new Path(TestTextBlockAliasMap.OUTFILE_PATH, (("blocks_" + (TestTextBlockAliasMap.BPID)) + ".csv"));
        opts.dirName(new Path(TestTextBlockAliasMap.OUTFILE_PATH));
        check(opts, cp, null);
        opts.codec("gzip");
        cp = new Path(TestTextBlockAliasMap.OUTFILE_PATH, (("blocks_" + (TestTextBlockAliasMap.BPID)) + ".csv.gz"));
        check(opts, cp, GzipCodec.class);
    }

    @Test
    public void testReaderOptions() throws Exception {
        TextReader.Options opts = TextReader.defaults();
        Assert.assertTrue((opts instanceof ReaderOptions));
        ReaderOptions ropts = ((ReaderOptions) (opts));
        Path cp = new Path(TestTextBlockAliasMap.OUTFILE_PATH, TextFileRegionAliasMap.fileNameFromBlockPoolID(TestTextBlockAliasMap.BPID));
        opts.filename(cp);
        check(opts, cp, null);
        cp = new Path(TestTextBlockAliasMap.OUTFILE_PATH, (("blocks_" + (TestTextBlockAliasMap.BPID)) + ".csv.gz"));
        opts.filename(cp);
        check(opts, cp, GzipCodec.class);
    }

    @Test
    public void testCSVReadWrite() throws Exception {
        final DataOutputBuffer out = new DataOutputBuffer();
        FileRegion r1 = new FileRegion(4344L, TestTextBlockAliasMap.OUTFILE, 0, 1024);
        FileRegion r2 = new FileRegion(4345L, TestTextBlockAliasMap.OUTFILE, 1024, 1024);
        FileRegion r3 = new FileRegion(4346L, TestTextBlockAliasMap.OUTFILE, 2048, 512);
        try (TextWriter csv = new TextWriter(new OutputStreamWriter(out), ",")) {
            csv.store(r1);
            csv.store(r2);
            csv.store(r3);
        }
        Iterator<FileRegion> i3;
        try (TextReader csv = new TextReader(null, null, null, ",") {
            @Override
            public InputStream createStream() {
                DataInputBuffer in = new DataInputBuffer();
                in.reset(out.getData(), 0, out.getLength());
                return in;
            }
        }) {
            Iterator<FileRegion> i1 = csv.iterator();
            Assert.assertEquals(r1, i1.next());
            Iterator<FileRegion> i2 = csv.iterator();
            Assert.assertEquals(r1, i2.next());
            Assert.assertEquals(r2, i2.next());
            Assert.assertEquals(r3, i2.next());
            Assert.assertEquals(r2, i1.next());
            Assert.assertEquals(r3, i1.next());
            Assert.assertFalse(i1.hasNext());
            Assert.assertFalse(i2.hasNext());
            i3 = csv.iterator();
        }
        try {
            i3.next();
        } catch (IllegalStateException e) {
            return;
        }
        Assert.fail("Invalid iterator");
    }

    @Test
    public void testCSVReadWriteTsv() throws Exception {
        final DataOutputBuffer out = new DataOutputBuffer();
        FileRegion r1 = new FileRegion(4344L, TestTextBlockAliasMap.OUTFILE, 0, 1024);
        FileRegion r2 = new FileRegion(4345L, TestTextBlockAliasMap.OUTFILE, 1024, 1024);
        FileRegion r3 = new FileRegion(4346L, TestTextBlockAliasMap.OUTFILE, 2048, 512);
        try (TextWriter csv = new TextWriter(new OutputStreamWriter(out), "\t")) {
            csv.store(r1);
            csv.store(r2);
            csv.store(r3);
        }
        Iterator<FileRegion> i3;
        try (TextReader csv = new TextReader(null, null, null, "\t") {
            @Override
            public InputStream createStream() {
                DataInputBuffer in = new DataInputBuffer();
                in.reset(out.getData(), 0, out.getLength());
                return in;
            }
        }) {
            Iterator<FileRegion> i1 = csv.iterator();
            Assert.assertEquals(r1, i1.next());
            Iterator<FileRegion> i2 = csv.iterator();
            Assert.assertEquals(r1, i2.next());
            Assert.assertEquals(r2, i2.next());
            Assert.assertEquals(r3, i2.next());
            Assert.assertEquals(r2, i1.next());
            Assert.assertEquals(r3, i1.next());
            Assert.assertFalse(i1.hasNext());
            Assert.assertFalse(i2.hasNext());
            i3 = csv.iterator();
        }
        try {
            i3.next();
        } catch (IllegalStateException e) {
            return;
        }
        Assert.fail("Invalid iterator");
    }
}

