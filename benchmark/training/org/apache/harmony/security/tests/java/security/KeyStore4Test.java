package org.apache.harmony.security.tests.java.security;


import TestKeyStoreSpi.CERT;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.MyProvider;


public class KeyStore4Test extends TestCase {
    Provider provider = new MyProvider();

    KeyStore keyStore;

    KeyStore uninitialized;

    KeyStore failing;

    public static final String KEY_STORE_TYPE = "TestKeyStore";

    public void testGetInstanceString() {
        try {
            KeyStore ks = KeyStore.getInstance("TestKeyStore");
            TestCase.assertNotNull("keystore is null", ks);
            TestCase.assertEquals("KeyStore is not of expected Type", "TestKeyStore", ks.getType());
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            KeyStore.getInstance("UnknownKeyStore");
            TestCase.fail("expected KeyStoreException");
        } catch (KeyStoreException e) {
            // ok
        }
        try {
            KeyStore.getInstance(null);
            TestCase.fail("expected NullPointerException");
        } catch (NullPointerException e) {
            // ok
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
    }

    public void testGetInstanceStringString() {
        try {
            KeyStore ks = KeyStore.getInstance("TestKeyStore", provider.getName());
            TestCase.assertNotNull("keystore is null", ks);
            TestCase.assertEquals("KeyStore is not of expected type", "TestKeyStore", ks.getType());
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchProviderException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            KeyStore.getInstance("UnknownKeyStore", provider.getName());
            TestCase.fail("expected KeyStoreException");
        } catch (KeyStoreException e) {
            // ok
        } catch (NoSuchProviderException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            KeyStore.getInstance("TestKeyStore", ((String) (null)));
            TestCase.fail("expected IllegalArgumentException");
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchProviderException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            KeyStore.getInstance("TestKeyStore", "");
            TestCase.fail("expected IllegalArgumentException");
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchProviderException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            KeyStore.getInstance(null, provider.getName());
            TestCase.fail("expected KeyStoreException");
        } catch (KeyStoreException e) {
            // ok
        } catch (NoSuchProviderException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NullPointerException e) {
            // also ok
        }
        try {
            KeyStore.getInstance("TestKeyStore", "UnknownProvider");
            TestCase.fail("expected NoSuchProviderException");
        } catch (NoSuchProviderException e) {
            // ok
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
    }

    public void testGetInstanceStringProvider() {
        try {
            KeyStore ks = KeyStore.getInstance("TestKeyStore", provider);
            TestCase.assertNotNull("KeyStore is null", ks);
            TestCase.assertEquals("KeyStore is not of expected type", "TestKeyStore", ks.getType());
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            KeyStore.getInstance("UnknownKeyStore", provider);
            TestCase.fail("expected KeyStoreException");
        } catch (KeyStoreException e) {
            // ok;
        }
        try {
            KeyStore.getInstance("TestKeyStore", ((Provider) (null)));
            TestCase.fail("expected IllegalArgumentException");
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (IllegalArgumentException e) {
            // ok
        }
        try {
            KeyStore.getInstance(null, provider);
            TestCase.fail("expected NullPointerException");
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NullPointerException e) {
            // ok
        }
    }

    public void testGetKey() {
        try {
            Key key = keyStore.getKey("keyalias", null);
            TestCase.assertNotNull(key);
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (UnrecoverableKeyException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            keyStore.getKey("certalias", null);
            TestCase.fail("expected NoSuchAlgorithmException");
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchAlgorithmException e) {
            // ok
        } catch (UnrecoverableKeyException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            uninitialized.getKey("keyalias", null);
            TestCase.fail("expected KeyStoreException");
        } catch (KeyStoreException e) {
            // ok
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (UnrecoverableKeyException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            keyStore.getKey("unknownalias", null);
            TestCase.fail("expected NoSuchAlgorithmException");
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchAlgorithmException e) {
            // ok
        } catch (UnrecoverableKeyException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            keyStore.getKey("unknownalias", "PASSWORD".toCharArray());
            TestCase.fail("expected UnrecoverableKeyException");
        } catch (UnrecoverableKeyException e) {
            // ok
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
    }

    public void testGetCertificateAlias() {
        try {
            String alias = keyStore.getCertificateAlias(CERT);
            TestCase.assertNotNull("alias is null", alias);
            TestCase.assertEquals("alias is not expected", "certalias", alias);
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            uninitialized.getCertificateAlias(CERT);
            TestCase.fail("expected KeyStoreException");
        } catch (KeyStoreException e) {
            // ok
        }
        try {
            keyStore.getCertificateAlias(null);
            TestCase.fail("expected NullPointerException");
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NullPointerException e) {
            // ok
        }
        try {
            String certificateAlias = keyStore.getCertificateAlias(new MyCertificate("dummy", null));
            TestCase.assertNull("alias was not null", certificateAlias);
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
    }

    public void testStoreOutputStreamCharArray() {
        OutputStream os = new ByteArrayOutputStream();
        char[] password = "PASSWORD".toCharArray();
        try {
            keyStore.store(os, password);
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (CertificateException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (IOException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            keyStore.store(os, null);
            TestCase.fail("expected NoSuchAlgorithmException");
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchAlgorithmException e) {
            // ok
        } catch (CertificateException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (IOException e) {
            // ok
        }
        try {
            keyStore.store(os, "".toCharArray());
            TestCase.fail("expected CertificateException");
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (CertificateException e) {
            // ok
        } catch (IOException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            keyStore.store(null, null);
            TestCase.fail("expected IOException");
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (CertificateException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (IOException e) {
            // ok
        }
        try {
            uninitialized.store(null, null);
            TestCase.fail("expected KeyStoreException");
        } catch (KeyStoreException e) {
            // ok
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (CertificateException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (IOException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
    }

    public void testStoreLoadStoreParameter() {
        try {
            keyStore.store(new KeyStore.LoadStoreParameter() {
                public KeyStore.ProtectionParameter getProtectionParameter() {
                    return new KeyStore.PasswordProtection("PASSWORD".toCharArray());
                }
            });
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (CertificateException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (IOException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            keyStore.store(null);
            TestCase.fail("expected IOException");
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (CertificateException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (IOException e) {
            // ok
        }
        try {
            keyStore.store(new KeyStore.LoadStoreParameter() {
                public KeyStore.ProtectionParameter getProtectionParameter() {
                    return null;
                }
            });
            TestCase.fail("expected UnsupportedOperationException");
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (CertificateException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (IOException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (UnsupportedOperationException e) {
            // ok
        }
        try {
            keyStore.store(new KeyStore.LoadStoreParameter() {
                public KeyStore.ProtectionParameter getProtectionParameter() {
                    return new KeyStore.PasswordProtection("".toCharArray());
                }
            });
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (CertificateException e) {
            // ok
        } catch (IOException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            keyStore.store(new KeyStore.LoadStoreParameter() {
                public KeyStore.ProtectionParameter getProtectionParameter() {
                    return new KeyStore.PasswordProtection(null);
                }
            });
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NoSuchAlgorithmException e) {
            // ok
        } catch (CertificateException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (IOException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            uninitialized.store(null);
            TestCase.fail("expected KeyStoreException");
        } catch (KeyStoreException e) {
            // ok
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (CertificateException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (IOException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
    }

    public void testLoadInputStreamCharArray() {
        InputStream is = new ByteArrayInputStream("DATA".getBytes());
        char[] password = "PASSWORD".toCharArray();
        try {
            keyStore.load(is, password);
            TestCase.assertTrue(keyStore.containsAlias("keyalias"));
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (CertificateException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (IOException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            keyStore.load(new ByteArrayInputStream("".getBytes()), password);
            TestCase.fail("expected IOException");
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (CertificateException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (IOException e) {
            // ok
        }
        try {
            keyStore.load(is, null);
            TestCase.fail("expected NoSuchAlgorithmException");
        } catch (NoSuchAlgorithmException e) {
            // ok
        } catch (CertificateException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (IOException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            keyStore.load(is, new char[]{  });
            TestCase.fail("expected CertificateException");
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (CertificateException e) {
            // ok
        } catch (IOException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
    }

    public void testLoadLoadStoreParameter() {
        try {
            keyStore.load(null);
            TestCase.fail("expected NoSuchAlgorithmException");
        } catch (NoSuchAlgorithmException e) {
            // ok
        } catch (CertificateException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (IOException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            keyStore.load(new KeyStore.LoadStoreParameter() {
                public KeyStore.ProtectionParameter getProtectionParameter() {
                    return new KeyStore.PasswordProtection("PASSWORD".toCharArray());
                }
            });
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (CertificateException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (IOException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            keyStore.load(new KeyStore.LoadStoreParameter() {
                public KeyStore.ProtectionParameter getProtectionParameter() {
                    return null;
                }
            });
            TestCase.fail("expected NoSuchAlgorithmException");
        } catch (NoSuchAlgorithmException e) {
            // ok
        } catch (CertificateException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (IOException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            keyStore.load(new KeyStore.LoadStoreParameter() {
                public KeyStore.ProtectionParameter getProtectionParameter() {
                    return new KeyStore.ProtectionParameter() {};
                }
            });
            TestCase.fail("expected CertificateException");
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (CertificateException e) {
            // ok
        } catch (IOException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
    }

    public void testGetEntry() {
        try {
            KeyStore.Entry entry = keyStore.getEntry("certalias", null);
            TestCase.assertNotNull("entry is null", entry);
            TestCase.assertTrue("entry is not cert entry", (entry instanceof KeyStore.TrustedCertificateEntry));
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (UnrecoverableEntryException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            KeyStore.Entry entry = keyStore.getEntry("certalias", new KeyStore.ProtectionParameter() {});
            TestCase.assertNotNull(entry);
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (UnrecoverableEntryException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (UnsupportedOperationException e) {
            // ok
        }
        try {
            KeyStore.Entry entry = keyStore.getEntry("keyalias", new KeyStore.PasswordProtection(new char[]{  }));
            TestCase.assertNotNull(entry);
            TestCase.assertTrue((entry instanceof KeyStore.SecretKeyEntry));
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (UnrecoverableEntryException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        }
        try {
            keyStore.getEntry("unknownalias", new KeyStore.PasswordProtection(new char[]{  }));
            TestCase.fail("expected NoSuchAlgorithmException");
        } catch (NoSuchAlgorithmException e) {
            // ok
        } catch (UnrecoverableEntryException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (UnsupportedOperationException e) {
            // also ok
        }
        try {
            keyStore.getEntry(null, new KeyStore.ProtectionParameter() {});
            TestCase.fail("expected NullPointerException");
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (UnrecoverableEntryException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (KeyStoreException e) {
            TestCase.fail(("unexpected exception: " + e));
        } catch (NullPointerException e) {
            // ok
        }
    }

    public void testGetType() {
        TestCase.assertEquals(KeyStore4Test.KEY_STORE_TYPE, keyStore.getType());
    }

    public void testGetProvider() {
        TestCase.assertNotNull(keyStore.getProvider());
        TestCase.assertEquals("not equal", provider, keyStore.getProvider());
    }
}

