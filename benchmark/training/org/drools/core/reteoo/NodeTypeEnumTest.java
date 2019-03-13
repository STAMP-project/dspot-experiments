/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.reteoo;


import org.junit.Assert;
import org.junit.Test;


public class NodeTypeEnumTest {
    EntryPointNode epNode = new EntryPointNode();

    Rete reteNod = new Rete();

    ObjectTypeNode otNode = new ObjectTypeNode();

    AlphaNode alphaNode = new AlphaNode();

    PropagationQueuingNode pqNpode = new PropagationQueuingNode();

    WindowNode winNode = new WindowNode();

    RightInputAdapterNode riaNode = new RightInputAdapterNode();

    RuleTerminalNode rtNode = new RuleTerminalNode();

    QueryTerminalNode qtNode = new QueryTerminalNode();

    LeftInputAdapterNode liaNode = new LeftInputAdapterNode();

    EvalConditionNode evalNode = new EvalConditionNode();

    FromNode fromNode = new FromNode();

    QueryElementNode uNode = new QueryElementNode();

    NotNode notNode = new NotNode();

    ExistsNode existsNode = new ExistsNode();

    JoinNode joinNode = new JoinNode();

    AccumulateNode accNode = new AccumulateNode();

    @Test
    public void tesObjectSource() {
        Assert.assertTrue(isObjectSource(epNode));
        Assert.assertTrue(isObjectSource(reteNod));
        Assert.assertTrue(isObjectSource(otNode));
        Assert.assertTrue(isObjectSource(alphaNode));
        Assert.assertTrue(isObjectSource(pqNpode));
        Assert.assertTrue(isObjectSource(riaNode));
        Assert.assertFalse(isObjectSource(rtNode));
        Assert.assertFalse(isObjectSource(qtNode));
        Assert.assertFalse(isObjectSource(liaNode));
        Assert.assertFalse(isObjectSource(evalNode));
        Assert.assertFalse(isObjectSource(fromNode));
        Assert.assertFalse(isObjectSource(uNode));
        Assert.assertFalse(isObjectSource(notNode));
        Assert.assertFalse(isObjectSource(existsNode));
        Assert.assertFalse(isObjectSource(joinNode));
        Assert.assertFalse(isObjectSource(accNode));
    }

    @Test
    public void tesObjectSink() {
        Assert.assertTrue(isObjectSink(epNode));
        Assert.assertTrue(isObjectSink(reteNod));
        Assert.assertTrue(isObjectSink(otNode));
        Assert.assertTrue(isObjectSink(alphaNode));
        Assert.assertTrue(isObjectSink(pqNpode));
        Assert.assertFalse(isObjectSink(riaNode));
        Assert.assertFalse(isObjectSink(rtNode));
        Assert.assertFalse(isObjectSink(qtNode));
        Assert.assertTrue(isObjectSink(liaNode));
        Assert.assertFalse(isObjectSink(evalNode));
        Assert.assertFalse(isObjectSink(fromNode));
        Assert.assertFalse(isObjectSink(uNode));
        Assert.assertFalse(isObjectSink(notNode));
        Assert.assertFalse(isObjectSink(existsNode));
        Assert.assertFalse(isObjectSink(joinNode));
        Assert.assertFalse(isObjectSink(accNode));
    }

    @Test
    public void tesLeftTupleSource() {
        Assert.assertFalse(isLeftTupleSource(epNode));
        Assert.assertFalse(isLeftTupleSource(reteNod));
        Assert.assertFalse(isLeftTupleSource(otNode));
        Assert.assertFalse(isLeftTupleSource(alphaNode));
        Assert.assertFalse(isLeftTupleSource(pqNpode));
        Assert.assertFalse(isLeftTupleSource(riaNode));
        Assert.assertFalse(isLeftTupleSource(rtNode));
        Assert.assertFalse(isLeftTupleSource(qtNode));
        Assert.assertTrue(isLeftTupleSource(liaNode));
        Assert.assertTrue(isLeftTupleSource(evalNode));
        Assert.assertTrue(isLeftTupleSource(fromNode));
        Assert.assertTrue(isLeftTupleSource(uNode));
        Assert.assertTrue(isLeftTupleSource(notNode));
        Assert.assertTrue(isLeftTupleSource(existsNode));
        Assert.assertTrue(isLeftTupleSource(joinNode));
        Assert.assertTrue(isLeftTupleSource(accNode));
    }

    @Test
    public void tesLeftTupleSink() {
        Assert.assertFalse(isLeftTupleSink(epNode));
        Assert.assertFalse(isLeftTupleSink(reteNod));
        Assert.assertFalse(isLeftTupleSink(otNode));
        Assert.assertFalse(isLeftTupleSink(alphaNode));
        Assert.assertFalse(isLeftTupleSink(pqNpode));
        Assert.assertTrue(isLeftTupleSink(riaNode));
        Assert.assertTrue(isLeftTupleSink(rtNode));
        Assert.assertTrue(isLeftTupleSink(qtNode));
        Assert.assertFalse(isLeftTupleSink(liaNode));
        Assert.assertTrue(isLeftTupleSink(evalNode));
        Assert.assertTrue(isLeftTupleSink(fromNode));
        Assert.assertTrue(isLeftTupleSink(uNode));
        Assert.assertTrue(isLeftTupleSink(notNode));
        Assert.assertTrue(isLeftTupleSink(existsNode));
        Assert.assertTrue(isLeftTupleSink(joinNode));
        Assert.assertTrue(isLeftTupleSink(accNode));
    }

    @Test
    public void testBetaNode() {
        Assert.assertFalse(isBetaNode(epNode));
        Assert.assertFalse(isBetaNode(reteNod));
        Assert.assertFalse(isBetaNode(otNode));
        Assert.assertFalse(isBetaNode(alphaNode));
        Assert.assertFalse(isBetaNode(pqNpode));
        Assert.assertFalse(isBetaNode(riaNode));
        Assert.assertFalse(isBetaNode(rtNode));
        Assert.assertFalse(isBetaNode(qtNode));
        Assert.assertFalse(isBetaNode(liaNode));
        Assert.assertFalse(isBetaNode(evalNode));
        Assert.assertFalse(isBetaNode(fromNode));
        Assert.assertFalse(isBetaNode(uNode));
        Assert.assertTrue(isBetaNode(notNode));
        Assert.assertTrue(isBetaNode(existsNode));
        Assert.assertTrue(isBetaNode(joinNode));
        Assert.assertTrue(isBetaNode(accNode));
    }
}

