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
package org.apache.flink.optimizer;


import DataExchangeMode.PIPELINED;
import LocalStrategy.NONE;
import LocalStrategy.SORT;
import Order.ANY;
import Order.ASCENDING;
import Order.DESCENDING;
import ShipStrategyType.BROADCAST;
import ShipStrategyType.FORWARD;
import ShipStrategyType.PARTITION_HASH;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.common.operators.util.FieldList;
import org.apache.flink.api.common.operators.util.FieldSet;
import org.apache.flink.optimizer.dataproperties.GlobalProperties;
import org.apache.flink.optimizer.dataproperties.LocalProperties;
import org.apache.flink.optimizer.dataproperties.RequestedGlobalProperties;
import org.apache.flink.optimizer.dataproperties.RequestedLocalProperties;
import org.apache.flink.optimizer.plan.Channel;
import org.apache.flink.optimizer.plan.DualInputPlanNode;
import org.apache.flink.optimizer.plan.SingleInputPlanNode;
import org.apache.flink.optimizer.plan.SourcePlanNode;
import org.apache.flink.runtime.operators.DriverStrategy;
import org.junit.Assert;
import org.junit.Test;


public class FeedbackPropertiesMatchTest {
    @Test
    public void testNoPartialSolutionFoundSingleInputOnly() {
        try {
            SourcePlanNode target = new SourcePlanNode(FeedbackPropertiesMatchTest.getSourceNode(), "Source");
            SourcePlanNode otherTarget = new SourcePlanNode(FeedbackPropertiesMatchTest.getSourceNode(), "Source");
            Channel toMap1 = new Channel(target);
            toMap1.setShipStrategy(FORWARD, PIPELINED);
            toMap1.setLocalStrategy(NONE);
            SingleInputPlanNode map1 = new SingleInputPlanNode(FeedbackPropertiesMatchTest.getMapNode(), "Mapper 1", toMap1, DriverStrategy.MAP);
            Channel toMap2 = new Channel(map1);
            toMap2.setShipStrategy(FORWARD, PIPELINED);
            toMap2.setLocalStrategy(NONE);
            SingleInputPlanNode map2 = new SingleInputPlanNode(FeedbackPropertiesMatchTest.getMapNode(), "Mapper 2", toMap2, DriverStrategy.MAP);
            {
                GlobalProperties gp = new GlobalProperties();
                LocalProperties lp = new LocalProperties();
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(otherTarget, gp, lp);
                Assert.assertTrue((report == (NO_PARTIAL_SOLUTION)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSingleInputOperators() {
        try {
            SourcePlanNode target = new SourcePlanNode(FeedbackPropertiesMatchTest.getSourceNode(), "Source");
            Channel toMap1 = new Channel(target);
            toMap1.setShipStrategy(FORWARD, PIPELINED);
            toMap1.setLocalStrategy(NONE);
            SingleInputPlanNode map1 = new SingleInputPlanNode(FeedbackPropertiesMatchTest.getMapNode(), "Mapper 1", toMap1, DriverStrategy.MAP);
            Channel toMap2 = new Channel(map1);
            toMap2.setShipStrategy(FORWARD, PIPELINED);
            toMap2.setLocalStrategy(NONE);
            SingleInputPlanNode map2 = new SingleInputPlanNode(FeedbackPropertiesMatchTest.getMapNode(), "Mapper 2", toMap2, DriverStrategy.MAP);
            // no feedback properties and none are ever required and present
            {
                GlobalProperties gp = new GlobalProperties();
                LocalProperties lp = new LocalProperties();
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // some global feedback properties and none are ever required and present
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(2, 5));
                LocalProperties lp = new LocalProperties();
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // some local feedback properties and none are ever required and present
            {
                GlobalProperties gp = new GlobalProperties();
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(1, 2));
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // some global and local feedback properties and none are ever required and present
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(2, 5));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(1, 2));
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // --------------------------- requirements on channel 1 -----------------------
            // some required global properties, which are matched exactly
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(2, 5));
                LocalProperties lp = new LocalProperties();
                RequestedGlobalProperties reqGp = new RequestedGlobalProperties();
                reqGp.setHashPartitioned(new FieldList(2, 5));
                toMap1.setRequiredGlobalProps(reqGp);
                toMap1.setRequiredLocalProps(null);
                toMap2.setRequiredGlobalProps(null);
                toMap2.setRequiredLocalProps(null);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // some required local properties, which are matched exactly
            {
                GlobalProperties gp = new GlobalProperties();
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(1, 2));
                RequestedLocalProperties reqLp = new RequestedLocalProperties();
                reqLp.setGroupedFields(new FieldList(1, 2));
                toMap1.setRequiredGlobalProps(null);
                toMap1.setRequiredLocalProps(reqLp);
                toMap2.setRequiredGlobalProps(null);
                toMap2.setRequiredLocalProps(null);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // some required global and local properties, which are matched exactly
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(2, 5));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(1, 2));
                RequestedGlobalProperties reqGp = new RequestedGlobalProperties();
                reqGp.setHashPartitioned(new FieldList(2, 5));
                RequestedLocalProperties reqLp = new RequestedLocalProperties();
                reqLp.setGroupedFields(new FieldList(1, 2));
                toMap1.setRequiredGlobalProps(reqGp);
                toMap1.setRequiredLocalProps(reqLp);
                toMap2.setRequiredGlobalProps(null);
                toMap2.setRequiredLocalProps(null);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // some required global and local properties, which are over-fulfilled
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(2));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(1, 2));
                RequestedGlobalProperties reqGp = new RequestedGlobalProperties();
                reqGp.setHashPartitioned(new FieldSet(2, 5));
                RequestedLocalProperties reqLp = new RequestedLocalProperties();
                reqLp.setGroupedFields(new FieldList(1));
                toMap1.setRequiredGlobalProps(reqGp);
                toMap1.setRequiredLocalProps(reqLp);
                toMap2.setRequiredGlobalProps(null);
                toMap2.setRequiredLocalProps(null);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // some required global properties that are not met
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(2, 1));
                LocalProperties lp = new LocalProperties();
                RequestedGlobalProperties reqGp = new RequestedGlobalProperties();
                reqGp.setHashPartitioned(new FieldList(2, 5));
                toMap1.setRequiredGlobalProps(reqGp);
                toMap1.setRequiredLocalProps(null);
                toMap2.setRequiredGlobalProps(null);
                toMap2.setRequiredLocalProps(null);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(NOT_MET, report);
            }
            // some required local properties that are not met
            {
                GlobalProperties gp = new GlobalProperties();
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(1));
                RequestedLocalProperties reqLp = new RequestedLocalProperties();
                reqLp.setGroupedFields(new FieldList(2, 1));
                toMap1.setRequiredGlobalProps(null);
                toMap1.setRequiredLocalProps(reqLp);
                toMap2.setRequiredGlobalProps(null);
                toMap2.setRequiredLocalProps(null);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(NOT_MET, report);
            }
            // some required global and local properties where the global properties are not met
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(2, 1));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(1));
                RequestedGlobalProperties reqGp = new RequestedGlobalProperties();
                reqGp.setAnyPartitioning(new FieldList(2, 5));
                RequestedLocalProperties reqLp = new RequestedLocalProperties();
                reqLp.setGroupedFields(new FieldList(1));
                toMap1.setRequiredGlobalProps(reqGp);
                toMap1.setRequiredLocalProps(reqLp);
                toMap2.setRequiredGlobalProps(null);
                toMap2.setRequiredLocalProps(null);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(NOT_MET, report);
            }
            // some required global and local properties where the local properties are not met
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(1));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(1));
                RequestedGlobalProperties reqGp = new RequestedGlobalProperties();
                reqGp.setAnyPartitioning(new FieldList(1));
                RequestedLocalProperties reqLp = new RequestedLocalProperties();
                reqLp.setGroupedFields(new FieldList(2));
                toMap1.setRequiredGlobalProps(reqGp);
                toMap1.setRequiredLocalProps(reqLp);
                toMap2.setRequiredGlobalProps(null);
                toMap2.setRequiredLocalProps(null);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(NOT_MET, report);
            }
            // --------------------------- requirements on channel 2 -----------------------
            // some required global properties, which are matched exactly
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(2, 5));
                LocalProperties lp = new LocalProperties();
                RequestedGlobalProperties reqGp = new RequestedGlobalProperties();
                reqGp.setHashPartitioned(new FieldList(2, 5));
                toMap1.setRequiredGlobalProps(null);
                toMap1.setRequiredLocalProps(null);
                toMap2.setRequiredGlobalProps(reqGp);
                toMap2.setRequiredLocalProps(null);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // some required local properties, which are matched exactly
            {
                GlobalProperties gp = new GlobalProperties();
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(1, 2));
                RequestedLocalProperties reqLp = new RequestedLocalProperties();
                reqLp.setGroupedFields(new FieldList(1, 2));
                toMap1.setRequiredGlobalProps(null);
                toMap1.setRequiredLocalProps(null);
                toMap2.setRequiredGlobalProps(null);
                toMap2.setRequiredLocalProps(reqLp);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // some required global and local properties, which are matched exactly
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(2, 5));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(1, 2));
                RequestedGlobalProperties reqGp = new RequestedGlobalProperties();
                reqGp.setHashPartitioned(new FieldList(2, 5));
                RequestedLocalProperties reqLp = new RequestedLocalProperties();
                reqLp.setGroupedFields(new FieldList(1, 2));
                toMap1.setRequiredGlobalProps(null);
                toMap1.setRequiredLocalProps(null);
                toMap2.setRequiredGlobalProps(reqGp);
                toMap2.setRequiredLocalProps(reqLp);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // some required global and local properties, which are over-fulfilled
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(2));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(1, 2));
                RequestedGlobalProperties reqGp = new RequestedGlobalProperties();
                reqGp.setHashPartitioned(new FieldSet(2, 5));
                RequestedLocalProperties reqLp = new RequestedLocalProperties();
                reqLp.setGroupedFields(new FieldList(1));
                toMap1.setRequiredGlobalProps(null);
                toMap1.setRequiredLocalProps(null);
                toMap2.setRequiredGlobalProps(reqGp);
                toMap2.setRequiredLocalProps(reqLp);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // some required global properties that are not met
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(2, 1));
                LocalProperties lp = new LocalProperties();
                RequestedGlobalProperties reqGp = new RequestedGlobalProperties();
                reqGp.setHashPartitioned(new FieldSet(2, 5));
                toMap1.setRequiredGlobalProps(null);
                toMap1.setRequiredLocalProps(null);
                toMap2.setRequiredGlobalProps(reqGp);
                toMap2.setRequiredLocalProps(null);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(NOT_MET, report);
            }
            // some required local properties that are not met
            {
                GlobalProperties gp = new GlobalProperties();
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(1));
                RequestedLocalProperties reqLp = new RequestedLocalProperties();
                reqLp.setGroupedFields(new FieldList(2, 1));
                toMap1.setRequiredGlobalProps(null);
                toMap1.setRequiredLocalProps(null);
                toMap2.setRequiredGlobalProps(null);
                toMap2.setRequiredLocalProps(reqLp);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(NOT_MET, report);
            }
            // some required global and local properties where the global properties are not met
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(2, 1));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(1));
                RequestedGlobalProperties reqGp = new RequestedGlobalProperties();
                reqGp.setAnyPartitioning(new FieldSet(2, 5));
                RequestedLocalProperties reqLp = new RequestedLocalProperties();
                reqLp.setGroupedFields(new FieldList(1));
                toMap1.setRequiredGlobalProps(null);
                toMap1.setRequiredLocalProps(null);
                toMap2.setRequiredGlobalProps(reqGp);
                toMap2.setRequiredLocalProps(reqLp);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(NOT_MET, report);
            }
            // some required global and local properties where the local properties are not met
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(1));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(1));
                RequestedGlobalProperties reqGp = new RequestedGlobalProperties();
                reqGp.setAnyPartitioning(new FieldList(1));
                RequestedLocalProperties reqLp = new RequestedLocalProperties();
                reqLp.setGroupedFields(new FieldList(2));
                toMap1.setRequiredGlobalProps(null);
                toMap1.setRequiredLocalProps(null);
                toMap2.setRequiredGlobalProps(reqGp);
                toMap2.setRequiredLocalProps(reqLp);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(NOT_MET, report);
            }
            // ---------------------- requirements mixed on 1 and 2 -----------------------
            // some required global properties at step one and some more at step 2
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(1, 2));
                LocalProperties lp = LocalProperties.EMPTY;
                RequestedGlobalProperties reqGp1 = new RequestedGlobalProperties();
                reqGp1.setAnyPartitioning(new FieldList(1, 2));
                RequestedGlobalProperties reqGp2 = new RequestedGlobalProperties();
                reqGp2.setHashPartitioned(new FieldList(1, 2));
                toMap1.setRequiredGlobalProps(reqGp1);
                toMap1.setRequiredLocalProps(null);
                toMap2.setRequiredGlobalProps(reqGp2);
                toMap2.setRequiredLocalProps(null);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // some required local properties at step one and some more at step 2
            {
                GlobalProperties gp = new GlobalProperties();
                LocalProperties lp = LocalProperties.forOrdering(new org.apache.flink.api.common.operators.Ordering(3, null, Order.ASCENDING).appendOrdering(1, null, DESCENDING));
                RequestedLocalProperties reqLp1 = new RequestedLocalProperties();
                reqLp1.setGroupedFields(new FieldList(3, 1));
                RequestedLocalProperties reqLp2 = new RequestedLocalProperties();
                reqLp2.setOrdering(new org.apache.flink.api.common.operators.Ordering(3, null, Order.ANY).appendOrdering(1, null, ANY));
                toMap1.setRequiredGlobalProps(null);
                toMap1.setRequiredLocalProps(reqLp1);
                toMap2.setRequiredGlobalProps(null);
                toMap2.setRequiredLocalProps(reqLp2);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // some required global properties at step one and some local ones at step 2
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(1, 2));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                RequestedGlobalProperties reqGp = new RequestedGlobalProperties();
                reqGp.setAnyPartitioning(new FieldList(1, 2));
                RequestedLocalProperties reqLp = new RequestedLocalProperties();
                reqLp.setGroupedFields(new FieldList(2));
                toMap1.setRequiredGlobalProps(reqGp);
                toMap1.setRequiredLocalProps(null);
                toMap2.setRequiredGlobalProps(null);
                toMap2.setRequiredLocalProps(reqLp);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // some required local properties at step one and some global ones at step 2
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(1, 2));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                RequestedGlobalProperties reqGp = new RequestedGlobalProperties();
                reqGp.setAnyPartitioning(new FieldList(1, 2));
                RequestedLocalProperties reqLp = new RequestedLocalProperties();
                reqLp.setGroupedFields(new FieldList(2));
                toMap1.setRequiredGlobalProps(null);
                toMap1.setRequiredLocalProps(reqLp);
                toMap2.setRequiredGlobalProps(reqGp);
                toMap2.setRequiredLocalProps(null);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // some fulfilled global properties at step one and some non-fulfilled local ones at step 2
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(1, 2));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                RequestedGlobalProperties reqGp = new RequestedGlobalProperties();
                reqGp.setAnyPartitioning(new FieldList(1, 2));
                RequestedLocalProperties reqLp = new RequestedLocalProperties();
                reqLp.setGroupedFields(new FieldList(2, 3));
                toMap1.setRequiredGlobalProps(reqGp);
                toMap1.setRequiredLocalProps(null);
                toMap2.setRequiredGlobalProps(null);
                toMap2.setRequiredLocalProps(reqLp);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(NOT_MET, report);
            }
            // some fulfilled local properties at step one and some non-fulfilled global ones at step 2
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(1, 2));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                RequestedGlobalProperties reqGp = new RequestedGlobalProperties();
                reqGp.setAnyPartitioning(new FieldList(2, 3));
                RequestedLocalProperties reqLp = new RequestedLocalProperties();
                reqLp.setGroupedFields(new FieldList(2, 1));
                toMap1.setRequiredGlobalProps(null);
                toMap1.setRequiredLocalProps(reqLp);
                toMap2.setRequiredGlobalProps(reqGp);
                toMap2.setRequiredLocalProps(null);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(NOT_MET, report);
            }
            // some non-fulfilled global properties at step one and some fulfilled local ones at step 2
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(1, 2));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                RequestedGlobalProperties reqGp = new RequestedGlobalProperties();
                reqGp.setAnyPartitioning(new FieldList(2, 3));
                RequestedLocalProperties reqLp = new RequestedLocalProperties();
                reqLp.setGroupedFields(new FieldList(2, 1));
                toMap1.setRequiredGlobalProps(reqGp);
                toMap1.setRequiredLocalProps(null);
                toMap2.setRequiredGlobalProps(null);
                toMap2.setRequiredLocalProps(reqLp);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(NOT_MET, report);
            }
            // some non-fulfilled local properties at step one and some fulfilled global ones at step 2
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(1, 2));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                RequestedGlobalProperties reqGp = new RequestedGlobalProperties();
                reqGp.setAnyPartitioning(new FieldList(1, 2));
                RequestedLocalProperties reqLp = new RequestedLocalProperties();
                reqLp.setGroupedFields(new FieldList(2, 1, 3));
                toMap1.setRequiredGlobalProps(null);
                toMap1.setRequiredLocalProps(reqLp);
                toMap2.setRequiredGlobalProps(reqGp);
                toMap2.setRequiredLocalProps(null);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(NOT_MET, report);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSingleInputOperatorsWithReCreation() {
        try {
            SourcePlanNode target = new SourcePlanNode(FeedbackPropertiesMatchTest.getSourceNode(), "Source");
            Channel toMap1 = new Channel(target);
            SingleInputPlanNode map1 = new SingleInputPlanNode(FeedbackPropertiesMatchTest.getMapNode(), "Mapper 1", toMap1, DriverStrategy.MAP);
            Channel toMap2 = new Channel(map1);
            SingleInputPlanNode map2 = new SingleInputPlanNode(FeedbackPropertiesMatchTest.getMapNode(), "Mapper 2", toMap2, DriverStrategy.MAP);
            // set ship strategy in first channel, so later non matching global properties do not matter
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(1, 2));
                LocalProperties lp = LocalProperties.EMPTY;
                RequestedGlobalProperties reqGp = new RequestedGlobalProperties();
                reqGp.setAnyPartitioning(new FieldSet(2, 5));
                toMap1.setShipStrategy(PARTITION_HASH, new FieldList(2, 5), PIPELINED);
                toMap1.setLocalStrategy(NONE);
                toMap2.setShipStrategy(FORWARD, PIPELINED);
                toMap2.setLocalStrategy(NONE);
                toMap1.setRequiredGlobalProps(null);
                toMap1.setRequiredLocalProps(null);
                toMap2.setRequiredGlobalProps(reqGp);
                toMap2.setRequiredLocalProps(null);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(MET, report);
            }
            // set ship strategy in second channel, so previous non matching global properties void the match
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(1, 2));
                LocalProperties lp = LocalProperties.EMPTY;
                RequestedGlobalProperties reqGp = new RequestedGlobalProperties();
                reqGp.setAnyPartitioning(new FieldSet(2, 5));
                toMap1.setShipStrategy(FORWARD, PIPELINED);
                toMap1.setLocalStrategy(NONE);
                toMap2.setShipStrategy(PARTITION_HASH, new FieldList(2, 5), PIPELINED);
                toMap2.setLocalStrategy(NONE);
                toMap1.setRequiredGlobalProps(reqGp);
                toMap1.setRequiredLocalProps(null);
                toMap2.setRequiredGlobalProps(null);
                toMap2.setRequiredLocalProps(null);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(NOT_MET, report);
            }
            // set local strategy in first channel, so later non matching local properties do not matter
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(1, 2));
                LocalProperties lp = LocalProperties.forOrdering(new org.apache.flink.api.common.operators.Ordering(3, null, Order.ASCENDING).appendOrdering(1, null, DESCENDING));
                RequestedLocalProperties reqLp = new RequestedLocalProperties();
                reqLp.setGroupedFields(new FieldList(4, 1));
                toMap1.setShipStrategy(FORWARD, PIPELINED);
                toMap1.setLocalStrategy(SORT, new FieldList(5, 7), new boolean[]{ false, false });
                toMap2.setShipStrategy(FORWARD, PIPELINED);
                toMap2.setLocalStrategy(NONE);
                toMap1.setRequiredGlobalProps(null);
                toMap1.setRequiredLocalProps(null);
                toMap2.setRequiredGlobalProps(null);
                toMap2.setRequiredLocalProps(reqLp);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // set local strategy in second channel, so previous non matching local properties void the match
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(1, 2));
                LocalProperties lp = LocalProperties.forOrdering(new org.apache.flink.api.common.operators.Ordering(3, null, Order.ASCENDING).appendOrdering(1, null, DESCENDING));
                RequestedLocalProperties reqLp = new RequestedLocalProperties();
                reqLp.setGroupedFields(new FieldList(4, 1));
                toMap1.setShipStrategy(FORWARD, PIPELINED);
                toMap1.setLocalStrategy(NONE);
                toMap2.setShipStrategy(FORWARD, PIPELINED);
                toMap2.setLocalStrategy(SORT, new FieldList(5, 7), new boolean[]{ false, false });
                toMap1.setRequiredGlobalProps(null);
                toMap1.setRequiredLocalProps(reqLp);
                toMap2.setRequiredGlobalProps(null);
                toMap2.setRequiredLocalProps(null);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(NOT_MET, report);
            }
            // create the properties on the same node as the requirement
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(1, 2));
                LocalProperties lp = LocalProperties.forOrdering(new org.apache.flink.api.common.operators.Ordering(3, null, Order.ASCENDING).appendOrdering(1, null, DESCENDING));
                RequestedGlobalProperties reqGp = new RequestedGlobalProperties();
                reqGp.setAnyPartitioning(new FieldSet(5, 7));
                RequestedLocalProperties reqLp = new RequestedLocalProperties();
                reqLp.setGroupedFields(new FieldList(5, 7));
                toMap1.setShipStrategy(PARTITION_HASH, new FieldList(5, 7), PIPELINED);
                toMap1.setLocalStrategy(SORT, new FieldList(5, 7), new boolean[]{ false, false });
                toMap2.setShipStrategy(FORWARD, PIPELINED);
                toMap2.setLocalStrategy(NONE);
                toMap1.setRequiredGlobalProps(reqGp);
                toMap1.setRequiredLocalProps(reqLp);
                toMap2.setRequiredGlobalProps(null);
                toMap2.setRequiredLocalProps(null);
                FeedbackPropertiesMeetRequirementsReport report = map2.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(MET, report);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSingleInputOperatorsChainOfThree() {
        try {
            SourcePlanNode target = new SourcePlanNode(FeedbackPropertiesMatchTest.getSourceNode(), "Source");
            Channel toMap1 = new Channel(target);
            SingleInputPlanNode map1 = new SingleInputPlanNode(FeedbackPropertiesMatchTest.getMapNode(), "Mapper 1", toMap1, DriverStrategy.MAP);
            Channel toMap2 = new Channel(map1);
            SingleInputPlanNode map2 = new SingleInputPlanNode(FeedbackPropertiesMatchTest.getMapNode(), "Mapper 2", toMap2, DriverStrategy.MAP);
            Channel toMap3 = new Channel(map2);
            SingleInputPlanNode map3 = new SingleInputPlanNode(FeedbackPropertiesMatchTest.getMapNode(), "Mapper 3", toMap3, DriverStrategy.MAP);
            // set local strategy in first channel, so later non matching local properties do not matter
            {
                GlobalProperties gp = new GlobalProperties();
                LocalProperties lp = LocalProperties.forOrdering(new org.apache.flink.api.common.operators.Ordering(3, null, Order.ASCENDING).appendOrdering(1, null, DESCENDING));
                RequestedLocalProperties reqLp = new RequestedLocalProperties();
                reqLp.setGroupedFields(new FieldList(4, 1));
                toMap1.setShipStrategy(FORWARD, PIPELINED);
                toMap1.setLocalStrategy(SORT, new FieldList(5, 7), new boolean[]{ false, false });
                toMap2.setShipStrategy(FORWARD, PIPELINED);
                toMap2.setLocalStrategy(NONE);
                toMap3.setShipStrategy(FORWARD, PIPELINED);
                toMap3.setLocalStrategy(NONE);
                toMap1.setRequiredGlobalProps(null);
                toMap1.setRequiredLocalProps(null);
                toMap2.setRequiredGlobalProps(null);
                toMap2.setRequiredLocalProps(null);
                toMap3.setRequiredGlobalProps(null);
                toMap3.setRequiredLocalProps(reqLp);
                FeedbackPropertiesMeetRequirementsReport report = map3.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // set global strategy in first channel, so later non matching global properties do not matter
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(5, 3));
                LocalProperties lp = LocalProperties.EMPTY;
                RequestedGlobalProperties reqGp = new RequestedGlobalProperties();
                reqGp.setAnyPartitioning(new FieldSet(2, 3));
                toMap1.setShipStrategy(PARTITION_HASH, new FieldList(1, 2), PIPELINED);
                toMap1.setLocalStrategy(NONE);
                toMap2.setShipStrategy(FORWARD, PIPELINED);
                toMap2.setLocalStrategy(NONE);
                toMap3.setShipStrategy(FORWARD, PIPELINED);
                toMap3.setLocalStrategy(NONE);
                toMap1.setRequiredGlobalProps(null);
                toMap1.setRequiredLocalProps(null);
                toMap2.setRequiredGlobalProps(null);
                toMap2.setRequiredLocalProps(null);
                toMap3.setRequiredGlobalProps(reqGp);
                toMap3.setRequiredLocalProps(null);
                FeedbackPropertiesMeetRequirementsReport report = map3.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testNoPartialSolutionFoundTwoInputOperator() {
        try {
            SourcePlanNode target = new SourcePlanNode(FeedbackPropertiesMatchTest.getSourceNode(), "Partial Solution");
            SourcePlanNode source1 = new SourcePlanNode(FeedbackPropertiesMatchTest.getSourceNode(), "Source 1");
            SourcePlanNode source2 = new SourcePlanNode(FeedbackPropertiesMatchTest.getSourceNode(), "Source 2");
            Channel toMap1 = new Channel(source1);
            toMap1.setShipStrategy(FORWARD, PIPELINED);
            toMap1.setLocalStrategy(NONE);
            SingleInputPlanNode map1 = new SingleInputPlanNode(FeedbackPropertiesMatchTest.getMapNode(), "Mapper 1", toMap1, DriverStrategy.MAP);
            Channel toMap2 = new Channel(source2);
            toMap2.setShipStrategy(FORWARD, PIPELINED);
            toMap2.setLocalStrategy(NONE);
            SingleInputPlanNode map2 = new SingleInputPlanNode(FeedbackPropertiesMatchTest.getMapNode(), "Mapper 2", toMap2, DriverStrategy.MAP);
            Channel toJoin1 = new Channel(map1);
            Channel toJoin2 = new Channel(map2);
            toJoin1.setShipStrategy(FORWARD, PIPELINED);
            toJoin1.setLocalStrategy(NONE);
            toJoin2.setShipStrategy(FORWARD, PIPELINED);
            toJoin2.setLocalStrategy(NONE);
            DualInputPlanNode join = new DualInputPlanNode(FeedbackPropertiesMatchTest.getJoinNode(), "Join", toJoin1, toJoin2, DriverStrategy.HYBRIDHASH_BUILD_FIRST);
            FeedbackPropertiesMeetRequirementsReport report = join.checkPartialSolutionPropertiesMet(target, new GlobalProperties(), new LocalProperties());
            Assert.assertEquals(NO_PARTIAL_SOLUTION, report);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testTwoOperatorsOneIndependent() {
        try {
            SourcePlanNode target = new SourcePlanNode(FeedbackPropertiesMatchTest.getSourceNode(), "Partial Solution");
            SourcePlanNode source = new SourcePlanNode(FeedbackPropertiesMatchTest.getSourceNode(), "Other Source");
            Channel toMap1 = new Channel(target);
            toMap1.setShipStrategy(FORWARD, PIPELINED);
            toMap1.setLocalStrategy(NONE);
            SingleInputPlanNode map1 = new SingleInputPlanNode(FeedbackPropertiesMatchTest.getMapNode(), "Mapper 1", toMap1, DriverStrategy.MAP);
            Channel toMap2 = new Channel(source);
            toMap2.setShipStrategy(FORWARD, PIPELINED);
            toMap2.setLocalStrategy(NONE);
            SingleInputPlanNode map2 = new SingleInputPlanNode(FeedbackPropertiesMatchTest.getMapNode(), "Mapper 2", toMap2, DriverStrategy.MAP);
            Channel toJoin1 = new Channel(map1);
            Channel toJoin2 = new Channel(map2);
            DualInputPlanNode join = new DualInputPlanNode(FeedbackPropertiesMatchTest.getJoinNode(), "Join", toJoin1, toJoin2, DriverStrategy.HYBRIDHASH_BUILD_FIRST);
            Channel toAfterJoin = new Channel(join);
            toAfterJoin.setShipStrategy(FORWARD, PIPELINED);
            toAfterJoin.setLocalStrategy(NONE);
            SingleInputPlanNode afterJoin = new SingleInputPlanNode(FeedbackPropertiesMatchTest.getMapNode(), "After Join Mapper", toAfterJoin, DriverStrategy.MAP);
            // attach some properties to the non-relevant input
            {
                toMap2.setShipStrategy(BROADCAST, PIPELINED);
                toMap2.setLocalStrategy(SORT, new FieldList(2, 7), new boolean[]{ true, true });
                RequestedGlobalProperties joinGp = new RequestedGlobalProperties();
                joinGp.setFullyReplicated();
                RequestedLocalProperties joinLp = new RequestedLocalProperties();
                joinLp.setOrdering(new org.apache.flink.api.common.operators.Ordering(2, null, Order.ASCENDING).appendOrdering(7, null, ASCENDING));
                toJoin2.setShipStrategy(FORWARD, PIPELINED);
                toJoin2.setLocalStrategy(NONE);
                toJoin2.setRequiredGlobalProps(joinGp);
                toJoin2.setRequiredLocalProps(joinLp);
            }
            // ------------------------------------------------------------------------------------
            // no properties from the partial solution, no required properties
            {
                toJoin1.setShipStrategy(FORWARD, PIPELINED);
                toJoin1.setLocalStrategy(NONE);
                GlobalProperties gp = new GlobalProperties();
                LocalProperties lp = LocalProperties.EMPTY;
                FeedbackPropertiesMeetRequirementsReport report = join.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // some properties from the partial solution, no required properties
            {
                toJoin1.setShipStrategy(FORWARD, PIPELINED);
                toJoin1.setLocalStrategy(NONE);
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(0));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                FeedbackPropertiesMeetRequirementsReport report = join.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // produced properties match relevant input
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(0));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                RequestedGlobalProperties rgp = new RequestedGlobalProperties();
                rgp.setHashPartitioned(new FieldList(0));
                RequestedLocalProperties rlp = new RequestedLocalProperties();
                rlp.setGroupedFields(new FieldList(2));
                toJoin1.setRequiredGlobalProps(rgp);
                toJoin1.setRequiredLocalProps(rlp);
                toJoin1.setShipStrategy(FORWARD, PIPELINED);
                toJoin1.setLocalStrategy(NONE);
                FeedbackPropertiesMeetRequirementsReport report = join.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // produced properties do not match relevant input
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(0));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                RequestedGlobalProperties rgp = new RequestedGlobalProperties();
                rgp.setHashPartitioned(new FieldList(0));
                RequestedLocalProperties rlp = new RequestedLocalProperties();
                rlp.setGroupedFields(new FieldList(1, 2, 3));
                toJoin1.setRequiredGlobalProps(rgp);
                toJoin1.setRequiredLocalProps(rlp);
                toJoin1.setShipStrategy(FORWARD, PIPELINED);
                toJoin1.setLocalStrategy(NONE);
                FeedbackPropertiesMeetRequirementsReport report = join.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(NOT_MET, report);
            }
            // produced properties overridden before join
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(0));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                RequestedGlobalProperties rgp = new RequestedGlobalProperties();
                rgp.setHashPartitioned(new FieldList(0));
                RequestedLocalProperties rlp = new RequestedLocalProperties();
                rlp.setGroupedFields(new FieldList(2, 1));
                toMap1.setRequiredGlobalProps(rgp);
                toMap1.setRequiredLocalProps(rlp);
                toJoin1.setRequiredGlobalProps(null);
                toJoin1.setRequiredLocalProps(null);
                toJoin1.setShipStrategy(PARTITION_HASH, new FieldList(2, 1), PIPELINED);
                toJoin1.setLocalStrategy(SORT, new FieldList(7, 3), new boolean[]{ true, false });
                FeedbackPropertiesMeetRequirementsReport report = join.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(MET, report);
            }
            // produced properties before join match, after join match as well
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(0));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                RequestedGlobalProperties rgp = new RequestedGlobalProperties();
                rgp.setHashPartitioned(new FieldList(0));
                RequestedLocalProperties rlp = new RequestedLocalProperties();
                rlp.setGroupedFields(new FieldList(2, 1));
                toMap1.setRequiredGlobalProps(null);
                toMap1.setRequiredLocalProps(null);
                toJoin1.setShipStrategy(FORWARD, PIPELINED);
                toJoin1.setLocalStrategy(NONE);
                toJoin1.setRequiredGlobalProps(rgp);
                toJoin1.setRequiredLocalProps(rlp);
                toAfterJoin.setShipStrategy(FORWARD, PIPELINED);
                toAfterJoin.setLocalStrategy(NONE);
                toAfterJoin.setRequiredGlobalProps(rgp);
                toAfterJoin.setRequiredLocalProps(rlp);
                FeedbackPropertiesMeetRequirementsReport report = join.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // produced properties before join match, after join do not match
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(0));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                RequestedGlobalProperties rgp1 = new RequestedGlobalProperties();
                rgp1.setHashPartitioned(new FieldList(0));
                RequestedGlobalProperties rgp2 = new RequestedGlobalProperties();
                rgp2.setHashPartitioned(new FieldList(3));
                RequestedLocalProperties rlp1 = new RequestedLocalProperties();
                rlp1.setGroupedFields(new FieldList(2, 1));
                RequestedLocalProperties rlp2 = new RequestedLocalProperties();
                rlp2.setGroupedFields(new FieldList(3, 4));
                toJoin1.setRequiredGlobalProps(rgp1);
                toJoin1.setRequiredLocalProps(rlp1);
                toAfterJoin.setShipStrategy(FORWARD, PIPELINED);
                toAfterJoin.setLocalStrategy(NONE);
                toAfterJoin.setRequiredGlobalProps(rgp2);
                toAfterJoin.setRequiredLocalProps(rlp2);
                FeedbackPropertiesMeetRequirementsReport report = afterJoin.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(NOT_MET, report);
            }
            // produced properties are overridden, does not matter that they do not match
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setAnyPartitioning(new FieldList(0));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                RequestedGlobalProperties rgp = new RequestedGlobalProperties();
                rgp.setHashPartitioned(new FieldList(1));
                RequestedLocalProperties rlp = new RequestedLocalProperties();
                rlp.setGroupedFields(new FieldList(1, 2, 3));
                toJoin1.setRequiredGlobalProps(null);
                toJoin1.setRequiredLocalProps(null);
                toJoin1.setShipStrategy(PARTITION_HASH, new FieldList(2, 1), PIPELINED);
                toJoin1.setLocalStrategy(SORT, new FieldList(7, 3), new boolean[]{ true, false });
                toAfterJoin.setRequiredGlobalProps(rgp);
                toAfterJoin.setRequiredLocalProps(rlp);
                FeedbackPropertiesMeetRequirementsReport report = afterJoin.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(MET, report);
            }
            // local property overridden before join, local property mismatch after join not relevant
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setAnyPartitioning(new FieldList(0));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                RequestedLocalProperties rlp = new RequestedLocalProperties();
                rlp.setGroupedFields(new FieldList(1, 2, 3));
                toJoin1.setRequiredGlobalProps(null);
                toJoin1.setRequiredLocalProps(null);
                toJoin1.setShipStrategy(FORWARD, PIPELINED);
                toJoin1.setLocalStrategy(SORT, new FieldList(7, 3), new boolean[]{ true, false });
                toAfterJoin.setRequiredGlobalProps(null);
                toAfterJoin.setRequiredLocalProps(rlp);
                FeedbackPropertiesMeetRequirementsReport report = afterJoin.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // local property overridden before join, global property mismatch after join void the match
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setAnyPartitioning(new FieldList(0));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                RequestedGlobalProperties rgp = new RequestedGlobalProperties();
                rgp.setHashPartitioned(new FieldList(1));
                RequestedLocalProperties rlp = new RequestedLocalProperties();
                rlp.setGroupedFields(new FieldList(1, 2, 3));
                toJoin1.setRequiredGlobalProps(null);
                toJoin1.setRequiredLocalProps(null);
                toJoin1.setShipStrategy(FORWARD, PIPELINED);
                toJoin1.setLocalStrategy(SORT, new FieldList(7, 3), new boolean[]{ true, false });
                toAfterJoin.setRequiredGlobalProps(rgp);
                toAfterJoin.setRequiredLocalProps(rlp);
                FeedbackPropertiesMeetRequirementsReport report = afterJoin.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(NOT_MET, report);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testTwoOperatorsBothDependent() {
        try {
            SourcePlanNode target = new SourcePlanNode(FeedbackPropertiesMatchTest.getSourceNode(), "Partial Solution");
            Channel toMap1 = new Channel(target);
            toMap1.setShipStrategy(FORWARD, PIPELINED);
            toMap1.setLocalStrategy(NONE);
            SingleInputPlanNode map1 = new SingleInputPlanNode(FeedbackPropertiesMatchTest.getMapNode(), "Mapper 1", toMap1, DriverStrategy.MAP);
            Channel toMap2 = new Channel(target);
            toMap2.setShipStrategy(FORWARD, PIPELINED);
            toMap2.setLocalStrategy(NONE);
            SingleInputPlanNode map2 = new SingleInputPlanNode(FeedbackPropertiesMatchTest.getMapNode(), "Mapper 2", toMap2, DriverStrategy.MAP);
            Channel toJoin1 = new Channel(map1);
            toJoin1.setShipStrategy(FORWARD, PIPELINED);
            toJoin1.setLocalStrategy(NONE);
            Channel toJoin2 = new Channel(map2);
            toJoin2.setShipStrategy(FORWARD, PIPELINED);
            toJoin2.setLocalStrategy(NONE);
            DualInputPlanNode join = new DualInputPlanNode(FeedbackPropertiesMatchTest.getJoinNode(), "Join", toJoin1, toJoin2, DriverStrategy.HYBRIDHASH_BUILD_FIRST);
            Channel toAfterJoin = new Channel(join);
            toAfterJoin.setShipStrategy(FORWARD, PIPELINED);
            toAfterJoin.setLocalStrategy(NONE);
            SingleInputPlanNode afterJoin = new SingleInputPlanNode(FeedbackPropertiesMatchTest.getMapNode(), "After Join Mapper", toAfterJoin, DriverStrategy.MAP);
            // no properties from the partial solution, no required properties
            {
                GlobalProperties gp = new GlobalProperties();
                LocalProperties lp = LocalProperties.EMPTY;
                FeedbackPropertiesMeetRequirementsReport report = afterJoin.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // some properties from the partial solution, no required properties
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(0));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                FeedbackPropertiesMeetRequirementsReport report = afterJoin.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // test requirements on one input and met
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(0));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                RequestedGlobalProperties rgp = new RequestedGlobalProperties();
                rgp.setHashPartitioned(new FieldList(0));
                RequestedLocalProperties rlp = new RequestedLocalProperties();
                rlp.setGroupedFields(new FieldList(2, 1));
                toJoin1.setRequiredGlobalProps(rgp);
                toJoin1.setRequiredLocalProps(rlp);
                FeedbackPropertiesMeetRequirementsReport report = afterJoin.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // test requirements on both input and met
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(0));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                RequestedGlobalProperties rgp = new RequestedGlobalProperties();
                rgp.setHashPartitioned(new FieldList(0));
                RequestedLocalProperties rlp = new RequestedLocalProperties();
                rlp.setGroupedFields(new FieldList(2, 1));
                toJoin1.setRequiredGlobalProps(rgp);
                toJoin1.setRequiredLocalProps(rlp);
                toJoin2.setRequiredGlobalProps(rgp);
                toJoin2.setRequiredLocalProps(rlp);
                FeedbackPropertiesMeetRequirementsReport report = afterJoin.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertTrue((((report != null) && (report != (NO_PARTIAL_SOLUTION))) && (report != (NOT_MET))));
            }
            // test requirements on both inputs, one not met
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(0));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                RequestedGlobalProperties rgp1 = new RequestedGlobalProperties();
                rgp1.setHashPartitioned(new FieldList(0));
                RequestedLocalProperties rlp1 = new RequestedLocalProperties();
                rlp1.setGroupedFields(new FieldList(2, 1));
                RequestedGlobalProperties rgp2 = new RequestedGlobalProperties();
                rgp2.setHashPartitioned(new FieldList(1));
                RequestedLocalProperties rlp2 = new RequestedLocalProperties();
                rlp2.setGroupedFields(new FieldList(0, 3));
                toJoin1.setRequiredGlobalProps(rgp1);
                toJoin1.setRequiredLocalProps(rlp1);
                toJoin2.setRequiredGlobalProps(rgp2);
                toJoin2.setRequiredLocalProps(rlp2);
                FeedbackPropertiesMeetRequirementsReport report = afterJoin.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(NOT_MET, report);
            }
            // test override on both inputs, later requirement ignored
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(0));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                RequestedGlobalProperties rgp = new RequestedGlobalProperties();
                rgp.setHashPartitioned(new FieldList(1));
                RequestedLocalProperties rlp = new RequestedLocalProperties();
                rlp.setGroupedFields(new FieldList(0, 3));
                toJoin1.setRequiredGlobalProps(null);
                toJoin1.setRequiredLocalProps(null);
                toJoin2.setRequiredGlobalProps(null);
                toJoin2.setRequiredLocalProps(null);
                toJoin1.setShipStrategy(PARTITION_HASH, new FieldList(88), PIPELINED);
                toJoin2.setShipStrategy(BROADCAST, PIPELINED);
                toAfterJoin.setRequiredGlobalProps(rgp);
                toAfterJoin.setRequiredLocalProps(rlp);
                FeedbackPropertiesMeetRequirementsReport report = afterJoin.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(MET, report);
            }
            // test override on one inputs, later requirement met
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(0));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                RequestedGlobalProperties rgp = new RequestedGlobalProperties();
                rgp.setHashPartitioned(new FieldList(0));
                RequestedLocalProperties rlp = new RequestedLocalProperties();
                rlp.setGroupedFields(new FieldList(2, 1));
                toJoin1.setShipStrategy(PARTITION_HASH, new FieldList(88), PIPELINED);
                toJoin2.setShipStrategy(FORWARD, PIPELINED);
                toAfterJoin.setRequiredGlobalProps(rgp);
                toAfterJoin.setRequiredLocalProps(rlp);
                FeedbackPropertiesMeetRequirementsReport report = afterJoin.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(PENDING, report);
            }
            // test override on one input, later requirement not met
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(0));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                RequestedGlobalProperties rgp = new RequestedGlobalProperties();
                rgp.setHashPartitioned(new FieldList(3));
                RequestedLocalProperties rlp = new RequestedLocalProperties();
                rlp.setGroupedFields(new FieldList(77, 69));
                toJoin1.setShipStrategy(PARTITION_HASH, new FieldList(88), PIPELINED);
                toJoin2.setShipStrategy(FORWARD, PIPELINED);
                toAfterJoin.setRequiredGlobalProps(rgp);
                toAfterJoin.setRequiredLocalProps(rlp);
                FeedbackPropertiesMeetRequirementsReport report = afterJoin.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(NOT_MET, report);
            }
            // test override on one input locally, later global requirement not met
            {
                GlobalProperties gp = new GlobalProperties();
                gp.setHashPartitioned(new FieldList(0));
                LocalProperties lp = LocalProperties.forGrouping(new FieldList(2, 1));
                RequestedGlobalProperties rgp = new RequestedGlobalProperties();
                rgp.setHashPartitioned(new FieldList(3));
                toJoin1.setShipStrategy(FORWARD, PIPELINED);
                toJoin1.setLocalStrategy(SORT, new FieldList(3), new boolean[]{ false });
                toJoin2.setShipStrategy(FORWARD, PIPELINED);
                toJoin1.setLocalStrategy(NONE);
                toAfterJoin.setRequiredGlobalProps(rgp);
                toAfterJoin.setRequiredLocalProps(null);
                FeedbackPropertiesMeetRequirementsReport report = afterJoin.checkPartialSolutionPropertiesMet(target, gp, lp);
                Assert.assertEquals(NOT_MET, report);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}

