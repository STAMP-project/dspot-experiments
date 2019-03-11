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


public class HMACSHA196Test {
    private static final Charset CHARSET = Charset.forName("US-ASCII");

    private static final byte[] PLAIN_TEXT = "Hello World".getBytes(HMACSHA196Test.CHARSET);

    private static final String EXPECTED_HMAC = "24ddeed57ad91465c5b59dce";

    @Test
    public void testUpdateWithDoFinal() {
        HMACSHA196 hmac = initHmac();
        hmac.update(HMACSHA196Test.PLAIN_TEXT);
        Assert.assertThat(Hex.toHexString(hmac.doFinal()), CoreMatchers.is(HMACSHA196Test.EXPECTED_HMAC));
    }

    @Test
    public void testDoFinalWithInput() {
        HMACSHA196 hmac = initHmac();
        Assert.assertThat(Hex.toHexString(hmac.doFinal(HMACSHA196Test.PLAIN_TEXT)), CoreMatchers.is(HMACSHA196Test.EXPECTED_HMAC));
    }

    @Test
    public void testUpdateWithDoFinalWithResultBuffer() {
        HMACSHA196 hmac = initHmac();
        byte[] resultBuf = new byte[12];
        hmac.update(HMACSHA196Test.PLAIN_TEXT);
        hmac.doFinal(resultBuf, 0);
        Assert.assertThat(Hex.toHexString(resultBuf), CoreMatchers.is(HMACSHA196Test.EXPECTED_HMAC));
    }
}

