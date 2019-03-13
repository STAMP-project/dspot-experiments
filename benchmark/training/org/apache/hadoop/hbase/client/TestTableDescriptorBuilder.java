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
package org.apache.hadoop.hbase.client;


import Durability.ASYNC_WAL;
import TableName.META_TABLE_NAME;
import TableName.VALID_USER_TABLE_REGEX;
import java.io.IOException;
import java.util.regex.Pattern;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.exceptions.DeserializationException;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.BuilderStyleTest;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test setting values in the descriptor
 */
@Category({ MiscTests.class, SmallTests.class })
public class TestTableDescriptorBuilder {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestTableDescriptorBuilder.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestTableDescriptorBuilder.class);

    @Rule
    public TestName name = new TestName();

    @Test(expected = IOException.class)
    public void testAddCoprocessorTwice() throws IOException {
        String cpName = "a.b.c.d";
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(META_TABLE_NAME).setCoprocessor(cpName).setCoprocessor(cpName).build();
    }

    @Test
    public void testPb() throws IOException, DeserializationException {
        final int v = 123;
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(META_TABLE_NAME).setMaxFileSize(v).setDurability(ASYNC_WAL).setReadOnly(true).setRegionReplication(2).build();
        byte[] bytes = TableDescriptorBuilder.toByteArray(htd);
        TableDescriptor deserializedHtd = TableDescriptorBuilder.parseFrom(bytes);
        Assert.assertEquals(htd, deserializedHtd);
        Assert.assertEquals(v, deserializedHtd.getMaxFileSize());
        Assert.assertTrue(deserializedHtd.isReadOnly());
        Assert.assertEquals(ASYNC_WAL, deserializedHtd.getDurability());
        Assert.assertEquals(2, deserializedHtd.getRegionReplication());
    }

    /**
     * Test cps in the table description
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetSetRemoveCP() throws Exception {
        // simple CP
        String className = "org.apache.hadoop.hbase.coprocessor.SimpleRegionObserver";
        TableDescriptor desc = // add and check that it is present
        TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setCoprocessor(className).build();
        Assert.assertTrue(desc.hasCoprocessor(className));
        desc = // remove it and check that it is gone
        TableDescriptorBuilder.newBuilder(desc).removeCoprocessor(className).build();
        Assert.assertFalse(desc.hasCoprocessor(className));
    }

    /**
     * Test cps in the table description
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSetListRemoveCP() throws Exception {
        TableDescriptor desc = TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).build();
        // Check that any coprocessor is present.
        Assert.assertTrue(desc.getCoprocessorDescriptors().isEmpty());
        // simple CP
        String className1 = "org.apache.hadoop.hbase.coprocessor.SimpleRegionObserver";
        String className2 = "org.apache.hadoop.hbase.coprocessor.SampleRegionWALObserver";
        desc = // Add the 1 coprocessor and check if present.
        TableDescriptorBuilder.newBuilder(desc).setCoprocessor(className1).build();
        Assert.assertTrue(((desc.getCoprocessorDescriptors().size()) == 1));
        Assert.assertTrue(desc.getCoprocessorDescriptors().stream().map(CoprocessorDescriptor::getClassName).anyMatch(( name) -> name.equals(className1)));
        desc = // Add the 2nd coprocessor and check if present.
        // remove it and check that it is gone
        TableDescriptorBuilder.newBuilder(desc).setCoprocessor(className2).build();
        Assert.assertTrue(((desc.getCoprocessorDescriptors().size()) == 2));
        Assert.assertTrue(desc.getCoprocessorDescriptors().stream().map(CoprocessorDescriptor::getClassName).anyMatch(( name) -> name.equals(className2)));
        desc = // Remove one and check
        TableDescriptorBuilder.newBuilder(desc).removeCoprocessor(className1).build();
        Assert.assertTrue(((desc.getCoprocessorDescriptors().size()) == 1));
        Assert.assertFalse(desc.getCoprocessorDescriptors().stream().map(CoprocessorDescriptor::getClassName).anyMatch(( name) -> name.equals(className1)));
        Assert.assertTrue(desc.getCoprocessorDescriptors().stream().map(CoprocessorDescriptor::getClassName).anyMatch(( name) -> name.equals(className2)));
        desc = // Remove the last and check
        TableDescriptorBuilder.newBuilder(desc).removeCoprocessor(className2).build();
        Assert.assertTrue(desc.getCoprocessorDescriptors().isEmpty());
        Assert.assertFalse(desc.getCoprocessorDescriptors().stream().map(CoprocessorDescriptor::getClassName).anyMatch(( name) -> name.equals(className1)));
        Assert.assertFalse(desc.getCoprocessorDescriptors().stream().map(CoprocessorDescriptor::getClassName).anyMatch(( name) -> name.equals(className2)));
    }

    /**
     * Test that we add and remove strings from settings properly.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRemoveString() throws Exception {
        byte[] key = Bytes.toBytes("Some");
        byte[] value = Bytes.toBytes("value");
        TableDescriptor desc = TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setValue(key, value).build();
        Assert.assertTrue(Bytes.equals(value, desc.getValue(key)));
        desc = TableDescriptorBuilder.newBuilder(desc).removeValue(key).build();
        Assert.assertTrue(((desc.getValue(key)) == null));
    }

    String[] legalTableNames = new String[]{ "foo", "with-dash_under.dot", "_under_start_ok", "with-dash.with_underscore", "02-01-2012.my_table_01-02", "xyz._mytable_", "9_9_0.table_02", "dot1.dot2.table", "new.-mytable", "with-dash.with.dot", "legal..t2", "legal..legal.t2", "trailingdots..", "trailing.dots...", "ns:mytable", "ns:_mytable_", "ns:my_table_01-02" };

    String[] illegalTableNames = new String[]{ ".dot_start_illegal", "-dash_start_illegal", "spaces not ok", "-dash-.start_illegal", "new.table with space", "01 .table", "ns:-illegaldash", "new:.illegaldot", "new:illegalcolon1:", "new:illegalcolon1:2" };

    @Test
    public void testLegalTableNames() {
        for (String tn : legalTableNames) {
            TableName.isLegalFullyQualifiedTableName(Bytes.toBytes(tn));
        }
    }

    @Test
    public void testIllegalTableNames() {
        for (String tn : illegalTableNames) {
            try {
                TableName.isLegalFullyQualifiedTableName(Bytes.toBytes(tn));
                Assert.fail((("invalid tablename " + tn) + " should have failed"));
            } catch (Exception e) {
                // expected
            }
        }
    }

    @Test
    public void testLegalTableNamesRegex() {
        for (String tn : legalTableNames) {
            TableName tName = TableName.valueOf(tn);
            Assert.assertTrue((("Testing: '" + tn) + "'"), Pattern.matches(VALID_USER_TABLE_REGEX, tName.getNameAsString()));
        }
    }

    @Test
    public void testIllegalTableNamesRegex() {
        for (String tn : illegalTableNames) {
            TestTableDescriptorBuilder.LOG.info((("Testing: '" + tn) + "'"));
            Assert.assertFalse(Pattern.matches(VALID_USER_TABLE_REGEX, tn));
        }
    }

    /**
     * Test default value handling for maxFileSize
     */
    @Test
    public void testGetMaxFileSize() {
        TableDescriptor desc = TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).build();
        Assert.assertEquals((-1), desc.getMaxFileSize());
        desc = TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setMaxFileSize(1111L).build();
        Assert.assertEquals(1111L, desc.getMaxFileSize());
    }

    /**
     * Test default value handling for memStoreFlushSize
     */
    @Test
    public void testGetMemStoreFlushSize() {
        TableDescriptor desc = TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).build();
        Assert.assertEquals((-1), desc.getMemStoreFlushSize());
        desc = TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setMemStoreFlushSize(1111L).build();
        Assert.assertEquals(1111L, desc.getMemStoreFlushSize());
    }

    @Test
    public void testClassMethodsAreBuilderStyle() {
        BuilderStyleTest.assertClassesAreBuilderStyle(TableDescriptorBuilder.class);
    }

    @Test
    public void testModifyFamily() {
        byte[] familyName = Bytes.toBytes("cf");
        ColumnFamilyDescriptor hcd = ColumnFamilyDescriptorBuilder.newBuilder(familyName).setBlocksize(1000).setDFSReplication(((short) (3))).build();
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setColumnFamily(hcd).build();
        Assert.assertEquals(1000, htd.getColumnFamily(familyName).getBlocksize());
        Assert.assertEquals(3, htd.getColumnFamily(familyName).getDFSReplication());
        hcd = ColumnFamilyDescriptorBuilder.newBuilder(familyName).setBlocksize(2000).setDFSReplication(((short) (1))).build();
        htd = TableDescriptorBuilder.newBuilder(htd).modifyColumnFamily(hcd).build();
        Assert.assertEquals(2000, htd.getColumnFamily(familyName).getBlocksize());
        Assert.assertEquals(1, htd.getColumnFamily(familyName).getDFSReplication());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testModifyInexistentFamily() {
        byte[] familyName = Bytes.toBytes("cf");
        HColumnDescriptor hcd = new HColumnDescriptor(familyName);
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).modifyColumnFamily(hcd).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddDuplicateFamilies() {
        byte[] familyName = Bytes.toBytes("cf");
        ColumnFamilyDescriptor hcd = ColumnFamilyDescriptorBuilder.newBuilder(familyName).setBlocksize(1000).build();
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setColumnFamily(hcd).build();
        Assert.assertEquals(1000, htd.getColumnFamily(familyName).getBlocksize());
        hcd = ColumnFamilyDescriptorBuilder.newBuilder(familyName).setBlocksize(2000).build();
        // add duplicate column
        TableDescriptorBuilder.newBuilder(htd).setColumnFamily(hcd).build();
    }

    @Test
    public void testPriority() {
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setPriority(42).build();
        Assert.assertEquals(42, htd.getPriority());
    }
}

