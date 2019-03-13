/**
 * Copyright (c) 2016 Ha Duy Trung
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
package io.github.hidroh.materialistic;


import RuntimeEnvironment.application;
import android.app.Activity;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.RemoteException;
import android.support.customtabs.ICustomTabsCallback;
import android.support.customtabs.ICustomTabsService;
import io.github.hidroh.materialistic.test.TestRunner;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowResolveInfo;


@RunWith(TestRunner.class)
public class CustomTabsDelegateTest {
    private CustomTabsDelegate delegate;

    private ActivityController<Activity> controller;

    private Activity activity;

    private ICustomTabsService service;

    @Test
    public void testUnboundService() {
        Assert.assertFalse(delegate.mayLaunchUrl(Uri.parse("http://www.example.com"), null, null));
        Assert.assertNull(delegate.getSession());
    }

    @Test
    public void testBindService() throws RemoteException {
        // no chrome installed should not bind service
        delegate.bindCustomTabsService(activity);
        assertThat(ShadowApplication.getInstance().getBoundServiceConnections()).isEmpty();
        // bind service should create connection
        Shadows.shadowOf(application.getPackageManager()).addResolveInfoForIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com")), ShadowResolveInfo.newResolveInfo("label", "com.android.chrome", "DefaultActivity"));
        Shadows.shadowOf(application.getPackageManager()).addResolveInfoForIntent(new Intent("android.support.customtabs.action.CustomTabsService").setPackage("com.android.chrome"), ShadowResolveInfo.newResolveInfo("label", "com.android.chrome", "DefaultActivity"));
        delegate.bindCustomTabsService(activity);
        List<ServiceConnection> connections = ShadowApplication.getInstance().getBoundServiceConnections();
        assertThat(connections).isNotEmpty();
        // on service connected should create session and warm up client
        Mockito.verify(service).warmup(ArgumentMatchers.anyLong());
        Assert.assertNotNull(delegate.getSession());
        Mockito.verify(service).newSession(ArgumentMatchers.any(ICustomTabsCallback.class));
        // may launch url should success
        Mockito.when(service.mayLaunchUrl(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(true);
        Assert.assertTrue(delegate.mayLaunchUrl(Uri.parse("http://www.example.com"), null, null));
        // on service disconnected should clear session
        delegate.unbindCustomTabsService(activity);
        Assert.assertNull(delegate.getSession());
    }
}

