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
package org.apache.hadoop.hbase.regionserver;


import RegionInfo.COMPARATOR;
import RegionInfo.MD5_HEX_LENGTH;
import RegionInfo.REPLICA_ID_FORMAT;
import RegionInfoBuilder.FIRST_META_REGIONINFO;
import TableName.META_TABLE_NAME;
import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.exceptions.DeserializationException;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.shaded.protobuf.generated.HBaseProtos;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSTableDescriptors;
import org.apache.hadoop.hbase.util.MD5Hash;
import org.apache.hbase.thirdparty.com.google.protobuf.UnsafeByteOperations;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


@Category({ RegionServerTests.class, SmallTests.class })
public class TestRegionInfoBuilder {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionInfoBuilder.class);

    @Rule
    public TestName name = new TestName();

    @Test
    public void testBuilder() {
        TableName tn = TableName.valueOf("test");
        RegionInfoBuilder builder = RegionInfoBuilder.newBuilder(tn);
        byte[] startKey = Bytes.toBytes("a");
        builder.setStartKey(startKey);
        byte[] endKey = Bytes.toBytes("z");
        builder.setEndKey(endKey);
        int regionId = 1;
        builder.setRegionId(1);
        int replicaId = 2;
        builder.setReplicaId(replicaId);
        boolean offline = true;
        builder.setOffline(offline);
        boolean isSplit = true;
        builder.setSplit(isSplit);
        RegionInfo ri = builder.build();
        Assert.assertEquals(tn, ri.getTable());
        Assert.assertArrayEquals(startKey, ri.getStartKey());
        Assert.assertArrayEquals(endKey, ri.getEndKey());
        Assert.assertEquals(regionId, ri.getRegionId());
        Assert.assertEquals(replicaId, ri.getReplicaId());
        Assert.assertEquals(offline, ri.isOffline());
        Assert.assertEquals(isSplit, ri.isSplit());
    }

    @Test
    public void testPb() throws DeserializationException {
        RegionInfo ri = RegionInfoBuilder.FIRST_META_REGIONINFO;
        byte[] bytes = RegionInfo.toByteArray(ri);
        RegionInfo pbri = RegionInfo.parseFrom(bytes);
        Assert.assertTrue(((COMPARATOR.compare(ri, pbri)) == 0));
    }

    @Test
    public void testReadAndWriteRegionInfoFile() throws IOException, InterruptedException {
        HBaseTestingUtility htu = new HBaseTestingUtility();
        RegionInfo ri = RegionInfoBuilder.FIRST_META_REGIONINFO;
        Path basedir = getDataTestDir();
        // Create a region.  That'll write the .regioninfo file.
        FSTableDescriptors fsTableDescriptors = new FSTableDescriptors(htu.getConfiguration());
        HRegion r = HBaseTestingUtility.createRegionAndWAL(convert(ri), basedir, htu.getConfiguration(), fsTableDescriptors.get(META_TABLE_NAME));
        // Get modtime on the file.
        long modtime = getModTime(r);
        HBaseTestingUtility.closeRegionAndWAL(r);
        Thread.sleep(1001);
        r = HRegion.openHRegion(basedir, convert(ri), fsTableDescriptors.get(META_TABLE_NAME), null, htu.getConfiguration());
        // Ensure the file is not written for a second time.
        long modtime2 = getModTime(r);
        Assert.assertEquals(modtime, modtime2);
        // Now load the file.
        RegionInfo deserializedRi = HRegionFileSystem.loadRegionInfoFileContent(r.getRegionFileSystem().getFileSystem(), r.getRegionFileSystem().getRegionDir());
        HBaseTestingUtility.closeRegionAndWAL(r);
    }

    @Test
    public void testCreateRegionInfoName() throws Exception {
        final String tableName = name.getMethodName();
        final TableName tn = TableName.valueOf(tableName);
        String startKey = "startkey";
        final byte[] sk = Bytes.toBytes(startKey);
        String id = "id";
        // old format region name
        byte[] name = RegionInfo.createRegionName(tn, sk, id, false);
        String nameStr = Bytes.toString(name);
        Assert.assertEquals(((((tableName + ",") + startKey) + ",") + id), nameStr);
        // new format region name.
        String md5HashInHex = MD5Hash.getMD5AsHex(name);
        Assert.assertEquals(MD5_HEX_LENGTH, md5HashInHex.length());
        name = RegionInfo.createRegionName(tn, sk, id, true);
        nameStr = Bytes.toString(name);
        Assert.assertEquals((((((((tableName + ",") + startKey) + ",") + id) + ".") + md5HashInHex) + "."), nameStr);
    }

    @Test
    public void testContainsRange() {
        TableDescriptor tableDesc = TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).build();
        RegionInfo ri = RegionInfoBuilder.newBuilder(tableDesc.getTableName()).setStartKey(Bytes.toBytes("a")).setEndKey(Bytes.toBytes("g")).build();
        // Single row range at start of region
        Assert.assertTrue(ri.containsRange(Bytes.toBytes("a"), Bytes.toBytes("a")));
        // Fully contained range
        Assert.assertTrue(ri.containsRange(Bytes.toBytes("b"), Bytes.toBytes("c")));
        // Range overlapping start of region
        Assert.assertTrue(ri.containsRange(Bytes.toBytes("a"), Bytes.toBytes("c")));
        // Fully contained single-row range
        Assert.assertTrue(ri.containsRange(Bytes.toBytes("c"), Bytes.toBytes("c")));
        // Range that overlaps end key and hence doesn't fit
        Assert.assertFalse(ri.containsRange(Bytes.toBytes("a"), Bytes.toBytes("g")));
        // Single row range on end key
        Assert.assertFalse(ri.containsRange(Bytes.toBytes("g"), Bytes.toBytes("g")));
        // Single row range entirely outside
        Assert.assertFalse(ri.containsRange(Bytes.toBytes("z"), Bytes.toBytes("z")));
        // Degenerate range
        try {
            ri.containsRange(Bytes.toBytes("z"), Bytes.toBytes("a"));
            Assert.fail("Invalid range did not throw IAE");
        } catch (IllegalArgumentException iae) {
        }
    }

    @Test
    public void testLastRegionCompare() {
        TableDescriptor tableDesc = TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).build();
        RegionInfo rip = RegionInfoBuilder.newBuilder(tableDesc.getTableName()).setStartKey(Bytes.toBytes("a")).setEndKey(new byte[0]).build();
        RegionInfo ric = RegionInfoBuilder.newBuilder(tableDesc.getTableName()).setStartKey(Bytes.toBytes("a")).setEndKey(Bytes.toBytes("b")).build();
        Assert.assertTrue(((COMPARATOR.compare(rip, ric)) > 0));
    }

    @Test
    public void testMetaTables() {
        Assert.assertTrue(FIRST_META_REGIONINFO.isMetaRegion());
    }

    @Test
    public void testComparator() {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[] empty = new byte[0];
        RegionInfo older = RegionInfoBuilder.newBuilder(tableName).setStartKey(empty).setEndKey(empty).setSplit(false).setRegionId(0L).build();
        RegionInfo newer = RegionInfoBuilder.newBuilder(tableName).setStartKey(empty).setEndKey(empty).setSplit(false).setRegionId(1L).build();
        Assert.assertTrue(((COMPARATOR.compare(older, newer)) < 0));
        Assert.assertTrue(((COMPARATOR.compare(newer, older)) > 0));
        Assert.assertTrue(((COMPARATOR.compare(older, older)) == 0));
        Assert.assertTrue(((COMPARATOR.compare(newer, newer)) == 0));
    }

    @Test
    public void testRegionNameForRegionReplicas() throws Exception {
        String tableName = name.getMethodName();
        final TableName tn = TableName.valueOf(tableName);
        String startKey = "startkey";
        final byte[] sk = Bytes.toBytes(startKey);
        String id = "id";
        // assert with only the region name without encoding
        // primary, replicaId = 0
        byte[] name = RegionInfo.createRegionName(tn, sk, Bytes.toBytes(id), 0, false);
        String nameStr = Bytes.toString(name);
        Assert.assertEquals(((((tableName + ",") + startKey) + ",") + id), nameStr);
        // replicaId = 1
        name = RegionInfo.createRegionName(tn, sk, Bytes.toBytes(id), 1, false);
        nameStr = Bytes.toString(name);
        Assert.assertEquals(((((((tableName + ",") + startKey) + ",") + id) + "_") + (String.format(REPLICA_ID_FORMAT, 1))), nameStr);
        // replicaId = max
        name = RegionInfo.createRegionName(tn, sk, Bytes.toBytes(id), 65535, false);
        nameStr = Bytes.toString(name);
        Assert.assertEquals(((((((tableName + ",") + startKey) + ",") + id) + "_") + (String.format(REPLICA_ID_FORMAT, 65535))), nameStr);
    }

    @Test
    public void testParseName() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[] startKey = Bytes.toBytes("startKey");
        long regionId = System.currentTimeMillis();
        int replicaId = 42;
        // test without replicaId
        byte[] regionName = RegionInfo.createRegionName(tableName, startKey, regionId, false);
        byte[][] fields = RegionInfo.parseRegionName(regionName);
        Assert.assertArrayEquals(Bytes.toString(fields[0]), tableName.getName(), fields[0]);
        Assert.assertArrayEquals(Bytes.toString(fields[1]), startKey, fields[1]);
        Assert.assertArrayEquals(Bytes.toString(fields[2]), Bytes.toBytes(Long.toString(regionId)), fields[2]);
        Assert.assertEquals(3, fields.length);
        // test with replicaId
        regionName = RegionInfo.createRegionName(tableName, startKey, regionId, replicaId, false);
        fields = RegionInfo.parseRegionName(regionName);
        Assert.assertArrayEquals(Bytes.toString(fields[0]), tableName.getName(), fields[0]);
        Assert.assertArrayEquals(Bytes.toString(fields[1]), startKey, fields[1]);
        Assert.assertArrayEquals(Bytes.toString(fields[2]), Bytes.toBytes(Long.toString(regionId)), fields[2]);
        Assert.assertArrayEquals(Bytes.toString(fields[3]), Bytes.toBytes(String.format(REPLICA_ID_FORMAT, replicaId)), fields[3]);
    }

    @Test
    public void testConvert() {
        final TableName tableName = TableName.valueOf(("ns1:" + (name.getMethodName())));
        byte[] startKey = Bytes.toBytes("startKey");
        byte[] endKey = Bytes.toBytes("endKey");
        boolean split = false;
        long regionId = System.currentTimeMillis();
        int replicaId = 42;
        RegionInfo ri = RegionInfoBuilder.newBuilder(tableName).setStartKey(startKey).setEndKey(endKey).setSplit(split).setRegionId(regionId).setReplicaId(replicaId).build();
        // convert two times, compare
        RegionInfo convertedRi = ProtobufUtil.toRegionInfo(ProtobufUtil.toRegionInfo(ri));
        Assert.assertEquals(ri, convertedRi);
        // test convert RegionInfo without replicaId
        HBaseProtos.RegionInfo info = HBaseProtos.RegionInfo.newBuilder().setTableName(HBaseProtos.TableName.newBuilder().setQualifier(UnsafeByteOperations.unsafeWrap(tableName.getQualifier())).setNamespace(UnsafeByteOperations.unsafeWrap(tableName.getNamespace())).build()).setStartKey(UnsafeByteOperations.unsafeWrap(startKey)).setEndKey(UnsafeByteOperations.unsafeWrap(endKey)).setSplit(split).setRegionId(regionId).build();
        convertedRi = ProtobufUtil.toRegionInfo(info);
        RegionInfo expectedRi = RegionInfoBuilder.newBuilder(tableName).setStartKey(startKey).setEndKey(endKey).setSplit(split).setRegionId(regionId).setReplicaId(0).build();
        Assert.assertEquals(expectedRi, convertedRi);
    }
}

