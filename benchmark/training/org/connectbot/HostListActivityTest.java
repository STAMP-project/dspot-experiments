/**
 * ConnectBot: simple, powerful, open-source SSH client for Android
 * Copyright 2015 Kenny Root, Jeffrey Sharkey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.connectbot;


import android.content.Intent;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.connectbot.service.TerminalManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;


@RunWith(AndroidJUnit4.class)
public class HostListActivityTest {
    @Test
    public void bindsToTerminalManager() {
        TerminalManager terminalManager = Mockito.spy(TerminalManager.class);
        mockBindToService(terminalManager);
        HostListActivity activity = Robolectric.buildActivity(HostListActivity.class).create().start().get();
        Intent serviceIntent = new Intent(activity, TerminalManager.class);
        Intent actualIntent = shadowOf(activity).getNextStartedService();
        Assert.assertTrue(actualIntent.filterEquals(serviceIntent));
    }
}

