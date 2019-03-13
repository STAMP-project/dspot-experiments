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
package org.apache.hadoop.streaming;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.typedbytes.TypedBytesInput;
import org.junit.Assert;
import org.junit.Test;


public class TestDumpTypedBytes {
    @Test
    public void testDumping() throws Exception {
        Configuration conf = new Configuration();
        MiniDFSCluster cluster = numDataNodes(2).build();
        FileSystem fs = cluster.getFileSystem();
        PrintStream psBackup = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream psOut = new PrintStream(out);
        System.setOut(psOut);
        DumpTypedBytes dumptb = new DumpTypedBytes(conf);
        try {
            Path root = new Path("/typedbytestest");
            Assert.assertTrue(fs.mkdirs(root));
            Assert.assertTrue(fs.exists(root));
            OutputStreamWriter writer = new OutputStreamWriter(fs.create(new Path(root, "test.txt")));
            try {
                for (int i = 0; i < 100; i++) {
                    writer.write((("" + (10 * i)) + "\n"));
                }
            } finally {
                writer.close();
            }
            String[] args = new String[1];
            args[0] = "/typedbytestest";
            int ret = dumptb.run(args);
            Assert.assertEquals("Return value != 0.", 0, ret);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            TypedBytesInput tbinput = new TypedBytesInput(new DataInputStream(in));
            int counter = 0;
            Object key = tbinput.read();
            while (key != null) {
                Assert.assertEquals(Long.class, key.getClass());// offset

                Object value = tbinput.read();
                Assert.assertEquals(String.class, value.getClass());
                Assert.assertTrue("Invalid output.", (((Integer.parseInt(value.toString())) % 10) == 0));
                counter++;
                key = tbinput.read();
            } 
            Assert.assertEquals("Wrong number of outputs.", 100, counter);
        } finally {
            try {
                fs.close();
            } catch (Exception e) {
            }
            System.setOut(psBackup);
            cluster.shutdown();
        }
    }
}

