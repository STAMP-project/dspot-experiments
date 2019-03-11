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
package org.apache.shardingsphere.shardingproxy.transport.mysql.packet.command.query.binary.execute.protocol;


import io.netty.buffer.ByteBuf;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class MySQLDoubleBinaryProtocolValueTest {
    @Mock
    private ByteBuf byteBuf;

    @Test
    public void assertRead() {
        Mockito.when(byteBuf.readDoubleLE()).thenReturn(1.0);
        Assert.assertThat(new MySQLDoubleBinaryProtocolValue().read(new org.apache.shardingsphere.shardingproxy.transport.mysql.payload.MySQLPacketPayload(byteBuf)), CoreMatchers.<Object>is(1.0));
    }

    @Test
    public void assertWrite() {
        new MySQLDoubleBinaryProtocolValue().write(new org.apache.shardingsphere.shardingproxy.transport.mysql.payload.MySQLPacketPayload(byteBuf), 1.0);
        Mockito.verify(byteBuf).writeDoubleLE(1.0);
    }
}

