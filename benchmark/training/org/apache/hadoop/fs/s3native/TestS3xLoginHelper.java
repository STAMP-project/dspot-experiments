/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.s3native;


import S3xLoginHelper.Login;
import java.net.URI;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test how URIs and login details are extracted from URIs.
 */
public class TestS3xLoginHelper extends Assert {
    public static final String BUCKET = "s3a://bucket";

    private static final URI ENDPOINT = TestS3xLoginHelper.uri(TestS3xLoginHelper.BUCKET);

    public static final String S = "%2f";

    public static final String P = "%2b";

    public static final String P_RAW = "+";

    public static final String USER = "user";

    public static final String PASLASHSLASH = ("pa" + (TestS3xLoginHelper.S)) + (TestS3xLoginHelper.S);

    public static final String PAPLUS = "pa" + (TestS3xLoginHelper.P);

    public static final String PAPLUS_RAW = "pa" + (TestS3xLoginHelper.P_RAW);

    public static final URI WITH_USER_AND_PASS = TestS3xLoginHelper.uri("s3a://user:pass@bucket");

    public static final URI WITH_SLASH_IN_PASS = TestS3xLoginHelper.uri((("s3a://user:" + (TestS3xLoginHelper.PASLASHSLASH)) + "@bucket"));

    public static final URI WITH_PLUS_IN_PASS = TestS3xLoginHelper.uri((("s3a://user:" + (TestS3xLoginHelper.PAPLUS)) + "@bucket"));

    public static final URI WITH_PLUS_RAW_IN_PASS = TestS3xLoginHelper.uri((("s3a://user:" + (TestS3xLoginHelper.PAPLUS_RAW)) + "@bucket"));

    public static final URI USER_NO_PASS = TestS3xLoginHelper.uri("s3a://user@bucket");

    public static final URI WITH_USER_AND_COLON = TestS3xLoginHelper.uri("s3a://user:@bucket");

    public static final URI NO_USER = TestS3xLoginHelper.uri("s3a://:pass@bucket");

    public static final URI NO_USER_NO_PASS = TestS3xLoginHelper.uri("s3a://:@bucket");

    public static final URI NO_USER_NO_PASS_TWO_COLON = TestS3xLoginHelper.uri("s3a://::@bucket");

    @Test
    public void testSimpleFSURI() throws Throwable {
        assertMatchesEndpoint(TestS3xLoginHelper.ENDPOINT);
    }

    @Test
    public void testLoginSimple() throws Throwable {
        S3xLoginHelper.Login login = assertMatchesLogin("", "", TestS3xLoginHelper.ENDPOINT);
        Assert.assertFalse(("Login of " + login), login.hasLogin());
    }

    @Test
    public void testLoginWithUser() throws Throwable {
        assertMatchesLogin(TestS3xLoginHelper.USER, "", TestS3xLoginHelper.USER_NO_PASS);
    }

    @Test
    public void testLoginWithUserAndColon() throws Throwable {
        assertMatchesLogin(TestS3xLoginHelper.USER, "", TestS3xLoginHelper.WITH_USER_AND_COLON);
    }

    @Test
    public void testLoginNoUser() throws Throwable {
        assertMatchesLogin("", "", TestS3xLoginHelper.NO_USER);
    }

    @Test
    public void testLoginNoUserNoPass() throws Throwable {
        assertMatchesLogin("", "", TestS3xLoginHelper.NO_USER_NO_PASS);
    }

    @Test
    public void testLoginNoUserNoPassTwoColon() throws Throwable {
        assertMatchesLogin("", "", TestS3xLoginHelper.NO_USER_NO_PASS_TWO_COLON);
    }

    @Test
    public void testFsUriWithUserAndPass() throws Throwable {
        assertInvalid(TestS3xLoginHelper.WITH_USER_AND_PASS);
    }

    @Test
    public void testFsUriWithSlashInPass() throws Throwable {
        assertInvalid(TestS3xLoginHelper.WITH_SLASH_IN_PASS);
    }

    @Test
    public void testFsUriWithPlusInPass() throws Throwable {
        assertInvalid(TestS3xLoginHelper.WITH_PLUS_IN_PASS);
    }

    @Test
    public void testFsUriWithPlusRawInPass() throws Throwable {
        assertInvalid(TestS3xLoginHelper.WITH_PLUS_RAW_IN_PASS);
    }

    @Test
    public void testFsUriWithUser() throws Throwable {
        assertInvalid(TestS3xLoginHelper.USER_NO_PASS);
    }

    @Test
    public void testFsUriWithUserAndColon() throws Throwable {
        assertInvalid(TestS3xLoginHelper.WITH_USER_AND_COLON);
    }

    @Test
    public void testFsiNoUser() throws Throwable {
        assertMatchesEndpoint(TestS3xLoginHelper.NO_USER);
    }

    @Test
    public void testFsUriNoUserNoPass() throws Throwable {
        assertMatchesEndpoint(TestS3xLoginHelper.NO_USER_NO_PASS);
    }

    @Test
    public void testFsUriNoUserNoPassTwoColon() throws Throwable {
        assertMatchesEndpoint(TestS3xLoginHelper.NO_USER_NO_PASS_TWO_COLON);
    }
}

