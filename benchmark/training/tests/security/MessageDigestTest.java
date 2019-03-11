/**
 * Copyright (C) 2009 The Android Open Source Project
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
package tests.security;


import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import junit.framework.TestCase;


public abstract class MessageDigestTest extends TestCase {
    private String digestAlgorithmName;

    protected MessageDigestTest(String digestAlgorithmName) {
        super();
        this.digestAlgorithmName = digestAlgorithmName;
    }

    private MessageDigest digest;

    private InputStream sourceData;

    private byte[] checkDigest;

    public void testMessageDigest1() {
        byte[] buf = new byte[128];
        int read = 0;
        try {
            while ((read = sourceData.read(buf)) != (-1)) {
                digest.update(buf, 0, read);
            } 
            sourceData.close();
        } catch (IOException e) {
            TestCase.fail("failed to read digest data");
        }
        byte[] computedDigest = digest.digest();
        TestCase.assertNotNull("computed digest is is null", computedDigest);
        TestCase.assertEquals("digest length mismatch", checkDigest.length, computedDigest.length);
        for (int i = 0; i < (checkDigest.length); i++) {
            TestCase.assertEquals((("byte " + i) + " of computed and check digest differ"), checkDigest[i], computedDigest[i]);
        }
    }

    public void testMessageDigest2() {
        int val;
        try {
            while ((val = sourceData.read()) != (-1)) {
                digest.update(((byte) (val)));
            } 
            sourceData.close();
        } catch (IOException e) {
            TestCase.fail("failed to read digest data");
        }
        byte[] computedDigest = digest.digest();
        TestCase.assertNotNull("computed digest is is null", computedDigest);
        TestCase.assertEquals("digest length mismatch", checkDigest.length, computedDigest.length);
        for (int i = 0; i < (checkDigest.length); i++) {
            TestCase.assertEquals((("byte " + i) + " of computed and check digest differ"), checkDigest[i], computedDigest[i]);
        }
    }

    /**
     * Official FIPS180-2 testcases
     */
    protected String source1;

    protected String source2;

    protected String source3;

    protected String expected1;

    protected String expected2;

    protected String expected3;

    public void testfips180_2_singleblock() {
        digest.update(source1.getBytes(), 0, source1.length());
        byte[] computedDigest = digest.digest();
        TestCase.assertNotNull("computed digest is null", computedDigest);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < (computedDigest.length); i++) {
            String res = Integer.toHexString(((computedDigest[i]) & 255));
            sb.append((((res.length()) == 1 ? "0" : "") + res));
        }
        TestCase.assertEquals("computed and check digest differ", expected1, sb.toString());
    }

    public void testfips180_2_multiblock() {
        digest.update(source2.getBytes(), 0, source2.length());
        byte[] computedDigest = digest.digest();
        TestCase.assertNotNull("computed digest is null", computedDigest);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < (computedDigest.length); i++) {
            String res = Integer.toHexString(((computedDigest[i]) & 255));
            sb.append((((res.length()) == 1 ? "0" : "") + res));
        }
        TestCase.assertEquals("computed and check digest differ", expected2, sb.toString());
    }

    public void testfips180_2_longMessage() {
        digest.update(source3.getBytes(), 0, source3.length());
        byte[] computedDigest = digest.digest();
        TestCase.assertNotNull("computed digest is null", computedDigest);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < (computedDigest.length); i++) {
            String res = Integer.toHexString(((computedDigest[i]) & 255));
            sb.append((((res.length()) == 1 ? "0" : "") + res));
        }
        TestCase.assertEquals("computed and check digest differ", expected3, sb.toString());
    }
}

