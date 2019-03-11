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
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static ReceiverActions.ACTION_0;
import static ReceiverActions.ACTION_1;
import static ReceiverActions.ACTION_2;


@RunWith(RobolectricTestRunner.class)
public class EViewAndEViewGroupWithReceiverTest {
    private EViewAndEViewGroupWithReceiverActivity_ activity;

    @Test
    public void eViewAndEViewGroupReceivedAction0Test() {
        Intent intent = new Intent(ACTION_0);
        activity.sendBroadcast(intent);
        Assert.assertTrue(activity.viewWithReceiver.action0Received);
        Assert.assertTrue(activity.viewGroupWithReceiver.action0Received);
    }

    @Test
    public void eViewAndEViewGroupReceivedAction1WithExtraTest() {
        String extraContent = "extraContent";
        Intent intent = new Intent(ACTION_1);
        intent.putExtra("extra", extraContent);
        activity.sendBroadcast(intent);
        Assert.assertTrue(activity.viewWithReceiver.action1Received);
        Assert.assertTrue(activity.viewWithReceiver.action1Extra.equals(extraContent));
        Assert.assertTrue(activity.viewGroupWithReceiver.action1Received);
        Assert.assertTrue(activity.viewGroupWithReceiver.action1Extra.equals(extraContent));
    }

    @Test
    public void eViewAndEViewGroupReceivedAction2WithExtraTest() {
        Intent intent = new Intent(ACTION_2);
        Intent extraIntent = new Intent("someAction");
        intent.putExtra("extra", extraIntent);
        LocalBroadcastManager.getInstance(application).sendBroadcast(intent);
        Assert.assertTrue(activity.viewWithReceiver.action2Received);
        Assert.assertTrue(activity.viewWithReceiver.action2Extra.equals(extraIntent));
        Assert.assertTrue(activity.viewGroupWithReceiver.action2Received);
        Assert.assertTrue(activity.viewGroupWithReceiver.action2Extra.equals(extraIntent));
    }
}

