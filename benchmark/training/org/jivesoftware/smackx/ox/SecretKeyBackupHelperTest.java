/**
 * Copyright 2018 Paul Schaub.
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
package org.jivesoftware.smackx.ox;


import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.Collections;
import junit.framework.TestCase;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.jivesoftware.smack.DummyConnection;
import org.jivesoftware.smack.test.util.FileTestUtil;
import org.jivesoftware.smack.test.util.SmackTestSuite;
import org.jivesoftware.smackx.ox.crypto.PainlessOpenPgpProvider;
import org.jivesoftware.smackx.ox.element.SecretkeyElement;
import org.jivesoftware.smackx.ox.exception.InvalidBackupCodeException;
import org.jivesoftware.smackx.ox.exception.MissingOpenPgpKeyException;
import org.jivesoftware.smackx.ox.exception.MissingUserIdOnKeyException;
import org.jivesoftware.smackx.ox.store.filebased.FileBasedOpenPgpStore;
import org.jivesoftware.smackx.ox.util.SecretKeyBackupHelper;
import org.junit.Test;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.pgpainless.PGPainless;
import org.pgpainless.key.collection.PGPKeyRing;


public class SecretKeyBackupHelperTest extends SmackTestSuite {
    private static final File basePath;

    static {
        basePath = FileTestUtil.getTempDir("ox_secret_keys");
    }

    @Test
    public void backupPasswordGenerationTest() {
        final String alphabet = "123456789ABCDEFGHIJKLMNPQRSTUVWXYZ";
        String backupCode = SecretKeyBackupHelper.generateBackupPassword();
        TestCase.assertEquals(29, backupCode.length());
        for (int i = 0; i < (backupCode.length()); i++) {
            if (((i + 1) % 5) == 0) {
                TestCase.assertEquals('-', backupCode.charAt(i));
            } else {
                TestCase.assertTrue(((alphabet.indexOf(backupCode.charAt(i))) != (-1)));
            }
        }
    }

    @Test
    public void createAndDecryptSecretKeyElementTest() throws IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, PGPException, InvalidBackupCodeException, MissingOpenPgpKeyException, MissingUserIdOnKeyException {
        // Prepare store and provider and so on...
        FileBasedOpenPgpStore store = new FileBasedOpenPgpStore(SecretKeyBackupHelperTest.basePath);
        PainlessOpenPgpProvider provider = new PainlessOpenPgpProvider(new DummyConnection(), store);
        // Generate and import key
        PGPKeyRing keyRing = PGPainless.generateKeyRing().simpleEcKeyRing("xmpp:alice@wonderland.lit");
        BareJid jid = JidCreate.bareFrom("alice@wonderland.lit");
        provider.getStore().importSecretKey(jid, keyRing.getSecretKeys());
        // Create encrypted backup
        String backupCode = SecretKeyBackupHelper.generateBackupPassword();
        SecretkeyElement element = SecretKeyBackupHelper.createSecretkeyElement(provider, jid, Collections.singleton(new org.pgpainless.key.OpenPgpV4Fingerprint(keyRing.getSecretKeys())), backupCode);
        // Decrypt backup and compare
        PGPSecretKeyRing secretKeyRing = SecretKeyBackupHelper.restoreSecretKeyBackup(element, backupCode);
        TestCase.assertTrue(Arrays.equals(keyRing.getSecretKeys().getEncoded(), secretKeyRing.getEncoded()));
    }
}

