/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog.plugins.netflow.utils;


import Protocol.TCP;
import Protocol.VRRP;
import org.junit.Assert;
import org.junit.Test;

import static Protocol.TCP;


public class ProtocolTest {
    @Test
    public void test() throws Exception {
        final Protocol tcp = TCP;
        Assert.assertEquals("tcp", tcp.getName());
        Assert.assertEquals(6, tcp.getNumber());
        Assert.assertEquals("TCP", tcp.getAlias());
    }

    @Test
    public void testGetByNumber() throws Exception {
        Assert.assertEquals(TCP, Protocol.getByNumber(6));
        Assert.assertEquals(VRRP, Protocol.getByNumber(112));
    }

    @Test
    public void testNull() throws Exception {
        Assert.assertNull(Protocol.getByNumber(1231323424));
    }
}

