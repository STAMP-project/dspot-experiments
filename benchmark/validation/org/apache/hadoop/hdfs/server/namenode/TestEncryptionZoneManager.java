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


import CipherSuite.AES_CTR_NOPADDING;
import CryptoProtocolVersion.ENCRYPTION_ZONES;
import DirOp.READ_LINK;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BatchedRemoteIterator.BatchedListEntries;
import org.apache.hadoop.fs.permission.PermissionStatus;
import org.apache.hadoop.hdfs.protocol.EncryptionZone;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test class for EncryptionZoneManager methods. Added tests for
 * listEncryptionZones method, for cases where inode can and cannot have a
 * parent inode.
 */
public class TestEncryptionZoneManager {
    private FSDirectory mockedDir;

    private INodesInPath mockedINodesInPath;

    private INodeDirectory firstINode;

    private INodeDirectory secondINode;

    private INodeDirectory rootINode;

    private PermissionStatus defaultPermission;

    private EncryptionZoneManager ezManager;

    @Test
    public void testListEncryptionZonesOneValidOnly() throws Exception {
        this.ezManager = new EncryptionZoneManager(mockedDir, new Configuration());
        this.ezManager.addEncryptionZone(1L, AES_CTR_NOPADDING, ENCRYPTION_ZONES, "test_key");
        this.ezManager.addEncryptionZone(2L, AES_CTR_NOPADDING, ENCRYPTION_ZONES, "test_key");
        // sets root as proper parent for firstINode only
        this.firstINode.setParent(rootINode);
        Mockito.when(mockedDir.getINodesInPath("/first", READ_LINK)).thenReturn(mockedINodesInPath);
        Mockito.when(mockedINodesInPath.getLastINode()).thenReturn(firstINode);
        BatchedListEntries<EncryptionZone> result = ezManager.listEncryptionZones(0);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(1L, result.get(0).getId());
        Assert.assertEquals("/first", result.get(0).getPath());
    }

    @Test
    public void testListEncryptionZonesTwoValids() throws Exception {
        this.ezManager = new EncryptionZoneManager(mockedDir, new Configuration());
        this.ezManager.addEncryptionZone(1L, AES_CTR_NOPADDING, ENCRYPTION_ZONES, "test_key");
        this.ezManager.addEncryptionZone(2L, AES_CTR_NOPADDING, ENCRYPTION_ZONES, "test_key");
        // sets root as proper parent for both inodes
        this.firstINode.setParent(rootINode);
        this.secondINode.setParent(rootINode);
        Mockito.when(mockedDir.getINodesInPath("/first", READ_LINK)).thenReturn(mockedINodesInPath);
        Mockito.when(mockedINodesInPath.getLastINode()).thenReturn(firstINode);
        INodesInPath mockedINodesInPathForSecond = Mockito.mock(INodesInPath.class);
        Mockito.when(mockedDir.getINodesInPath("/second", READ_LINK)).thenReturn(mockedINodesInPathForSecond);
        Mockito.when(mockedINodesInPathForSecond.getLastINode()).thenReturn(secondINode);
        BatchedListEntries<EncryptionZone> result = ezManager.listEncryptionZones(0);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(1L, result.get(0).getId());
        Assert.assertEquals("/first", result.get(0).getPath());
        Assert.assertEquals(2L, result.get(1).getId());
        Assert.assertEquals("/second", result.get(1).getPath());
    }

    @Test
    public void testListEncryptionZonesForRoot() throws Exception {
        this.ezManager = new EncryptionZoneManager(mockedDir, new Configuration());
        this.ezManager.addEncryptionZone(0L, AES_CTR_NOPADDING, ENCRYPTION_ZONES, "test_key");
        // sets root as proper parent for firstINode only
        Mockito.when(mockedDir.getINodesInPath("/", READ_LINK)).thenReturn(mockedINodesInPath);
        Mockito.when(mockedINodesInPath.getLastINode()).thenReturn(rootINode);
        BatchedListEntries<EncryptionZone> result = ezManager.listEncryptionZones((-1));
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(0L, result.get(0).getId());
        Assert.assertEquals("/", result.get(0).getPath());
    }

    @Test
    public void testListEncryptionZonesSubDirInvalid() throws Exception {
        INodeDirectory thirdINode = new INodeDirectory(3L, "third".getBytes(), defaultPermission, System.currentTimeMillis());
        Mockito.when(this.mockedDir.getInode(3L)).thenReturn(thirdINode);
        // sets "second" as parent
        thirdINode.setParent(this.secondINode);
        this.ezManager = new EncryptionZoneManager(mockedDir, new Configuration());
        this.ezManager.addEncryptionZone(1L, AES_CTR_NOPADDING, ENCRYPTION_ZONES, "test_key");
        this.ezManager.addEncryptionZone(3L, AES_CTR_NOPADDING, ENCRYPTION_ZONES, "test_key");
        // sets root as proper parent for firstINode only,
        // leave secondINode with no parent
        this.firstINode.setParent(rootINode);
        Mockito.when(mockedDir.getINodesInPath("/first", READ_LINK)).thenReturn(mockedINodesInPath);
        Mockito.when(mockedINodesInPath.getLastINode()).thenReturn(firstINode);
        BatchedListEntries<EncryptionZone> result = ezManager.listEncryptionZones(0);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(1L, result.get(0).getId());
        Assert.assertEquals("/first", result.get(0).getPath());
    }
}

