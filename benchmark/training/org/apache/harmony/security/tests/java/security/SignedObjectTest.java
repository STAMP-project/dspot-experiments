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
package org.apache.harmony.security.tests.java.security;


import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignedObject;
import java.util.Properties;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.TestKeyPair;


/**
 * Tests for <code>SignedObject</code> constructor and methods
 */
public class SignedObjectTest extends TestCase {
    public void testSignedObject() throws Exception {
        TestKeyPair tkp = null;
        Properties prop;
        Signature sig = Signature.getInstance("SHA1withDSA");
        try {
            tkp = new TestKeyPair("DSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }
        prop = new Properties();
        prop.put("aaa", "bbb");
        SignedObject so = new SignedObject(prop, tkp.getPrivate(), sig);
        TestCase.assertEquals("SHA1withDSA", so.getAlgorithm());
        TestCase.assertEquals(prop, so.getObject());
        TestCase.assertTrue("verify() failed", so.verify(tkp.getPublic(), sig));
        TestCase.assertNotNull("signature is null", so.getSignature());
    }
}

