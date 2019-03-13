/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 *
 *
 * @author Alexander V. Astapchuk
 * @version $Revision$
 */
package org.apache.harmony.security.tests.java.security;


import java.security.CodeSigner;
import java.security.Timestamp;
import java.security.cert.CertPath;
import java.util.Date;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.TestCertUtils;


/**
 * Unit test for CodeSigner.
 */
public class CodeSignerTest extends TestCase {
    private CertPath cpath = TestCertUtils.genCertPath(3, 0);

    private Date now = new Date();

    private Timestamp ts = new Timestamp(now, cpath);

    /**
     * must throw NPE if signerCertPath is null
     */
    public void testCodeSigner_00() {
        try {
            new CodeSigner(null, ts);
            TestCase.fail("must not accept null");
        } catch (NullPointerException ex) {
            /* it's ok */
        }
    }

    /**
     * timestamp can be null
     */
    public final void testCodeSigner_01() {
        try {
            CodeSigner cs = new CodeSigner(cpath, null);
            TestCase.assertNotNull(cs);
        } catch (Exception e) {
            TestCase.fail("Unexpected exception");
        }
    }

    /**
     * Not null parameters
     */
    public final void testCodeSigner_02() {
        try {
            CodeSigner cs = new CodeSigner(cpath, ts);
            TestCase.assertNotNull(cs);
        } catch (Exception e) {
            TestCase.fail("Unexpected exception");
        }
    }

    /**
     * Test various assertions about equals()
     */
    public final void testEqualsObject() {
        CodeSigner one = new CodeSigner(cpath, ts);
        CodeSigner two = new CodeSigner(cpath, ts);
        CodeSigner three = new CodeSigner(cpath, null);
        CertPath cpath2 = TestCertUtils.genCertPath(5, 3);
        CodeSigner four = new CodeSigner(cpath2, null);
        TestCase.assertTrue(one.equals(one));
        TestCase.assertTrue(one.equals(two));
        TestCase.assertTrue(two.equals(one));
        TestCase.assertFalse(one.equals(three));
        TestCase.assertFalse(three.equals(one));
        TestCase.assertTrue(three.equals(three));
        // different CertPaths
        TestCase.assertFalse(three.equals(four));
        // special cases
        TestCase.assertFalse(one.equals(null));
        TestCase.assertFalse(one.equals(new Object()));
    }

    /**
     * Tests CodeSigner.getSignerCertPath()
     */
    public void testGetSignerCertPath() {
        TestCase.assertSame(new CodeSigner(cpath, null).getSignerCertPath(), cpath);
    }

    /**
     * Tests CodeSigner.getTimeStamp()
     */
    public void testGetTimestamp() {
        TestCase.assertNull(new CodeSigner(cpath, null).getTimestamp());
        TestCase.assertSame(new CodeSigner(cpath, ts).getTimestamp(), ts);
    }

    /**
     * Tests CodeSigner.toString()
     */
    public void testToString() {
        TestCase.assertTrue(new CodeSigner(cpath, null).toString().contains(""));
        TestCase.assertTrue(new CodeSigner(cpath, ts).toString().contains(""));
        TestCase.assertTrue(new CodeSigner(cpath, null).toString().contains("Signer"));
        TestCase.assertTrue(new CodeSigner(cpath, ts).toString().contains(ts.toString()));
    }

    /**
     * Tests CodeSigner.hashCode()
     */
    public void testHashCode() {
        CodeSigner cs1 = new CodeSigner(cpath, ts);
        CodeSigner cs2 = new CodeSigner(cpath, ts);
        CodeSigner cs3 = new CodeSigner(cpath, null);
        TestCase.assertTrue(((cs1.hashCode()) == (cs2.hashCode())));
        TestCase.assertTrue(((cs2.hashCode()) != (cs3.hashCode())));
    }
}

