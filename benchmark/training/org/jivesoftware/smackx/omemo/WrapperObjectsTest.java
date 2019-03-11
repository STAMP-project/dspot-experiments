/**
 * Copyright the original author or authors
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
package org.jivesoftware.smackx.omemo;


import OmemoElement.TYPE_OMEMO_MESSAGE;
import OmemoElement.TYPE_OMEMO_PREKEY_MESSAGE;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import junit.framework.TestCase;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jivesoftware.smackx.omemo.element.OmemoElement;
import org.jivesoftware.smackx.omemo.exceptions.CryptoFailedException;
import org.jivesoftware.smackx.omemo.internal.CipherAndAuthTag;
import org.jivesoftware.smackx.omemo.internal.CiphertextTuple;
import org.jivesoftware.smackx.omemo.util.OmemoMessageBuilder;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the identityKeyWrapper.
 */
public class WrapperObjectsTest {
    @Test
    public void ciphertextTupleTest() {
        byte[] c = OmemoMessageBuilder.generateIv();
        CiphertextTuple c1 = new CiphertextTuple(c, OmemoElement.TYPE_OMEMO_PREKEY_MESSAGE);
        TestCase.assertTrue(c1.isPreKeyMessage());
        Assert.assertArrayEquals(c, c1.getCiphertext());
        TestCase.assertEquals(TYPE_OMEMO_PREKEY_MESSAGE, c1.getMessageType());
        CiphertextTuple c2 = new CiphertextTuple(c, OmemoElement.TYPE_OMEMO_MESSAGE);
        Assert.assertFalse(c2.isPreKeyMessage());
        TestCase.assertEquals(TYPE_OMEMO_MESSAGE, c2.getMessageType());
    }

    @Test
    public void cipherAndAuthTagTest() throws NoSuchAlgorithmException, CryptoFailedException {
        Security.addProvider(new BouncyCastleProvider());
        byte[] key = OmemoMessageBuilder.generateKey(KEYTYPE, KEYLENGTH);
        byte[] iv = OmemoMessageBuilder.generateIv();
        byte[] authTag = OmemoMessageBuilder.generateIv();
        CipherAndAuthTag cat = new CipherAndAuthTag(key, iv, authTag, true);
        Assert.assertNotNull(cat.getCipher());
        Assert.assertArrayEquals(key, cat.getKey());
        Assert.assertArrayEquals(iv, cat.getIv());
        Assert.assertArrayEquals(authTag, cat.getAuthTag());
        TestCase.assertTrue(cat.wasPreKeyEncrypted());
    }
}

