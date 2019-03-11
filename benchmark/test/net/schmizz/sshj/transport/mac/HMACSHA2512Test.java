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
package net.schmizz.sshj.transport.mac;


import java.nio.charset.Charset;
import org.bouncycastle.util.encoders.Hex;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class HMACSHA2512Test {
    private static final Charset CHARSET = Charset.forName("US-ASCII");

    private static final byte[] PLAIN_TEXT = "Hello World".getBytes(HMACSHA2512Test.CHARSET);

    private static final String EXPECTED_HMAC = "28929cffc903039ef18cbc9cea6fd5f1420763af297a470d731236ed1f5a4c61d64dfccf6529265205bec932f2f7850c8ae4de1dc1a5259dc5b1fd85d8e62c04";

    @Test
    public void testUpdateWithDoFinal() {
        HMACSHA2512 hmac = initHmac();
        hmac.update(HMACSHA2512Test.PLAIN_TEXT);
        Assert.assertThat(Hex.toHexString(hmac.doFinal()), CoreMatchers.is(HMACSHA2512Test.EXPECTED_HMAC));
    }

    @Test
    public void testDoFinalWithInput() {
        HMACSHA2512 hmac = initHmac();
        Assert.assertThat(Hex.toHexString(hmac.doFinal(HMACSHA2512Test.PLAIN_TEXT)), CoreMatchers.is(HMACSHA2512Test.EXPECTED_HMAC));
    }

    @Test
    public void testUpdateWithDoFinalWithResultBuffer() {
        HMACSHA2512 hmac = initHmac();
        byte[] resultBuf = new byte[64];
        hmac.update(HMACSHA2512Test.PLAIN_TEXT);
        hmac.doFinal(resultBuf, 0);
        Assert.assertThat(Hex.toHexString(resultBuf), CoreMatchers.is(HMACSHA2512Test.EXPECTED_HMAC));
    }
}

