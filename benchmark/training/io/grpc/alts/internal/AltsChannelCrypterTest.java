/**
 * Copyright 2018 The gRPC Authors
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
package io.grpc.alts.internal;


import java.security.GeneralSecurityException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link AltsChannelCrypter}.
 */
@RunWith(JUnit4.class)
public final class AltsChannelCrypterTest extends ChannelCrypterNettyTestBase {
    @Test
    public void encryptDecryptKdfCounterIncr() throws GeneralSecurityException {
        AltsChannelCrypter client = new AltsChannelCrypter(new byte[AltsChannelCrypter.getKeyLength()], true);
        AltsChannelCrypter server = new AltsChannelCrypter(new byte[AltsChannelCrypter.getKeyLength()], false);
        String message = "Hello world";
        ChannelCrypterNettyTestBase.FrameEncrypt frameEncrypt1 = createFrameEncrypt(message);
        client.encrypt(frameEncrypt1.out, frameEncrypt1.plain);
        ChannelCrypterNettyTestBase.FrameDecrypt frameDecrypt1 = frameDecryptOfEncrypt(frameEncrypt1);
        server.decrypt(frameDecrypt1.out, frameDecrypt1.tag, frameDecrypt1.ciphertext);
        assertThat(frameEncrypt1.plain.get(0).slice(0, frameDecrypt1.out.readableBytes())).isEqualTo(frameDecrypt1.out);
        // Increase counters to get a new KDF counter value (first two bytes are skipped).
        client.incrementOutCounterForTesting((1 << 17));
        server.incrementInCounterForTesting((1 << 17));
        ChannelCrypterNettyTestBase.FrameEncrypt frameEncrypt2 = createFrameEncrypt(message);
        client.encrypt(frameEncrypt2.out, frameEncrypt2.plain);
        ChannelCrypterNettyTestBase.FrameDecrypt frameDecrypt2 = frameDecryptOfEncrypt(frameEncrypt2);
        server.decrypt(frameDecrypt2.out, frameDecrypt2.tag, frameDecrypt2.ciphertext);
        assertThat(frameEncrypt2.plain.get(0).slice(0, frameDecrypt2.out.readableBytes())).isEqualTo(frameDecrypt2.out);
    }

    @Test
    public void overflowsClient() throws GeneralSecurityException {
        byte[] maxFirst = new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)) };
        byte[] maxFirstPred = Arrays.copyOf(maxFirst, maxFirst.length);
        (maxFirstPred[0])--;
        byte[] oldCounter = new byte[AltsChannelCrypter.getCounterLength()];
        byte[] counter = Arrays.copyOf(maxFirstPred, maxFirstPred.length);
        AltsChannelCrypter.incrementCounter(counter, oldCounter);
        assertThat(oldCounter).isEqualTo(maxFirstPred);
        assertThat(counter).isEqualTo(maxFirst);
        try {
            AltsChannelCrypter.incrementCounter(counter, oldCounter);
            Assert.fail("Exception expected");
        } catch (GeneralSecurityException ex) {
            assertThat(ex).hasMessageThat().contains("Counter has overflowed");
        }
        assertThat(oldCounter).isEqualTo(maxFirst);
        assertThat(counter).isEqualTo(maxFirst);
    }

    @Test
    public void overflowsServer() throws GeneralSecurityException {
        byte[] maxSecond = new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (128)) };
        byte[] maxSecondPred = Arrays.copyOf(maxSecond, maxSecond.length);
        (maxSecondPred[0])--;
        byte[] oldCounter = new byte[AltsChannelCrypter.getCounterLength()];
        byte[] counter = Arrays.copyOf(maxSecondPred, maxSecondPred.length);
        AltsChannelCrypter.incrementCounter(counter, oldCounter);
        assertThat(oldCounter).isEqualTo(maxSecondPred);
        assertThat(counter).isEqualTo(maxSecond);
        try {
            AltsChannelCrypter.incrementCounter(counter, oldCounter);
            Assert.fail("Exception expected");
        } catch (GeneralSecurityException ex) {
            assertThat(ex).hasMessageThat().contains("Counter has overflowed");
        }
        assertThat(oldCounter).isEqualTo(maxSecond);
        assertThat(counter).isEqualTo(maxSecond);
    }
}

