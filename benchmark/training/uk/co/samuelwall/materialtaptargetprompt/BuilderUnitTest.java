/**
 * Copyright (C) 2017-2018 Samuel Wall
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


import MaterialTapTargetPrompt.Builder;
import android.R.id.content;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.view.ViewGroup;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22)
public class BuilderUnitTest {
    @Test
    public void testBuilder_Fragment() {
        final Fragment fragment = Robolectric.buildFragment(android.support.v4.app.Fragment.class).create().get();
        final MaterialTapTargetPrompt.Builder builder = new MaterialTapTargetPrompt.Builder(fragment);
        Assert.assertTrue(((builder.getResourceFinder()) instanceof ActivityResourceFinder));
    }

    @Test
    public void testBuilder_Fragment_Resource() {
        final Fragment fragment = Robolectric.buildFragment(android.support.v4.app.Fragment.class).create().get();
        final MaterialTapTargetPrompt.Builder builder = new MaterialTapTargetPrompt.Builder(fragment, 0);
        Assert.assertTrue(((builder.getResourceFinder()) instanceof ActivityResourceFinder));
    }

    @Test
    public void testBuilder_DialogFragment() {
        final DialogFragment dialogFragment = Mockito.spy(Robolectric.buildFragment(android.support.v4.app.DialogFragment.class).create().get());
        final Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        final Dialog dialog = Mockito.mock(Dialog.class);
        Mockito.when(dialogFragment.getDialog()).thenReturn(dialog);
        Mockito.when(dialog.getOwnerActivity()).thenReturn(activity);
        Mockito.when(dialog.findViewById(content)).thenReturn(activity.findViewById(content));
        final MaterialTapTargetPrompt.Builder builder = new MaterialTapTargetPrompt.Builder(dialogFragment);
        Assert.assertTrue(((builder.getResourceFinder()) instanceof DialogResourceFinder));
    }

    @Test
    public void testBuilder_Dialog() {
        final Dialog dialog = Mockito.mock(Dialog.class);
        final Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        Mockito.when(dialog.getOwnerActivity()).thenReturn(activity);
        Mockito.when(dialog.findViewById(content)).thenReturn(activity.findViewById(content));
        final MaterialTapTargetPrompt.Builder builder = new MaterialTapTargetPrompt.Builder(dialog);
        Assert.assertTrue(((builder.getResourceFinder()) instanceof DialogResourceFinder));
    }

    @Test
    public void testBuilder_Activity() {
        final Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        final MaterialTapTargetPrompt.Builder builder = new MaterialTapTargetPrompt.Builder(activity);
        Assert.assertTrue(((builder.getResourceFinder()) instanceof ActivityResourceFinder));
    }

    @Test
    public void testBuilder_SupportFragment() {
        final android.support.v4.app.Fragment fragment = Mockito.spy(new android.support.v4.app.Fragment());
        final ViewGroup view = Mockito.mock(ViewGroup.class);
        Mockito.when(fragment.getView()).thenReturn(view);
        startFragment(fragment);
        final MaterialTapTargetPrompt.Builder builder = new MaterialTapTargetPrompt.Builder(fragment);
        Assert.assertTrue(((builder.getResourceFinder()) instanceof SupportFragmentResourceFinder));
    }

    @Test
    public void testBuilder_SupportFragment_Resource() {
        final android.support.v4.app.Fragment fragment = Mockito.spy(new android.support.v4.app.Fragment());
        final ViewGroup view = Mockito.mock(ViewGroup.class);
        Mockito.when(fragment.getView()).thenReturn(view);
        startFragment(fragment);
        final MaterialTapTargetPrompt.Builder builder = new MaterialTapTargetPrompt.Builder(fragment, 0);
        Assert.assertTrue(((builder.getResourceFinder()) instanceof SupportFragmentResourceFinder));
    }

    @Test
    public void testBuilder_SupportDialogFragment() {
        final android.support.v4.app.DialogFragment dialogFragment = Mockito.spy(new android.support.v4.app.DialogFragment());
        startFragment(dialogFragment);
        final ViewGroup view = Mockito.mock(ViewGroup.class);
        Mockito.when(dialogFragment.getView()).thenReturn(view);
        final Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        final Dialog dialog = Mockito.mock(Dialog.class);
        Mockito.when(dialogFragment.getDialog()).thenReturn(dialog);
        Mockito.when(dialog.getOwnerActivity()).thenReturn(activity);
        Mockito.when(dialog.findViewById(content)).thenReturn(activity.findViewById(content));
        final MaterialTapTargetPrompt.Builder builder = new MaterialTapTargetPrompt.Builder(dialogFragment);
        Assert.assertTrue(((builder.getResourceFinder()) instanceof SupportFragmentResourceFinder));
    }
}

