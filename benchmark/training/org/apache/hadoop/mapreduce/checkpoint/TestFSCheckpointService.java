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
package org.apache.hadoop.mapreduce.checkpoint;


import java.nio.ByteBuffer;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestFSCheckpointService {
    private final int BUFSIZE = 1024;

    @Test
    public void testCheckpointCreate() throws Exception {
        checkpointCreate(ByteBuffer.allocate(BUFSIZE));
    }

    @Test
    public void testCheckpointCreateDirect() throws Exception {
        checkpointCreate(ByteBuffer.allocateDirect(BUFSIZE));
    }

    @Test
    public void testDelete() throws Exception {
        FileSystem fs = Mockito.mock(FileSystem.class);
        Path chkloc = new Path("/chk/chk0");
        Mockito.when(fs.delete(ArgumentMatchers.eq(chkloc), ArgumentMatchers.eq(false))).thenReturn(true);
        Path base = new Path("/otherchk");
        FSCheckpointID id = new FSCheckpointID(chkloc);
        FSCheckpointService chk = new FSCheckpointService(fs, base, new SimpleNamingService("chk0"), ((short) (1)));
        Assert.assertTrue(chk.delete(id));
        Mockito.verify(fs).delete(ArgumentMatchers.eq(chkloc), ArgumentMatchers.eq(false));
    }
}

