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
package org.apache.hadoop.hdfs;


import FileType.IS_FILE;
import FileType.IS_SYMLINK;
import HdfsFileStatusProto.Builder;
import com.google.protobuf.ByteString;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import org.apache.hadoop.fs.FSProtos.FileStatusProto;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.hdfs.protocol.proto.HdfsProtos.HdfsFileStatusProto;
import org.apache.hadoop.hdfs.protocol.proto.HdfsProtos.HdfsFileStatusProto.FileType;
import org.apache.hadoop.hdfs.protocolPB.PBHelperClient;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Verify compatible FileStatus/HdfsFileStatus serialization.
 */
public class TestFileStatusSerialization {
    private static final URI BASEURI = new Path("hdfs://foobar").toUri();

    private static final Path BASEPATH = new Path("/dingos");

    private static final String FILE = "zot";

    private static final Path FULLPATH = new Path("hdfs://foobar/dingos/zot");

    /**
     * Test API backwards-compatibility with 2.x applications w.r.t. FsPermission.
     */
    @Test
    @SuppressWarnings("deprecation")
    public void testFsPermissionCompatibility() throws Exception {
        final int flagmask = 8;
        // flags compatible with 2.x; fixed as constant in this test to ensure
        // compatibility is maintained. New flags are not part of the contract this
        // test verifies.
        for (int i = 0; i < flagmask; ++i) {
            FsPermission perm = FsPermission.createImmutable(((short) (11)));
            HdfsFileStatusProto.Builder hspb = TestFileStatusSerialization.baseStatus().setPermission(PBHelperClient.convert(perm)).setFlags(i);
            HdfsFileStatus stat = PBHelperClient.convert(hspb.build());
            stat.makeQualified(TestFileStatusSerialization.BASEURI, TestFileStatusSerialization.BASEPATH);
            Assert.assertEquals(TestFileStatusSerialization.FULLPATH, stat.getPath());
            // verify deprecated FsPermissionExtension methods
            FsPermission sp = stat.getPermission();
            Assert.assertEquals(sp.getAclBit(), stat.hasAcl());
            Assert.assertEquals(sp.getEncryptedBit(), stat.isEncrypted());
            Assert.assertEquals(sp.getErasureCodedBit(), stat.isErasureCoded());
            // verify Writable contract
            DataOutputBuffer dob = new DataOutputBuffer();
            stat.write(dob);
            DataInputBuffer dib = new DataInputBuffer();
            dib.reset(dob.getData(), 0, dob.getLength());
            FileStatus fstat = new FileStatus();
            fstat.readFields(dib);
            TestFileStatusSerialization.checkFields(((FileStatus) (stat)), fstat);
            // FsPermisisonExtension used for HdfsFileStatus, not FileStatus,
            // attribute flags should still be preserved
            Assert.assertEquals(sp.getAclBit(), fstat.hasAcl());
            Assert.assertEquals(sp.getEncryptedBit(), fstat.isEncrypted());
            Assert.assertEquals(sp.getErasureCodedBit(), fstat.isErasureCoded());
        }
    }

    @Test
    public void testJavaSerialization() throws Exception {
        HdfsFileStatusProto hsp = TestFileStatusSerialization.baseStatus().build();
        HdfsFileStatus hs = PBHelperClient.convert(hsp);
        hs.makeQualified(TestFileStatusSerialization.BASEURI, TestFileStatusSerialization.BASEPATH);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(hs);
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            FileStatus deser = ((FileStatus) (ois.readObject()));
            Assert.assertEquals(hs, deser);
            TestFileStatusSerialization.checkFields(((FileStatus) (hs)), deser);
        }
    }

    @Test
    public void testCrossSerializationProto() throws Exception {
        for (FileType t : FileType.values()) {
            HdfsFileStatusProto.Builder hspb = TestFileStatusSerialization.baseStatus().setFileType(t);
            if (IS_SYMLINK.equals(t)) {
                hspb.setSymlink(ByteString.copyFromUtf8("hdfs://yaks/dingos"));
            }
            if (IS_FILE.equals(t)) {
                hspb.setFileId(4544);
            }
            HdfsFileStatusProto hsp = hspb.build();
            byte[] src = hsp.toByteArray();
            FileStatusProto fsp = FileStatusProto.parseFrom(src);
            Assert.assertEquals(hsp.getPath().toStringUtf8(), fsp.getPath());
            Assert.assertEquals(hsp.getLength(), fsp.getLength());
            Assert.assertEquals(hsp.getPermission().getPerm(), fsp.getPermission().getPerm());
            Assert.assertEquals(hsp.getOwner(), fsp.getOwner());
            Assert.assertEquals(hsp.getGroup(), fsp.getGroup());
            Assert.assertEquals(hsp.getModificationTime(), fsp.getModificationTime());
            Assert.assertEquals(hsp.getAccessTime(), fsp.getAccessTime());
            Assert.assertEquals(hsp.getSymlink().toStringUtf8(), fsp.getSymlink());
            Assert.assertEquals(hsp.getBlockReplication(), fsp.getBlockReplication());
            Assert.assertEquals(hsp.getBlocksize(), fsp.getBlockSize());
            Assert.assertEquals(hsp.getFileType().ordinal(), fsp.getFileType().ordinal());
            // verify unknown fields preserved
            byte[] dst = fsp.toByteArray();
            HdfsFileStatusProto hsp2 = HdfsFileStatusProto.parseFrom(dst);
            Assert.assertEquals(hsp, hsp2);
            FileStatus hstat = ((FileStatus) (PBHelperClient.convert(hsp)));
            FileStatus hstat2 = ((FileStatus) (PBHelperClient.convert(hsp2)));
            TestFileStatusSerialization.checkFields(hstat, hstat2);
        }
    }
}

