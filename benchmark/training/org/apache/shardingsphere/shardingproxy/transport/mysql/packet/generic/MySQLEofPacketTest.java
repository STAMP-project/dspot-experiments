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
package org.apache.shardingsphere.shardingproxy.transport.mysql.packet.generic;


import MySQLEofPacket.HEADER;
import MySQLStatusFlag.SERVER_STATUS_AUTOCOMMIT;
import org.apache.shardingsphere.shardingproxy.transport.mysql.payload.MySQLPacketPayload;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class MySQLEofPacketTest {
    @Mock
    private MySQLPacketPayload payload;

    @Test
    public void assertNewEofPacketWithSequenceId() {
        MySQLEofPacket actual = new MySQLEofPacket(1);
        Assert.assertThat(actual.getSequenceId(), CoreMatchers.is(1));
        Assert.assertThat(actual.getWarnings(), CoreMatchers.is(0));
        Assert.assertThat(actual.getStatusFlags(), CoreMatchers.is(SERVER_STATUS_AUTOCOMMIT.getValue()));
    }

    @Test
    public void assertWrite() {
        new MySQLEofPacket(1).write(payload);
        Mockito.verify(payload).writeInt1(HEADER);
        Mockito.verify(payload).writeInt2(0);
        Mockito.verify(payload).writeInt2(SERVER_STATUS_AUTOCOMMIT.getValue());
    }
}

