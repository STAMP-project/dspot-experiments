/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2019 the AndroidAnnotations project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.test.receiver;


import RuntimeEnvironment.application;
import WifiManager.EXTRA_BSSID;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static ActivityWithReceiver.ACTION_1;
import static ActivityWithReceiver.ACTION_2;
import static ActivityWithReceiver.CUSTOM_HTTP_ACTION;


@RunWith(RobolectricTestRunner.class)
public class ActivityWithReceiverTest {
    private ActivityWithReceiver_ activity;

    @Test
    public void onWifiStateChangedTest() {
        final String SSID = "TEST SSID";
        Intent intent = new Intent(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intent.putExtra(EXTRA_BSSID, SSID);
        activity.sendBroadcast(intent);
        Assert.assertTrue(activity.wifiChangeIntentReceived);
        Assert.assertTrue(SSID.equals(activity.wifiSsid));
    }

    @Test
    public void onLocalWifiStateChangedTest() {
        Intent intent = new Intent(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        LocalBroadcastManager.getInstance(application).sendBroadcast(intent);
        Assert.assertTrue(activity.localWifiChangeIntentReceived);
    }

    @Test
    public void onDataShemeHttpTest() {
        Intent intentFtp = new Intent(CUSTOM_HTTP_ACTION);
        intentFtp.setData(Uri.parse("ftp://androidannotations.org"));
        activity.sendBroadcast(intentFtp);
        Assert.assertFalse(activity.dataSchemeHttpIntentReceived);
        Intent intentHttp = new Intent(CUSTOM_HTTP_ACTION);
        intentHttp.setData(Uri.parse("http://androidannotations.org"));
        activity.sendBroadcast(intentHttp);
        Assert.assertTrue(activity.dataSchemeHttpIntentReceived);
    }

    @Test
    public void onBroadcastWithTwoActionsTest() {
        Intent intent1 = new Intent(ACTION_1);
        Intent intent2 = new Intent(ACTION_2);
        Assert.assertFalse(activity.action1Fired);
        Assert.assertFalse(activity.action2Fired);
        activity.sendBroadcast(intent1);
        Assert.assertTrue(activity.action1Fired);
        Assert.assertFalse(activity.action2Fired);
        activity.sendBroadcast(intent2);
        Assert.assertTrue(activity.action1Fired);
        Assert.assertTrue(activity.action2Fired);
    }

    @Test
    public void onBroadcastWithExtrasTest() {
        Intent intent = new Intent(ACTION_1);
        Intent extraIntent = new Intent("someAction");
        intent.putExtra("extraIntent", extraIntent);
        activity.sendBroadcast(intent);
        Assert.assertEquals(intent, activity.originalIntent);
        Assert.assertEquals(extraIntent, activity.extraIntent);
    }
}

