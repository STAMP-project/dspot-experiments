/**
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.conscrypt;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import junit.framework.TestCase;
import libcore.java.security.TestKeyStore;


public class TrustedCertificateStoreTest extends TestCase {
    private static final File DIR_TEMP = new File(System.getProperty("java.io.tmpdir"));

    private static final File DIR_TEST = new File(TrustedCertificateStoreTest.DIR_TEMP, "test");

    private static final File DIR_SYSTEM = new File(TrustedCertificateStoreTest.DIR_TEST, "system");

    private static final File DIR_ADDED = new File(TrustedCertificateStoreTest.DIR_TEST, "added");

    private static final File DIR_DELETED = new File(TrustedCertificateStoreTest.DIR_TEST, "removed");

    private static X509Certificate CA1;

    private static X509Certificate CA2;

    private static KeyStore.PrivateKeyEntry PRIVATE;

    private static X509Certificate[] CHAIN;

    private static X509Certificate CA3_WITH_CA1_SUBJECT;

    private static String ALIAS_SYSTEM_CA1;

    private static String ALIAS_SYSTEM_CA2;

    private static String ALIAS_USER_CA1;

    private static String ALIAS_USER_CA2;

    private static String ALIAS_SYSTEM_CHAIN0;

    private static String ALIAS_SYSTEM_CHAIN1;

    private static String ALIAS_SYSTEM_CHAIN2;

    private static String ALIAS_USER_CHAIN0;

    private static String ALIAS_USER_CHAIN1;

    private static String ALIAS_USER_CHAIN2;

    private static String ALIAS_SYSTEM_CA3;

    private static String ALIAS_SYSTEM_CA3_COLLISION;

    private static String ALIAS_USER_CA3;

    private static String ALIAS_USER_CA3_COLLISION;

    private TrustedCertificateStore store;

    public void testEmptyDirectories() throws Exception {
        assertEmpty();
    }

    public void testOneSystemOneDeleted() throws Exception {
        TrustedCertificateStoreTest.install(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
        store.deleteCertificateEntry(TrustedCertificateStoreTest.getAliasSystemCa1());
        assertEmpty();
        assertDeleted(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
    }

    public void testTwoSystemTwoDeleted() throws Exception {
        TrustedCertificateStoreTest.install(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
        store.deleteCertificateEntry(TrustedCertificateStoreTest.getAliasSystemCa1());
        TrustedCertificateStoreTest.install(TrustedCertificateStoreTest.getCa2(), TrustedCertificateStoreTest.getAliasSystemCa2());
        store.deleteCertificateEntry(TrustedCertificateStoreTest.getAliasSystemCa2());
        assertEmpty();
        assertDeleted(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
        assertDeleted(TrustedCertificateStoreTest.getCa2(), TrustedCertificateStoreTest.getAliasSystemCa2());
    }

    public void testPartialFileIsIgnored() throws Exception {
        File file = TrustedCertificateStoreTest.file(TrustedCertificateStoreTest.getAliasSystemCa1());
        OutputStream os = new FileOutputStream(file);
        os.write(0);
        os.close();
        TestCase.assertTrue(file.exists());
        assertEmpty();
        TestCase.assertTrue(file.exists());
    }

    public void testTwoSystem() throws Exception {
        testTwo(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1(), TrustedCertificateStoreTest.getCa2(), TrustedCertificateStoreTest.getAliasSystemCa2());
    }

    public void testTwoUser() throws Exception {
        testTwo(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasUserCa1(), TrustedCertificateStoreTest.getCa2(), TrustedCertificateStoreTest.getAliasUserCa2());
    }

    public void testOneSystemOneUser() throws Exception {
        testTwo(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1(), TrustedCertificateStoreTest.getCa2(), TrustedCertificateStoreTest.getAliasUserCa2());
    }

    public void testTwoSystemSameSubject() throws Exception {
        testTwo(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1(), TrustedCertificateStoreTest.getCa3WithCa1Subject(), TrustedCertificateStoreTest.getAliasSystemCa3Collision());
    }

    public void testTwoUserSameSubject() throws Exception {
        testTwo(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasUserCa1(), TrustedCertificateStoreTest.getCa3WithCa1Subject(), TrustedCertificateStoreTest.getAliasUserCa3Collision());
        store.deleteCertificateEntry(TrustedCertificateStoreTest.getAliasUserCa1());
        assertDeleted(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasUserCa1());
        assertTombstone(TrustedCertificateStoreTest.getAliasUserCa1());
        assertRootCa(TrustedCertificateStoreTest.getCa3WithCa1Subject(), TrustedCertificateStoreTest.getAliasUserCa3Collision());
        assertAliases(TrustedCertificateStoreTest.getAliasUserCa3Collision());
        store.deleteCertificateEntry(TrustedCertificateStoreTest.getAliasUserCa3Collision());
        assertDeleted(TrustedCertificateStoreTest.getCa3WithCa1Subject(), TrustedCertificateStoreTest.getAliasUserCa3Collision());
        assertNoTombstone(TrustedCertificateStoreTest.getAliasUserCa3Collision());
        assertNoTombstone(TrustedCertificateStoreTest.getAliasUserCa1());
        assertEmpty();
    }

    public void testOneSystemOneUserSameSubject() throws Exception {
        testTwo(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1(), TrustedCertificateStoreTest.getCa3WithCa1Subject(), TrustedCertificateStoreTest.getAliasUserCa3());
        testTwo(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasUserCa1(), TrustedCertificateStoreTest.getCa3WithCa1Subject(), TrustedCertificateStoreTest.getAliasSystemCa3());
    }

    public void testOneSystemOneUserOneDeleted() throws Exception {
        TrustedCertificateStoreTest.install(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
        store.installCertificate(TrustedCertificateStoreTest.getCa2());
        store.deleteCertificateEntry(TrustedCertificateStoreTest.getAliasSystemCa1());
        assertDeleted(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
        assertRootCa(TrustedCertificateStoreTest.getCa2(), TrustedCertificateStoreTest.getAliasUserCa2());
        assertAliases(TrustedCertificateStoreTest.getAliasUserCa2());
    }

    public void testOneSystemOneUserOneDeletedSameSubject() throws Exception {
        TrustedCertificateStoreTest.install(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
        store.installCertificate(TrustedCertificateStoreTest.getCa3WithCa1Subject());
        store.deleteCertificateEntry(TrustedCertificateStoreTest.getAliasSystemCa1());
        assertDeleted(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
        assertRootCa(TrustedCertificateStoreTest.getCa3WithCa1Subject(), TrustedCertificateStoreTest.getAliasUserCa3());
        assertAliases(TrustedCertificateStoreTest.getAliasUserCa3());
    }

    public void testUserMaskingSystem() throws Exception {
        TrustedCertificateStoreTest.install(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
        TrustedCertificateStoreTest.install(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasUserCa1());
        assertMasked(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
        assertRootCa(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasUserCa1());
        assertAliases(TrustedCertificateStoreTest.getAliasSystemCa1(), TrustedCertificateStoreTest.getAliasUserCa1());
    }

    public void testChain() throws Exception {
        testChain(TrustedCertificateStoreTest.getAliasSystemChain1(), TrustedCertificateStoreTest.getAliasSystemChain2());
        testChain(TrustedCertificateStoreTest.getAliasSystemChain1(), TrustedCertificateStoreTest.getAliasUserChain2());
        testChain(TrustedCertificateStoreTest.getAliasUserChain1(), TrustedCertificateStoreTest.getAliasSystemCa1());
        testChain(TrustedCertificateStoreTest.getAliasUserChain1(), TrustedCertificateStoreTest.getAliasUserChain2());
    }

    public void testMissingSystemDirectory() throws Exception {
        cleanStore();
        createStore();
        assertEmpty();
    }

    public void testWithExistingUserDirectories() throws Exception {
        TrustedCertificateStoreTest.DIR_ADDED.mkdirs();
        TrustedCertificateStoreTest.DIR_DELETED.mkdirs();
        TrustedCertificateStoreTest.install(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
        assertRootCa(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
        assertAliases(TrustedCertificateStoreTest.getAliasSystemCa1());
    }

    public void testIsTrustAnchorWithReissuedgetCa() throws Exception {
        PublicKey publicKey = TrustedCertificateStoreTest.getPrivate().getCertificate().getPublicKey();
        PrivateKey privateKey = TrustedCertificateStoreTest.getPrivate().getPrivateKey();
        String name = "CN=CA4";
        X509Certificate ca1 = TestKeyStore.createCa(publicKey, privateKey, name);
        Thread.sleep((1 * 1000));// wait to ensure CAs vary by expiration

        X509Certificate ca2 = TestKeyStore.createCa(publicKey, privateKey, name);
        TestCase.assertFalse(ca1.equals(ca2));
        String systemAlias = TrustedCertificateStoreTest.alias(false, ca1, 0);
        TrustedCertificateStoreTest.install(ca1, systemAlias);
        assertRootCa(ca1, systemAlias);
        TestCase.assertTrue(store.isTrustAnchor(ca2));
        TestCase.assertEquals(ca1, store.findIssuer(ca2));
        resetStore();
        String userAlias = TrustedCertificateStoreTest.alias(true, ca1, 0);
        store.installCertificate(ca1);
        assertRootCa(ca1, userAlias);
        TestCase.assertTrue(store.isTrustAnchor(ca2));
        TestCase.assertEquals(ca1, store.findIssuer(ca2));
        resetStore();
    }

    public void testInstallEmpty() throws Exception {
        store.installCertificate(TrustedCertificateStoreTest.getCa1());
        assertRootCa(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasUserCa1());
        assertAliases(TrustedCertificateStoreTest.getAliasUserCa1());
        // reinstalling should not change anything
        store.installCertificate(TrustedCertificateStoreTest.getCa1());
        assertRootCa(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasUserCa1());
        assertAliases(TrustedCertificateStoreTest.getAliasUserCa1());
    }

    public void testInstallEmptySystemExists() throws Exception {
        TrustedCertificateStoreTest.install(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
        assertRootCa(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
        assertAliases(TrustedCertificateStoreTest.getAliasSystemCa1());
        // reinstalling should not affect system CA
        store.installCertificate(TrustedCertificateStoreTest.getCa1());
        assertRootCa(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
        assertAliases(TrustedCertificateStoreTest.getAliasSystemCa1());
    }

    public void testInstallEmptyDeletedSystemExists() throws Exception {
        TrustedCertificateStoreTest.install(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
        store.deleteCertificateEntry(TrustedCertificateStoreTest.getAliasSystemCa1());
        assertEmpty();
        assertDeleted(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
        // installing should restore deleted system CA
        store.installCertificate(TrustedCertificateStoreTest.getCa1());
        assertRootCa(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
        assertAliases(TrustedCertificateStoreTest.getAliasSystemCa1());
    }

    public void testDeleteEmpty() throws Exception {
        store.deleteCertificateEntry(TrustedCertificateStoreTest.getAliasSystemCa1());
        assertEmpty();
        assertDeleted(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
    }

    public void testDeleteUser() throws Exception {
        store.installCertificate(TrustedCertificateStoreTest.getCa1());
        assertRootCa(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasUserCa1());
        assertAliases(TrustedCertificateStoreTest.getAliasUserCa1());
        store.deleteCertificateEntry(TrustedCertificateStoreTest.getAliasUserCa1());
        assertEmpty();
        assertDeleted(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasUserCa1());
        assertNoTombstone(TrustedCertificateStoreTest.getAliasUserCa1());
    }

    public void testDeleteSystem() throws Exception {
        TrustedCertificateStoreTest.install(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
        assertRootCa(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
        assertAliases(TrustedCertificateStoreTest.getAliasSystemCa1());
        store.deleteCertificateEntry(TrustedCertificateStoreTest.getAliasSystemCa1());
        assertEmpty();
        assertDeleted(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
        // deleting again should not change anything
        store.deleteCertificateEntry(TrustedCertificateStoreTest.getAliasSystemCa1());
        assertEmpty();
        assertDeleted(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
    }

    public void testIsUserAddedCertificate() throws Exception {
        TestCase.assertFalse(store.isUserAddedCertificate(TrustedCertificateStoreTest.getCa1()));
        TestCase.assertFalse(store.isUserAddedCertificate(TrustedCertificateStoreTest.getCa2()));
        TrustedCertificateStoreTest.install(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasSystemCa1());
        TestCase.assertFalse(store.isUserAddedCertificate(TrustedCertificateStoreTest.getCa1()));
        TestCase.assertFalse(store.isUserAddedCertificate(TrustedCertificateStoreTest.getCa2()));
        TrustedCertificateStoreTest.install(TrustedCertificateStoreTest.getCa1(), TrustedCertificateStoreTest.getAliasUserCa1());
        TestCase.assertTrue(store.isUserAddedCertificate(TrustedCertificateStoreTest.getCa1()));
        TestCase.assertFalse(store.isUserAddedCertificate(TrustedCertificateStoreTest.getCa2()));
        TrustedCertificateStoreTest.install(TrustedCertificateStoreTest.getCa2(), TrustedCertificateStoreTest.getAliasUserCa2());
        TestCase.assertTrue(store.isUserAddedCertificate(TrustedCertificateStoreTest.getCa1()));
        TestCase.assertTrue(store.isUserAddedCertificate(TrustedCertificateStoreTest.getCa2()));
        store.deleteCertificateEntry(TrustedCertificateStoreTest.getAliasUserCa1());
        TestCase.assertFalse(store.isUserAddedCertificate(TrustedCertificateStoreTest.getCa1()));
        TestCase.assertTrue(store.isUserAddedCertificate(TrustedCertificateStoreTest.getCa2()));
        store.deleteCertificateEntry(TrustedCertificateStoreTest.getAliasUserCa2());
        TestCase.assertFalse(store.isUserAddedCertificate(TrustedCertificateStoreTest.getCa1()));
        TestCase.assertFalse(store.isUserAddedCertificate(TrustedCertificateStoreTest.getCa2()));
    }
}

