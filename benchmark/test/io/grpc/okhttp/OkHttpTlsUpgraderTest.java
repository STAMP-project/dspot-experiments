/**
 * Copyright 2016 The gRPC Authors
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
package io.grpc.okhttp;


import OkHttpTlsUpgrader.TLS_PROTOCOLS;
import Protocol.GRPC_EXP;
import Protocol.HTTP_2;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link io.grpc.okhttp.OkHttpTlsUpgrader}.
 */
@RunWith(JUnit4.class)
public class OkHttpTlsUpgraderTest {
    @Test
    public void upgrade_grpcExp() {
        Assert.assertTrue((((TLS_PROTOCOLS.indexOf(GRPC_EXP)) == (-1)) || ((TLS_PROTOCOLS.indexOf(GRPC_EXP)) < (TLS_PROTOCOLS.indexOf(HTTP_2)))));
    }

    @Test
    public void canonicalizeHosts() {
        Assert.assertEquals("::1", OkHttpTlsUpgrader.canonicalizeHost("::1"));
        Assert.assertEquals("::1", OkHttpTlsUpgrader.canonicalizeHost("[::1]"));
        Assert.assertEquals("127.0.0.1", OkHttpTlsUpgrader.canonicalizeHost("127.0.0.1"));
        Assert.assertEquals("some.long.url.com", OkHttpTlsUpgrader.canonicalizeHost("some.long.url.com"));
        // Extra square brackets in a malformed URI are retained
        Assert.assertEquals("[::1]", OkHttpTlsUpgrader.canonicalizeHost("[[::1]]"));
    }
}

