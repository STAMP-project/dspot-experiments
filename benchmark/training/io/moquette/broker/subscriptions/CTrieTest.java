/**
 * Copyright (c) 2012-2018 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package io.moquette.broker.subscriptions;


import io.netty.handler.codec.mqtt.MqttQoS;
import java.util.Optional;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class CTrieTest {
    private CTrie sut;

    @Test
    public void testAddOnSecondLayerWithEmptyTokenOnEmptyTree() {
        // Exercise
        sut.addToTree(CTrieTest.clientSubOnTopic("TempSensor1", "/"));
        // Verify
        final Optional<CNode> matchedNode = sut.lookup(Topic.asTopic("/"));
        Assert.assertTrue("Node on path / must be present", matchedNode.isPresent());
        // verify structure, only root INode and the first CNode should be present
        assertThat(this.sut.root.mainNode().subscriptions).isEmpty();
        assertThat(this.sut.root.mainNode().allChildren()).isNotEmpty();
        INode firstLayer = this.sut.root.mainNode().allChildren().get(0);
        assertThat(firstLayer.mainNode().subscriptions).isEmpty();
        assertThat(firstLayer.mainNode().allChildren()).isNotEmpty();
        INode secondLayer = firstLayer.mainNode().allChildren().get(0);
        assertThat(secondLayer.mainNode().subscriptions).isNotEmpty();
        assertThat(secondLayer.mainNode().allChildren()).isEmpty();
    }

    @Test
    public void testAddFirstLayerNodeOnEmptyTree() {
        // Exercise
        sut.addToTree(CTrieTest.clientSubOnTopic("TempSensor1", "/temp"));
        // Verify
        final Optional<CNode> matchedNode = sut.lookup(Topic.asTopic("/temp"));
        Assert.assertTrue("Node on path /temp must be present", matchedNode.isPresent());
        Assert.assertFalse(matchedNode.get().subscriptions.isEmpty());
    }

    @Test
    public void testLookup() {
        final Subscription existingSubscription = CTrieTest.clientSubOnTopic("TempSensor1", "/temp");
        sut.addToTree(existingSubscription);
        // Exercise
        final Optional<CNode> matchedNode = sut.lookup(Topic.asTopic("/humidity"));
        // Verify
        Assert.assertFalse("Node on path /humidity can't be present", matchedNode.isPresent());
    }

    @Test
    public void testAddNewSubscriptionOnExistingNode() {
        final Subscription existingSubscription = CTrieTest.clientSubOnTopic("TempSensor1", "/temp");
        sut.addToTree(existingSubscription);
        // Exercise
        final Subscription newSubscription = CTrieTest.clientSubOnTopic("TempSensor2", "/temp");
        sut.addToTree(newSubscription);
        // Verify
        final Optional<CNode> matchedNode = sut.lookup(Topic.asTopic("/temp"));
        Assert.assertTrue("Node on path /temp must be present", matchedNode.isPresent());
        final Set<Subscription> subscriptions = matchedNode.get().subscriptions;
        Assert.assertTrue(subscriptions.contains(newSubscription));
    }

    @Test
    public void testAddNewDeepNodes() {
        sut.addToTree(CTrieTest.clientSubOnTopic("TempSensorRM", "/italy/roma/temp"));
        sut.addToTree(CTrieTest.clientSubOnTopic("TempSensorFI", "/italy/firenze/temp"));
        sut.addToTree(CTrieTest.clientSubOnTopic("HumSensorFI", "/italy/roma/humidity"));
        final Subscription happinessSensor = CTrieTest.clientSubOnTopic("HappinessSensor", "/italy/happiness");
        sut.addToTree(happinessSensor);
        // Verify
        final Optional<CNode> matchedNode = sut.lookup(Topic.asTopic("/italy/happiness"));
        Assert.assertTrue("Node on path /italy/happiness must be present", matchedNode.isPresent());
        final Set<Subscription> subscriptions = matchedNode.get().subscriptions;
        Assert.assertTrue(subscriptions.contains(happinessSensor));
    }

    @Test
    public void givenTreeWithSomeNodeWhenRemoveContainedSubscriptionThenNodeIsUpdated() {
        sut.addToTree(CTrieTest.clientSubOnTopic("TempSensor1", "/temp"));
        // Exercise
        sut.removeFromTree(Topic.asTopic("/temp"), "TempSensor1");
        // Verify
        final Optional<CNode> matchedNode = sut.lookup(Topic.asTopic("/temp"));
        Assert.assertFalse("Node on path /temp can't be present", matchedNode.isPresent());
    }

    @Test
    public void givenTreeWithSomeNodeUnsubscribeAndResubscribeCleanTomb() {
        sut.addToTree(CTrieTest.clientSubOnTopic("TempSensor1", "test"));
        sut.removeFromTree(Topic.asTopic("test"), "TempSensor1");
        sut.addToTree(CTrieTest.clientSubOnTopic("TempSensor1", "test"));
        Assert.assertTrue(((sut.root.mainNode().allChildren().size()) == 1));// looking to see if TNode is cleaned up

    }

    @Test
    public void givenTreeWithSomeNodeWhenRemoveMultipleTimes() {
        sut.addToTree(CTrieTest.clientSubOnTopic("TempSensor1", "test"));
        // make sure no TNode exceptions
        sut.removeFromTree(Topic.asTopic("test"), "TempSensor1");
        sut.removeFromTree(Topic.asTopic("test"), "TempSensor1");
        sut.removeFromTree(Topic.asTopic("test"), "TempSensor1");
        sut.removeFromTree(Topic.asTopic("test"), "TempSensor1");
        // Verify
        final Optional<CNode> matchedNode = sut.lookup(Topic.asTopic("/temp"));
        Assert.assertFalse("Node on path /temp can't be present", matchedNode.isPresent());
    }

    @Test
    public void givenTreeWithSomeDeepNodeWhenRemoveMultipleTimes() {
        sut.addToTree(CTrieTest.clientSubOnTopic("TempSensor1", "/test/me/1/2/3"));
        // make sure no TNode exceptions
        sut.removeFromTree(Topic.asTopic("/test/me/1/2/3"), "TempSensor1");
        sut.removeFromTree(Topic.asTopic("/test/me/1/2/3"), "TempSensor1");
        sut.removeFromTree(Topic.asTopic("/test/me/1/2/3"), "TempSensor1");
        // Verify
        final Optional<CNode> matchedNode = sut.lookup(Topic.asTopic("/temp"));
        Assert.assertFalse("Node on path /temp can't be present", matchedNode.isPresent());
    }

    @Test
    public void givenTreeWithSomeNodeHierarchWhenRemoveContainedSubscriptionThenNodeIsUpdated() {
        sut.addToTree(CTrieTest.clientSubOnTopic("TempSensor1", "/temp/1"));
        sut.addToTree(CTrieTest.clientSubOnTopic("TempSensor1", "/temp/2"));
        // Exercise
        sut.removeFromTree(Topic.asTopic("/temp/1"), "TempSensor1");
        sut.removeFromTree(Topic.asTopic("/temp/1"), "TempSensor1");
        final Set<Subscription> matchingSubs = sut.recursiveMatch(Topic.asTopic("/temp/2"));
        // Verify
        final Subscription expectedMatchingsub = new Subscription("TempSensor1", Topic.asTopic("/temp/2"), MqttQoS.AT_MOST_ONCE);
        assertThat(matchingSubs).contains(expectedMatchingsub);
    }

    @Test
    public void givenTreeWithSomeNodeHierarchWhenRemoveContainedSubscriptionSmallerThenNodeIsNotUpdated() {
        sut.addToTree(CTrieTest.clientSubOnTopic("TempSensor1", "/temp/1"));
        sut.addToTree(CTrieTest.clientSubOnTopic("TempSensor1", "/temp/2"));
        // Exercise
        sut.removeFromTree(Topic.asTopic("/temp"), "TempSensor1");
        final Set<Subscription> matchingSubs1 = sut.recursiveMatch(Topic.asTopic("/temp/1"));
        final Set<Subscription> matchingSubs2 = sut.recursiveMatch(Topic.asTopic("/temp/2"));
        // Verify
        // not clear to me, but I believe /temp unsubscribe should not unsub you from downstream /temp/1 or /temp/2
        final Subscription expectedMatchingsub1 = new Subscription("TempSensor1", Topic.asTopic("/temp/1"), MqttQoS.AT_MOST_ONCE);
        assertThat(matchingSubs1).contains(expectedMatchingsub1);
        final Subscription expectedMatchingsub2 = new Subscription("TempSensor1", Topic.asTopic("/temp/2"), MqttQoS.AT_MOST_ONCE);
        assertThat(matchingSubs2).contains(expectedMatchingsub2);
    }

    @Test
    public void givenTreeWithDeepNodeWhenRemoveContainedSubscriptionThenNodeIsUpdated() {
        sut.addToTree(CTrieTest.clientSubOnTopic("TempSensor1", "/bah/bin/bash"));
        sut.removeFromTree(Topic.asTopic("/bah/bin/bash"), "TempSensor1");
        // Verify
        final Optional<CNode> matchedNode = sut.lookup(Topic.asTopic("/bah/bin/bash"));
        Assert.assertFalse("Node on path /temp can't be present", matchedNode.isPresent());
    }

    @Test
    public void testMatchSubscriptionNoWildcards() {
        sut.addToTree(CTrieTest.clientSubOnTopic("TempSensor1", "/temp"));
        // Exercise
        final Set<Subscription> matchingSubs = sut.recursiveMatch(Topic.asTopic("/temp"));
        // Verify
        final Subscription expectedMatchingsub = new Subscription("TempSensor1", Topic.asTopic("/temp"), MqttQoS.AT_MOST_ONCE);
        assertThat(matchingSubs).contains(expectedMatchingsub);
    }

    @Test
    public void testRemovalInnerTopicOffRootSameClient() {
        sut.addToTree(CTrieTest.clientSubOnTopic("TempSensor1", "temp"));
        sut.addToTree(CTrieTest.clientSubOnTopic("TempSensor1", "temp/1"));
        // Exercise
        final Set<Subscription> matchingSubs1 = sut.recursiveMatch(Topic.asTopic("temp"));
        final Set<Subscription> matchingSubs2 = sut.recursiveMatch(Topic.asTopic("temp/1"));
        // Verify
        final Subscription expectedMatchingsub1 = new Subscription("TempSensor1", Topic.asTopic("temp"), MqttQoS.AT_MOST_ONCE);
        final Subscription expectedMatchingsub2 = new Subscription("TempSensor1", Topic.asTopic("temp/1"), MqttQoS.AT_MOST_ONCE);
        assertThat(matchingSubs1).contains(expectedMatchingsub1);
        assertThat(matchingSubs2).contains(expectedMatchingsub2);
        sut.removeFromTree(Topic.asTopic("temp"), "TempSensor1");
        // Exercise
        final Set<Subscription> matchingSubs3 = sut.recursiveMatch(Topic.asTopic("temp"));
        final Set<Subscription> matchingSubs4 = sut.recursiveMatch(Topic.asTopic("temp/1"));
        assertThat(matchingSubs3).doesNotContain(expectedMatchingsub1);
        assertThat(matchingSubs4).contains(expectedMatchingsub2);
    }

    @Test
    public void testRemovalInnerTopicOffRootDiffClient() {
        sut.addToTree(CTrieTest.clientSubOnTopic("TempSensor1", "temp"));
        sut.addToTree(CTrieTest.clientSubOnTopic("TempSensor2", "temp/1"));
        // Exercise
        final Set<Subscription> matchingSubs1 = sut.recursiveMatch(Topic.asTopic("temp"));
        final Set<Subscription> matchingSubs2 = sut.recursiveMatch(Topic.asTopic("temp/1"));
        // Verify
        final Subscription expectedMatchingsub1 = new Subscription("TempSensor1", Topic.asTopic("temp"), MqttQoS.AT_MOST_ONCE);
        final Subscription expectedMatchingsub2 = new Subscription("TempSensor2", Topic.asTopic("temp/1"), MqttQoS.AT_MOST_ONCE);
        assertThat(matchingSubs1).contains(expectedMatchingsub1);
        assertThat(matchingSubs2).contains(expectedMatchingsub2);
        sut.removeFromTree(Topic.asTopic("temp"), "TempSensor1");
        // Exercise
        final Set<Subscription> matchingSubs3 = sut.recursiveMatch(Topic.asTopic("temp"));
        final Set<Subscription> matchingSubs4 = sut.recursiveMatch(Topic.asTopic("temp/1"));
        assertThat(matchingSubs3).doesNotContain(expectedMatchingsub1);
        assertThat(matchingSubs4).contains(expectedMatchingsub2);
    }

    @Test
    public void testRemovalOuterTopicOffRootDiffClient() {
        sut.addToTree(CTrieTest.clientSubOnTopic("TempSensor1", "temp"));
        sut.addToTree(CTrieTest.clientSubOnTopic("TempSensor2", "temp/1"));
        // Exercise
        final Set<Subscription> matchingSubs1 = sut.recursiveMatch(Topic.asTopic("temp"));
        final Set<Subscription> matchingSubs2 = sut.recursiveMatch(Topic.asTopic("temp/1"));
        // Verify
        final Subscription expectedMatchingsub1 = new Subscription("TempSensor1", Topic.asTopic("temp"), MqttQoS.AT_MOST_ONCE);
        final Subscription expectedMatchingsub2 = new Subscription("TempSensor2", Topic.asTopic("temp/1"), MqttQoS.AT_MOST_ONCE);
        assertThat(matchingSubs1).contains(expectedMatchingsub1);
        assertThat(matchingSubs2).contains(expectedMatchingsub2);
        sut.removeFromTree(Topic.asTopic("temp/1"), "TempSensor2");
        // Exercise
        final Set<Subscription> matchingSubs3 = sut.recursiveMatch(Topic.asTopic("temp"));
        final Set<Subscription> matchingSubs4 = sut.recursiveMatch(Topic.asTopic("temp/1"));
        assertThat(matchingSubs3).contains(expectedMatchingsub1);
        assertThat(matchingSubs4).doesNotContain(expectedMatchingsub2);
    }
}

