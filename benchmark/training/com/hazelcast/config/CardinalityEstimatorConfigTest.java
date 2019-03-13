/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.config;


import CardinalityEstimatorConfig.DEFAULT_MERGE_POLICY_CONFIG;
import Warning.NONFINAL_FIELDS;
import Warning.NULL_FIELDS;
import com.hazelcast.config.CardinalityEstimatorConfig.CardinalityEstimatorConfigReadOnly;
import com.hazelcast.spi.merge.DiscardMergePolicy;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CardinalityEstimatorConfigTest extends HazelcastTestSupport {
    private CardinalityEstimatorConfig config = new CardinalityEstimatorConfig();

    @Test
    public void testConstructor_withName() {
        config = new CardinalityEstimatorConfig("myEstimator");
        Assert.assertEquals("myEstimator", config.getName());
    }

    @Test
    public void testConstructor_withNameAndBackupCounts() {
        config = new CardinalityEstimatorConfig("myEstimator", 2, 3);
        Assert.assertEquals("myEstimator", config.getName());
        Assert.assertEquals(2, config.getBackupCount());
        Assert.assertEquals(3, config.getAsyncBackupCount());
        Assert.assertEquals(5, config.getTotalBackupCount());
        Assert.assertEquals(DEFAULT_MERGE_POLICY_CONFIG, config.getMergePolicyConfig());
    }

    @Test
    public void testConstructor_withNameAndBackupCounts_withMergePolicy() {
        MergePolicyConfig mergePolicyConfig = new MergePolicyConfig("DiscardMergePolicy", 1000);
        config = new CardinalityEstimatorConfig("myEstimator", 2, 3, mergePolicyConfig);
        Assert.assertEquals("myEstimator", config.getName());
        Assert.assertEquals(2, config.getBackupCount());
        Assert.assertEquals(3, config.getAsyncBackupCount());
        Assert.assertEquals(5, config.getTotalBackupCount());
        Assert.assertEquals(mergePolicyConfig, config.getMergePolicyConfig());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_withNameAndBackupCounts_withNegativeBackupCount() {
        new CardinalityEstimatorConfig("myEstimator", (-1), 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_withNameAndBackupCounts_withNegativeAsyncBackupCount() {
        new CardinalityEstimatorConfig("myEstimator", 2, (-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_withNameAndBackupCounts_withNegativeBackupSum1() {
        new CardinalityEstimatorConfig("myEstimator", 6, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_withNameAndBackupCounts_withNegativeBackupSum2() {
        new CardinalityEstimatorConfig("myEstimator", 1, 6);
    }

    @Test
    public void testSetName() {
        config.setName("myCardinalityEstimator");
        Assert.assertEquals("myCardinalityEstimator", config.getName());
    }

    @Test
    public void testBackupCount() {
        config.setBackupCount(2);
        config.setAsyncBackupCount(3);
        Assert.assertEquals(2, config.getBackupCount());
        Assert.assertEquals(3, config.getAsyncBackupCount());
        Assert.assertEquals(5, config.getTotalBackupCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetBackupCount_withNegativeValue() {
        config.setBackupCount((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetAsyncBackupCount_withNegativeValue() {
        config.setAsyncBackupCount((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetBackupCount_withInvalidValue() {
        config.setBackupCount(7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetAsyncBackupCount_withInvalidValue() {
        config.setAsyncBackupCount(7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetBackupCount_withInvalidBackupSum() {
        config.setAsyncBackupCount(1);
        config.setBackupCount(6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetAsyncBackupCount_withInvalidBackupSum() {
        config.setBackupCount(1);
        config.setAsyncBackupCount(6);
    }

    @Test
    public void testToString() {
        HazelcastTestSupport.assertContains(config.toString(), "CardinalityEstimatorConfig");
    }

    @Test
    public void testEqualsAndHashCode() {
        HazelcastTestSupport.assumeDifferentHashCodes();
        EqualsVerifier.forClass(CardinalityEstimatorConfig.class).allFieldsShouldBeUsedExcept("readOnly").suppress(NULL_FIELDS, NONFINAL_FIELDS).withPrefabValues(CardinalityEstimatorConfigReadOnly.class, new CardinalityEstimatorConfigReadOnly(new CardinalityEstimatorConfig("red")), new CardinalityEstimatorConfigReadOnly(new CardinalityEstimatorConfig("black"))).withPrefabValues(MergePolicyConfig.class, new MergePolicyConfig(), new MergePolicyConfig(DiscardMergePolicy.class.getSimpleName(), 10)).verify();
    }
}

