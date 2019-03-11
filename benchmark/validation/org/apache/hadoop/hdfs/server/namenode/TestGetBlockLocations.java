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


import DirOp.READ;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestGetBlockLocations {
    private static final String FILE_NAME = "foo";

    private static final String FILE_PATH = "/" + (TestGetBlockLocations.FILE_NAME);

    private static final long MOCK_INODE_ID = 16386;

    private static final String RESERVED_PATH = "/.reserved/.inodes/" + (TestGetBlockLocations.MOCK_INODE_ID);

    @Test(timeout = 30000)
    public void testResolveReservedPath() throws IOException {
        FSNamesystem fsn = TestGetBlockLocations.setupFileSystem();
        FSEditLog editlog = fsn.getEditLog();
        fsn.getBlockLocations("dummy", TestGetBlockLocations.RESERVED_PATH, 0, 1024);
        Mockito.verify(editlog).logTimes(ArgumentMatchers.eq(TestGetBlockLocations.FILE_PATH), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        fsn.close();
    }

    @Test(timeout = 30000)
    public void testGetBlockLocationsRacingWithDelete() throws IOException {
        FSNamesystem fsn = Mockito.spy(TestGetBlockLocations.setupFileSystem());
        final FSDirectory fsd = fsn.getFSDirectory();
        FSEditLog editlog = fsn.getEditLog();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                INodesInPath iip = fsd.getINodesInPath(TestGetBlockLocations.FILE_PATH, READ);
                FSDirDeleteOp.delete(fsd, iip, new INode.BlocksMapUpdateInfo(), new ArrayList<INode>(), new ArrayList<Long>(), now());
                invocation.callRealMethod();
                return null;
            }
        }).when(fsn).writeLock();
        fsn.getBlockLocations("dummy", TestGetBlockLocations.RESERVED_PATH, 0, 1024);
        Mockito.verify(editlog, Mockito.never()).logTimes(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        fsn.close();
    }

    @Test(timeout = 30000)
    public void testGetBlockLocationsRacingWithRename() throws IOException {
        FSNamesystem fsn = Mockito.spy(TestGetBlockLocations.setupFileSystem());
        final FSDirectory fsd = fsn.getFSDirectory();
        FSEditLog editlog = fsn.getEditLog();
        final String DST_PATH = "/bar";
        final boolean[] renamed = new boolean[1];
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                invocation.callRealMethod();
                if (!(renamed[0])) {
                    FSDirRenameOp.renameTo(fsd, fsd.getPermissionChecker(), TestGetBlockLocations.FILE_PATH, DST_PATH, new INode.BlocksMapUpdateInfo(), false);
                    renamed[0] = true;
                }
                return null;
            }
        }).when(fsn).writeLock();
        fsn.getBlockLocations("dummy", TestGetBlockLocations.RESERVED_PATH, 0, 1024);
        Mockito.verify(editlog).logTimes(ArgumentMatchers.eq(DST_PATH), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        fsn.close();
    }
}

