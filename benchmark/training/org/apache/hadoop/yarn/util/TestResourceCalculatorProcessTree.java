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
package org.apache.hadoop.yarn.util;


import org.apache.hadoop.conf.Configuration;
import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.core.IsSame;
import org.junit.Assert;
import org.junit.Test;


/**
 * A JUnit test to test {@link ResourceCalculatorPlugin}
 */
public class TestResourceCalculatorProcessTree {
    public static class EmptyProcessTree extends ResourceCalculatorProcessTree {
        public EmptyProcessTree(String pid) {
            super(pid);
        }

        public void updateProcessTree() {
        }

        public String getProcessTreeDump() {
            return "Empty tree for testing";
        }

        public long getRssMemorySize(int age) {
            return 0;
        }

        @SuppressWarnings("deprecation")
        public long getCumulativeRssmem(int age) {
            return 0;
        }

        public long getVirtualMemorySize(int age) {
            return 0;
        }

        @SuppressWarnings("deprecation")
        public long getCumulativeVmem(int age) {
            return 0;
        }

        public long getCumulativeCpuTime() {
            return 0;
        }

        @Override
        public float getCpuUsagePercent() {
            return UNAVAILABLE;
        }

        public boolean checkPidPgrpidForMatch() {
            return false;
        }
    }

    @Test
    public void testCreateInstance() {
        ResourceCalculatorProcessTree tree;
        tree = ResourceCalculatorProcessTree.getResourceCalculatorProcessTree("1", TestResourceCalculatorProcessTree.EmptyProcessTree.class, new Configuration());
        Assert.assertNotNull(tree);
        Assert.assertThat(tree, IsInstanceOf.instanceOf(TestResourceCalculatorProcessTree.EmptyProcessTree.class));
    }

    @Test
    public void testCreatedInstanceConfigured() {
        ResourceCalculatorProcessTree tree;
        Configuration conf = new Configuration();
        tree = ResourceCalculatorProcessTree.getResourceCalculatorProcessTree("1", TestResourceCalculatorProcessTree.EmptyProcessTree.class, conf);
        Assert.assertNotNull(tree);
        Assert.assertThat(tree.getConf(), IsSame.sameInstance(conf));
    }
}

