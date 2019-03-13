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


import Compression.Algorithm.GZ;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.file.tfile.TFile.Reader;
import org.apache.hadoop.io.file.tfile.TFile.Reader.Scanner;
import org.apache.hadoop.io.file.tfile.TFile.Writer;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestTFileUnsortedByteArrays {
    private static String ROOT = GenericTestUtils.getTestDir().getAbsolutePath();

    private static final int BLOCK_SIZE = 512;

    private static final int BUF_SIZE = 64;

    private FileSystem fs;

    private Configuration conf;

    private Path path;

    private FSDataOutputStream out;

    private Writer writer;

    private String compression = GZ.getName();

    private String outputFile = "TFileTestUnsorted";

    /* pre-sampled numbers of records in one block, based on the given the
    generated key and value strings
     */
    private int records1stBlock = 4314;

    private int records2ndBlock = 4108;

    // we still can scan records in an unsorted TFile
    @Test
    public void testFailureScannerWithKeys() throws IOException {
        Reader reader = new Reader(fs.open(path), fs.getFileStatus(path).getLen(), conf);
        Assert.assertFalse(reader.isSorted());
        Assert.assertEquals(((int) (reader.getEntryCount())), 4);
        try {
            Scanner scanner = reader.createScannerByKey("aaa".getBytes(), "zzz".getBytes());
            Assert.fail("Failed to catch creating scanner with keys on unsorted file.");
        } catch (RuntimeException e) {
        } finally {
            reader.close();
        }
    }

    // we still can scan records in an unsorted TFile
    @Test
    public void testScan() throws IOException {
        Reader reader = new Reader(fs.open(path), fs.getFileStatus(path).getLen(), conf);
        Assert.assertFalse(reader.isSorted());
        Assert.assertEquals(((int) (reader.getEntryCount())), 4);
        Scanner scanner = reader.createScanner();
        try {
            // read key and value
            byte[] kbuf = new byte[TestTFileUnsortedByteArrays.BUF_SIZE];
            int klen = scanner.entry().getKeyLength();
            scanner.entry().getKey(kbuf);
            Assert.assertEquals(new String(kbuf, 0, klen), "keyZ");
            byte[] vbuf = new byte[TestTFileUnsortedByteArrays.BUF_SIZE];
            int vlen = scanner.entry().getValueLength();
            scanner.entry().getValue(vbuf);
            Assert.assertEquals(new String(vbuf, 0, vlen), "valueZ");
            scanner.advance();
            // now try get value first
            vbuf = new byte[TestTFileUnsortedByteArrays.BUF_SIZE];
            vlen = scanner.entry().getValueLength();
            scanner.entry().getValue(vbuf);
            Assert.assertEquals(new String(vbuf, 0, vlen), "valueM");
            kbuf = new byte[TestTFileUnsortedByteArrays.BUF_SIZE];
            klen = scanner.entry().getKeyLength();
            scanner.entry().getKey(kbuf);
            Assert.assertEquals(new String(kbuf, 0, klen), "keyM");
        } finally {
            scanner.close();
            reader.close();
        }
    }

    // we still can scan records in an unsorted TFile
    @Test
    public void testScanRange() throws IOException {
        Reader reader = new Reader(fs.open(path), fs.getFileStatus(path).getLen(), conf);
        Assert.assertFalse(reader.isSorted());
        Assert.assertEquals(((int) (reader.getEntryCount())), 4);
        Scanner scanner = reader.createScanner();
        try {
            // read key and value
            byte[] kbuf = new byte[TestTFileUnsortedByteArrays.BUF_SIZE];
            int klen = scanner.entry().getKeyLength();
            scanner.entry().getKey(kbuf);
            Assert.assertEquals(new String(kbuf, 0, klen), "keyZ");
            byte[] vbuf = new byte[TestTFileUnsortedByteArrays.BUF_SIZE];
            int vlen = scanner.entry().getValueLength();
            scanner.entry().getValue(vbuf);
            Assert.assertEquals(new String(vbuf, 0, vlen), "valueZ");
            scanner.advance();
            // now try get value first
            vbuf = new byte[TestTFileUnsortedByteArrays.BUF_SIZE];
            vlen = scanner.entry().getValueLength();
            scanner.entry().getValue(vbuf);
            Assert.assertEquals(new String(vbuf, 0, vlen), "valueM");
            kbuf = new byte[TestTFileUnsortedByteArrays.BUF_SIZE];
            klen = scanner.entry().getKeyLength();
            scanner.entry().getKey(kbuf);
            Assert.assertEquals(new String(kbuf, 0, klen), "keyM");
        } finally {
            scanner.close();
            reader.close();
        }
    }

    @Test
    public void testFailureSeek() throws IOException {
        Reader reader = new Reader(fs.open(path), fs.getFileStatus(path).getLen(), conf);
        Scanner scanner = reader.createScanner();
        try {
            // can't find ceil
            try {
                scanner.lowerBound("keyN".getBytes());
                Assert.fail("Cannot search in a unsorted TFile!");
            } catch (Exception e) {
                // noop, expecting excetions
            } finally {
            }
            // can't find higher
            try {
                scanner.upperBound("keyA".getBytes());
                Assert.fail("Cannot search higher in a unsorted TFile!");
            } catch (Exception e) {
                // noop, expecting excetions
            } finally {
            }
            // can't seek
            try {
                scanner.seekTo("keyM".getBytes());
                Assert.fail("Cannot search a unsorted TFile!");
            } catch (Exception e) {
                // noop, expecting excetions
            } finally {
            }
        } finally {
            scanner.close();
            reader.close();
        }
    }
}

