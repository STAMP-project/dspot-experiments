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
package org.apache.hadoop.hbase.io.hfile;


import Bytes.BYTES_RAWCOMPARATOR;
import CellComparatorImpl.COMPARATOR;
import CellComparatorImpl.MetaCellComparator;
import CellComparatorImpl.MetaCellComparator.META_COMPARATOR;
import HFileProtos.FileTrailerProto;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.CellComparator;
import org.apache.hadoop.hbase.CellComparatorImpl;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.shaded.protobuf.generated.HFileProtos;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static HFile.MAX_FORMAT_VERSION;
import static HFile.MIN_FORMAT_VERSION;
import static HFileReaderImpl.MINOR_VERSION_NO_CHECKSUM;
import static HFileReaderImpl.PBUF_TRAILER_MINOR_VERSION;


@RunWith(Parameterized.class)
@Category({ IOTests.class, SmallTests.class })
public class TestFixedFileTrailer {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFixedFileTrailer.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestFixedFileTrailer.class);

    private static final int MAX_COMPARATOR_NAME_LENGTH = 128;

    /**
     * The number of used fields by version. Indexed by version minus two.
     * Min version that we support is V2
     */
    private static final int[] NUM_FIELDS_BY_VERSION = new int[]{ 14, 15 };

    private HBaseTestingUtility util = new HBaseTestingUtility();

    private FileSystem fs;

    private ByteArrayOutputStream baos = new ByteArrayOutputStream();

    private int version;

    static {
        assert (TestFixedFileTrailer.NUM_FIELDS_BY_VERSION.length) == (((MAX_FORMAT_VERSION) - (MIN_FORMAT_VERSION)) + 1);
    }

    public TestFixedFileTrailer(int version) {
        this.version = version;
    }

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testComparatorIsHBase1Compatible() {
        FixedFileTrailer t = new FixedFileTrailer(version, PBUF_TRAILER_MINOR_VERSION);
        t.setComparatorClass(COMPARATOR.getClass());
        Assert.assertEquals(COMPARATOR.getClass().getName(), t.getComparatorClassName());
        HFileProtos.FileTrailerProto pb = t.toProtobuf();
        Assert.assertEquals(KeyValue.COMPARATOR.getClass().getName(), pb.getComparatorClassName());
        t.setComparatorClass(META_COMPARATOR.getClass());
        pb = t.toProtobuf();
        Assert.assertEquals(KeyValue.META_COMPARATOR.getClass().getName(), pb.getComparatorClassName());
    }

    @Test
    public void testCreateComparator() throws IOException {
        FixedFileTrailer t = new FixedFileTrailer(version, PBUF_TRAILER_MINOR_VERSION);
        try {
            Assert.assertEquals(CellComparatorImpl.class, t.createComparator(KeyValue.COMPARATOR.getLegacyKeyComparatorName()).getClass());
            Assert.assertEquals(CellComparatorImpl.class, t.createComparator(KeyValue.COMPARATOR.getClass().getName()).getClass());
            Assert.assertEquals(CellComparatorImpl.class, t.createComparator(CellComparator.class.getName()).getClass());
            Assert.assertEquals(MetaCellComparator.class, t.createComparator(KeyValue.META_COMPARATOR.getLegacyKeyComparatorName()).getClass());
            Assert.assertEquals(MetaCellComparator.class, t.createComparator(KeyValue.META_COMPARATOR.getClass().getName()).getClass());
            Assert.assertEquals(MetaCellComparator.class, t.createComparator(META_COMPARATOR.getClass().getName()).getClass());
            Assert.assertNull(t.createComparator(BYTES_RAWCOMPARATOR.getClass().getName()));
            Assert.assertNull(t.createComparator("org.apache.hadoop.hbase.KeyValue$RawBytesComparator"));
        } catch (IOException e) {
            Assert.fail("Unexpected exception while testing FixedFileTrailer#createComparator()");
        }
        // Test an invalid comparatorClassName
        expectedEx.expect(IOException.class);
        t.createComparator("");
    }

    @Test
    public void testTrailer() throws IOException {
        FixedFileTrailer t = new FixedFileTrailer(version, PBUF_TRAILER_MINOR_VERSION);
        t.setDataIndexCount(3);
        t.setEntryCount((((long) (Integer.MAX_VALUE)) + 1));
        t.setLastDataBlockOffset(291);
        t.setNumDataIndexLevels(3);
        t.setComparatorClass(COMPARATOR.getClass());
        t.setFirstDataBlockOffset(9081723123L);// Completely unrealistic.

        t.setUncompressedDataIndexSize(827398717L);// Something random.

        t.setLoadOnOpenOffset(128);
        t.setMetaIndexCount(7);
        t.setTotalUncompressedBytes(129731987);
        {
            DataOutputStream dos = new DataOutputStream(baos);// Limited scope.

            t.serialize(dos);
            dos.flush();
            Assert.assertEquals(dos.size(), FixedFileTrailer.getTrailerSize(version));
        }
        byte[] bytes = baos.toByteArray();
        baos.reset();
        Assert.assertEquals(bytes.length, FixedFileTrailer.getTrailerSize(version));
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        // Finished writing, trying to read.
        {
            DataInputStream dis = new DataInputStream(bais);
            FixedFileTrailer t2 = new FixedFileTrailer(version, PBUF_TRAILER_MINOR_VERSION);
            t2.deserialize(dis);
            Assert.assertEquals((-1), bais.read());// Ensure we have read everything.

            checkLoadedTrailer(version, t, t2);
        }
        // Now check what happens if the trailer is corrupted.
        Path trailerPath = new Path(getDataTestDir(), ("trailer_" + (version)));
        {
            for (byte invalidVersion : new byte[]{ (MIN_FORMAT_VERSION) - 1, (MAX_FORMAT_VERSION) + 1 }) {
                bytes[((bytes.length) - 1)] = invalidVersion;
                writeTrailer(trailerPath, null, bytes);
                try {
                    readTrailer(trailerPath);
                    Assert.fail("Exception expected");
                } catch (IllegalArgumentException ex) {
                    // Make it easy to debug this.
                    String msg = ex.getMessage();
                    String cleanMsg = msg.replaceAll("^(java(\\.[a-zA-Z]+)+:\\s+)?|\\s+\\(.*\\)\\s*$", "");
                    // will be followed by " expected: ..."
                    Assert.assertEquals(((("Actual exception message is \"" + msg) + "\".\n") + "Cleaned-up message"), ("Invalid HFile version: " + invalidVersion), cleanMsg);
                    TestFixedFileTrailer.LOG.info(("Got an expected exception: " + msg));
                }
            }
        }
        // Now write the trailer into a file and auto-detect the version.
        writeTrailer(trailerPath, t, null);
        FixedFileTrailer t4 = readTrailer(trailerPath);
        checkLoadedTrailer(version, t, t4);
        String trailerStr = t.toString();
        Assert.assertEquals((("Invalid number of fields in the string representation " + "of the trailer: ") + trailerStr), TestFixedFileTrailer.NUM_FIELDS_BY_VERSION[((version) - 2)], trailerStr.split(", ").length);
        Assert.assertEquals(trailerStr, t4.toString());
    }

    @Test
    public void testTrailerForV2NonPBCompatibility() throws Exception {
        if ((version) == 2) {
            FixedFileTrailer t = new FixedFileTrailer(version, MINOR_VERSION_NO_CHECKSUM);
            t.setDataIndexCount(3);
            t.setEntryCount((((long) (Integer.MAX_VALUE)) + 1));
            t.setLastDataBlockOffset(291);
            t.setNumDataIndexLevels(3);
            t.setComparatorClass(COMPARATOR.getClass());
            t.setFirstDataBlockOffset(9081723123L);// Completely unrealistic.

            t.setUncompressedDataIndexSize(827398717L);// Something random.

            t.setLoadOnOpenOffset(128);
            t.setMetaIndexCount(7);
            t.setTotalUncompressedBytes(129731987);
            {
                DataOutputStream dos = new DataOutputStream(baos);// Limited scope.

                serializeAsWritable(dos, t);
                dos.flush();
                Assert.assertEquals(FixedFileTrailer.getTrailerSize(version), dos.size());
            }
            byte[] bytes = baos.toByteArray();
            baos.reset();
            Assert.assertEquals(bytes.length, FixedFileTrailer.getTrailerSize(version));
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            {
                DataInputStream dis = new DataInputStream(bais);
                FixedFileTrailer t2 = new FixedFileTrailer(version, MINOR_VERSION_NO_CHECKSUM);
                t2.deserialize(dis);
                Assert.assertEquals((-1), bais.read());// Ensure we have read everything.

                checkLoadedTrailer(version, t, t2);
            }
        }
    }
}

