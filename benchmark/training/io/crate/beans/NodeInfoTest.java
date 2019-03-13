/**
 * This file is part of a module with proprietary Enterprise Features.
 *
 * Licensed to Crate.io Inc. ("Crate.io") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 *
 * To use this file, Crate.io must have given you permission to enable and
 * use such Enterprise Features and you must have a valid Enterprise or
 * Subscription Agreement with Crate.io.  If you enable or use the Enterprise
 * Features, you represent and warrant that you have a valid Enterprise or
 * Subscription Agreement with Crate.io.  Your use of the Enterprise Features
 * if governed by the terms and conditions of your Enterprise or Subscription
 * Agreement with Crate.io.
 */
package io.crate.beans;


import io.crate.testing.DiscoveryNodes;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class NodeInfoTest {
    @Test
    public void testNodeInfo() {
        NodeInfo nodeInfo = new NodeInfo(() -> DiscoveryNodes.newNode("testNode", "testNodeId"), () -> 1L);
        MatcherAssert.assertThat(nodeInfo.getNodeId(), Matchers.is("testNodeId"));
        MatcherAssert.assertThat(nodeInfo.getNodeName(), Matchers.is("testNode"));
        MatcherAssert.assertThat(nodeInfo.getClusterStateVersion(), Matchers.is(1L));
    }
}

