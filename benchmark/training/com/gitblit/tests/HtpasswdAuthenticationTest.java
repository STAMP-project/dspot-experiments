/**
 * Copyright 2013 gitblit.com.
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
package com.gitblit.tests;


import com.gitblit.auth.HtpasswdAuthProvider;
import com.gitblit.manager.AuthenticationManager;
import com.gitblit.models.UserModel;
import com.gitblit.tests.mock.MemorySettings;
import java.io.IOException;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the Htpasswd user service.
 */
public class HtpasswdAuthenticationTest extends GitblitUnitTest {
    private static final String RESOURCE_DIR = "src/test/resources/htpasswd/";

    private static final String KEY_SUPPORT_PLAINTEXT_PWD = "realm.htpasswd.supportPlaintextPasswords";

    private static final int NUM_USERS_HTPASSWD = 10;

    private static final MemorySettings MS = new MemorySettings(new HashMap<String, Object>());

    private HtpasswdAuthProvider htpasswd;

    private AuthenticationManager auth;

    @Test
    public void testSetup() throws IOException {
        Assert.assertEquals(HtpasswdAuthenticationTest.NUM_USERS_HTPASSWD, htpasswd.getNumberHtpasswdUsers());
    }

    @Test
    public void testAuthenticate() {
        HtpasswdAuthenticationTest.MS.put(HtpasswdAuthenticationTest.KEY_SUPPORT_PLAINTEXT_PWD, "true");
        UserModel user = htpasswd.authenticate("user1", "pass1".toCharArray());
        Assert.assertNotNull(user);
        Assert.assertEquals("user1", user.username);
        user = htpasswd.authenticate("user2", "pass2".toCharArray());
        Assert.assertNotNull(user);
        Assert.assertEquals("user2", user.username);
        // Test different encryptions
        user = htpasswd.authenticate("plain", "passWord".toCharArray());
        Assert.assertNotNull(user);
        Assert.assertEquals("plain", user.username);
        HtpasswdAuthenticationTest.MS.put(HtpasswdAuthenticationTest.KEY_SUPPORT_PLAINTEXT_PWD, "false");
        user = htpasswd.authenticate("crypt", "password".toCharArray());
        Assert.assertNotNull(user);
        Assert.assertEquals("crypt", user.username);
        user = htpasswd.authenticate("md5", "password".toCharArray());
        Assert.assertNotNull(user);
        Assert.assertEquals("md5", user.username);
        user = htpasswd.authenticate("sha", "password".toCharArray());
        Assert.assertNotNull(user);
        Assert.assertEquals("sha", user.username);
        // Test leading and trailing whitespace
        user = htpasswd.authenticate("trailing", "whitespace".toCharArray());
        Assert.assertNotNull(user);
        Assert.assertEquals("trailing", user.username);
        user = htpasswd.authenticate("tabbed", "frontAndBack".toCharArray());
        Assert.assertNotNull(user);
        Assert.assertEquals("tabbed", user.username);
        user = htpasswd.authenticate("leading", "whitespace".toCharArray());
        Assert.assertNotNull(user);
        Assert.assertEquals("leading", user.username);
    }

    @Test
    public void testAuthenticationManager() {
        HtpasswdAuthenticationTest.MS.put(HtpasswdAuthenticationTest.KEY_SUPPORT_PLAINTEXT_PWD, "true");
        UserModel user = auth.authenticate("user1", "pass1".toCharArray(), null);
        Assert.assertNotNull(user);
        Assert.assertEquals("user1", user.username);
        user = auth.authenticate("user2", "pass2".toCharArray(), null);
        Assert.assertNotNull(user);
        Assert.assertEquals("user2", user.username);
        // Test different encryptions
        user = auth.authenticate("plain", "passWord".toCharArray(), null);
        Assert.assertNotNull(user);
        Assert.assertEquals("plain", user.username);
        HtpasswdAuthenticationTest.MS.put(HtpasswdAuthenticationTest.KEY_SUPPORT_PLAINTEXT_PWD, "false");
        user = auth.authenticate("crypt", "password".toCharArray(), null);
        Assert.assertNotNull(user);
        Assert.assertEquals("crypt", user.username);
        user = auth.authenticate("md5", "password".toCharArray(), null);
        Assert.assertNotNull(user);
        Assert.assertEquals("md5", user.username);
        user = auth.authenticate("sha", "password".toCharArray(), null);
        Assert.assertNotNull(user);
        Assert.assertEquals("sha", user.username);
        // Test leading and trailing whitespace
        user = auth.authenticate("trailing", "whitespace".toCharArray(), null);
        Assert.assertNotNull(user);
        Assert.assertEquals("trailing", user.username);
        user = auth.authenticate("tabbed", "frontAndBack".toCharArray(), null);
        Assert.assertNotNull(user);
        Assert.assertEquals("tabbed", user.username);
        user = auth.authenticate("leading", "whitespace".toCharArray(), null);
        Assert.assertNotNull(user);
        Assert.assertEquals("leading", user.username);
    }

    @Test
    public void testAttributes() {
        HtpasswdAuthenticationTest.MS.put(HtpasswdAuthenticationTest.KEY_SUPPORT_PLAINTEXT_PWD, "true");
        UserModel user = htpasswd.authenticate("user1", "pass1".toCharArray());
        Assert.assertNotNull(user);
        Assert.assertEquals("El Capitan", user.displayName);
        Assert.assertEquals("cheffe@example.com", user.emailAddress);
        Assert.assertTrue(user.canAdmin);
        user = htpasswd.authenticate("user2", "pass2".toCharArray());
        Assert.assertNotNull(user);
        Assert.assertEquals("User Two", user.displayName);
        Assert.assertTrue(user.canCreate);
        Assert.assertTrue(user.canFork);
    }

    @Test
    public void testAuthenticateDenied() {
        UserModel user = null;
        HtpasswdAuthenticationTest.MS.put(HtpasswdAuthenticationTest.KEY_SUPPORT_PLAINTEXT_PWD, "true");
        user = htpasswd.authenticate("user1", "".toCharArray());
        Assert.assertNull("User 'user1' falsely authenticated.", user);
        user = htpasswd.authenticate("user1", "pass2".toCharArray());
        Assert.assertNull("User 'user1' falsely authenticated.", user);
        user = htpasswd.authenticate("user2", "lalala".toCharArray());
        Assert.assertNull("User 'user2' falsely authenticated.", user);
        user = htpasswd.authenticate("user3", "disabled".toCharArray());
        Assert.assertNull("User 'user3' falsely authenticated.", user);
        user = htpasswd.authenticate("user4", "disabled".toCharArray());
        Assert.assertNull("User 'user4' falsely authenticated.", user);
        user = htpasswd.authenticate("plain", "text".toCharArray());
        Assert.assertNull("User 'plain' falsely authenticated.", user);
        user = htpasswd.authenticate("plain", "password".toCharArray());
        Assert.assertNull("User 'plain' falsely authenticated.", user);
        HtpasswdAuthenticationTest.MS.put(HtpasswdAuthenticationTest.KEY_SUPPORT_PLAINTEXT_PWD, "false");
        user = htpasswd.authenticate("crypt", "".toCharArray());
        Assert.assertNull("User 'cyrpt' falsely authenticated.", user);
        user = htpasswd.authenticate("crypt", "passwd".toCharArray());
        Assert.assertNull("User 'crypt' falsely authenticated.", user);
        user = htpasswd.authenticate("md5", "".toCharArray());
        Assert.assertNull("User 'md5' falsely authenticated.", user);
        user = htpasswd.authenticate("md5", "pwd".toCharArray());
        Assert.assertNull("User 'md5' falsely authenticated.", user);
        user = htpasswd.authenticate("sha", "".toCharArray());
        Assert.assertNull("User 'sha' falsely authenticated.", user);
        user = htpasswd.authenticate("sha", "letmein".toCharArray());
        Assert.assertNull("User 'sha' falsely authenticated.", user);
        user = htpasswd.authenticate("  tabbed", "frontAndBack".toCharArray());
        Assert.assertNull("User 'tabbed' falsely authenticated.", user);
        user = htpasswd.authenticate("    leading", "whitespace".toCharArray());
        Assert.assertNull("User 'leading' falsely authenticated.", user);
    }

    @Test
    public void testAuthenticationMangerDenied() {
        UserModel user = null;
        HtpasswdAuthenticationTest.MS.put(HtpasswdAuthenticationTest.KEY_SUPPORT_PLAINTEXT_PWD, "true");
        user = auth.authenticate("user1", "".toCharArray(), null);
        Assert.assertNull("User 'user1' falsely authenticated.", user);
        user = auth.authenticate("user1", "pass2".toCharArray(), null);
        Assert.assertNull("User 'user1' falsely authenticated.", user);
        user = auth.authenticate("user2", "lalala".toCharArray(), null);
        Assert.assertNull("User 'user2' falsely authenticated.", user);
        user = auth.authenticate("user3", "disabled".toCharArray(), null);
        Assert.assertNull("User 'user3' falsely authenticated.", user);
        user = auth.authenticate("user4", "disabled".toCharArray(), null);
        Assert.assertNull("User 'user4' falsely authenticated.", user);
        user = auth.authenticate("plain", "text".toCharArray(), null);
        Assert.assertNull("User 'plain' falsely authenticated.", user);
        user = auth.authenticate("plain", "password".toCharArray(), null);
        Assert.assertNull("User 'plain' falsely authenticated.", user);
        HtpasswdAuthenticationTest.MS.put(HtpasswdAuthenticationTest.KEY_SUPPORT_PLAINTEXT_PWD, "false");
        user = auth.authenticate("crypt", "".toCharArray(), null);
        Assert.assertNull("User 'cyrpt' falsely authenticated.", user);
        user = auth.authenticate("crypt", "passwd".toCharArray(), null);
        Assert.assertNull("User 'crypt' falsely authenticated.", user);
        user = auth.authenticate("md5", "".toCharArray(), null);
        Assert.assertNull("User 'md5' falsely authenticated.", user);
        user = auth.authenticate("md5", "pwd".toCharArray(), null);
        Assert.assertNull("User 'md5' falsely authenticated.", user);
        user = auth.authenticate("sha", "".toCharArray(), null);
        Assert.assertNull("User 'sha' falsely authenticated.", user);
        user = auth.authenticate("sha", "letmein".toCharArray(), null);
        Assert.assertNull("User 'sha' falsely authenticated.", user);
        user = auth.authenticate("  tabbed", "frontAndBack".toCharArray(), null);
        Assert.assertNull("User 'tabbed' falsely authenticated.", user);
        user = auth.authenticate("    leading", "whitespace".toCharArray(), null);
        Assert.assertNull("User 'leading' falsely authenticated.", user);
    }

    @Test
    public void testCleartextIntrusion() {
        HtpasswdAuthenticationTest.MS.put(HtpasswdAuthenticationTest.KEY_SUPPORT_PLAINTEXT_PWD, "true");
        Assert.assertNull(htpasswd.authenticate("md5", "$apr1$qAGGNfli$sAn14mn.WKId/3EQS7KSX0".toCharArray()));
        Assert.assertNull(htpasswd.authenticate("sha", "{SHA}W6ph5Mm5Pz8GgiULbPgzG37mj9g=".toCharArray()));
        Assert.assertNull(htpasswd.authenticate("user1", "#externalAccount".toCharArray()));
        HtpasswdAuthenticationTest.MS.put(HtpasswdAuthenticationTest.KEY_SUPPORT_PLAINTEXT_PWD, "false");
        Assert.assertNull(htpasswd.authenticate("md5", "$apr1$qAGGNfli$sAn14mn.WKId/3EQS7KSX0".toCharArray()));
        Assert.assertNull(htpasswd.authenticate("sha", "{SHA}W6ph5Mm5Pz8GgiULbPgzG37mj9g=".toCharArray()));
        Assert.assertNull(htpasswd.authenticate("user1", "#externalAccount".toCharArray()));
    }

    @Test
    public void testCryptVsPlaintext() {
        HtpasswdAuthenticationTest.MS.put(HtpasswdAuthenticationTest.KEY_SUPPORT_PLAINTEXT_PWD, "false");
        Assert.assertNull(htpasswd.authenticate("crypt", "6TmlbxqZ2kBIA".toCharArray()));
        Assert.assertNotNull(htpasswd.authenticate("crypt", "password".toCharArray()));
        HtpasswdAuthenticationTest.MS.put(HtpasswdAuthenticationTest.KEY_SUPPORT_PLAINTEXT_PWD, "true");
        Assert.assertNotNull(htpasswd.authenticate("crypt", "6TmlbxqZ2kBIA".toCharArray()));
        Assert.assertNull(htpasswd.authenticate("crypt", "password".toCharArray()));
    }

    @Test
    public void testChangeHtpasswdFile() {
        UserModel user;
        // User default set up.
        user = htpasswd.authenticate("md5", "password".toCharArray());
        Assert.assertNotNull(user);
        Assert.assertEquals("md5", user.username);
        user = htpasswd.authenticate("sha", "password".toCharArray());
        Assert.assertNotNull(user);
        Assert.assertEquals("sha", user.username);
        user = htpasswd.authenticate("blueone", "GoBlue!".toCharArray());
        Assert.assertNull(user);
        user = htpasswd.authenticate("bluetwo", "YayBlue!".toCharArray());
        Assert.assertNull(user);
        // Switch to different htpasswd file.
        getSettings(((HtpasswdAuthenticationTest.RESOURCE_DIR) + "htpasswd-user"), null, null);
        user = htpasswd.authenticate("md5", "password".toCharArray());
        Assert.assertNull(user);
        user = htpasswd.authenticate("sha", "password".toCharArray());
        Assert.assertNull(user);
        user = htpasswd.authenticate("blueone", "GoBlue!".toCharArray());
        Assert.assertNotNull(user);
        Assert.assertEquals("blueone", user.username);
        user = htpasswd.authenticate("bluetwo", "YayBlue!".toCharArray());
        Assert.assertNotNull(user);
        Assert.assertEquals("bluetwo", user.username);
    }

    @Test
    public void testChangeHtpasswdFileNotExisting() {
        UserModel user;
        // User default set up.
        user = htpasswd.authenticate("md5", "password".toCharArray());
        Assert.assertNotNull(user);
        Assert.assertEquals("md5", user.username);
        user = htpasswd.authenticate("sha", "password".toCharArray());
        Assert.assertNotNull(user);
        Assert.assertEquals("sha", user.username);
        user = htpasswd.authenticate("blueone", "GoBlue!".toCharArray());
        Assert.assertNull(user);
        user = htpasswd.authenticate("bluetwo", "YayBlue!".toCharArray());
        Assert.assertNull(user);
        // Switch to different htpasswd file that doesn't exist.
        // Currently we stop working with old users upon this change.
        getSettings(((HtpasswdAuthenticationTest.RESOURCE_DIR) + "no-such-file"), null, null);
        user = htpasswd.authenticate("md5", "password".toCharArray());
        Assert.assertNull(user);
        user = htpasswd.authenticate("sha", "password".toCharArray());
        Assert.assertNull(user);
        user = htpasswd.authenticate("blueone", "GoBlue!".toCharArray());
        Assert.assertNull(user);
        user = htpasswd.authenticate("bluetwo", "YayBlue!".toCharArray());
        Assert.assertNull(user);
    }
}

