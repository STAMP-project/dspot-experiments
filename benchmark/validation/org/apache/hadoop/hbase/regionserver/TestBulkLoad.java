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


import MultiVersionConcurrencyControl.WriteEntry;
import WALEdit.BULK_LOAD;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.DoNotRetryIOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.shaded.protobuf.generated.WALProtos.BulkLoadDescriptor;
import org.apache.hadoop.hbase.shaded.protobuf.generated.WALProtos.StoreDescriptor;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.hbase.wal.WAL;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.apache.hadoop.hbase.wal.WALKeyImpl;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * This class attempts to unit test bulk HLog loading.
 */
@Category(SmallTests.class)
public class TestBulkLoad {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBulkLoad.class);

    @ClassRule
    public static TemporaryFolder testFolder = new TemporaryFolder();

    private static HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private final WAL log = Mockito.mock(WAL.class);

    private final Configuration conf = HBaseConfiguration.create();

    private final Random random = new Random();

    private final byte[] randomBytes = new byte[100];

    private final byte[] family1 = Bytes.toBytes("family1");

    private final byte[] family2 = Bytes.toBytes("family2");

    @Rule
    public TestName name = new TestName();

    @Test
    public void verifyBulkLoadEvent() throws IOException {
        TableName tableName = TableName.valueOf("test", "test");
        List<Pair<byte[], String>> familyPaths = withFamilyPathsFor(family1);
        byte[] familyName = familyPaths.get(0).getFirst();
        String storeFileName = familyPaths.get(0).getSecond();
        storeFileName = new Path(storeFileName).getName();
        List<String> storeFileNames = new ArrayList<>();
        storeFileNames.add(storeFileName);
        Mockito.when(log.append(ArgumentMatchers.any(), ArgumentMatchers.any(), MockitoHamcrest.argThat(TestBulkLoad.bulkLogWalEdit(BULK_LOAD, tableName.toBytes(), familyName, storeFileNames)), ArgumentMatchers.anyBoolean())).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                WALKeyImpl walKey = invocation.getArgument(1);
                MultiVersionConcurrencyControl mvcc = walKey.getMvcc();
                if (mvcc != null) {
                    MultiVersionConcurrencyControl.WriteEntry we = mvcc.begin();
                    walKey.setWriteEntry(we);
                }
                return 1L;
            }
        });
        testRegionWithFamiliesAndSpecifiedTableName(tableName, family1).bulkLoadHFiles(familyPaths, false, null);
        Mockito.verify(log).sync(ArgumentMatchers.anyLong());
    }

    @Test
    public void bulkHLogShouldThrowNoErrorAndWriteMarkerWithBlankInput() throws IOException {
        testRegionWithFamilies(family1).bulkLoadHFiles(new ArrayList(), false, null);
    }

    @Test
    public void shouldBulkLoadSingleFamilyHLog() throws IOException {
        Mockito.when(log.append(ArgumentMatchers.any(), ArgumentMatchers.any(), MockitoHamcrest.argThat(TestBulkLoad.bulkLogWalEditType(BULK_LOAD)), ArgumentMatchers.anyBoolean())).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                WALKeyImpl walKey = invocation.getArgument(1);
                MultiVersionConcurrencyControl mvcc = walKey.getMvcc();
                if (mvcc != null) {
                    MultiVersionConcurrencyControl.WriteEntry we = mvcc.begin();
                    walKey.setWriteEntry(we);
                }
                return 1L;
            }
        });
        testRegionWithFamilies(family1).bulkLoadHFiles(withFamilyPathsFor(family1), false, null);
        Mockito.verify(log).sync(ArgumentMatchers.anyLong());
    }

    @Test
    public void shouldBulkLoadManyFamilyHLog() throws IOException {
        Mockito.when(log.append(ArgumentMatchers.any(), ArgumentMatchers.any(), MockitoHamcrest.argThat(TestBulkLoad.bulkLogWalEditType(BULK_LOAD)), ArgumentMatchers.anyBoolean())).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                WALKeyImpl walKey = invocation.getArgument(1);
                MultiVersionConcurrencyControl mvcc = walKey.getMvcc();
                if (mvcc != null) {
                    MultiVersionConcurrencyControl.WriteEntry we = mvcc.begin();
                    walKey.setWriteEntry(we);
                }
                return 1L;
            }
        });
        testRegionWithFamilies(family1, family2).bulkLoadHFiles(withFamilyPathsFor(family1, family2), false, null);
        Mockito.verify(log).sync(ArgumentMatchers.anyLong());
    }

    @Test
    public void shouldBulkLoadManyFamilyHLogEvenWhenTableNameNamespaceSpecified() throws IOException {
        Mockito.when(log.append(ArgumentMatchers.any(), ArgumentMatchers.any(), MockitoHamcrest.argThat(TestBulkLoad.bulkLogWalEditType(BULK_LOAD)), ArgumentMatchers.anyBoolean())).thenAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                WALKeyImpl walKey = invocation.getArgument(1);
                MultiVersionConcurrencyControl mvcc = walKey.getMvcc();
                if (mvcc != null) {
                    MultiVersionConcurrencyControl.WriteEntry we = mvcc.begin();
                    walKey.setWriteEntry(we);
                }
                return 1L;
            }
        });
        TableName tableName = TableName.valueOf("test", "test");
        testRegionWithFamiliesAndSpecifiedTableName(tableName, family1, family2).bulkLoadHFiles(withFamilyPathsFor(family1, family2), false, null);
        Mockito.verify(log).sync(ArgumentMatchers.anyLong());
    }

    @Test(expected = DoNotRetryIOException.class)
    public void shouldCrashIfBulkLoadFamiliesNotInTable() throws IOException {
        testRegionWithFamilies(family1).bulkLoadHFiles(withFamilyPathsFor(family1, family2), false, null);
    }

    @Test(expected = DoNotRetryIOException.class)
    public void bulkHLogShouldThrowErrorWhenFamilySpecifiedAndHFileExistsButNotInTableDescriptor() throws IOException {
        testRegionWithFamilies().bulkLoadHFiles(withFamilyPathsFor(family1), false, null);
    }

    @Test(expected = DoNotRetryIOException.class)
    public void shouldThrowErrorIfBadFamilySpecifiedAsFamilyPath() throws IOException {
        testRegionWithFamilies().bulkLoadHFiles(Arrays.asList(withInvalidColumnFamilyButProperHFileLocation(family1)), false, null);
    }

    @Test(expected = FileNotFoundException.class)
    public void shouldThrowErrorIfHFileDoesNotExist() throws IOException {
        List<Pair<byte[], String>> list = Arrays.asList(withMissingHFileForFamily(family1));
        testRegionWithFamilies(family1).bulkLoadHFiles(list, false, null);
    }

    private static class WalMatcher extends TypeSafeMatcher<WALEdit> {
        private final byte[] typeBytes;

        private final byte[] tableName;

        private final byte[] familyName;

        private final List<String> storeFileNames;

        public WalMatcher(byte[] typeBytes) {
            this(typeBytes, null, null, null);
        }

        public WalMatcher(byte[] typeBytes, byte[] tableName, byte[] familyName, List<String> storeFileNames) {
            this.typeBytes = typeBytes;
            this.tableName = tableName;
            this.familyName = familyName;
            this.storeFileNames = storeFileNames;
        }

        @Override
        protected boolean matchesSafely(WALEdit item) {
            Assert.assertTrue(Arrays.equals(CellUtil.cloneQualifier(item.getCells().get(0)), typeBytes));
            BulkLoadDescriptor desc;
            try {
                desc = WALEdit.getBulkLoadDescriptor(item.getCells().get(0));
            } catch (IOException e) {
                return false;
            }
            Assert.assertNotNull(desc);
            if ((tableName) != null) {
                Assert.assertTrue(Bytes.equals(ProtobufUtil.toTableName(desc.getTableName()).getName(), tableName));
            }
            if ((storeFileNames) != null) {
                int index = 0;
                StoreDescriptor store = desc.getStores(0);
                Assert.assertTrue(Bytes.equals(store.getFamilyName().toByteArray(), familyName));
                Assert.assertTrue(Bytes.equals(Bytes.toBytes(store.getStoreHomeDir()), familyName));
                Assert.assertEquals(storeFileNames.size(), store.getStoreFileCount());
            }
            return true;
        }

        @Override
        public void describeTo(Description description) {
        }
    }
}

