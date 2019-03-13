/**
 * Copyright (c) 2016?2017 Andrei Tomashpolskiy and individual contributors.
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
package bt.protocol;


import org.junit.Assert;
import org.junit.Test;


public class ProtocolsTest {
    private static final byte[] bytes = new byte[]{ 0, 15, ((byte) (240)), ((byte) (255)) };

    @Test
    public void test_toHex() {
        Assert.assertEquals("000ff0ff", Protocols.toHex(ProtocolsTest.bytes));
    }

    @Test
    public void test_fromHex_LowerCase() {
        String s = "000ff0ff";
        Assert.assertArrayEquals(ProtocolsTest.bytes, Protocols.fromHex(s));
    }

    @Test
    public void test_fromHex_UpperCase() {
        String s = "000FF0FF";
        Assert.assertArrayEquals(ProtocolsTest.bytes, Protocols.fromHex(s));
    }

    @Test
    public void test_fromHex_MixedCase() {
        String s = "000fF0Ff";
        Assert.assertArrayEquals(ProtocolsTest.bytes, Protocols.fromHex(s));
    }

    @Test
    public void test_infoHashFromBase32() {
        String infoHashHex = "C12FE1C06BBA254A9DC9F519B335AA7C1367A88A";
        String infoHashBase32 = "YEX6DQDLXISUVHOJ6UM3GNNKPQJWPKEK";
        Assert.assertArrayEquals(Protocols.fromHex(infoHashHex), Protocols.infoHashFromBase32(infoHashBase32));
    }
}

