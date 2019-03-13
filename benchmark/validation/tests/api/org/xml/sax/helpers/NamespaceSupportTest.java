/**
 * Copyright (C) 2007 The Android Open Source Project
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
package tests.api.org.xml.sax.helpers;


import java.util.ArrayList;
import java.util.EmptyStackException;
import junit.framework.TestCase;
import org.xml.sax.helpers.NamespaceSupport;


public class NamespaceSupportTest extends TestCase {
    static final String defaultUri = "http://www.android.com";

    static final String marketUri = "http://www.android.com/market";

    NamespaceSupport ns;

    ArrayList<String> expected;

    public void testPush_PopContext() {
        int count;
        ns = new NamespaceSupport();
        count = countPrefixes();
        ns.pushContext();
        ns.declarePrefix("dc", "http://www.purl.org/dc#");
        TestCase.assertEquals("Test 1: Incorrect prefix count;", (count + 1), countPrefixes());
        ns.popContext();
        TestCase.assertEquals("Test 2: Incorrect prefix count;", count, countPrefixes());
        // Check that only one context has been created by pushContext().
        try {
            ns.popContext();
            TestCase.fail("Test 3: EmptyStackException expected.");
        } catch (EmptyStackException e) {
            // Expected.
        }
    }

    public void testReset() {
        int count;
        ns = new NamespaceSupport();
        count = countPrefixes();
        ns.pushContext();
        ns.declarePrefix("dc", "http://www.purl.org/dc#");
        TestCase.assertEquals("Test 1: Incorrect prefix count;", (count + 1), countPrefixes());
        ns.reset();
        TestCase.assertEquals("Test 2: Incorrect prefix count;", count, countPrefixes());
        // Check that only one context has been created by reset().
        try {
            ns.popContext();
            TestCase.fail("Test 3: EmptyStackException expected.");
        } catch (EmptyStackException e) {
            // Expected.
        }
    }

    public void testDeclare_GetPrefix() {
        ns.pushContext();
        // Part 1: Check that xml and xmlns are not accepted as prefixes.
        TestCase.assertFalse("Test 1: Invalid prefix accepted.", ns.declarePrefix("xml", NamespaceSupportTest.marketUri));
        TestCase.assertFalse("Test 2: Invalid prefix accepted.", ns.declarePrefix("xmlns", NamespaceSupportTest.marketUri));
        // Part 2: Check that declarePrefix and getPrefix work for valid
        // prefixes.
        TestCase.assertTrue("Test 3: Valid prefix not accepted.", ns.declarePrefix("ak", NamespaceSupportTest.marketUri));
        TestCase.assertTrue("Test 4: Incorrect prefix returned.", ns.getPrefix(NamespaceSupportTest.marketUri).equals("ak"));
        TestCase.assertTrue("Test 5: Valid prefix not accepted.", ns.declarePrefix("bk", NamespaceSupportTest.marketUri));
        TestCase.assertTrue("Test 6: Incorrect prefix returned.", expected.contains(ns.getPrefix(NamespaceSupportTest.marketUri)));
        TestCase.assertTrue("Test 7: Valid prefix not accepted.", ns.declarePrefix("", NamespaceSupportTest.defaultUri));
        // Part 3: Negative Tests for getPrefix.
        TestCase.assertNull(("Test 8: Non-null value returned for the URI that is " + "assigned to the default namespace."), ns.getPrefix(NamespaceSupportTest.defaultUri));
        TestCase.assertNull("Test 9: Non-null value returned for an unassigned URI.", ns.getPrefix(((NamespaceSupportTest.defaultUri) + "/42")));
    }

    public void testGetUri() {
        TestCase.assertEquals("Test 1: Incorrect URI returned;", NamespaceSupportTest.marketUri, ns.getURI("bk"));
        TestCase.assertEquals("Test 2: Incorrect URI returned;", NamespaceSupportTest.defaultUri, ns.getURI(""));
        TestCase.assertNull("Test 3: Null expected for not-existing prefix.", ns.getURI("ck"));
        ns.popContext();
        TestCase.assertNull("Test 4: Null expected for not-existing prefix.", ns.getURI("bk"));
        TestCase.assertEquals("Test 5: Incorrect URI returned;", NamespaceSupport.XMLNS, ns.getURI("xml"));
    }

    public void testNamespaceDeclUris() {
        TestCase.assertFalse("Test 1: Incorrect default value returned by isNamespaceDeclUris().", ns.isNamespaceDeclUris());
        try {
            ns.setNamespaceDeclUris(true);
            TestCase.fail("Test 2: IllegalStateException expected since a context has already been pushed in setUp().");
        } catch (IllegalStateException e) {
            // Expected.
        }
        ns = new NamespaceSupport();
        ns.setNamespaceDeclUris(true);
        TestCase.assertTrue("Test 3: Incorrect value returned by isNamespaceDeclUris().", ns.isNamespaceDeclUris());
        ns.setNamespaceDeclUris(false);
        TestCase.assertFalse("Test 4: Incorrect value returned by isNamespaceDeclUris().", ns.isNamespaceDeclUris());
    }

    public void testProcessName_Element() {
        String[] parts = new String[3];
        TestCase.assertNotNull("Test 1: Non-null value expected.", ns.processName("ak:hello", parts, false));
        TestCase.assertEquals("Test 2: Incorrect namespace URI;", NamespaceSupportTest.marketUri, parts[0]);
        TestCase.assertEquals("Test 3: Incorrect local name;", "hello", parts[1]);
        TestCase.assertEquals("Test 4: Incorrect raw name;", "ak:hello", parts[2]);
        TestCase.assertNotNull("Test 5: Non-null value expected.", ns.processName("bk:", parts, false));
        TestCase.assertEquals("Test 6: Incorrect namespace URI;", NamespaceSupportTest.marketUri, parts[0]);
        TestCase.assertEquals("Test 7: Incorrect local name;", "", parts[1]);
        TestCase.assertEquals("Test 8: Incorrect raw name;", "bk:", parts[2]);
        TestCase.assertNotNull("Test 9: Non-null value expected.", ns.processName("world", parts, false));
        TestCase.assertEquals("Test 10: Incorrect namespace URI;", NamespaceSupportTest.defaultUri, parts[0]);
        TestCase.assertEquals("Test 11: Incorrect local name;", "world", parts[1]);
        TestCase.assertEquals("Test 12: Incorrect raw name;", "world", parts[2]);
        TestCase.assertNull("Test 13: Null expected for undeclared prefix.", ns.processName("ck:lorem", parts, false));
        TestCase.assertNull("Test 14: Null expected for xmlns prefix.", ns.processName("xmlns:ipsum", parts, false));
        ns = new NamespaceSupport();
        ns.pushContext();
        TestCase.assertNotNull("Test 15: Non-null value expected.", ns.processName("world", parts, false));
        TestCase.assertEquals("Test 16: Incorrect namespace URI;", "", parts[0]);
        TestCase.assertEquals("Test 17: Incorrect local name;", "world", parts[1]);
        TestCase.assertEquals("Test 18: Incorrect raw name;", "world", parts[2]);
    }

    public void testProcessName_Attribute() {
        String[] parts = new String[3];
        TestCase.assertNotNull("Test 1: Non-null value expected.", ns.processName("ak:hello", parts, true));
        TestCase.assertEquals("Test 2: Incorrect namespace URI;", NamespaceSupportTest.marketUri, parts[0]);
        TestCase.assertEquals("Test 3: Incorrect local name;", "hello", parts[1]);
        TestCase.assertEquals("Test 4: Incorrect raw name;", "ak:hello", parts[2]);
        TestCase.assertNotNull("Test 5: Non-null value expected.", ns.processName("bk:", parts, true));
        TestCase.assertEquals("Test 6: Incorrect namespace URI;", NamespaceSupportTest.marketUri, parts[0]);
        TestCase.assertEquals("Test 7: Incorrect local name;", "", parts[1]);
        TestCase.assertEquals("Test 8: Incorrect raw name;", "bk:", parts[2]);
        TestCase.assertNotNull("Test 9: Non-null value expected.", ns.processName("world", parts, true));
        TestCase.assertEquals("Test 10: Incorrect namespace URI;", "", parts[0]);
        TestCase.assertEquals("Test 11: Incorrect local name;", "world", parts[1]);
        TestCase.assertEquals("Test 12: Incorrect raw name;", "world", parts[2]);
        TestCase.assertNull("Test 13: Null expected for undeclared prefix.", ns.processName("ck:lorem", parts, true));
        TestCase.assertNull("Test 14: Null expected for xmlns prefix.", ns.processName("xmlns:ipsum", parts, true));
        ns = new NamespaceSupport();
        ns.setNamespaceDeclUris(true);
        ns.pushContext();
        TestCase.assertNotNull("Test 15: Non-null value expected.", ns.processName("xmlns", parts, true));
        TestCase.assertEquals("Test 16: Incorrect namespace URI;", NamespaceSupport.NSDECL, parts[0]);
        TestCase.assertEquals("Test 17: Incorrect local name;", "xmlns", parts[1]);
        TestCase.assertEquals("Test 18: Incorrect raw name;", "xmlns", parts[2]);
    }
}

