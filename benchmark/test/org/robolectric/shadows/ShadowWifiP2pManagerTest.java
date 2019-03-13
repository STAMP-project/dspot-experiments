package org.robolectric.shadows;


import WifiP2pManager.BUSY;
import WifiP2pManager.Channel;
import WifiP2pManager.ChannelListener;
import WifiP2pManager.ERROR;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;


@RunWith(AndroidJUnit4.class)
public class ShadowWifiP2pManagerTest {
    private WifiP2pManager manager;

    private ShadowWifiP2pManager shadowManager;

    @Mock
    private ChannelListener mockListener;

    private Channel channel;

    @Test
    public void createGroup_success() {
        ShadowWifiP2pManagerTest.TestActionListener testListener = new ShadowWifiP2pManagerTest.TestActionListener();
        manager.createGroup(channel, testListener);
        assertThat(testListener.success).isTrue();
    }

    @Test
    public void createGroup_nullListener() {
        manager.createGroup(channel, null);
        // Should not fail with a null listener
    }

    @Test
    public void createGroup_fail() {
        ShadowWifiP2pManagerTest.TestActionListener testListener = new ShadowWifiP2pManagerTest.TestActionListener();
        RuntimeEnvironment.getMasterScheduler().pause();
        manager.createGroup(channel, testListener);
        shadowManager.setNextActionFailure(BUSY);
        RuntimeEnvironment.getMasterScheduler().unPause();
        assertThat(testListener.success).isFalse();
        assertThat(testListener.reason).isEqualTo(BUSY);
    }

    @Test
    public void clearActionFailure() {
        shadowManager.setNextActionFailure(ERROR);
        ShadowWifiP2pManagerTest.TestActionListener testListener = new ShadowWifiP2pManagerTest.TestActionListener();
        manager.createGroup(channel, testListener);
        assertThat(testListener.success).isFalse();
        manager.createGroup(channel, testListener);
        assertThat(testListener.success).isTrue();
    }

    @Test
    public void removeGroup_success() {
        ShadowWifiP2pManagerTest.TestActionListener testListener = new ShadowWifiP2pManagerTest.TestActionListener();
        manager.removeGroup(channel, testListener);
        assertThat(testListener.success).isTrue();
    }

    @Test
    public void removeGroup_nullListener() {
        manager.removeGroup(channel, null);
        // Should not fail with a null listener
    }

    @Test
    public void removeGroup_failure() {
        ShadowWifiP2pManagerTest.TestActionListener testListener = new ShadowWifiP2pManagerTest.TestActionListener();
        RuntimeEnvironment.getMasterScheduler().pause();
        manager.removeGroup(channel, testListener);
        shadowManager.setNextActionFailure(BUSY);
        RuntimeEnvironment.getMasterScheduler().unPause();
        assertThat(testListener.success).isFalse();
        assertThat(testListener.reason).isEqualTo(BUSY);
    }

    @Test
    public void requestGroupInfo() {
        ShadowWifiP2pManagerTest.TestGroupInfoListener listener = new ShadowWifiP2pManagerTest.TestGroupInfoListener();
        WifiP2pGroup wifiP2pGroup = new WifiP2pGroup();
        Shadows.shadowOf(wifiP2pGroup).setInterface("ssid");
        Shadows.shadowOf(wifiP2pGroup).setPassphrase("passphrase");
        Shadows.shadowOf(wifiP2pGroup).setNetworkName("networkname");
        shadowManager.setGroupInfo(channel, wifiP2pGroup);
        manager.requestGroupInfo(channel, listener);
        assertThat(listener.group.getNetworkName()).isEqualTo(wifiP2pGroup.getNetworkName());
        assertThat(listener.group.getInterface()).isEqualTo(wifiP2pGroup.getInterface());
        assertThat(listener.group.getPassphrase()).isEqualTo(wifiP2pGroup.getPassphrase());
    }

    @Test
    public void requestGroupInfo_nullListener() {
        WifiP2pGroup wifiP2pGroup = new WifiP2pGroup();
        shadowManager.setGroupInfo(channel, wifiP2pGroup);
        manager.requestGroupInfo(channel, null);
        // Should not fail with a null listener
    }

    private static class TestActionListener implements WifiP2pManager.ActionListener {
        private int reason;

        private boolean success;

        @Override
        public void onSuccess() {
            success = true;
        }

        @Override
        public void onFailure(int reason) {
            success = false;
            this.reason = reason;
        }
    }

    private static class TestGroupInfoListener implements WifiP2pManager.GroupInfoListener {
        private WifiP2pGroup group;

        @Override
        public void onGroupInfoAvailable(WifiP2pGroup group) {
            this.group = group;
        }
    }
}

