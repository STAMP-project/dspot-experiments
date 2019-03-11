package com.kickstarter.viewmodels;


import Backing.STATUS_CANCELED;
import Backing.STATUS_COLLECTED;
import Backing.STATUS_DROPPED;
import Backing.STATUS_ERRORED;
import Backing.STATUS_PLEDGED;
import Backing.STATUS_PREAUTH;
import BackingViewModel.ViewModel;
import IntentKey.BACKER;
import IntentKey.IS_FROM_MESSAGES_ACTIVITY;
import IntentKey.PROJECT;
import KoalaEvent.VIEWED_PLEDGE_INFO;
import android.content.Intent;
import android.util.Pair;
import com.kickstarter.KSRobolectricTestCase;
import com.kickstarter.libs.RefTag;
import com.kickstarter.libs.utils.DateTimeUtils;
import com.kickstarter.libs.utils.NumberUtils;
import com.kickstarter.mock.factories.BackingFactory;
import com.kickstarter.mock.factories.LocationFactory;
import com.kickstarter.mock.factories.RewardFactory;
import com.kickstarter.models.Backing;
import com.kickstarter.models.Location;
import com.kickstarter.models.Project;
import com.kickstarter.models.Reward;
import com.kickstarter.models.RewardsItem;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.Test;
import rx.observers.TestSubscriber;


public final class BackingViewModelTest extends KSRobolectricTestCase {
    private ViewModel vm;

    private final TestSubscriber<String> backerNameTextViewText = new TestSubscriber();

    private final TestSubscriber<String> backerNumberTextViewText = new TestSubscriber();

    private final TestSubscriber<String> backingStatusTextViewText = new TestSubscriber();

    private final TestSubscriber<Pair<String, String>> backingAmountAndDateTextViewText = new TestSubscriber();

    private final TestSubscriber<String> creatorNameTextViewText = new TestSubscriber();

    private final TestSubscriber<Boolean> estimatedDeliverySectionIsGone = new TestSubscriber();

    private final TestSubscriber<String> estimatedDeliverySectionTextViewText = new TestSubscriber();

    private final TestSubscriber<Void> goBack = new TestSubscriber();

    private final TestSubscriber<String> loadBackerAvatar = new TestSubscriber();

    private final TestSubscriber<String> loadProjectPhoto = new TestSubscriber();

    private final TestSubscriber<Boolean> markAsReceivedIsChecked = new TestSubscriber();

    private final TestSubscriber<String> projectNameTextViewText = new TestSubscriber();

    private final TestSubscriber<Boolean> receivedSectionIsGone = new TestSubscriber();

    private final TestSubscriber<Pair<String, String>> rewardMinimumAndDescriptionTextViewText = new TestSubscriber();

    private final TestSubscriber<List<RewardsItem>> rewardsItemList = new TestSubscriber();

    private final TestSubscriber<Boolean> rewardsItemsAreGone = new TestSubscriber();

    private final TestSubscriber<String> shippingAmountTextViewText = new TestSubscriber();

    private final TestSubscriber<String> shippingLocationTextViewText = new TestSubscriber();

    private final TestSubscriber<Boolean> shippingSectionIsGone = new TestSubscriber();

    private final TestSubscriber<Pair<Project, Backing>> startMessagesActivity = new TestSubscriber();

    private final TestSubscriber<Pair<Project, RefTag>> startProjectActivity = new TestSubscriber();

    private final TestSubscriber<Boolean> viewMessagesButtonIsGone = new TestSubscriber();

    @Test
    public void testBackerNameTextViewText() {
        final Backing backing = BackingFactory.backing();
        setUpEnvironmentAndIntentWithBacking(backing);
        this.backerNameTextViewText.assertValues(backing.backer().name());
        this.koalaTest.assertValues(VIEWED_PLEDGE_INFO);
    }

    @Test
    public void testBackerNumberTextViewText() {
        final Backing backing = BackingFactory.backing();
        setUpEnvironmentAndIntentWithBacking(backing);
        this.backerNumberTextViewText.assertValues(NumberUtils.format(backing.sequence()));
    }

    @Test
    public void testBackingAmountAndDateTextViewText() {
        final Backing backing = BackingFactory.backing().toBuilder().amount(50.0F).build();
        setUpEnvironmentAndIntentWithBacking(backing);
        this.backingAmountAndDateTextViewText.assertValue(Pair.create("$50", DateTimeUtils.fullDate(backing.pledgedAt())));
    }

    @Test
    public void testBackingStatus() {
        final Backing backing = BackingFactory.backing();
        setUpEnvironmentAndIntentWithBacking(backing);
        this.backingStatusTextViewText.assertValue(backing.status());
    }

    @Test
    public void testCreatorNameTextViewText() {
        final Backing backing = BackingFactory.backing();
        setUpEnvironmentAndIntentWithBacking(backing);
        this.creatorNameTextViewText.assertValues(backing.project().creator().name());
    }

    @Test
    public void testEstimatedDeliverySectionIsGone_deliveryNull() {
        final Reward reward = RewardFactory.reward().toBuilder().estimatedDeliveryOn(null).build();
        final Backing backing = BackingFactory.backing().toBuilder().reward(reward).build();
        setUpEnvironmentAndIntentWithBacking(backing);
        this.estimatedDeliverySectionIsGone.assertValues(true);
    }

    @Test
    public void getTestEstimatedDeliverySectionIsGone_deliveryNotNull() {
        final Reward reward = RewardFactory.reward().toBuilder().estimatedDeliveryOn(DateTime.now()).build();
        final Backing backing = BackingFactory.backing().toBuilder().reward(reward).build();
        setUpEnvironmentAndIntentWithBacking(backing);
        this.estimatedDeliverySectionIsGone.assertValues(false);
    }

    @Test
    public void estimatedDeliverySectionTextViewText() {
        final DateTime testDateTime = DateTime.now();
        final Reward reward = RewardFactory.reward().toBuilder().estimatedDeliveryOn(testDateTime).build();
        final Backing backing = BackingFactory.backing().toBuilder().reward(reward).build();
        setUpEnvironmentAndIntentWithBacking(backing);
        this.estimatedDeliverySectionTextViewText.assertValues(DateTimeUtils.estimatedDeliveryOn(testDateTime));
    }

    @Test
    public void testGoBackOnProjectClick() {
        final Backing backing = BackingFactory.backing();
        setUpEnvironmentAndIntentWithBacking(backing);
        this.goBack.assertNoValues();
        this.vm.inputs.projectClicked();
        this.goBack.assertValueCount(1);
        // Project context click doesn't start project activity if not from Messages.
        this.startProjectActivity.assertNoValues();
    }

    @Test
    public void testLoadBackerAvatar() {
        final Backing backing = BackingFactory.backing();
        setUpEnvironmentAndIntentWithBacking(backing);
        this.loadBackerAvatar.assertValues(backing.backer().avatar().medium());
    }

    @Test
    public void testLoadProjectPhoto() {
        final Backing backing = BackingFactory.backing();
        setUpEnvironmentAndIntentWithBacking(backing);
        this.loadProjectPhoto.assertValues(backing.project().photo().full());
    }

    @Test
    public void testMarkAsReceivedIsChecked_isFalse_whenBackingIsNotBackerCompleted() {
        final Backing initialBacking = BackingFactory.backing();
        final Backing updatedBacking = initialBacking.toBuilder().backerCompletedAt(DateTime.now()).build();
        setUpEnvironmentAndIntentWithBacking(initialBacking);
        this.markAsReceivedIsChecked.assertValue(false);
        setUpEnvironmentAndIntentWithBacking(updatedBacking);
        this.vm.inputs.markAsReceivedSwitchChecked(true);
        this.markAsReceivedIsChecked.assertValues(false, true);
    }

    @Test
    public void testMarkAsReceivedIsChecked_isTrue_whenBackingIsBackerCompleted() {
        final Backing initialBacking = BackingFactory.backing().toBuilder().backerCompletedAt(DateTime.now()).build();
        final Backing updatedBacking = initialBacking.toBuilder().backerCompletedAt(null).build();
        setUpEnvironmentAndIntentWithBacking(initialBacking);
        this.markAsReceivedIsChecked.assertValues(true);
        setUpEnvironmentAndIntentWithBacking(updatedBacking);
        this.vm.inputs.markAsReceivedSwitchChecked(true);
        this.markAsReceivedIsChecked.assertValues(true, false);
    }

    @Test
    public void testProjectNameTextViewText() {
        final Backing backing = BackingFactory.backing();
        setUpEnvironmentAndIntentWithBacking(backing);
        this.projectNameTextViewText.assertValues(backing.project().name());
    }

    @Test
    public void testReceivedSectionIsGone_isTrue_whenBackingStatusCanceled() {
        final Backing backing = BackingFactory.backing(STATUS_CANCELED);
        setUpEnvironmentAndIntentWithBacking(backing);
        this.receivedSectionIsGone.assertValue(true);
    }

    @Test
    public void testReceivedSectionIsGone_isTrue_whenBackingStatusDropped() {
        final Backing backing = BackingFactory.backing(STATUS_DROPPED);
        setUpEnvironmentAndIntentWithBacking(backing);
        this.receivedSectionIsGone.assertValue(true);
    }

    @Test
    public void testReceivedSectionIsGone_isTrue_whenBackingStatusErrored() {
        final Backing backing = BackingFactory.backing(STATUS_ERRORED);
        setUpEnvironmentAndIntentWithBacking(backing);
        this.receivedSectionIsGone.assertValue(true);
    }

    @Test
    public void testReceivedSectionIsGone_isTrue_whenBackingStatusPledged() {
        final Backing backing = BackingFactory.backing(STATUS_PLEDGED);
        setUpEnvironmentAndIntentWithBacking(backing);
        this.receivedSectionIsGone.assertValue(true);
    }

    @Test
    public void testReceivedSectionIsGone_isTrue_whenBackingStatusPreAuth() {
        final Backing backing = BackingFactory.backing(STATUS_PREAUTH);
        setUpEnvironmentAndIntentWithBacking(backing);
        this.receivedSectionIsGone.assertValue(true);
    }

    @Test
    public void testReceivedSectionIsGone_isTrue_whenRewardIsNull() {
        final Backing backingWithNullReward = BackingFactory.backing(STATUS_COLLECTED).toBuilder().reward(null).build();
        setUpEnvironmentAndIntentWithBacking(backingWithNullReward);
        this.receivedSectionIsGone.assertValue(true);
    }

    @Test
    public void testReceivedSectionIsGone_isTrue_whenRewardIsNoReward() {
        final Backing backingWithNoReward = BackingFactory.backing(STATUS_COLLECTED).toBuilder().reward(RewardFactory.noReward()).build();
        setUpEnvironmentAndIntentWithBacking(backingWithNoReward);
        this.receivedSectionIsGone.assertValue(true);
    }

    @Test
    public void testReceivedSectionIsGone_isFalse_whenRewardIsReceivableAndBackingIsCollected() {
        final Backing backing = BackingFactory.backing(STATUS_COLLECTED);
        setUpEnvironmentAndIntentWithBacking(backing);
        this.receivedSectionIsGone.assertValue(false);
    }

    @Test
    public void testRewardMinimumAndDescriptionTextViewText() {
        final Reward reward = RewardFactory.reward().toBuilder().minimum(100.0F).build();
        final Backing backing = BackingFactory.backing().toBuilder().reward(reward).build();
        setUpEnvironmentAndIntentWithBacking(backing);
        this.rewardMinimumAndDescriptionTextViewText.assertValue(Pair.create("$100", backing.reward().description()));
    }

    @Test
    public void testRewardsItemAreHidden() {
        final Reward reward = RewardFactory.reward().toBuilder().rewardsItems(null).build();
        final Backing backing = BackingFactory.backing().toBuilder().reward(reward).build();
        setUpEnvironmentAndIntentWithBacking(backing);
        this.rewardsItemList.assertValue(Collections.emptyList());
        this.rewardsItemsAreGone.assertValues(true);
    }

    @Test
    public void testRewardsItemAreEmitted() {
        final Reward reward = RewardFactory.itemized();
        final Backing backing = BackingFactory.backing().toBuilder().reward(reward).build();
        setUpEnvironmentAndIntentWithBacking(backing);
        this.rewardsItemList.assertValue(reward.rewardsItems());
        this.rewardsItemsAreGone.assertValues(false);
    }

    @Test
    public void testShipping_withoutShippingLocation() {
        final Backing backing = BackingFactory.backing();
        setUpEnvironmentAndIntentWithBacking(backing);
        this.shippingLocationTextViewText.assertNoValues();
        this.shippingAmountTextViewText.assertNoValues();
        this.shippingSectionIsGone.assertValues(true);
    }

    @Test
    public void testShipping_withShippingLocation() {
        final Location location = LocationFactory.sydney();
        final Reward reward = RewardFactory.rewardWithShipping();
        final Backing backing = BackingFactory.backing().toBuilder().location(location).reward(reward).rewardId(reward.id()).shippingAmount(5.0F).build();
        setUpEnvironmentAndIntentWithBacking(backing);
        this.shippingLocationTextViewText.assertValues("Sydney, AU");
        this.shippingAmountTextViewText.assertValues("$5");
        this.shippingSectionIsGone.assertValues(false);
    }

    @Test
    public void testStartMessagesActivity() {
        final Backing backing = BackingFactory.backing();
        setUpEnvironmentAndIntentWithBacking(backing);
        this.vm.inputs.viewMessagesButtonClicked();
        this.startMessagesActivity.assertValue(Pair.create(backing.project(), backing));
    }

    @Test
    public void testStartProjectActivity() {
        final Backing backing = BackingFactory.backing();
        setUpEnvironment(envWithBacking(BackingFactory.backing()));
        this.vm.intent(new Intent().putExtra(BACKER, backing.backer()).putExtra(PROJECT, backing.project()).putExtra(IS_FROM_MESSAGES_ACTIVITY, true));
        this.vm.inputs.projectClicked();
        this.startProjectActivity.assertValue(Pair.create(backing.project(), RefTag.pledgeInfo()));
        this.goBack.assertNoValues();
    }

    @Test
    public void testViewMessagesButtonIsGone_FromMessages() {
        final Backing backing = BackingFactory.backing();
        setUpEnvironment(envWithBacking(backing));
        this.vm.intent(new Intent().putExtra(BACKER, backing.backer()).putExtra(PROJECT, backing.project()).putExtra(IS_FROM_MESSAGES_ACTIVITY, true));
        this.viewMessagesButtonIsGone.assertValues(true);
    }

    @Test
    public void testViewMessagesButtonIsVisible() {
        final Backing backing = BackingFactory.backing();
        setUpEnvironmentAndIntentWithBacking(backing);
        this.viewMessagesButtonIsGone.assertValues(false);
    }
}

