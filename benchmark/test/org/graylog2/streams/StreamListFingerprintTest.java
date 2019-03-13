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
package org.graylog2.streams;


import com.google.common.collect.Lists;
import org.graylog2.plugin.streams.Output;
import org.graylog2.plugin.streams.Stream;
import org.graylog2.plugin.streams.StreamRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class StreamListFingerprintTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Stream stream1;

    @Mock
    Stream stream2;

    @Mock
    StreamRule streamRule1;

    @Mock
    StreamRule streamRule2;

    @Mock
    StreamRule streamRule3;

    @Mock
    Output output1;

    @Mock
    Output output2;

    private final String expectedEmptyFingerprint = "da39a3ee5e6b4b0d3255bfef95601890afd80709";

    @Test
    public void testGetFingerprint() throws Exception {
        final StreamListFingerprint fingerprint = new StreamListFingerprint(Lists.newArrayList(stream1, stream2));
        // The fingerprint depends on the hashCode of each stream and stream rule and might change if the underlying
        // implementation changed.
        Assert.assertEquals("d669c1037a49c956ef8f25033abc065c2fb259d4", fingerprint.getFingerprint());
    }

    @Test
    public void testIdenticalStreams() throws Exception {
        final StreamListFingerprint fingerprint1 = new StreamListFingerprint(Lists.newArrayList(stream1));
        final StreamListFingerprint fingerprint2 = new StreamListFingerprint(Lists.newArrayList(stream1));
        final StreamListFingerprint fingerprint3 = new StreamListFingerprint(Lists.newArrayList(stream2));
        Assert.assertEquals(fingerprint1.getFingerprint(), fingerprint2.getFingerprint());
        Assert.assertNotEquals(fingerprint1.getFingerprint(), fingerprint3.getFingerprint());
    }

    @Test
    public void testWithEmptyStreamList() throws Exception {
        final StreamListFingerprint fingerprint = new StreamListFingerprint(Lists.<Stream>newArrayList());
        Assert.assertEquals(expectedEmptyFingerprint, fingerprint.getFingerprint());
    }
}

