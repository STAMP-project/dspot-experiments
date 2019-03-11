/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.firebase.ui.auth.ui.email;


import R.id.button_done;
import R.id.password_layout;
import R.string.fui_required_field;
import ShadowActivity.IntentForResult;
import android.support.design.widget.TextInputLayout;
import android.widget.Button;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowActivity;


@RunWith(RobolectricTestRunner.class)
public class WelcomeBackPasswordPromptTest {
    @Test
    public void testSignInButton_validatesFields() {
        WelcomeBackPasswordPrompt welcomeBack = createActivity();
        Button signIn = welcomeBack.findViewById(button_done);
        signIn.performClick();
        TextInputLayout passwordLayout = welcomeBack.findViewById(password_layout);
        Assert.assertEquals(welcomeBack.getString(fui_required_field), passwordLayout.getError().toString());
        // should block and not start a new activity
        ShadowActivity.IntentForResult nextIntent = Shadows.shadowOf(welcomeBack).getNextStartedActivityForResult();
        Assert.assertNull(nextIntent);
    }
}

