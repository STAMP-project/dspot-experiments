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


import AuthUI.EMAIL_LINK_PROVIDER;
import CheckEmailFragment.TAG;
import EmailAuthProvider.PROVIDER_ID;
import R.id.button_create;
import R.id.name_layout;
import R.id.password_layout;
import R.integer.fui_min_password_length;
import R.plurals.fui_error_weak_password;
import R.string.fui_required_field;
import RuntimeEnvironment.application;
import android.support.design.widget.TextInputLayout;
import android.widget.Button;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.testhelpers.TestConstants;
import com.firebase.ui.auth.util.data.EmailLinkPersistenceManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class EmailActivityTest {
    private static final String EMAIL = "email";

    private static final String ID_TOKEN = "idToken";

    private static final String SECRET = "secret";

    @Test
    public void testOnCreate_passwordNormalFlow_expectCheckEmailFlowStarted() {
        EmailActivity emailActivity = createActivity(PROVIDER_ID);
        emailActivity.getSupportFragmentManager().findFragmentByTag(TAG);
    }

    @Test
    public void testOnCreate_emailLinkNormalFlow_expectCheckEmailFlowStarted() {
        EmailActivity emailActivity = createActivity(EMAIL_LINK_PROVIDER);
        emailActivity.getSupportFragmentManager().findFragmentByTag(TAG);
    }

    @Test
    public void testOnCreate_emailLinkLinkingFlow_expectSendEmailLinkFlowStarted() {
        // This is normally done by EmailLinkSendEmailHandler, saving the IdpResponse is done
        // in EmailActivity but it will not be saved if we haven't previously set the email
        EmailLinkPersistenceManager.getInstance().saveEmail(application, EmailActivityTest.EMAIL, TestConstants.SESSION_ID, TestConstants.UID);
        EmailActivity emailActivity = createActivity(EMAIL_LINK_PROVIDER, true);
        EmailLinkFragment fragment = ((EmailLinkFragment) (emailActivity.getSupportFragmentManager().findFragmentByTag(EmailLinkFragment.TAG)));
        assertThat(fragment).isNotNull();
        EmailLinkPersistenceManager persistenceManager = EmailLinkPersistenceManager.getInstance();
        IdpResponse response = persistenceManager.retrieveSessionRecord(application).getIdpResponseForLinking();
        assertThat(response.getProviderType()).isEqualTo(GoogleAuthProvider.PROVIDER_ID);
        assertThat(response.getEmail()).isEqualTo(EmailActivityTest.EMAIL);
        assertThat(response.getIdpToken()).isEqualTo(EmailActivityTest.ID_TOKEN);
        assertThat(response.getIdpSecret()).isEqualTo(EmailActivityTest.SECRET);
    }

    @Test
    public void testOnClickResendEmail_expectSendEmailLinkFlowStarted() {
        EmailActivity emailActivity = createActivity(EMAIL_LINK_PROVIDER);
        emailActivity.onClickResendEmail(EmailActivityTest.EMAIL);
        EmailLinkFragment fragment = ((EmailLinkFragment) (emailActivity.getSupportFragmentManager().findFragmentByTag(EmailLinkFragment.TAG)));
        assertThat(fragment).isNotNull();
    }

    @Test
    public void testSignUpButton_validatesFields() {
        EmailActivity emailActivity = createActivity(PROVIDER_ID);
        // Trigger RegisterEmailFragment (bypass check email)
        emailActivity.onNewUser(build());
        Button button = emailActivity.findViewById(button_create);
        button.performClick();
        TextInputLayout nameLayout = emailActivity.findViewById(name_layout);
        TextInputLayout passwordLayout = emailActivity.findViewById(password_layout);
        Assert.assertEquals(emailActivity.getString(fui_required_field), nameLayout.getError().toString());
        Assert.assertEquals(String.format(emailActivity.getResources().getQuantityString(fui_error_weak_password, fui_min_password_length), emailActivity.getResources().getInteger(fui_min_password_length)), passwordLayout.getError().toString());
    }
}

