package com.zegoggles.smssync.preferences;


import RuntimeEnvironment.application;
import android.preference.PreferenceManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class AuthPreferencesTest {
    private AuthPreferences authPreferences;

    @Test
    public void testStoreUri() throws Exception {
        PreferenceManager.getDefaultSharedPreferences(application).edit().putString("server_address", "foo.com:993").putString("server_protocol", "+ssl+").putString("server_authentication", "plain").commit();
        authPreferences.setImapUser("a:user");
        authPreferences.setImapPassword("password:has:colons");
        assertThat(authPreferences.getStoreUri()).isEqualTo("imap+ssl+://PLAIN:a%253Auser:password%253Ahas%253Acolons@foo.com:993");
    }

    @Test
    public void testStoreUriWithXOAuth2() throws Exception {
        PreferenceManager.getDefaultSharedPreferences(application).edit().putString("server_address", "imap.gmail.com:993").putString("server_protocol", "+ssl+").putString("server_authentication", "xoauth").commit();
        authPreferences.setOauth2Token("user", "token", null);
        assertThat(authPreferences.getStoreUri()).isEqualTo("imap+ssl+://XOAUTH2:user:dXNlcj11c2VyAWF1dGg9QmVhcmVyIHRva2VuAQE%253D@imap.gmail.com:993");
    }
}

