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
package org.apache.hadoop.hdfs.protocolPB;


import AclEntryScope.ACCESS;
import AclEntryScope.DEFAULT;
import AclEntryType.OTHER;
import AclEntryType.USER;
import BlockChecksumType.COMPOSITE_CRC;
import BlockChecksumType.MD5CRC;
import ByteString.EMPTY;
import DFSConfigKeys.DFS_BLOCK_SIZE_DEFAULT;
import DFSConfigKeys.DFS_BYTES_PER_CHECKSUM_DEFAULT;
import DFSConfigKeys.DFS_CHECKSUM_TYPE_DEFAULT;
import DFSConfigKeys.DFS_ENCRYPT_DATA_TRANSFER_DEFAULT;
import DFSConfigKeys.DFS_REPLICATION_DEFAULT;
import DFSConfigKeys.FS_TRASH_INTERVAL_DEFAULT;
import DFSConfigKeys.IO_FILE_BUFFER_SIZE_DEFAULT;
import DataChecksum.Type;
import DataChecksum.Type.CRC32;
import DataChecksum.Type.CRC32C;
import DataChecksum.Type.NULL;
import DatanodeStorage.State;
import FsAction.ALL;
import FsAction.NONE;
import FsAction.READ_EXECUTE;
import HdfsClientConfigKeys.DFS_CLIENT_WRITE_PACKET_SIZE_DEFAULT;
import HdfsProtos.AddErasureCodingPolicyResponseProto;
import HdfsProtos.ChecksumTypeProto;
import HdfsProtos.ChecksumTypeProto.CHECKSUM_CRC32;
import HdfsProtos.ChecksumTypeProto.CHECKSUM_CRC32C;
import HdfsProtos.ChecksumTypeProto.CHECKSUM_NULL;
import HdfsProtos.DatanodeInfoProto;
import HdfsProtos.DatanodeInfoProto.Builder;
import HdfsProtos.ECSchemaProto;
import HdfsProtos.ErasureCodingPolicyProto;
import HdfsServerConstants.INVALID_TXID;
import NamenodeRoleProto.BACKUP;
import NamenodeRoleProto.CHECKPOINT;
import NamenodeRoleProto.NAMENODE;
import NodeType.NAME_NODE;
import SlowDiskReports.DiskOp.METADATA;
import SlowDiskReports.DiskOp.READ;
import SlowDiskReports.DiskOp.WRITE;
import SlowPeerReports.EMPTY_REPORT;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.protobuf.UninitializedMessageException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.fs.FsServerDefaults;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.StripedFileTestUtil;
import org.apache.hadoop.hdfs.protocol.AddErasureCodingPolicyResponse;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.BlockType;
import org.apache.hadoop.hdfs.protocol.DatanodeID;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo.DatanodeInfoBuilder;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.SystemErasureCodingPolicies;
import org.apache.hadoop.hdfs.protocol.proto.DatanodeProtocolProtos.BlockCommandProto;
import org.apache.hadoop.hdfs.protocol.proto.DatanodeProtocolProtos.BlockECReconstructionCommandProto;
import org.apache.hadoop.hdfs.protocol.proto.DatanodeProtocolProtos.BlockRecoveryCommandProto;
import org.apache.hadoop.hdfs.protocol.proto.DatanodeProtocolProtos.DatanodeRegistrationProto;
import org.apache.hadoop.hdfs.protocol.proto.HdfsProtos;
import org.apache.hadoop.hdfs.protocol.proto.HdfsProtos.BlockProto;
import org.apache.hadoop.hdfs.protocol.proto.HdfsProtos.BlockTypeProto;
import org.apache.hadoop.hdfs.protocol.proto.HdfsProtos.DatanodeIDProto;
import org.apache.hadoop.hdfs.protocol.proto.HdfsProtos.DatanodeStorageProto;
import org.apache.hadoop.hdfs.protocol.proto.HdfsProtos.ExtendedBlockProto;
import org.apache.hadoop.hdfs.protocol.proto.HdfsProtos.LocatedBlockProto;
import org.apache.hadoop.hdfs.protocol.proto.HdfsServerProtos.BlockKeyProto;
import org.apache.hadoop.hdfs.protocol.proto.HdfsServerProtos.BlockWithLocationsProto;
import org.apache.hadoop.hdfs.protocol.proto.HdfsServerProtos.BlocksWithLocationsProto;
import org.apache.hadoop.hdfs.protocol.proto.HdfsServerProtos.CheckpointSignatureProto;
import org.apache.hadoop.hdfs.protocol.proto.HdfsServerProtos.ExportedBlockKeysProto;
import org.apache.hadoop.hdfs.protocol.proto.HdfsServerProtos.NamenodeRegistrationProto;
import org.apache.hadoop.hdfs.protocol.proto.HdfsServerProtos.NamespaceInfoProto;
import org.apache.hadoop.hdfs.protocol.proto.HdfsServerProtos.RecoveringBlockProto;
import org.apache.hadoop.hdfs.protocol.proto.HdfsServerProtos.RemoteEditLogProto;
import org.apache.hadoop.hdfs.protocol.proto.HdfsServerProtos.StorageInfoProto;
import org.apache.hadoop.hdfs.security.token.block.BlockKey;
import org.apache.hadoop.hdfs.security.token.block.BlockTokenIdentifier;
import org.apache.hadoop.hdfs.security.token.block.ExportedBlockKeys;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManagerTestUtil;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeStorageInfo;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.NamenodeRole;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.NodeType;
import org.apache.hadoop.hdfs.server.common.StorageInfo;
import org.apache.hadoop.hdfs.server.namenode.CheckpointSignature;
import org.apache.hadoop.hdfs.server.protocol.BlockCommand;
import org.apache.hadoop.hdfs.server.protocol.BlockECReconstructionCommand;
import org.apache.hadoop.hdfs.server.protocol.BlockECReconstructionCommand.BlockECReconstructionInfo;
import org.apache.hadoop.hdfs.server.protocol.BlockRecoveryCommand;
import org.apache.hadoop.hdfs.server.protocol.BlockRecoveryCommand.RecoveringBlock;
import org.apache.hadoop.hdfs.server.protocol.BlocksWithLocations;
import org.apache.hadoop.hdfs.server.protocol.BlocksWithLocations.BlockWithLocations;
import org.apache.hadoop.hdfs.server.protocol.DatanodeProtocol;
import org.apache.hadoop.hdfs.server.protocol.DatanodeRegistration;
import org.apache.hadoop.hdfs.server.protocol.DatanodeStorage;
import org.apache.hadoop.hdfs.server.protocol.NamenodeRegistration;
import org.apache.hadoop.hdfs.server.protocol.NamespaceInfo;
import org.apache.hadoop.hdfs.server.protocol.RemoteEditLog;
import org.apache.hadoop.hdfs.server.protocol.SlowDiskReports;
import org.apache.hadoop.hdfs.server.protocol.SlowPeerReports;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.erasurecode.ECSchema;
import org.apache.hadoop.security.proto.SecurityProtos.TokenProto;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link PBHelper}
 */
public class TestPBHelper {
    /**
     * Used for asserting equality on doubles.
     */
    private static final double DELTA = 1.0E-6;

    @Test
    public void testGetByteString() {
        Assert.assertSame(EMPTY, PBHelperClient.getByteString(new byte[0]));
    }

    @Test
    public void testConvertNamenodeRole() {
        Assert.assertEquals(BACKUP, PBHelper.convert(NamenodeRole.BACKUP));
        Assert.assertEquals(CHECKPOINT, PBHelper.convert(NamenodeRole.CHECKPOINT));
        Assert.assertEquals(NAMENODE, PBHelper.convert(NamenodeRole.NAMENODE));
        Assert.assertEquals(NamenodeRole.BACKUP, PBHelper.convert(BACKUP));
        Assert.assertEquals(NamenodeRole.CHECKPOINT, PBHelper.convert(CHECKPOINT));
        Assert.assertEquals(NamenodeRole.NAMENODE, PBHelper.convert(NAMENODE));
    }

    @Test
    public void testConvertStoragInfo() {
        StorageInfo info = TestPBHelper.getStorageInfo(NAME_NODE);
        StorageInfoProto infoProto = PBHelper.convert(info);
        StorageInfo info2 = PBHelper.convert(infoProto, NAME_NODE);
        Assert.assertEquals(info.getClusterID(), info2.getClusterID());
        Assert.assertEquals(info.getCTime(), info2.getCTime());
        Assert.assertEquals(info.getLayoutVersion(), info2.getLayoutVersion());
        Assert.assertEquals(info.getNamespaceID(), info2.getNamespaceID());
    }

    @Test
    public void testConvertNamenodeRegistration() {
        StorageInfo info = TestPBHelper.getStorageInfo(NAME_NODE);
        NamenodeRegistration reg = new NamenodeRegistration("address:999", "http:1000", info, NamenodeRole.NAMENODE);
        NamenodeRegistrationProto regProto = PBHelper.convert(reg);
        NamenodeRegistration reg2 = PBHelper.convert(regProto);
        Assert.assertEquals(reg.getAddress(), reg2.getAddress());
        Assert.assertEquals(reg.getClusterID(), reg2.getClusterID());
        Assert.assertEquals(reg.getCTime(), reg2.getCTime());
        Assert.assertEquals(reg.getHttpAddress(), reg2.getHttpAddress());
        Assert.assertEquals(reg.getLayoutVersion(), reg2.getLayoutVersion());
        Assert.assertEquals(reg.getNamespaceID(), reg2.getNamespaceID());
        Assert.assertEquals(reg.getRegistrationID(), reg2.getRegistrationID());
        Assert.assertEquals(reg.getRole(), reg2.getRole());
        Assert.assertEquals(reg.getVersion(), reg2.getVersion());
    }

    @Test
    public void testConvertDatanodeID() {
        DatanodeID dn = DFSTestUtil.getLocalDatanodeID();
        DatanodeIDProto dnProto = PBHelperClient.convert(dn);
        DatanodeID dn2 = PBHelperClient.convert(dnProto);
        compare(dn, dn2);
    }

    @Test
    public void testConvertBlock() {
        Block b = new Block(1, 100, 3);
        BlockProto bProto = PBHelperClient.convert(b);
        Block b2 = PBHelperClient.convert(bProto);
        Assert.assertEquals(b, b2);
    }

    @Test
    public void testConvertBlockType() {
        BlockType bContiguous = BlockType.CONTIGUOUS;
        BlockTypeProto bContiguousProto = PBHelperClient.convert(bContiguous);
        BlockType bContiguous2 = PBHelperClient.convert(bContiguousProto);
        Assert.assertEquals(bContiguous, bContiguous2);
        BlockType bStriped = BlockType.STRIPED;
        BlockTypeProto bStripedProto = PBHelperClient.convert(bStriped);
        BlockType bStriped2 = PBHelperClient.convert(bStripedProto);
        Assert.assertEquals(bStriped, bStriped2);
    }

    @Test
    public void testConvertBlockWithLocations() {
        boolean[] testSuite = new boolean[]{ false, true };
        for (int i = 0; i < (testSuite.length); i++) {
            BlockWithLocations locs = TestPBHelper.getBlockWithLocations(1, testSuite[i]);
            BlockWithLocationsProto locsProto = PBHelper.convert(locs);
            BlockWithLocations locs2 = PBHelper.convert(locsProto);
            compare(locs, locs2);
        }
    }

    @Test
    public void testConvertBlocksWithLocations() {
        boolean[] testSuite = new boolean[]{ false, true };
        for (int i = 0; i < (testSuite.length); i++) {
            BlockWithLocations[] list = new BlockWithLocations[]{ TestPBHelper.getBlockWithLocations(1, testSuite[i]), TestPBHelper.getBlockWithLocations(2, testSuite[i]) };
            BlocksWithLocations locs = new BlocksWithLocations(list);
            BlocksWithLocationsProto locsProto = PBHelper.convert(locs);
            BlocksWithLocations locs2 = PBHelper.convert(locsProto);
            BlockWithLocations[] blocks = locs.getBlocks();
            BlockWithLocations[] blocks2 = locs2.getBlocks();
            Assert.assertEquals(blocks.length, blocks2.length);
            for (int j = 0; j < (blocks.length); j++) {
                compare(blocks[j], blocks2[j]);
            }
        }
    }

    @Test
    public void testConvertBlockKey() {
        BlockKey key = TestPBHelper.getBlockKey(1);
        BlockKeyProto keyProto = PBHelper.convert(key);
        BlockKey key1 = PBHelper.convert(keyProto);
        compare(key, key1);
    }

    @Test
    public void testConvertExportedBlockKeys() {
        BlockKey[] keys = new BlockKey[]{ TestPBHelper.getBlockKey(2), TestPBHelper.getBlockKey(3) };
        ExportedBlockKeys expKeys = new ExportedBlockKeys(true, 9, 10, TestPBHelper.getBlockKey(1), keys);
        ExportedBlockKeysProto expKeysProto = PBHelper.convert(expKeys);
        ExportedBlockKeys expKeys1 = PBHelper.convert(expKeysProto);
        compare(expKeys, expKeys1);
    }

    @Test
    public void testConvertCheckpointSignature() {
        CheckpointSignature s = new CheckpointSignature(TestPBHelper.getStorageInfo(NAME_NODE), "bpid", 100, 1);
        CheckpointSignatureProto sProto = PBHelper.convert(s);
        CheckpointSignature s1 = PBHelper.convert(sProto);
        Assert.assertEquals(s.getBlockpoolID(), s1.getBlockpoolID());
        Assert.assertEquals(s.getClusterID(), s1.getClusterID());
        Assert.assertEquals(s.getCTime(), s1.getCTime());
        Assert.assertEquals(s.getCurSegmentTxId(), s1.getCurSegmentTxId());
        Assert.assertEquals(s.getLayoutVersion(), s1.getLayoutVersion());
        Assert.assertEquals(s.getMostRecentCheckpointTxId(), s1.getMostRecentCheckpointTxId());
        Assert.assertEquals(s.getNamespaceID(), s1.getNamespaceID());
    }

    @Test
    public void testConvertRemoteEditLog() {
        RemoteEditLog l = new RemoteEditLog(1, 100);
        RemoteEditLogProto lProto = PBHelper.convert(l);
        RemoteEditLog l1 = PBHelper.convert(lProto);
        compare(l, l1);
    }

    @Test
    public void testConvertRemoteEditLogManifest() {
        List<RemoteEditLog> logs = new ArrayList<RemoteEditLog>();
        logs.add(new RemoteEditLog(1, 10));
        logs.add(new RemoteEditLog(11, 20));
        convertAndCheckRemoteEditLogManifest(new org.apache.hadoop.hdfs.server.protocol.RemoteEditLogManifest(logs, 20), logs, 20);
        convertAndCheckRemoteEditLogManifest(new org.apache.hadoop.hdfs.server.protocol.RemoteEditLogManifest(logs), logs, INVALID_TXID);
    }

    @Test
    public void testConvertExtendedBlock() {
        ExtendedBlock b = getExtendedBlock();
        ExtendedBlockProto bProto = PBHelperClient.convert(b);
        ExtendedBlock b1 = PBHelperClient.convert(bProto);
        Assert.assertEquals(b, b1);
        b.setBlockId((-1));
        bProto = PBHelperClient.convert(b);
        b1 = PBHelperClient.convert(bProto);
        Assert.assertEquals(b, b1);
    }

    @Test
    public void testConvertRecoveringBlock() {
        DatanodeInfo di1 = DFSTestUtil.getLocalDatanodeInfo();
        DatanodeInfo di2 = DFSTestUtil.getLocalDatanodeInfo();
        DatanodeInfo[] dnInfo = new DatanodeInfo[]{ di1, di2 };
        RecoveringBlock b = new RecoveringBlock(getExtendedBlock(), dnInfo, 3);
        RecoveringBlockProto bProto = PBHelper.convert(b);
        RecoveringBlock b1 = PBHelper.convert(bProto);
        Assert.assertEquals(b.getBlock(), b1.getBlock());
        DatanodeInfo[] dnInfo1 = b1.getLocations();
        Assert.assertEquals(dnInfo.length, dnInfo1.length);
        for (int i = 0; i < (dnInfo.length); i++) {
            compare(dnInfo[0], dnInfo1[0]);
        }
    }

    @Test
    public void testConvertBlockRecoveryCommand() {
        DatanodeInfo di1 = DFSTestUtil.getLocalDatanodeInfo();
        DatanodeInfo di2 = DFSTestUtil.getLocalDatanodeInfo();
        DatanodeInfo[] dnInfo = new DatanodeInfo[]{ di1, di2 };
        List<RecoveringBlock> blks = ImmutableList.of(new RecoveringBlock(getExtendedBlock(1), dnInfo, 3), new RecoveringBlock(getExtendedBlock(2), dnInfo, 3));
        BlockRecoveryCommand cmd = new BlockRecoveryCommand(blks);
        BlockRecoveryCommandProto proto = PBHelper.convert(cmd);
        Assert.assertEquals(1, proto.getBlocks(0).getBlock().getB().getBlockId());
        Assert.assertEquals(2, proto.getBlocks(1).getBlock().getB().getBlockId());
        BlockRecoveryCommand cmd2 = PBHelper.convert(proto);
        List<RecoveringBlock> cmd2Blks = Lists.newArrayList(cmd2.getRecoveringBlocks());
        Assert.assertEquals(blks.get(0).getBlock(), cmd2Blks.get(0).getBlock());
        Assert.assertEquals(blks.get(1).getBlock(), cmd2Blks.get(1).getBlock());
        Assert.assertEquals(Joiner.on(",").join(blks), Joiner.on(",").join(cmd2Blks));
        Assert.assertEquals(cmd.toString(), cmd2.toString());
    }

    @Test
    public void testConvertText() {
        Text t = new Text("abc".getBytes());
        String s = t.toString();
        Text t1 = new Text(s);
        Assert.assertEquals(t, t1);
    }

    @Test
    public void testConvertBlockToken() {
        Token<BlockTokenIdentifier> token = new Token<BlockTokenIdentifier>("identifier".getBytes(), "password".getBytes(), new Text("kind"), new Text("service"));
        TokenProto tokenProto = PBHelperClient.convert(token);
        Token<BlockTokenIdentifier> token2 = PBHelperClient.convert(tokenProto);
        compare(token, token2);
    }

    @Test
    public void testConvertNamespaceInfo() {
        NamespaceInfo info = new NamespaceInfo(37, "clusterID", "bpID", 2300);
        NamespaceInfoProto proto = PBHelper.convert(info);
        NamespaceInfo info2 = PBHelper.convert(proto);
        compare(info, info2);// Compare the StorageInfo

        Assert.assertEquals(info.getBlockPoolID(), info2.getBlockPoolID());
        Assert.assertEquals(info.getBuildVersion(), info2.getBuildVersion());
    }

    @Test
    public void testConvertLocatedBlock() {
        LocatedBlock lb = createLocatedBlock();
        LocatedBlockProto lbProto = PBHelperClient.convertLocatedBlock(lb);
        LocatedBlock lb2 = PBHelperClient.convertLocatedBlockProto(lbProto);
        compare(lb, lb2);
    }

    @Test
    public void testConvertLocatedBlockNoStorageMedia() {
        LocatedBlock lb = createLocatedBlockNoStorageMedia();
        LocatedBlockProto lbProto = PBHelperClient.convertLocatedBlock(lb);
        LocatedBlock lb2 = PBHelperClient.convertLocatedBlockProto(lbProto);
        compare(lb, lb2);
    }

    @Test
    public void testConvertLocatedBlockList() {
        ArrayList<LocatedBlock> lbl = new ArrayList<LocatedBlock>();
        for (int i = 0; i < 3; i++) {
            lbl.add(createLocatedBlock());
        }
        List<LocatedBlockProto> lbpl = PBHelperClient.convertLocatedBlocks2(lbl);
        List<LocatedBlock> lbl2 = PBHelperClient.convertLocatedBlocks(lbpl);
        Assert.assertEquals(lbl.size(), lbl2.size());
        for (int i = 0; i < (lbl.size()); i++) {
            compare(lbl.get(i), lbl2.get(2));
        }
    }

    @Test
    public void testConvertLocatedBlockArray() {
        LocatedBlock[] lbl = new LocatedBlock[3];
        for (int i = 0; i < 3; i++) {
            lbl[i] = createLocatedBlock();
        }
        LocatedBlockProto[] lbpl = PBHelperClient.convertLocatedBlocks(lbl);
        LocatedBlock[] lbl2 = PBHelperClient.convertLocatedBlocks(lbpl);
        Assert.assertEquals(lbl.length, lbl2.length);
        for (int i = 0; i < (lbl.length); i++) {
            compare(lbl[i], lbl2[i]);
        }
    }

    @Test
    public void testConvertDatanodeRegistration() {
        DatanodeID dnId = DFSTestUtil.getLocalDatanodeID();
        BlockKey[] keys = new BlockKey[]{ TestPBHelper.getBlockKey(2), TestPBHelper.getBlockKey(3) };
        ExportedBlockKeys expKeys = new ExportedBlockKeys(true, 9, 10, TestPBHelper.getBlockKey(1), keys);
        DatanodeRegistration reg = new DatanodeRegistration(dnId, new StorageInfo(NodeType.DATA_NODE), expKeys, "3.0.0");
        DatanodeRegistrationProto proto = PBHelper.convert(reg);
        DatanodeRegistration reg2 = PBHelper.convert(proto);
        compare(reg.getStorageInfo(), reg2.getStorageInfo());
        compare(reg.getExportedKeys(), reg2.getExportedKeys());
        compare(reg, reg2);
        Assert.assertEquals(reg.getSoftwareVersion(), reg2.getSoftwareVersion());
    }

    @Test
    public void TestConvertDatanodeStorage() {
        DatanodeStorage dns1 = new DatanodeStorage("id1", State.NORMAL, StorageType.SSD);
        DatanodeStorageProto proto = PBHelperClient.convert(dns1);
        DatanodeStorage dns2 = PBHelperClient.convert(proto);
        compare(dns1, dns2);
    }

    @Test
    public void testConvertBlockCommand() {
        Block[] blocks = new Block[]{ new Block(21), new Block(22) };
        DatanodeInfo[][] dnInfos = new DatanodeInfo[][]{ new DatanodeInfo[1], new DatanodeInfo[2] };
        dnInfos[0][0] = DFSTestUtil.getLocalDatanodeInfo();
        dnInfos[1][0] = DFSTestUtil.getLocalDatanodeInfo();
        dnInfos[1][1] = DFSTestUtil.getLocalDatanodeInfo();
        String[][] storageIDs = new String[][]{ new String[]{ "s00" }, new String[]{ "s10", "s11" } };
        StorageType[][] storageTypes = new StorageType[][]{ new StorageType[]{ StorageType.DEFAULT }, new StorageType[]{ StorageType.DEFAULT, StorageType.DEFAULT } };
        BlockCommand bc = new BlockCommand(DatanodeProtocol.DNA_TRANSFER, "bp1", blocks, dnInfos, storageTypes, storageIDs);
        BlockCommandProto bcProto = PBHelper.convert(bc);
        BlockCommand bc2 = PBHelper.convert(bcProto);
        Assert.assertEquals(bc.getAction(), bc2.getAction());
        Assert.assertEquals(bc.getBlocks().length, bc2.getBlocks().length);
        Block[] blocks2 = bc2.getBlocks();
        for (int i = 0; i < (blocks.length); i++) {
            Assert.assertEquals(blocks[i], blocks2[i]);
        }
        DatanodeInfo[][] dnInfos2 = bc2.getTargets();
        Assert.assertEquals(dnInfos.length, dnInfos2.length);
        for (int i = 0; i < (dnInfos.length); i++) {
            DatanodeInfo[] d1 = dnInfos[i];
            DatanodeInfo[] d2 = dnInfos2[i];
            Assert.assertEquals(d1.length, d2.length);
            for (int j = 0; j < (d1.length); j++) {
                compare(d1[j], d2[j]);
            }
        }
    }

    @Test
    public void testChecksumTypeProto() {
        Assert.assertEquals(NULL, PBHelperClient.convert(CHECKSUM_NULL));
        Assert.assertEquals(CRC32, PBHelperClient.convert(CHECKSUM_CRC32));
        Assert.assertEquals(CRC32C, PBHelperClient.convert(CHECKSUM_CRC32C));
        Assert.assertEquals(PBHelperClient.convert(NULL), CHECKSUM_NULL);
        Assert.assertEquals(PBHelperClient.convert(CRC32), CHECKSUM_CRC32);
        Assert.assertEquals(PBHelperClient.convert(CRC32C), CHECKSUM_CRC32C);
    }

    @Test
    public void testBlockChecksumTypeProto() {
        Assert.assertEquals(MD5CRC, PBHelperClient.convert(HdfsProtos.BlockChecksumTypeProto.MD5CRC));
        Assert.assertEquals(COMPOSITE_CRC, PBHelperClient.convert(HdfsProtos.BlockChecksumTypeProto.COMPOSITE_CRC));
        Assert.assertEquals(PBHelperClient.convert(MD5CRC), HdfsProtos.BlockChecksumTypeProto.MD5CRC);
        Assert.assertEquals(PBHelperClient.convert(COMPOSITE_CRC), HdfsProtos.BlockChecksumTypeProto.COMPOSITE_CRC);
    }

    @Test
    public void testAclEntryProto() {
        // All fields populated.
        AclEntry e1 = new AclEntry.Builder().setName("test").setPermission(READ_EXECUTE).setScope(DEFAULT).setType(OTHER).build();
        // No name.
        AclEntry e2 = new AclEntry.Builder().setScope(ACCESS).setType(USER).setPermission(ALL).build();
        // No permission, which will default to the 0'th enum element.
        AclEntry e3 = new AclEntry.Builder().setScope(ACCESS).setType(USER).setName("test").build();
        AclEntry[] expected = new AclEntry[]{ e1, e2, new AclEntry.Builder().setScope(e3.getScope()).setType(e3.getType()).setName(e3.getName()).setPermission(NONE).build() };
        AclEntry[] actual = Lists.newArrayList(PBHelperClient.convertAclEntry(PBHelperClient.convertAclEntryProto(Lists.newArrayList(e1, e2, e3)))).toArray(new AclEntry[0]);
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testAclStatusProto() {
        AclEntry e = new AclEntry.Builder().setName("test").setPermission(READ_EXECUTE).setScope(DEFAULT).setType(OTHER).build();
        AclStatus s = new AclStatus.Builder().owner("foo").group("bar").addEntry(e).build();
        Assert.assertEquals(s, PBHelperClient.convert(PBHelperClient.convert(s)));
    }

    @Test
    public void testBlockECRecoveryCommand() {
        DatanodeInfo[] dnInfos0 = new DatanodeInfo[]{ DFSTestUtil.getLocalDatanodeInfo(), DFSTestUtil.getLocalDatanodeInfo() };
        DatanodeStorageInfo targetDnInfos_0 = BlockManagerTestUtil.newDatanodeStorageInfo(DFSTestUtil.getLocalDatanodeDescriptor(), new DatanodeStorage("s00"));
        DatanodeStorageInfo targetDnInfos_1 = BlockManagerTestUtil.newDatanodeStorageInfo(DFSTestUtil.getLocalDatanodeDescriptor(), new DatanodeStorage("s01"));
        DatanodeStorageInfo[] targetDnInfos0 = new DatanodeStorageInfo[]{ targetDnInfos_0, targetDnInfos_1 };
        byte[] liveBlkIndices0 = new byte[2];
        BlockECReconstructionInfo blkECRecoveryInfo0 = new BlockECReconstructionInfo(new ExtendedBlock("bp1", 1234), dnInfos0, targetDnInfos0, liveBlkIndices0, StripedFileTestUtil.getDefaultECPolicy());
        DatanodeInfo[] dnInfos1 = new DatanodeInfo[]{ DFSTestUtil.getLocalDatanodeInfo(), DFSTestUtil.getLocalDatanodeInfo() };
        DatanodeStorageInfo targetDnInfos_2 = BlockManagerTestUtil.newDatanodeStorageInfo(DFSTestUtil.getLocalDatanodeDescriptor(), new DatanodeStorage("s02"));
        DatanodeStorageInfo targetDnInfos_3 = BlockManagerTestUtil.newDatanodeStorageInfo(DFSTestUtil.getLocalDatanodeDescriptor(), new DatanodeStorage("s03"));
        DatanodeStorageInfo[] targetDnInfos1 = new DatanodeStorageInfo[]{ targetDnInfos_2, targetDnInfos_3 };
        byte[] liveBlkIndices1 = new byte[2];
        BlockECReconstructionInfo blkECRecoveryInfo1 = new BlockECReconstructionInfo(new ExtendedBlock("bp2", 3256), dnInfos1, targetDnInfos1, liveBlkIndices1, StripedFileTestUtil.getDefaultECPolicy());
        List<BlockECReconstructionInfo> blkRecoveryInfosList = new ArrayList<BlockECReconstructionInfo>();
        blkRecoveryInfosList.add(blkECRecoveryInfo0);
        blkRecoveryInfosList.add(blkECRecoveryInfo1);
        BlockECReconstructionCommand blkECReconstructionCmd = new BlockECReconstructionCommand(DatanodeProtocol.DNA_ERASURE_CODING_RECONSTRUCTION, blkRecoveryInfosList);
        BlockECReconstructionCommandProto blkECRecoveryCmdProto = PBHelper.convert(blkECReconstructionCmd);
        blkECReconstructionCmd = PBHelper.convert(blkECRecoveryCmdProto);
        Iterator<BlockECReconstructionInfo> iterator = blkECReconstructionCmd.getECTasks().iterator();
        assertBlockECRecoveryInfoEquals(blkECRecoveryInfo0, iterator.next());
        assertBlockECRecoveryInfoEquals(blkECRecoveryInfo1, iterator.next());
    }

    @Test
    public void testDataNodeInfoPBHelper() {
        DatanodeID id = DFSTestUtil.getLocalDatanodeID();
        DatanodeInfo dnInfos0 = new DatanodeInfoBuilder().setNodeID(id).build();
        dnInfos0.setCapacity(3500L);
        dnInfos0.setDfsUsed(1000L);
        dnInfos0.setNonDfsUsed(2000L);
        dnInfos0.setRemaining(500L);
        HdfsProtos.DatanodeInfoProto dnproto = PBHelperClient.convert(dnInfos0);
        DatanodeInfo dnInfos1 = PBHelperClient.convert(dnproto);
        compare(dnInfos0, dnInfos1);
        Assert.assertEquals(dnInfos0.getNonDfsUsed(), dnInfos1.getNonDfsUsed());
        // Testing without nonDfs field
        HdfsProtos.DatanodeInfoProto.Builder b = DatanodeInfoProto.newBuilder();
        b.setId(PBHelperClient.convert(id)).setCapacity(3500L).setDfsUsed(1000L).setRemaining(500L);
        DatanodeInfo dnInfos3 = PBHelperClient.convert(b.build());
        Assert.assertEquals(dnInfos0.getNonDfsUsed(), dnInfos3.getNonDfsUsed());
    }

    @Test
    public void testSlowPeerInfoPBHelper() {
        // Test with a map that has a few slow peer entries.
        final SlowPeerReports slowPeers = SlowPeerReports.create(ImmutableMap.of("peer1", 0.0, "peer2", 1.0, "peer3", 2.0));
        SlowPeerReports slowPeersConverted1 = PBHelper.convertSlowPeerInfo(PBHelper.convertSlowPeerInfo(slowPeers));
        Assert.assertTrue(((("Expected map:" + slowPeers) + ", got map:") + (slowPeersConverted1.getSlowPeers())), slowPeersConverted1.equals(slowPeers));
        // Test with an empty map.
        SlowPeerReports slowPeersConverted2 = PBHelper.convertSlowPeerInfo(PBHelper.convertSlowPeerInfo(EMPTY_REPORT));
        Assert.assertTrue((("Expected empty map:" + ", got map:") + slowPeersConverted2), slowPeersConverted2.equals(EMPTY_REPORT));
    }

    @Test
    public void testSlowDiskInfoPBHelper() {
        // Test with a map that has a few slow disk entries.
        final SlowDiskReports slowDisks = SlowDiskReports.create(ImmutableMap.of("disk1", ImmutableMap.of(METADATA, 0.5), "disk2", ImmutableMap.of(READ, 1.0, WRITE, 1.0), "disk3", ImmutableMap.of(METADATA, 1.2, READ, 1.5, WRITE, 1.3)));
        SlowDiskReports slowDisksConverted1 = PBHelper.convertSlowDiskInfo(PBHelper.convertSlowDiskInfo(slowDisks));
        Assert.assertTrue(((("Expected map:" + slowDisks) + ", got map:") + (slowDisksConverted1.getSlowDisks())), slowDisksConverted1.equals(slowDisks));
        // Test with an empty map
        SlowDiskReports slowDisksConverted2 = PBHelper.convertSlowDiskInfo(PBHelper.convertSlowDiskInfo(SlowDiskReports.EMPTY_REPORT));
        Assert.assertTrue((("Expected empty map:" + ", got map:") + slowDisksConverted2), slowDisksConverted2.equals(SlowDiskReports.EMPTY_REPORT));
    }

    /**
     * Test case for old namenode where the namenode doesn't support returning
     * keyProviderUri.
     */
    @Test
    public void testFSServerDefaultsHelper() {
        HdfsProtos.FsServerDefaultsProto.Builder b = HdfsProtos.FsServerDefaultsProto.newBuilder();
        b.setBlockSize(DFS_BLOCK_SIZE_DEFAULT);
        b.setBytesPerChecksum(DFS_BYTES_PER_CHECKSUM_DEFAULT);
        b.setWritePacketSize(DFS_CLIENT_WRITE_PACKET_SIZE_DEFAULT);
        b.setReplication(DFS_REPLICATION_DEFAULT);
        b.setFileBufferSize(IO_FILE_BUFFER_SIZE_DEFAULT);
        b.setEncryptDataTransfer(DFS_ENCRYPT_DATA_TRANSFER_DEFAULT);
        b.setTrashInterval(FS_TRASH_INTERVAL_DEFAULT);
        b.setChecksumType(ChecksumTypeProto.valueOf(Type.valueOf(DFS_CHECKSUM_TYPE_DEFAULT).id));
        HdfsProtos.FsServerDefaultsProto proto = b.build();
        Assert.assertFalse("KeyProvider uri is not supported", proto.hasKeyProviderUri());
        FsServerDefaults fsServerDefaults = PBHelperClient.convert(proto);
        Assert.assertNotNull("FsServerDefaults is null", fsServerDefaults);
        Assert.assertNull("KeyProviderUri should be null", fsServerDefaults.getKeyProviderUri());
    }

    @Test
    public void testConvertAddingECPolicyResponse() throws Exception {
        // Check conversion of the built-in policies.
        for (ErasureCodingPolicy policy : SystemErasureCodingPolicies.getPolicies()) {
            AddErasureCodingPolicyResponse response = new AddErasureCodingPolicyResponse(policy);
            HdfsProtos.AddErasureCodingPolicyResponseProto proto = PBHelperClient.convertAddErasureCodingPolicyResponse(response);
            // Optional fields should not be set.
            Assert.assertFalse("Unnecessary field is set.", proto.hasErrorMsg());
            // Convert proto back to an object and check for equality.
            AddErasureCodingPolicyResponse convertedResponse = PBHelperClient.convertAddErasureCodingPolicyResponse(proto);
            Assert.assertEquals("Converted policy not equal", response.getPolicy(), convertedResponse.getPolicy());
            Assert.assertEquals("Converted policy not equal", response.isSucceed(), convertedResponse.isSucceed());
        }
        ErasureCodingPolicy policy = SystemErasureCodingPolicies.getPolicies().get(0);
        AddErasureCodingPolicyResponse response = new AddErasureCodingPolicyResponse(policy, "failed");
        HdfsProtos.AddErasureCodingPolicyResponseProto proto = PBHelperClient.convertAddErasureCodingPolicyResponse(response);
        // Convert proto back to an object and check for equality.
        AddErasureCodingPolicyResponse convertedResponse = PBHelperClient.convertAddErasureCodingPolicyResponse(proto);
        Assert.assertEquals("Converted policy not equal", response.getPolicy(), convertedResponse.getPolicy());
        Assert.assertEquals("Converted policy not equal", response.getErrorMsg(), convertedResponse.getErrorMsg());
    }

    @Test
    public void testConvertErasureCodingPolicy() throws Exception {
        // Check conversion of the built-in policies.
        for (ErasureCodingPolicy policy : SystemErasureCodingPolicies.getPolicies()) {
            HdfsProtos.ErasureCodingPolicyProto proto = PBHelperClient.convertErasureCodingPolicy(policy);
            // Optional fields should not be set.
            Assert.assertFalse("Unnecessary field is set.", proto.hasName());
            Assert.assertFalse("Unnecessary field is set.", proto.hasSchema());
            Assert.assertFalse("Unnecessary field is set.", proto.hasCellSize());
            // Convert proto back to an object and check for equality.
            ErasureCodingPolicy convertedPolicy = PBHelperClient.convertErasureCodingPolicy(proto);
            Assert.assertEquals("Converted policy not equal", policy, convertedPolicy);
        }
        // Check conversion of a non-built-in policy.
        ECSchema newSchema = new ECSchema("testcodec", 3, 2);
        ErasureCodingPolicy newPolicy = new ErasureCodingPolicy(newSchema, (128 * 1024));
        HdfsProtos.ErasureCodingPolicyProto proto = PBHelperClient.convertErasureCodingPolicy(newPolicy);
        // Optional fields should be set.
        Assert.assertTrue("Optional field not set", proto.hasName());
        Assert.assertTrue("Optional field not set", proto.hasSchema());
        Assert.assertTrue("Optional field not set", proto.hasCellSize());
        ErasureCodingPolicy convertedPolicy = PBHelperClient.convertErasureCodingPolicy(proto);
        // Converted policy should be equal.
        Assert.assertEquals("Converted policy not equal", newPolicy, convertedPolicy);
    }

    @Test(expected = UninitializedMessageException.class)
    public void testErasureCodingPolicyMissingId() throws Exception {
        HdfsProtos.ErasureCodingPolicyProto.Builder builder = ErasureCodingPolicyProto.newBuilder();
        PBHelperClient.convertErasureCodingPolicy(builder.build());
    }

    @Test
    public void testErasureCodingPolicyMissingOptionalFields() throws Exception {
        // For non-built-in policies, the optional fields are required
        // when parsing an ErasureCodingPolicyProto.
        HdfsProtos.ECSchemaProto schemaProto = PBHelperClient.convertECSchema(StripedFileTestUtil.getDefaultECPolicy().getSchema());
        try {
            PBHelperClient.convertErasureCodingPolicy(ErasureCodingPolicyProto.newBuilder().setId(14).setSchema(schemaProto).setCellSize(123).build());
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("Missing", e);
        }
        try {
            PBHelperClient.convertErasureCodingPolicy(ErasureCodingPolicyProto.newBuilder().setId(14).setName("testpolicy").setCellSize(123).build());
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("Missing", e);
        }
        try {
            PBHelperClient.convertErasureCodingPolicy(ErasureCodingPolicyProto.newBuilder().setId(14).setName("testpolicy").setSchema(schemaProto).build());
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("Missing", e);
        }
    }
}

