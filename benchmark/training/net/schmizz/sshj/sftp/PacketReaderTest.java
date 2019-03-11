/**
 * Copyright (C)2009 - SSHJ Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.schmizz.sshj.sftp;


import java.io.DataOutputStream;
import java.util.Arrays;
import net.schmizz.sshj.common.SSHException;
import org.junit.Assert;
import org.junit.Test;


public class PacketReaderTest {
    private DataOutputStream dataout;

    private PacketReader reader;

    // FIXME What is the byte format for the size? Big endian? Little endian?
    @Test
    public void shouldReadPacket() throws Exception {
        byte[] bytes = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        dataout.writeInt(10);
        dataout.write(bytes);
        dataout.flush();
        SFTPPacket<Response> packet = reader.readPacket();
        Assert.assertEquals(packet.available(), 10);
        Assert.assertTrue(("actual=" + (Arrays.toString(packet.array()))), Arrays.equals(bytes, subArray(packet.array(), 0, 10)));
    }

    @Test
    public void shouldFailWhenPacketLengthTooLarge() throws Exception {
        dataout.writeInt(Integer.MAX_VALUE);
        dataout.flush();
        try {
            reader.readPacket();
            Assert.fail(("Should have failed to read packet of size " + (Integer.MAX_VALUE)));
        } catch (SSHException e) {
            e.printStackTrace();
            // success; indicated packet size was too large
        }
    }
}

