/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.shardingproxy.transport.mysql.packet.handshake;


import com.google.common.primitives.Bytes;
import org.apache.shardingsphere.core.rule.Authentication;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class MySQLAuthenticationHandlerTest {
    private final MySQLAuthenticationHandler authenticationHandler = new MySQLAuthenticationHandler();

    private final byte[] part1 = new byte[]{ 84, 85, 115, 77, 68, 116, 85, 78 };

    private final byte[] part2 = new byte[]{ 83, 121, 75, 81, 87, 56, 120, 112, 73, 109, 77, 69 };

    @Test
    public void assertLoginWithPassword() {
        setAuthentication(new Authentication("root", "root"));
        byte[] authResponse = new byte[]{ -27, 89, -20, -27, 65, -120, -64, -101, 86, -100, -108, -100, 6, -125, -37, 117, 14, -43, 95, -113 };
        Assert.assertTrue(authenticationHandler.login("root", authResponse));
    }

    @Test
    public void assertLoginWithoutPassword() {
        setAuthentication(new Authentication("root", null));
        byte[] authResponse = new byte[]{ -27, 89, -20, -27, 65, -120, -64, -101, 86, -100, -108, -100, 6, -125, -37, 117, 14, -43, 95, -113 };
        Assert.assertTrue(authenticationHandler.login("root", authResponse));
    }

    @Test
    public void assertGetAuthPluginData() {
        Assert.assertThat(authenticationHandler.getAuthPluginData().getAuthPluginData(), CoreMatchers.is(Bytes.concat(part1, part2)));
    }
}

