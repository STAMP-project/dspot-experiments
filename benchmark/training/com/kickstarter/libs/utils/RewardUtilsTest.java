package com.kickstarter.libs.utils;


import com.kickstarter.mock.factories.RewardFactory;
import com.kickstarter.models.Reward;
import junit.framework.TestCase;


public final class RewardUtilsTest extends TestCase {
    public void testHasBackers() {
        TestCase.assertTrue(RewardUtils.hasBackers(RewardFactory.backers()));
        TestCase.assertFalse(RewardUtils.hasBackers(RewardFactory.noBackers()));
    }

    public void testIsLimited() {
        final Reward rewardWithRemaining = RewardFactory.reward().toBuilder().remaining(5).limit(10).build();
        TestCase.assertTrue(RewardUtils.isLimited(rewardWithRemaining));
        final Reward rewardWithNoneRemaining = RewardFactory.reward().toBuilder().remaining(0).limit(10).build();
        TestCase.assertFalse(RewardUtils.isLimited(rewardWithNoneRemaining));
        final Reward rewardWithNoLimitAndRemainingSet = RewardFactory.reward().toBuilder().remaining(null).limit(null).build();
        TestCase.assertFalse(RewardUtils.isLimited(rewardWithNoLimitAndRemainingSet));
    }

    public void testIsItemized() {
        TestCase.assertFalse(RewardUtils.isItemized(RewardFactory.reward()));
        TestCase.assertTrue(RewardUtils.isItemized(RewardFactory.itemized()));
    }

    public void testIsLimitReachedWhenLimitSetAndRemainingIsZero() {
        final Reward reward = RewardFactory.reward().toBuilder().limit(100).remaining(0).build();
        TestCase.assertTrue(RewardUtils.isLimitReached(reward));
    }

    public void testIsLimitNotReachedWhenLimitSetButRemainingIsNull() {
        final Reward reward = RewardFactory.reward().toBuilder().limit(100).build();
        TestCase.assertFalse(RewardUtils.isLimitReached(reward));
    }

    public void testIsLimitReachedWhenRemainingIsGreaterThanZero() {
        final Reward reward = RewardFactory.reward().toBuilder().limit(100).remaining(50).build();
        TestCase.assertFalse(RewardUtils.isLimitReached(reward));
    }

    public void testIsReward() {
        TestCase.assertTrue(RewardUtils.isReward(RewardFactory.reward()));
        TestCase.assertFalse(RewardUtils.isReward(RewardFactory.noReward()));
    }

    public void testIsNoReward() {
        TestCase.assertTrue(RewardUtils.isNoReward(RewardFactory.noReward()));
        TestCase.assertFalse(RewardUtils.isNoReward(RewardFactory.reward()));
    }

    public void testIsShippable() {
        final Reward rewardWithNullShippingEnabled = RewardFactory.reward().toBuilder().shippingEnabled(null).build();
        TestCase.assertFalse(RewardUtils.isShippable(rewardWithNullShippingEnabled));
        final Reward rewardWithFalseShippingEnabled = RewardFactory.reward().toBuilder().shippingEnabled(false).build();
        TestCase.assertFalse(RewardUtils.isShippable(rewardWithFalseShippingEnabled));
        final Reward rewardWithShippingEnabled = RewardFactory.reward().toBuilder().shippingEnabled(true).build();
        TestCase.assertTrue(RewardUtils.isShippable(rewardWithShippingEnabled));
    }
}

