/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.client.avro;


import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TestBufferedLineReader {
    private File tmpDir;

    @Test
    public void testSimpleRead() throws IOException {
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        Files.write(("file1line1\nfile1line2\nfile1line3\nfile1line4\n" + "file1line5\nfile1line6\nfile1line7\nfile1line8\n"), f1, Charsets.UTF_8);
        SimpleTextLineEventReader reader = new SimpleTextLineEventReader(new FileReader(f1));
        Assert.assertEquals("file1line1", TestSpoolingFileLineReader.bodyAsString(reader.readEvent()));
        Assert.assertEquals("file1line2", TestSpoolingFileLineReader.bodyAsString(reader.readEvent()));
        Assert.assertEquals("file1line3", TestSpoolingFileLineReader.bodyAsString(reader.readEvent()));
        Assert.assertEquals("file1line4", TestSpoolingFileLineReader.bodyAsString(reader.readEvent()));
        Assert.assertEquals("file1line5", TestSpoolingFileLineReader.bodyAsString(reader.readEvent()));
        Assert.assertEquals("file1line6", TestSpoolingFileLineReader.bodyAsString(reader.readEvent()));
        Assert.assertEquals("file1line7", TestSpoolingFileLineReader.bodyAsString(reader.readEvent()));
        Assert.assertEquals("file1line8", TestSpoolingFileLineReader.bodyAsString(reader.readEvent()));
        Assert.assertEquals(null, reader.readEvent());
    }

    @Test
    public void testBatchedReadsWithinAFile() throws IOException {
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        Files.write(("file1line1\nfile1line2\nfile1line3\nfile1line4\n" + "file1line5\nfile1line6\nfile1line7\nfile1line8\n"), f1, Charsets.UTF_8);
        SimpleTextLineEventReader reader = new SimpleTextLineEventReader(new FileReader(f1));
        List<String> out = TestSpoolingFileLineReader.bodiesAsStrings(reader.readEvents(5));
        // Make sure we got every line
        Assert.assertEquals(5, out.size());
        Assert.assertTrue(out.contains("file1line1"));
        Assert.assertTrue(out.contains("file1line2"));
        Assert.assertTrue(out.contains("file1line3"));
        Assert.assertTrue(out.contains("file1line4"));
        Assert.assertTrue(out.contains("file1line5"));
    }

    @Test
    public void testBatchedReadsAtFileBoundary() throws IOException {
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        Files.write(("file1line1\nfile1line2\nfile1line3\nfile1line4\n" + "file1line5\nfile1line6\nfile1line7\nfile1line8\n"), f1, Charsets.UTF_8);
        SimpleTextLineEventReader reader = new SimpleTextLineEventReader(new FileReader(f1));
        List<String> out = TestSpoolingFileLineReader.bodiesAsStrings(reader.readEvents(10));
        // Make sure we got exactly 8 lines
        Assert.assertEquals(8, out.size());
        Assert.assertTrue(out.contains("file1line1"));
        Assert.assertTrue(out.contains("file1line2"));
        Assert.assertTrue(out.contains("file1line3"));
        Assert.assertTrue(out.contains("file1line4"));
        Assert.assertTrue(out.contains("file1line5"));
        Assert.assertTrue(out.contains("file1line6"));
        Assert.assertTrue(out.contains("file1line7"));
        Assert.assertTrue(out.contains("file1line8"));
    }
}

