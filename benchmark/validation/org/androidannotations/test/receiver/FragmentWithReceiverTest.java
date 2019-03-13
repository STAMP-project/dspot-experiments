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

import static FragmentWithReceiver.RECEIVER_ACTION;


@RunWith(RobolectricTestRunner.class)
public class FragmentWithReceiverTest {
    FragmentWithReceiver_ fragment;

    @Test
    public void defaultReceiverCalled() {
        Intent intent = new Intent(RECEIVER_ACTION);
        fragment.getActivity().sendBroadcast(intent);
        Assert.assertTrue(fragment.defaultReceiverCalled);
    }

    @Test
    public void onAttachReceiverCalled() {
        Intent intent = new Intent(RECEIVER_ACTION);
        fragment.getActivity().sendBroadcast(intent);
        Assert.assertTrue(fragment.onAttachReceiverCalled);
    }

    @Test
    public void onStartReceiverCalled() {
        Intent intent = new Intent(RECEIVER_ACTION);
        fragment.getActivity().sendBroadcast(intent);
        Assert.assertTrue(fragment.onStartReceiverCalled);
    }

    @Test
    public void onResumeReceiverCalled() {
        Intent intent = new Intent(RECEIVER_ACTION);
        LocalBroadcastManager.getInstance(application).sendBroadcast(intent);
        Assert.assertTrue(fragment.onResumeReceiverCalled);
    }
}

