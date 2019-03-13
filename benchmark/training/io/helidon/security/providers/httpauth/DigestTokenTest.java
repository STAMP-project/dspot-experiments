/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.security.providers.httpauth;


import HttpDigest.Algorithm.MD5;
import HttpDigest.Qop.AUTH;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


/**
 * Unit test for {@link DigestToken}.
 */
public class DigestTokenTest {
    @Test
    public void rfcExampleTest() {
        String header = "username=\"Mufasa\", realm=\"testrealm@host.com\", nonce=\"dcd98b7102dd2f0e8b11d0f600bfb0c093\", " + ("uri=\"/dir/index.html\", qop=auth, nc=00000001, cnonce=\"0a4f113b\", " + "response=\"6629fae49393a05397450978507c4ef1\", opaque=\"5ccc069c403ebaf9f0171e9517f40e41\"");
        String method = "GET";
        DigestToken dt = DigestToken.fromAuthorizationHeader(header, method);
        char[] password = "Circle Of Life".toCharArray();
        MatcherAssert.assertThat(dt.getUsername(), CoreMatchers.is("Mufasa"));
        MatcherAssert.assertThat(dt.getRealm(), CoreMatchers.is("testrealm@host.com"));
        MatcherAssert.assertThat(dt.getUri(), CoreMatchers.is("/dir/index.html"));
        MatcherAssert.assertThat(dt.getAlgorithm(), CoreMatchers.is(MD5));
        MatcherAssert.assertThat(dt.getResponse(), CoreMatchers.is("6629fae49393a05397450978507c4ef1"));
        MatcherAssert.assertThat(dt.getOpaque(), CoreMatchers.is("5ccc069c403ebaf9f0171e9517f40e41"));
        MatcherAssert.assertThat(dt.getQop(), CoreMatchers.is(AUTH));
        MatcherAssert.assertThat(dt.getNc(), CoreMatchers.is("00000001"));
        MatcherAssert.assertThat(dt.getCnonce(), CoreMatchers.is("0a4f113b"));
        MatcherAssert.assertThat(dt.getMethod(), CoreMatchers.is(method));
        MatcherAssert.assertThat(dt.getNonce(), CoreMatchers.is("dcd98b7102dd2f0e8b11d0f600bfb0c093"));
        MatcherAssert.assertThat(dt.digest(password), CoreMatchers.is("6629fae49393a05397450978507c4ef1"));
        MatcherAssert.assertThat(dt.validateLogin(password), CoreMatchers.is(true));
    }

    @Test
    public void chromeBrowserTest() {
        // this is an actual request from a browser (Chrome) generated for a nonce. The timestamp is obviously invalid,
        // fortunatelly the DigestToken does not validate timestamp...
        String header = "username=\"jack\", realm=\"mic\", nonce=\"ADm0cNeFcVZBE4el5LHrXD1VGvw3f7XgFsQEk0sLa2A=\", " + ("uri=\"/digest\", algorithm=MD5, response=\"943217664b97f16ddbde11c44f0ee980\", " + "opaque=\"y52cjfKNa+X2UC05MXnlT+5icnkunfsFBpY3n9E0k+M=\", qop=auth, nc=00000001, cnonce=\"4f7353deb0f2d452\"");
        String method = "GET";
        // service password
        DigestToken dt = DigestToken.fromAuthorizationHeader(header, method);
        MatcherAssert.assertThat(dt.getUsername(), CoreMatchers.is("jack"));
        MatcherAssert.assertThat(dt.getRealm(), CoreMatchers.is("mic"));
        MatcherAssert.assertThat(dt.getUri(), CoreMatchers.is("/digest"));
        MatcherAssert.assertThat(dt.getAlgorithm(), CoreMatchers.is(MD5));
        MatcherAssert.assertThat(dt.getResponse(), CoreMatchers.is("943217664b97f16ddbde11c44f0ee980"));
        MatcherAssert.assertThat(dt.getOpaque(), CoreMatchers.is("y52cjfKNa+X2UC05MXnlT+5icnkunfsFBpY3n9E0k+M="));
        MatcherAssert.assertThat(dt.getQop(), CoreMatchers.is(AUTH));
        MatcherAssert.assertThat(dt.getNc(), CoreMatchers.is("00000001"));
        MatcherAssert.assertThat(dt.getCnonce(), CoreMatchers.is("4f7353deb0f2d452"));
        MatcherAssert.assertThat(dt.getMethod(), CoreMatchers.is(method));
        MatcherAssert.assertThat(dt.getNonce(), CoreMatchers.is("ADm0cNeFcVZBE4el5LHrXD1VGvw3f7XgFsQEk0sLa2A="));
        // user's password
        MatcherAssert.assertThat(dt.digest("kleslo".toCharArray()), CoreMatchers.is("943217664b97f16ddbde11c44f0ee980"));
        MatcherAssert.assertThat(dt.validateLogin("kleslo".toCharArray()), CoreMatchers.is(true));
        MatcherAssert.assertThat(dt.validateLogin("other".toCharArray()), CoreMatchers.is(false));
    }
}

