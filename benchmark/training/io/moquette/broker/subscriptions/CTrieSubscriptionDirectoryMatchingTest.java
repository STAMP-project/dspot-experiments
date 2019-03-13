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


import io.moquette.broker.ISubscriptionsRepository;
import io.netty.handler.codec.mqtt.MqttQoS;
import java.util.Optional;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class CTrieSubscriptionDirectoryMatchingTest {
    private CTrieSubscriptionDirectory sut;

    private ISubscriptionsRepository sessionsRepository;

    @Test
    public void testMatchSimple() {
        Subscription slashSub = CTrieTest.clientSubOnTopic("TempSensor1", "/");
        sut.add(slashSub);
        assertThat(sut.matchWithoutQosSharpening(Topic.asTopic("finance"))).isEmpty();
        Subscription slashFinanceSub = CTrieTest.clientSubOnTopic("TempSensor1", "/finance");
        sut.add(slashFinanceSub);
        assertThat(sut.matchWithoutQosSharpening(Topic.asTopic("finance"))).isEmpty();
        assertThat(sut.matchWithoutQosSharpening(Topic.asTopic("/finance"))).contains(slashFinanceSub);
        assertThat(sut.matchWithoutQosSharpening(Topic.asTopic("/"))).contains(slashSub);
    }

    @Test
    public void testMatchSimpleMulti() {
        Subscription anySub = CTrieTest.clientSubOnTopic("TempSensor1", "#");
        sut.add(anySub);
        assertThat(sut.matchWithoutQosSharpening(Topic.asTopic("finance"))).contains(anySub);
        Subscription financeAnySub = CTrieTest.clientSubOnTopic("TempSensor1", "finance/#");
        sut.add(financeAnySub);
        assertThat(sut.matchWithoutQosSharpening(Topic.asTopic("finance"))).containsExactlyInAnyOrder(financeAnySub, anySub);
    }

    @Test
    public void testMatchingDeepMulti_one_layer() {
        Subscription anySub = CTrieTest.clientSubOnTopic("AllSensor1", "#");
        Subscription financeAnySub = CTrieTest.clientSubOnTopic("FinanceSensor", "finance/#");
        sut.add(anySub);
        sut.add(financeAnySub);
        // Verify
        assertThat(sut.matchWithoutQosSharpening(Topic.asTopic("finance/stock"))).containsExactlyInAnyOrder(financeAnySub, anySub);
        assertThat(sut.matchWithoutQosSharpening(Topic.asTopic("finance/stock/ibm"))).containsExactlyInAnyOrder(financeAnySub, anySub);
        // System.out.println(sut.dumpTree());
    }

    @Test
    public void testMatchingDeepMulti_two_layer() {
        Subscription financeAnySub = CTrieTest.clientSubOnTopic("FinanceSensor", "finance/stock/#");
        sut.add(financeAnySub);
        // Verify
        assertThat(sut.matchWithoutQosSharpening(Topic.asTopic("finance/stock/ibm"))).containsExactly(financeAnySub);
    }

    @Test
    public void testMatchSimpleSingle() {
        Subscription anySub = CTrieTest.clientSubOnTopic("AnySensor", "+");
        sut.add(anySub);
        assertThat(sut.matchWithoutQosSharpening(Topic.asTopic("finance"))).containsExactly(anySub);
        Subscription financeOne = CTrieTest.clientSubOnTopic("AnySensor", "finance/+");
        sut.add(financeOne);
        assertThat(sut.matchWithoutQosSharpening(Topic.asTopic("finance/stock"))).containsExactly(financeOne);
    }

    @Test
    public void testMatchManySingle() {
        Subscription manySub = CTrieTest.clientSubOnTopic("AnySensor", "+/+");
        sut.add(manySub);
        // verify
        assertThat(sut.matchWithoutQosSharpening(Topic.asTopic("/finance"))).contains(manySub);
    }

    @Test
    public void testMatchSlashSingle() {
        Subscription slashPlusSub = CTrieTest.clientSubOnTopic("AnySensor", "/+");
        sut.add(slashPlusSub);
        Subscription anySub = CTrieTest.clientSubOnTopic("AnySensor", "+");
        sut.add(anySub);
        // Verify
        assertThat(sut.matchWithoutQosSharpening(Topic.asTopic("/finance"))).containsOnly(slashPlusSub);
        assertThat(sut.matchWithoutQosSharpening(Topic.asTopic("/finance"))).doesNotContain(anySub);
    }

    @Test
    public void testMatchManyDeepSingle() {
        Subscription slashPlusSub = CTrieTest.clientSubOnTopic("FinanceSensor1", "/finance/+/ibm");
        sut.add(slashPlusSub);
        Subscription slashPlusDeepSub = CTrieTest.clientSubOnTopic("FinanceSensor2", "/+/stock/+");
        sut.add(slashPlusDeepSub);
        // Verify
        assertThat(sut.matchWithoutQosSharpening(Topic.asTopic("/finance/stock/ibm"))).containsExactlyInAnyOrder(slashPlusSub, slashPlusDeepSub);
    }

    @Test
    public void testMatchSimpleMulti_allTheTree() {
        Subscription sub = CTrieTest.clientSubOnTopic("AnySensor1", "#");
        sut.add(sub);
        assertThat(sut.matchWithoutQosSharpening(Topic.asTopic("finance"))).isNotEmpty();
        assertThat(sut.matchWithoutQosSharpening(Topic.asTopic("finance/ibm"))).isNotEmpty();
    }

    @Test
    public void rogerLightTopicMatches() {
        assertMatch("foo/bar", "foo/bar");
        assertMatch("foo/bar", "foo/bar");
        assertMatch("foo/+", "foo/bar");
        assertMatch("foo/+/baz", "foo/bar/baz");
        assertMatch("foo/+/#", "foo/bar/baz");
        assertMatch("#", "foo/bar/baz");
        assertNotMatch("foo/bar", "foo");
        assertNotMatch("foo/+", "foo/bar/baz");
        assertNotMatch("foo/+/baz", "foo/bar/bar");
        assertNotMatch("foo/+/#", "fo2/bar/baz");
        assertMatch("#", "/foo/bar");
        assertMatch("/#", "/foo/bar");
        assertNotMatch("/#", "foo/bar");
        assertMatch("foo//bar", "foo//bar");
        assertMatch("foo//+", "foo//bar");
        assertMatch("foo/+/+/baz", "foo///baz");
        assertMatch("foo/bar/+", "foo/bar/");
    }

    @Test
    public void testOverlappingSubscriptions() {
        Subscription genericSub = new Subscription("Sensor1", Topic.asTopic("a/+"), MqttQoS.AT_MOST_ONCE);
        this.sessionsRepository.addNewSubscription(genericSub);
        sut.add(genericSub);
        Subscription specificSub = new Subscription("Sensor1", Topic.asTopic("a/b"), MqttQoS.AT_MOST_ONCE);
        this.sessionsRepository.addNewSubscription(specificSub);
        sut.add(specificSub);
        // Exercise
        final Set<Subscription> matchingForSpecific = sut.matchQosSharpening(Topic.asTopic("a/b"));
        // Verify
        assertThat(matchingForSpecific.size()).isEqualTo(1);
    }

    @Test
    public void removeSubscription_withDifferentClients_subscribedSameTopic() {
        Subscription slashSub = CTrieTest.clientSubOnTopic("Sensor1", "/topic");
        sut.add(slashSub);
        Subscription slashSub2 = CTrieTest.clientSubOnTopic("Sensor2", "/topic");
        sut.add(slashSub2);
        // Exercise
        sut.removeSubscription(Topic.asTopic("/topic"), slashSub2.clientId);
        // Verify
        Subscription remainedSubscription = sut.matchWithoutQosSharpening(Topic.asTopic("/topic")).iterator().next();
        assertThat(remainedSubscription.clientId).isEqualTo(slashSub.clientId);
        Assert.assertEquals(slashSub.clientId, remainedSubscription.clientId);
    }

    @Test
    public void removeSubscription_sameClients_subscribedSameTopic() {
        Subscription slashSub = CTrieTest.clientSubOnTopic("Sensor1", "/topic");
        sut.add(slashSub);
        // Exercise
        sut.removeSubscription(Topic.asTopic("/topic"), slashSub.clientId);
        // Verify
        final Set<Subscription> matchingSubscriptions = sut.matchWithoutQosSharpening(Topic.asTopic("/topic"));
        assertThat(matchingSubscriptions).isEmpty();
    }

    /* Test for Issue #49 */
    @Test
    public void duplicatedSubscriptionsWithDifferentQos() {
        Subscription client2Sub = new Subscription("client2", Topic.asTopic("client/test/b"), MqttQoS.AT_MOST_ONCE);
        this.sut.add(client2Sub);
        Subscription client1SubQoS0 = new Subscription("client1", Topic.asTopic("client/test/b"), MqttQoS.AT_MOST_ONCE);
        this.sut.add(client1SubQoS0);
        Subscription client1SubQoS2 = new Subscription("client1", Topic.asTopic("client/test/b"), MqttQoS.EXACTLY_ONCE);
        this.sut.add(client1SubQoS2);
        // Verify
        Set<Subscription> subscriptions = this.sut.matchQosSharpening(Topic.asTopic("client/test/b"));
        assertThat(subscriptions).contains(client1SubQoS2);
        assertThat(subscriptions).contains(client2Sub);
        final Optional<Subscription> matchingClient1Sub = subscriptions.stream().filter(( s) -> s.equals(client1SubQoS0)).findFirst();
        Assert.assertTrue(matchingClient1Sub.isPresent());
        Subscription client1Sub = matchingClient1Sub.get();
        assertThat(client1SubQoS0.getRequestedQos()).isNotEqualTo(client1Sub.getRequestedQos());
        // client1SubQoS2 should override client1SubQoS0
        assertThat(client1Sub.getRequestedQos()).isEqualTo(client1SubQoS2.getRequestedQos());
    }
}

