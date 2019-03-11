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


import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Timestamp;
import java.util.Calendar;
import org.apache.shardingsphere.shardingproxy.transport.mysql.payload.MySQLPacketPayload;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class MySQLDateBinaryProtocolValueTest {
    @Mock
    private MySQLPacketPayload payload;

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void assertReadWithZeroByte() throws SQLException {
        new MySQLDateBinaryProtocolValue().read(payload);
    }

    @Test
    public void assertReadWithFourBytes() throws SQLException {
        Mockito.when(payload.readInt1()).thenReturn(4, 12, 31);
        Mockito.when(payload.readInt2()).thenReturn(2018);
        Calendar actual = Calendar.getInstance();
        actual.setTimeInMillis(((Timestamp) (new MySQLDateBinaryProtocolValue().read(payload))).getTime());
        Assert.assertThat(actual.get(Calendar.YEAR), CoreMatchers.is(2018));
        Assert.assertThat(actual.get(Calendar.MONTH), CoreMatchers.is(Calendar.DECEMBER));
        Assert.assertThat(actual.get(Calendar.DAY_OF_MONTH), CoreMatchers.is(31));
    }

    @Test
    public void assertReadWithSevenBytes() throws SQLException {
        Mockito.when(payload.readInt1()).thenReturn(7, 12, 31, 10, 59, 0);
        Mockito.when(payload.readInt2()).thenReturn(2018);
        Calendar actual = Calendar.getInstance();
        actual.setTimeInMillis(((Timestamp) (new MySQLDateBinaryProtocolValue().read(payload))).getTime());
        Assert.assertThat(actual.get(Calendar.YEAR), CoreMatchers.is(2018));
        Assert.assertThat(actual.get(Calendar.MONTH), CoreMatchers.is(Calendar.DECEMBER));
        Assert.assertThat(actual.get(Calendar.DAY_OF_MONTH), CoreMatchers.is(31));
        Assert.assertThat(actual.get(Calendar.HOUR_OF_DAY), CoreMatchers.is(10));
        Assert.assertThat(actual.get(Calendar.MINUTE), CoreMatchers.is(59));
        Assert.assertThat(actual.get(Calendar.SECOND), CoreMatchers.is(0));
    }

    @Test
    public void assertReadWithElevenBytes() throws SQLException {
        Mockito.when(payload.readInt1()).thenReturn(11, 12, 31, 10, 59, 0);
        Mockito.when(payload.readInt2()).thenReturn(2018);
        Mockito.when(payload.readInt4()).thenReturn(500);
        Calendar actual = Calendar.getInstance();
        actual.setTimeInMillis(((Timestamp) (new MySQLDateBinaryProtocolValue().read(payload))).getTime());
        Assert.assertThat(actual.get(Calendar.YEAR), CoreMatchers.is(2018));
        Assert.assertThat(actual.get(Calendar.MONTH), CoreMatchers.is(Calendar.DECEMBER));
        Assert.assertThat(actual.get(Calendar.DAY_OF_MONTH), CoreMatchers.is(31));
        Assert.assertThat(actual.get(Calendar.HOUR_OF_DAY), CoreMatchers.is(10));
        Assert.assertThat(actual.get(Calendar.MINUTE), CoreMatchers.is(59));
        Assert.assertThat(actual.get(Calendar.SECOND), CoreMatchers.is(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertReadWithIllegalArgument() throws SQLException {
        Mockito.when(payload.readInt1()).thenReturn(100);
        new MySQLDateBinaryProtocolValue().read(payload);
    }
}

