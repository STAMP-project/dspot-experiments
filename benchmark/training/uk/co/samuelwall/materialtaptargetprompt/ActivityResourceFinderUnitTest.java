/**
 * Copyright (C) 2017 Samuel Wall
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
package uk.co.samuelwall.materialtaptargetprompt;


import Build.VERSION;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22)
public class ActivityResourceFinderUnitTest {
    @Test
    public void testGetString() {
        final String resource = "test string";
        final int resourceId = 64532;
        final Activity activity = Mockito.mock(Activity.class);
        final ActivityResourceFinder resourceFinder = new ActivityResourceFinder(activity);
        Mockito.when(activity.getString(resourceId)).thenReturn(resource);
        Assert.assertEquals(resource, resourceFinder.getString(resourceId));
    }

    @Test
    public void testGetDrawable() {
        final Drawable resource = Mockito.mock(Drawable.class);
        final int resourceId = 64532;
        final Activity activity = Mockito.mock(Activity.class);
        final ActivityResourceFinder resourceFinder = new ActivityResourceFinder(activity);
        Mockito.when(activity.getDrawable(resourceId)).thenReturn(resource);
        Assert.assertEquals(resource, resourceFinder.getDrawable(resourceId));
    }

    @Test
    public void testGetDrawablePreLollipop() {
        ReflectionHelpers.setStaticField(VERSION.class, "SDK_INT", 20);
        final Drawable resource = Mockito.mock(Drawable.class);
        final int resourceId = 64532;
        final Activity activity = Mockito.mock(Activity.class);
        final ActivityResourceFinder resourceFinder = new ActivityResourceFinder(activity);
        final Resources resources = Mockito.mock(Resources.class);
        Mockito.when(activity.getResources()).thenReturn(resources);
        Mockito.when(resources.getDrawable(resourceId)).thenReturn(resource);
        Assert.assertEquals(resource, resourceFinder.getDrawable(resourceId));
    }
}

