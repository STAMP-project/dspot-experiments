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
package org.apache.hadoop.yarn.api.resource;


import AllocationTagNamespaceType.SELF;
import TargetType.ALLOCATION_TAG;
import TargetType.NODE_ATTRIBUTE;
import org.apache.hadoop.yarn.api.resource.PlacementConstraint.AbstractConstraint;
import org.apache.hadoop.yarn.api.resource.PlacementConstraint.And;
import org.apache.hadoop.yarn.api.resource.PlacementConstraint.SingleConstraint;
import org.apache.hadoop.yarn.api.resource.PlacementConstraint.TargetExpression;
import org.junit.Assert;
import org.junit.Test;

import static PlacementTargets.allocationTag;
import static PlacementTargets.nodeAttribute;


/**
 * Test class for the various static methods in
 * {@link org.apache.hadoop.yarn.api.resource.PlacementConstraints}.
 */
public class TestPlacementConstraints {
    @Test
    public void testNodeAffinityToTag() {
        AbstractConstraint constraintExpr = PlacementConstraints.targetIn(PlacementConstraints.NODE, allocationTag("hbase-m"));
        SingleConstraint sConstraint = ((SingleConstraint) (constraintExpr));
        Assert.assertEquals(PlacementConstraints.NODE, sConstraint.getScope());
        Assert.assertEquals(1, sConstraint.getMinCardinality());
        Assert.assertEquals(Integer.MAX_VALUE, sConstraint.getMaxCardinality());
        Assert.assertEquals(1, sConstraint.getTargetExpressions().size());
        TargetExpression tExpr = sConstraint.getTargetExpressions().iterator().next();
        Assert.assertEquals(SELF.toString(), tExpr.getTargetKey());
        Assert.assertEquals(ALLOCATION_TAG, tExpr.getTargetType());
        Assert.assertEquals(1, tExpr.getTargetValues().size());
        Assert.assertEquals("hbase-m", tExpr.getTargetValues().iterator().next());
        PlacementConstraint constraint = PlacementConstraints.build(constraintExpr);
        Assert.assertNotNull(constraint.getConstraintExpr());
    }

    @Test
    public void testNodeAntiAffinityToAttribute() {
        AbstractConstraint constraintExpr = PlacementConstraints.targetNotIn(PlacementConstraints.NODE, nodeAttribute("java", "1.8"));
        SingleConstraint sConstraint = ((SingleConstraint) (constraintExpr));
        Assert.assertEquals(PlacementConstraints.NODE, sConstraint.getScope());
        Assert.assertEquals(0, sConstraint.getMinCardinality());
        Assert.assertEquals(0, sConstraint.getMaxCardinality());
        Assert.assertEquals(1, sConstraint.getTargetExpressions().size());
        TargetExpression tExpr = sConstraint.getTargetExpressions().iterator().next();
        Assert.assertEquals("java", tExpr.getTargetKey());
        Assert.assertEquals(NODE_ATTRIBUTE, tExpr.getTargetType());
        Assert.assertEquals(1, tExpr.getTargetValues().size());
        Assert.assertEquals("1.8", tExpr.getTargetValues().iterator().next());
    }

    @Test
    public void testAndConstraint() {
        AbstractConstraint constraintExpr = PlacementConstraints.and(PlacementConstraints.targetIn(PlacementConstraints.RACK, allocationTag("spark")), PlacementConstraints.maxCardinality(PlacementConstraints.NODE, 3, "spark"), PlacementConstraints.targetCardinality(PlacementConstraints.RACK, 2, 10, allocationTag("zk")));
        And andExpr = ((And) (constraintExpr));
        Assert.assertEquals(3, andExpr.getChildren().size());
        SingleConstraint sConstr = ((SingleConstraint) (andExpr.getChildren().get(0)));
        TargetExpression tExpr = sConstr.getTargetExpressions().iterator().next();
        Assert.assertEquals("spark", tExpr.getTargetValues().iterator().next());
        sConstr = ((SingleConstraint) (andExpr.getChildren().get(1)));
        Assert.assertEquals(0, sConstr.getMinCardinality());
        Assert.assertEquals(3, sConstr.getMaxCardinality());
        sConstr = ((SingleConstraint) (andExpr.getChildren().get(2)));
        Assert.assertEquals(2, sConstr.getMinCardinality());
        Assert.assertEquals(10, sConstr.getMaxCardinality());
    }
}

