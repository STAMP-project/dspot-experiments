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
package org.graylog2.plugin.journal;


import Configuration.EMPTY_CONFIGURATION;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.graylog2.plugin.system.NodeId;
import org.junit.Assert;
import org.junit.Test;


public class RawMessageTest {
    @Test
    public void minimalEncodeDecode() throws IOException {
        final RawMessage rawMessage = new RawMessage("testmessage".getBytes(StandardCharsets.UTF_8));
        final File tempFile = File.createTempFile("node", "test");
        rawMessage.addSourceNode("inputid", new NodeId(tempFile.getAbsolutePath()));
        rawMessage.setCodecName("raw");
        rawMessage.setCodecConfig(EMPTY_CONFIGURATION);
        final byte[] encoded = rawMessage.encode();
        final RawMessage decodedMsg = RawMessage.decode(encoded, 1);
        Assert.assertNotNull(decodedMsg);
        Assert.assertArrayEquals("testmessage".getBytes(StandardCharsets.UTF_8), decodedMsg.getPayload());
        Assert.assertEquals("raw", decodedMsg.getCodecName());
    }
}

