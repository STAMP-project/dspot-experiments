/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import bolts.Task;
import java.util.HashMap;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;


public class ParseAuthenticationManagerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ParseAuthenticationManager manager;

    private ParseCurrentUserController controller;

    private AuthenticationCallback provider;

    // region testRegister
    @Test
    public void testRegisterMultipleShouldThrow() {
        Mockito.when(controller.getAsync(false)).thenReturn(Task.<ParseUser>forResult(null));
        AuthenticationCallback provider2 = Mockito.mock(AuthenticationCallback.class);
        manager.register("test_provider", provider);
        thrown.expect(IllegalStateException.class);
        manager.register("test_provider", provider2);
    }

    @Test
    public void testRegisterAnonymous() {
        manager.register("anonymous", Mockito.mock(AuthenticationCallback.class));
        Mockito.verifyNoMoreInteractions(controller);
    }

    @Test
    public void testRegister() {
        ParseUser user = Mockito.mock(ParseUser.class);
        Mockito.when(controller.getAsync(false)).thenReturn(Task.forResult(user));
        manager.register("test_provider", provider);
        Mockito.verify(controller).getAsync(false);
        Mockito.verify(user).synchronizeAuthDataAsync("test_provider");
    }

    // endregion
    @Test
    public void testRestoreAuthentication() throws ParseException {
        Mockito.when(controller.getAsync(false)).thenReturn(Task.<ParseUser>forResult(null));
        Mockito.when(provider.onRestore(Matchers.<Map<String, String>>any())).thenReturn(true);
        manager.register("test_provider", provider);
        Map<String, String> authData = new HashMap<>();
        ParseTaskUtils.wait(manager.restoreAuthenticationAsync("test_provider", authData));
        Mockito.verify(provider).onRestore(authData);
    }

    @Test
    public void testDeauthenticateAsync() throws ParseException {
        Mockito.when(controller.getAsync(false)).thenReturn(Task.<ParseUser>forResult(null));
        manager.register("test_provider", provider);
        ParseTaskUtils.wait(manager.deauthenticateAsync("test_provider"));
        Mockito.verify(provider).onRestore(null);
    }
}

