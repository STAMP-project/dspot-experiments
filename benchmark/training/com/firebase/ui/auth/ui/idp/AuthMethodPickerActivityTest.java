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
package com.firebase.ui.auth.ui.idp;


import AuthUI.ANONYMOUS_PROVIDER;
import GoogleAuthProvider.PROVIDER_ID;
import R.id.email_button;
import R.id.phone_button;
import R.layout;
import ShadowActivity.IntentForResult;
import android.widget.Button;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.ui.email.EmailActivity;
import com.firebase.ui.auth.ui.phone.PhoneActivity;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowActivity;


@RunWith(RobolectricTestRunner.class)
public class AuthMethodPickerActivityTest {
    @Test
    public void testAllProvidersArePopulated() {
        // Exclude Facebook until the `NoClassDefFoundError: com/facebook/common/R$style` exception
        // is fixed.
        List<String> providers = Arrays.asList(PROVIDER_ID, TwitterAuthProvider.PROVIDER_ID, EmailAuthProvider.PROVIDER_ID, PhoneAuthProvider.PROVIDER_ID, ANONYMOUS_PROVIDER);
        AuthMethodPickerActivity authMethodPickerActivity = createActivity(providers);
        Assert.assertEquals(providers.size(), getChildCount());
    }

    @Test
    public void testEmailLoginFlow() {
        List<String> providers = Arrays.asList(EmailAuthProvider.PROVIDER_ID);
        AuthMethodPickerActivity authMethodPickerActivity = createActivity(providers);
        Button emailButton = authMethodPickerActivity.findViewById(email_button);
        emailButton.performClick();
        ShadowActivity.IntentForResult nextIntent = Shadows.shadowOf(authMethodPickerActivity).getNextStartedActivityForResult();
        Assert.assertEquals(EmailActivity.class.getName(), nextIntent.intent.getComponent().getClassName());
    }

    @Test
    public void testPhoneLoginFlow() {
        List<String> providers = Arrays.asList(PhoneAuthProvider.PROVIDER_ID);
        AuthMethodPickerActivity authMethodPickerActivity = createActivity(providers);
        Button phoneButton = authMethodPickerActivity.findViewById(phone_button);
        phoneButton.performClick();
        ShadowActivity.IntentForResult nextIntent = Shadows.shadowOf(authMethodPickerActivity).getNextStartedActivityForResult();
        Assert.assertEquals(PhoneActivity.class.getName(), nextIntent.intent.getComponent().getClassName());
    }

    @Test
    public void testCustomAuthMethodPickerLayout() {
        List<String> providers = Arrays.asList(EmailAuthProvider.PROVIDER_ID);
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout.Builder(layout.fui_provider_button_email).setEmailButtonId(email_button).build();
        AuthMethodPickerActivity authMethodPickerActivity = createActivityWithCustomLayout(providers, customLayout);
        Button emailButton = authMethodPickerActivity.findViewById(email_button);
        emailButton.performClick();
        // Expected result -> Directing users to EmailActivity
        ShadowActivity.IntentForResult nextIntent = Shadows.shadowOf(authMethodPickerActivity).getNextStartedActivityForResult();
        Assert.assertEquals(EmailActivity.class.getName(), nextIntent.intent.getComponent().getClassName());
    }
}

