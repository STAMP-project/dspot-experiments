/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util.security;


import org.eclipse.jetty.util.security.Credential.Crypt;
import org.eclipse.jetty.util.security.Credential.MD5;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * CredentialTest
 */
public class CredentialTest {
    @Test
    public void testCrypt() throws Exception {
        Crypt c1 = ((Crypt) (Credential.getCredential(Crypt.crypt("fred", "abc123"))));
        Crypt c2 = ((Crypt) (Credential.getCredential(Crypt.crypt("fred", "abc123"))));
        Crypt c3 = ((Crypt) (Credential.getCredential(Crypt.crypt("fred", "xyz123"))));
        Credential c4 = Credential.getCredential(Crypt.crypt("fred", "xyz123"));
        Assertions.assertTrue(c1.equals(c2));
        Assertions.assertTrue(c2.equals(c1));
        Assertions.assertFalse(c1.equals(c3));
        Assertions.assertFalse(c3.equals(c1));
        Assertions.assertFalse(c3.equals(c2));
        Assertions.assertTrue(c4.equals(c3));
        Assertions.assertFalse(c4.equals(c1));
    }

    @Test
    public void testMD5() throws Exception {
        MD5 m1 = ((MD5) (Credential.getCredential(MD5.digest("123foo"))));
        MD5 m2 = ((MD5) (Credential.getCredential(MD5.digest("123foo"))));
        MD5 m3 = ((MD5) (Credential.getCredential(MD5.digest("123boo"))));
        Assertions.assertTrue(m1.equals(m2));
        Assertions.assertTrue(m2.equals(m1));
        Assertions.assertFalse(m3.equals(m1));
    }

    @Test
    public void testPassword() throws Exception {
        Password p1 = new Password(Password.obfuscate("abc123"));
        Credential p2 = Credential.getCredential(Password.obfuscate("abc123"));
        Assertions.assertTrue(p1.equals(p2));
    }

    @Test
    public void testStringEquals() {
        Assertions.assertTrue(Credential.stringEquals("foo", "foo"));
        Assertions.assertFalse(Credential.stringEquals("foo", "fooo"));
        Assertions.assertFalse(Credential.stringEquals("foo", "fo"));
        Assertions.assertFalse(Credential.stringEquals("foo", "bar"));
    }

    @Test
    public void testBytesEquals() {
        Assertions.assertTrue(Credential.byteEquals("foo".getBytes(), "foo".getBytes()));
        Assertions.assertFalse(Credential.byteEquals("foo".getBytes(), "fooo".getBytes()));
        Assertions.assertFalse(Credential.byteEquals("foo".getBytes(), "fo".getBytes()));
        Assertions.assertFalse(Credential.byteEquals("foo".getBytes(), "bar".getBytes()));
    }

    @Test
    public void testEmptyString() {
        Assertions.assertFalse(Credential.stringEquals("fooo", ""));
        Assertions.assertFalse(Credential.stringEquals("", "fooo"));
        Assertions.assertTrue(Credential.stringEquals("", ""));
    }

    @Test
    public void testEmptyBytes() {
        Assertions.assertFalse(Credential.byteEquals("fooo".getBytes(), "".getBytes()));
        Assertions.assertFalse(Credential.byteEquals("".getBytes(), "fooo".getBytes()));
        Assertions.assertTrue(Credential.byteEquals("".getBytes(), "".getBytes()));
    }
}

