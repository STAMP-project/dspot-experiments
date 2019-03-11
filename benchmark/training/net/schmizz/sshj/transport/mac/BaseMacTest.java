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
import net.schmizz.sshj.common.SSHRuntimeException;
import org.bouncycastle.util.encoders.Hex;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class BaseMacTest {
    private static final Charset CHARSET = Charset.forName("US-ASCII");

    private static final byte[] PLAIN_TEXT = "Hello World".getBytes(BaseMacTest.CHARSET);

    private static final String EXPECTED_HMAC = "24ddeed57ad91465c5b59dce74ef73778bfb0cb9";

    private static final String KEY = "et1Quo5ooCie6theel8i";

    @Test
    public void testResizeTooBigKeys() {
        BaseMAC hmac = new HMACSHA1();
        hmac.init(((BaseMacTest.KEY) + "foo").getBytes(BaseMacTest.CHARSET));
        hmac.update(BaseMacTest.PLAIN_TEXT);
        Assert.assertThat(Hex.toHexString(hmac.doFinal()), CoreMatchers.is(BaseMacTest.EXPECTED_HMAC));
    }

    @Test(expected = SSHRuntimeException.class)
    public void testUnknownAlgorithm() {
        BaseMAC hmac = new BaseMAC("AlgorithmThatDoesNotExist", 20, 20, false);
        hmac.init(((BaseMacTest.KEY) + "foo").getBytes(BaseMacTest.CHARSET));
        Assert.fail("Should not initialize a non-existent MAC");
    }

    @Test
    public void testUpdateWithDoFinal() {
        BaseMAC hmac = initHmac();
        hmac.update(BaseMacTest.PLAIN_TEXT);
        Assert.assertThat(Hex.toHexString(hmac.doFinal()), CoreMatchers.is(BaseMacTest.EXPECTED_HMAC));
    }

    @Test
    public void testUpdateWithRange() {
        BaseMAC hmac = initHmac();
        // a leading and trailing byte to the plaintext
        byte[] plainText = new byte[(BaseMacTest.PLAIN_TEXT.length) + 2];
        System.arraycopy(BaseMacTest.PLAIN_TEXT, 0, plainText, 1, BaseMacTest.PLAIN_TEXT.length);
        // update with the range from the second to penultimate byte
        hmac.update(plainText, 1, BaseMacTest.PLAIN_TEXT.length);
        Assert.assertThat(Hex.toHexString(hmac.doFinal()), CoreMatchers.is(BaseMacTest.EXPECTED_HMAC));
    }

    @Test
    public void testDoFinalWithInput() {
        BaseMAC hmac = initHmac();
        Assert.assertThat(Hex.toHexString(hmac.doFinal(BaseMacTest.PLAIN_TEXT)), CoreMatchers.is(BaseMacTest.EXPECTED_HMAC));
    }

    @Test
    public void testUpdateWithDoFinalWithResultBuffer() {
        BaseMAC hmac = initHmac();
        byte[] resultBuf = new byte[20];
        hmac.update(BaseMacTest.PLAIN_TEXT);
        hmac.doFinal(resultBuf, 0);
        Assert.assertThat(Hex.toHexString(resultBuf), CoreMatchers.is(BaseMacTest.EXPECTED_HMAC));
    }
}

