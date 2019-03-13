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
package org.apache.hadoop.hbase.constraint;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Do the complex testing of constraints against a minicluster
 */
@Category({ MiscTests.class, MediumTests.class })
public class TestConstraint {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestConstraint.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestConstraint.class);

    private static HBaseTestingUtility util;

    private static final TableName tableName = TableName.valueOf("test");

    private static final byte[] dummy = Bytes.toBytes("dummy");

    private static final byte[] row1 = Bytes.toBytes("r1");

    private static final byte[] test = Bytes.toBytes("test");

    /**
     * Test that we run a passing constraint
     *
     * @throws Exception
     * 		
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testConstraintPasses() throws Exception {
        // create the table
        // it would be nice if this was also a method on the util
        HTableDescriptor desc = new HTableDescriptor(TestConstraint.tableName);
        for (byte[] family : new byte[][]{ TestConstraint.dummy, TestConstraint.test }) {
            desc.addFamily(new HColumnDescriptor(family));
        }
        // add a constraint
        Constraints.add(desc, TestConstraint.CheckWasRunConstraint.class);
        TestConstraint.util.getAdmin().createTable(desc);
        Table table = TestConstraint.util.getConnection().getTable(TestConstraint.tableName);
        try {
            // test that we don't fail on a valid put
            Put put = new Put(TestConstraint.row1);
            byte[] value = Bytes.toBytes(Integer.toString(10));
            byte[] qualifier = new byte[0];
            put.addColumn(TestConstraint.dummy, qualifier, value);
            table.put(put);
        } finally {
            table.close();
        }
        Assert.assertTrue(TestConstraint.CheckWasRunConstraint.wasRun);
    }

    /**
     * Test that constraints will fail properly
     *
     * @throws Exception
     * 		
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testConstraintFails() throws Exception {
        // create the table
        // it would be nice if this was also a method on the util
        HTableDescriptor desc = new HTableDescriptor(TestConstraint.tableName);
        for (byte[] family : new byte[][]{ TestConstraint.dummy, TestConstraint.test }) {
            desc.addFamily(new HColumnDescriptor(family));
        }
        // add a constraint that is sure to fail
        Constraints.add(desc, AllFailConstraint.class);
        TestConstraint.util.getAdmin().createTable(desc);
        Table table = TestConstraint.util.getConnection().getTable(TestConstraint.tableName);
        // test that we do fail on violation
        Put put = new Put(TestConstraint.row1);
        byte[] qualifier = new byte[0];
        put.addColumn(TestConstraint.dummy, qualifier, Bytes.toBytes("fail"));
        TestConstraint.LOG.warn("Doing put in table");
        try {
            table.put(put);
            Assert.fail("This put should not have suceeded - AllFailConstraint was not run!");
        } catch (ConstraintException e) {
            // expected
        }
        table.close();
    }

    /**
     * Check that if we just disable one constraint, then
     *
     * @throws Throwable
     * 		
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testDisableConstraint() throws Throwable {
        // create the table
        HTableDescriptor desc = new HTableDescriptor(TestConstraint.tableName);
        // add a family to the table
        for (byte[] family : new byte[][]{ TestConstraint.dummy, TestConstraint.test }) {
            desc.addFamily(new HColumnDescriptor(family));
        }
        // add a constraint to make sure it others get run
        Constraints.add(desc, TestConstraint.CheckWasRunConstraint.class);
        // Add Constraint to check
        Constraints.add(desc, AllFailConstraint.class);
        // and then disable the failing constraint
        Constraints.disableConstraint(desc, AllFailConstraint.class);
        TestConstraint.util.getAdmin().createTable(desc);
        Table table = TestConstraint.util.getConnection().getTable(TestConstraint.tableName);
        try {
            // test that we don't fail because its disabled
            Put put = new Put(TestConstraint.row1);
            byte[] qualifier = new byte[0];
            put.addColumn(TestConstraint.dummy, qualifier, Bytes.toBytes("pass"));
            table.put(put);
        } finally {
            table.close();
        }
        Assert.assertTrue(TestConstraint.CheckWasRunConstraint.wasRun);
    }

    /**
     * Test that if we disable all constraints, then nothing gets run
     *
     * @throws Throwable
     * 		
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testDisableConstraints() throws Throwable {
        // create the table
        HTableDescriptor desc = new HTableDescriptor(TestConstraint.tableName);
        // add a family to the table
        for (byte[] family : new byte[][]{ TestConstraint.dummy, TestConstraint.test }) {
            desc.addFamily(new HColumnDescriptor(family));
        }
        // add a constraint to check to see if is run
        Constraints.add(desc, TestConstraint.CheckWasRunConstraint.class);
        // then disable all the constraints
        Constraints.disable(desc);
        TestConstraint.util.getAdmin().createTable(desc);
        Table table = TestConstraint.util.getConnection().getTable(TestConstraint.tableName);
        try {
            // test that we do fail on violation
            Put put = new Put(TestConstraint.row1);
            byte[] qualifier = new byte[0];
            put.addColumn(TestConstraint.dummy, qualifier, Bytes.toBytes("pass"));
            TestConstraint.LOG.warn("Doing put in table");
            table.put(put);
        } finally {
            table.close();
        }
        Assert.assertFalse(TestConstraint.CheckWasRunConstraint.wasRun);
    }

    /**
     * Check to make sure a constraint is unloaded when it fails
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testIsUnloaded() throws Exception {
        // create the table
        HTableDescriptor desc = new HTableDescriptor(TestConstraint.tableName);
        // add a family to the table
        for (byte[] family : new byte[][]{ TestConstraint.dummy, TestConstraint.test }) {
            desc.addFamily(new HColumnDescriptor(family));
        }
        // make sure that constraints are unloaded
        Constraints.add(desc, RuntimeFailConstraint.class);
        // add a constraint to check to see if is run
        Constraints.add(desc, TestConstraint.CheckWasRunConstraint.class);
        TestConstraint.CheckWasRunConstraint.wasRun = false;
        TestConstraint.util.getAdmin().createTable(desc);
        Table table = TestConstraint.util.getConnection().getTable(TestConstraint.tableName);
        // test that we do fail on violation
        Put put = new Put(TestConstraint.row1);
        byte[] qualifier = new byte[0];
        put.addColumn(TestConstraint.dummy, qualifier, Bytes.toBytes("pass"));
        try {
            table.put(put);
            Assert.fail("RuntimeFailConstraint wasn't triggered - this put shouldn't work!");
        } catch (Exception e) {
            // NOOP
        }
        // try the put again, this time constraints are not used, so it works
        table.put(put);
        // and we make sure that constraints were not run...
        Assert.assertFalse(TestConstraint.CheckWasRunConstraint.wasRun);
        table.close();
    }

    /**
     * Constraint to check that it was actually run (or not)
     */
    public static class CheckWasRunConstraint extends BaseConstraint {
        public static boolean wasRun = false;

        @Override
        public void check(Put p) {
            TestConstraint.CheckWasRunConstraint.wasRun = true;
        }
    }
}

