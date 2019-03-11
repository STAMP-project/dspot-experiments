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
package org.graylog2.inputs.transports;


import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class HttpPollTransportTest {
    @Test
    public void testParseHeaders() throws Exception {
        Assert.assertEquals(0, HttpPollTransport.parseHeaders("").size());
        Assert.assertEquals(0, HttpPollTransport.parseHeaders(" ").size());
        Assert.assertEquals(0, HttpPollTransport.parseHeaders(" . ").size());
        Assert.assertEquals(0, HttpPollTransport.parseHeaders("foo").size());
        Assert.assertEquals(1, HttpPollTransport.parseHeaders("X-Foo: Bar").size());
        Map<String, String> expectedSingle = ImmutableMap.of("Accept", "application/json");
        Map<String, String> expectedMulti = ImmutableMap.of("Accept", "application/json", "X-Foo", "bar");
        Assert.assertEquals(expectedMulti, HttpPollTransport.parseHeaders("Accept: application/json, X-Foo: bar"));
        Assert.assertEquals(expectedSingle, HttpPollTransport.parseHeaders("Accept: application/json"));
        Assert.assertEquals(expectedMulti, HttpPollTransport.parseHeaders(" Accept:  application/json,X-Foo:bar"));
        Assert.assertEquals(expectedMulti, HttpPollTransport.parseHeaders("Accept:application/json,   X-Foo: bar "));
        Assert.assertEquals(expectedMulti, HttpPollTransport.parseHeaders("Accept:    application/json,     X-Foo: bar"));
        Assert.assertEquals(expectedMulti, HttpPollTransport.parseHeaders("Accept :application/json,   X-Foo: bar "));
        Assert.assertEquals(expectedSingle, HttpPollTransport.parseHeaders(" Accept: application/json"));
        Assert.assertEquals(expectedSingle, HttpPollTransport.parseHeaders("Accept:application/json"));
        Assert.assertEquals(expectedSingle, HttpPollTransport.parseHeaders(" Accept: application/json "));
        Assert.assertEquals(expectedSingle, HttpPollTransport.parseHeaders(" Accept :application/json "));
    }
}

