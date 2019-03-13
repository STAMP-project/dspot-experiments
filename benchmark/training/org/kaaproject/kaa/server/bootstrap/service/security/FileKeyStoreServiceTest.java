/**
 * Copyright 2014-2016 CyberVision, Inc.
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
/**
 *
 */
package org.kaaproject.kaa.server.bootstrap.service.security;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrey Panasenko <apanasenko@cybervisiontech.com>
 */
public class FileKeyStoreServiceTest {
    private static final String PRIVATE_KEY_LOCATION = "privateKey";

    private static final String PUBLIC_KEY_LOCATION = "publicKey";

    private static String privateKeyPath;

    private static String publicKeyPath;

    /**
     * Test method for {@link org.kaaproject.kaa.server.bootstrap.service.security.BootstrapFileKeyStoreService#FileKeyStoreService()}.
     */
    @Test
    public void testFileKeyStoreService() {
        BootstrapFileKeyStoreService ks = new BootstrapFileKeyStoreService();
        Assert.assertNotNull(ks);
    }

    /**
     * Test method for {@link org.kaaproject.kaa.server.bootstrap.service.security.BootstrapFileKeyStoreService#loadKeys()}.
     */
    @Test
    public void testLoadKeys() {
        BootstrapFileKeyStoreService ks = new BootstrapFileKeyStoreService();
        Assert.assertNotNull(ks);
        ks.setPrivateKeyLocation(FileKeyStoreServiceTest.PRIVATE_KEY_LOCATION);
        ks.setPublicKeyLocation(FileKeyStoreServiceTest.PUBLIC_KEY_LOCATION);
        // Test file generation
        File pri = new File(FileKeyStoreServiceTest.privateKeyPath);
        if (pri.exists()) {
            pri.delete();
        }
        File pub = new File(FileKeyStoreServiceTest.publicKeyPath);
        if (pub.exists()) {
            pub.delete();
        }
        ks.loadKeys();
        if ((!(pri.exists())) || (!(pri.canRead()))) {
            Assert.fail("Private key file create failed.");
        }
        if ((!(pub.exists())) || (!(pub.canRead()))) {
            Assert.fail("Public key file create failed.");
        }
        // Test load incorrect files.
        if (pub.exists()) {
            pub.delete();
            FileWriter pubfw;
            try {
                pubfw = new FileWriter(pub);
                pubfw.write("ascasdcasdcasdc");
                pubfw.close();
            } catch (IOException e) {
                Assert.fail(e.toString());
            }
        }
        try {
            ks.loadKeys();
        } catch (Exception e) {
            Assert.assertNotNull(e.toString(), e);
        }
        if (pri.exists()) {
            pri.delete();
            FileWriter prifw;
            try {
                prifw = new FileWriter(pri);
                prifw.write("ascasdcasdcasdc");
                prifw.close();
            } catch (IOException e) {
                Assert.fail(e.toString());
            }
        }
        try {
            ks.loadKeys();
        } catch (Exception e) {
            Assert.assertNotNull(e.toString(), e);
        }
    }

    /**
     * Test method for {@link org.kaaproject.kaa.server.bootstrap.service.security.BootstrapFileKeyStoreService#getPrivateKey()}.
     */
    @Test
    public void testGetPrivateKey() {
        File pri = new File(FileKeyStoreServiceTest.privateKeyPath);
        if (pri.exists()) {
            pri.delete();
        }
        File pub = new File(FileKeyStoreServiceTest.publicKeyPath);
        if (pub.exists()) {
            pub.delete();
        }
        BootstrapFileKeyStoreService ks = new BootstrapFileKeyStoreService();
        Assert.assertNotNull(ks);
        ks.setPrivateKeyLocation(FileKeyStoreServiceTest.PRIVATE_KEY_LOCATION);
        ks.setPublicKeyLocation(FileKeyStoreServiceTest.PUBLIC_KEY_LOCATION);
        ks.loadKeys();
        Assert.assertNotNull(ks.getPrivateKey());
    }

    /**
     * Test method for {@link org.kaaproject.kaa.server.bootstrap.service.security.BootstrapFileKeyStoreService#getPublicKey()}.
     */
    @Test
    public void testGetPublicKey() {
        File pri = new File(FileKeyStoreServiceTest.privateKeyPath);
        if (pri.exists()) {
            pri.delete();
        }
        File pub = new File(FileKeyStoreServiceTest.publicKeyPath);
        if (pub.exists()) {
            pub.delete();
        }
        BootstrapFileKeyStoreService ks = new BootstrapFileKeyStoreService();
        Assert.assertNotNull(ks);
        ks.setPrivateKeyLocation(FileKeyStoreServiceTest.PRIVATE_KEY_LOCATION);
        ks.setPublicKeyLocation(FileKeyStoreServiceTest.PUBLIC_KEY_LOCATION);
        ks.loadKeys();
        Assert.assertNotNull(ks.getPublicKey());
    }

    @Test(expected = RuntimeException.class)
    public void testIncorrectPublicKey() throws IOException {
        BootstrapFileKeyStoreService ks = new BootstrapFileKeyStoreService();
        Assert.assertNotNull(ks);
        ks.setPublicKeyLocation(FileKeyStoreServiceTest.PUBLIC_KEY_LOCATION);
        File pub = new File(FileKeyStoreServiceTest.publicKeyPath);
        if (pub.exists()) {
            pub.delete();
        }
        FileWriter pubfw;
        pubfw = new FileWriter(pub);
        pubfw.write("ascasdca42314*&^*$@^#5$^&sdcasdc");
        pubfw.close();
        ks.loadKeys();
    }

    @Test(expected = RuntimeException.class)
    public void testIncorrectPrivateKey() throws IOException {
        BootstrapFileKeyStoreService ks = new BootstrapFileKeyStoreService();
        Assert.assertNotNull(ks);
        ks.setPrivateKeyLocation(FileKeyStoreServiceTest.PRIVATE_KEY_LOCATION);
        File pub = new File(FileKeyStoreServiceTest.privateKeyPath);
        if (pub.exists()) {
            pub.delete();
        }
        FileWriter privfw;
        privfw = new FileWriter(pub);
        privfw.write("ascasdca42314*&^*$@^#5$^&sdcasdc");
        privfw.close();
        ks.loadKeys();
    }
}

