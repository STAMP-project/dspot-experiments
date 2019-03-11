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


import MySQLOKPacket.HEADER;
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
public final class MySQLOKPacketTest {
    @Mock
    private MySQLPacketPayload packetPayload;

    @Test
    public void assertNewOKPacketWithSequenceId() {
        MySQLOKPacket actual = new MySQLOKPacket(1);
        Assert.assertThat(actual.getSequenceId(), CoreMatchers.is(1));
        Assert.assertThat(actual.getAffectedRows(), CoreMatchers.is(0L));
        Assert.assertThat(actual.getLastInsertId(), CoreMatchers.is(0L));
        Assert.assertThat(actual.getWarnings(), CoreMatchers.is(0));
        Assert.assertThat(actual.getInfo(), CoreMatchers.is(""));
    }

    @Test
    public void assertNewOKPacketWithAffectedRowsAndLastInsertId() {
        MySQLOKPacket actual = new MySQLOKPacket(1, 100L, 9999L);
        Assert.assertThat(actual.getSequenceId(), CoreMatchers.is(1));
        Assert.assertThat(actual.getAffectedRows(), CoreMatchers.is(100L));
        Assert.assertThat(actual.getLastInsertId(), CoreMatchers.is(9999L));
        Assert.assertThat(actual.getWarnings(), CoreMatchers.is(0));
        Assert.assertThat(actual.getInfo(), CoreMatchers.is(""));
    }

    @Test
    public void assertWrite() {
        new MySQLOKPacket(1, 100L, 9999L).write(packetPayload);
        Mockito.verify(packetPayload).writeInt1(HEADER);
        Mockito.verify(packetPayload).writeIntLenenc(100L);
        Mockito.verify(packetPayload).writeIntLenenc(9999L);
        Mockito.verify(packetPayload).writeInt2(SERVER_STATUS_AUTOCOMMIT.getValue());
        Mockito.verify(packetPayload).writeInt2(0);
        Mockito.verify(packetPayload).writeStringEOF("");
    }
}

