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
package org.apache.hadoop.mapred;


import IFile.Reader;
import IFile.Writer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.DefaultCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.junit.Assert;
import org.junit.Test;


public class TestIFile {
    /**
     * Create an IFile.Writer using GzipCodec since this code does not
     * have a compressor when run via the tests (ie no native libraries).
     */
    @Test
    public void testIFileWriterWithCodec() throws Exception {
        Configuration conf = new Configuration();
        FileSystem localFs = FileSystem.getLocal(conf);
        FileSystem rfs = getRaw();
        Path path = new Path(new Path("build/test.ifile"), "data");
        DefaultCodec codec = new GzipCodec();
        codec.setConf(conf);
        Writer<Text, Text> writer = new Writer<Text, Text>(conf, rfs.create(path), .class, .class, codec, null);
        writer.close();
    }

    /**
     * Same as above but create a reader.
     */
    @Test
    public void testIFileReaderWithCodec() throws Exception {
        Configuration conf = new Configuration();
        FileSystem localFs = FileSystem.getLocal(conf);
        FileSystem rfs = getRaw();
        Path path = new Path(new Path("build/test.ifile"), "data");
        DefaultCodec codec = new GzipCodec();
        codec.setConf(conf);
        FSDataOutputStream out = rfs.create(path);
        Writer<Text, Text> writer = new Writer<Text, Text>(conf, out, .class, .class, codec, null);
        writer.close();
        FSDataInputStream in = rfs.open(path);
        Reader<Text, Text> reader = new Reader<Text, Text>(conf, in, rfs.getFileStatus(path).getLen(), codec, null);
        reader.close();
        // test check sum
        byte[] ab = new byte[100];
        int readed = reader.checksumIn.readWithChecksum(ab, 0, ab.length);
        Assert.assertEquals(readed, reader.checksumIn.getChecksum().length);
    }
}

