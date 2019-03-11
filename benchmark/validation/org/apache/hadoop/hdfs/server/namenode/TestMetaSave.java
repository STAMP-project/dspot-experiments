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
package org.apache.hadoop.hdfs.server.namenode;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.protocol.NamenodeProtocols;
import org.apache.hadoop.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests the creation and validation of metasave
 */
public class TestMetaSave {
    static final int NUM_DATA_NODES = 2;

    static final long seed = 3735928559L;

    static final int blockSize = 8192;

    private static MiniDFSCluster cluster = null;

    private static FileSystem fileSys = null;

    private static NamenodeProtocols nnRpc = null;

    /**
     * Tests metasave
     */
    @Test
    public void testMetaSave() throws IOException, InterruptedException, TimeoutException {
        for (int i = 0; i < 2; i++) {
            Path file = new Path(("/filestatus" + i));
            DFSTestUtil.createFile(TestMetaSave.fileSys, file, 1024, 1024, TestMetaSave.blockSize, ((short) (2)), TestMetaSave.seed);
        }
        // stop datanode and wait for namenode to discover that a datanode is dead
        stopDatanodeAndWait(1);
        TestMetaSave.nnRpc.setReplication("/filestatus0", ((short) (4)));
        TestMetaSave.nnRpc.metaSave("metasave.out.txt");
        // Verification
        FileInputStream fstream = new FileInputStream(TestMetaSave.getLogFile("metasave.out.txt"));
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = reader.readLine();
            Assert.assertEquals("3 files and directories, 2 blocks = 5 total filesystem objects", line);
            line = reader.readLine();
            Assert.assertTrue(line.equals("Live Datanodes: 1"));
            line = reader.readLine();
            Assert.assertTrue(line.equals("Dead Datanodes: 1"));
            reader.readLine();
            line = reader.readLine();
            Assert.assertTrue(line.matches("^/filestatus[01]:.*"));
        } finally {
            if (reader != null)
                reader.close();

        }
    }

    /**
     * Tests metasave after delete, to make sure there are no orphaned blocks
     */
    @Test
    public void testMetasaveAfterDelete() throws IOException, InterruptedException, TimeoutException {
        for (int i = 0; i < 2; i++) {
            Path file = new Path(("/filestatus" + i));
            DFSTestUtil.createFile(TestMetaSave.fileSys, file, 1024, 1024, TestMetaSave.blockSize, ((short) (2)), TestMetaSave.seed);
        }
        // stop datanode and wait for namenode to discover that a datanode is dead
        stopDatanodeAndWait(1);
        TestMetaSave.nnRpc.setReplication("/filestatus0", ((short) (4)));
        TestMetaSave.nnRpc.delete("/filestatus0", true);
        TestMetaSave.nnRpc.delete("/filestatus1", true);
        TestMetaSave.nnRpc.metaSave("metasaveAfterDelete.out.txt");
        // Verification
        BufferedReader reader = null;
        try {
            FileInputStream fstream = new FileInputStream(TestMetaSave.getLogFile("metasaveAfterDelete.out.txt"));
            DataInputStream in = new DataInputStream(fstream);
            reader = new BufferedReader(new InputStreamReader(in));
            reader.readLine();
            String line = reader.readLine();
            Assert.assertTrue(line.equals("Live Datanodes: 1"));
            line = reader.readLine();
            Assert.assertTrue(line.equals("Dead Datanodes: 1"));
            line = reader.readLine();
            Assert.assertTrue(line.equals("Metasave: Blocks waiting for reconstruction: 0"));
            line = reader.readLine();
            Assert.assertTrue(line.equals("Metasave: Blocks currently missing: 0"));
            line = reader.readLine();
            Assert.assertTrue(line.equals("Mis-replicated blocks that have been postponed:"));
            line = reader.readLine();
            Assert.assertTrue(line.equals("Metasave: Blocks being reconstructed: 0"));
            line = reader.readLine();
            Assert.assertTrue(line.equals("Metasave: Blocks 2 waiting deletion from 1 datanodes."));
            // skip 2 lines to reach HDFS-9033 scenario.
            line = reader.readLine();
            line = reader.readLine();
            Assert.assertTrue(line.contains("blk"));
            // skip 1 line for Corrupt Blocks section.
            line = reader.readLine();
            line = reader.readLine();
            Assert.assertTrue(line.equals("Metasave: Number of datanodes: 2"));
            line = reader.readLine();
            Assert.assertFalse(line.contains("NaN"));
        } finally {
            if (reader != null)
                reader.close();

        }
    }

    /**
     * Tests that metasave overwrites the output file (not append).
     */
    @Test
    public void testMetaSaveOverwrite() throws Exception {
        // metaSave twice.
        TestMetaSave.nnRpc.metaSave("metaSaveOverwrite.out.txt");
        TestMetaSave.nnRpc.metaSave("metaSaveOverwrite.out.txt");
        // Read output file.
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader rdr = null;
        try {
            fis = new FileInputStream(TestMetaSave.getLogFile("metaSaveOverwrite.out.txt"));
            isr = new InputStreamReader(fis);
            rdr = new BufferedReader(isr);
            // Validate that file was overwritten (not appended) by checking for
            // presence of only one "Live Datanodes" line.
            boolean foundLiveDatanodesLine = false;
            String line = rdr.readLine();
            while (line != null) {
                if (line.startsWith("Live Datanodes")) {
                    if (foundLiveDatanodesLine) {
                        Assert.fail("multiple Live Datanodes lines, output file not overwritten");
                    }
                    foundLiveDatanodesLine = true;
                }
                line = rdr.readLine();
            } 
        } finally {
            IOUtils.cleanup(null, rdr, isr, fis);
        }
    }
}

