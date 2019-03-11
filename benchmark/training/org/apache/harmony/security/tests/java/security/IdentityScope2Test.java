/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.security.tests.java.security;


import java.security.Identity;
import java.security.IdentityScope;
import java.security.KeyManagementException;
import java.security.KeyPairGenerator;
import java.security.Principal;
import java.security.PublicKey;
import java.util.Enumeration;
import java.util.Hashtable;
import junit.framework.TestCase;


@SuppressWarnings("deprecation")
public class IdentityScope2Test extends TestCase {
    private static PublicKey PUB_KEY;

    public static class IdentityScopeSubclass extends IdentityScope {
        private static final long serialVersionUID = 1L;

        Hashtable<Identity, Identity> identities;

        public IdentityScopeSubclass(String name, PublicKey pk) {
            super(name);
            try {
                setPublicKey(pk);
            } catch (KeyManagementException e) {
            }
            identities = new Hashtable<Identity, Identity>();
        }

        public IdentityScopeSubclass() {
            super();
            identities = new Hashtable<Identity, Identity>();
        }

        public IdentityScopeSubclass(String name) {
            super(name);
            identities = new Hashtable<Identity, Identity>();
        }

        public IdentityScopeSubclass(String name, IdentityScope scope) throws KeyManagementException {
            super(name, scope);
            identities = new Hashtable<Identity, Identity>();
        }

        public int size() {
            return identities.size();
        }

        public Identity getIdentity(String name) {
            Enumeration<Identity> en = identities();
            while (en.hasMoreElements()) {
                Identity current = ((Identity) (en.nextElement()));
                if (current.getName().equals(name))
                    return current;

            } 
            return null;
        }

        public Identity getIdentity(PublicKey pk) {
            Enumeration<Identity> en = identities();
            while (en.hasMoreElements()) {
                Identity current = ((Identity) (en.nextElement()));
                if ((current.getPublicKey()) == pk)
                    return current;

            } 
            return null;
        }

        public Enumeration<Identity> identities() {
            return identities.elements();
        }

        public void addIdentity(Identity id) throws KeyManagementException {
            if (identities.containsKey(id))
                throw new KeyManagementException("This Identity is already contained in the scope");

            if ((getIdentity(id.getPublicKey())) != null)
                throw new KeyManagementException("This Identity's public key already exists in the scope");

            identities.put(id, id);
        }

        public void removeIdentity(Identity id) throws KeyManagementException {
            if (!(identities.containsKey(id)))
                throw new KeyManagementException("This Identity is not contained in the scope");

            identities.remove(id);
        }
    }

    /**
     * java.security.IdentityScope#IdentityScope()
     */
    public void test_Constructor() {
        new IdentityScope2Test.IdentityScopeSubclass();
    }

    /**
     * java.security.IdentityScope#IdentityScope(java.lang.String)
     */
    public void test_ConstructorLjava_lang_String() {
        String[] str = new String[]{ "test", "", null };
        IdentityScope2Test.IdentityScopeSubclass iss;
        for (int i = 0; i < (str.length); i++) {
            try {
                iss = new IdentityScope2Test.IdentityScopeSubclass(str[i]);
                TestCase.assertNotNull(iss);
                TestCase.assertTrue((iss instanceof IdentityScope));
            } catch (Exception e) {
                TestCase.fail(("Unexpected exception for parameter " + (str[i])));
            }
        }
    }

    /**
     * java.security.IdentityScope#IdentityScope(java.lang.String,
     *        java.security.IdentityScope)
     */
    public void test_ConstructorLjava_lang_StringLjava_security_IdentityScope() {
        String nameNull = null;
        String[] str = new String[]{ "test", "", "!@#$%^&*()", "identity name" };
        IdentityScope is;
        IdentityScope iss = new IdentityScope2Test.IdentityScopeSubclass("test scope");
        for (int i = 0; i < (str.length); i++) {
            try {
                is = new IdentityScope2Test.IdentityScopeSubclass(str[i], new IdentityScope2Test.IdentityScopeSubclass());
                TestCase.assertNotNull(is);
                TestCase.assertTrue((is instanceof IdentityScope));
            } catch (Exception e) {
                TestCase.fail(("Unexpected exception for parameter " + (str[i])));
            }
        }
        try {
            is = new IdentityScope2Test.IdentityScopeSubclass(nameNull, new IdentityScope2Test.IdentityScopeSubclass());
        } catch (NullPointerException npe) {
        } catch (Exception e) {
            TestCase.fail((("Incorrect exception " + e) + " was thrown"));
        }
        try {
            is = new IdentityScope2Test.IdentityScopeSubclass("test", iss);
            is = new IdentityScope2Test.IdentityScopeSubclass("test", iss);
            TestCase.fail("KeyManagementException was not thrown");
        } catch (KeyManagementException npe) {
            // expected
        } catch (Exception e) {
            TestCase.fail((("Incorrect exception " + e) + " was thrown instead of KeyManagementException"));
        }
    }

    /**
     * java.security.IdentityScope#addIdentity(java.security.Identity)
     */
    public void test_addIdentityLjava_security_Identity() throws Exception {
        IdentityScope2Test.IdentityScopeSubclass sub = new IdentityScope2Test.IdentityScopeSubclass("test", new IdentityScope2Test.IdentityScopeSubclass());
        Identity id = new Identity2Test.IdentitySubclass("id1");
        id.setPublicKey(IdentityScope2Test.getPubKey());
        sub.addIdentity(id);
        try {
            Identity id2 = new Identity2Test.IdentitySubclass("id2");
            id2.setPublicKey(IdentityScope2Test.getPubKey());
            sub.addIdentity(id2);
            TestCase.fail("KeyManagementException should have been thrown");
        } catch (KeyManagementException e) {
            // Expected
        }
    }

    /**
     * java.security.IdentityScope#removeIdentity(java.security.Identity)
     */
    public void test_removeIdentityLjava_security_Identity() throws Exception {
        IdentityScope2Test.IdentityScopeSubclass sub = new IdentityScope2Test.IdentityScopeSubclass("test", new IdentityScope2Test.IdentityScopeSubclass());
        Identity id = new Identity2Test.IdentitySubclass();
        id.setPublicKey(IdentityScope2Test.getPubKey());
        sub.addIdentity(id);
        sub.removeIdentity(id);
        try {
            sub.removeIdentity(id);
            TestCase.fail("KeyManagementException should have been thrown");
        } catch (KeyManagementException expected) {
        }
    }

    /**
     * java.security.IdentityScope#identities()
     */
    public void test_identities() throws Exception {
        IdentityScope2Test.IdentityScopeSubclass sub = new IdentityScope2Test.IdentityScopeSubclass("test", new IdentityScope2Test.IdentityScopeSubclass());
        Identity id = new Identity2Test.IdentitySubclass();
        id.setPublicKey(IdentityScope2Test.getPubKey());
        sub.addIdentity(id);
        Enumeration<Identity> en = sub.identities();
        TestCase.assertEquals("Wrong object contained in identities", en.nextElement(), id);
        TestCase.assertFalse("Contains too many elements", en.hasMoreElements());
    }

    /**
     * java.security.IdentityScope#getIdentity(java.security.Principal)
     */
    public void test_getIdentityLjava_security_Principal() throws Exception {
        Identity id = new Identity2Test.IdentitySubclass("principal name");
        id.setPublicKey(IdentityScope2Test.getPubKey());
        IdentityScope2Test.IdentityScopeSubclass sub = new IdentityScope2Test.IdentityScopeSubclass("test", new IdentityScope2Test.IdentityScopeSubclass());
        try {
            sub.getIdentity(((Principal) (null)));
            TestCase.fail("Test 1: NullPointerException expected.");
        } catch (NullPointerException expected) {
        }
        sub.addIdentity(id);
        Identity returnedId = sub.getIdentity(id);
        TestCase.assertEquals("Test 2: Returned Identity not the same as the added one;", id, returnedId);
        Identity id2 = new Identity2Test.IdentitySubclass("Another identity");
        id2.setPublicKey(IdentityScope2Test.getPubKey());
        TestCase.assertNull("Test 3: Null value expected.", sub.getIdentity(id2));
        try {
            sub.getIdentity(((Principal) (null)));
            TestCase.fail("Test 4: NullPointerException expected.");
        } catch (NullPointerException expected) {
        }
    }

    /**
     * java.security.IdentityScope#getIdentity(java.security.PublicKey)
     */
    public void test_getIdentityLjava_security_PublicKey() throws Exception {
        IdentityScope2Test.IdentityScopeSubclass sub = new IdentityScope2Test.IdentityScopeSubclass("test", new IdentityScope2Test.IdentityScopeSubclass());
        Identity id = new Identity2Test.IdentitySubclass();
        id.setPublicKey(IdentityScope2Test.getPubKey());
        sub.addIdentity(id);
        Identity returnedId = sub.getIdentity(IdentityScope2Test.getPubKey());
        TestCase.assertEquals("Test 1: Returned Identity not the same as the added one;", id, returnedId);
        TestCase.assertNull("Test 2: Null value expected.", sub.getIdentity(((PublicKey) (null))));
        PublicKey anotherKey = KeyPairGenerator.getInstance("DSA").genKeyPair().getPublic();
        TestCase.assertNull("Test 3: Null value expected.", sub.getIdentity(anotherKey));
    }

    /**
     * java.security.IdentityScope#getIdentity(java.lang.String)
     */
    public void test_getIdentityLjava_lang_String() throws Exception {
        IdentityScope2Test.IdentityScopeSubclass sub = new IdentityScope2Test.IdentityScopeSubclass("test", new IdentityScope2Test.IdentityScopeSubclass());
        Identity id = new Identity2Test.IdentitySubclass("test");
        id.setPublicKey(IdentityScope2Test.getPubKey());
        sub.addIdentity(id);
        Identity returnedId = sub.getIdentity("test");
        TestCase.assertEquals("Returned Identity not the same as the added one", id, returnedId);
    }

    /**
     * java.security.IdentityScope#size()
     */
    public void test_size() throws Exception {
        IdentityScope2Test.IdentityScopeSubclass sub = new IdentityScope2Test.IdentityScopeSubclass("test", new IdentityScope2Test.IdentityScopeSubclass());
        Identity id = new Identity2Test.IdentitySubclass();
        id.setPublicKey(IdentityScope2Test.getPubKey());
        sub.addIdentity(id);
        TestCase.assertEquals("Wrong size", 1, sub.size());
    }

    /**
     * java.security.IdentityScope#toString()
     */
    public void test_toString() throws Exception {
        IdentityScope2Test.IdentityScopeSubclass sub = new IdentityScope2Test.IdentityScopeSubclass("test", new IdentityScope2Test.IdentityScopeSubclass());
        Identity id = new Identity2Test.IdentitySubclass();
        id.setPublicKey(IdentityScope2Test.getPubKey());
        sub.addIdentity(id);
        TestCase.assertNotNull("toString returned a null", sub.toString());
        TestCase.assertTrue("Not a valid String ", ((sub.toString().length()) > 0));
    }

    public void test_getIdentity() throws Exception {
        // Regression for HARMONY-1173
        IdentityScope scope = IdentityScope.getSystemScope();
        try {
            scope.getIdentity(((String) (null)));
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }
}

