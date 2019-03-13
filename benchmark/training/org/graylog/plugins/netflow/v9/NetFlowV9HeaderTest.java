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
package org.graylog.plugins.netflow.v9;


import org.junit.Test;


public class NetFlowV9HeaderTest {
    @Test
    public void prettyHexDump() {
        final NetFlowV9Header header = NetFlowV9Header.create(5, 23, 42L, 1000L, 1L, 1L);
        assertThat(header.prettyHexDump()).isNotEmpty();
    }
}

