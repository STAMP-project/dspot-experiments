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


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.view.Window;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DialogResourceFinderUnitTest {
    @Test
    public void testGetPromptParentView() {
        final Dialog dialog = Mockito.mock(Dialog.class);
        final Activity activity = Mockito.mock(Activity.class);
        final Window window = Mockito.mock(Window.class);
        final ViewGroup decorView = Mockito.mock(ViewGroup.class);
        final DialogResourceFinder resourceFinder = new DialogResourceFinder(dialog);
        Mockito.when(window.getDecorView()).thenReturn(decorView);
        Mockito.when(dialog.getWindow()).thenReturn(window);
        Mockito.when(dialog.getOwnerActivity()).thenReturn(activity);
        Assert.assertEquals(decorView, resourceFinder.getPromptParentView());
    }

    @Test
    public void testGetContext() {
        final Dialog dialog = Mockito.mock(Dialog.class);
        final Activity activity = Mockito.mock(Activity.class);
        final Context context = Mockito.mock(Context.class);
        final DialogResourceFinder resourceFinder = new DialogResourceFinder(dialog);
        Mockito.when(dialog.getOwnerActivity()).thenReturn(activity);
        Mockito.when(dialog.getContext()).thenReturn(context);
        Assert.assertEquals(context, resourceFinder.getContext());
    }
}

