package org.robolectric.shadows;


import SubscriptionManager.OnSubscriptionsChangedListener;
import android.os.Build.VERSION_CODES;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowSubscriptionManager.SubscriptionInfoBuilder;


/**
 * Test for {@link ShadowSubscriptionManager}.
 */
@RunWith(AndroidJUnit4.class)
@Config(minSdk = VERSION_CODES.N)
public class ShadowSubscriptionManagerTest {
    private SubscriptionManager subscriptionManager;

    private ShadowSubscriptionManager shadowSubscriptionManager;

    @Test
    public void shouldGiveDefaultSubscriptionId() {
        int testId = 42;
        ShadowSubscriptionManager.setDefaultSubscriptionId(testId);
        assertThat(subscriptionManager.getDefaultSubscriptionId()).isEqualTo(testId);
    }

    @Test
    public void shouldGiveDefaultDataSubscriptionId() {
        int testId = 42;
        shadowSubscriptionManager.setDefaultDataSubscriptionId(testId);
        assertThat(subscriptionManager.getDefaultDataSubscriptionId()).isEqualTo(testId);
    }

    @Test
    public void shouldGiveDefaultSmsSubscriptionId() {
        int testId = 42;
        shadowSubscriptionManager.setDefaultSmsSubscriptionId(testId);
        assertThat(subscriptionManager.getDefaultSmsSubscriptionId()).isEqualTo(testId);
    }

    @Test
    public void shouldGiveDefaultVoiceSubscriptionId() {
        int testId = 42;
        shadowSubscriptionManager.setDefaultVoiceSubscriptionId(testId);
        assertThat(subscriptionManager.getDefaultVoiceSubscriptionId()).isEqualTo(testId);
    }

    @Test
    public void addOnSubscriptionsChangedListener_shouldAddListener() {
        ShadowSubscriptionManagerTest.DummySubscriptionsChangedListener listener = new ShadowSubscriptionManagerTest.DummySubscriptionsChangedListener();
        shadowSubscriptionManager.addOnSubscriptionsChangedListener(listener);
        shadowSubscriptionManager.setActiveSubscriptionInfos(SubscriptionInfoBuilder.newBuilder().setId(123).buildSubscriptionInfo());
        assertThat(listener.subscriptionChanged).isTrue();
    }

    @Test
    public void removeOnSubscriptionsChangedListener_shouldRemoveListener() {
        ShadowSubscriptionManagerTest.DummySubscriptionsChangedListener listener = new ShadowSubscriptionManagerTest.DummySubscriptionsChangedListener();
        ShadowSubscriptionManagerTest.DummySubscriptionsChangedListener listener2 = new ShadowSubscriptionManagerTest.DummySubscriptionsChangedListener();
        shadowSubscriptionManager.addOnSubscriptionsChangedListener(listener);
        shadowSubscriptionManager.addOnSubscriptionsChangedListener(listener2);
        shadowSubscriptionManager.removeOnSubscriptionsChangedListener(listener);
        shadowSubscriptionManager.setActiveSubscriptionInfos(SubscriptionInfoBuilder.newBuilder().setId(123).buildSubscriptionInfo());
        assertThat(listener.subscriptionChanged).isFalse();
        assertThat(listener2.subscriptionChanged).isTrue();
    }

    @Test
    public void getActiveSubscriptionInfo_shouldReturnInfoWithSubId() {
        SubscriptionInfo expectedSubscriptionInfo = SubscriptionInfoBuilder.newBuilder().setId(123).buildSubscriptionInfo();
        shadowSubscriptionManager.setActiveSubscriptionInfos(expectedSubscriptionInfo);
        assertThat(shadowSubscriptionManager.getActiveSubscriptionInfo(123)).isSameAs(expectedSubscriptionInfo);
    }

    @Test
    public void getActiveSubscriptionInfoForSimSlotIndex_shouldReturnInfoWithSlotIndex() {
        SubscriptionInfo expectedSubscriptionInfo = SubscriptionInfoBuilder.newBuilder().setSimSlotIndex(123).buildSubscriptionInfo();
        shadowSubscriptionManager.setActiveSubscriptionInfos(expectedSubscriptionInfo);
        assertThat(shadowSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(123)).isSameAs(expectedSubscriptionInfo);
    }

    @Test
    public void getActiveSubscriptionInfo_shouldReturnNullForNullList() {
        shadowSubscriptionManager.setActiveSubscriptionInfoList(null);
        assertThat(shadowSubscriptionManager.getActiveSubscriptionInfo(123)).isNull();
    }

    @Test
    public void getActiveSubscriptionInfo_shouldReturnNullForNullVarargsList() {
        shadowSubscriptionManager.setActiveSubscriptionInfos(((SubscriptionInfo[]) (null)));
        assertThat(shadowSubscriptionManager.getActiveSubscriptionInfo(123)).isNull();
    }

    @Test
    public void getActiveSubscriptionInfo_shouldReturnNullForEmptyList() {
        shadowSubscriptionManager.setActiveSubscriptionInfos();
        assertThat(shadowSubscriptionManager.getActiveSubscriptionInfo(123)).isNull();
    }

    @Test
    public void isNetworkRoaming_shouldReturnTrueIfSet() {
        /* isNetworkRoaming= */
        shadowSubscriptionManager.setNetworkRoamingStatus(123, true);
        assertThat(shadowSubscriptionManager.isNetworkRoaming(123)).isTrue();
    }

    /**
     * Multi act-asserts are discouraged but here we are testing the set+unset.
     */
    @Test
    public void isNetworkRoaming_shouldReturnFalseIfUnset() {
        /* isNetworkRoaming= */
        shadowSubscriptionManager.setNetworkRoamingStatus(123, true);
        assertThat(shadowSubscriptionManager.isNetworkRoaming(123)).isTrue();
        /* isNetworkRoaming= */
        shadowSubscriptionManager.setNetworkRoamingStatus(123, false);
        assertThat(shadowSubscriptionManager.isNetworkRoaming(123)).isFalse();
    }

    /**
     * Multi act-asserts are discouraged but here we are testing the set+clear.
     */
    @Test
    public void isNetworkRoaming_shouldReturnFalseOnClear() {
        /* isNetworkRoaming= */
        shadowSubscriptionManager.setNetworkRoamingStatus(123, true);
        assertThat(shadowSubscriptionManager.isNetworkRoaming(123)).isTrue();
        shadowSubscriptionManager.clearNetworkRoamingStatus();
        assertThat(shadowSubscriptionManager.isNetworkRoaming(123)).isFalse();
    }

    @Test
    public void getActiveSubscriptionInfoCount_shouldReturnZeroIfActiveSubscriptionInfoListNotSet() {
        shadowSubscriptionManager.setActiveSubscriptionInfoList(null);
        assertThat(shadowSubscriptionManager.getActiveSubscriptionInfoCount()).isEqualTo(0);
    }

    @Test
    public void getActiveSubscriptionInfoCount_shouldReturnSizeOfActiveSubscriotionInfosList() {
        SubscriptionInfo expectedSubscriptionInfo = SubscriptionInfoBuilder.newBuilder().setId(123).buildSubscriptionInfo();
        shadowSubscriptionManager.setActiveSubscriptionInfos(expectedSubscriptionInfo);
        assertThat(shadowSubscriptionManager.getActiveSubscriptionInfoCount()).isEqualTo(1);
    }

    private static class DummySubscriptionsChangedListener extends SubscriptionManager.OnSubscriptionsChangedListener {
        private boolean subscriptionChanged = false;

        @Override
        public void onSubscriptionsChanged() {
            subscriptionChanged = true;
        }
    }
}

