/**
 * Copyright (C) 2016-2018 Samuel Wall
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
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.app.DialogFragment;
import android.view.ViewGroup;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22)
public class SupportFragmentResourceFinderUnitTest {
    @Test
    public void testGetString() {
        final String resource = "test string";
        final int resourceId = 64532;
        final DialogFragment dialogFragment = Mockito.spy(new DialogFragment());
        final SupportFragmentResourceFinder resourceFinder = new SupportFragmentResourceFinder(dialogFragment);
        final Context context = Mockito.mock(Context.class);
        Mockito.when(dialogFragment.getContext()).thenReturn(context);
        final Resources resources = Mockito.mock(Resources.class);
        Mockito.when(dialogFragment.getResources()).thenReturn(resources);
        Mockito.when(dialogFragment.getString(resourceId)).thenReturn(resource);
        Assert.assertEquals(resource, resourceFinder.getString(resourceId));
    }

    @Test
    public void testGetDrawable() {
        final int resourceId = 64532;
        final DialogFragment dialogFragment = Mockito.spy(new DialogFragment());
        final SupportFragmentResourceFinder resourceFinder = new SupportFragmentResourceFinder(dialogFragment);
        final Drawable resource = Mockito.mock(Drawable.class);
        final Resources resources = Mockito.mock(Resources.class);
        final Context context = Mockito.mock(Context.class);
        Mockito.when(dialogFragment.getContext()).thenReturn(context);
        Mockito.when(dialogFragment.getResources()).thenReturn(resources);
        Mockito.when(resources.getDrawable(resourceId)).thenReturn(resource);
        Assert.assertEquals(resource, resourceFinder.getDrawable(resourceId));
    }

    @Test
    public void testGetDrawablePreLollipop() {
        ReflectionHelpers.setStaticField(VERSION.class, "SDK_INT", 20);
        final Drawable resource = Mockito.mock(Drawable.class);
        final int resourceId = 64532;
        final DialogFragment dialogFragment = Mockito.spy(new DialogFragment());
        final SupportFragmentResourceFinder resourceFinder = new SupportFragmentResourceFinder(dialogFragment);
        final Resources resources = Mockito.mock(Resources.class);
        final Context context = Mockito.mock(Context.class);
        Mockito.when(dialogFragment.getContext()).thenReturn(context);
        Mockito.when(dialogFragment.getResources()).thenReturn(resources);
        Mockito.when(resources.getDrawable(resourceId)).thenReturn(resource);
        Assert.assertEquals(resource, resourceFinder.getDrawable(resourceId));
    }

    @Test
    public void testGetPromptParentView() {
        final DialogFragment dialogFragment = Mockito.spy(new DialogFragment());
        final ViewGroup parent = Mockito.mock(ViewGroup.class);
        final ViewGroup view = Mockito.mock(ViewGroup.class);
        final SupportFragmentResourceFinder resourceFinder = new SupportFragmentResourceFinder(dialogFragment);
        Mockito.when(dialogFragment.getView()).thenReturn(view);
        Mockito.when(view.getParent()).thenReturn(parent);
        Assert.assertEquals(parent, resourceFinder.getPromptParentView());
        Assert.assertEquals(parent, resourceFinder.getPromptParentView());
    }

    @Test
    public void testGetContext() {
        final DialogFragment dialogFragment = Mockito.spy(new DialogFragment());
        final Context context = Mockito.mock(Context.class);
        final SupportFragmentResourceFinder resourceFinder = new SupportFragmentResourceFinder(dialogFragment);
        Mockito.when(dialogFragment.getContext()).thenReturn(context);
        Assert.assertEquals(context, resourceFinder.getContext());
    }
}

