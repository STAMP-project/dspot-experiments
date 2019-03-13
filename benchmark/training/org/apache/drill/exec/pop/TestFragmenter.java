/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.pop;


import java.io.IOException;
import org.apache.drill.categories.PlannerTest;
import org.apache.drill.exec.exception.FragmentSetupException;
import org.apache.drill.exec.planner.PhysicalPlanReader;
import org.apache.drill.exec.planner.PhysicalPlanReaderTestFactory;
import org.apache.drill.exec.planner.fragment.Fragment;
import org.apache.drill.exec.work.foreman.ForemanSetupException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(PlannerTest.class)
public class TestFragmenter extends PopUnitTestBase {
    static final Logger logger = LoggerFactory.getLogger(TestFragmenter.class);

    @Test
    public void ensureOneFragment() throws IOException, FragmentSetupException, ForemanSetupException {
        PhysicalPlanReader ppr = PhysicalPlanReaderTestFactory.defaultPhysicalPlanReader(PopUnitTestBase.CONFIG);
        Fragment b = PopUnitTestBase.getRootFragment(ppr, "/physical_test1.json");
        Assert.assertEquals(1, PopUnitTestBase.getFragmentCount(b));
        Assert.assertEquals(0, b.getReceivingExchangePairs().size());
        Assert.assertNull(b.getSendingExchange());
    }

    @Test
    public void ensureThreeFragments() throws IOException, FragmentSetupException, ForemanSetupException {
        PhysicalPlanReader ppr = PhysicalPlanReaderTestFactory.defaultPhysicalPlanReader(PopUnitTestBase.CONFIG);
        Fragment b = PopUnitTestBase.getRootFragment(ppr, "/physical_double_exchange.json");
        TestFragmenter.logger.debug("Fragment Node {}", b);
        Assert.assertEquals(3, PopUnitTestBase.getFragmentCount(b));
        Assert.assertEquals(1, b.getReceivingExchangePairs().size());
        Assert.assertNull(b.getSendingExchange());
        // get first child.
        b = b.iterator().next().getNode();
        Assert.assertEquals(1, b.getReceivingExchangePairs().size());
        Assert.assertNotNull(b.getSendingExchange());
        b = b.iterator().next().getNode();
        Assert.assertEquals(0, b.getReceivingExchangePairs().size());
        Assert.assertNotNull(b.getSendingExchange());
    }
}

