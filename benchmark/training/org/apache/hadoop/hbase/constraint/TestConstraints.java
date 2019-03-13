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


import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


/**
 * Test reading/writing the constraints into the {@link HTableDescriptor}
 */
@Category({ MiscTests.class, SmallTests.class })
public class TestConstraints {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestConstraints.class);

    @Rule
    public TestName name = new TestName();

    @SuppressWarnings("unchecked")
    @Test
    public void testSimpleReadWrite() throws Throwable {
        HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(name.getMethodName()));
        Constraints.add(desc, WorksConstraint.class);
        List<? extends Constraint> constraints = Constraints.getConstraints(desc, this.getClass().getClassLoader());
        Assert.assertEquals(1, constraints.size());
        Assert.assertEquals(WorksConstraint.class, constraints.get(0).getClass());
        // Check that we can add more than 1 constraint and that ordering is
        // preserved
        Constraints.add(desc, TestConstraints.AlsoWorks.class, WorksConstraint.NameConstraint.class);
        constraints = Constraints.getConstraints(desc, this.getClass().getClassLoader());
        Assert.assertEquals(3, constraints.size());
        Assert.assertEquals(WorksConstraint.class, constraints.get(0).getClass());
        Assert.assertEquals(TestConstraints.AlsoWorks.class, constraints.get(1).getClass());
        Assert.assertEquals(WorksConstraint.NameConstraint.class, constraints.get(2).getClass());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testReadWriteWithConf() throws Throwable {
        HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(name.getMethodName()));
        Constraints.add(desc, new org.apache.hadoop.hbase.util.Pair(CheckConfigurationConstraint.class, CheckConfigurationConstraint.getConfiguration()));
        List<? extends Constraint> c = Constraints.getConstraints(desc, this.getClass().getClassLoader());
        Assert.assertEquals(1, c.size());
        Assert.assertEquals(CheckConfigurationConstraint.class, c.get(0).getClass());
        // check to make sure that we overwrite configurations
        Constraints.add(desc, new org.apache.hadoop.hbase.util.Pair(CheckConfigurationConstraint.class, new Configuration(false)));
        try {
            Constraints.getConstraints(desc, this.getClass().getClassLoader());
            Assert.fail("No exception thrown  - configuration not overwritten");
        } catch (IllegalArgumentException e) {
            // expect to have the exception, so don't do anything
        }
    }

    /**
     * Test that Constraints are properly enabled, disabled, and removed
     *
     * @throws Exception
     * 		
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testEnableDisableRemove() throws Exception {
        HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(name.getMethodName()));
        // check general enabling/disabling of constraints
        // first add a constraint
        Constraints.add(desc, AllPassConstraint.class);
        // make sure everything is enabled
        Assert.assertTrue(Constraints.enabled(desc, AllPassConstraint.class));
        Assert.assertTrue(desc.hasCoprocessor(ConstraintProcessor.class.getName()));
        // check disabling
        Constraints.disable(desc);
        Assert.assertFalse(desc.hasCoprocessor(ConstraintProcessor.class.getName()));
        // make sure the added constraints are still present
        Assert.assertTrue(Constraints.enabled(desc, AllPassConstraint.class));
        // check just removing the single constraint
        Constraints.remove(desc, AllPassConstraint.class);
        Assert.assertFalse(Constraints.has(desc, AllPassConstraint.class));
        // Add back the single constraint
        Constraints.add(desc, AllPassConstraint.class);
        // and now check that when we remove constraints, all are gone
        Constraints.remove(desc);
        Assert.assertFalse(desc.hasCoprocessor(ConstraintProcessor.class.getName()));
        Assert.assertFalse(Constraints.has(desc, AllPassConstraint.class));
    }

    /**
     * Test that when we update a constraint the ordering is not modified.
     *
     * @throws Exception
     * 		
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateConstraint() throws Exception {
        HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(name.getMethodName()));
        Constraints.add(desc, CheckConfigurationConstraint.class, TestConstraint.CheckWasRunConstraint.class);
        Constraints.setConfiguration(desc, CheckConfigurationConstraint.class, CheckConfigurationConstraint.getConfiguration());
        List<? extends Constraint> constraints = Constraints.getConstraints(desc, this.getClass().getClassLoader());
        Assert.assertEquals(2, constraints.size());
        // check to make sure the order didn't change
        Assert.assertEquals(CheckConfigurationConstraint.class, constraints.get(0).getClass());
        Assert.assertEquals(TestConstraint.CheckWasRunConstraint.class, constraints.get(1).getClass());
    }

    /**
     * Test that if a constraint hasn't been set that there are no problems with
     * attempting to remove it.
     *
     * @throws Throwable
     * 		on failure.
     */
    @Test
    public void testRemoveUnsetConstraint() throws Throwable {
        HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(name.getMethodName()));
        Constraints.remove(desc);
        Constraints.remove(desc, TestConstraints.AlsoWorks.class);
    }

    @Test
    public void testConfigurationPreserved() throws Throwable {
        Configuration conf = new Configuration();
        conf.setBoolean("_ENABLED", false);
        conf.setLong("_PRIORITY", 10);
        HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(name.getMethodName()));
        Constraints.add(desc, TestConstraints.AlsoWorks.class, conf);
        Constraints.add(desc, WorksConstraint.class);
        Assert.assertFalse(Constraints.enabled(desc, TestConstraints.AlsoWorks.class));
        List<? extends Constraint> constraints = Constraints.getConstraints(desc, this.getClass().getClassLoader());
        for (Constraint c : constraints) {
            Configuration storedConf = c.getConf();
            if (c instanceof TestConstraints.AlsoWorks)
                Assert.assertEquals(10, storedConf.getLong("_PRIORITY", (-1)));
            else// its just a worksconstraint

                Assert.assertEquals(2, storedConf.getLong("_PRIORITY", (-1)));

        }
    }

    // ---------- Constraints just used for testing
    /**
     * Also just works
     */
    public static class AlsoWorks extends BaseConstraint {
        @Override
        public void check(Put p) {
            // NOOP
        }
    }
}

