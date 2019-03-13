/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import JSONCompareMode.NON_EXTENSIBLE;
import ParseHttpRequest.Builder;
import ParseHttpRequest.Method.GET;
import ParseHttpRequest.Method.POST;
import com.parse.http.ParseHttpRequest;
import com.parse.http.ParseHttpResponse;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


// endregion
public class ParseRESTUserCommandTest {
    // region testConstruct
    @Test
    public void testGetCurrentUserCommand() {
        ParseRESTUserCommand command = ParseRESTUserCommand.getCurrentUserCommand("sessionToken");
        Assert.assertEquals("users/me", command.httpPath);
        Assert.assertEquals(GET, command.method);
        Assert.assertNull(command.jsonParameters);
        Assert.assertEquals("sessionToken", command.getSessionToken());
        // TODO(mengyan): Find a way to verify revocableSession
    }

    @Test
    public void testLogInUserCommand() throws Exception {
        ParseRESTUserCommand command = ParseRESTUserCommand.logInUserCommand("userName", "password", true);
        Assert.assertEquals("login", command.httpPath);
        Assert.assertEquals(GET, command.method);
        Assert.assertEquals("userName", command.jsonParameters.getString("username"));
        Assert.assertEquals("password", command.jsonParameters.getString("password"));
        Assert.assertNull(command.getSessionToken());
        // TODO(mengyan): Find a way to verify revocableSession
    }

    @Test
    public void testResetPasswordResetCommand() throws Exception {
        ParseRESTUserCommand command = ParseRESTUserCommand.resetPasswordResetCommand("test@parse.com");
        Assert.assertEquals("requestPasswordReset", command.httpPath);
        Assert.assertEquals(POST, command.method);
        Assert.assertEquals("test@parse.com", command.jsonParameters.getString("email"));
        Assert.assertNull(command.getSessionToken());
        // TODO(mengyan): Find a way to verify revocableSession
    }

    @Test
    public void testSignUpUserCommand() throws Exception {
        JSONObject parameters = new JSONObject();
        parameters.put("key", "value");
        ParseRESTUserCommand command = ParseRESTUserCommand.signUpUserCommand(parameters, "sessionToken", true);
        Assert.assertEquals("users", command.httpPath);
        Assert.assertEquals(POST, command.method);
        Assert.assertEquals("value", command.jsonParameters.getString("key"));
        Assert.assertEquals("sessionToken", command.getSessionToken());
        // TODO(mengyan): Find a way to verify revocableSession
    }

    @Test
    public void testServiceLogInUserCommandWithParameters() throws Exception {
        JSONObject parameters = new JSONObject();
        parameters.put("key", "value");
        ParseRESTUserCommand command = ParseRESTUserCommand.serviceLogInUserCommand(parameters, "sessionToken", true);
        Assert.assertEquals("users", command.httpPath);
        Assert.assertEquals(POST, command.method);
        Assert.assertEquals("value", command.jsonParameters.getString("key"));
        Assert.assertEquals("sessionToken", command.getSessionToken());
        // TODO(mengyan): Find a way to verify revocableSession
    }

    @Test
    public void testServiceLogInUserCommandWithAuthType() throws Exception {
        Map<String, String> facebookAuthData = new HashMap<>();
        facebookAuthData.put("token", "test");
        ParseRESTUserCommand command = ParseRESTUserCommand.serviceLogInUserCommand("facebook", facebookAuthData, true);
        Assert.assertEquals("users", command.httpPath);
        Assert.assertEquals(POST, command.method);
        Assert.assertNull(command.getSessionToken());
        JSONObject authenticationData = new JSONObject();
        authenticationData.put("facebook", PointerEncoder.get().encode(facebookAuthData));
        JSONObject parameters = new JSONObject();
        parameters.put("authData", authenticationData);
        Assert.assertEquals(parameters, command.jsonParameters, NON_EXTENSIBLE);
        // TODO(mengyan): Find a way to verify revocableSession
    }

    // endregion
    // region testAddAdditionalHeaders
    @Test
    public void testAddAdditionalHeaders() throws Exception {
        JSONObject parameters = new JSONObject();
        parameters.put("key", "value");
        ParseRESTUserCommand command = ParseRESTUserCommand.signUpUserCommand(parameters, "sessionToken", true);
        ParseHttpRequest.Builder requestBuilder = new ParseHttpRequest.Builder();
        command.addAdditionalHeaders(requestBuilder);
        Assert.assertEquals("1", requestBuilder.build().getHeader("X-Parse-Revocable-Session"));
    }

    // endregion
    // region testOnResponseAsync
    @Test
    public void testOnResponseAsync() {
        ParseRESTUserCommand command = ParseRESTUserCommand.getCurrentUserCommand("sessionToken");
        String content = "content";
        String contentType = "application/json";
        int statusCode = 200;
        ParseHttpResponse response = new ParseHttpResponse.Builder().setContent(new ByteArrayInputStream(content.getBytes())).setContentType(contentType).setStatusCode(statusCode).build();
        command.onResponseAsync(response, null);
        Assert.assertEquals(200, command.getStatusCode());
    }
}

