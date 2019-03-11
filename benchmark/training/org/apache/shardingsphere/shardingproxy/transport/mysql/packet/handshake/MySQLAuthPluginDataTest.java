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
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class MySQLAuthPluginDataTest {
    @Test
    public void assertGetAuthPluginData() {
        byte[] actualPart1 = new byte[]{ 106, 105, 55, 122, 117, 98, 115, 109 };
        byte[] actualPart2 = new byte[]{ 68, 102, 53, 122, 65, 49, 84, 79, 85, 115, 116, 113 };
        MySQLAuthPluginData actual = new MySQLAuthPluginData(actualPart1, actualPart2);
        Assert.assertThat(actual.getAuthPluginDataPart1(), CoreMatchers.is(actualPart1));
        Assert.assertThat(actual.getAuthPluginDataPart2(), CoreMatchers.is(actualPart2));
        Assert.assertThat(actual.getAuthPluginData(), CoreMatchers.is(Bytes.concat(actualPart1, actualPart2)));
    }

    @Test
    public void assertGetAuthPluginDataWithoutArguments() {
        MySQLAuthPluginData actual = new MySQLAuthPluginData();
        Assert.assertThat(actual.getAuthPluginDataPart1().length, CoreMatchers.is(8));
        Assert.assertThat(actual.getAuthPluginDataPart2().length, CoreMatchers.is(12));
        Assert.assertThat(actual.getAuthPluginData().length, CoreMatchers.is(20));
    }
}

