/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.common;


import NodeTypeEnums.ExistsNode;
import NodeTypeEnums.JoinNode;
import Operator.EQUAL;
import Operator.LESS;
import Operator.NOT_EQUAL;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.test.model.Cheese;
import org.junit.Test;


public class SingleBetaConstraintsTest extends BaseBetaConstraintsTest {
    @Test
    public void testIndexed() {
        BetaNodeFieldConstraint constraint0 = getConstraint("cheeseType0", EQUAL, "type", Cheese.class);
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[]{ constraint0 };
        checkBetaConstraints(constraints, SingleBetaConstraints.class);
    }

    @Test
    public void testNotIndexed() {
        BetaNodeFieldConstraint constraint0 = getConstraint("cheeseType0", NOT_EQUAL, "type", Cheese.class);
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[]{ constraint0 };
        checkBetaConstraints(constraints, SingleBetaConstraints.class);
    }

    @Test
    public void testIndexedForComparison() {
        BetaNodeFieldConstraint constraint0 = getConstraint("cheeseType0", LESS, "type", Cheese.class);
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[]{ constraint0 };
        checkBetaConstraints(constraints, SingleBetaConstraints.class, ExistsNode);
    }

    @Test
    public void testNotIndexedForComparison() {
        BetaNodeFieldConstraint constraint0 = getConstraint("cheeseType0", LESS, "type", Cheese.class);
        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[]{ constraint0 };
        checkBetaConstraints(constraints, SingleBetaConstraints.class, JoinNode);
    }
}

