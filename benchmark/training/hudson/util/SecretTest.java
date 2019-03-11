/**
 * The MIT License
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., Kohsuke Kawaguchi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.util;


import Jenkins.XSTREAM;
import java.util.Base64;
import java.util.Random;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import jenkins.security.ConfidentialStoreRule;
import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import static HistoricalSecrets.MAGIC;


public class SecretTest {
    @Rule
    public ConfidentialStoreRule confidentialStore = new ConfidentialStoreRule();

    @Rule
    public MockSecretRule mockSecretRule = new MockSecretRule();

    private static final Pattern ENCRYPTED_VALUE_PATTERN = Pattern.compile("\\{?[A-Za-z0-9+/]+={0,2}}?");

    @Test
    public void encrypt() {
        Secret secret = Secret.fromString("abc");
        Assert.assertEquals("abc", secret.getPlainText());
        // make sure we got some encryption going
        Assert.assertNotEquals("abc", secret.getEncryptedValue());
        // can we round trip?
        Assert.assertEquals(secret, Secret.fromString(secret.getEncryptedValue()));
        // Two consecutive encryption requests of the same object should result in the same encrypted value - SECURITY-304
        Assert.assertEquals(secret.getEncryptedValue(), secret.getEncryptedValue());
        // Two consecutive encryption requests of different objects with the same value should not result in the same encrypted value - SECURITY-304
        Assert.assertNotEquals(secret.getEncryptedValue(), Secret.fromString(secret.getPlainText()).getEncryptedValue());
    }

    @Test
    public void encryptedValuePattern() {
        for (int i = 1; i < 100; i++) {
            String plaintext = RandomStringUtils.random(new Random().nextInt(i));
            String ciphertext = Secret.fromString(plaintext).getEncryptedValue();
            // println "${plaintext} ? ${ciphertext}"
            assert SecretTest.ENCRYPTED_VALUE_PATTERN.matcher(ciphertext).matches();
        }
        // Not "plain" text
        assert !(SecretTest.ENCRYPTED_VALUE_PATTERN.matcher("hello world").matches());
        // Not "plain" text
        assert !(SecretTest.ENCRYPTED_VALUE_PATTERN.matcher("helloworld!").matches());
        // legacy key
        assert SecretTest.ENCRYPTED_VALUE_PATTERN.matcher("abcdefghijklmnopqr0123456789").matches();
        // legacy key
        assert SecretTest.ENCRYPTED_VALUE_PATTERN.matcher("abcdefghijklmnopqr012345678==").matches();
    }

    @Test
    public void decrypt() {
        Assert.assertEquals("abc", Secret.toString(Secret.fromString("abc")));
    }

    @Test
    public void serialization() {
        Secret s = Secret.fromString("Mr.Jenkins");
        String xml = XSTREAM.toXML(s);
        Assert.assertThat(xml, CoreMatchers.not(CoreMatchers.containsString(s.getPlainText())));
        // TODO MatchesPattern not available until Hamcrest 2.0
        Assert.assertTrue(xml, xml.matches("<hudson[.]util[.]Secret>[{][A-Za-z0-9+/]+={0,2}[}]</hudson[.]util[.]Secret>"));
        Object o = XSTREAM.fromXML(xml);
        Assert.assertEquals(xml, s, o);
    }

    public static class Foo {
        Secret password;
    }

    /**
     * Makes sure the serialization form is backward compatible with String.
     */
    @Test
    public void testCompatibilityFromString() {
        String tagName = SecretTest.Foo.class.getName().replace("$", "_-");
        String xml = ((("<" + tagName) + "><password>secret</password></") + tagName) + ">";
        SecretTest.Foo foo = new SecretTest.Foo();
        XSTREAM.fromXML(xml, foo);
        Assert.assertEquals("secret", Secret.toString(foo.password));
    }

    /**
     * Secret persisted with Jenkins.getSecretKey() should still decrypt OK.
     */
    @Test
    public void migrationFromLegacyKeyToConfidentialStore() throws Exception {
        SecretKey legacy = HistoricalSecrets.getLegacyKey();
        for (String str : new String[]{ "Hello world", "", "\u0000unprintable" }) {
            Cipher cipher = Secret.getCipher("AES");
            cipher.init(Cipher.ENCRYPT_MODE, legacy);
            String old = new String(Base64.getEncoder().encode(cipher.doFinal((str + (MAGIC)).getBytes("UTF-8"))));
            Secret s = Secret.fromString(old);
            Assert.assertEquals("secret by the old key should decrypt", str, s.getPlainText());
            Assert.assertNotEquals("but when encrypting, ConfidentialKey should be in use", old, s.getEncryptedValue());
        }
    }
}

