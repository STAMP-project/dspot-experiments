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
package org.apache.hadoop.hbase.master.procedure;


import HConstants.RECOVERED_EDITS_DIR;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.InvalidFamilyOperationException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hbase.wal.WALSplitter;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MasterTests.class, LargeTests.class })
public class TestDeleteColumnFamilyProcedureFromClient {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestDeleteColumnFamilyProcedureFromClient.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final TableName TABLENAME = TableName.valueOf("column_family_handlers");

    private static final byte[][] FAMILIES = new byte[][]{ Bytes.toBytes("cf1"), Bytes.toBytes("cf2"), Bytes.toBytes("cf3") };

    @Test
    public void deleteColumnFamilyWithMultipleRegions() throws Exception {
        Admin admin = TestDeleteColumnFamilyProcedureFromClient.TEST_UTIL.getAdmin();
        HTableDescriptor beforehtd = admin.getTableDescriptor(TestDeleteColumnFamilyProcedureFromClient.TABLENAME);
        FileSystem fs = TestDeleteColumnFamilyProcedureFromClient.TEST_UTIL.getDFSCluster().getFileSystem();
        // 1 - Check if table exists in descriptor
        Assert.assertTrue(admin.isTableAvailable(TestDeleteColumnFamilyProcedureFromClient.TABLENAME));
        // 2 - Check if all three families exist in descriptor
        Assert.assertEquals(3, beforehtd.getColumnFamilyCount());
        HColumnDescriptor[] families = beforehtd.getColumnFamilies();
        for (int i = 0; i < (families.length); i++) {
            Assert.assertTrue(families[i].getNameAsString().equals(("cf" + (i + 1))));
        }
        // 3 - Check if table exists in FS
        Path tableDir = FSUtils.getTableDir(TestDeleteColumnFamilyProcedureFromClient.TEST_UTIL.getDefaultRootDirPath(), TestDeleteColumnFamilyProcedureFromClient.TABLENAME);
        Assert.assertTrue(fs.exists(tableDir));
        // 4 - Check if all the 3 column families exist in FS
        FileStatus[] fileStatus = fs.listStatus(tableDir);
        for (int i = 0; i < (fileStatus.length); i++) {
            if ((fileStatus[i].isDirectory()) == true) {
                FileStatus[] cf = fs.listStatus(fileStatus[i].getPath(), new PathFilter() {
                    @Override
                    public boolean accept(Path p) {
                        if (p.getName().contains(RECOVERED_EDITS_DIR)) {
                            return false;
                        }
                        return true;
                    }
                });
                int k = 1;
                for (int j = 0; j < (cf.length); j++) {
                    if (((cf[j].isDirectory()) == true) && ((cf[j].getPath().getName().startsWith(".")) == false)) {
                        Assert.assertEquals(cf[j].getPath().getName(), ("cf" + k));
                        k++;
                    }
                }
            }
        }
        // TEST - Disable and delete the column family
        admin.disableTable(TestDeleteColumnFamilyProcedureFromClient.TABLENAME);
        admin.deleteColumnFamily(TestDeleteColumnFamilyProcedureFromClient.TABLENAME, Bytes.toBytes("cf2"));
        // 5 - Check if only 2 column families exist in the descriptor
        HTableDescriptor afterhtd = admin.getTableDescriptor(TestDeleteColumnFamilyProcedureFromClient.TABLENAME);
        Assert.assertEquals(2, afterhtd.getColumnFamilyCount());
        HColumnDescriptor[] newFamilies = afterhtd.getColumnFamilies();
        Assert.assertTrue(newFamilies[0].getNameAsString().equals("cf1"));
        Assert.assertTrue(newFamilies[1].getNameAsString().equals("cf3"));
        // 6 - Check if the second column family is gone from the FS
        fileStatus = fs.listStatus(tableDir);
        for (int i = 0; i < (fileStatus.length); i++) {
            if ((fileStatus[i].isDirectory()) == true) {
                FileStatus[] cf = fs.listStatus(fileStatus[i].getPath(), new PathFilter() {
                    @Override
                    public boolean accept(Path p) {
                        if (WALSplitter.isSequenceIdFile(p)) {
                            return false;
                        }
                        return true;
                    }
                });
                for (int j = 0; j < (cf.length); j++) {
                    if ((cf[j].isDirectory()) == true) {
                        Assert.assertFalse(cf[j].getPath().getName().equals("cf2"));
                    }
                }
            }
        }
    }

    @Test
    public void deleteColumnFamilyTwice() throws Exception {
        Admin admin = TestDeleteColumnFamilyProcedureFromClient.TEST_UTIL.getAdmin();
        HTableDescriptor beforehtd = admin.getTableDescriptor(TestDeleteColumnFamilyProcedureFromClient.TABLENAME);
        String cfToDelete = "cf1";
        FileSystem fs = TestDeleteColumnFamilyProcedureFromClient.TEST_UTIL.getDFSCluster().getFileSystem();
        // 1 - Check if table exists in descriptor
        Assert.assertTrue(admin.isTableAvailable(TestDeleteColumnFamilyProcedureFromClient.TABLENAME));
        // 2 - Check if all the target column family exist in descriptor
        HColumnDescriptor[] families = beforehtd.getColumnFamilies();
        Boolean foundCF = false;
        for (int i = 0; i < (families.length); i++) {
            if (families[i].getNameAsString().equals(cfToDelete)) {
                foundCF = true;
                break;
            }
        }
        Assert.assertTrue(foundCF);
        // 3 - Check if table exists in FS
        Path tableDir = FSUtils.getTableDir(TestDeleteColumnFamilyProcedureFromClient.TEST_UTIL.getDefaultRootDirPath(), TestDeleteColumnFamilyProcedureFromClient.TABLENAME);
        Assert.assertTrue(fs.exists(tableDir));
        // 4 - Check if all the target column family exist in FS
        FileStatus[] fileStatus = fs.listStatus(tableDir);
        foundCF = false;
        for (int i = 0; i < (fileStatus.length); i++) {
            if ((fileStatus[i].isDirectory()) == true) {
                FileStatus[] cf = fs.listStatus(fileStatus[i].getPath(), new PathFilter() {
                    @Override
                    public boolean accept(Path p) {
                        if (p.getName().contains(RECOVERED_EDITS_DIR)) {
                            return false;
                        }
                        return true;
                    }
                });
                for (int j = 0; j < (cf.length); j++) {
                    if (((cf[j].isDirectory()) == true) && (cf[j].getPath().getName().equals(cfToDelete))) {
                        foundCF = true;
                        break;
                    }
                }
            }
            if (foundCF) {
                break;
            }
        }
        Assert.assertTrue(foundCF);
        // TEST - Disable and delete the column family
        if (admin.isTableEnabled(TestDeleteColumnFamilyProcedureFromClient.TABLENAME)) {
            admin.disableTable(TestDeleteColumnFamilyProcedureFromClient.TABLENAME);
        }
        admin.deleteColumnFamily(TestDeleteColumnFamilyProcedureFromClient.TABLENAME, Bytes.toBytes(cfToDelete));
        // 5 - Check if the target column family is gone from the FS
        fileStatus = fs.listStatus(tableDir);
        for (int i = 0; i < (fileStatus.length); i++) {
            if ((fileStatus[i].isDirectory()) == true) {
                FileStatus[] cf = fs.listStatus(fileStatus[i].getPath(), new PathFilter() {
                    @Override
                    public boolean accept(Path p) {
                        if (WALSplitter.isSequenceIdFile(p)) {
                            return false;
                        }
                        return true;
                    }
                });
                for (int j = 0; j < (cf.length); j++) {
                    if ((cf[j].isDirectory()) == true) {
                        Assert.assertFalse(cf[j].getPath().getName().equals(cfToDelete));
                    }
                }
            }
        }
        try {
            // Test: delete again
            admin.deleteColumnFamily(TestDeleteColumnFamilyProcedureFromClient.TABLENAME, Bytes.toBytes(cfToDelete));
            Assert.fail("Delete a non-exist column family should fail");
        } catch (InvalidFamilyOperationException e) {
            // Expected.
        }
    }
}

