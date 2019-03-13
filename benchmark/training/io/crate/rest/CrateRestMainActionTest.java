/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.rest;


import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.settings.SecureString;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CrateRestMainActionTest {
    @Test
    public void testIsBrowser() {
        Assert.assertThat(CrateRestMainAction.isBrowser(null), Matchers.is(false));
        Assert.assertThat(CrateRestMainAction.isBrowser("some header"), Matchers.is(false));
        Assert.assertThat(CrateRestMainAction.isBrowser("Mozilla/5.0 (X11; Linux x86_64)"), Matchers.is(true));
    }

    @Test
    public void testIsAcceptJson() {
        Assert.assertThat(CrateRestMainAction.isAcceptJson(null), Matchers.is(false));
        Assert.assertThat(CrateRestMainAction.isAcceptJson("text/html"), Matchers.is(false));
        Assert.assertThat(CrateRestMainAction.isAcceptJson("application/json"), Matchers.is(true));
    }

    @Test
    public void testExtractUsernamePasswordFromHttpBasicAuthHeader() {
        Tuple<String, SecureString> creds = CrateRestMainAction.extractCredentialsFromHttpBasicAuthHeader("");
        Assert.assertThat(creds.v1(), Matchers.is(""));
        Assert.assertThat(creds.v2().toString(), Matchers.is(""));
        creds = CrateRestMainAction.extractCredentialsFromHttpBasicAuthHeader(null);
        Assert.assertThat(creds.v1(), Matchers.is(""));
        Assert.assertThat(creds.v2().toString(), Matchers.is(""));
        creds = CrateRestMainAction.extractCredentialsFromHttpBasicAuthHeader("Basic QXJ0aHVyOkV4Y2FsaWJ1cg==");
        Assert.assertThat(creds.v1(), Matchers.is("Arthur"));
        Assert.assertThat(creds.v2().toString(), Matchers.is("Excalibur"));
        creds = CrateRestMainAction.extractCredentialsFromHttpBasicAuthHeader("Basic QXJ0aHVyOjp0ZXN0OnBhc3N3b3JkOg==");
        Assert.assertThat(creds.v1(), Matchers.is("Arthur"));
        Assert.assertThat(creds.v2().toString(), Matchers.is(":test:password:"));
        creds = CrateRestMainAction.extractCredentialsFromHttpBasicAuthHeader("Basic QXJ0aHVyOg==");
        Assert.assertThat(creds.v1(), Matchers.is("Arthur"));
        Assert.assertThat(creds.v2().toString(), Matchers.is(""));
        creds = CrateRestMainAction.extractCredentialsFromHttpBasicAuthHeader("Basic OnBhc3N3b3Jk");
        Assert.assertThat(creds.v1(), Matchers.is(""));
        Assert.assertThat(creds.v2().toString(), Matchers.is("password"));
    }
}

