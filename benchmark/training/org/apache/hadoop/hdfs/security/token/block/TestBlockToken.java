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
package org.apache.hadoop.hdfs.security.token.block;


import BlockTokenIdentifier.AccessMode;
import BlockTokenIdentifier.AccessMode.WRITE;
import Client.LOG;
import StorageType.EMPTY_ARRAY;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.Set;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocol.proto.ClientDatanodeProtocolProtos.GetReplicaVisibleLengthRequestProto;
import org.apache.hadoop.hdfs.protocol.proto.ClientDatanodeProtocolProtos.GetReplicaVisibleLengthResponseProto;
import org.apache.hadoop.hdfs.protocolPB.PBHelperClient;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit tests for block tokens
 */
public class TestBlockToken {
    public static final Logger LOG = LoggerFactory.getLogger(TestBlockToken.class);

    private static final String ADDRESS = "0.0.0.0";

    static {
        GenericTestUtils.setLogLevel(Client.LOG, Level.ALL);
        GenericTestUtils.setLogLevel(Server.LOG, Level.ALL);
        GenericTestUtils.setLogLevel(SaslRpcClient.LOG, Level.ALL);
        GenericTestUtils.setLogLevel(SaslRpcServer.LOG, Level.ALL);
        GenericTestUtils.setLogLevel(SaslInputStream.LOG, Level.ALL);
    }

    /**
     * Directory where we can count our open file descriptors under Linux
     */
    static final File FD_DIR = new File("/proc/self/fd/");

    final long blockKeyUpdateInterval = (10 * 60) * 1000;// 10 mins


    final long blockTokenLifetime = (2 * 60) * 1000;// 2 mins


    final ExtendedBlock block1 = new ExtendedBlock("0", 0L);

    final ExtendedBlock block2 = new ExtendedBlock("10", 10L);

    final ExtendedBlock block3 = new ExtendedBlock("-10", (-108L));

    private static class GetLengthAnswer implements Answer<GetReplicaVisibleLengthResponseProto> {
        final BlockTokenSecretManager sm;

        final BlockTokenIdentifier ident;

        public GetLengthAnswer(BlockTokenSecretManager sm, BlockTokenIdentifier ident) {
            this.sm = sm;
            this.ident = ident;
        }

        @Override
        public GetReplicaVisibleLengthResponseProto answer(InvocationOnMock invocation) throws IOException {
            Object[] args = invocation.getArguments();
            Assert.assertEquals(2, args.length);
            GetReplicaVisibleLengthRequestProto req = ((GetReplicaVisibleLengthRequestProto) (args[1]));
            Set<TokenIdentifier> tokenIds = UserGroupInformation.getCurrentUser().getTokenIdentifiers();
            Assert.assertEquals("Only one BlockTokenIdentifier expected", 1, tokenIds.size());
            long result = 0;
            for (TokenIdentifier tokenId : tokenIds) {
                BlockTokenIdentifier id = ((BlockTokenIdentifier) (tokenId));
                TestBlockToken.LOG.info(("Got: " + (id.toString())));
                Assert.assertTrue("Received BlockTokenIdentifier is wrong", ident.equals(id));
                sm.checkAccess(id, null, PBHelperClient.convert(req.getBlock()), WRITE, new StorageType[]{ StorageType.DEFAULT }, null);
                result = id.getBlockId();
            }
            return GetReplicaVisibleLengthResponseProto.newBuilder().setLength(result).build();
        }
    }

    @Test
    public void testWritableLegacy() throws Exception {
        testWritable(false);
    }

    @Test
    public void testWritableProtobuf() throws Exception {
        testWritable(true);
    }

    @Test
    public void testBlockTokenSecretManagerLegacy() throws Exception {
        testBlockTokenSecretManager(false);
    }

    @Test
    public void testBlockTokenSecretManagerProtobuf() throws Exception {
        testBlockTokenSecretManager(true);
    }

    @Test
    public void testBlockTokenRpcLegacy() throws Exception {
        testBlockTokenRpc(false);
    }

    @Test
    public void testBlockTokenRpcProtobuf() throws Exception {
        testBlockTokenRpc(true);
    }

    @Test
    public void testBlockTokenRpcLeakLegacy() throws Exception {
        testBlockTokenRpcLeak(false);
    }

    @Test
    public void testBlockTokenRpcLeakProtobuf() throws Exception {
        testBlockTokenRpcLeak(true);
    }

    @Test
    public void testBlockPoolTokenSecretManagerLegacy() throws Exception {
        testBlockPoolTokenSecretManager(false);
    }

    @Test
    public void testBlockPoolTokenSecretManagerProtobuf() throws Exception {
        testBlockPoolTokenSecretManager(true);
    }

    @Test
    public void testBlockTokenInLastLocatedBlockLegacy() throws IOException, InterruptedException {
        testBlockTokenInLastLocatedBlock(false);
    }

    @Test
    public void testBlockTokenInLastLocatedBlockProtobuf() throws IOException, InterruptedException {
        testBlockTokenInLastLocatedBlock(true);
    }

    @Test
    public void testLegacyBlockTokenBytesIsLegacy() throws IOException {
        final boolean useProto = false;
        BlockTokenSecretManager sm = new BlockTokenSecretManager(blockKeyUpdateInterval, blockTokenLifetime, 0, 1, "fake-pool", null, useProto);
        Token<BlockTokenIdentifier> token = sm.generateToken(block1, EnumSet.noneOf(AccessMode.class), new StorageType[]{ StorageType.DEFAULT }, new String[0]);
        final byte[] tokenBytes = token.getIdentifier();
        BlockTokenIdentifier legacyToken = new BlockTokenIdentifier();
        BlockTokenIdentifier protobufToken = new BlockTokenIdentifier();
        BlockTokenIdentifier readToken = new BlockTokenIdentifier();
        DataInputBuffer dib = new DataInputBuffer();
        dib.reset(tokenBytes, tokenBytes.length);
        legacyToken.readFieldsLegacy(dib);
        boolean invalidProtobufMessage = false;
        try {
            dib.reset(tokenBytes, tokenBytes.length);
            protobufToken.readFieldsProtobuf(dib);
        } catch (IOException e) {
            invalidProtobufMessage = true;
        }
        Assert.assertTrue(invalidProtobufMessage);
        dib.reset(tokenBytes, tokenBytes.length);
        readToken.readFields(dib);
        // Using legacy, the token parses as a legacy block token and not a protobuf
        Assert.assertEquals(legacyToken, readToken);
        Assert.assertNotEquals(protobufToken, readToken);
    }

    @Test
    public void testEmptyLegacyBlockTokenBytesIsLegacy() throws IOException {
        BlockTokenIdentifier emptyIdent = new BlockTokenIdentifier();
        DataOutputBuffer dob = new DataOutputBuffer(4096);
        DataInputBuffer dib = new DataInputBuffer();
        emptyIdent.writeLegacy(dob);
        byte[] emptyIdentBytes = Arrays.copyOf(dob.getData(), dob.getLength());
        BlockTokenIdentifier legacyToken = new BlockTokenIdentifier();
        BlockTokenIdentifier protobufToken = new BlockTokenIdentifier();
        BlockTokenIdentifier readToken = new BlockTokenIdentifier();
        dib.reset(emptyIdentBytes, emptyIdentBytes.length);
        legacyToken.readFieldsLegacy(dib);
        boolean invalidProtobufMessage = false;
        try {
            dib.reset(emptyIdentBytes, emptyIdentBytes.length);
            protobufToken.readFieldsProtobuf(dib);
        } catch (IOException e) {
            invalidProtobufMessage = true;
        }
        Assert.assertTrue(invalidProtobufMessage);
        dib.reset(emptyIdentBytes, emptyIdentBytes.length);
        readToken.readFields(dib);
    }

    @Test
    public void testProtobufBlockTokenBytesIsProtobuf() throws IOException {
        final boolean useProto = true;
        BlockTokenSecretManager sm = new BlockTokenSecretManager(blockKeyUpdateInterval, blockTokenLifetime, 0, 1, "fake-pool", null, useProto);
        Token<BlockTokenIdentifier> token = sm.generateToken(block1, EnumSet.noneOf(AccessMode.class), EMPTY_ARRAY, new String[0]);
        final byte[] tokenBytes = token.getIdentifier();
        BlockTokenIdentifier legacyToken = new BlockTokenIdentifier();
        BlockTokenIdentifier protobufToken = new BlockTokenIdentifier();
        BlockTokenIdentifier readToken = new BlockTokenIdentifier();
        DataInputBuffer dib = new DataInputBuffer();
        /* We receive NegativeArraySizeException because we didn't call
        readFields and instead try to parse this directly as a legacy
        BlockTokenIdentifier.

        Note: because the parsing depends on the expiryDate which is based on
        `Time.now()` it can sometimes fail with IOException and sometimes with
        NegativeArraySizeException.
         */
        boolean invalidLegacyMessage = false;
        try {
            dib.reset(tokenBytes, tokenBytes.length);
            legacyToken.readFieldsLegacy(dib);
        } catch (IOException | NegativeArraySizeException e) {
            invalidLegacyMessage = true;
        }
        Assert.assertTrue(invalidLegacyMessage);
        dib.reset(tokenBytes, tokenBytes.length);
        protobufToken.readFieldsProtobuf(dib);
        dib.reset(tokenBytes, tokenBytes.length);
        readToken.readFields(dib);
        // Using protobuf, the token parses as a protobuf and not a legacy block
        // token
        Assert.assertNotEquals(legacyToken, readToken);
        Assert.assertEquals(protobufToken, readToken);
    }

    @Test
    public void testEmptyProtobufBlockTokenBytesIsProtobuf() throws IOException {
        // Empty BlockTokenIdentifiers throw IOException
        BlockTokenIdentifier identifier = new BlockTokenIdentifier();
        testCraftedProtobufBlockTokenIdentifier(identifier, true, false);
    }

    @Test
    public void testCraftedProtobufBlockTokenBytesIsProtobuf() throws IOException {
        /* Parsing BlockTokenIdentifier with expiryDate
        2017-02-09 00:12:35,072+0100 will throw IOException.
        However, expiryDate of
        2017-02-09 00:12:35,071+0100 will throw NegativeArraySizeException.
         */
        BlockTokenIdentifier identifier = new BlockTokenIdentifier("user", "blockpool", 123, EnumSet.allOf(AccessMode.class), new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }, new String[]{ "fake-storage-id" }, true);
        Calendar cal = new GregorianCalendar();
        cal.set(2017, 1, 9, 0, 12, 35);
        long datetime = cal.getTimeInMillis();
        datetime = (datetime / 1000) * 1000;// strip milliseconds.

        datetime = datetime + 71;// 2017-02-09 00:12:35,071+0100

        identifier.setExpiryDate(datetime);
        testCraftedProtobufBlockTokenIdentifier(identifier, false, true);
        datetime += 1;// 2017-02-09 00:12:35,072+0100

        identifier.setExpiryDate(datetime);
        testCraftedProtobufBlockTokenIdentifier(identifier, true, false);
    }

    @Test
    public void testEmptyBlockTokenSerialization() throws IOException {
        BlockTokenIdentifier ident = new BlockTokenIdentifier();
        BlockTokenIdentifier ret = writeAndReadBlockToken(ident);
        Assert.assertEquals(ret.getExpiryDate(), 0);
        Assert.assertEquals(ret.getKeyId(), 0);
        Assert.assertEquals(ret.getUserId(), null);
        Assert.assertEquals(ret.getBlockPoolId(), null);
        Assert.assertEquals(ret.getBlockId(), 0);
        Assert.assertEquals(ret.getAccessModes(), EnumSet.noneOf(AccessMode.class));
        Assert.assertArrayEquals(ret.getStorageTypes(), EMPTY_ARRAY);
    }

    @Test
    public void testBlockTokenSerialization() throws IOException {
        testBlockTokenSerialization(false);
        testBlockTokenSerialization(true);
    }

    @Test
    public void testBadStorageIDCheckAccess() throws IOException {
        testBadStorageIDCheckAccess(false);
        testBadStorageIDCheckAccess(true);
    }
}

